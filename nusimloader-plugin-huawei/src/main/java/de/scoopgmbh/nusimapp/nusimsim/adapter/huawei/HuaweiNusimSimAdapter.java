/*
Copyright 2020 HiSilicon (Shanghai) Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package de.scoopgmbh.nusimapp.nusimsim.adapter.huawei;

import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NoEidAvailableException;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NusimSimAdapter;
import de.scoopgmbh.nusimapp.nusimsim.adapter.ProfileLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Note: on Linux, to access the serial port as an unauthorized user you might
 * need to add the os user to the appropriate group, depending on os e.g.:
 * {@code sudo usermod -a -G uucp <username>}
 */
public class HuaweiNusimSimAdapter extends NusimSimAdapter {

    private static final Logger log = LoggerFactory.getLogger(HuaweiNusimSimAdapter.class);

    private final HuaweiNusimConfig config;
    private final Supplier<ISerialPortHandler> serialPortHandlerSupplier;

    /**
     * This constructor is called by the framework's plugin concept.
     * <p>
     * The constructor creates an instance of {@link ISerialPortHandler} for a
     * quick connection test with the configuration given in {@code adapterConfig}.
     * This is done so that config errors are detected early, during startup.
     *
     * @throws SerialPortInvalidPortException if the {@code portDescriptor}
     *                                        does not point to a proper device,
     *                                        ie. if the port cannot be opened.
     */
    @SuppressWarnings("unused")
    public HuaweiNusimSimAdapter(Config adapterConfig) {
        super(adapterConfig);
        this.config = getHuaweiNusimConfig();
        this.serialPortHandlerSupplier = () -> new SerialPortHandlerImpl(config.getSerialPort(), config.getBaudRate());
        // Connection test: create the serial port handler and close it immediately afterwards
        ISerialPortHandler serialPortHandler = this.serialPortHandlerSupplier.get();
        serialPortHandler.close();
    }

    /**
     * used for tests with mocked {@link ISerialPortHandler} supplier.
     */
    HuaweiNusimSimAdapter(Config adapterConfig, Supplier<ISerialPortHandler> serialPortHandlerSupplier) {
        super(adapterConfig);
        this.config = getHuaweiNusimConfig();
        this.serialPortHandlerSupplier = serialPortHandlerSupplier;
    }

    private HuaweiNusimConfig getHuaweiNusimConfig() {
        return ConfigBeanFactory.create(getConfig(), HuaweiNusimConfig.class);
    }


