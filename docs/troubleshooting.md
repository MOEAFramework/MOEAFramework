# Troubleshooting

Below lists common errors, warnings, and other troubleshooting advice.  If you can not resolve the problem using this
guide, please [report the issue](#reporting-an-issue).

## Setup / Installation

### Unsupported Version (`UnsupportedClassVersionError`)

An error similar to the one below indicates you are using a version of Java that is not compatible with the compiled
binaries.

```
Exception in thread "main" java.lang.UnsupportedClassVersionError: org/moeaframework/...
```

The MOEA Framework requires Java 17 or newer.  To check which version of Java you have installed, run `java -version`
from the command line.  Please install a compatible version of Java or, if already installed, ensure your system PATH
points to the correct version.

### Modules

Java 9 introduced "modules", a mechanism for configuring dependencies and encapsulating a project.  We do not use this
feature, but some development tools may enable modules by default when importing a project.  In Eclipse, this manifests
as the build error `<className> cannot be resolved to a type`, typically also showing a :x: on all Java classes.

To confirm this is the case, look for the file `src/module-info.java`:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/78d76409-b70a-4fa3-8a57-cc4df16df4c2)

To fix, we recommend starting from scratch by deleting the broken project including the source directory.  Download
and extract the MOEA Framework again, but when importing make sure you uncheck or disable modules.  For example, in
Eclipse, you should uncheck the following:

![image](https://github.com/MOEAFramework/MOEAFramework/assets/2496211/cab2283a-0dd4-4574-8720-d15f4c2657ab)

## Errors

### Class Definition Not Found (`NoClassDefFoundError`)

This error, which typically also displays the missing class name, indicates the referenced class could not be found
on the Java classpath.  Classes are typically bundled into "JAR" files, which must be included on the classpath when
launching the Java application.

First, identify where the missing class is located, either bundled in a JAR file or as an individual `.class` file.
If found in a JAR, make sure the JAR is referenced on the classpath:

```bash
java -classpath "$CLASSPATH:/path/to/library.jar" ...
```

For an individual `.class` file, include the folder containing this file on the classpath:

```bash
java -classpath "$CLASSPATH:/path/to/library" ...
```

Also make sure you use the correct separator for your operating system.  Linux and Mac OS users must use a colon (`:`)
while Windows / Cygwin users must use a semi-colon (`;`).

### No Provider for Algorithm / Problem / Operator

This error indicates the requested algorithm, problem, or operator could not be found.

1. Check the full exception stack trace for any clues.  Typically, the underlying cause will appear as a nested
   exception.  For instance, a common cause is trying to use incompatible types (e.g., binary variables with an
   algorithm that only support real-valued variables).

2. Verify the name of the [algorithm](listOfAlgorithms.md), [problem](listOfProblems.md), or
   [operator](listOfOperators.md) is correct.
   
3. If the algorithm, problem, or operator is not one built into the MOEA Framework, verify that the `.jar`  or
   `.class` defining the object is listed on the Java classpath.
   
### Normalization

When using normalized performance indicators, such as Hypervolume, the populations are normalized according to the
bounds of the reference set.  However, if the reference set contains fewer than two solutions or the two solutions
are nearly identical, we are unable to compute bounds for normalization.  Typically, this results in one of the
following errors:

1. `requires at least two solutions`, or
2. `objective with empty range`.  

If this occurs, you either need to provide a reference set with two or more **different** solutions, provide explicit
lower and upper bounds for normalization, or disable normalization.  See the `Indicators` class for more details.

## Reporting an Issue

If encountering an issue not covered by this troubleshooting guide, first search the
[issues](https://github.com/MOEAFramework/MOEAFramework/issues) on our GitHub page to see if the problem is already
addressed.  If not, please open a new issue.  When doing so, we ask you provide the following information:

1. The version and distribution of Java being used (`java -version`)
2. The operating system name, version, and any specifics of the environment
3. A copy of any error or warning messages
4. If possible, example code that reproduces the error or warning messages
