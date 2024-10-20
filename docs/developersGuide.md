# Developer's Guide

Interested in contributing to the MOEA Framework?  All active development is handled in our GitHub repository at
http://github.com/MOEAFramework/MOEAFramework.  Please follow the steps below to ensure your contribution is received
and merged in a timely manner!

## Checklist

To make the process of contributing changes as efficient as possible, please follow this checklist:

- [ ] Acknowledge that your contributions will be included in the MOEA Framework and licensed under the GNU Lesser
      General Public License.  See `COPYING` for details.
- [ ] Add any necessary tests to validate your changes.
- [ ] Include any references or citations in the Javadoc comments.
- [ ] Include the MOEA Framework copyright header at the beginning of every file.  You may include your name and/or your
      institution's name in the copyright statement.
- [ ] Open a [pull request](https://github.com/MOEAFramework/MOEAFramework/pulls) with your changes.
- [ ] Monitor the pull request to ensure all tests pass and any reviewer feedback is addressed.

If everything looks good, we will approve and merge the changes for you.  Thank you for contributing to this project!

## Setting up a Development Environment

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
> must be downloaded separately.  Run `ant -f test.xml download-junit` to install JUnit.

### Codespaces

If using GitHub Codespaces (or other compatible system), the following steps can be used to set up the environment:

```
sdk install java 17-zulu
sdk install ant
ant package-binary

# run example
java -classpath "lib/*:dist/*" examples/Example1.java

# run tests
ant -f test.xml install-junit
ant -f test.xml test
```

## Versions

We use [semantic versioning](https://semver.org/) following the pattern `{major}.{minor}`.  Two versions with the
same `{major}` number are expected to be backwards compatible, for example allowing one to upgrade from `4.0` to
`4.2` without difficulty.  

### Supported Java Versions

Each release of the MOEA Framework targets a specific version of Java:

* `5.x` - Java 17+
* `4.x` - Java 17+
* `3.x` - Java 8+
* `2.x` - Java 6+ (some features are deprecated in Java 16+ and no longer work)

To determine if and when to update which Java version we target, we generally look at the
[support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) and favor LTS releases.
While supporting earlier versions limits our use of newer language features, the tradeoff is supporting the widest
possible audience.

## Documentation

In addition to Javadoc comments included with source code, we also provide Markdown documentation under the `docs/`
folder.  When adding example code to the documentation, we strongly recommend using our custom tool to sync examples
with source code.  This tool looks for special comments embedded in the Markdown that identifies the language,
source file, and section of code:

````
<!-- java:examples/Example1.java [33:40] -->

```java
...
```
````

From this, we can then either validate or update the documentation:

```
ant validate-docs
ant update-docs
```


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

We strongly recommend installing all test dependencies by running:

```
ant -f test.xml download-all
```

as some tests are skipped if the dependency is not available.

### Maven

The MOEA Framework source code is not structured for Maven, but we can produce a Maven-compatible release using the
`build-maven` target.  We can run tests with:

```
ant -f build.xml build-maven
cd build
mvn test
```

### Continuous Integration

When code changes are pushed to the GitHub repository, our CI tests are automatically run.  These are powered by the
`ci.yml` workflow, and run all tests against the supported versions of Java, Maven, etc.

### Snapshots

Snapshots are unofficial, development releases of a project primarily used for testing purposes.  Some project call
these "nightlies" or "nightly builds".  While we do not publish snapshots on a regular cadence and prefer operating
from the `master` branch, please open an issue on GitHub if you require one.

### Releases

New releases are published by the `staging.yml` workflow file running on GitHub Actions.  This workflow stages the
artifacts to GitHub Releases and Maven and triggers our [integration tests](https://github.com/MOEAFramework/IntegrationTests).
After validating the release, we then manually release the new version.

Please note that it can take several hours for the new release to be available for download using Maven.
