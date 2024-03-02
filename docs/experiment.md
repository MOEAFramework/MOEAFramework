# Experiments

This library also supports running experiments where we analyze the performance of one or more algorithms.  This capability
is split into three classes: the `Executor`, `Analyzer`, and `Instrumenter`.

## Executor

The `Executor`'s sole responsibility is to configure and execute an optimization algorithm on a problem.  For example, here we run
NSGA-II on the UF1 test problem for 10,000 function evaluations.

```java

NondominatedPopulation result = new Executor()
        .withProblem("UF1")
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000)
        .run();
```

If you want to test your own problem, simply pass in your problem's class:

```java

NondominatedPopulation result = new Executor()
        .withProblem(new MyProblem())
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000)
        .run();
```

Each algorithm can be customized by setting properties.  The available properties are detailed in the [List of Algorithms](algorithms.md)
and [List of Operators](operators.md).

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

The `Analyzer` takes the output from an `Executor`, namely the Pareto sets produced by an optimization algorithm, and perform some analysis.
Typically this is evaluating the quality of a Pareto set, using hypervolume, generational distance, or another quality indicator.  For example:

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

```
NSGAII:
    Hypervolume: 
        Min: 0.25346731086086105
        Median: 0.4978698844074442
        Max: 0.5428078102564696
        Count: 50
    GenerationalDistance: 
        Min: 0.0012862482952161272
        Median: 0.009759301199366435
        Max: 0.10977024941144814
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
200   0.513917             
300   0.420843             
400   0.424941             
500   0.379473             
600   0.389628             
700   0.340794             
800   0.277609             
900   0.242039             
1000  0.214610             
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

```java

instrumenter.getObservations().saveCSV(new File("NSGAII_UF1_Runtime.csv"));
```
