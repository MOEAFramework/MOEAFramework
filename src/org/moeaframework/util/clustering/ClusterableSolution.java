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
package org.moeaframework.util.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.validate.Validate;

/**
 * {@link Clusterable} wrapper for a {@link Solution} that associates the point used for clustering with each solution.
 * The primary purpose of this wrapper is to allow defining different points depending on the context, such as
 * clustering based on decision variables or objective values.  Storing the point also avoids any unnecessary copying
 * or cloning of the underlying data.
 */
public class ClusterableSolution implements Clusterable {
	
	private final Solution solution;
	
	private final double[] point;

	/**
	 * Constructs a new clusterable solution.
	 * 
	 * @param solution the solution
	 * @param point the point used for clustering
	 */
	public ClusterableSolution(Solution solution, double[] point) {
		super();
		this.solution = solution;
		this.point = point;
	}
	
	/**
	 * Returns the solution.
	 * 
	 * @return the solution
	 */
	public Solution getSolution() {
		return solution;
	}

	/**
	 * Returns the point used for clustering.  This method is intended for internal use by the clustering algorithms.
	 * Do not modify the contents of the array, as it can result in undefined behavior!
	 * 
	 * @return the point
	 */
	@Override
	public double[] getPoint() {
		return point;
	}
	
	/**
	 * Constructs a clusterable solution based on the objective values of the solution.
	 * 
	 * @param solution the solution
	 * @return the clusterable solution based on the objective values
	 */
	public static ClusterableSolution withObjectives(Solution solution) {
		return new ClusterableSolution(solution, solution.getObjectives());
	}
	
	/**
	 * Constructs a clusterable solution based on the decision variables of the solution.  This only supports numeric
	 * decision variables, such as {@link RealVariable} and {@link BinaryIntegerVariable}, and will throw if any
	 * supported type is given.
	 * 
	 * @param solution the solution
	 * @return the clusterable solution based on the decision variables
	 * @throws IllegalArgumentException if any decision variables are not numeric
	 */
	public static ClusterableSolution withVariables(Solution solution) {
		double[] point = new double[solution.getNumberOfVariables()];
		
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			Variable variable = solution.getVariable(i);
			
			if (variable instanceof RealVariable realVariable) {
				point[i] = realVariable.getValue();
			} else if (variable instanceof BinaryIntegerVariable binaryIntegerVariable) {
				point[i] = binaryIntegerVariable.getValue();
			} else {
				Validate.that("solution", solution).fails("Clustering only supports real and integer types, given " +
						variable.getClass().getName());
			}
		}
		
		return new ClusterableSolution(solution, point);
	}

}
