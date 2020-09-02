[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/maven-0.0.5-blue.svg?maxAge=2592000)](https://github.com/nhood-org/repository/packages/127632)
[![CircleCI](https://circleci.com/gh/nhood-org/nhood-engine.svg?style=shield)](https://circleci.com/gh/nhood-org/nhood-engine)

# Engine

Service is part of [nhood](https://github.com/nhood-org/nhood-docs) project.

The `nhood-engine` is a core library covering key engine functionaries.

Project is split into two maven submodules:

- `nhood-engine-core` containing core engine implementation
- `nhood-engine-core-api` containing core engine interfaces
- `nhood-engine-core-test` containing core engine abstract unit tests
- `nhood-engine-core-performance` containing core engine performance tests
- `nhood-engine-matrix` containing matrix management implementation
- `nhood-engine-matrix-api` containing matrix management interfaces
- `nhood-engine-matrix-test` containing matrix management abstract unit tests
- `nhood-engine-matrix-performance` containing core engine performance tests
- `nhood-engine-test-utils` containing test utilities

## Usage examples

There are a couple of examples implemented as ready-to-run unit tests. 
You can find those in [nhood-examples](https://github.com/nhood-org/nhood-examples) repository.

## Technology

Library is based on pure Java 8 with minimal possible amount of dependencies.

## Pre-requisites

- Java 8
- Maven

## Build, test, run

Use pre-defined Makefile tasks

## Performance Test

In order to run performance test use the following maven commands:

```bash
make run-core-performance-tests
make run-matrix-performance-tests
```

Previous performance test results may be found here:
- [nhood-engine-core-performance/performance.results](nhood-engine-core-performance/performance.results)
- [nhood-engine-matrix-performance/performance.results](nhood-engine-matrix-performance/performance.results)

## CI/CD

Project is continuously integrated with `circleCi` pipeline that link to which may be found [here](https://circleci.com/gh/nhood-org/workflows/nhood-engine)

Pipeline is fairly simple:

1. Build and test project with a set of jdk: `1.8` and `11`.
2. Deploy new snapshot version to [nhood maven repository](https://github.com/nhood-org/nhood-repository/tree/mvn-repo/com/h8/nh)

Configuration of CI is implemented in `.circleci` and  `.circleci.setting.xml`.

## Versioning

In order to release version of maven artifacts, send an API request to circleCI with the following command:

```bash
export CIRCLE_CI_USER_TOKEN=<CIRCLE_CI_USER_TOKEN>
make trigger-circle-ci-maven-release
```

## License

`nhood-engine` is released under the MIT license:
- https://opensource.org/licenses/MIT
