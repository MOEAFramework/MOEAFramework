# Other Topics

## Checkpoints

Checkpoints are useful when performing expensive or long-running optimizations.  This will periodically save
the state of the optimization to a file, which can be resumed at a later point if the run is interrupted.

<!-- java:examples/org/moeaframework/examples/misc/CheckpointExample.java [37:49] -->

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
Alternatively, we can save the output to a file in various formats using:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [41:43] -->

```java
algorithm.getResult().save(TableFormat.CSV, new File("solutions.csv"));
algorithm.getResult().save(TableFormat.Markdown, new File("solutions.md"));
algorithm.getResult().save(TableFormat.Latex, new File("solutions.tex"));
```

We can also customize how the output is formatted.  Say we want to use ten digits of precision
for the output:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [46:47] -->

```java
NumberFormatter.getDefault().setPrecision(10);
algorithm.getResult().display();
```

## Injecting Initial Solutions

By default, algorithms initialize the population using randomly-generated solutions.  It's also possible to inject
pre-defined solutions into the initial population.  The remainder of the population will be filled with random
solutions.

<!-- java:examples/org/moeaframework/examples/misc/InjectSolutionsExample.java [34:46] -->

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

Some problems may require combining different types of decision variables.  This is supported by the MOEA Framework,
but it does require some additional setup.  First, when defining the problem, configure the solution with the desired
types.  Here we setup up a binary integer and a real decision variable.

<!-- java:examples/org/moeaframework/examples/misc/MixedTypesExample.java [62:68] -->

```java
public Solution newSolution() {
    Solution solution = new Solution(2, 2, 2);

    solution.setVariable(0, EncodingUtils.newBinaryInt(-20, 20));
    solution.setVariable(1, EncodingUtils.newReal(-20.0, 20.0));

    return solution;
```

The `evaluate` method would also need to read the correct types:

<!-- java:examples/org/moeaframework/examples/misc/MixedTypesExample.java [48:49] -->

```java
int x = EncodingUtils.getInt(solution.getVariable(0));
double y = EncodingUtils.getReal(solution.getVariable(1));
```

No default operators are provided for mixed types, but we can easily construct the operators using
`CompoundVariation` to combine the individual operators.  Here we combine Simulated Binary Crossover (SBX),
Half-Uniform Crossover (HUX), Polynomial Mutation (PM), and Bit Flip Mutation (BF) operators.  SBX and PM operate on
the real value whereas HUX and BF operate on the binary variable.

<!-- java:examples/org/moeaframework/examples/misc/MixedTypesExample.java [74:80] -->

```java
Problem problem = new MixedTypesSrinivasProblem();
NSGAII algorithm = new NSGAII(problem);

algorithm.setVariation(new CompoundVariation(new SBX(), new HUX(), new PM(), new BitFlip()));

algorithm.run(10000);
algorithm.getResult().display();
```

The order of the operators does matter.  The rule of thumb is to put the crossover operators first (SBX and HUX)
followed by the mutation operators (PM and BF).

## Termination Conditions

We typically run algorithms for a fixed number of function evaluations (NFE), but we can specify different termination
conditions.  Here, we set the max wall-clock time.

<!-- java:examples/org/moeaframework/examples/misc/MaxTimeTerminationExample.java [35:36] -->

```java
NSGAII algorithm = new NSGAII(new Srinivas());
algorithm.run(new MaxElapsedTime(Duration.ofSeconds(seconds)));
```

## Reference Sets

We provide default reference sets for many problems under the `./pf/` folder.  These same reference sets are
automatically used when computing performance indicators, unless you provide your own sets.  For instance, below
we load a reference set from file to use with the `Indicators`:

<!-- java:examples/Example2.java [44:47] -->

```java
NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/DTLZ2.2D.pf");

Indicators indicators = Indicators.all(problem, referenceSet);
indicators.apply(approximationSet).display();
```

For many of the test problems, the reference set is known analytically.  These problems implement the
`AnalyticalProblem` interface which defines the `generate()` method.  Each time we call `generate()`, we get
a new, random Pareto optimal solutions.  Below demonstrates how we can generate and save these Pareto optimal solutions
to a file:

<!-- java:examples/org/moeaframework/examples/misc/GenerateReferenceSetExample.java [36:44] -->

```java
try (AnalyticalProblem problem = new DTLZ2(3)) {
    NondominatedPopulation archive = new EpsilonBoxDominanceArchive(Epsilons.of(0.01));

    for (int i = 0; i < 1000; i++) {
        archive.add(problem.generate());
    }

    archive.saveObjectives(new File("DTLZ2_3_RefSet.txt"));
}
```
