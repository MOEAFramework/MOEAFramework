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
package org.moeaframework.examples.ga.onemax;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The one-max problem for maximizing the number of {@code 1} bits in a binary
 * string.  The one-max problem is trivial for most GAs, and is often used to
 * measure the convergence speed of different crossover and mutation operators.
 */
public class OneMax extends AbstractProblem {

	/**
	 * The number of bits in this OneMax problem instance.
	 */
	private final int numberOfBits;

	/**
	 * Constructs the one-max problem with the specified number of bits.
	 * 
	 * @param numberOfBits the number of bits in this instance
	 */
	public OneMax(int numberOfBits) {
		super(1, 1);
		this.numberOfBits = numberOfBits;
	}

	@Override
	public void evaluate(Solution solution) {
		BinaryVariable binary = (BinaryVariable)solution.getVariable(0);
		solution.setObjective(0, numberOfBits - binary.cardinality());
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new BinaryVariable(numberOfBits));
		return solution;
	}

}
