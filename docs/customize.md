## Defining New Algorithms

Similary, we can develop new optimization algorithms by implementing the `Algorithm` interface.  Instead of
implementing this interface directly, there are several abstract classes that provide common functionality, including
`AbstractAlgorithm` and `AbstractEvolutionaryAlgorithm`.

For this example, we're going to create a simple algorithm that just randomly mutates solutions in the population
using the Polynomial Mutation (PM) operator.  We'll call this the `RandomWalker` algorithm.  Since this algorithm
will maintain a population of solutions, we can build this from the `AbstractEvolutionaryAlgorithm`:

<!-- java:examples/org/moeaframework/examples/misc/RandomWalkerExample.java [36:69] -->

```java
public static class RandomWalker extends AbstractEvolutionaryAlgorithm {

    public RandomWalker(Problem problem) {
        super(problem,
                Settings.DEFAULT_POPULATION_SIZE,
                new NondominatedSortingPopulation(),
                null,
                new RandomInitialization(problem),
                OperatorFactory.getInstance().getVariation("pm", problem));
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
   populuation, mutate it using Polynomial Mutation (PM), add it back into the population, and truncate the worst
   solution from the population.

Then, to use this algorithm, we simply construct it like we would any other:

<!-- java:examples/org/moeaframework/examples/misc/RandomWalkerExample.java [72:74] -->

```java
RandomWalker algorithm = new RandomWalker(new Srinivas());
algorithm.run(10000);
algorithm.getResult().display();
```
