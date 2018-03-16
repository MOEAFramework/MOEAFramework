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
 * Parent-centric crossover (PCX) operator.  PCX is a multiparent operator,
 * allowing a user-defined number of parents and offspring.  Offspring are
 * clustered around the parents, as depicted in the figure below.
 * <p>
 * <img src="doc-files/PCX-1.png" alt="Example PCX operator distribution" />
 * <p>
 * References:
 * <ol>
 * <li>Deb, K., Anand, A., and Joshi, D., "A Computationally Efficient
 * Evolutionary Algorithm for Real-Parameter Optimization," Evolutionary
 * Computation, 10(4):371-395, 2002.
 * </ol>
 */
public class PCX implements Variation {

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
	 * of solutions in the direction of the selected parent.
	 */
	private final double eta;

	/**
	 * The standard deviation of the normal distribution controlling the spread
	 * of solutions in the directions defined by the remaining parents.
	 */
	private final double zeta;

	/**
	 * Constructs a PCX operator with the specified number of parents and 
	 * offspring. The {@code eta} and {@code zeta} parameters are set to 
	 * {@code 0.1}, as suggested by Deb et al. (2002).
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of offspring produced by this
	 *        operator
	 */
	public PCX(int numberOfParents, int numberOfOffspring) {
		this(numberOfParents, numberOfOffspring, 0.1, 0.1);
	}

	/**
	 * Constructs a PCX operator with the specified number of parents and 
	 * offspring, and the specified values for {@code sigma_eta} and
	 * {@code sigma_zeta}.
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of offspring produced by this
	 *        operator
	 * @param eta the standard deviation of the normal distribution controlling
	 *        the spread of solutions in the direction of the selected parent
	 * @param zeta the standard deviation of the normal distribution controlling
	 *        the spread of solutions in the directions defined by the remaining
	 *        parents
	 */
	public PCX(int numberOfParents, int numberOfOffspring, double eta,
			double zeta) {
		this.numberOfParents = numberOfParents;
		this.numberOfOffspring = numberOfOffspring;
		this.eta = eta;
		this.zeta = zeta;
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
	 * spread of solutions in the direction of the selected parent.
	 * 
	 * @return the standard deviation of the normal distribution controlling the
	 *         spread of solutions in the direction of the selected parent
	 */
	public double getEta() {
		return eta;
	}

	/**
	 * Returns the standard deviation of the normal distribution controlling the
	 * spread of solutions in the directions defined by the remaining parents.
	 * 
	 * @return the standard deviation of the normal distribution controlling the
	 *         spread of solutions in the directions defined by the remaining
	 *         parents
	 */
	public double getZeta() {
		return zeta;
	}

	@Override
	public int getArity() {
		return numberOfParents;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution[] result = new Solution[numberOfOffspring];

		parents = parents.clone(); // prevent reordering of parents

		for (int i = 0; i < numberOfOffspring; i++) {
			int index = PRNG.nextInt(parents.length);
			Solution temp = parents[index];
			parents[index] = parents[parents.length - 1];
			parents[parents.length - 1] = temp;

			result[i] = pcx(parents);
		}

		return result;
	}

	/**
	 * Returns one randomly-generated offspring produced by this operator using
	 * {@code parents[parents.length-1]} as the selected parent. Multiple
	 * invocations of this method with the same argument will produce offspring
	 * distributed about the selected parent.
	 * 
	 * @param parents the parent solutions
	 * @return one randomly-generated offspring produced by this operator using
	 *         {@code parents[parents.length-1]} as the selected parent
	 */
	protected Solution pcx(Solution[] parents) {
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

		List<double[]> e_eta = new ArrayList<double[]>();
		e_eta.add(Vector.subtract(x[k - 1], g));

		double D = 0.0;

		// basis vectors defined by parents
		for (int i = 0; i < k - 1; i++) {
			double[] d = Vector.subtract(x[i], g);

			if (!Vector.isZero(d)) {
				double[] e = Vector.orthogonalize(d, e_eta);

				if (!Vector.isZero(e)) {
					D += Vector.magnitude(e);
					e_eta.add(Vector.normalize(e));
				}
			}
		}

		D /= k - 1;

		// construct the offspring
		double[] variables = x[k - 1];

		variables = Vector.add(variables, Vector.multiply(PRNG.nextGaussian(
				0.0, zeta), e_eta.get(0)));

		double eta = PRNG.nextGaussian(0.0, this.eta);
		for (int i = 1; i < e_eta.size(); i++) {
			variables = Vector.add(variables, Vector.multiply(eta * D, e_eta
					.get(i)));
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
