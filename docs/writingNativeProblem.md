# Natively-Compiled Functions

This example demonstrates how to construct native problems, where we write the function in a language like C, C++,
or Fortran.  The code is then compiled into a shared library (`.dll` or `.so`) or an executable (`.exe`), which is
then called from the MOEA Framework.  While there is a small amount of additional overhead, writing complex functions
in a native language will typically outperform their Java equivalent.

## Prerequisites

To run this example, you will need a C/C++ compiler and Make.  For Windows, we recommend using
[MSYS2](https://www.msys2.org/) with MinGW.  After installing MSYS2, open the MinGW64 terminal and run:

```bash
pacman -S make mingw-w64-x86_64-gcc
```

## Generating the Problem Template

We provide a tool, `BuildProblem`, that creates a folder with the template for writing a native problem.  This
tool supports a variety of programming languages, including `C`, `C++`, `Fortran`, and `Java`.  As an example,
here we create `TestProblem` with a single real-valued decision variable and two objectives.  The lower and uppper
bounds of the variables is also specified:

```bash
java -classpath "lib/*" org.moeaframework.builder.BuildProblem --problemName TestProblem --language c \
	--numberOfVariables 1 --numberOfObjectives 2 --lowerBound -10.0 --upperBound 10.0
```

The generated files will appear under the `native/` folder, such as `native/TestProblem` in this example.  If we
open this folder, we will see the following files:

1. `TestProblem.c`, which contains the function used to implement the problem;
2. `TestProblem.java`, which is the MOEA Framework problem definition that calls the native function; this file also
   specifies the structure of the problem, including the bounds of each decision variable;
3. `Makefile`, which is used to compile and test the function using the tool `make`; and
4. `Example.java`, which is a simple example solving your new problem using NSGA-II

The folder also will contain a problem provider, such as `TestProblemProvider.java`, and the `META-INF/`folder.
These files are responsible for registering and integrating the problem with the MOEA Framework.  *Typically, you will
not need to modify these files!*

## Writing the Function

Open the file `TestProblem.c` under `native/TestProblem` using an editor of your choice.  The contents of this
file will appear similar to:

```c
int nvars = 1;
int nobjs = 2;
int nconstrs = 0;

void evaluate(double* vars, double* objs, double* constrs) {
	// TODO: Fill in with your function definition
}
```

For the purposes of this example, we will implement the Schaffer problem which is defined as $f(x) = (x^2, (x-2)^2)$.

```c
void evaluate(double* vars, double* objs, double* constrs) {
	double x = vars[0];
	
	objs[0] = x * x;
	objs[1] = (x - 2.0) * (x - 2.0);
}
```

We already specified the lower and upper bounds of the decision variables when calling `BuildProblem`.  However,
if you do need more control over the bounds, edit `TestProblem.java`.

## Compiling and Testing

After saving these changes, we can use the provided Makefile to compile the code by running:

```bash
make
```

Note this creates a shared library (a `.dll` on Windows or a `.so` on Linux) along with a `.jar` file.  Furthermore,
we can test the code using the provided example in `Example.java` by running:

```bash
make run
```

If everything is configured correctly, you should see the Pareto front displayed.

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

## Integrating Problem with the MOEA Framework

The build script also generates a Java `.jar` file containing all the required files to integrate the problem with
the MOEA Framework.  Simply copy the `.jar` file and place it into the `lib/` folder.  If using an IDE like Eclipse, you
will also need to add this `.jar` to the build path.

At this point, the problem is now discoverable by the MOEA Framework and can be used as follows:

```java
TestProblem problem = new TestProblem();

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

## Supported Languages

The following table lists the available languages:

Language | Default Compiler | Notes
-------- | ---------------- | -----
C        | `gcc`            | C function
CPP      | `g++`            | C++ function.  Output includes `.c` and `.h` file
Fortran  | `gfortran`      | Fortran90 function
Java     | `javac`         | Java problem definition (see [Writing Java Problem](writingJavaProblem.md))
Python   |                  | Python program using Standard I/O (see [Writing External Problems](writingExternalProblem.md))
External |                  | External C/C++ problem using Standard I/O (see [Writing External Problems](writingExternalProblem.md))

## Cross-Platform Support

The provided `Makefile` will only compile the native library for the host system.  The compiled library is placed in
a directory identifying the system architecture, such as `win32-x86-64`.  A cross-platform version of the JAR can
be created by compiling the native library on different systems and combining these platform-specific directories into
a single JAR file.

If you experience issues locating or loading the shared library, add `-Djna.debug_load=true` to the `java`
command do print out debugging logs.
