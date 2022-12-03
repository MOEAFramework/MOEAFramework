# An Example

Starting in version 3.2, we can now create, configure, and run algorithms directly using classes.  Below, we create and solve
the 2-dimension DTLZ2 test problem using NSGA-II:

```java

Problem problem = new DTLZ2(2);
NSGAII algorithm = new NSGAII(problem);
		
algorithm.run(10000);

NondominatedPopulation result = algorithm.getResult();
```

While this looks simple, there is a lot going on behind the scenes.  When we create a new instance of the NSGA-II algorithm, it 
inspects the problem to determine its type and supplies the correct variation operators.  In this example, since the problem is
real-valued, it will use Simulated Binary Crossover (SBX) and Polynomial Mutation (PM).  We can confirm this by viewing
the configuration:

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

What if we want to run this algorithm with a different configuration?  We can simply call the setter methods:

```java

Problem problem = new DTLZ2(2);

NSGAII algorithm = new NSGAII(problem);
algorithm.setInitialPopulationSize(250);
algorithm.setVariation(new PCX(10, 2));
algorithm.setArchive(new EpsilonBoxDominanceArchive(0.01));
		
algorithm.run(10000);
```
