# Running Experiments

This library also supports running experiments where we analyze the performance of one or more algorithms.  This
capability is split into three classes: the `Executor`, `Analyzer`, and `Instrumenter`.

## Executor

The `Executor`'s sole responsibility is to configure and execute an optimization algorithm on a problem.
For example, here we run NSGA-II on the UF1 test problem for 10,000 function evaluations.

<!-- java:test/org/moeaframework/snippet/ExperimentSnippet.java [41:45] -->

```java
NondominatedPopulation result = new Executor()
        .withProblem("UF1")
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000)
        .run();
```

If you want to test your own problem, simply pass in your problem's class:

<!-- java:test/org/moeaframework/snippet/ExperimentSnippet.java [50:54] -->

```java
NondominatedPopulation result = new Executor()
        .withProblem(new MyProblem())
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000)
        .run();
```

Each algorithm can be customized by setting properties.  The available properties are detailed in the
[List of Algorithms](listOfAlgorithms.md) and [List of Operators](listOfOperators.md).

<!-- java:test/org/moeaframework/snippet/ExperimentSnippet.java [59:65] -->

```java
NondominatedPopulation result = new Executor()
        .withProblem("UF1")
        .withAlgorithm("NSGAII")
        .withProperty("populationSize", 250)
        .withProperty("operator", "pcx+um")
        .withMaxEvaluations(10000)
        .run();
```

## Analyzer

The `Analyzer` takes the output from an `Executor`, namely the Pareto sets produced by an optimization algorithm,
and perform some analysis.  Typically this is evaluating the quality of a Pareto set, using hypervolume, generational
distance, or another quality indicator.  For example:

<!-- java:examples/org/moeaframework/examples/experiment/MultipleSeedsExample.java [30:41] -->

```java
Executor executor = new Executor()
        .withProblem("UF1")
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000);

Analyzer analyzer = new Analyzer()
        .withSameProblemAs(executor)
        .includeHypervolume()
        .includeGenerationalDistance();

analyzer.addAll("NSGAII", executor.runSeeds(50));
analyzer.display();
```

produces the output:

<!-- output:examples/org/moeaframework/examples/experiment/MultipleSeedsExample.java -->

```
NSGAII:
    Hypervolume:
        Min: 0.319489
        Median: 0.514978
        Max: 0.544461
        Count: 50
    GenerationalDistance:
        Min: 0.000488
        Median: 0.005468
        Max: 0.049152
        Count: 50
```

The results from multiple algorithms can be provided and compared:

<!-- java:examples/Example4.java [39:59] -->

```java
String problem = "UF1";
String[] algorithms = { "NSGAII", "GDE3", "eMOEA" };

Executor executor = new Executor()
        .withProblem(problem)
        .withMaxEvaluations(10000);

Analyzer analyzer = new Analyzer()
        .withProblem(problem)
        .includeHypervolume()
        .showStatisticalSignificance();

for (String algorithm : algorithms) {
    analyzer.addAll(algorithm, executor.withAlgorithm(algorithm).runSeeds(50));
}

analyzer.display();
```

## Instrumenter

The `Analyzer` shows end-of-run performance.  We can also look at the runtime dynamics using the `Instrumenter`.
It collects information about each algorithm while it is running at a fixed frequency:

<!-- java:examples/Example5.java [40:55] -->

```java
Instrumenter instrumenter = new Instrumenter()
        .withProblem("UF1")
        .withFrequency(100)
        .attachGenerationalDistanceCollector();

new Executor()
        .withProblem("UF1")
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000)
        .withInstrumenter(instrumenter)
        .run();

instrumenter.getObservations().display();
```

<!-- output:examples/Example5.java [:12] -->

```
NFE   GenerationalDistance
----- --------------------
100   0.799030
200   0.707753
300   0.438113
400   0.383873
500   0.431799
600   0.372148
700   0.344861
800   0.294252
900   0.294386
1000  0.293309
```

We can also plot these results:

<!-- java:examples/org/moeaframework/examples/plots/PlotRuntimeDynamics.java [50:54] -->

```java
Observations observations = instrumenter.getObservations();

new Plot()
    .add(observations)
    .show();
```

![image](https://user-images.githubusercontent.com/2496211/226907939-8d8569e6-b7f1-4574-badd-3d6d5800f380.png)

Or export the data into another format, such as comma-separated values (CSV), that can be loaded into Excel or other tools:

<!-- java:test/org/moeaframework/snippet/ExperimentSnippet.java [102:102] -->

```java
instrumenter.getObservations().saveCSV(new File("NSGAII_UF1_Runtime.csv"));
```