    @Override
    public String getEID() throws NoEidAvailableException {
        try {
            return execCommand("AT+NSESIM=GETEID", output -> {
                Matcher matcher = Pattern.compile("(?im)^\\+GETEID:([0-9A-F]+)$").matcher(output);
                if (matcher.find())
                    return matcher.group(1);
                else
                    throw new NoEidAvailableException("no EID in output: \"" + output + "\"");
            });
        } catch (RuntimeException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof NoEidAvailableException)
                throw (NoEidAvailableException) ex.getCause();
            else
                throw new NoEidAvailableException("could not retrieve EID from device", ex.getCause() != null ? ex.getCause() : ex);
        }
    }

    @Override
    public String getCertNusim() {
        return execCommand("AT+NSESIM=GETCERTIFICATE", output -> {
            Matcher matcher = Pattern.compile("(?im)^\\+GETCERTIFICATE:(.+)$").matcher(output);
            if (matcher.find()) {
                String certInHex = matcher.group(1).toUpperCase(Locale.US);
                byte[] certData = BaseEncoding.base16().upperCase().decode(certInHex);
                String certInDERFormat = BaseEncoding.base64().withPadChar('=').withSeparator("\n", 67).encode(certData);
                return "-----BEGIN CERTIFICATE-----\n" + certInDERFormat + "\n-----END CERTIFICATE-----\n";
            }
            return null;
        });
    }

    @Override
    public String getNusimCapabilities() {
        return execCommand("AT+NSESIM=GETCAPABILITIES", output -> {
            // the GETCAPABILITIES is answered with reply "+GETCAPABILITIES:<version>,<features>"
            // where <version> is a two hex digits version number
            // and <features> is a two hex digits bitmask with leading zeroes.
            // Example:
            //   AT+NSESIM=GETCAPABILITIES
            //   +GETCAPABILITIES:10,05
            //   OK
            // The method getNusimCapabilities() must return <version> as String.
            Matcher matcher = Pattern.compile("(?im)^\\+GETCAPABILITIES:[^,]+,(.+)$").matcher(output);
            if (matcher.find())
                return matcher.group(1);
            else
                return "00"; // no capabilities
        });
    }

    @Override
    public String loadProfile(String eid, String mac, String encP, String eKPubDP, @Nullable String sigEKPubDP, @Nullable String kPubDP, @Nullable String sigKPubDP, @Nullable String kPubCI) throws ProfileLoadingException {
        String eKPubDPHex = base64ToHex(requireNonNull(eKPubDP, "eKPubDP may not be null"));
        String macHex = requireNonNull(mac, "mac may not be null"); // note: mac is already in hex format
        String sigEKPubDPHex = pad(base64ToHex(sigEKPubDP), 64);
        String kPubDPHex = pad(base64ToHex(kPubDP), 65);
        String sigKPubDPHex = pad(base64ToHex(sigKPubDP), 64);
        String kPubCIHex = pad(base64ToHex(kPubCI), 65);
        String encPHex = base64ToHex(encP);

        int length = numBytes(eKPubDPHex) // should be 65 chars
                + numBytes(macHex) // should be 16 chars
                + numBytes(sigEKPubDPHex) // should be 64 chars
                + numBytes(kPubDPHex) // should be 65 chars
                + numBytes(sigKPubDPHex) // should be 64 chars
                + numBytes(kPubCIHex) // should be 65 chars
                + numBytes(encPHex); // arbitrary length

        // build the complete command
        String command = String.format("AT+NSESIM=SETPROFILE,%d,%s%s%s%s%s%s%s", length, eKPubDPHex, macHex, sigEKPubDPHex, kPubDPHex, sigKPubDPHex, kPubCIHex, encPHex);
        // it's a success if we get "+SETPROFILE:<iccid>" on a new line where <iccid> is a hex string with at least 8 chars
        Pattern successPattern = Pattern.compile("(?m)^\\s*\\+SETPROFILE:\\s*([0-9A-Fa-f]{8,})\\s*$");
        // it's an error if we either get "ERROR", or "+SETPROFILE:<num>" where <num> is an error number
        Pattern errorPattern = Pattern.compile("(?m)^\\s*(?:\\+SETPROFILE:\\s*([0-9]{1,7})|ERROR)\\s*$");

        try {
            return execCommand(command, successPattern, errorPattern, output -> {
                Matcher matcher = successPattern.matcher(output);
                if (matcher.find())
                    return matcher.group(1);
                throw new ProfileLoadingException("could not set new profile: SETPROFILE command did not return new ICCID");
            });
        } catch (SerialPortErroneousOutputException ex) {
            Matcher matcher = errorPattern.matcher(ex.getOutput());
            if (matcher.find() && matcher.groupCount() > 0) {
                int errorNumber = Integer.parseInt(matcher.group(1));
                String errorText = mapErrorNumber(errorNumber);
                throw new ProfileLoadingException(String.format("error %d: %s", errorNumber, errorText), ex);
            }
            throw new ProfileLoadingException(ex);
        } catch (Exception ex) {
            throw new ProfileLoadingException("unexpected error loading profile to device", ex);
        }
    }

    @Nonnull
    private static String mapErrorNumber(int errorNumber) {
        switch (errorNumber) {
            case 4:
                return "general error";
            case 50:
                return "syntax error";
            case 541:
                return "invalid KpubCI (does not match stored KpubCI)";
            case 542:
                return "invalid KpubDP (verification of SigKpubDP failed)";
            case 543:
                return "Invalid eKpubDP (verification of SigEKpubDP failed)";
            case 544:
                return "MAC failure (calculated MAC does not match given mac)";
            case 545:
                return "profile already loaded, Eagle locked (reprovisioning not supported)";
            case 546:
                return "profile error (problem parsing decrypted profile)";
            default:
                return "(unknown error code)";
        }
    }

    @Nonnull
    private static String base64ToHex(@Nullable String s) {
        return s == null ? "" : BaseEncoding.base16().upperCase().encode(BaseEncoding.base64().decode(s));
    }

    @Nonnull
    private static String pad(@Nonnull String s, int length) {
        return Strings.padEnd(s, (length - numBytes(s)) * 2, '0');
    }

    private static int numBytes(@Nonnull String hex) {
        return hex.length() / 2;
    }


    @FunctionalInterface
    private interface OutputParser {
        String parse(String output) throws Exception;
    }


    /**
     * Execute an AT command with configurable number of retries and
     * retry delays, and parse the output from the device with the given
     * {@link OutputParser}.
     *
     * @param command      the AT command to execute
     * @param outputParser the output parser receives
     * @return the output received from device after sending the command
     * until (but not including) next "OK" or "ERROR".
     */
    private String execCommand(String command, OutputParser outputParser) {
        return execCommand(command, null, null, outputParser);
    }

    private String execCommand(String command, Pattern successPattern, Pattern errorPattern, OutputParser outputParser) {
        int commandRetries = config.getCommandRetries();
        int commandTimeoutMs = config.getCommandTimeoutMs();
        int commandRetryDelayMs = config.getCommandRetryDelayMs();
        ISerialPortHandler serialPortHandler = null;

        try {
            for (int i = 0; i < commandRetries; i++) {
                if (serialPortHandler == null) {
                    serialPortHandler = this.serialPortHandlerSupplier.get(); // this might throw an exception if config is wrong or port is currently unreachable
                }
                if (i > 0) {
                    log.info("Retry #{} of {} for command \"{}\" ...", i + 1, commandRetries, command);
                }
                try {
                    CompletableFuture<String> future = successPattern == null && errorPattern == null
                            ? serialPortHandler.sendCommand(command)
                            : serialPortHandler.sendCommand(command, successPattern, errorPattern);
                    // wait for output until successPattern or errorPattern matches or timeout expires
                    String deviceOutput = future.get(commandTimeoutMs, MILLISECONDS);
                    return outputParser.parse(deviceOutput);
                } catch (InterruptedException ex) {
                    logErrorOrThrow(new RuntimeException(ex), "interrupted", command, i, commandRetries, commandRetryDelayMs);
                } catch (TimeoutException ex) {
                    logErrorOrThrow(new RuntimeException(ex), "timeout", command, i, commandRetries, commandRetryDelayMs);
                } catch (ExecutionException ex) {
                    RuntimeException cause;
                    String output;
                    if (ex.getCause() instanceof SerialPortErroneousOutputException) {
                        cause = (SerialPortErroneousOutputException) ex.getCause();
                        output = "got output: \"" + ((SerialPortErroneousOutputException) ex.getCause()).getOutput() + "\"";
                    } else if (ex.getCause() instanceof RuntimeException) {
                        cause = (RuntimeException) ex.getCause();
                        output = ex.getCause().toString();
                    } else {
                        cause = new RuntimeException(ex.getCause());
                        output = ex.getCause().toString();
                    }
                    logErrorOrThrow(cause, output, command, i, commandRetries, commandRetryDelayMs);
                } catch (RuntimeException ex) {
                    logErrorOrThrow(ex, ex.toString(), command, i, commandRetries, commandRetryDelayMs);
                } catch (Exception ex) {
                    logErrorOrThrow(new RuntimeException(ex), ex.toString(), command, i, commandRetries, commandRetryDelayMs);
                }
            }
        } finally {
            if (serialPortHandler != null) {
                serialPortHandler.close();
            }
        }

        // should never come here
        throw new IllegalStateException("no command output received");
    }

    private static void logErrorOrThrow(RuntimeException ex, String errorOutput, String command, int commandRetry, int commandRetries, int commandRetryDelayMs) {
        if (commandRetry < commandRetries - 1) {
            log.warn("Error executing command \"{}\": {}. That was retry #{} of {}. Retry in {}ms ...", command, errorOutput, commandRetry + 1, commandRetries, commandRetryDelayMs);
            sleep(commandRetryDelayMs);
        } else {
            log.error("Error executing command \"{}\": {}. That was the last retry #{} of {}.", command, errorOutput, commandRetry + 1, commandRetries);
            throw ex;
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
            // intentionally ignored
        }
    }

}
