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

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public interface ISerialPortHandler extends Closeable {

    /**
     * Send an AT command to the device, return output until next OK or ERROR.
     *
     * @param command an AT command, e.g. "AT+CGMR"
     * @return a <i>future</i> which, upon completion, returns the output of the
     * device as string up until, but not including, the "OK" terminator. If
     * the result of the command ends with "ERROR" instead, then the future ends
     * exceptionally with a {@link SerialPortErroneousOutputException} wrapped
     * in an {@code ExecutionException}. In this case he
     * {@link SerialPortErroneousOutputException} contains the output up until,
     * but not including, the "ERROR" terminator.
     */
    CompletableFuture<String> sendCommand(String command);

    /**
     * Send an AT command to the device, return output until either success or
     * error pattern matched.
     *
     * @param command an AT command, e.g. "AT+CGMR"
     * @return a <i>future</i> which, upon completion, returns the output of the
     * device as string up until either the {@code successPattern} or the
     * {@code errorPattern} matches. If the result of the command ends with
     * the pattern of {@code errorPattern} matching, then the future ends
     * exceptionally with a {@link SerialPortErroneousOutputException} wrapped
     * in an {@code ExecutionException}. In this case he
     * {@link SerialPortErroneousOutputException} contains the output up until,
     * but not including, the matching pattern.
     */
    CompletableFuture<String> sendCommand(String command, Pattern successPattern, Pattern errorPattern);

    /**
     * Close connection to the serial port.
     * <p>
     * Overridden from {@link Closeable} to not throw {@code IOException},
     * but simplicity reasons.
     */
    @Override
    void close();

}
