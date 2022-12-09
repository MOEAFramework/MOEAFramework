# Experiments

This library also supports running experiments where we analayze the performance of one or more algorithms.  This capability
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

The `Analyzer` shows end-of-run performance.  We can also look at the runtime dynamics using the `Instrumenter`.  It collects
information about each algorithm while it is running:

```java

Instrumenter instrumenter = new Instrumenter()
        .withProblem("UF1")
        .withFrequency(100)
        .attachElapsedTimeCollector()
        .attachGenerationalDistanceCollector();
		
new Executor()
        .withProblem("UF1")
        .withAlgorithm("NSGAII")
        .withMaxEvaluations(10000)
        .withInstrumenter(instrumenter)
        .run();
		
instrumenter.getObservations().display();
```

```
NFE   Elapsed Time GenerationalDistance 
----- ------------ -------------------- 
100   0.048455     0.554547             
200   0.068240     0.486866             
300   0.076951     0.876918             
400   0.082156     0.690796             
500   0.087247     0.542534             
...                 
9500  0.351868     0.044477             
9600  0.354273     0.037876             
9700  0.356552     0.038029             
9800  0.358757     0.038815             
9900  0.361296     0.032959             
10000 0.363735     0.020477             
```
