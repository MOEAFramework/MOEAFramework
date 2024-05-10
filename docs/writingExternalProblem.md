# Calling Executable with Standard I/O or Sockets

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
    MOEA_Init_socket(nobjs, 0, MOEA_DEFAULT_PORT);
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

```bash
gcc -o dtlz2_stdio.exe dtlz2.c moeaframework.c -lm
```

This will produce the executable `dtlz2_stdio.exe`.  Then, we can switch over to the Java and create an
external problem referencing this executable:

<!-- java:examples/org/moeaframework/examples/external/ExternalProblemWithStdio.java [61:99] -->

```java
public static class MyDTLZ2 extends ExternalProblem {

    public MyDTLZ2() throws IOException {
        super(new ExternalProblem.Builder()
                .withCommand("./examples/dtlz2_stdio.exe"));
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

<!-- java:examples/org/moeaframework/examples/external/ExternalProblemWithStdio.java [111:115] -->

```java
try (Problem problem = new MyDTLZ2()) {
    Algorithm algorithm = new NSGAII(problem);
    algorithm.run(10000);
    algorithm.getResult().display();
}
```

## Supported Decision Variables

This external problem interface currently supports real-valued, integer, binary, permutation, and subset variables.
The `MOEA_Read_<type>` calls must be made in the same order of the decision variables.  The sections below detail
each type:

### Real-Valued

Real-valued decision variables can either be read individually or as an array:

```c
double x, y;
MOEA_Read_double(&x);
MOEA_Read_double(&y);

double vars[10];
MOEA_Read_doubles(10, vars);
```

### Integers

Similar to real-valued, integer values can be read individually or as an array:

```c
int x, y;
MOEA_Read_int(&x);
MOEA_Read_int(&y);

int vars[10];
MOEA_Read_ints(10, vars);
```

### Binary

Binary strings are parsed into an integer array containing `0` and `1`.  Here we read a binary string of length `20`:

```c
int binary[20];
MOEA_Read_binary(20, binary);
```

### Permutation

Permutations are parsed into an integer array, with the i-th index containing the i-th element of the permutation.
Here we read a permutation of 5 elements:

```c
int permutation[5];
MOEA_Read_permutation(5, permutation);
```

### Subset

Subsets are also parsed into an integer array, but can have variable length.  Therefore, the method will return both
the array and the number of elements in the array.  Here, we read a subset of size `2 <= subset_size <= 5`.  Note
we allocate the array to hold the maximum size:

```c
int subset[5];
int subset_size;

MOEA_Read_subset(2, 5, subset, &subset_size); 
```

## Sockets

The above example uses standard input and output to communicate with the program.  Programs that use input / output for
any other purpose will interfere with the communication.  To avoid this, we can instead use sockets (networking) to
send messages between the two processes.  To enable sockets:

First, in the C/C++ code, change `MOEA_init` to `MOEA_Init_socket`.  As our example above demonstrates, we define
`USE_SOCKET` to switch between these two methods.  Use the following to compile with this flag enabled:

```bash
gcc -DUSE_SOCKET -o dtlz2_socket.exe dtlz2.c moeaframework.c -lm
```

Second, update the Java code, namely the `MyDTLZ2` constructor, to use sockets:

<!-- java:examples/org/moeaframework/examples/external/ExternalProblemWithSocket.java [45:49] -->

```java
public MyDTLZ2() throws IOException {
    super(new ExternalProblem.Builder()
            .withCommand("./examples/dtlz2_socket.exe")
            .withSocket("127.0.0.1", DEFAULT_PORT));
}
```

## Other Languages

While we provide a C/C++ library for convenience, this same technique can be used by other programming languages.
Simply construct a loop to read the decision variables from the input and write the objective (and constraint) values
to the output.  As an example, we have included Python examples at [`examples/dtlz2.py`](../examples/dtlz2.py) and
[`examples/dtlz2_socket.py`](../examples/dtlz2_socket.py).

## Troubleshooting

#### Missing msys.dll on Windows
This occurs when using the default MSYS compiler instead of the MinGW compiler.  The latter creates an executable that
can run on Windows outside of the MSYS environment.  Run `which gcc` and verify it outputs `/mingw64/bin/gcc`.
If not, make sure you are using the MinGW64 terminal.

#### Linking (ld) errors on Windows
When compiling on Windows, we must link against the Winsock libraries.  Add `-lwsock32 -lWs2_32` at the end of the
`gcc` command.

#### Enable debugging output
If you are seeing unexpected behavior, please enable debugging to try and diagnose the problem.  You can enable this
on a specific problem by modifying the constructor:

```java
new Builder()
    .withCommand("./examples/dtlz2_stdio.exe")
    .withDebugging()
```

or globally by adding the following line to `moeaframework.properties`:

```
org.moeaframework.problem.external.enable_debugging = true
```
