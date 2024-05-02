# Natively-Compiled Functions

This example demonstrates how to construct native problems, where we write the function in a language like C, C++,
or Fortran, using our tool for generating problem templates.  This process involves four steps:

1. Using the `BuildProblem` tool to generate the template for a given programming language (e.g., C)
2. Updating the generated template files to define our problem
3. Compiling and testing the problem
4. Linking the compiled code back with the MOEA Framework

While there is a small amount of additional overhead, writing complex functions in a native language will typically
outperform their Java equivalent.

## Prerequisites

To run this example, you will need a C/C++ compiler and Make.  For Windows, we recommend using
[MSYS2](https://www.msys2.org/) with MinGW.  After installing MSYS2, open the MinGW64 terminal and run:

```bash
pacman -S make mingw-w64-x86_64-gcc
```

## Supported Languages

The following table lists the programming languages supported by the `BuildProblem` tool.  Specify the language
using the `--language` option when running this tool.

Language | Default Compiler | Notes
-------- | ---------------- | -----
C        | `gcc`            | C function
CPP      | `g++`            | C++ function.  Output includes `.c` and `.h` file
Fortran  | `gfortran`       | Fortran90 function
Java     | `javac`          | Java problem definition (see [Writing Problems in Java](writingJavaProblem.md))
Python   |                  | Python program using Standard I/O (see [Writing External Problems](writingExternalProblem.md))
External |                  | External C/C++ problem using Standard I/O (see [Writing External Problems](writingExternalProblem.md))
Matlab   |                  | Uses `MatlabEngine` to call Matlab function (**experimental**)

## Example

For this example, we will implement the Schaffer problem, given as $f(x) = (x^2, (x-2)^2)$, in the C programming
language.  This problem has a single decision variable, $-10 \leq x \leq 10$, and two objectives.

### Step 1 - Generating the Problem Template

The `BuildProblem` tool generates templates for writing problems in other programming languages.  This includes
both the file for defining the function in the chosen language, several Java classes used to interface with the 
MOEA Framework, and a Makefile for compiling and testing the code.

We run this tool as follows:

```bash
java -classpath "lib/*" org.moeaframework.builder.BuildProblem --problemName TestProblem --language c \
	--numberOfVariables 1 --numberOfObjectives 2 --lowerBound -10.0 --upperBound 10.0
```

By default, the generated files are created under the `native/` folder, such as `native/TestProblem` in this
example.  The exact contents will vary for the different programming languages, but should appear similar to:

```
META-INF/
Example.java
Makefile
TestProblem.c
TestProblem.java
TestProblemProvider.java
```

### Step 2 - Writing the Function

For this example, which is being written in C, the function will be defined in `TestProblem.c`.  Other languages
will have a similar file, with a different extension, for defining the function.  To begin, open `TestProblem.c`
in an editor of your choice.  The contents will look similar to:

```c
int nvars = 1;
int nobjs = 2;
int nconstrs = 0;

void evaluate(double* vars, double* objs, double* constrs) {
	// TODO: Fill in with your function definition
}
```

Note that the generated template defines a function taking three arguments: the decision variables `vars`, the
computed objective values `objs`, and the computed constraint values `constrs`.  `objs` and `constrs` are outputs.
We can fill in the body of the function as follows to implement the Schaffer problem:

```c
void evaluate(double* vars, double* objs, double* constrs) {
	double x = vars[0];
	
	objs[0] = x * x;
	objs[1] = (x - 2.0) * (x - 2.0);
}
```

Since this function has a single decision variable, we are fine to use the lower and upper bounds we provided when
generating the template files.  However, if you need to supply different bounds, edit `TestProblem.java` and make
the required changes.

### Step 3 - Compiling and Testing

After saving these changes, we can use the provided `Makefile` to compile the code by running:

```bash
make
```

This `Makefile` produces two files:

1. The shared library, such as `TestProblem.dll` on Windows or `libTestProblem.so` on Linux; and
2. A Java JAR file, such as `TestProblem.jar`, that contains everything required to integrate the problem with
   the MOEA Framework.  This JAR is "self-contained", meaning it contains the shared library.  Thus, you only need to
   copy the JAR file.
   
The generated templates also provide an example, `Example.java`, that solves the problem we just created using
NSGA-II.  We can run this example with:

```bash
make run
```

If everything is configured correctly, you should see the Pareto approximation set displayed to the output:

```
Var1     Obj1     Obj2
-------- -------- --------
1.999997 3.999986 0.000000
0.000000 0.000000 3.999999
0.285445 0.081479 2.939697
1.551488 2.407114 0.201163
1.491630 2.224960 0.258440
0.871998 0.760380 1.272389
...
```

### Step 4 - Integrating Problem with the MOEA Framework

The `Makefile` also produces a JAR file, such as `TestProblem.jar`, that we can copy into the MOEA Framework's
`lib/` folder so it is included on the classpath.  If using an IDE like Eclipse, you must also add this JAR to the
build path.  Once set up, we can reference this new problem as we would any other:

```java
TestProblem problem = new TestProblem();

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

## Limitations and Troubleshooting

### Decision Variable Types

The templates are only designed to support real-valued decision variables.  That being said, it's certainly possible
to use different types, you just need to update both the native code and the Java code to pass the correct types.  See
[JNA Java to Native type mappings](https://github.com/java-native-access/jna/blob/master/www/Mappings.md) for more
details.

### Required Dependencies

The `Makefile` bundles the shared library in the Java JAR.  If your entire program is contained within the shared
library, then the JAR file contains everything required to use the problem.  However, any other dependencies like input
files, executables, and referenced libraries will not be included!  Consequently, these dependencies must be installed
separately on the system and, if necessary, included in the `PATH` or `LD_LIBRARY_PATH` environment variables so
they can be discovered.

### Cross-Platform Support

The `Makefile` will only compile the native library for the host system.  The compiled library is placed in
a directory identifying the system architecture, such as `win32-x86-64`.  A cross-platform version of the JAR can
be created by compiling the native library on different systems and combining these platform-specific directories into
a single JAR file.

### Issues Loading the Native Library

If you experience errors indicating the library could not be found or loaded, try adding `-Djna.debug_load=true` to
the `java` command to display debugging logs.

Errors can also occur if targeting the wrong system architecture or using a different calling convention.  The provided
`Makefile` should handle this correctly, but errors can occur if the library was compiled on a 64-bit architecture but
used with a 32-bit version of Java, for example.  One workaround is adding `-m32` to the compiler flags to create a
32-bit version of the library.
