# Contributing to the MOEA Framework

## Setting up a Development Environment

We recommend using [Eclipse](http://eclipse.org/) when working with the MOEA Framework as all dependencies come prepackaged in Eclipse.  If you choose to use a
different IDE, you might need to install the following dependencies separately:

#### Java 8+
Java 8, or any newer version, is required to use the MOEA Framework. Today, due to licensing concerns with Oracle, a number of third-party Java distributions are 
available.  We recommend using either [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/?package=jdk).

#### Apache Ant
Apache Ant is used to compile, test, and package distributions of the MOEA Framework.  Apache Ant can be downloaded from http://ant.apache.org/.

#### JUnit 4
JUnit 4 is required to run our tests.  Due to licensing differences, JUnit is not provided alongside the MOEA Framework and must be downloaded separately.  Visit
https://github.com/junit-team/junit4/wiki/Download-and-Install and download `junit.jar` and `hamcrest-core.jar` to the `lib/` folder.

## Submitting Changes

Want to get your improvements or bug fixes merged into the MOEA Framework?  Follow these steps:

1. Please be aware that any contributions will be subject to the GNU Lesser General Public License, version 3 or later.  By submitting your contribution,
   you agree to release the code under these terms.
   
2. Implement your feature or bug fix.  Testing is important.  Consider if any new tests or updates to existing tests are required.
   
3. Open a pull request with your changes.  This will trigger GitHub Actions workflows that run all tests.  Please verify all tests pass.

4. Wait for and respond to any feedback provided on your pull request.

5. Once all feedback is addressed and the pull request approved, we will merge the changes.  Congrats on becoming a contributor!
