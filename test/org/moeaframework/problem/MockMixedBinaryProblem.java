/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.problem;

import java.util.BitSet;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * A mock problem with a binary variable and a binary-encoded integer variable.
 */
public class MockMixedBinaryProblem extends AbstractProblem {

	public MockMixedBinaryProblem() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		BitSet binaryValue = EncodingUtils.getBitSet(solution.getVariable(0));
		int intValue = EncodingUtils.getInt(solution.getVariable(1));
		
		solution.setObjective(0, 10 - binaryValue.cardinality());
		solution.setObjective(1, intValue);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);
		solution.setVariable(0, new BinaryVariable(10));
		solution.setVariable(1, new BinaryIntegerVariable(5, 10));
		return solution;
	}

}
