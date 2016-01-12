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

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * The biobjective leading ones, trailing zeros (LOTZ) problem.
 */
public class LOTZ extends AbstractProblem {
	
	/**
	 * The length of the bit string.
	 */
	protected final int numberOfBits;

	/**
	 * Constructs an instance of the LOTZ problem with the specified number of
	 * bits.
	 * 
	 * @param numberOfBits the number of bits
	 */
	public LOTZ(int numberOfBits) {
		super(1, 2);
		this.numberOfBits = numberOfBits;
	}

	@Override
	public void evaluate(Solution solution) {
		int ones = 0;
		int zeros = 0;
		boolean[] bits = EncodingUtils.getBinary(solution.getVariable(0));
		
		// count the number of leading ones
		for (int i = 0; i < bits.length; i++) {
			if (bits[i]) {
				ones++;
			} else {
				break;
			}
		}
		
		// count the number of tailing zeros
		for (int i = bits.length-1; i >= 0; i--) {
			if (bits[i]) {
				break;
			} else {
				zeros++;
			}
		}
		
		// subtract from numberOfBits to convert to minimization problem
		solution.setObjective(0, numberOfBits - ones);
		solution.setObjective(1, numberOfBits - zeros);
	}
	
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);
		solution.setVariable(0, EncodingUtils.newBinary(numberOfBits));
		return solution;
	}
	
}
