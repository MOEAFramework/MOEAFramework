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
package org.moeaframework.examples.problem.single;

import java.io.IOException;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.algorithm.single.RepeatedSingleObjective;
import org.moeaframework.core.comparator.MinMaxDominanceComparator;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Another way to solve multi-objective problems using single-objective optimizers is using a process called Repeated
 * Single Objective (RSO).  It produces solutions across the Pareto front by running multiple single-objective
 * optimizers using different weights.
 */
public class RepeatedSingleObjectiveExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		
		RepeatedSingleObjective algorithm = new RepeatedSingleObjective(problem, 50,
				(p, w) -> {
					GeneticAlgorithm weightedInstance = new GeneticAlgorithm(p);
					weightedInstance.setComparator(new MinMaxDominanceComparator(w));
					return weightedInstance;
				});
		
		algorithm.run(100000);
		algorithm.getResult().display();
	}

}
