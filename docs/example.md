# An Example

Let's create and solve the 2-dimension DTLZ2 test problem using NSGA-II:

<!-- java:examples/Example1.java [29-34] -->

```java
Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.run(10000);

algorithm.getResult().display();
```

The output is a `NondominatedPopulation`, which stores the Pareto non-dominated solutions to the problem.  Calling the `display()`
method is an easy way to print the solutions to the console.

```java

result.display();
```

```
Var1     Var2     Var3     Var4     Var5     Var6     Var7     Var8     Var9     Var10    Var11    Obj1     Obj2     
-------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- -------- 
0.000041 0.503718 0.506932 0.447991 0.506008 0.512730 0.485891 0.429912 0.483916 0.490623 0.496267 1.008437 0.000066 
1.000000 0.499793 0.489942 0.462222 0.499729 0.509639 0.484418 0.500449 0.496438 0.506171 0.513194 0.000000 1.002089 
0.138334 0.499877 0.505968 0.500226 0.503661 0.493865 0.492449 0.500704 0.500868 0.501986 0.509822 0.976724 0.215641 
0.157600 0.499868 0.521459 0.498674 0.506442 0.509200 0.492042 0.500788 0.500838 0.496114 0.499437 0.970162 0.245201 
0.738257 0.499375 0.494656 0.489710 0.506868 0.483571 0.497058 0.536027 0.500327 0.503610 0.501457 0.400368 0.918290
...
```

## Configuring Algorithms

While this looks simple, there is a lot going on behind the scenes.  When we create a new instance of the NSGA-II algorithm, it 
inspects the problem to determine its type and supplies the recommended variation operators and parameters.  In this example,
since the problem is real-valued, it will use Simulated Binary Crossover (SBX) and Polynomial Mutation (PM).  We can confirm
this by viewing the configuration:

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

```java

Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.setInitialPopulationSize(250);
algorithm.setVariation(new PCX(10, 2));
		
algorithm.run(10000);

NondominatedPopulation result = algorithm.getResult();
```

Alternatively, similar to how we read the current configuration with `getConfiguration`, we can apply
different settings with `applyConfiguration`.  For example, the following results in the same setup
as the previous example:

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

NondominatedPopulation result = algorithm.getResult();
```
