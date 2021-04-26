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

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

/**
 * Helper class to send AT commands to a serial port device and to return
 * answer output up to the next "OK" or "ERROR".
 * <p>
 * Note: on Linux, to access the serial port as an unauthorized user you might
 * need to add the os user to the appropriate group, depending on os e.g.:
 * {@code sudo usermod -a -G uucp <username>}
 */
class SerialPortHandlerImpl implements ISerialPortHandler {

    private static final Logger log = LoggerFactory.getLogger(SerialPortHandlerImpl.class);
    private static final Charset TERMINAL_CHARSET = StandardCharsets.US_ASCII;
    private static final boolean debugWireData = !"false".equals(System.getenv("huawei.debugWireData"));
    private static final Pattern defaultSuccessPattern = Pattern.compile("^\\s*OK\\s*$");
    private static final Pattern defaultErrorPattern = Pattern.compile("^\\s*ERROR\\s*$");


    private final String portDescriptor;
    private final SerialPort commPort;
    private volatile CompletableFuture<String> currentRunningCommand = null;
    private volatile Pattern currentSuccessPattern = defaultSuccessPattern;
    private volatile Pattern currentErrorPattern = defaultErrorPattern;

    /**
     * Create a new serial port handler for a certain device.
     *
     * @param portDescriptor device, ie. "/dev/ttyXRUSB0" or "COM3"
     * @param baudRate       baud rate, e.g {@code 9600}.
     * @throws SerialPortInvalidPortException if the {@code portDescriptor}
     *                                        does not point to a proper device,
     *                                        ie. if the port cannot be opened.
     */
    SerialPortHandlerImpl(String portDescriptor, int baudRate) {
        this.portDescriptor = portDescriptor;
        log.info("Opening port {} ...", portDescriptor);
        SerialPort commPort = SerialPort.getCommPort(portDescriptor);
        commPort.setBaudRate(baudRate);
        commPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 20000, 0);
        boolean opened = commPort.openPort();
        if (!opened) {
            throw new SerialPortInvalidPortException("Port " + portDescriptor + " could not be opened");
        }
        this.commPort = commPort;
        log.info("Listening for data on {} ...", portDescriptor);
        commPort.addDataListener(new BufferedSerialPortMessageListener());
    }

    @Override
    public synchronized CompletableFuture<String> sendCommand(String command) {
        return sendCommand(command, defaultSuccessPattern, defaultErrorPattern);
    }

    @Override
    public synchronized CompletableFuture<String> sendCommand(String command, Pattern successPattern, Pattern errorPattern) {
        if (commPort == null) {
            throw new SerialPortInvalidPortException("Port " + portDescriptor + " is not open.");
        }
        if (currentRunningCommand != null) {
            cancelCurrentRunningCommand();
        }
        this.currentSuccessPattern = ofNullable(successPattern).orElse(defaultSuccessPattern);
        this.currentErrorPattern = ofNullable(errorPattern).orElse(defaultErrorPattern);
        try {
            if (debugWireData && log.isDebugEnabled()) {
                log.debug("{} >>> {}", portDescriptor, command.trim());
            }
            if (!command.endsWith("\n")) {
                command += "\n";
            }
            byte[] input = command.getBytes();
            int numBytesWritten = commPort.writeBytes(input, input.length);
            if (numBytesWritten != input.length) {
                log.error("{} >>> {} : ERROR: expected to write {} bytes, but wrote {} bytes", portDescriptor, command.trim(), input.length, numBytesWritten);
            } else if (debugWireData && log.isDebugEnabled()) {
                log.debug("{} >>> {} : <sent>", portDescriptor, command.trim());
            }
            return currentRunningCommand = new CompletableFuture<>();
        } catch (RuntimeException ex) {
            this.currentSuccessPattern = defaultSuccessPattern;
            this.currentErrorPattern = defaultErrorPattern;
            this.currentRunningCommand = null;
            throw ex;
        }
    }

    /**
     * Close connection to the serial port.
     */
    @Override
    public synchronized void close() {
        if (currentRunningCommand != null) {
            cancelCurrentRunningCommand();
        }
        if (commPort != null) {
            commPort.closePort();
        }
    }


    private void cancelCurrentRunningCommand() {
        if (currentRunningCommand != null && !currentRunningCommand.isDone()) {
            currentRunningCommand.cancel(false);
        }
        currentRunningCommand = null;
    }


    private class BufferedSerialPortMessageListener implements SerialPortMessageListener {
        private final StringBuilder messageBuffer = new StringBuilder();

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return new byte[]{(byte) '\n'};
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            try {
                handleSerialEvent(event);
            } catch (Exception ex) {
                log.error("error handling serial event", ex);
            }
        }

        private void handleSerialEvent(SerialPortEvent event) {
            byte[] newData = event.getReceivedData();
            int len = newData.length;
            if (len > 0) {
                String line = new String(newData, 0, len, TERMINAL_CHARSET);
                if (debugWireData && log.isDebugEnabled()) {
                    log.debug("{} <<< {}", portDescriptor, line.trim());
                }
                messageBuffer.append(line);
                boolean isOK = currentSuccessPattern.matcher(line).matches();
                boolean isError = !isOK && currentErrorPattern.matcher(line).matches();
                if (isOK || isError) {
                    currentSuccessPattern = defaultSuccessPattern;
                    currentErrorPattern = defaultErrorPattern;
                    String output = messageBuffer.toString();
                    messageBuffer.setLength(0);
                    if (isOK) {
                        currentRunningCommand.complete(output);
                    } else {
                        currentRunningCommand.completeExceptionally(new SerialPortErroneousOutputException(output));
                    }
                }
            }
        }
    }
}
