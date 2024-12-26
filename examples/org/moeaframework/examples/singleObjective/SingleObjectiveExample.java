/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.examples.singleObjective;

import java.io.IOException;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates solving a single-objective problem using a genetic algorithm.
 */
public class SingleObjectiveExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new Rosenbrock();
		
		GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
		algorithm.run(100000);
		
		algorithm.getResult().display();
	}

}
