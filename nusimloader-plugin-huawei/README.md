[comment]: <> (Copyright 2020 HiSilicon &#40;Shanghai&#41; Limited)

[comment]: <> (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[comment]: <> (you may not use this file except in compliance with the License.)
[comment]: <> (You may obtain a copy of the License at)

[comment]: <> (    http://www.apache.org/licenses/LICENSE-2.0)

[comment]: <> (Unless required by applicable law or agreed to in writing, software)
[comment]: <> (distributed under the License is distributed on an "AS IS" BASIS,)
[comment]: <> (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[comment]: <> (See the License for the specific language governing permissions and)
[comment]: <> (limitations under the License.)


# Huawei NuSim Loader App Plugin

This directory contains a plugin for the NuSim Loader App which talks
to the Huawei NB IoT-Device based on a Quectel chip, using AT commands
over UART.


## Linux driver setup

The Quectel NB-IoT device has a special UART component named XR21V1414
by Exar Corp. which allows for serial communication via USB.
Unfortunately the default Linux driver cdc-acm which supports UART/tty
communication doesn't work with the XR21V1414 correctly. You need to
install the Exar driver kernel module.

### Installation on CentOs or other Linux variants

 1. Download source code for driver from
    https://www.maxlinear.com/content/document.ashx?id=21651
   
    (driver download overview: https://www.maxlinear.com/support/design-tools/software-drivers)
 2. Unzip to any directory
 3. Install package linux-headers
 4. Depending on your OS you might need to change line 3 in `Makefile`
    from
    ```
    KERNELDIR ?= /lib/modules/$(shell uname -r)/build
    ```
    to
    ```
    KERNELDIR ?= /usr/lib/modules/$(shell uname -r)/build
    ```
 5. Build the kernel module:
    ```
    $ make
    ```
 6. Remove default module, install new module for testing:
    ```
    # modprobe -r cdc_acm
    # insmod ./xr_usb_serial_common.ko
    ```
 7. Unplug & plug in device again. `dmesg` should output the following:
    ```
    usb 1-2: new full-speed USB device number 10 using xhci_hcd
    usb 1-2: New USB device found, idVendor=04e2, idProduct=1414, bcdDevice= 0.03
    usb 1-2: New USB device strings: Mfr=0, Product=0, SerialNumber=0
    cdc_xr_usb_serial 1-2:1.0: USB_device_id idVendor:04e2, idProduct 1414
    ...
    cdc_xr_usb_serial 1-2:1.0: ttyXR_USB_SERIAL0: USB XR_USB_SERIAL device
    ...
    cdc_xr_usb_serial 1-2:1.6: ttyXR_USB_SERIAL3: USB XR_USB_SERIAL device
    ```
    The kernel should have autodetected 4 new USB serial devices.
    Usually they are named `/dev/ttyXRUSB[0-4]`.
 8. Depending on your OS you might need to add your user to the correct
    unix group so that you have the rights to open the device. On Arch
    Linux for example, the group name is `uucp`:
    ```
    $ ls -l /dev/ttyXRUSB*
    crw-rw---- 1 root uucp 266, 0 May 29 19:49 /dev/ttyXRUSB0
    crw-rw---- 1 root uucp 266, 1 May 29 19:49 /dev/ttyXRUSB1
    crw-rw---- 1 root uucp 266, 2 May 29 19:49 /dev/ttyXRUSB2
    crw-rw---- 1 root uucp 266, 3 May 29 19:49 /dev/ttyXRUSB3
    ```
    As you can see here the devices have rw rights for group `uucp`,
    so add your user to this group:
    ```
    # usermod -a -G uucp <username>
    ```
    Other common group names for tty access on other Linux operating
    systems are `dialout`, `tty` or `lock`.  
 9. Test if you can communicate with the device, using minicom. 
    See "minicom" below.
10. Depending on your OS, make sure that the kernel module 
    `xr_usb_serial_common.ko` gets loaded during system reboots, and 
    that it will be rebuilt on kernel updates. See `dkms` 
    documentation for that.


## EagleInit

Before first use of the NB IoT device it must be initialized with Eagle
root data and optional DP authentication parameters. Currently, this
can be done on Windows only.

On Windows do the following: _(Note: running Windows on Linux in a
VirtualBox VM also works, but you need to install the Exar XR21V1414
kernel module on the Linux host, see section "Linux driver setup" 
above. Make sure that the module `xr_usb_serial_common` is *unloaded*!)_

 1. Install .NET Framework 4.7.2 **Offline Installer** from
    https://dotnet.microsoft.com/download/thank-you/net472-offline
 2. Install `EagleInit-3.36.0.2.msi` and `UEMonitor-3.36.0.2.msi` which
    can be found in the `./tools/` subdirectory.
 4. Run **EagleInit**:
 
        C:\> cd C:\Program Files\Neul\EagleInit
        
        C:\Program Files\Neul\EagleInit> EagleInit.exe AddEidKeyAndCert -i <srcdir>\tools\EagledataNoDpNoCert3.bin -o out.bin -p COM3`
        
        EagleInit v3.36.0.2
        Z:\Downloads\eagle-huawei-tools\EagledataNoDpNoCert3.bin loaded, 118 bytes long
        Attempting to establish communications with UE...
        Trying to reset UE using bootloader commands...
        Trying to reset UE using AT command...
        Trying to reset UE using bootloader commands...
        Trying to reset UE using AT command...
        Initial bootInfo
        Secondary bootInfo/ack
        Verifying connection...
        Bootloader: Chip:H2115Cleopatra Stage:SsbRom V:5 is compatible with Unconstrained:True
        Comms established to H2115Cleopatra, bootloader stage SsbRom protocol version 5.1
        Established communications with UE
        Loading payload containing EID+Capabilities+Key+optional Certificate. Return key material or status
        Initial bootInfo
        Verifying connection...
        Bootloader: Chip:H2115Cleopatra Stage:FlashSecurity V:5 is compatible with SecurityCore:True
        Z:\Downloads\eagle-huawei-tools\out.bin saved, 65 bytes long
        Application finished.



## Plugin setup

To enable the **Huawei NuSim Loader App Plugin**, replace the _complete_ section `"nusimSimAdapter"` in `defaultConfig.json` with:

```
"nusimSimAdapter": {
  "adapterClass": "de.scoopgmbh.nusimapp.nusimsim.adapter.huawei.HuaweiNusimSimAdapter",
  "serialPort": "/dev/ttyXRUSB0",
  "baudRate": 9600,
  "commandRetries": 3,
  "commandTimeoutMs": 10000,
  "commandRetryDelayMs": 500
},
```

Adjust the settings `serialPort` and `baudRate` according to your environment. After changing the settings, restart the server.



## Testing

### minicom

On Linux, use `minicom` to interact with the device and test AT commands.

`LANG=C minicom --device /dev/ttyXRUSB0 --baudrate 9600 --color=on --ansi`

Important! Switch off hardware and software control first!
- `Ctrl-A O` => Serial port setup => Hardware flow control: No
- `Ctrl-A O` => Serial port setup => Software flow control: No
- `Ctrl-A O` => Save setup as dfl

Also important: enable local echo:
- `Ctrl-A E` : echo on

Use `Ctrl-A X` to exit `minicom`.


### Test vectors

Set profile _(see markdown source code of this README.md for full text)_:
```
AT+NSESIM=SETPROFILE,691,04a47a63...DD94AF28
```
<!--
AT+NSESIM=SETPROFILE,691,04a47a63c15060f1b524c58082a2956c2139b2fd8688274cd5d756189b84f94e400cd691ed9be1f0f8a2d206850d08328426fa0c092bce39b98a50ed0b2ed871de7016D34479DA105EAE10154D974D131384016A0A2A314C557F7AD5CF35A48D5872F495902494F967643E8BBB46567BFD89165D2AA825A5BBD57A715A4B6332332A42EE8F64A9068B62A1C730598CCF72041b7cc46825b5ebf4cba03d4dbfdc1ddc2eb32f10f72ca52b64f9c58edfece3668376c741d31598e38c1c22e873bad20da1506bcf9004c305dcbe8782835d58fa6E13ECF5E7A7C948CE85A38024F19272A84006D3F233931AA3AE7A45457A66108E447B66B4C05287593086CD586C07CB8DD5372BCB640F3D4AC838A87632CA9104451669370a39c14aa166048a939c6061dc6066418e885fb71b4d595647e02da287553a538a081b2cbce2eec79043f4dc674371456fba5d818be58b35393b3bf48041AD49FC2F84BF84D376E30EB5B7E6E00E43A07977D56DB6A63DDD7041887607CDD2C1EBB97A9A14BAD7D8682D1BB17EB67BF3971ECEFE8AE8EFD22F2E85812789CAA1CF706ADB10F16C96CD625F38A74ADA8D499E9F03B5797B9E9BCB74F79F01FF14C4077D28F5DD963DEB2BBF5AD80B16A7FAEC799B35E38D6E56B92ABDDF5B0B55DCFA3339580A1C0D76578095DEC92DC561B6C66C5470E121B2BA054FB471F0EB68FD38C4C42E4576BC75D483B886EA4E436A0A91A7C5BFBF3CB30AA524B286F428CBC51100C78E47A8D76BBF75BE70F70AA578B320204FCFDBFA9A219BA65E7A41F0F1E7D354D1EABE5B7FF592A4952E08F4AAC1E204CAC00CC54C347217544E09FE614460AAE52B2005E3630A5176A892698ECACBC5D54A344007129105F296C7ADCB90AA283D78D56FCE4AD6D1EDD9637736DAEBF0D3DF9A6F73A7A59BE0768164F1C8434C67C3F54924E191A64752E00A94864A05D7CFDD94AF28
-->


Delete profile _(must set powered-down state first)_:
```
AT+CFUN=0
AT+NSESIM=DELETEPROFILE
```

