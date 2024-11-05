# Adding New Algorithms

We can introduce new optimization algorithms by implementing the `Algorithm` interface.  Instead of
implementing this interface directly, there are several abstract classes that provide common functionality, including
`AbstractAlgorithm` and `AbstractEvolutionaryAlgorithm`.

## Implementing the Algorithm

For this example, we're going to create a simple algorithm that just randomly mutates solutions in the population
using the Polynomial Mutation (PM) operator.  We'll call this the `RandomWalker` algorithm.  Since this algorithm
will maintain a population of solutions, we can build this from the `AbstractEvolutionaryAlgorithm`:

<!-- java:examples/org/moeaframework/examples/algorithm/RandomWalker.java [33:67] -->

```java
public class RandomWalker extends AbstractEvolutionaryAlgorithm {

    public RandomWalker(Problem problem) {
        super(problem,
                Settings.DEFAULT_POPULATION_SIZE,
                new NondominatedSortingPopulation(),
                null,
                new RandomInitialization(problem),
                OperatorFactory.getInstance().getVariation("pm", problem));
    }

    @Override
    public String getName() {
        return "RandomWalker";
    }

    @Override
    protected void iterate() {
        NondominatedSortingPopulation population = (NondominatedSortingPopulation)getPopulation();

        int index = PRNG.nextInt(population.size());
        Solution parent = population.get(index);

        Solution offspring = getVariation().evolve(new Solution[] { parent })[0];

        evaluate(offspring);
        population.add(offspring);
        population.truncate(population.size()-1);
    }

}
```

The two key components are:

1. The constructor, which configures the algorithm.  This includes setting the initial population size, the type of
   population (non-dominated sorting), the initialization strategy, and the variation operator(s).
2. The `iterate` method, which defines one iteration of the algorithm.  Here we randomly select one parent from the
   population, mutate it using Polynomial Mutation (PM), add it back into the population, and truncate the worst
   solution from the population.
   
## Running the Algorithm

Then, to use this algorithm, we simply construct it like we would any other:

<!-- java:examples/org/moeaframework/examples/algorithm/RandomWalkerExample.java [28:30] -->

```java
RandomWalker algorithm = new RandomWalker(new Srinivas());
algorithm.run(10000);
algorithm.getResult().display();
```

## Configuring the Algorithm

Like all algorithms defined in the MOEA Framework, we initialized the default settings in the constructor.  However,
these settings can not be changed unless we make them configurable.  We accomplish this by adding setter methods for
each property:

<!-- java:examples/org/moeaframework/examples/algorithm/ConfigurableRandomWalker.java [34:44] -->

```java
@Override
@Property("populationSize")
public void setInitialPopulationSize(int initialPopulationSize) {
    super.setInitialPopulationSize(initialPopulationSize);
}

@Override
@Property("operator")
public void setVariation(Variation variation) {
    super.setVariation(variation);
}
```

Also note the `@Property` annotations.  The MOEA Framework provides a Configuration API for inspecting and configuring
these properties.  We can get the current configuration as follows:

<!-- java:examples/org/moeaframework/examples/algorithm/ConfigurableRandomWalkerExample.java [29:29] -->

```java
algorithm.getConfiguration().display();
```

<!-- output:examples/org/moeaframework/examples/algorithm/ConfigurableRandomWalkerExample.java -->

```
Property             Value
-------------------- ------------
algorithm            RandomWalker
operator             pm
pm.distributionIndex 20.0
pm.rate              0.5
populationSize       100
problem              Srinivas
```

Here, we find the two properties we added, `populationSize` and `operator`.  Two additional properties for the
Polynomial Mutation (PM) operator, those prefixed with `pm.`, are also included automatically.


