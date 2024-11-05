# Other Topics

## Saving and Formatting Results

In many of the examples, you likely noticed we displayed results using `algorithm.getResult().display()`.
Alternatively, we can save the output to a file in various formats using:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [41:44] -->

```java
algorithm.getResult().save(TableFormat.CSV, new File("solutions.csv"));
algorithm.getResult().save(TableFormat.Markdown, new File("solutions.md"));
algorithm.getResult().save(TableFormat.Latex, new File("solutions.tex"));
algorithm.getResult().save(TableFormat.Json, new File("solution.json"));
```

We can also customize how the output is formatted.  Say we want to use ten digits of precision
for the output:

<!-- java:examples/org/moeaframework/examples/misc/SaveAndFormatResultsExample.java [47:48] -->

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

While most of the problems presented here use a single decision variable type (e.g., all real-valued numbers), the MOEA
Framework also supports mixing types.  To see this in action, let's first define a problem that includes a binary integer
and a real-valued decision variable:

<!-- java:examples/org/moeaframework/examples/misc/MixedTypesExample.java [63:73] -->

```java
public Solution newSolution() {
    Solution solution = new Solution(2, 2, 2);

    solution.setVariable(0, EncodingUtils.newBinaryInt(-20, 20));
    solution.setVariable(1, EncodingUtils.newReal(-20.0, 20.0));

    solution.setConstraint(0, LessThanOrEqual.to(0.0));
    solution.setConstraint(1, LessThanOrEqual.to(0.0));

    return solution;
}
```

The `evaluate` method would also need to read the correct types:

<!-- java:examples/org/moeaframework/examples/misc/MixedTypesExample.java [49:50] -->

```java
int x = EncodingUtils.getInt(solution.getVariable(0));
double y = EncodingUtils.getReal(solution.getVariable(1));
```

Lastly, we must configure the variation operators.  Default operators are typically selected, even for mixed types.
In this example, the default is Simulated Binary Crossover (SBX), Half-Uniform Crossover (HUX), Polynomial Mutation
(PM), and Bit Flip Mutation (BF).  SBX and PM operate on the real variable whereas HUX and BF operate on the
binary variable.

We can also explicitly supply a variation operator for mixed types by combining the operators as demonstrated below:

<!-- java:examples/org/moeaframework/examples/misc/MixedTypesExample.java [78:84] -->

```java
Problem problem = new MixedTypesSrinivasProblem();
NSGAII algorithm = new NSGAII(problem);

algorithm.setVariation(new CompoundVariation(new SBX(), new HUX(), new PM(), new BitFlip()));

algorithm.run(10000);
algorithm.getResult().display();
```

Please refer to the documentation for instructions on combining operators.  The type and order of the operators is
important!

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
NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/DTLZ2.2D.pf");

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

    archive.save(new File("DTLZ2_3_RefSet.txt"));
}
```

## Normalization

When evaluating an approximation set using one of the provided quality / performance indicators, the approximation sets
are automatically normalized by the bounds of the reference set.  Normalization helps to produce comparable and
repeatable results across multiple studies.  However, in some exceptional situations, normalization might not be
feasible (no reference set or insufficient number of solutions) or not desirable (comparing against another study
that did not use normalization).

By default, we would provide a reference set that defines the bounds for normalization:

<!-- java:examples/org/moeaframework/examples/misc/NormalizationExample.java [44:47] -->


```java
NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/UF1.dat");

Hypervolume defaultHypervolume = new Hypervolume(problem, referenceSet);
System.out.println("Normalized by reference set (default): " + defaultHypervolume.evaluate(approximationSet.copy()));
```

We can also explicitly provide the lower and upper bounds:

<!-- java:examples/org/moeaframework/examples/misc/NormalizationExample.java [50:51] -->

```java
Hypervolume explicitHypervolume = new Hypervolume(problem, new double[] { 0.0, 0.0 }, new double[] { 2.0, 2.0 });
System.out.println("Normalized with explicit bounds: " + explicitHypervolume.evaluate(approximationSet));
```

Or disable normalization:

<!-- java:examples/org/moeaframework/examples/misc/NormalizationExample.java [54:55] -->

```java
Hypervolume disabledHypervolume = new Hypervolume(problem, Normalizer.none());
System.out.println("Disabled normalization: " + disabledHypervolume.evaluate(approximationSet));
```

Also note that we can configure normalization preferences for specific problems globally using the
`DefaultNormalizer` class.  Refer to the class documentation for usage.
