# Other Topics

## Checkpoints

Checkpoints are useful when performing expensive or long-running optimizations.  This will periodically save
the state of the optimization to a file, which can be resumed at a later point if the run is interrupted.

<!-- java:examples/org/moeaframework/examples/misc/CheckpointExample.java [36-48] -->

```java
File checkpointFile = new File("checkpoint.dat");

NSGAII algorithm = new NSGAII(new Srinivas());
Checkpoints checkpoints = new Checkpoints(algorithm, checkpointFile, 1000);

if (checkpointFile.exists()) {
    System.out.println("Checkpoint file exists, resuming previous run at " +
        checkpoints.getNumberOfEvaluations() + " evaluations!");
} else {
    System.out.println("No checkpoint file, starting new run!");
}

checkpoints.run(1000000);
```

If we run this example a few times, you will see the output:

```
No checkpoint file, starting new run!
Checkpoint file exists, resuming previous run at 1000000 evaluations!
Checkpoint file exists, resuming previous run at 2000000 evaluations!
```

## Saving and Formatting Results

In many of the examples, you likely noticed we displayed results using `algorithm.getResult().display()`.
Alternatively, we can save the output to a file using:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [41-41] -->

```java
algorithm.getResult().asTabularData().saveCSV(new File("solutions.csv"));
```

We can also customize how the output is formatted.  Say we want to use ten digits of precision
for the output:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [44-49] -->

```java
NumberFormatter numberFormat = new NumberFormatter();
numberFormat.setPrecision(10);
		
TabularData<Solution> results = algorithm.getResult().asTabularData();
results.addFormatter(numberFormat);
results.display();
```

## Injecting Initial Solutions

By default, algorithms initialize the population using randomly-generated solutions.  It's also possible to
inject pre-defined solutions into the initial population:

<!-- java:examples/org/moeaframework/examples/misc/InjectSolutionsExample.java [33-45] -->

```java
Problem problem = new DTLZ2(2);

Solution solutionA = problem.newSolution();
EncodingUtils.setReal(solutionA, new double[] { 0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 });

Solution solutionB = problem.newSolution();
EncodingUtils.setReal(solutionB, new double[] { 1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 });

NSGAII algorithm = new NSGAII(problem);	
algorithm.setInitialization(new InjectedInitialization(problem, solutionA, solutionB));
algorithm.run(10000);

algorithm.getResult().display();
```

## Mixed Types

Some problems may require combining different types of decision variables.  This is supported by the
MOEA Framework, but it does require some additional setup.  First, when defining the problem, configure
the solution with the desired types.  Here we setup up a binary integer and a real decision variable.

```java
public class MixedTypeProblem extends AbstractProblem {
        // ... other methods ...

	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 0);
		
		solution.setVariable(0, EncodingUtils.newBinaryInt(0, 10));
		solution.setVariable(1, EncodingUtils.newReal(0.0, 1.0));
		
		return solution;
	}
}
```

No default operators are provided for mixed types, but we can easily construct the operators using
the `OperatorFactory` and the expression `"sbx+hux+pm+bf"`, combining the
Simulated Binary Crossover (SBX), Half-Uniform Crossover (HUX), Polynomial Mutation (PM), and
Bit Flip Mutation (BF) operators.  SBX and PM operate on the real value whereas HUX and BF operate on
the binary variable.

```java

MixedTypeProblem problem = new MixedTypeProblem();
NSGAII algorithm = new NSGAII(problem);

algorithm.setVariation(OperatorFactory.getInstance().getVariation("sbx+hux+pm+bf", problem));
		
algorithm.run(10000);
algorithm.getResult().display();
```

The order of the operators in `"sbx+hux+pm+bf"` does matter.  The rule of thumb is to put the
crossover operators first (SBX and HUX) followed by the mutation operators (PM and BF).

Alternatively, we can use the constructors of each operator and `CompoundVariation` to combine
them.  The following produces the same operator as above:

```java
algorithm.setVariation(new CompoundVariation(new SBX(), new HUX(), new PM(), new BitFlip()));
```
