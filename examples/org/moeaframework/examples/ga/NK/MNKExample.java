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
package org.moeaframework.examples.ga.NK;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.comparator.LexicographicalComparator;
import org.moeaframework.core.population.NondominatedPopulation;

/**
 * Example of binary optimization on the single or multi-objective NK-landscape
 * problem.
 */
public class MNKExample {
	
	public static void main(String[] args) {
		MNKProblem problem = new MNKProblem(20, 4);
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(100000);
		
		NondominatedPopulation result = algorithm.getResult();
		result.sort(new LexicographicalComparator());
		result.display();
	}

}
