/* Copyright 2009-2018 David Hadka
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

import org.apache.commons.math3.linear.RealMatrix;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.RotationMatrixBuilder;
import org.moeaframework.util.Vector;

/**
 * Decorator to create rotated instances of test problems.  The rotation is
 * defined by a rotation matrix, which should be orthogonal and have a
 * determinant of 1.  See {@link RotationMatrixBuilder} for a utility to quickly
 * construct rotation matrices.
 * <p>
 * Regardless of the rotation, all rotated instances use expanded lower and
 * upper bounds for the decision variables.  An additional constraint is added
 * to account for solutions existing in this expanded region, which are not in
 * the original unrotated problem.  This expansion is consistent across all
 * rotations, which ensures the volume of the constraint-violating expanded
 * region is constant across all instances of a problem.
 */
public class RotatedProblem implements Problem {
	
	/**
	 * The original unrotated problem.
	 */
	private final Problem problem;
	
	/**
	 * The rotation matrix.
	 */
	private final RealMatrix rotation;
	
	/**
	 * The expanded lower bounds.
	 */
	private final double[] lowerBounds;
	
	/**
	 * The expanded upper bounds.
	 */
	private final double[] upperBounds;
	
	/**
	 * The center of the search space about which the rotation occurs.
	 */
	private final double[] center;
	
	/**
	 * Decorates the specified problem, creating a rotated instance using the
	 * specified rotation matrix.
	 * 
	 * @param problem the original unrotated problem
	 * @param rotation the rotation matrix
	 */
	public RotatedProblem(Problem problem, RealMatrix rotation) {
		super();
		this.problem = problem;
		this.rotation = rotation;
		
		//calculate the expanded lower and upper bounds
		Solution solution = problem.newSolution();
		
		center = new double[getNumberOfVariables()];
		lowerBounds = new double[getNumberOfVariables()];
		upperBounds = new double[getNumberOfVariables()];
		
		for (int i = 0; i < getNumberOfVariables(); i++) {
			RealVariable variable = (RealVariable)solution.getVariable(i);
			
			center[i] = (variable.getLowerBound() + variable.getUpperBound()) /
					2.0;
			lowerBounds[i] = Math.sqrt(2.0) *
					(variable.getLowerBound() - center[i]);
			upperBounds[i] = Math.sqrt(2.0) *
					(variable.getUpperBound() - center[i]);
		}
	}

	@Override
	public String getName() {
		return "Rotated " + problem.getName();
	}

	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints() + 1;
	}

	@Override
	public void evaluate(Solution solution) {
		Solution temp = problem.newSolution();
		
		//apply the rotation
		double[] x = EncodingUtils.getReal(solution);
		x = rotation.operate(x);
		x = Vector.add(x, center);
		
		//calculate the bounds violation
		double boundsViolation = 0.0;
		
		for (int i=0; i<getNumberOfVariables(); i++) {
			RealVariable variable = (RealVariable)temp.getVariable(i);
			
			if (x[i] < variable.getLowerBound()) {
				boundsViolation += variable.getLowerBound() - x[i];
				variable.setValue(variable.getLowerBound());
			} else if (x[i] > variable.getUpperBound()) {
				boundsViolation += x[i] - variable.getUpperBound();
				variable.setValue(variable.getUpperBound());
			} else {
				variable.setValue(x[i]);
			}
		}
		
		//evaluate the solution
		problem.evaluate(temp);
		
		//extract the results
		solution.setObjectives(temp.getObjectives());
		
		for (int i = 0; i < problem.getNumberOfConstraints(); i++) {
			solution.setConstraint(i, temp.getConstraint(i));
		}
		
		//set the bounds violation constraint
		solution.setConstraint(problem.getNumberOfConstraints(), 
				boundsViolation);
	}

	@Override
	public Solution newSolution() {
		Solution result = new Solution(getNumberOfVariables(), 
				getNumberOfObjectives(), getNumberOfConstraints());
		
		for (int i = 0; i < getNumberOfVariables(); i++) {
			result.setVariable(i, new RealVariable(lowerBounds[i], 
					upperBounds[i]));
		}
		
		return result;
	}

	@Override
	public void close() {
		problem.close();
	}

}
