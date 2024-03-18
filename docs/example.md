# An Example

Let's create and solve the 2-dimension DTLZ2 test problem using NSGA-II:

<!-- java:examples/Example1.java [29:34] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

The last line gets the Pareto non-dominated solutions to the problem and displays them in the terminal.

<!-- output:examples/Example1.java [:7] -->

```
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- --------
0.000000 0.507443 0.502672 0.512445 0.495386 0.506435 0.503101 0.502002 0.502372 0.504076 0.501044 1.000317 0.000000
0.999999 0.494182 0.504375 0.501972 0.499916 0.490770 0.495367 0.494110 0.499109 0.505300 0.503289 0.000002 1.000238
0.823936 0.495123 0.504453 0.502181 0.503019 0.489560 0.503451 0.494876 0.468302 0.509226 0.501564 0.273403 0.963248
0.845306 0.507390 0.502668 0.502006 0.502936 0.512325 0.495864 0.493941 0.499754 0.503011 0.500400 0.240678 0.970903
0.808070 0.507494 0.505370 0.488540 0.495386 0.506386 0.503131 0.502002 0.502372 0.503923 0.501195 0.297031 0.955197
```

## Performance indicators

We can also measure the quality of the resulting Pareto non-dominated solutions using one or more performance
indicators, such as Hypervolume.  The `Indicators` class is a convenient way to calculate and display the
performance indicator values.  We can select specific indicators or enable all.

<!-- java:examples/Example2.java [37:48] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

NondominatedPopulation approximationSet = algorithm.getResult();

NondominatedPopulation referenceSet = PopulationIO.readReferenceSet("pf/DTLZ2.2D.pf");

Indicators indicators = Indicators.all(problem, referenceSet);
indicators.apply(approximationSet).display();
```

<!-- output:examples/Example2.java -->

```
Indicator                    Value
---------------------------- --------
Hypervolume                  0.209259
GenerationalDistance         0.000987
InvertedGenerationalDistance 0.004388
AdditiveEpsilonIndicator     0.008974
Spacing                      0.005820
MaximumParetoFrontError      0.039554
Contribution                 0.000000
R1                           0.471058
R2                           0.000258
R3                           0.000389
```

## Configuring Algorithms

While this looks simple, there is a lot going on behind the scenes.  When we create a new instance of the NSGA-II algorithm, it 
inspects the problem to determine its type and supplies the recommended variation operators and parameters.  In this example,
since the problem is real-valued, it will use Simulated Binary Crossover (SBX) and Polynomial Mutation (PM).  We can confirm
this by viewing the configuration:

<!-- java:examples/org/moeaframework/examples/configuration/GetConfigurationExample.java [32:35] -->

```java
Problem problem = new DTLZ2(2);
NSGAII algorithm = new NSGAII(problem);

algorithm.getConfiguration().display();
```

```
operator=sbx+pm
pm.distributionIndex=20.0
pm.rate=0.09090909090909091
populationSize=100
sbx.distributionIndex=15.0
sbx.rate=1.0
sbx.swap=true
sbx.symmetric=false
```

What if we want to run this algorithm with a different configuration?  We can simply call the setter methods.
Here we change the initial population size and set the variation operator to Parent Centrix Crossover (PCX):

<!-- java:examples/org/moeaframework/examples/configuration/SetConfigurationExample.java [33:40] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.setInitialPopulationSize(250);
algorithm.setVariation(new PCX(10, 2));

algorithm.run(10000);
algorithm.getResult().display();
```

Alternatively, similar to how we read the current configuration with `getConfiguration`, we can apply
different settings with `applyConfiguration`.  For example, the following results in the same setup
as the previous example:

<!-- java:examples/org/moeaframework/examples/configuration/ApplyConfigurationExample.java [34:46] -->

```java
Problem problem = new DTLZ2(2);
NSGAII algorithm = new NSGAII(problem);

TypedProperties properties = new TypedProperties();
properties.setInt("populationSize", 250);
properties.setString("operator", "pcx");
properties.setInt("pcx.parents", 10);
properties.setInt("pcx.offspring", 2);

algorithm.applyConfiguration(properties);

algorithm.run(10000);
algorithm.getResult().display();
```
