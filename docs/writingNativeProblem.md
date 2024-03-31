# Natively-Compiled Functions

In this example, we will create a natively-compiled function in C, compile it, and connect to it directly using the
Java Native Access (JNA) library.  This example also demonstrates our tool, `BuildProblem`, that generates the
scaffolding for the problem.  This tool supports a number of native languages, like `C`, `C++`, and `Fortran`.

## Prerequisites

To run this example, you will need a C/C++ compiler and Make.  For Windows, we recommend using
[MSYS2](https://www.msys2.org/) with MinGW.  After installing MSYS2, open the MinGW64 terminal and run:

```bash
pacman -S make mingw-w64-x86_64-gcc
```

## Generating the Problem Scaffolding

Running the following program will generate the scaffolding for the problem.  This includes the C file to implement
the function, the Java files that integrate with the MOEA Framework, and a Makefile to compile everything.

```bash
java -classpath "lib/*" org.moeaframework.builder.BuildProblem --problemName TestProblem --language c \
	--numberOfVariables 1 --numberOfObjectives 2 --lowerBound -10.0 --upperBound 10.0
```

By default, the generated files will appear under the `native/` folder, such as `native/TestProblem` in this
example.

## Updating the C Code

Navigate to the `native/TestProblem` folder and open `TestProblem.c` in an editor of your choice.  The contents
of this file will appear similar to:

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
int nvars = 1;
int nobjs = 2;
int nconstrs = 0;

void evaluate(double* vars, double* objs, double* constrs) {
	objs[0] = vars[0] * vars[0];
	objs[1] = (vars[0] - 2.0) * (vars[0] - 2.0);
}
```

We have already configured the lower and upper bounds of the decision variables to be `[-10, 10]`.  For more control
over these bounds, you can edit `TestProblem.java`.

## Compiling and Testing

After saving the changes, we can use the provided Makefile to compile the example by running:

```bash
make
```

You will note this creates a shared library (a `.dll` on Windows or a `.so` on Linux) along with a `.jar` file.
We can test this example by running:

```bash
make run
```

If everything is configured correctly, you should see the Pareto front displayed.

## Using the JAR

Now that we tested the example, we can incorporate this problem into your MOEA Framework installation by copying the
`.jar` file into the `lib/` directory.  If using Eclipse, you will also need to add this JAR to the build path.
