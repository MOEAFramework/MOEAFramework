# Developer's Guide

## Contributing

Have a bug fix or new feature to contribute to the MOEA Framework?  Please submit the change as a pull request (PR) to
our GitHub repository at http://github.com/MOEAFramework/MOEAFramework.  Before submitting changes, please note:

1. All contributions will be released under the [GNU Lesser General Public License](../META-INF/LGPL-LICENSE).  By
   submitting a PR, you agree to these terms and conditions.
   
2. To properly attribute your contributions, include the copyright and license header at the top of each source file
   (see any `.java` file for an example).  You may optionally include your name or, if applicable, your institution's
   name in the header.

3. Add any necessary tests and documentation.  Since this project is academic in nature, include any references or
   citations in the Javadoc comments.
   
4. After creating a PR, please verify all CI tests pass.  We may leave comments on the PR that require changes.  If the
   PR is accepted, we will handle merging and releasing the change.

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
ant -f test.xml download-junit
ant -f test.xml test
```

## Versions

We use [semantic versioning](https://semver.org/) following the pattern `{major}.{minor}`.  Two versions with the
same `{major}` number are expected to be backwards compatible, including both the API and data file formats.  However,
we may at times make breaking changes to address a bug or when developing a new feature spanning several releases.
Such breaking changes will be identified in the release notes.

Each version also targets a specific version of Java:

* `5.x` - Java 17+
* `4.x` - Java 17+
* `3.x` - Java 8+
* `2.x` - Java 6+ (some features are deprecated in Java 16+ and no longer work)

To determine if and when to update which Java version we target, we generally look at the
[support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) and favor LTS releases.

## Building, Testing, and Packaging

Apache Ant is used to compile, test, and package the code.  When using Eclipse, drag-and-drop `build.xml` and `test.xml`
into the Ant window, then double-click on any of the targets.  For example, the `package-binary` target will create the
binary distributions.  Alternatively, from the terminal, we can run:

```
ant -f build.xml package-binary
```

Tests are powered by JUnit 4.  Individual tests or test classes can be evaluated within Eclipse by right-clicking on
the test or class and selecting `Run As > JUnit Test`.  To run all tests using Ant, run the `test` target from `test.xml`,
or from the terminal run:

```
ant -f test.xml test
```

We strongly recommend installing all test dependencies beforehand as some tests are skipped if the dependency is
missing.  Run the following Ant target to download all test dependencies:

```
ant -f test.xml download-all
```

### Maven

The MOEA Framework source code is not structured for Maven, but we can produce a Maven-compatible release using the
`build-maven` target.  Below demonstrates running all tests through Maven:

```
ant -f build.xml build-maven
cd build
mvn test
```

### Continuous Integration

When code changes are pushed to the GitHub repository, our CI tests are automatically run.  These are powered by the
`ci.yml` workflow, and run all tests against the supported versions of Java, Maven, etc.

### Releases

New releases are published by the `staging.yml` workflow file running on GitHub Actions.  The process to create a new
release is:

1. Merge a PR to increment the version number in `META-INF/build.properties`.  Append `-SNAPSHOT` to the version to create
   a snapshot release.
2. After CI passes, manually trigger the Staging workflow (`staging.yml`).
3. Verify the [integration tests](https://github.com/MOEAFramework/IntegrationTests) are passing with the new release.
4. Publish the Maven artifacts, GitHub release, and Website update.

Please note that it can take several hours for the new release to be available for download using Maven.

## Documentation

We publish Markdown documentation under the `docs/` folder along with generated Javadocs from the comments in the
source code.  To ensure code samples shown in documentation is consistent, we have a custom tool that syncs code
snippets in documentation with working Java code.  This works by placing a special comment immediately before a code
block that identifys the language, source file, and section.  For example, this will extract lines 33 to 40 from
`Example1.java` with `<!-- :code: src=examples/Example1.java lines=33:40 -->`.

Rather than using line numbers, we can also identify blocks of code using an identifier.  In the Java code, surround
the code with these comments:

```
// begin-example: foo
...
// end-example: foo
```

then reference this block by its identifier in the Markdown docs with `<!-- :code: src=examples/Example1.java id=foo -->`.
Then, we can either validate the docs, alerting if any changes are detected, or update the docs to match the code
samples:

```
ant validate-docs
ant update-docs
```

## Errors, Exceptions, and Warnings

The following guidance should be used when emitting any kind of error or warning:

1. All errors and warnings should be written to the standard error stream (`System.err`).  Informational messages may
   also be written to standard error to keep these messages separate from output.
2. Prefix the error or warning message with `ERROR:` or `WARNING:`.  Do not prefix exception messages.
3. The first word should be capitalized (unless referencing a variable, parameter, etc. that is not capitalized).
4. Surround any displayed values or inputs in single quotes, especially with strings that may contain whitespace.
5. For invalid inputs, display both the expected format alongside the invalid input.

For exceptions:

1. Throw the most appropriate exception type for the problem, preferring existing Java exception types.  For example,
   `IllegalArgumentException` for invalid inputs, `IOException` for any errors related to I/O, files, parsing, etc.
2. If no existing exception types are appropriate to represent an error, create a new type extending from
   `FrameworkException`.

## Service Providers

The Java Service Provider Interface (SPI) is a standardized way to implement plugin-style functionality for specific
Java types.  We use the SPI to dynamically locate and construct algorithms, problems, operators, etc. at runtime.
Consequently, when adding a new implementation of these types, it's mandatory to add a corresponding provider.
See the Javadocs for the specific provider type, such as `AlgorithmFactory`, for more details.

