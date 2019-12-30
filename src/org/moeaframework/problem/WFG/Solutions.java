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
package org.moeaframework.problem.WFG;

import java.util.Arrays;

import org.moeaframework.core.PRNG;

/* This code is based on the Walking Fish Group implementation.
 * 
 * Copyright 2005 The Walking Fish Group (WFG).
 *
 * This material is provided "as is", with no warranty expressed or implied.
 * Any use is at your own risk. Permission to use or copy this software for
 * any purpose is hereby granted without fee, provided this notice is
 * retained on all copies. Permission to modify the code and to distribute
 * modified code is granted, provided a notice that the code was modified is
 * included with the above copyright notice.
 *
 * http://www.wfg.csse.uwa.edu.au/
 */
class Solutions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Solutions() {
		super();
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the WFG1 problem with {@code k} position-related variables
	 * and {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the WFG1 problem
	 */
	public static double[] WFG_1_random_soln(int k, int l) {
		double[] result = new double[k + l];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = Math.pow(PRNG.nextDouble(), 50.0); // account for
			// polynomial bias
		}

		// set distance parameters
		for (int i = k; i < k + l; i++) {
			result[i] = 0.35;
		}

		// scale to the correct domains
		for (int i = 0; i < k + l; i++) {
			result[i] *= 2.0 * (i + 1);
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the WFG2-WFG7 problems with {@code k} position-related
	 * variables and {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the WFG2-WFG7 problems
	 */
	public static double[] WFG_2_thru_7_random_soln(int k, int l) {
		double[] result = new double[k + l];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = PRNG.nextDouble();
		}

		// set distance parameters
		for (int i = k; i < k + l; i++) {
			result[i] = 0.35;
		}

		// scale to the correct domains
		for (int i = 0; i < k + l; i++) {
			result[i] *= 2.0 * (i + 1);
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the WFG8 problem with {@code k} position-related variables
	 * and {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the WFG8 problem
	 */
	public static double[] WFG_8_random_soln(int k, int l) {
		double[] result = new double[k];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = PRNG.nextDouble();
		}

		// set distance parameters
		for (int i = k; i < k + l; i++) {
			double[] w = new double[result.length];
			Arrays.fill(w, 1.0);

			double u = TransFunctions.r_sum(result, w);

			double tmp1 = Math.abs(Math.floor(0.5 - u) + 0.98 / 49.98);
			double tmp2 = 0.02 + 49.98 * (0.98 / 49.98 - (1.0 - 2.0 * u) * tmp1);

			result = Arrays.copyOf(result, result.length+1);
			result[i] = Math.pow(0.35, Math.pow(tmp2, -1.0));
		}

		// scale to the correct domains
		for (int i = 0; i < k + l; i++) {
			result[i] *= 2.0 * (i + 1);
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the WFG9 problem with {@code k} position-related variables
	 * and {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the WFG9 problem
	 */
	public static double[] WFG_9_random_soln(int k, int l) {
		double[] result = new double[k + l];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = PRNG.nextDouble();
		}

		// set distance parameters
		result[k + l - 1] = 0.35; // the last distance parameter is easy
		for (int i = k + l - 2; i >= k; i--) {
			double[] result_sub = new double[k + l - i - 1];

			for (int j = i + 1; j < k + l; j++) {
				result_sub[j - i - 1] = result[j];
			}

			double[] w = new double[result_sub.length];
			Arrays.fill(w, 1.0);

			double tmp1 = TransFunctions.r_sum(result_sub, w);

			result[i] = Math.pow(0.35, Math.pow(0.02 + 1.96 * tmp1, -1.0));
		}

		// scale to the correct domains
		for (int i = 0; i < k + l; i++) {
			result[i] *= 2.0 * (i + 1);
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the I1 problem with {@code k} position-related variables and
	 * {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the I1 problem
	 */
	public static double[] I1_random_soln(int k, int l) {
		double[] result = new double[k + l];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = PRNG.nextDouble();
		}

		// set distance parameters
		for (int i = k; i < k + l; i++) {
			result[i] = 0.35;
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the I2 problem with {@code k} position-related variables and
	 * {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the I2 problem
	 */
	public static double[] I2_random_soln(int k, int l) {
		double[] result = new double[k + l];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = PRNG.nextDouble();
		}

		// set distance parameters
		result[k + l - 1] = 0.35; // the last distance parameter is easy
		for (int i = k + l - 2; i >= k; i--) {
			double[] result_sub = new double[k + l - i - 1];

			for (int j = i + 1; j < k + l; j++) {
				result_sub[j - i - 1] = result[j];
			}

			double[] w = new double[result_sub.length];
			Arrays.fill(w, 1.0);

			double tmp1 = TransFunctions.r_sum(result_sub, w);

			result[i] = Math.pow(0.35, Math.pow(0.02 + 1.96 * tmp1, -1.0));
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the I3 problem with {@code k} position-related variables and
	 * {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the I3 problem
	 */
	public static double[] I3_random_soln(int k, int l) {
		double[] result = new double[k];

		// set position parameters
		for (int i = 0; i < k; i++) {
			result[i] = PRNG.nextDouble();
		}

		// set distance parameters
		for (int i = k; i < k + l; i++) {
			double[] w = new double[result.length];
			Arrays.fill(w, 1.0);

			double u = TransFunctions.r_sum(result, w);

			double tmp1 = Math.abs(Math.floor(0.5 - u) + 0.98 / 49.98);
			double tmp2 = 0.02 + 49.98 * (0.98 / 49.98 - (1.0 - 2.0 * u) * tmp1);

			result = Arrays.copyOf(result, result.length+1);
			result[i] = Math.pow(0.35, Math.pow(tmp2, -1.0));
		}

		return result;
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the I4 problem with {@code k} position-related variables and
	 * {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the I4 problem
	 */
	public static double[] I4_random_soln(int k, int l) {
		return I1_random_soln(k, l);
	}

	/**
	 * Returns the decision variables of a randomly-generated Pareto optimal
	 * solution to the I5 problem with {@code k} position-related variables and
	 * {@code l} distance-related variables.
	 * 
	 * @param k the number of position-related parameters
	 * @param l the number of distance-related parameters
	 * @return the decision variables of a randomly-generated Pareto optimal
	 *         solution to the I5 problem
	 */
	public static double[] I5_random_soln(int k, int l) {
		return I3_random_soln(k, l);
	}

}
