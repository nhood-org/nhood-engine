[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![CircleCI](https://circleci.com/gh/nhood-org/nhood-engine.svg?style=shield)](https://circleci.com/gh/nhood-org/nhood-engine)

# Engine

Service is part of [nhood](https://github.com/nhood-org/nhood-docs) project. 

The `nhood-engine` is a core library covering key engine functionaries. 

Project is split into two maven submodules:

- `nhood-engine-core-api` containing core engine interfaces
- `nhood-engine-matrix-api` containing matrix management interfaces

## Technology

Library is based on pure Java 8 with minimal possible amount of dependencies.

## Pre-requisites

- Java 8
- Maven

## Build

In order to build the project use the following maven command:

```bash
mvn clean install
```

## Test

In order to test the project use the following maven command:

```bash
mvn clean test
```

## CI/CD

Project is continuously integrated with `circleCi` pipeline that link to which may be found [here](https://circleci.com/gh/nhood-org/workflows/nhood-engine)

Pipeline is fairly simple:

1. Build and test project with a set of jdk: `1.8`, `9`, `10` and `11`.
2. Deploy new snapshot version to [nhood maven repository](https://github.com/nhood-org/nhood-repository/tree/mvn-repo/com/h8/nh)

Configuration of CI is implemented in `.circleci` and  `.circleci.setting.xml`.

## Versioning

In order to release version, send the following API request to circleCI:

```bash
curl -u <CIRCLE_CI_USER_TOKEN> \
    -d build_parameters[CIRCLE_JOB]=release \
    https://circleci.com/api/v1.1/project/github/nhood-org/nhood-engine/tree/master
```

## License

`nhood-data-url-svc` is released under the MIT license:
- https://opensource.org/licenses/MIT
