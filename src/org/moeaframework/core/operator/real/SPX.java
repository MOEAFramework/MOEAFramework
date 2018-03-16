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

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Simplex crossover (SPX) operator.  SPX is a multiparent operator, allowing a
 * user-defined number of parents and offspring.  The parents form a convex 
 * hull, called a simplex.  Offspring are generated uniformly at random from 
 * within the simplex.  The expansion rate parameter can be used to expand the
 * size of the simplex beyond the bounds of the parents.  For example, the 
 * figure below shows three parent points and the offspring distribution, 
 * clearly filling an expanded triangular simplex.
 * <p>
 * <img src="doc-files/SPX-1.png" alt="Example SPX operator distribution" />
 * <p>
 * References:
 * <ol>
 * <li>Tsutsui, S., Yamamura, M., and Higuchi, T., "Multi-parent Recombination
 * with Simplex Crossover in Real Coded Genetic Algorithms," Proceedings of the
 * Genetic and Evolutionary Computation Conference, vol. 1, pp. 657-664, 1999.
 * <li>Higuchi, T., Tsutsui, S., and Yamamura, M., "Theoretical Analysis of
 * Simplex Crossover for Real-Coded Genetic Algorithms," Parallel Problem
 * Solving from Nature PPSN VI, pp. 365-374, 2000.
 * </ol>
 */
public class SPX implements Variation {

	/**
	 * The number of parents required by this operator.
	 */
	private final int numberOfParents;

	/**
	 * The number of offspring produced by this operator.
	 */
	private final int numberOfOffspring;

	/**
	 * The expansion rate of this operator.
	 */
	private final double epsilon;

	/**
	 * Constructs a SPX operator with the specified number of parents and 
	 * number of offspring. The expansion rate is set to
	 * {@code sqrt(numberOfParents+1)} to preserve the covariance matrix of the
	 * population.
	 * 
	 * @param numberOfParents the number of parents
	 * @param numberOfOffspring the number of offspring
	 */
	public SPX(int numberOfParents, int numberOfOffspring) {
		this(numberOfParents, numberOfOffspring, Math.sqrt(numberOfParents + 1));
	}

	/**
	 * Constructs a simplex operator with the specified number of parents, 
	 * number of offspring, and expansion rate.
	 * 
	 * @param numberOfParents the number of parents
	 * @param numberOfOffspring the number of offspring
	 * @param epsilon the expansion rate
	 */
	public SPX(int numberOfParents, int numberOfOffspring, double epsilon) {
		this.numberOfParents = numberOfParents;
		this.numberOfOffspring = numberOfOffspring;
		this.epsilon = epsilon;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		int n = parents.length;
		int m = parents[0].getNumberOfVariables();

		double[] G = new double[m]; // center of mass
		double[][] x = new double[n][m]; // expanded simplex vertices
		double[] r = new double[n - 1]; // random numbers
		double[][] C = new double[n][m]; // random offset vectors
		Solution[] offspring = new Solution[numberOfOffspring];

		// compute center of mass
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				G[j] += ((RealVariable)parents[i].getVariable(j)).getValue();
			}
		}

		for (int j = 0; j < m; j++) {
			G[j] /= n;
		}

		// compute simplex vertices expanded by epsilon
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				x[i][j] = G[j]
						+ epsilon
						* (((RealVariable)parents[i].getVariable(j)).getValue() - G[j]);
			}
		}

		// generate offspring
		for (int k = 0; k < numberOfOffspring; k++) {
			Solution child = parents[n - 1].copy();

			for (int i = 0; i < n - 1; i++) {
				r[i] = Math.pow(PRNG.nextDouble(), 1.0 / (i + 1.0));
			}

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (i == 0) {
						C[i][j] = 0;
					} else {
						C[i][j] = r[i - 1]
								* (x[i - 1][j] - x[i][j] + C[i - 1][j]);
					}
				}
			}

			for (int j = 0; j < m; j++) {
				RealVariable variable = (RealVariable)child.getVariable(j);
				double value = x[n - 1][j] + C[n - 1][j];

				if (value < variable.getLowerBound()) {
					value = variable.getLowerBound();
				} else if (value > variable.getUpperBound()) {
					value = variable.getUpperBound();
				}

				variable.setValue(value);
			}

			offspring[k] = child;
		}

		return offspring;
	}

	@Override
	public int getArity() {
		return numberOfParents;
	}

	/**
	 * Returns the number of parents required by this operator.
	 * 
	 * @return the number of parents required by this operator
	 */
	public int getNumberOfParents() {
		return numberOfParents;
	}

	/**
	 * Returns the number of offspring produced by this operator.
	 * 
	 * @return the number of offspring produced by this operator
	 */
	public int getNumberOfOffspring() {
		return numberOfOffspring;
	}

	/**
	 * Returns the expansion rate of this operator.
	 * 
	 * @return the expansion rate of this operator
	 */
	public double getEpsilon() {
		return epsilon;
	}

}
