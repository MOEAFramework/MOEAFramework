# Connecting to External Problems

## Prerequisites

In order to compile these examples, you will need a C/C++ compiler and Make.  These tools should be available on
most Linux and Mac platforms.  For Windows, we recommend https://www.msys2.org/.  After installing MSYS2, open the
MSYS2 MinGW64 terminal and run:

```
pacman -S make mingw-w64-x86_64-gcc
```

## C/C++ Example

To assist in building C/C++ programs that can talk with the MOEA Framework, we have provided the `moeaframework.c`
and `moeaframework.h` files, which can be found in the `examples/` folder.  To use this library, first include
the header:

<!-- c:examples/dtlz2.c [20:20] -->

```c
#include "moeaframework.h"
```

Next, we define a function for our multiobjective problem.  Here, we are defining the 2-objective DTLZ2 test problem:

<!-- c:examples/dtlz2.c [24:51] -->

```c
int nvars = 11;
int nobjs = 2;

void evaluate(double* vars, double* objs) {
    int i;
    int j;
    int k = nvars - nobjs + 1;
    double g = 0.0;

    for (i=nvars-k; i<nvars; i++) {
        g += pow(vars[i] - 0.5, 2.0);
    }

    for (i=0; i<nobjs; i++) {
        objs[i] = 1.0 + g;

        for (j=0; j<nobjs-i-1; j++) {
            objs[i] *= cos(0.5*PI*vars[j]);
        }

        if (i != 0) {
            objs[i] *= sin(0.5*PI*vars[nobjs-i-1]);
        }
    }
}
```

Lastly, we create the main method to initialize the connection with the MOEA Framework and begin a loop processing each
line of input.  Observe how the loop read the decision variables, calls our function defined above, and writes the
objectives to the output.  The second argument to `MOEA_Write` is `NULL` since there are no constraints.


<!-- c:examples/dtlz2.c [57-76] -->

```c
int main(int argc, char* argv[]) {
    double vars[nvars];
    double objs[nobjs];

#ifdef USE_SOCKET
    MOEA_Init_socket(nobjs, 0, NULL);
#else
    MOEA_Init(nobjs, 0);
#endif

    while (MOEA_Next_solution() == MOEA_SUCCESS) {
        MOEA_Read_doubles(nvars, vars);
        evaluate(vars, objs);
        MOEA_Write(objs, NULL);
    }

    MOEA_Terminate();

    return EXIT_SUCCESS;
}
```

See `examples/dtlz2.c` for the full code sample.  Once this file is written, we can compile using:

<!-- bash:examples/Makefile [15:15] -->

```bash
gcc -o dtlz2_stdio.exe dtlz2.c moeaframework.c -lm
```

This will produce the executable `dtlz2_stdio.exe`.  Then, we can switch over to the MOEA Framework and create an
external problem referencing this executable:

<!-- java:examples/org/moeaframework/examples/external/ExternalProblemWithStdio.java [61:98] -->

```java
public static class MyDTLZ2 extends ExternalProblem {

    public MyDTLZ2() throws IOException {
        super("./examples/dtlz2_stdio.exe");
    }

    @Override
    public String getName() {
        return "DTLZ2";
    }

    @Override
    public int getNumberOfVariables() {
        return 11;
    }

    @Override
    public int getNumberOfObjectives() {
        return 2;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            solution.setVariable(i, new RealVariable(0.0, 1.0));
        }

        return solution;
    }

}
```

Note we create an instance of the `ExternalProblem` class, provide the path to this executable, and specify the
structure of the problem.  We can now use this problem like any other within the MOEA Framework.

## Other Languages

We can use the same approach to connect to problems written in practically any programming language.  Simply read the
decision variables from the input, and write the objective (and constraint) values to the output.  As an example, we
have also provided a Python example at `examples/dtlz2.py`.

One note: always flush the output after writing.  This ensures the output is written immediately instead of being
buffered by the OS.

## Troubleshooting

On Windows, if you see an error message about the `msys.dll` missing, make sure you use the MingGW compiler.  Be sure
to open the MinGW64 shell.  To verify the correct version is installed, run `which gcc`.  This should display
`/mingw64/bin/gcc`.

For extra debugging output, set `org.moeaframework.problem.external_problem_debugging = true` in
`moeaframework.properties`.
