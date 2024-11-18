# Performance Indicators

Performance indicators measure the quality of an approximation set, i.e., the result of running an algorithm, typically
measuring in some manner the convergence (proximity) and / or diversity (spread) of solutions.  Often, but not always,
this measurement is in relation to a reference set containing the optimal solutions to the problem.

The MOEA Framework supports the common performance indicators, including hypervolume, generational distance (GD),
inverted generational distance (IGD), epsilon-indciator, and the R-indicators.  We can construct the specific indicator
we are evaluating:

<!-- java:examples/org/moeaframework/examples/indicators/HypervolumeExample.java [34:45] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

NondominatedPopulation approximationSet = algorithm.getResult();

NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/DTLZ2.2D.pf");
Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
System.out.println("Hypervolume: " + hypervolume.evaluate(approximationSet));
```

Or use the `Indicators` class as a way to compute multiple performance indicators at the same time.  

<!-- java:examples/Example3.java [34:45] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

NondominatedPopulation approximationSet = algorithm.getResult();

NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/DTLZ2.2D.pf");
Indicators indicators = Indicators.all(problem, referenceSet);
indicators.apply(approximationSet).display();
```

<!-- output:examples/Example3.java -->

```
Indicator                        Value
-------------------------------- --------
Hypervolume                      0.209256
GenerationalDistance             0.001027
GenerationalDistancePlus         0.002517
InvertedGenerationalDistance     0.004491
InvertedGenerationalDistancePlus 0.002956
AdditiveEpsilonIndicator         0.010052
Spacing                          0.005918
MaximumParetoFrontError          0.042614
Contribution                     0.000000
R1Indicator                      0.446108
R2Indicator                      0.000276
R3Indicator                      0.000419
```

## Statistical Comparison

The `IndicatorStatistics` class assists in collecting performance indicators from multiple algorithms and multiple seeds
and comparing results.  Below we configure `IndicatorStatistics` with the hypervolume metric, adding the results from
three algorithms (NSGA-II, MOEA/D, and OMOPSO) across 10 seeds:

<!-- java:examples/org/moeaframework/examples/indicators/IndicatorStatisticsExample.java [39:64] -->

```java
Problem problem = new UF1();
NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/UF1.pf");

Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
IndicatorStatistics statistics = new IndicatorStatistics(hypervolume);

for (int seed = 0; seed < 10; seed++) {
    PRNG.setSeed(seed);

    NSGAII algorithm1 = new NSGAII(problem);
    algorithm1.run(10000);
    statistics.add("NSGA-II", algorithm1.getResult());

    MOEAD algorithm2 = new MOEAD(problem);
    algorithm2.run(10000);
    statistics.add("MOEA/D", algorithm2.getResult());

    OMOPSO algorithm3 = new OMOPSO(problem);
    algorithm3.run(10000);
    statistics.add("OMOPSO", algorithm3.getResult());
}

statistics.display();
```

Calling `display()` will produce a table similar to the following.  We see it aggregates the hypervolume values to
display the minimum, median, maximum, and inter-quartile range (IQR).  Additionally, it uses the non-parametric
Kruskal-Wallis and Mann-Whitney U-test to determine if the results are statistically similar with a significance level
of 95% (`a=0.05`).  Here, we see, according to the tests, that NSGA-II and MOEA/D produce similar results on this
problem.

<!-- output:examples/org/moeaframework/examples/indicators/IndicatorStatisticsExample.java -->

```
Name    Min      Median   Max      IQR (+/-) Count Statistically Similar (a=0.05)
------- -------- -------- -------- --------- ----- ------------------------------
MOEA/D  0.369024 0.464019 0.548999 0.063138  10    NSGA-II
NSGA-II 0.401330 0.515988 0.531498 0.086326  10    MOEA/D
OMOPSO  0.242201 0.332316 0.408065 0.114875  10
```

## Reference Sets

Observe in the examples above we load a reference set from files in the `./pf/` folder.  For many of the test problems,
the reference set is known analytically.  That is, there exists a mathematical formula to generating the reference
set.  Within the MOEA Framework, such problems implement the `AnalyticalProblem` interface.  As shown below, we can
generate reference sets for such problems by calling their `generate()` method:

<!-- java:examples/org/moeaframework/examples/indicators/GenerateReferenceSetExample.java [37:44] -->

```java
AnalyticalProblem problem = new DTLZ2(3);
NondominatedPopulation referenceSet = new EpsilonBoxDominanceArchive(Epsilons.of(0.01));

for (int i = 0; i < 1000; i++) {
    referenceSet.add(problem.generate());
}

referenceSet.save(new File("Custom_DTLZ2.3D.pf"));
```

However, many real problems do not have well-defined reference sets.  In such scenarios, a common practice is to
combine the resulting approximation sets from running multiple algorithms across multiple seeds.


## Normalization

Another consideration when using performance indicators is normalization.  Normalization is the recommended practice
when computing performance indicators as it ensures the results are comparable and repeatable across studies, assuming
of course the same reference set is used.

The standard and recommended practice is using a reference set, either a known reference set or one produced by
combining results from multiple algorithms and seeds:

<!-- java:examples/org/moeaframework/examples/indicators/NormalizationExample.java [44:47] -->


```java
Hypervolume defaultHypervolume = new Hypervolume(problem, referenceSet);
System.out.println("Normalized by reference set (default): " + defaultHypervolume.evaluate(approximationSet.copy()));
```

We can alternatively specify the lower and upper bounds explicitly:

<!-- java:examples/org/moeaframework/examples/indicators/NormalizationExample.java [50:51] -->

```java
System.out.println("Normalized with explicit bounds: " + explicitHypervolume.evaluate(approximationSet));
```

Or disable normalization entirely:

<!-- java:examples/org/moeaframework/examples/indicators/NormalizationExample.java [54:55] -->

```java
    System.out.println("Disabled normalization: " + disabledHypervolume.evaluate(approximationSet));
}
```

Regardless of the approach taken, the key is being consistent and documenting the process, as that ensures your results
can be reproduced by others.

