# Adding Problems

While this library includes a [large collection of test problems](listOfProblems.md) found in the literature, it also
allows solving your own multi-objective optimization problems.

## Two Approaches

The two approaches to integrate your own optimization problems are:

* [Writing the Problem in Java](writingJavaProblems.md): We recommend this approach as it's the easiest option, but
   does require some knowledge of Java.
   
* [Natively-compiled Function](writingNativeProblems.md): This approach involves compiling code written in
   C, C++, Fortran, or another language.  We do provide a tool that generates problem templates to make it easier to
   configure, compile, and use native functions.
