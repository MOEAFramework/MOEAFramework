# Adding Problems

While this library includes a [large collection of test problems](listOfProblems.md) found in the literature, it also
allows solving your own multi-objective optimization problems.

## Three Approaches

The three main approaches to integrate your own optimization problems are:

1. [Writing the Problem in Java](writingJavaProblem.md): We recommend this approach as it's the easiest option, but
   does require some knowledge of Java.
   
2. [Natively-compiled Function](writingNativeProblem.md): This approach involves compiling code written in
   C, C++, Fortran, or another language.  We do provide a tool that generates problem templates to make it easier to
   configure, compile, and use native functions.
   
3. [Calling Executable with Standard I/O or Sockets](writingExternalProblem.md): This approach involves calling an
   external program using standard I/O or sockets for communication.  This has significantly more overhead, but can be
   used with practically any programming language.
   
## Performance

The table below shows the average time to perform 100,000 function evaluations of a simple test problem (DTLZ2), so
these times reflect the overhead from calling the function.

Method                          | Avg Time (sec)
------------------------------- | --------------
Java                            | 0.593
Native Fortran                  | 0.465
Native C                        | 0.587
Native C++                      | 0.587
External C - Standard I/O       | 3.714
External Python - Standard I/O  | 4.584
External C - Sockets            | 5.862
External Python - Sockets       | 7.508

The key takeaway is writing the function in pure Java or using a low-level compiled function in C, C++, Fortran, or
similar language offers the best performance.  The `ExternalProblem` approach, which communicates with a program
using standard I/O or sockets, is about 6x slower, but this is still relatively fast given it ran 100,000 function
evaluations.
