# Introduction to cgdsclj

[![Clojars Project](https://img.shields.io/clojars/v/cgdsclj.svg)](https://clojars.org/cgdsclj)

Clojure based API for accessing the Cancer Genomics Data Server (CGDS).

**NOTE: It's an unstable version, NOT RECOMMENDED for production use.**

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)

## Introduction

cgdsclj based API for accessing the Cancer Genomics Data Server (CGDS). It's based on [cgdsr](https://github.com/cBioPortal/cgdsr). Queries the CGDS API and returns available cancer studies. Input is a CGDS object and output is an array with information regarding the different cancer studies.

## Installation

`cgdsclj` is available as a Maven artifact from Clojars.

With Leiningen/Boot:

```
[cgdsclj "0.1.0"]
```

cgdsclj supports clojure 1.8.0 and higher.

## Usage

To use cgdsclj:

```clojure
(require '[cgdsclj "0.1.0" as cgds])

# get cancer study
(cgds/list->map (cgds/cancer-studies))

# get case list
(case-lists "um_qimr_2016")

# get genetic profile
(genetic-profiles "um_qimr_2016")

# get clinical data
(clinical-data ["um_qimr_2016_all"] :case-set-id)

# get profile data
(profile-data ["EGFR"] "um_qimr_2016_mutations" ["um_qimr_2016_all"] :case-set-id)
```

## Advanced Usage

```
# You may want to change headers and base-url for each http request.

(require '[cgdsclj "0.1.0" as cgds])

(def base-url "http://www.cbioportal.org/")

(def headers
  {:content-type :json
   :connection-timeout 10000
   :accept :json})

# get cancer study
(cgds/list->map (cgds/cancer-studies base-url headers))

# get case list
(case-lists "um_qimr_2016" base-url headers)

# get genetic profile
(genetic-profiles "um_qimr_2016" base-url headers)

# get clinical data
(clinical-data ["um_qimr_2016_all"] :case-set-id base-url headers)

# get mutation data
(mutation-data "um_qimr_2016" "um_qimr_2016_all" "um_qimr_2016_mutations" ["EGFR"])

# get profile data
(profile-data ["EGFR"] ["um_qimr_2016_mutations"] ["um_qimr_2016_all"] :case-set-id base-url headers)
```

## Contact

Jingcheng Yang
yjcyxky@163.com

Your feedbacks are welcome. If you're struggling using the librairy, the best way to ask questions is to use the Github issues so that they are shared with everybody.

## License

Copyright © 2019 Choppy Team. https://github.com/orgs/go-choppy/teams/choppy-team

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
