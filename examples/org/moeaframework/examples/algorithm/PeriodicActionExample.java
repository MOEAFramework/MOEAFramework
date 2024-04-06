/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.examples.algorithm;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PeriodicAction;
import org.moeaframework.core.Population;
import org.moeaframework.core.PeriodicAction.FrequencyType;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.problem.CEC2009.UF1;

/**
 * Periodic action is one means to extend the functionality of an existing algorithm.  It will perform some operation
 * at a fixed frequency.  In this example, we create a periodic action that adds randomness to the population.
 * 
 * Tip: As demonstrated below, prefer collecting all offspring and calling evaluateAll(offspring) once, as that
 * enables parallelizing function evaluations.
 */
public class PeriodicActionExample {
	
	public static void main(String[] args) {
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
	}
	
}
