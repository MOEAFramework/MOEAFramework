package org.moeaframework.core.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Initialization method that injects pre-defined solutions into the initial
 * population.  This is typically used to initialize an algorithm with a set
 * of known "good" solutions.
 */
public class InjectedInitialization extends RandomInitialization {
	
	/**
	 * The solutions to be injected into the initial population.
	 */
	public List<Solution> injectedSolutions;

	/**
	 * Constructs a random initialization operator that includes one or more
	 * pre-defined solutions.
	 * 
	 * @param problem the problem
	 * @param populationSize the initial population size
	 * @param injectedSolutions the pre-defined solutions injected into the
	 *        initial population
	 */
	public InjectedInitialization(Problem problem, int populationSize,
			Solution... injectedSolutions) {
		this(problem, populationSize, Arrays.asList(injectedSolutions));
	}
	
	/**
	 * Constructs a random initialization operator that includes one or more
	 * pre-defined solutions.
	 * 
	 * @param problem the problem
	 * @param populationSize the initial population size
	 * @param injectedSolutions the pre-defined solutions injected into the
	 *        initial population
	 */
	public InjectedInitialization(Problem problem, int populationSize,
			List<Solution> injectedSolutions) {
		super(problem, populationSize);
		this.injectedSolutions = new ArrayList<Solution>(injectedSolutions);
	}

	@Override
	public Solution[] initialize() {
		if (populationSize <= injectedSolutions.size()) {
			return injectedSolutions.toArray(new Solution[0]);
		} else {
			Solution[] initialPopulation = super.initialize();
			
			for (int i = 0; i < injectedSolutions.size(); i++) {
				initialPopulation[i] = injectedSolutions.get(i);
			}
			
			return initialPopulation;
		}
	}

}
