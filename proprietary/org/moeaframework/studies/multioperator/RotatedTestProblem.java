/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.studies.multioperator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

public class RotatedTestProblem extends MultioperatorTestProblem {
	
	private final MultioperatorTestProblem problem;
	
	private final double r;
	
	public RotatedTestProblem(MultioperatorTestProblem problem) {
		this(problem, Math.PI/4.0);
	}
	
	public RotatedTestProblem(MultioperatorTestProblem problem, double r) {
		super();
		this.problem = problem;
		this.r = r;
	}

	public double getR() {
		return r;
	}

	@Override
	public void evaluate(Solution solution) {
		Solution rotatedSolution = rotate(solution);
		problem.evaluate(rotatedSolution);
		solution.setObjectives(rotatedSolution.getObjectives());
	}

	@Override
	public String getName() {
		return "Rotated " + problem.getName();
	}

	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}

	@Override
	public Solution newSolution() {
		return problem.newSolution();
	}
	
	private Solution rotate(Solution solution) {
		Solution rsolution = new Solution(2, 1);
		
		RealVariable x = (RealVariable)solution.getVariable(0);
		RealVariable y = (RealVariable)solution.getVariable(1);
		
		RealVariable rx = new RealVariable( 
				(x.getValue() - 0.5)*Math.cos(r) - (y.getValue() - 0.5)*Math.sin(r) + 0.5,
				-Math.sqrt(2), Math.sqrt(2));
		RealVariable ry = new RealVariable(
				(x.getValue() - 0.5)*Math.sin(r) + (y.getValue() - 0.5)*Math.cos(r) + 0.5,
				-Math.sqrt(2), Math.sqrt(2));
		
		rsolution.setVariable(0, rx);
		rsolution.setVariable(1, ry);
		
		return rsolution;
	}

	@Override
	public boolean isOptimum(Solution solution, double epsilon) {
		Solution rotatedSolution = rotate(solution);
		return problem.isOptimum(rotatedSolution, epsilon);		
	}

}
