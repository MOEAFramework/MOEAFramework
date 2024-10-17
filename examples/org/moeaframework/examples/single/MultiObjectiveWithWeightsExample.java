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
package org.moeaframework.examples.single;

import java.io.IOException;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.comparator.LinearDominanceComparator;
import org.moeaframework.core.comparator.MinMaxDominanceComparator;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates solving a multi-objective problem using a single-objective optimizer by supplying weights.
 * Linear weights tend to result in solutions near the boundary, whereas min-max weights tends to find
 * intermediate solutions on the Pareto front.
 */
public class MultiObjectiveWithWeightsExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		
		System.out.println("Linear weights:");
		GeneticAlgorithm algorithm1 = new GeneticAlgorithm(problem);
		algorithm1.setComparator(new LinearDominanceComparator(0.75, 0.25));
		algorithm1.run(100000);
		algorithm1.getResult().display();
		
		System.out.println();
		
		System.out.println("Min-Max weights:");
		GeneticAlgorithm algorithm2 = new GeneticAlgorithm(problem);
		algorithm2.setComparator(new MinMaxDominanceComparator(0.75, 0.25));
		algorithm2.run(100000);
		algorithm2.getResult().display();
	}

}
