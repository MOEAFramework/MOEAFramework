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
package org.moeaframework.algorithm.single;

import java.io.Serializable;

import org.moeaframework.core.Solution;
import org.moeaframework.util.Vector;

/**
 * The vector angle distance scaling aggregate function.  The distance between
 * the origin and the objective vector is scaled by the cosine angle between the
 * objective vector and a target vector.
 * <p>
 * References:
 * <ol>
 *   <li>E. J. Hughes.  "Multiple Single Objective Pareto Sampling."  2003
 *       Congress on Evolutionary Computation, pp. 2678-2684.
 * </ol>
 */
public class VectorAngleDistanceScalingComparator implements AggregateObjectiveComparator, Serializable {

	private static final long serialVersionUID = -2535092560377062714L;

	/**
	 * Factor for scaling the effects of the angle.
	 */
	private final double q;
	
	/**
	 * The weight vector.  Must have a magnitude of 1.0.
	 */
	private final double[] weights;
	
	/**
	 * Constructs a new instance of the vector angle distance scaling aggregate
	 * function with the given weights.
	 * 
	 * @param weights the weight vector; must have a magnitude of 1.0
	 */
	public VectorAngleDistanceScalingComparator(double[] weights) {
		this(weights, 100.0);
	}
	
	/**
	 * Constructs a new instance of the vector angle distance scaling aggregate
	 * function with the given weights.
	 * 
	 * @param weights the weight vector; must have a magnitude of 1.0
	 * @param q factor for scaling the effects of the angle
	 */
	public VectorAngleDistanceScalingComparator(double[] weights, double q) {
		super();
		this.weights = weights;
		this.q = q;
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		double fitness1 = calculateFitness(solution1, weights, q);
		double fitness2 = calculateFitness(solution2, weights, q);
		
		return Double.compare(fitness1, fitness2);
	}

	/**
	 * Computes the vector angle distance scaling aggregate fitness of the
	 * solution.  One weight should be given for each objective.
	 * 
	 * @param solution the solution
	 * @param weights the weight vector
	 * @param q factor for scaling the effects of the angle
	 * @return the fitness, where smaller values are preferred
	 */
	public static double calculateFitness(Solution solution, double[] weights, double q) {
		double[] objectives = solution.getObjectives();
		double magnitude = Vector.magnitude(objectives);
		double cosine = Vector.dot(weights, Vector.divide(objectives, magnitude));
		
		// prevent numerical error
		if (cosine > 1.0) {
			cosine = 1.0;
		}
		
		return magnitude / (Math.pow(cosine, q)+0.01);
	}

	
}
