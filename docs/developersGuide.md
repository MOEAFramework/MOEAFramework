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

We recommend using [Eclipse](http://eclipse.org/) when working on this code.  Simply import the MOEA Framework
project using the `Open Projects from File System...` command.

When using a different IDE, you may need to install the following:

* **Java Development Kit (JDK) 17+**: Recommend using either [Eclipse Temurin](https://adoptium.net/) or
  [Azul Zulu](https://www.azul.com/downloads/?package=jdk).

* **Apache Ant**: Download from http://ant.apache.org/.

* **JUnit 4**: Run `ant -f test.xml download-junit` to download JUnit 4 into the `lib/` folder.

For example, using SDKMAN! we can install these dependencies and run tests with:

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

Apache Ant is used to compile, test, and package the code.  When using Eclipse, simply drag-and-drop `build.xml` and
`test.xml` into the Ant panel, then run any of the targets.  Individual test classes or methods can be run
by selecting `Run As > JUnit Test` from the menu.  Alternatively, we can run Ant targets from the terminal:

```
ant -f build.xml package-binary
ant -f test.xml test
```

### Optional Test Dependencies

Some tests utilize dependencies that are not redistributed with the MOEA Framework due to licensing restrictions or
other reasons.  While these tests are skipped when the dependency is missing, consider running the full test suite by
first downloading all test dependencies:

```
# Install optional Java dependencies
ant -f test.xml download-all

# Install optional Python dependencies
pip install pandas cvxopt
```

> [!TIP]
> When running tests through Eclipse, be sure to add the downloaded JAR files in the `lib/` directory to the build path.

### Maven

While we do not use Maven directly, a Maven-compatible release can be generated using the `build-maven` target:

```
ant -f build.xml build-maven
cd build
mvn test
```

### Continuous Integration

All changes pushed to the GitHub repository will trigger the `ci.yml` workflow running on GitHub Actions.  Please
verify these tests are passing, otherwise this may delay the review or approval of a PR.

### Releases

New releases are published by the `staging.yml` workflow.  The process to create a new release is:

1. Merge a PR to increment the version number in `META-INF/build.properties`.  Optionally append `-SNAPSHOT` to the
   version to publish a pre-release.
2. After CI passes, manually trigger the Staging workflow (`staging.yml`).
3. Verify the [integration tests](https://github.com/MOEAFramework/IntegrationTests) are passing with the new release.
4. Publish the Maven artifacts, GitHub release, and updated website.

Please note that it can take several hours for the new release to be available for download using Maven.

## Documentation

We publish Markdown documentation under the `docs/` folder.  To ensure code samples shown in documentation are both
up-to-date and working, we have a custom tool that syncs code snippets found in documentation with working Java code.
This works by placing a special directive, in the form of a comment, in the Markdown file immediately before the code
block:

````
<!-- :code: src=examples/Example1.java lines=33:40 -->

```java
... copied source code ...
```
````

Then, we can either validate or update the content of the code block by running:

```
ant validate-docs
ant update-docs
```

In addition to `:code:` which copies source code, the `:exec:` and `:plot:` directives can compile and run a Java
class or method, updating the Markdown file with either the output text or rendered plot.

## Errors, Exceptions, and Warnings

The following guidance should be used when displaying any kind of error or warning:

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
   `FrameworkException`.  `FrameworkException` is also used as a catch-all when rethrowing checked exceptions.

## Service Providers

The Java Service Provider Interface (SPI) is a standardized way to implement plugin-style functionality for specific
Java types.  We use the SPI to dynamically locate and construct algorithms, problems, operators, etc. at runtime.
Consequently, when adding a new implementation of these types, it's mandatory to add a corresponding provider.
See the Javadocs for the specific provider type, such as `AlgorithmFactory`, for more details.

