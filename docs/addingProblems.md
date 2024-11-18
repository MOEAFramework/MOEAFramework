# Adding Problems

While this library includes a [large collection of test problems](listOfProblems.md) found in the literature, it also
allows solving your own multi-objective optimization problems.

## Two Approaches

The two approaches to integrate your own optimization problems are:

* [Writing the Problem in Java](writingJavaProblem.md): We recommend this approach as it's the easiest option, but
   does require some knowledge of Java.
   
* [Natively-compiled Function](writingNativeProblem.md): This approach involves compiling code written in
   C, C++, Fortran, or another language.  We do provide a tool that generates problem templates to make it easier to
   configure, compile, and use native functions.
   
## Performance

While calling into the native function introduces additional overhead, the natively compiled code is often more
performant than Javaa.  But these two factors tend to cancel out, as the table below shows the average time to
evaluate the function 100,000 times:

Method                          | Avg Time (sec)
------------------------------- | --------------
Java                            | 0.593
Native Fortran                  | 0.465
Native C                        | 0.587
Native C++                      | 0.587
