# How to Contribute

## Setting up a Development Environment

We recommend using [Eclipse](http://eclipse.org/) when working with the MOEA Framework as all dependencies are included in Eclipse.  If you choose to use a
different IDE, you might need to install the following dependencies separately:

> **Java 8**, or any newer version, is required to use the MOEA Framework. Today, due to licensing concerns with Oracle, a number of third-party Java 
> distributions are available.  We recommend using either [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/?package=jdk).
> 
> **Apache Ant** is used to compile, test, and package distributions of the MOEA Framework.  Apache Ant can be downloaded from http://ant.apache.org/.
> 
> **JUnit 4** for running tests.  Due to licensing differences, JUnit is not provided alongside the MOEA Framework and must be downloaded separately.  Visit
> https://github.com/junit-team/junit4/wiki/Download-and-Install and download `junit.jar` and `hamcrest-core.jar` to the `lib/` folder.

## Testing Changes

Tests can be easly triggered within Eclipse by right-clicking on the `test/` folder or specific test classes and choosing `Run As > JUnit Test`.  You can
also run the entire test suite from the command line with `ant -f test.xml`.

## Submitting Changes

Want to get your improvements or bug fixes merged into the MOEA Framework?  Follow these steps:

1. Please be aware that any contributions will be subject to the GNU Lesser General Public License, version 3 or later.  By submitting your contribution,
   you agree to release the code under these terms.
   
2. Implement your feature or bug fix.  Please consider what tests are needed to validate your changes.
   
3. Open a [pull request](https://github.com/MOEAFramework/MOEAFramework/pulls) with your changes.

4. Monitor the pull request to ensure that all tests pass and any reviewer feedback is addressed.

5. If everything looks good, we will approve and merge the changes.  Thank you for contributing to this project!
