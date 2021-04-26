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

public class HuaweiNusimConfig {

    /**
     * serial port device name, e.g. "/dev/ttyXRUSB0" or "COM3".
     * <p>
     * Default: "/dev/ttyXRUSB0"
     */
    private String serialPort = "/dev/ttyXRUSB0";

    /**
     * serial port baud rate, e.g. {@code 9600}.
     * <p>
     * Default: 9600
     */
    private int baudRate = 9600;

    /**
     * how many times to retry AT commands on errors.
     * <p>
     * Default: 1
     */
    private int commandRetries = 1;

    /**
     * the timeout for AT commands, in milliseconds.
     * <p>
     * Default: 10000
     */
    private int commandTimeoutMs = 10000;

    /**
     * the time to wait before a retry of an AT command, in milliseconds.
     * <p>
     * Default: 1000
     */
    private int commandRetryDelayMs = 1000;


    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getCommandRetries() {
        return commandRetries;
    }

    public void setCommandRetries(int commandRetries) {
        this.commandRetries = commandRetries;
    }

    public int getCommandTimeoutMs() {
        return commandTimeoutMs;
    }

    public void setCommandTimeoutMs(int commandTimeoutMs) {
        this.commandTimeoutMs = commandTimeoutMs;
    }

    public int getCommandRetryDelayMs() {
        return commandRetryDelayMs;
    }

    public void setCommandRetryDelayMs(int commandRetryDelayMs) {
        this.commandRetryDelayMs = commandRetryDelayMs;
    }
}
