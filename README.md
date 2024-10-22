<h1 align="center">
    nuSIM-Loader-Application
</h1>

<p align="center">
    <a href="https://github.com/telekom/nuSIM-Loader-Application/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/nuSIM-Loader-Application?style=flat"></a>
    <a href="https://github.com/telekom/nuSIM-Loader-Application/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/nuSIM-Loader-Application?style=flat"></a>
    <a href="https://github.com/telekom/nuSIM-Loader-Application/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#documentation">Documentation</a> •
  <a href="#Plug-In-Adapters">Plug-In Adapters</a> •
  <a href="#build">Build</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## 1 About this project

The goal of this project is to provide a basic reference implementation of the nuSIM loader (nuSIM Loader Application) to control the nuSIM profile provisioning process, e.g. at a device production line. Functions to request and receive encrypted nuSIM profiles from a remote nuSIM Data Preparator, to store them in a local database and finally load them to the target devices are implemented according to the nuSIM technical specifications in combination with an elementary GUI. The reference implementation can be further developed and extended to fit with given production environments and particular needs of a nuSIM device manufacturer.

## 2 Documentation

### 2.1 Requirements

- Java 8 JRE with an up-to-date patch level
- PostgreSQL DB Version 9 or later (see e.g. https://stackoverflow.com/questions/26441873/starting-postgresql-and-pgadmin-in-windows-without-installation for Windows installation of PostgreSQL), readily configured with a database and user. See section "Default Configuration" below.
- a modern Browser with enabled JavaScript like
    - Firefox 56+
    - Chrome 62+
    - Safari 10+

### 2.2 Installation

There is no need for any further installation tasks. Also, no adminstrator / root access is needed to install the Application.
It is delivered as a simple ZIP file or a TAR file.
Unpack the delivery into a directory of your choice.

### 2.3 Starting the Application

- Be sure to change into an appropriate directory, since the application will use the current working directory if not defined otherwise. See section "Configuration" below. It is advisable to start the application from within the directory where you unpacked the installation zip.
- Under Linux / MacOS, run `nusimapp-x.x.x/bin/nusimapp`
- Unter Windows, run `nusimapp-x.x.x\bin\nusimapp.bat`
- Open http://localhost:${server.port} in your browser (if not configured otherwise)

Please see section "Default Configuration" below for details about starting the application for the first time.

### 2.4 Configuration

#### 2.4.1 Environment Variables

The following environment variables are supported:

- `NUSIMAPP_HOME_DIR`: Defines the base directory of the NUSIM Loader App. This directory is used for any user defined data, e.g. to store the configuration and the logs. It can be set to either an absolute path or a path relative to the current working directory. Defaults to a directory `nusim-data` inside the current working directory.
- `NUSIMAPP_LOGGING_DIR`: Can be set to either an absolute path or a path relative to the current working directory to define where logfiles are stored. Defaults to a directory `logs` within `NUSIMAPP_HOME_DIR`
- `NUSIMAPP_LOG_CONSOLE`: Can be set to `true` to enable additional logging to stdout instead of to logfile only
- `NUSIMAPP_OPTS`: Can be set to configure jvm specific options. May be needed for debugging purposes.

#### 2.4.2 Default Configuration

When starting the application for the first time, there is no configuration file available in `NUSIMAPP_HOME_DIR/conf`, and a default configuration becomes effective. This default configuration file is named `defaultConfig.json` and can be found in the application directory. The most important configuration options are:

- The Server will be listening at port `${server.port}`, will *not* offer HTTPS and as such the GUI will be available at http://localhost:${server.port}
- The Database that is used must be available at `${database.jdbcURL}` using user/password = ${database.username}/${database.password} as credentials.
- The FileImporter will watch for input files at `${fileimport.importBaseDir}`. This directory is created if it does not exist.
- The pluggable IF2 nuSIM adapter is per default configured to use the dummy class `${nusimSimAdapter.adapterClass}` which is a simple HTTP simulator implementing the nuSIM adapter interface. Most likely you will want to use another adapter implementation. For this you must change the configuration section `"nusimSimAdapter"` in file `defaultConfig.json` before the first start. See section "Implementing a specific nuSIM adapter (IF2 adapter)" above.

You can always restore the default configuration by deleting your custom configuration file `nusimapp.conf` residing in `NUSIMAPP_HOME_DIR/conf` (requires restart)

#### 2.4.3 Configuration via GUI

After starting the application, it can be configured in detail by opening the confguration gui available at http://localhost:${server.port}/configuration

These are the configurable parameters. Those marked with [*] can be changed without a restart of the application.

General note: Any paths can be either provided as absolute paths or as paths relative to the current working directory.

- **Provisioning**
    - **Provisioning Mode** [*]: Defines whether the Provisioning will be running in Bulk Mode oder Single Mode.
    - **Certificate Source** [*]: Defines where to get the nuSIM Certificates from when using Single Mode.
- **DP Interface**
    - **General**
        - **Base URL**: Defines the URL where to reach the DP interface.
        - **certCM** [*]: The certCM to be used for Provisioning.
    - **TLS Configuration** (used when the Base URL is an https URL)
        - **Certificate Location**: directory for trusted certificate files. The application expects a file named `trusted.crt` to exist in this directory, containing a list of PEM certificates/CAs to trust when connecting to the adapter via HTTPS. The application optionally supports the use of client certificates, if there is a `client.key` and `client.crt` key/certificate pair available.
- **CM Interface**
    - **General**
        - **Base URL**: Defines the URL where to reach the CM interface.
    - **TLS Configuration** (used when the Base URL is an https URL)
        - **Certificate Location**: directory for trusted certificate files. The application expects a file named `trusted.crt` to exist in this directory, containing a list of PEM certificates/CAs to trust when connecting to the adapter via HTTPS. The application optionally supports the use of client certificates, if there is a `client.key` and `client.crt` key/certificate pair available.
- **Management via Web GUI** (Configuration regarding the server part of the application)
    - **General**
        - **Listening Port**: Defines the listening port of the application. This port is used to deliver the web application.
        - **Path Prefix**: Defines an URL Prefix that is used for reaching the application. This defaults to "`${server.pathPrefix}`"
    - **TLS Configuration**
        - **Require TLS**: If enabled, the server will provide an HTTPS connection, otherwise it will provide HTTP only. Be aware that this will only affect the communication between the GUI and the application server. It does not affect any partner interfaces. Defaults to `${server.ssl.enabled}`
        - **Path to own certificates**: directory for server certificate files. The server expects two files to exist in this directory, `server.crt` and `server.key`, a certificate and private key file in PEM format that are used when TLS is enabled.
        - **Supported Protocols**: A comma separated list of TLS Protocols to support when TLS is enabled. Possible Values: `TLSv1` and `TLSv1.2`.
- **Database**
    - **General**
        - **Show SQL Exceutions in Logs**: Debug Flag to enabled logging of all SQL statements performed.
        - **DB username**: The username to be used when connecting to the PostgreSQL DB. Defaults to `${database.username}`
        - **DB password**: The password to be used when connecting to the PostgreSQL DB. Defaults to `${database.password}`
        - **DB URL**: The JDBC URL to be used when connecting to the PostgreSQL DB. Defaults to `${database.jdbcURL}`. Of the form `jdbc:<dbms>://<host>:<port>/<db path>`
- **Bulk File Import**
    - **General**
        - **Base directory for files**: A directory (created if not exists) to be used as a base directory for the file adapter. This can be an absolute path or a relative path under `NUSIMAPP_HOME_DIR`
        - **File suffixes for EID list files**: A comma separated list of file endings that are to be parsed as EID list files. Defaults to `${fileimport.eidFileEndings}`
        - **File suffixes for certificate file archives**: A comma separated list of file endings that are to be parsed as Certificate archives. Defaults to `${fileimport.certificateFileEndings}`

### 2.5 Usage

#### 2.5.1 File Import

The nusimapp supports the import of files with EIDs and (optionally) nuSIM certificates into the system. These will then be used when running the application in `bulk mode`.

File import works by simply storing files in the appropriate directory. The base directory can be configured and defaults to `NUSIMAPP_HOME_DIR/import`. Within this base directory, three subdirectories are created:

- `in`: files within this directory will be processed automatically by nusimapp
- `out`: any file that was processed successfully will be moved here.
- `error`: contains files that could not be processed successfully. These files can be reprocessed by moving them back into `in`

Two types of files are supported:

- **EID list files** are simple text files that contain exactly one eid per line. These are used to feed the system with EIDs where the corresponding certificates are provided by the CM through the IF3 interface.
- **certificate file archives** are archive files (ZIP, tar), that contain certificate files. These files in turn must be named `cert_<eid>.pem`, where `<eid>` defines an EID. The content of each certificate file must be a single PEM certificate for the EID derived from its name.

#### 2.5.2 Web GUI Status

When opening the web gui, it connects itself to the running nusimapp to receive live data about the apps current state. As soon as the connection is established, this is indicated by a green `sync` icon in the upper right corner.
Should the connection be lost for any reason, this is immediately indicated by the icon turning red. Furthermore, the connection is retried until it succeeds again.

This allows for restarting the nusimapp without having to reload the web gui.

#### 2.5.3 Web GUI Views

The web gui allows you to configure, control and monitor the nusimapp.

Use the `Logging` view to receive a live view of the logs. You can filter the level of displayed logs from `DEBUG` (most verbose) to `INFO` (normal log level), `WARNINGS` (shows only warnings and errors) and `ERROR` (shows only error messages) by selecting the appropriate entry from the dropdown list called `Loglevel`.
After extended use of the application, the internal logbuffer will eventually overflow.
This can result in moving log lines, event if `Follow Logs` is not selected. Depending on your browser and hardware, this can also result in the Web GUI becomin laggy. In this case, you can simply refresh your browser page which implicitly empties the log buffer.

Use the `Configuration` view to display and change the configuration that is currently effective. The configuration is subdivided by technical aspects. Each section can be opened by clicking.
By clicking `Save` the configuration is stored in `NUSIMAPP_HOME_DIR/nusimapp.conf`. Depending on the parameters changed, the new configuration will become effective either immediately or as soon as the nusimapp has been restarted, which is communicated by a confirmation dialog.

#### 2.5.4 Web GUI Actions

Click on `Get Certs` to retrieve nuSIM certificates from the CM interface for all previously imported EIDs without certificates. A popup dialog will show you the number of loaded certificates or a technical error message.

Click on `Request Profiles` to request profile retrieval for previously imported EIDs that already have certificates, but no profile information yet. These can either be EIDs imported with a **certificate file archive** or EIDs that were imported with an **EID list file** and have their certificate already loaded.  A popup dialog asks you to specify the maximum number of profiles to be requested, as well as the refInfo fields for this request. A confirmation dialog will show you the actual number of requested profiles or a technical error message.

Click on `Retrieve Profiles` to retrieve previously requested profiles. A popup dialog shows all pending Reference IDs and their requested data. A green icon indicates that the estimated retrieval time has already passed, a red icon indicates that it is currently too early to perform the retrieval. Klick on any entry to perform the retrieval. A confirmation dialog will show you the actual number of retrieved profiles or a technical error message.

Click on `Stock Information` to query profile stock information from the DP. A popup dialog asks you to specify the refInfo fields for this request. A result dialog will show you a list of stock information available.
All Profile stock query operations are logged into a CSV file `NUSIMAPP_HOME_DIR/provisioning/queryProfileStock_<date>.csv` where `<date>` denotes the current date (without time).

Click on `START` to actually start the provisioning of nuSIMs. A popup dialog will ask to provide a number of SIMs you want to provision. After confirming this dialog the provisioning process will begin.

The provisioning process will stop

- on any error
- after the number of requested SIMs are provisioned successfully
- after clicking the `STOP` button.

Every Provisioning run will create a separate _provisioning file_ containing the ICCID and EID of all successfully provisioned SIMs in CSV format. the files will be stored into `NUSIMAPP_HOME_DIR/provisioning/provisioning_<dateTime>.csv` where `<dateTime>` denotes the local timestamp of the beginning of a provisioning run.

Any GUI action triggered can be monitored in the `Logging` view with a recommended log level of `INFO` or `DEBUG`

#### 2.5.5 Web GUI Input Dialogs

During Web GUI Actions, the user is asked for input data (e.g. number of profiles to retrieve, refInfo data, etc.). All entered data is saved on the users local browser *per dialog* and is used to pre-fill the form when the dialog is displayed.

#### 2.5.6 Feature Switches

##### 2.5.6.1 Demo Mode

Per default configuration, the GUI Actions are fully functionable. For demonstrational purposes the four GUI actions related to Bulk Provisioning can be disabled by changing the configuration value `provisioning.demoModeFeature` to `true` (not `"true"` !!), restarting the server and reloading the GUI. After that, the four GUI actions "Get Certs", "Request Profiles", "Retrieve Profiles" and "Stock Information" will be disabled.

##### 2.5.6.2 Repeatable Downloads

Per default configuration, an EID can only be used once: As soon as a nuSIM is successfully
provisioned, the corresponding EID profile data that may exist in the Database is deleted to prevent
reusing the EID.

For demonstration and/or testing purposes however, it may be preferable to be able to reuse
an EID. To allows this behavior, the configuration has a feature toggle that can be enabled
by changing `provisioning.repeatableDownloadsFeature` to `true` (not `"true"` !!).

### 2.6 Troubleshooting

#### 2.6.1 Startup Problems

##### 2.6.1.1 `java.net.UnknownHostException:` during startup

This error arises when the application cannot resolve the current computers hostname. Under linux, be sure that your own hostname is either resolvable via standard DNS or locally by configuring your `/etc/hosts` file accordingly.

##### 2.6.1.2 `Ident authentication failed for user` during startup

This error arises when your postgreSQL database instance cannot login the application due to misconfigured authentication methods. You can prevent this by changing your databases configuration. See e.g. https://www.postgresql.org/docs/9.1/static/auth-pg-hba-conf.html and set `auth-method` to `trust` for IPv4 and IPv6 connections.

## 3 Plug-In Adapters

### 3.1 Implementation of customized plug-in adapters

This software contains an example implementation of an IF2 nuSIM adapter that connects to a nuSIM Simulator via HTTP. It also contains a reference implementation of an IF3 Adapter that connects to a CM Simulator via HTTP(s). Additional module-specific adapters for IF2 and IF3 can be developed according to the following guidelines. 

#### 3.1.1 Implementing a specific nuSIM adapter (IF2 adapter)

To create a specific IF2 nuSIM adapter for communicating with an nuSIM, you must create a new Java class that extends `de.scoopgmbh.nusimapp.nusimsim.adapter.NusimSimAdapter` of submodule `nusimloader-api` and implement all abstract methods. These abstract methods are:

1. `getEID()` to retrieve the EID
2. `getCertNusim()` to retrieve the nuSIM certificate in PEM format
3. `getNusimCapabilities()` to retrieve the NUSIM capabilites as specified
4. `loadProfile(eid, mac, encP, eKPubDP, sigEKPubDP, kPubDP, sigKPubDP)` to load profile data onto the nuSIM and return an ICCID

To acually _use_ the adapter, its class name has to be configured into the default configuration file (`defaultConfig.json`) at JSON path `nusimSimAdapter.adapterClass`

The adapter will have access to the JSON configuration residing below JSON path `nusimSimAdapter`. Any needed parameters can be defined there.

#### 3.1.2 Implementing a specific CM adapter (IF3 adapter)

To create a specific CM adapter for communicating with a Chip Manufacturer, you have to create a
new Java Class that extends `de.scoopgmbh.nusimapp.if3.IF3Adapter` and
implement the abstract methods:

1. `getCertificate(eid, rootKeyId)` to retrieve the a certificate for the given EID and rootKeyId

To acually _use_ the adapter, its class name has to be configured into the default configuration
file (`defaultConfig.json`) at JSON path `if3.adapterClass`

The adapter will have access to the JSON configuration residing below JSON path `if3`. Any needed parameters can be
defined there.

## 4 Build

### 4.1 Requirements

- latest Java 8 JDK

**Note:** During the build, the build process needs an internet connection to download Java and JavaScript Libraries on demand.
In case you need to use a proxy, please refer to the gradle and yarn documentation, for example:

- https://docs.gradle.org/current/userguide/build_environment.html#sec:accessing_the_web_via_a_proxy
- https://www.jhipster.tech/configuring-a-corporate-proxy/

### 4.2 Building the Application / Creating a distribution ZIP

You can build all artifacts - gui and server related - and wrap them into a distribution ZIP, by
running  `./gradlew distZip` (Linux) from within the root folder of the project. Windows users can use the
corresponding `gradlew.bat distZip`

This will implicitly download all Java and Javascript dependencies, generate and compile all Java code, generate and
transpile all JavaScript code and finally pack everthing together into a
file `./nusimapp/build/distributions/nusimapp-<version>.zip`

Create a source zip file with `./gradlew srcZip` (Windows: `gradlew.bat srcZip`). This creates a
file `./nusimapp/build/distributions/nusimapp-<version>-src.zip`.

Create OSDF library documentation with `./gradlew createOSDF` (Windows: `gradlew.bat createOSDF`). This creates the
following files:

- `./build/osdf/nusimapp-<version>-osdf.pdf` and
- `./build/osdf/nusimapp-<version>-librarySources.zip`.

#### 4.2.1 Troubleshooting

The build may occasionally hang in step `:gui:build` for several minutes. Unless there's no further error output, this
is not a severe error condition. In this case, simply abort the process with `Ctrl-C` and continue
with `./gradlew -x :gui:build distZip` or `gradlew.bat -x :gui:build distZip` respectively.

This can be applied to any build task (simply add `-x :gui:build` as a command line parameter to `./gradlew`
or `gradlew.bat`)

### 4.3 Building and developing the Web GUI only

Base folder for the WebGUI is `./gui`. Run any commands from within this directory.
Make sure the correct version of yarn is downloaded once by running `../gradlew yarn` (Windows: `..\gradlew.bat yarn`)

Download all dependencies for the GUI by running `.gradle/yarn/yarn-v1.7.0/bin/yarn install`

You can run the GUI in development mode by running `.gradle/yarn/yarn-v1.7.0/bin/yarn dev`

You can build the production artifacts by running `.gradle/yarn/yarn-v1.7.0/bin/yarn build`. These will then be built
into `.dist`

### 4.4 Building and developing the Server

Build the Server by running `./gradlew :nusimapp:build` (Windows: `gradlew.bat :nusimapp:build`)

Run the Server by running `./gradle :nusimapp:run` (Windows: `gradlew.bat :nusimapp:run`)

## 5 Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.0 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

## 6 Working Language

We decided to apply _English_ as the primary project language.  

Consequently, all content will be made available primarily in English. We also ask all interested people to use English as language to create issues, in their code (comments, documentation etc.) and when you send requests to us. The application itself and all end-user facing content will be made available in other languages as needed. 


## 7 Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**   | <a href="https://github.com/telekom/nuSIM-Loader-Application/issues/new/choose" title="General Discussion"><img src="https://img.shields.io/github/issues/telekom/nuSIM-Loader-Application?style=flat-square"></a> </a>   |
| **Other Requests**    | <a href="mailto:opensource@telekom.de" title="Email Open Source Team"><img src="https://img.shields.io/badge/email-Open%20Source%20Team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## 8 How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## 9 Contributors

Our commitment to open source means that we are enabling - in fact encouraging - all interested parties to contribute and become part of our developer community.

## 10 Licensing

Copyright (c) 2020 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
