# Single-Objective Optimization

While the MOEA Framework, by it's name, is intended for multi-objective optimization, it does support single-objective
optimization.  

## Defining the Problem

A single-objective problem is defined the same way as their multi-objective variants, except we specify only one
objective.  Here, we create a class for the Rosenbrock problem:

$$ \text{Minimize } f(x,y) = 100(y-x^2)^2 + (1-x)^2 $$

<!-- java:examples/org/moeaframework/examples/single/Rosenbrock.java [27:49] -->

```java
public class Rosenbrock extends AbstractProblem {

    public Rosenbrock() {
        super(2, 1);
    }

    @Override
    public void evaluate(Solution solution) {
        double x = EncodingUtils.getReal(solution.getVariable(0));
        double y = EncodingUtils.getReal(solution.getVariable(1));

        solution.setObjective(0, 100*(y - x*x)*(y - x*x) + (1 - x)*(1 - x));
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(2, 1);
        solution.setVariable(0, EncodingUtils.newReal(-10, 10));
        solution.setVariable(1, EncodingUtils.newReal(-10, 10));
        return solution;
    }

}
```

## Solving with a Genetic Algorithm

Next, we can solve the Rosenbrock problem using a genetic algorithm:

<!-- java:examples/org/moeaframework/examples/single/SingleObjectiveExample.java [31:36] -->

```java
Problem problem = new Rosenbrock();

GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
algorithm.run(100000);

algorithm.getResult().display();
```

This should produce a solution near the optimum of $(x, y) = (1, 1)$ with a fitness value of $f(x, y) = 0$:

<!-- output:examples/org/moeaframework/examples/single/SingleObjectiveExample.java -->

```
Var1     Var2     Obj1
-------- -------- --------
1.006126 1.012287 0.000038
```

## Using Weights to Solve Multi-Objective Problems

We can also use these single-objective optimizers to solve multi-objective problems.  This works by calculating a
weighted aggregate fitness value.  Two supported approaches are "linear" and "min-max".  Here, we solve the 2-objective
DTLZ2 problem:

<!-- java:examples/org/moeaframework/examples/single/MultiObjectiveWithWeightsExample.java [36:50] -->

```java
Problem problem = new DTLZ2(2);

System.out.println("Linear weights:");
GeneticAlgorithm algorithm1 = new GeneticAlgorithm(problem);
algorithm1.setComparator(new LinearDominanceComparator(0.75, 0.25));
algorithm1.run(100000);
algorithm1.getResult().display();

System.out.println();

System.out.println("Min-Max weights:");
GeneticAlgorithm algorithm2 = new GeneticAlgorithm(problem);
algorithm2.setComparator(new MinMaxDominanceComparator(0.75, 0.25));
algorithm2.run(100000);
algorithm2.getResult().display();
```

Note how linear weights tend to find solutions near the boundaries, whereas min-max weights are often
better at finding intermediate solutions:

<!-- output:examples/org/moeaframework/examples/single/MultiObjectiveWithWeightsExample.java -->

```
Linear weights:
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- --------
1.000000 0.501318 0.499486 0.500479 0.500013 0.499872 0.500399 0.499489 0.499410 0.499346 0.499755 0.000000 1.000004

Min-Max weights:
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- --------
0.795165 0.499168 0.500785 0.499686 0.501572 0.500629 0.499436 0.500051 0.498400 0.500782 0.500000 0.316234 0.948690
```

## Repeated Single Objective

Observe above that each weight vector will typically produce a single solution.  The Repeated Single Objective (RSO)
algorithm extends this idea to produce a Pareto front by solving the problems with multiple weights.

<!-- java:examples/org/moeaframework/examples/single/RepeatedSingleObjectiveExample.java [36:46] -->

```java
Problem problem = new DTLZ2(2);

RepeatedSingleObjective algorithm = new RepeatedSingleObjective(problem, 50,
        (p, w) -> {
            GeneticAlgorithm weightedInstance = new GeneticAlgorithm(p);
            weightedInstance.setComparator(new MinMaxDominanceComparator(w));
            return weightedInstance;
        });

algorithm.run(100000);
algorithm.getResult().display();
```

Since this essentially is solving the problem using `50` genetic algorithms with different weights, we expect to
get `50` Pareto optimal solutions:

<!-- output:examples/org/moeaframework/examples/single/RepeatedSingleObjectiveExample.java [:7] -->

```
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- --------
0.000001 0.784339 0.655026 0.356214 0.484376 0.568065 0.646731 0.392905 0.512172 0.573540 0.462883 1.170366 0.000001
0.999998 0.545630 0.440571 0.465660 0.692718 0.603546 0.405917 0.505772 0.524952 0.569806 0.483925 0.000004 1.069294
0.010852 0.456152 0.539906 0.487323 0.509044 0.486386 0.502341 0.485789 0.455131 0.505509 0.492831 1.006099 0.017152
0.100449 0.512263 0.477734 0.456705 0.511608 0.551392 0.516108 0.519335 0.512558 0.467112 0.522962 0.995179 0.158340
0.097464 0.529165 0.488107 0.469683 0.518976 0.496194 0.541695 0.571284 0.513365 0.519407 0.510400 0.997959 0.153988
```
