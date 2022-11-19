# The Executor, Analyzer, and Instrumenter

## Executor

The `Executor`'s sole responsibility is to apply an optimization algorithm to a problem.  For example, here we run
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
analyzer.printAnalysis();
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

analyzer.printAnalysis();
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
		
Accumulator accumulator = instrumenter.getLastAccumulator();
		
System.out.format("  NFE    Time      Generational Distance%n");
		
for (int i=0; i<accumulator.size("NFE"); i++) {
    System.out.format("%5d    %-8.4f  %-8.4f%n",
        accumulator.get("NFE", i),
        accumulator.get("Elapsed Time", i),
        accumulator.get("GenerationalDistance", i));
}
```

```
  NFE    Time      Generational Distance
  100    0.0484    0.5520  
  200    0.0701    0.4409  
  300    0.0792    0.3811  
  400    0.0860    0.4267  
  500    0.0915    0.3590  
  ...    ...       ...
 9500    0.3894    0.0021  
 9600    0.3923    0.0022  
 9700    0.3953    0.0021  
 9800    0.3985    0.0022  
 9900    0.4016    0.0023  
10000    0.4060    0.0022  
```
