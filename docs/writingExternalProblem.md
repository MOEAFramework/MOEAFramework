# Connecting to Problems with Standard I/O

In this example, we will write a C/C++ program for the 2-objective DTLZ2 problem, compile it into an executable, and
optimize it using the MOEA Framework.  This uses the `ExternalProblem` class, which uses standard input/output to
talk with the external program.  This interface is simple, but can be used to connect with problems defined in
practically any programming language.

## Prerequisites

To run this example, you will need:

1. A C/C++ compiler and Make.  For Windows, we recommend using [MSYS2](https://www.msys2.org/) with MinGW.  After
   installing MSYS2, open the MinGW64 terminal and run:
   ```
   pacman -S make mingw-w64-x86_64-gcc
   ```
   
2. The MOEA Framework C interface, `moeaframework.c` and `moeaframework.h`, found in the `examples/` folder.

## Example

We start with the C/C++ program.  First, we need to include the MOEA Framework C interface:

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
line of input.  Observe how the loop reads the decision variables, calls our function defined above, and writes the
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

This will produce the executable `dtlz2_stdio.exe`.  Then, we can switch over to the Java and create an
external problem referencing this executable:

<!-- java:examples/org/moeaframework/examples/external/ExternalProblemWithStdio.java [61:98] -->

```java
public static class MyDTLZ2 extends ExternalProblem {

    public MyDTLZ2() throws IOException {
        super(new Builder().withCommand("./examples/dtlz2_stdio.exe"));
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

Note in the above example that

1. We extend from the `ExternalProblem` class.
2. We provide the path to the executable in the constructor.
3. There are five methods we must implement, which describe the structure of the problem.

Finally, we can solve this problem.  Since the `ExternalProblem` spins up the program in the background, it's
important that the problem is closed after use.  As demonstrated below, we can use a try-with-resources block, allowing
Java to automatically close the problem.

<!-- java:examples/org/moeaframework/examples/external/ExternalProblemWithStdio.java [110:114] -->

```java
try (Problem problem = new MyDTLZ2()) {
    Algorithm algorithm = new NSGAII(problem);
    algorithm.run(10000);
    algorithm.getResult().display();
}
```

## Other Languages

We can use the same approach to connect to problems written in other programming languages.  Simply construct a loop
to read the decision variables from the input and write the objective (and constraint) values to the output.  As an
example, we have included a Python example at [`examples/dtlz2.py`](../examples/dtlz2.py).

## Troubleshooting

On Windows, if you see an error message about the `msys.dll` missing, make sure you use the MingGW compiler.  To
verify what version you have, open the MinGW64 terminal and run `which gcc`.  This should display
`/mingw64/bin/gcc`.

The program can hang if the number of decision variables sent to the program do not match the expected number, as it
will wait for further input.  To assist in debugging, set
`org.moeaframework.problem.external_problem_debugging = true` in `moeaframework.properties`.
