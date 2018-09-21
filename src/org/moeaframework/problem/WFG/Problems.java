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
class Problems {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Problems() {
		super();
	}

	/**
	 * Returns {@code true} if the supplied arguments are valid; {@code false}
	 * otherwise. Specifically, returns {@code true} if {@code (k >= 1) && (k <
	 * z.length)}, {@code M >= 2} and {@code k % (M-1) == 0}.
	 * 
	 * @param z the decision variables
	 * @param k the number of position-related parameters (implies the number of
	 *        distance-related parameters)
	 * @param M the number of objectives
	 * @return {@code true} if the supplied arguments are valid; {@code false}
	 *         otherwise
	 */
	private static boolean ArgsOk(double[] z, int k, int M) {
		int n = z.length;

		return (k >= 1) && (k < n) && (M >= 2) && (k % (M - 1) == 0);
	}

	/**
	 * Normalizes the decision variables to be in the range {@code [0, 1]}.
	 * 
	 * @param z the decision variables
	 * @return the normalized decision variables
	 */
	private static double[] WFG_normalize_z(double[] z) {
		double[] result = new double[z.length];

		for (int i = 0; i < z.length; i++) {
			double bound = 2.0 * (i + 1);

			assert z[i] >= 0.0;
			assert z[i] <= bound;

			result[i] = z[i] / bound;
		}

		return result;
	}

	/**
	 * Implementation of the WFG1 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG1 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG1(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG1_t1(y, k);
		y = Transitions.WFG1_t2(y, k);
		y = Transitions.WFG1_t3(y);
		y = Transitions.WFG1_t4(y, k, M);

		return Shapes.WFG1_shape(y);
	}

	/**
	 * Implementation of the WFG2 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG2 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG2(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);
		assert (z.length - k) % 2 == 0;

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG1_t1(y, k);
		y = Transitions.WFG2_t2(y, k);
		y = Transitions.WFG2_t3(y, k, M);

		return Shapes.WFG2_shape(y);
	}

	/**
	 * Implementation of the WFG3 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG3 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG3(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);
		assert (z.length - k) % 2 == 0;

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG1_t1(y, k);
		y = Transitions.WFG2_t2(y, k);
		y = Transitions.WFG2_t3(y, k, M);

		return Shapes.WFG3_shape(y);
	}

	/**
	 * Implementation of the WFG4 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG4 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG4(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG4_t1(y);
		y = Transitions.WFG2_t3(y, k, M);

		return Shapes.WFG4_shape(y);
	}

	/**
	 * Implementation of the WFG5 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG5 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG5(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG5_t1(y);
		y = Transitions.WFG2_t3(y, k, M);

		return Shapes.WFG4_shape(y);
	}

	/**
	 * Implementation of the WFG6 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG6 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG6(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG1_t1(y, k);
		y = Transitions.WFG6_t2(y, k, M);

		return Shapes.WFG4_shape(y);
	}

	/**
	 * Implementation of the WFG7 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG7 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG7(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG7_t1(y, k);
		y = Transitions.WFG1_t1(y, k);
		y = Transitions.WFG2_t3(y, k, M);

		return Shapes.WFG4_shape(y);
	}

	/**
	 * Implementation of the WFG8 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG8 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG8(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG8_t1(y, k);
		y = Transitions.WFG1_t1(y, k);
		y = Transitions.WFG2_t3(y, k, M);

		return Shapes.WFG4_shape(y);
	}

	/**
	 * Implementation of the WFG9 test problem, evaluating the decision
	 * variables for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the WFG9 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] WFG9(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = WFG_normalize_z(z);

		y = Transitions.WFG9_t1(y);
		y = Transitions.WFG9_t2(y, k);
		y = Transitions.WFG6_t2(y, k, M);

		return Shapes.WFG4_shape(y);
	}

	/**
	 * Implementation of the I1 test problem, evaluating the decision variables
	 * for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the I1 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] I1(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = z;

		y = Transitions.I1_t2(y, k);
		y = Transitions.I1_t3(y, k, M);

		return Shapes.I1_shape(y);
	}

	/**
	 * Implementation of the I2 test problem, evaluating the decision variables
	 * for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the I2 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] I2(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = z;

		y = Transitions.I2_t1(y);
		y = Transitions.I1_t2(y, k);
		y = Transitions.I1_t3(y, k, M);

		return Shapes.I1_shape(y);
	}

	/**
	 * Implementation of the I3 test problem, evaluating the decision variables
	 * for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the I3 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] I3(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = z;

		y = Transitions.I3_t1(y);
		y = Transitions.I1_t2(y, k);
		y = Transitions.I1_t3(y, k, M);

		return Shapes.I1_shape(y);
	}

	/**
	 * Implementation of the I4 test problem, evaluating the decision variables
	 * for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the I4 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] I4(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = z;

		y = Transitions.I1_t2(y, k);
		y = Transitions.I4_t3(y, k, M);

		return Shapes.I1_shape(y);
	}

	/**
	 * Implementation of the I5 test problem, evaluating the decision variables
	 * for an instance with {@code k} position-related variables,
	 * {@code z.length-k} distance-related parameters, and {@code M} objectives.
	 * 
	 * @param z the decision variables being evaluated
	 * @param k the number of position-related variables (implies the number of
	 *        distance-related variables)
	 * @param M the number of objectives
	 * @return the objectives for the I5 instance with the specified decision
	 *         variables and parameters
	 */
	public static double[] I5(double[] z, int k, int M) {
		assert ArgsOk(z, k, M);

		double[] y = z;

		y = Transitions.I3_t1(y);
		y = Transitions.I1_t2(y, k);
		y = Transitions.I4_t3(y, k, M);

		return Shapes.I1_shape(y);
	}

}
