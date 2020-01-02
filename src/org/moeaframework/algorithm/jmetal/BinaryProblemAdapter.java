/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.algorithm.jmetal;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;

public class BinaryProblemAdapter extends ProblemAdapter<BinarySolution> implements BinaryProblem {

	private static final long serialVersionUID = -7944545872958727275L;
	
	private final Solution schema;
	
	private final int totalNumberOfBits;
	
	public BinaryProblemAdapter(Problem problem) {
		super(problem);
		schema = problem.newSolution();
		
		// count the total number of bits
		int numberOfBits = 0;
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			numberOfBits += ((BinaryVariable)schema.getVariable(i)).getNumberOfBits();
		}
		
		totalNumberOfBits = numberOfBits;
	}
	
	@Override
	public int getNumberOfBits(int index) {
		return ((BinaryVariable)schema.getVariable(index)).getNumberOfBits();
	}

	@Override
	public int getTotalNumberOfBits() {
		return totalNumberOfBits;
	}
	
	@Override
	public BinarySolution createSolution() {
		return new DefaultBinarySolution(this);
	}
	
	@Override
	public Solution convert(BinarySolution solution) {
		Solution result = getProblem().newSolution();
		
		for (int i = 0; i < getNumberOfVariables(); i++) {
			EncodingUtils.setBitSet(result.getVariable(i), solution.getVariableValue(i));
		}
		
		return result;
	}
	
	@Override
	public int getNumberOfMutationIndices() {
		return getTotalNumberOfBits();
	}

}
