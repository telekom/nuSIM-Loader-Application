Changelog
=========

## v0.5.0
- all HTTP connectors support proxy configuration: configure e.g. `/if1/proxyURI` to a validURI (e.g. `http://localhost:3128/`) to enable proxying requests to DP connector.
  You can also configure `proxyUser`  and `proxyPass` to a non-empty value to enable basic authentication. Needs a restart
- HUAWEI plugin is now available under `nusimloader-plugin-huawei` 
- new Configuration `bulkRequestAuthFlag` availbale for `if1`, and can be changed without restart
- EIDs are always requested case-insensitively from DB (although, still being stored as-is)
- FIX: when converting from PEM to DER, certificates still contained whitespace characters
- FIX: when a Profile becomes available while the RetrieveProfileDialog is open, the icon color changes accordingly
- FIX: added a line break in RetrieveProfileDialog to always display the "Available at" timestamp correctly
- FEATURE: IF3 interface enhanced to support bulk retrieval of certificates (breaks compatibility)

## v0.4.9 github 
- removed directories docs, nusimloader-plugin-huawei
- nusimloader-plugin-huawei section removed from  build.gradle
- include 'nusimloader-plugin-huawei' removed from  settings.gradle
- HUAWEI nuSIM adapter section removed from readme.md

## v0.4.8
- new config If1.bulkRequestAuthFlag for setting authFlag for bulk request
- added message "DP authentication information required. Please run Get KpubDP first" when config is missing 

## v0.4.7
- KPubDP information is now stored in configuration to allow bulk mode without havinbg to contact DP
- configuration GUI: toggling the certificateSource Parameter ist now visible for single mode configuration only

## v0.4.6
- now really finally fixed the problem with missing Signature Algorithm 1.3.101.112 (EDDSA, ED25519) by removing the library 'net.i2p.crypto:eddsa' again - in fact BouncyCastle 1.66 works fine with EDDSA/ED25519 already (verified with live pre-prod)

## v0.4.5
- include OSDF files

## v0.4.4
- HTTP client timeout is now set to 60 seconds

## v0.4.3
- finally fixed the problem with missing Signature Algorithm 1.3.101.112 (EDDSA, ED25519)

## v0.4.2
- try to fix problem with missing ED25519 cipher

## v0.4.1
- added ability to generate license infos with `gradlew licenses licenseInfo` 
- provisioning dialog now distinguishes between single and bulk mode
- optical improvement of displaying RefInfos

## v0.4.0
- updates according to nuSIM Provisioning spec update v1.4.1 and v.1.5.0:
  * for DP requests `GetEncProfile` and `RetrieveBulkEncProfiles` the LA now accepts attribute `SigEKpubDP` in responses. (The old attribute `SigEKpub` is still accepted for backward compatibility with older DP implementations.)
  * in case refId1/2/3 are not provided in the UI, LA now sends empty strings instead of `null`, because refIds are now required by DP.
- new feature **repeatable downloads**: a profile can be uploaded to the same device multiple times by setting config property `provisioning.repeatableDownloadsFeature` to `true`. (Off by default.)
- new feature **demo mode**: the buttons "Get Certs", "Request Profiles", "Retrieve Profiles" and "Stock Information" can now be disabled by setting config property `provisioning.demoModeFeature` to `true`. (Off by default.)
- UI: nuSIM logo and SCOOP logo now prominently displayed in header; replaced Wifi icon with a different icon showing connection status
- Huawei plugin: always close connection to serial port after profile upload, so that the serial port can be used for the next device (or for other use cases such as monitoring)

## v0.3.3
- allow use of Ed25519 EC keys by updating bouncycastle 1.59 -> 1.63
- FIX: allow EC private keys (and other key types) for LA client.key
- FIX: RetrieveBulkEncProfiles request: use `referenceID` instead of `referenceId`
- FIX: UI: Profile Stock Information label for RefInfo3 was named "RefInfo2"
- better understandable error messages for "Get Certs" function: now clearly separates between empty database and populated database with EIDs without certs

## v0.3.2
- FIX: don't always require DP-Auth in bulk mode, instead ask device capabilities like in single mode

## v0.3.1
- FIX: Java 11 issue

## v0.3.0
- added Huawei plugin (see nusimloader-plugin-huawei)
- made compatible with Java 11

## v0.2.4
- new optional attributes for RetrieveBulkEncProfiles and RequestBulkEncProfiles
- FIX: better logging when RequestProfiles fails with empty input data
- FIX: RetrieveProfileDialog: typo in RefInfo display

## v0.2.3
- ALWAYS try to delete EID from PROFILES table after successful provisioning
- FIX: Single Mode Provisioning did not retrieve Cert retrieval via DB
 
## v0.2.2
- first public release with name "nuSIM"
