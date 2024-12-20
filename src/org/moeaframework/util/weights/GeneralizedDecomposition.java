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
package org.moeaframework.util.weights;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

/**
 * Implements generalized decomposition, whereby the weights for the Chebychev scalarizing function are optimized to
 * attain points near given targets.
 * <p>
 * References:
 * <ol>
 *   <li>Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013).  "Generalized Decomposition."  Evolutionary
 *       Multi-Criterion Optimization, 7th International Conference, pp. 428-442.
 * </ol>
 */
public class GeneralizedDecomposition implements WeightGenerator {
	
	/**
	 * The target points normalized on the unit hyperplane.
	 */
	private final List<double[]> targets;
	
	/**
	 * Constructs a weight generator transforming the given weights into those defined by generalized decomposition.
	 * 
	 * @param generator the weight generator
	 */
	public GeneralizedDecomposition(WeightGenerator generator) {
		this(generator.generate());
	}
	
	/**
	 * Constructs a weight generator transforming the given target points into those defined by generalized
	 * decomposition.
	 * 
	 * @param targets the target points normalized on the unit hypervolume
	 */
	public GeneralizedDecomposition(List<double[]> targets) {
		super();
		this.targets = targets;
	}

	@Override
	public int size() {
		return targets.size();
	}

	@Override
	public List<double[]> generate() {
		List<double[]> result = new ArrayList<double[]>();
		
		for (double[] target : targets) {
			result.add(solve_chebychev(target));
		}
		
		return result;
	}

	/**
	 * Finds the Chebychev scalarizing function weights to attain a point near the given target point.
	 * 
	 * The weights are the solution to the convex optimization problem:
	 * <pre>
	 *   minimize norm_inf(X*w)
	 *   subject to
	 *     sum(w) = 1
	 *     0 <= w <= 1
	 *   where
	 *     X is an n x n diagonal matrix
	 *     w is a n x 1 vector
	 * </pre>
	 * 
	 * 
	 * {@code norm_inf(X*w)} is solved by introducing a slack variable, {@code t}, which is minimized instead.  This
	 * requires the addition of the constraints {@code X*w <= t} and {@code X*w >= -t}.
	 * 
	 * @param x the target point
	 * @return the weights
	 */
	private static double[] solve_chebychev(double[] x) {
		double[] functionCoefficients = new double[x.length + 1];
		functionCoefficients[x.length] = 1.0;
		
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		
		// sum of weights must equal 1.0
		{
			double[] coefficients = new double[x.length + 1];
			
			for (int i = 0; i < x.length; i++) {
				coefficients[i] = 1.0;
			}
			
			constraints.add(new LinearConstraint(coefficients, Relationship.EQ, 1.0));
		}
		
		for (int i = 0; i < x.length; i++) {
			// each weight must be >= 0
			{
				double[] coefficients = new double[x.length + 1];
				coefficients[i] = 1.0;
				constraints.add(new LinearConstraint(coefficients, Relationship.GEQ, 0.0));
			}
			
			// each weight must be <= 1
			{
				double[] coefficients = new double[x.length + 1];
				coefficients[i] = 1.0;
				constraints.add(new LinearConstraint(coefficients, Relationship.LEQ, 1.0));
			}
			
			// x[i]*w[i] <= t
			{
				double[] coefficients = new double[x.length + 1];
				coefficients[i] = x[i];
				coefficients[x.length] = -1.0;
				constraints.add(new LinearConstraint(coefficients, Relationship.LEQ, 0.0));
			}
			
			// x[i]*x[i] >= -t
			{
				double[] coefficients = new double[x.length + 1];
				coefficients[i] = x[i];
				coefficients[x.length] = 1.0;
				constraints.add(new LinearConstraint(coefficients, Relationship.GEQ, 0.0));
			}
		}
		
		PointValuePair solution = new SimplexSolver().optimize(
				new LinearObjectiveFunction(functionCoefficients, 0.0),
				new LinearConstraintSet(constraints),
				GoalType.MINIMIZE,
				new NonNegativeConstraint(false));

		return Arrays.copyOfRange(solution.getPoint(), 0, x.length);
	}

}