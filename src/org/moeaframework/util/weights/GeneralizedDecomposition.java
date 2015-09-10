/* Copyright 2009-2015 David Hadka
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

import org.moeaframework.core.FrameworkException;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.LPOptimizationRequest;
import com.joptimizer.optimizers.LPPrimalDualMethod;

/**
 * Generates weights used to find solutions near a given set of target points.
 * Generalized decomposition determines the Chebychev scalarizing function
 * weights that are used to attain solutions near the given target points.
 * <p>
 * References:
 * <ol>
 *   <li>Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013).
 *       "Generalized Decomposition."  Evolutionary Multi-Criterion
 *       Optimization, 7th International Conference, pp. 428-442.
 * </ol>
 */
public class GeneralizedDecomposition implements WeightGenerator {
	
	/**
	 * The target points normalized on the unit hyperplane.
	 */
	private final List<double[]> targets;
	
	/**
	 * Constructs a weight generator transforming the given weights into those
	 * defined by generalized decomposition.
	 * 
	 * @param generator the weight generator
	 */
	public GeneralizedDecomposition(WeightGenerator generator) {
		this(generator.generate());
	}
	
	/**
	 * Constructs a weight generator transforming the given target points into
	 * the required Chebychef scalaring function weights.
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

	private static double[] solve_chebychev(double[] x) {
		// This function uses JOptimizer to solve the following convex
		// optimization problem:
		//
		//   minimize norm_inf(X*w)
		//   subject to
		//      sum(w) = 1
		//      0 <= w <= 1
		//   where
		//      X is an n x n diagonal matrix
		//      w is a n x 1 vector
		//
		// norm_inf(X*w) is solved by introducing a slack variable, t, and 
		// adding constraints X*w <= t and X*w >= -t.
		
		// setup the standard form matrices for LP
		double[] c = new double[x.length + 1];
		double[][] G = new double[4 * x.length][x.length + 1];
		double[] h = new double[4 * x.length];
		double[][] A = new double[1][x.length + 1];
		double[] b = new double[1];
		
		c[0] = 1.0;
		b[0] = 1.0;
		
		for (int i = 0; i < x.length; i++) {
			// inequality constraint for x*w <= t
			G[i][0] = -1.0;
			G[i][i+1] = x[i];
			h[i] = 0.0;
			
			// inequality constraint for x*w >= -t
			G[i+x.length][0] = -1.0;
			G[i+x.length][i+1] = -x[i];
			h[i+x.length] = 0.0;
			
			// inequality constraint for w >= 0
			G[i+2*x.length][i+1] = -1.0;
			h[i+2*x.length] = 0.0;
			
			// inequality constraint for w <= 1
			G[i+3*x.length][i+1] = 1.0;
			h[i+3*x.length] = 1.0;
			
			// equality constraint for sum(w) = 1
			A[0][i+1] = 1.0;
		}
		
		// use JOptimizer to solve the LP
		ConvexMultivariateRealFunction[] inequalities =
				new ConvexMultivariateRealFunction[4 * x.length];
		
		for (int i = 0; i < 4 * x.length; i++) {
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
		
		LPOptimizationRequest or = new LPOptimizationRequest();
		or.setC(c);
		or.setG(G);
		or.setH(h);
		or.setA(A);
		or.setB(b);
		or.setMaxIteration(10000);
		or.setTolerance(0.001);
		or.setToleranceFeas(0.001);
				
		LPPrimalDualMethod opt = new LPPrimalDualMethod();
		opt.setLPOptimizationRequest(or);
		
		try {
			opt.optimize();
		} catch (Exception e) {
			throw new FrameworkException("Generalized decomposition failed", e);
		}
		
		// remove the slack variable from solution
		double[] solution = opt.getOptimizationResponse().getSolution();
		return Arrays.copyOfRange(solution, 1, x.length+1);
	}

}
