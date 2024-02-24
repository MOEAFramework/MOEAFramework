# Extending with New Problems and Algorithms

Now let's explore how to solve your own optimization problems or develop new optimization algorithms.

## Defining New Problems

To define a new problem, we need to create a new class following the `Problem` interface.  The `AbstractProblem` class provides a lot of
common functionality, so most of the time we only need to extend `AbstractProblem`.  In doing so, we will need to specify:

1. The constructor, which specifies the number of variables, objectives, and constraints;
2. The `evaluate` method, which reads the decision variables, evaluates the problem, and sets the objectives / constraints; and
3. The `newSolution` method, which defines a solution to the problem, specifically setting the types and bounds of decision variables.

Here we construct the Srinivas problem, defined as:

$$ \begin{align} \text{Minimize } &f_1 = (x - 2)^2 + (y - 1)^2 + 2 \\\ &f_2 = 9x - (y - 1)^2 \\\ \text{Subject to } &x^2 + y^2 \leq 225 \\\ &x - 3y \leq -10 \\\ \text{Where } &-20 \leq x, y \leq 20 \end{align}$$

which has two decision variables, `x` and `y`, two objectives, `f1` and `f2`, and two constraints, `c1` and `c2`.  Programming this
using the MOEA Framework would look something like:

```java

public class SrinivasProblem extends AbstractProblem {

	public SrinivasProblem() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = Math.pow(x - 2.0, 2.0) + Math.pow(y - 1.0, 2.0) + 2.0;
		double f2 = 9.0*x - Math.pow(y - 1.0, 2.0);
		double c1 = Math.pow(x, 2.0) + Math.pow(y, 2.0) - 225.0;
		double c2 = x - 3.0*y + 10.0;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(-20.0, 20.0));
		solution.setVariable(1, EncodingUtils.newReal(-20.0, 20.0));
		
		return solution;
	}

}
```

Next, we can solve this problem by passing it directly to the constructor of an optimization algorithm, such as NSGA-II:

```java
NSGAII algorithm = new NSGAII(new SrinivasProblem());
algorithm.run(10000);
algorithm.getResult().display();
```

## Defining New Algorithms

Similary, we can develop new optimization algorithms by implementing the `Algorithm` interface.  Instead of implementing this interface
directly, there are several abstract classes that provide common functionality, including `AbstractAlgorithm` and `AbstractEvolutionaryAlgorithm`.

For this example, we're going to create a simple algorithm that just randomly mutates solutions in the population using the Polynomial Mutation (PM)
operator.  We'll call this the `RandomWalker` algorithm.  Since this algorithm will maintain a population of solutions, we can build this from the
`AbstractEvolutionaryAlgorithm`:

```java
public class RandomWalker extends AbstractEvolutionaryAlgorithm {
	
	public RandomWalker(Problem problem) {
		super(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new NondominatedSortingPopulation(),
				null, /* no archive */
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation("pm", problem));
	}

	@Override
	protected void iterate() {
		// get the current population
		NondominatedSortingPopulation population = (NondominatedSortingPopulation)getPopulation();
		
		// randomly select a solution from the population
		int index = PRNG.nextInt(population.size());
		Solution parent = population.get(index);
		
		// mutate the selected solution
		Solution offspring = getVariation().evolve(new Solution[] { parent })[0];
		
		// evaluate the objectives/constraints
		evaluate(offspring);
		
		// add the offspring to the population
		population.add(offspring);
		
		// use non-dominated sorting to remove the worst solution
		population.truncate(population.size()-1);
	}

}
```

The two key components are:

1. The constructor, which configures the algorithm.  This includes setting the initial population size, the type of population (non-dominated sorting),
   the initialization strategy, and the variation operator(s).
2. The `iterate` method, which defines one iteration of the algorithm.  Here we randomly select one parent from the populuation, mutate it using Polynomial Mutation (PM),
   add it back into the population, and truncate the worst solution from the population.

Then, to use this algorithm, we simply construct it like we would any other:

```java
RandomWalker algorithm = new RandomWalker(new SrinivasProblem());
algorithm.run(10000);
algorithm.getResult().display();
```
