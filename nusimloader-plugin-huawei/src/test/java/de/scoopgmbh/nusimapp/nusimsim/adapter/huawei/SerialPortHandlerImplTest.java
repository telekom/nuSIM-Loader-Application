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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Usage: {@code SerialPortHandlerImplTest [command] [device] [baudRate]}
 * <p>
 * where:
 * <ul>
 * <li>{@code command}: <i>(optional)</i> the AT command to send. Default: "AT+CGMR"
 * <li>{@code device}: <i>(optional)</i> e.g. "/dev/tty1" or "COM3". Default: "/dev/ttyXRUSB0"</li>
 * <li>{@code baudRate}: <i>(optional)</i> the baud rate. Default: 9600</li>
 * </ul>
 * Note: to access the serial port as an unauthorized user you might need to add
 * the os user to the appropriate group, depending on os e.g.:
 * {@code sudo usermod -a -G uucp <username>}
 */
public class SerialPortHandlerImplTest {

    private static final Logger log = LoggerFactory.getLogger(SerialPortHandlerImplTest.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        String command = args.length > 0 ? args[0] : "AT+CGMR";
        String portDescriptor = args.length > 1 ? args[1] : "/dev/ttyXRUSB0";
        int baudRate = Integer.parseInt(args.length > 2 ? args[2] : "9600");

        try (SerialPortHandlerImpl serialPortHandler = new SerialPortHandlerImpl(portDescriptor, baudRate)) {
            /*
            serialPortHandler.sendCommand(command)
                    .thenAccept(output -> log.info("OUTPUT: {}", output))
                    .exceptionally(ex -> {
                        log.error("error", ex);
                        return null;
                    })
                    .get(10, SECONDS);
            */
            String output = serialPortHandler.sendCommand(command).get(10, SECONDS);
            log.info("OUTPUT: {}", output);
        }
    }
}
