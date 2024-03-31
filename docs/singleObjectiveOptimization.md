# Single-Objective Optimization

While the MOEA Framework, by it's name, is intended for multi-objective optimization, it does support single-objective
optimization.  

## Defining the Problem

A single-objective problem is defined the same way as their multi-objective variants, except we specify only
one objective.  Here, we create a class for the Rosenbrock problem:

$$ \text{Minimize } f(x,y) = 100(y-x^2)^2 + (1-x)^2 $$

<!-- java:src/org/moeaframework/problem/single/Rosenbrock.java [28:61] -->

```java
public class Rosenbrock extends AbstractProblem implements AnalyticalProblem {

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

    @Override
    public Solution generate() {
        Solution solution = newSolution();
        EncodingUtils.setReal(solution, new double[] { 1.0, 1.0 });
        evaluate(solution);
        return solution;
    }

}
```

## Solving with a Genetic Algorithm

Next, we can solve the Rosenbrock problem using a genetic algorithm:

<!-- java:examples/org/moeaframework/examples/single/SingleObjectiveExample.java [32:37] -->

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

We can also use these single-objective optimizers to solve multi-objective problems.  This works by
calculating a weighted aggregate fitness value.  Two supported approaches are "linear" and "min-max".
Here, we solve the 2-objective DTLZ2 problem:

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

Observe above that each weight vector will typically produce a single solution.  The Repeated Single
Objective (RSO) algorithm extends this idea to produce a Pareto front by solving the problems with
multiple weights.

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
