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
package org.moeaframework.examples.LOTZ;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.comparator.LexicographicalComparator;
import org.moeaframework.core.population.NondominatedPopulation;

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
		LOTZ problem = new LOTZ(10);
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		NondominatedPopulation result = algorithm.getResult();
		result.sort(new LexicographicalComparator());
		result.display();
	}

}
