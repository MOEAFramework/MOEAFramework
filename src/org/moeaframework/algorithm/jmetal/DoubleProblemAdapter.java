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
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

public class DoubleProblemAdapter extends ProblemAdapter<DoubleSolution> implements DoubleProblem {

	private static final long serialVersionUID = 4011361659496044697L;
	
	private Solution schema;
	
	public DoubleProblemAdapter(Problem problem) {
		super(problem);
		schema = problem.newSolution();
	}
	
	@Override
	public Double getLowerBound(int index) {
		return ((RealVariable)schema.getVariable(index)).getLowerBound();
	}

	@Override
	public Double getUpperBound(int index) {
		return ((RealVariable)schema.getVariable(index)).getUpperBound();
	}

	@Override
	public DoubleSolution createSolution() {
		return new DefaultDoubleSolution(this);
	}
	
	@Override
	public Solution convert(DoubleSolution solution) {
		Solution result = getProblem().newSolution();
		
		for (int i = 0; i < getNumberOfVariables(); i++) {
			EncodingUtils.setReal(result.getVariable(i), solution.getVariableValue(i));
		}
		
		return result;
	}

}
