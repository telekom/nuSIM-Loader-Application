#How to add new licenses

there are 2 gradle plugins reading all libraries:
- licenseReport(java) - generates JSON (```build/licenses/index.json```)
- licenseText (npm/javascript) - generates JSON (```gui/build/javascript-licensesText.json```)

for basic information copy the new plugins from the JSONs above and insert them into
- ```osdfcreator/src/main/resources/allLicensesBackend.json```
<br> or <br>
- ```osdfcreator/src/main/resources/allLicensesJavaScript.json``` 

if there appears to be a new license with a text that only needs to be listed once
insert it into:
 - ```osdfcreator/src/main/resources/generalLicenses.json``` 
 
otherwise please refer to the other entries using the same license

#general rules:

---

---

## OSDF (Open Source Declaration File) for repobasics

<!-- IF your work is open source software too, treat as part of this list -->
## Overview of open source software (OSS) (components / dependencies):
*   [C1](#component-c1) - **License-of-C1-SPDX-Identifier** <!-- [TODO] Insert SPDX identifier -->
*   [C2](#component-c2) - **License-of-C2-SPDX-Identifier** <!-- [TODO] Insert SPDX identifier -->
*   [CX](#component-cX) - **License-of-CX-SPDX-Identifier** <!-- [TODO] Insert SPDX identifier -->
<!-- [TODO] Insert Markdown heading link to each component. Ensure the link is working. -->

<!-- IF the component list contains a component licensed under any copyleft license: -->
## written offer:
This project uses sub-components licensed under any copyleft license. So, we are obliged also to handover the source code to the user. For doing so, we select the method of granting a 'Written Offer' to you:

<!-- [TODO] Insert your written offer here -->

<!-- FI -->

## [PROJECT_NAME] uses the following third party OSS components: 
<!-- [TODO] Replace [PROJECT_NAME] -->

### Component: c1 
<!-- [TODO] Replace c1 with component name -->

* Component name: <!-- [TODO] Insert -->
* Component version:  <!-- [TODO] Insert the version you use -->
* License type: <!-- [TODO] Insert the SPDX identifier(s) -->
* Public homepage: <!-- [TODO] Insert, IF exists -->
* Public repository: <!-- [TODO] Insert -->

<!-- IF the component is licensed under multiple licenses: -->
<!-- [TODO] List all license type SPDX identifiers in the list above -->
<!-- [TODO] Replace [LICENSE] in the line below with the SPDX identifier of one of the licenses, we recommend to choose the most permissible license available -->
We are redistributing this component under the terms of the [LICENSE] license.
<!-- FI -->

<!-- IF the component is licensed under the Apache-v2 license: -->
<!-- [TODO] Insert the license text once in the appendix, and place a link to the heading in the appendix here -->
License Text: [Apache-2.0](#Apache-20)  
  
NOTICE file in the repository:
<!-- IF such a file exist -->

<!-- [TODO] Insert the notice file text here as found in the repository, wrap it in three back ticks (```) for proper formatting. -->

<!-- ELSE -->
Project does not use a file named NOTICE
<!-- FI -->

<!-- ELSE IF the document is licensed under any FSF / GNU (GPL) license> -->

<!-- [TODO] Insert the list of copyright owners and authors, grepped out of the repository source code, wrap it in three back ticks (```) for proper formatting. -->

<!-- ELSE IF the component is licensed under the MIT, ISC or any BSD (except 0BSD) license: -->
License Text:
<!-- [TODO] Insert the original license text here, including the copyright line, wrap it in three back ticks (```) for proper formatting. -->

<!-- ELSE IF the component is licensed under the CC-BY-3.0 or CC-BY-4.0 license: -->
License Text: [CC-BY-X.0](#CC-BY-X0) <!-- [TODO] Insert the license text once in the appendix, and update the link to the heading in the appendix here -->
 
<!-- [TODO] IF the authors specify how they want to be credited, follow these instructions. -->
<!-- [TODO] IF the authors include any of the following information in any file (LICENSE, COPYING, README, AUTHORS, ...) in the root directory of their repository, include the original text, wrap it in three back ticks (```) for proper formatting: 
    - Copyright notices
    - Names or pseudonyms of copyright holders or authors
    - Notices referring to a disclaimer of warranty 
-->

<!-- IF you modified the licensed material -->
<!-- [TODO] List the modifications -->
<!-- ELSE -->
The licensed material has not been modified.
<!-- FI -->

<!-- ELSE IF the component is in the public domain using a custom statement: -->
License Text:
<!-- [TODO] Insert the original statement text here, wrap it in three back ticks (```) for proper formatting. -->

<!-- ELSE IF the component is licensed under any other license-->
<!-- [TODO] Insert the license text once in the appendix, and place a link to the heading in the appendix here -->
License Text: [SPDX-LICENSE-IDENTIFIER](#SPDX-LICENSE-IDENTIFIER)
<!-- FI -->

## Appendix: the multiply used license texts

### SPDX-LICENSE-IDENTIFIER

### Apache-2.0

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->

### GPL-3.0-or-later

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->

### CC-BY-X.0

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->

#examples given:

---
---

# OSDF (Open Source Declaration File) for repobasics
<!-- [TODO] replace "repobasics" with the name of your project -->

<!-- General Instructions -->
<!-- 
- This file contains demo components that illustrate how each license type should be handled. You can delete all demo components that do not correspond to licenses used in your project.

- Make sure that each of your components is listed in the overview chapter, and has a working clickable link

- Within each demo component ensure that you follow all [TODO] instructions.

- If you have any questions, get in touch with your TOSCom Advisor or reach out to opensource@telekom.de
-->


<!-- [TODO] If your work is open source software too, treat as part of this list -->
## Overview of open source software (OSS) (components / dependencies):
- [Components](#components)
  * [Component: apache-2.0-demo](#component--apache-20-demo) - Apache-2.0 <!-- [TODO] Insert SPDX identifier(s) -->
  * [Component: mit-isc-bsd-demo](#component--mit-isc-bsd-demo) - MIT <!-- [TODO] Insert SPDX identifier(s) -->
  * [Component: (l)gpl-demo](#component---l-gpl-demo) - GPL-3.0-or-later<!-- [TODO] Insert SPDX identifier(s) -->
  * [Component: cc-by-x.0-demo](#component--cc-by-x0-demo) - CC-BY-3.0 <!-- [TODO] Insert SPDX identifier(s) -->
  * [Component: public-domain-demo](#component--public-domain-demo) - Public Domain <!-- [TODO] Insert SPDX identifier(s) -->
  * [Component: multiple-licenses-choice-demo](#component--multiple-licenses-choice-demo) - LGPL-2.0, MIT <!-- [TODO] Insert SPDX identifier(s) -->
  * [Component: other-licenses-demo](#component--other-licenses-demo) - SPDX-Identifier <!-- [TODO] Insert SPDX identifier(s) -->
<!-- [TODO] List Markdown heading links to each component and the SPDX identifier(s) for the licenses. Ensure the link is working. Tools such as https://ecotrust-canada.github.io/markdown-toc/ can help to generate the correct link slug based on the heading name. -->

- [Appendix](Appendix)
  * [Apache-2.0](#apache-20)
  * [GPL-3.0-or-later](#gpl-30-or-later)
  * [CC-BY-3.0](#cc-by-30)
<!-- [TODO] List Markdown heading links to each license in the appendix. Ensure the link is working. Tools such as https://ecotrust-canada.github.io/markdown-toc/ can help to generate the correct link slug based on the heading name. -->


<!-- IF the component list contains a component licensed under any copyleft license: -->
## written offer:
This project uses sub-components licensed under any copyleft license. So, we are obliged also to handover the source code to the user. For doing so, we select the method of granting a 'Written Offer' to you:

<!-- [TODO] Insert your written offer here -->
```
[Telekom Department Name] hereby offers, valid for at least three years, to give you or any third party, for a charge no more than the cost of physically performing source distribution, on a medium customarily used for software interchange a complete machine-readable copy of the corresponding source code of the software given to you under any copyleft license (particularly - but not exclusively - under the GNU General Public License (GPL), the GNU Lesser General Public License (LGPL), or the GNU Affero General Public License (AGPL) , the Eclipse Public License (EPL), the Mozilla Public License (MPL) ). To receive such source code please contact[Telekom Department Name] as follows:

[Contact addresses]
``` 

<!-- ENDIF -->
<a name="components"></a> <!-- HTML Anchor for components link in Table of Contents -->

## [PROJECT_NAME] uses the following third party OSS components: 
<!-- [TODO] Replace [PROJECT_NAME] -->

### Component: apache-2.0-demo
<!-- Use for Apache 2.0 -->
<!-- [TODO] Replace apache-2.0-demo with component name -->

* Component name: apache-2.0-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: Apache-2.0 <!-- [TODO] Insert the SPDX identifier(s) -->
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

<!-- [TODO] Insert the license text once in the appendix, and place a link to the heading in the appendix here -->
License Text: [Apache-2.0](#Apache-20)  
  
<!-- IF notice file exists --> 
NOTICE file in the repository:
<!-- [TODO] Insert notice file content as found in the repository below-->
```
Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua
```
<!-- ELSE -->
<!-- Project does not use a file named NOTICE -->
<!-- ENDIF -->


### Component: mit-isc-bsd-demo
<!-- Use for MIT, ISC and BSD-type (except 0BSD) licenses -->
<!-- [TODO] Replace mit-isc-bsd-demo with component name -->

* Component name: mit-isc-bsd-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: MIT <!-- [TODO] Insert the SPDX identifier(s) -->
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

License Text:
<!-- [TODO] Insert the original license text here, including the copyright line -->
```
Copyright 1970 Carina Creator

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```


### Component: (l)gpl-demo
<!-- Use for (L)GPL licenses of any version -->
<!-- [TODO] Replace (l)gpl-demo with component name -->

* Component name: (l)gpl-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: GPL-3.0-or-later <!-- [TODO] Insert the SPDX identifier(s) -->
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

Copyright owners:
<!-- [TODO] Insert the list of copyright owners and authors, grepped out of all files in the repository source code and de-duplicated. -->
```
(c) 1970 Carina Creator
(c) 1970 Christian Coder
... 
```

<!-- [TODO] Insert the license text once in the appendix, and place a link to the heading in the appendix here -->
License Text: [GPL-3.0-or-later](#GPL-3.0-or-later)


### Component: cc-by-x.0-demo
<!-- Use for CC-BY-3.0 and CC-BY-4.0 -->
<!-- [TODO] Replace cc-by-x.0-demo with component name -->

* Component name: cc-by-x.0-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: CC-BY-3.0 <!-- [TODO] Insert the SPDX identifier(s) -->
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

<!-- [TODO] Insert the license text once in the appendix, and place a link to the heading in the appendix here -->
License Text: [CC-BY-3.0](#CC-BY-30) 
 
<!-- [TODO] IF the authors specify how they want to be credited, follow these instructions. -->

<!-- [TODO] IF the authors include any of the following information in any file (LICENSE, COPYING, README, AUTHORS, ...) in the root directory of their repository, include the original text, wrap it in three back ticks (```) for proper formatting: 
    - Copyright notices
    - Names or pseudonyms of copyright holders or authors
    - Notices referring to a disclaimer of warranty 
-->

<!-- IF you modified the licensed material -->
<!-- [TODO] List the modifications -->
<!-- ELSE -->
The licensed material has not been modified.
<!-- ENDIF -->


### Component: public-domain-demo
<!-- Use if the component is in the public domain, without a license -->
<!-- [TODO] Replace public-domain-demo with component name -->

* Component name: public-domain-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: Public Domain
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

License Text:
<!-- [TODO] Insert the original statement text that releases the component into the public domain. -->
```
This is free and unencumbered software released into the public domain.
```


### Component: multiple-licenses-choice-demo
<!-- Use if the component allows choosing between multiple licenses -->
<!-- [TODO] Replace multiple-licenses-choice-demo with component name -->

* Component name: multiple-licenses-choice-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: LGPL-2.0, MIT <!-- [TODO] List the SPDX identifiers -->
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

<!-- [TODO] Replace MIT in the line below with the SPDX identifier of one of the available licenses, we recommend to choose the most permissible license. -->
We are redistributing this component under the terms of the MIT license.

<!-- [TODO] Continue with the component template (find it above) for the chosen license  -->

### Component: other-licenses-demo
<!-- Use for all other licenses. -->
<!-- [TODO] Replaceother-licenses-demo with component name -->

* Component name: other-licenses-demo <!-- [TODO] Insert component name -->
* Component version: 1.0 <!-- [TODO] Insert the version you use -->
* License type: SPDX-Identifier <!-- [TODO] Insert the SPDX identifier(s) -->
* Public homepage: https://project.site <!-- [TODO] Insert, IF exists -->
* Public repository: https://github.com/sample/sample <!-- [TODO] Insert -->

<!-- [TODO] Insert the license text once in the appendix, and place a link to the heading in the appendix here -->
License Text: [SPDX-LICENSE-IDENTIFIER](#SPDX-LICENSE-IDENTIFIER)

## Appendix
<!-- Create a sub-chapter for each license with the instruction text:

"Insert the license text once in the appendix, and place a link to the heading in the appendix here" 

in the appendix, and make sure the link placed in the component's chapter is clickable.
-->

### Apache-2.0

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->

### GPL-3.0-or-later

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->

### CC-BY-3.0

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->

### SPDX-Identifier-XYZ

<!-- [TODO] Insert the license text here, wrap it in three back ticks (```) for proper formatting. -->
