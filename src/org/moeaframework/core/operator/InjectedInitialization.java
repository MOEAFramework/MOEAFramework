/* Copyright 2009-2018 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	private List<Solution> injectedSolutions;

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
