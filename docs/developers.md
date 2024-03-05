# Developer's Guide

## Contributing

Interested in contributing to the MOEA Framework?  All active development is handled in our GitHub repository at http://github.com/MOEAFramework/MOEAFramework.  Please follow the steps below to ensure your contribution is received
and merged in a timely manner!

### Setting up a Development Environment

We recommend using [Eclipse](http://eclipse.org/) when working with the MOEA Framework as all dependencies are included
in Eclipse.  If you choose to use a different IDE, you might need to install the following dependencies separately:

> **Java 8**, or any newer version, is required to use the MOEA Framework. Today, due to licensing concerns with Oracle,
> a number of third-party Java distributions are available.  We recommend using either
> [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/?package=jdk).
> 
> **Apache Ant** is used to compile, test, and package distributions of the MOEA Framework.  Apache Ant can be
> downloaded from http://ant.apache.org/.
> 
> **JUnit 4** for running tests.  Due to licensing differences, JUnit is not provided alongside the MOEA Framework and
> must be downloaded separately.  Visit https://github.com/junit-team/junit4/wiki/Download-and-Install and download
> `junit.jar` and `hamcrest-core.jar` to the `lib/` folder.

### Checklist

After making and testing your changes locally, the next step is to submit the code for review.  The checklist below
outlines this process:

- [ ] Acknowledge that your contributions will included in the MOEA Framework and licensed under the GNU Lesser General
      Public License, version 3 or later.  This also applies to any third-party code or libraries included in your
      change.
- [ ] Add any necessary tests to validate your changes.
- [ ] Include any references or citations in the Javadoc comments.
- [ ] Include the MOEA Framework copyright header at the beginning of every file.  You may include your or your
      institution's name in the copyright statement.
- [ ] Open a [pull request](https://github.com/MOEAFramework/MOEAFramework/pulls) with your changes.
- [ ] Monitor the pull request to ensure all tests pass and any reviewer feedback is addressed.

If everything looks good, we will approve and merge the changes for you.  Thank you for contributing to this project!

## Versioning

We use [semantic versioning](https://semver.org/) following the pattern `{major}.{minor}`.  Two versions with the
same `{major}` number are expected to be backwards compatible, for example allowing one to upgrade from `3.0` to
`3.2` without difficulty.  

### Preview Features

In some instances, especially when working directly from the Git default branch, new code is being actively developed.
These packages and classes should include the `@preview` tag to indicate they are subject to change.

### Supported Java Versions

Each release of the MOEA Framework targets a specific version of Java:

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
as well as using the configuration API.  The configuration API allows users to get or set the configuration
dynamically.  Simply annotate the setter method with the `@Property` tag.  
