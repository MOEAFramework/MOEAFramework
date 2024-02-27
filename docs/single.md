# Single-Objective Optimization

While the MOEA Framework, by it's name, is intended for multi-objective optimization, it does support single-objective
optimization.  

## Defining the Problem

A single-objective problem is defined the same way as their multi-objective variants, except we specify only
one objective.  Here, we create a class for the Rosenbrock problem with an optimal solution at (1, 1).

<!-- java:examples/org/moeaframework/examples/single/RosenbrockProblem.java [28:54] -->

```java
public class RosenbrockProblem extends AbstractProblem {
	
	public RosenbrockProblem() {
		super(2, 1, 0);
	}

	@Override
	public void evaluate(Solution solution) {
		double result = 0.0;
		double[] x = EncodingUtils.getReal(solution);

		for (int i = 0; i < x.length-1; i++) {
			result += 100 * (x[i]*x[i] - x[i+1])*(x[i]*x[i] - x[i+1]) + (x[i] - 1)*(x[i] - 1);
		}

		solution.setObjective(0, result);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1, 0);
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
Problem problem = new RosenbrockProblem();

GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
algorithm.run(100000);

algorithm.getResult().display();
```

<!-- output:examples/org/moeaframework/examples/single/SingleObjectiveExample.java -->

```
Var1     Var2     Obj1     
-------- -------- -------- 
1.004058 1.008122 0.000016 
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

<!-- output:examples/org/moeaframework/examples/single/MultiObjectiveWithWeightsExample.java -->

```
Linear weights:
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2     
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- 
1.000000 0.499812 0.499999 0.499998 0.499788 0.500457 0.499990 0.500054 0.500197 0.499950 0.500217 0.000000 1.000000 

Min-Max weights:
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2     
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- 
0.795167 0.500311 0.500209 0.499812 0.500178 0.501225 0.500197 0.499787 0.499976 0.500311 0.500805 0.316228 0.948686 
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
