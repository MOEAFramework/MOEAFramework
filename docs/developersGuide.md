# Developer's Guide

## Contributing

Interested in contributing to the MOEA Framework?  All active development is handled in our GitHub repository at http://github.com/MOEAFramework/MOEAFramework.  Please follow the steps below to ensure your contribution is received
and merged in a timely manner!

### Setting up a Development Environment

We recommend using [Eclipse](http://eclipse.org/) when working with the MOEA Framework as all dependencies are included
in Eclipse.  If you choose to use a different IDE, you might need to install the following dependencies separately:

> **Java 17**, or any newer version, is required to use the MOEA Framework. Today, due to licensing concerns with
> Oracle, a number of third-party Java distributions are available.  We recommend using either
> [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/?package=jdk).
> 
> **Apache Ant** is used to compile, test, and package distributions of the MOEA Framework.  Apache Ant can be
> downloaded from http://ant.apache.org/.
> 
> **JUnit 4** for running tests.  Due to licensing differences, JUnit is not provided alongside the MOEA Framework and
> must be downloaded separately.  Visit https://github.com/junit-team/junit4/wiki/Download-and-Install and download
> `junit.jar` and `hamcrest.jar` to the `lib/` folder.

### Checklist

After making and testing your changes locally, the next step is to submit the code for review.  The checklist below
outlines this process:

- [ ] Acknowledge that your contributions will be included in the MOEA Framework and licensed under the GNU Lesser
      General Public License, version 3 or later.  This also applies to any third-party code or libraries included in
      your change.
- [ ] Add any necessary tests to validate your changes.
- [ ] Include any references or citations in the Javadoc comments.
- [ ] Include the MOEA Framework copyright header at the beginning of every file.  You may include your or your
      institution's name in the copyright statement.
- [ ] Open a [pull request](https://github.com/MOEAFramework/MOEAFramework/pulls) with your changes.
- [ ] Monitor the pull request to ensure all tests pass and any reviewer feedback is addressed.

If everything looks good, we will approve and merge the changes for you.  Thank you for contributing to this project!

## Versioning

We use [semantic versioning](https://semver.org/) following the pattern `{major}.{minor}`.  Two versions with the
same `{major}` number are expected to be backwards compatible, for example allowing one to upgrade from `4.0` to
`4.2` without difficulty.  

### Supported Java Versions

Each release of the MOEA Framework targets a specific version of Java:

* `4.x` - Java 17+
* `3.x` - Java 8+
* `2.x` - Java 6+ (some features are deprecated in Java 16+ and no longer work)

To determine if and when to update which Java version we target, we generally look at the
[support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) and favor LTS releases.
While supporting earlier versions limits our use of newer language features, the tradeoff is supporting the widest
possible audience.

## Service Providers and Configuration

This library uses Java's Service Provider Interface (SPI) to support extensibility.  See `AlgorithmProvider`,
`ProblemProvider`, and `OperatorProvider`.  To introduce a new algorithm, problem, or operator, one would
create a custom provider using one of these classes.  Then, add the new class to the appropriate file under
`META-INF/services`.

All algorithms and operators are expected to be configurable.  This means adding the appropriate setter methods
as well as using the configuration API.  The configuration API uses reflections to dynamically discover and
get/set an algorithm's configuration.  The only requirement is adding the `@Property` annotation to each
setter method.

## Writing Tests

Given our target audience is the scientific community, testing is key for assuring the correctness and accuracy of this
software.  However, given the stochastic nature of MOEAs, writing reliable unit tests has challenges.  To assist in
writing tests, we have two constants, `TestThresholds.LOW_PRECISION` and `TestThresholds.HIGH_PRECISION`,
that standardize the accuracy of tests.  Prefer using `HIGH_PRECISION` whenever possible.

Despite our best efforts, some tests will occasionally fail due to randomness.  For such tests, we encourage adding
the `@Retryable` attribute, which re-runs the test several times if it fails.  Be sure to also include
`@RunWith(CIRunner.class)` on the test class.  Tests marked with `@Flaky` fail too frequently for inclusion in
CI, but can still run manually.

## Building, Testing, and Packaging

This project uses Apache Ant to compile, test, and package the code.  If using Eclipse, drag-and-drop `build.xml`
and `test.xml` into the Ant window, then double-click on any of the targets.  For example, the `package-binary`
target will create the binary distributions.  Alternatively, from the terminal, we can run:

```
ant -f build.xml package-binary
```

Tests are powered by JUnit.  Individual tests or test classes can be evaluated from within Eclipse by right-clicking
on the test or class and selecting `Run As > JUnit Test`.  To run all tests using Ant, run the `test` target
from `test.xml`, or from the terminal run:

```
ant -f test.xml test
```

We also have a custom tool to validate code examples in our Markdown and HTML documentation.  Use the
`validate-docs` and `update-docs` targets to keep the examples in sync.

### Maven

The MOEA Framework source code is not structured for Maven, but we can produce a Maven-compatible release using the
`package-maven` target.  We can test the Maven configuration by running the `build-maven-tests` target followed
by:

```
mvn test
```
