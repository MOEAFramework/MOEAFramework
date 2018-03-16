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
package org.moeaframework.core.operator.real;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

/**
 * The adaptive metropolis (AM) operator.  AM is a multiparent operator,
 * allowing a user-defined number of parents and offspring.  AM produces
 * normally-distributed clusters around each parent, where the shape of the
 * distribution is controlled by the covariance of the parents.
 * <p>
 * Internally, the Cholesky decomposition is used to update the resulting
 * offspring distribution.  Cholesky decomposition requires that its input be
 * positive definite.  In order to guarantee this condition is satisfied, all
 * parents must be unique.  In the event that the positive definite condition
 * is not satisifed, no offspring are produced and an empty array is returned
 * by {@link #evolve(Solution[])}.
 * <p>
 * References:
 * <ol>
 *   <li>Vrugt, J.A., Robinson, B.A. and Hyman, J.M.  "Self-Adaptive 
 *       MultiMethod Search For Global Optimization in Real-Parameter Spaces."
 *       IEEE Transactions on Evolutionary Computation, pp. 1-17, 2009.
 *   <li>Vrugt, J.A. and Robinson, B.A.  "Improved Evolutionary Optimization 
 *       from Genetically Adaptive Multimethod Search."  Proceedings of the 
 *       National Academy of Sciences of the United States  of America, vol. 
 *       104, pp. 708 - 711, 2007.
 *   <li>Gelman, A., Roberts, G.O. and Gilks, W.R.  "Efficient Metropolis
 *       Jumping Rules."  Bayesian Statistics, vol. 5, pp. 599-607, 1996.
 * </ol>
 */
public class AdaptiveMetropolis implements Variation {
	
	/**
	 * The number of parents required by this operator.
	 */
	private final int numberOfParents;

	/**
	 * The number of offspring produced by this operator.
	 */
	private final int numberOfOffspring;
	
	/**
	 * The jump rate coefficient, controlling the standard deviation of the 
	 * covariance matrix.  The actual jump rate is calculated as {@code 
	 * Math.pow(jumpRateCoefficient / Math.sqrt(n), 2.0)}, where  {@code n} is
	 * the number of decision variables.  The recommended value is {@code 2.4}.
	 */
	private final double jumpRateCoefficient;
	
	/**
	 * Constructs an adaptive metropolis operator.
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of parents produced by this operator
	 * @param jumpRateCoefficient the jump raote coefficient, controlling the
	 *        standard deviation of the covariance matrix
	 */
	public AdaptiveMetropolis(int numberOfParents, int numberOfOffspring, 
			double jumpRateCoefficient) {
		super();
		this.numberOfParents = numberOfParents;
		this.numberOfOffspring = numberOfOffspring;
		this.jumpRateCoefficient = jumpRateCoefficient;
	}

	@Override
	public int getArity() {
		return numberOfParents;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		int k = parents.length;
		int n = parents[0].getNumberOfVariables();
		RealMatrix x = new Array2DRowRealMatrix(k, n);
		
		for (int i=0; i<k; i++) {
			x.setRow(i, EncodingUtils.getReal(parents[i]));
		}
		
		try {
			//perform Cholesky factorization and get the upper triangular matrix
			double jumpRate = Math.pow(jumpRateCoefficient / Math.sqrt(n), 2.0);

			RealMatrix chol = new CholeskyDecomposition(
						new Covariance(x.scalarMultiply(jumpRate))
						.getCovarianceMatrix()).getLT();
			
			//produce the offspring
			Solution[] offspring = new Solution[numberOfOffspring];
			
			for (int i=0; i<numberOfOffspring; i++) {
				Solution child = parents[PRNG.nextInt(parents.length)].copy();
				
				//apply adaptive metropolis step to solution
				RealVector muC = new ArrayRealVector(
						EncodingUtils.getReal(child));
				RealVector ru = new ArrayRealVector(n);
				
				for (int j=0; j<n; j++) {
					ru.setEntry(j, PRNG.nextGaussian());
				}
				
				double[] variables = muC.add(chol.preMultiply(ru)).toArray();
				
				//assign variables back to solution
				for (int j=0; j<n; j++) {
					RealVariable variable = (RealVariable)child.getVariable(j);
					double value = variables[j];

					if (value < variable.getLowerBound()) {
						value = variable.getLowerBound();
					} else if (value > variable.getUpperBound()) {
						value = variable.getUpperBound();
					}

					variable.setValue(value);
				}
				
				offspring[i] = child;
			}
			
			return offspring;
		} catch (Exception e) {
			return new Solution[0];
		}
	}

}
