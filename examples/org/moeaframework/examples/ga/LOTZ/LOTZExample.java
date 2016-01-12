/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.ga.LOTZ;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.LexicographicalComparator;

/**
 * Example of binary optimization on the {@link LOTZ} problem.  The goal of this
 * problem is an enumeration of bit strings that begin with some number of
 * {@code 1} bits (leading ones), and the remaining bits are {@code 0}
 * (trailing zeros).  For example, with four bits, the ideal results is:
 * <pre>
 *   1111
 *   1110
 *   1100
 *   1000
 *   0000
 * </pre>
 */
public class LOTZExample {

	public static void main(String[] args) {
		// solve the LOTZ problem with 10 bits
		NondominatedPopulation result = new Executor()
				.withProblemClass(LOTZ.class, 10)
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.run();

		// sort the results so the solutions appear in order
		result.sort(new LexicographicalComparator());
		
		// print the bit string and the objective values
		for (Solution solution : result) {
			System.out.println(solution.getVariable(0) + " " + 
					solution.getObjective(0) + " " + 
					solution.getObjective(1));
		}
	}

}
