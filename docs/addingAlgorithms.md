# Adding and Extending Algorithms

This page details how to add new optimization algorithms or extend the functionality of existing algorithms.

## Adding a New Algorithm

We can introduce new optimization algorithms by implementing the `Algorithm` interface.  Instead of
implementing this interface directly, there are several abstract classes that provide common functionality, including
`AbstractAlgorithm` and `AbstractEvolutionaryAlgorithm`.

For this example, we're going to create a simple algorithm that just randomly mutates solutions in the population
using the Polynomial Mutation (PM) operator.  We'll call this the `RandomWalker` algorithm.  Since this algorithm
will maintain a population of solutions, we can build this from the `AbstractEvolutionaryAlgorithm`:

<!-- java:examples/org/moeaframework/examples/algorithm/NewAlgorithmExample.java [36:65] -->

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

<!-- java:examples/org/moeaframework/examples/algorithm/NewAlgorithmExample.java [68:70] -->

```java
RandomWalker algorithm = new RandomWalker(new Srinivas());
algorithm.run(10000);
algorithm.getResult().display();
```

## Extending Algorithms

We can also modify or extend existing optimization algorithms.  One such means is writing a new subclass that extends
the existing algorithm.  Or, as demonstrated below, we can use the provided `PeriodicAction` wrapper to perform
some operation at a fixed frequency.  Here, we inject additional randomness into the population every 1,000 function
evaluations by applying the Uniform Mutation (UM) operator.

<!-- java:examples/org/moeaframework/examples/algorithm/PeriodicActionExample.java [40-67] -->

```java
Problem problem = new UF1();
NSGAII algorithm = new NSGAII(problem);

PeriodicAction randomizer = new PeriodicAction(algorithm, 1000, FrequencyType.EVALUATIONS) {

    @Override
    public void doAction() {
        System.out.println("Injecting randomness at NFE " + getNumberOfEvaluations());

        NSGAII algorithm = (NSGAII)getAlgorithm();
        NondominatedSortingPopulation population = algorithm.getPopulation();

        Population offspring = new Population();
        UM mutation = new UM(1.0 / getProblem().getNumberOfVariables());

        for (Solution solution : population) {
            offspring.add(mutation.mutate(solution));
        }

        evaluateAll(offspring);
        population.addAll(offspring);
        population.truncate(offspring.size());
    }

};

randomizer.run(10000);
randomizer.getResult().display();
```

> [!TIP]
> Avoid making changes to a population while iterating over its contents.  This will likely result in a 
> `ConcurrentModificationException`.  Instead, collect all offspring and add them back into the population by
> calling `addAll`.  Furthermore, by calling `evaluateAll` on all offspring, we can also parallelize function
> evaluations!
