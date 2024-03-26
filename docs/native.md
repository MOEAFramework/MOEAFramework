# Natively-Compiled Functions

In this example, we will create a natively-compiled function for the 2-objective DTLZ2 problem and connect to it
directly using the Java Native Access (JNA) library.  This takes a bit more work to setup than the other methods,
but offers the highest performance.

## Prerequisites

To run this example, you will need:

1. A C/C++ compiler and Make.  For Windows, we recommend using [MSYS2](https://www.msys2.org/) with MinGW.  After
   installing MSYS2, open the MinGW64 terminal and run:
   ```
   pacman -S make mingw-w64-x86_64-gcc
   ```
   
2. The Java Native Access (JNA) library.  Download the latest release from https://github.com/java-native-access/jna
   and place the JAR file on the classpath, typically by extracting it into the `lib/` folder.

## Example

First, create the C file `dtlz2.c` and define the function that evaluates the DTLZ2 problem.  In this example, the
function takes two double arrays (`double*` in C), the first for passing in the decision variables, and the second
for returning the objective values.  If your problem also has constraints, you will need a third array.

<!-- c:https://raw.githubusercontent.com/MOEAFramework/JNAExample/main/dtlz2.c [18:46] -->

```c
#include <math.h>

#define PI 3.14159265358979323846

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

We can then compile this into the shared library with:

```bash
gcc -c -fPIC dtlz2.c -lm
gcc -shared -o dtlz2.dll dtlz2.o
```

Next, moving to Java, we first create a JNA interface that maps to the C function we created above.  Note how the
name of the function and the arguments are identical, except we use `double[]` for the arrays.

<!-- java:https://raw.githubusercontent.com/MOEAFramework/JNAExample/main/src/main/java/NativeDTLZ2.java [31:33] -->

```java
public interface NativeDTLZ2Impl extends Library {
    void evaluate(double[] vars, double[] objs);
}
```

Then, we can load the shared library into Java with:

<!-- java:https://raw.githubusercontent.com/MOEAFramework/JNAExample/main/src/main/java/NativeDTLZ2.java [35:35] -->

```java
private final NativeDTLZ2Impl INSTANCE = (NativeDTLZ2Impl)Native.load("dtlz2", NativeDTLZ2Impl.class);
```

We provide the library name `"dtlz2"`, which JNA locates by looking for the appropriate file on the classpath.
For instance, this would map to `dtlz2.dll` on Windows or `libdtlz2.so` on Linux.

Lastly, just like any other problem in the MOEA Framework, we create a `Problem` class describing the problem.
However, the `evaluate` method now must call into this native code.

<!-- java:https://raw.githubusercontent.com/MOEAFramework/JNAExample/main/src/main/java/NativeDTLZ2.java [41:48] -->

```java
public void evaluate(Solution solution) {
    double[] vars = EncodingUtils.getReal(solution);
    double[] objs = new double[numberOfObjectives];

    INSTANCE.evaluate(vars, objs);

    solution.setObjectives(objs);
}
```

Putting all this together, we end up with the following class defining our problem:

<!-- java:https://raw.githubusercontent.com/MOEAFramework/JNAExample/main/src/main/java/NativeDTLZ2.java [18:] -->

```java
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NativeDTLZ2 extends AbstractProblem {

    public interface NativeDTLZ2Impl extends Library {
        void evaluate(double[] vars, double[] objs);
    }

    private final NativeDTLZ2Impl INSTANCE = (NativeDTLZ2Impl)Native.load("dtlz2", NativeDTLZ2Impl.class);

    public NativeDTLZ2() {
        super(11, 2);
    }

    public void evaluate(Solution solution) {
        double[] vars = EncodingUtils.getReal(solution);
        double[] objs = new double[numberOfObjectives];

        INSTANCE.evaluate(vars, objs);

        solution.setObjectives(objs);
    }

    public Solution newSolution() {
        Solution solution = new Solution(numberOfVariables, numberOfObjectives);

        for (int i = 0; i < numberOfVariables; i++) {
            solution.setVariable(i, new RealVariable(0.0, 1.0));
        }

        return solution;
    }

}
```

For a complete, working version of this example, please visit https://github.com/MOEAFramework/JNAExample.
