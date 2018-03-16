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

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.Vector;

/**
 * Unimodal Normal Distribution Crossover (UNDX) operator.  UNDX is a 
 * multiparent operator, allowing a user-defined number of parents and
 * offspring.  Offspring are centered around the centroid, forming a normal 
 * distribution whose shape is controlled by the positions of the parents, as 
 * depicted in the figure below.
 * <p>
 * <img src="doc-files/UNDX-1.png" alt="Example UNDX operator distribution" />
 * <p>
 * References:
 * <ol>
 * <li>Kita, H., Ono, I., and Kobayashi, S., "Multi-parental Extension of the
 * Unimodal Normal Distribution Crossover for Real-coded Genetic Algorithms,"
 * Proceedings of the 1999 Congress on Evolutionary Computation, pp. 1581-1588,
 * 1999.
 * <li>Deb, K., Anand, A., and Joshi, D., "A Computationally Efficient
 * Evolutionary Algorithm for Real-Parameter Optimization," Evolutionary
 * Computation, vol. 10, no. 4, pp. 371-395, 2002.
 * </ol>
 */
public class UNDX implements Variation {

	/**
	 * The number of parents required by this operator.
	 */
	private final int numberOfParents;

	/**
	 * The number of offspring produced by this operator.
	 */
	private final int numberOfOffspring;

	/**
	 * The standard deviation of the normal distribution controlling the spread
	 * of solutions in the orthogonal directions defined by the parents.
	 */
	private final double zeta;

	/**
	 * The standard deviation of the normal distribution controlling the spread
	 * of solutions in the remaining orthogonal directions not defined by the
	 * parents. This value is divided by {@code Math.sqrt(n)} prior to use,
	 * where {@code n} is the number of decision variables.
	 */
	private final double eta;

	/**
	 * Constructs a UNDX operator with the specified number of parents and 
	 * offspring. The parameters {@code zeta=0.5} and {@code eta=0.35} are used
	 * as suggested by Kita et al. (1999).
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of offspring produced by this
	 *        operator
	 */
	public UNDX(int numberOfParents, int numberOfOffspring) {
		this(numberOfParents, numberOfOffspring, 0.5, 0.35);
	}

	/**
	 * Constructs a UNDX operator with the specified number of parents and 
	 * offspring. The parameters {@code sigma_zeta=0.5} and
	 * {@code sigma_eta=0.35} are used as suggested by Kita et al. (1999).
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of offspring produced by this
	 *        operator
	 * @param zeta the standard deviation of the normal distribution controlling
	 *        the spread of solutions in the orthogonal directions defined by
	 *        the parents
	 * @param eta the standard deviation of the normal distribution controlling
	 *        the spread of solutions in the remaining orthogonal directions not
	 *        defined by the parents
	 */
	public UNDX(int numberOfParents, int numberOfOffspring, double zeta,
			double eta) {
		this.numberOfParents = numberOfParents;
		this.numberOfOffspring = numberOfOffspring;
		this.zeta = zeta;
		this.eta = eta;
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
	 * Returns the standard deviation of the normal distribution controlling the
	 * spread of solutions in the orthogonal directions defined by the parents.
	 * 
	 * @return the standard deviation of the normal distribution controlling the
	 *         spread of solutions in the orthogonal directions defined by the
	 *         parents
	 */
	public double getZeta() {
		return zeta;
	}

	/**
	 * Returns the standard deviation of the normal distribution controlling the
	 * spread of solutions in the remaining orthogonal directions not defined by
	 * the parents.
	 * 
	 * @return the standard deviation of the normal distribution controlling the
	 *         spread of solutions in the remaining orthogonal directions not
	 *         defined by the parents
	 */
	public double getEta() {
		return eta;
	}

	@Override
	public int getArity() {
		return numberOfParents;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution[] result = new Solution[numberOfOffspring];

		for (int i = 0; i < numberOfOffspring; i++) {
			result[i] = undx(parents);
		}

		return result;
	}

	private double[] randomVector(int n) {
		double[] v = new double[n];

		for (int i = 0; i < n; i++) {
			v[i] = PRNG.nextGaussian();
		}

		return v;
	}

	/**
	 * Returns one randomly-generated offspring produced by this operator.
	 * 
	 * @param parents the parent solutions
	 * @return one randomly-generated offspring produced by this operator.
	 */
	public Solution undx(Solution[] parents) {
		if (parents.length < 2) {
			throw new IllegalArgumentException("requires at least 2 parents");
		}

		int k = parents.length;
		int n = parents[0].getNumberOfVariables();
		double[][] x = new double[k][n];

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < n; j++) {
				x[i][j] = ((RealVariable)parents[i].getVariable(j)).getValue();
			}
		}

		double[] g = Vector.mean(x);

		List<double[]> e_zeta = new ArrayList<double[]>();
		List<double[]> e_eta = new ArrayList<double[]>();

		// basis vectors defined by parents
		for (int i = 0; i < k - 1; i++) {
			double[] d = Vector.subtract(x[i], g);

			if (!Vector.isZero(d)) {
				double dbar = Vector.magnitude(d);
				double[] e = Vector.orthogonalize(d, e_zeta);

				if (!Vector.isZero(e)) {
					e_zeta.add(Vector.multiply(dbar, Vector.normalize(e)));
				}
			}
		}

		double D = Vector.magnitude(Vector.subtract(x[k - 1], g));

		// create the remaining basis vectors
		for (int i = 0; i < n - e_zeta.size(); i++) {
			double[] d = randomVector(n);

			if (!Vector.isZero(d)) {
				double[] e = Vector.orthogonalize(d, e_eta);

				if (!Vector.isZero(e)) {
					e_eta.add(Vector.multiply(D, Vector.normalize(e)));
				}
			}
		}

		// construct the offspring
		double[] variables = g;

		for (int i = 0; i < e_zeta.size(); i++) {
			variables = Vector.add(variables, Vector.multiply(PRNG
					.nextGaussian(0.0, zeta), e_zeta.get(i)));
		}

		for (int i = 0; i < e_eta.size(); i++) {
			variables = Vector.add(variables, Vector.multiply(PRNG
					.nextGaussian(0.0, eta / Math.sqrt(n)), e_eta.get(i)));
		}

		Solution result = parents[k - 1].copy();

		for (int j = 0; j < n; j++) {
			RealVariable variable = (RealVariable)result.getVariable(j);
			double value = variables[j];

			if (value < variable.getLowerBound()) {
				value = variable.getLowerBound();
			} else if (value > variable.getUpperBound()) {
				value = variable.getUpperBound();
			}

			variable.setValue(value);
		}

		return result;
	}

}
