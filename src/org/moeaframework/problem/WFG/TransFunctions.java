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
class TransFunctions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TransFunctions() {
		super();
	}

	public static double b_poly(double y, double alpha) {
		assert (y >= 0.0) && (y <= 1.0);
		assert (alpha > 0.0) && (alpha != 1.0);

		return Misc.correct_to_01(Math.pow(y, alpha));
	}

	public static double b_flat(double y, double A, double B, double C) {
		assert (y >= 0.0) && (y <= 1.0);
		assert (A >= 0.0) && (A <= 1.0);
		assert (B >= 0.0) && (B <= 1.0);
		assert (C >= 0.0) && (C <= 1.0);
		assert B < C;
		assert (B != 0.0) || (A == 0.0);
		assert (B != 0.0) || (C != 1.0);
		assert (C != 1.0) || (A == 1.0);
		assert (C != 1.0) || (B != 0.0);

		double tmp1 = Math.min(0.0, Math.floor(y - B)) * A * (B - y) / B;
		double tmp2 = Math.min(0.0, Math.floor(C - y)) * (1.0 - A) * (y - C)
				/ (1.0 - C);

		return Misc.correct_to_01(A + tmp1 - tmp2);
	}

	public static double b_param(double y, double u, double A, double B,
			double C) {
		assert (y >= 0.0) && (y <= 1.0);
		assert (u >= 0.0) && (u <= 1.0);
		assert (A > 0.0) && (A < 1.0);
		assert B > 0.0;
		assert B < C;

		double v = A - (1.0 - 2.0 * u) * Math.abs(Math.floor(0.5 - u) + A);

		return Misc.correct_to_01(Math.pow(y, B + (C - B) * v));
	}

	public static double s_linear(double y, double A) {
		assert (y >= 0.0) && (y <= 1.0);
		assert (A > 0.0) && (A < 1.0);

		return Misc.correct_to_01(Math.abs(y - A)
				/ Math.abs(Math.floor(A - y) + A));
	}

	public static double s_decept(double y, double A, double B, double C) {
		assert (y >= 0.0) && (y <= 1.0);
		assert (A > 0.0) && (A < 1.0);
		assert (B > 0.0) && (B < 1.0);
		assert (C > 0.0) && (C < 1.0);
		assert (A - B > 0.0) && (A + B < 1.0);

		double tmp1 = Math.floor(y - A + B) * (1.0 - C + (A - B) / B) / (A - B);
		double tmp2 = Math.floor(A + B - y) * (1.0 - C + (1.0 - A - B) / B)
				/ (1.0 - A - B);

		return Misc.correct_to_01(1.0 + (Math.abs(y - A) - B)
				* (tmp1 + tmp2 + 1.0 / B));
	}

	public static double s_multi(double y, int A, double B, double C) {
		assert (y >= 0.0) && (y <= 1.0);
		assert A >= 1.0;
		assert B >= 0.0;
		assert (4.0 * A + 2.0) * Math.PI >= 4.0 * B;
		assert (C > 0.0) && (C < 1.0);

		double tmp1 = Math.abs(y - C) / (2.0 * (Math.floor(C - y) + C));
		double tmp2 = (4.0 * A + 2.0) * Math.PI * (0.5 - tmp1);

		return Misc.correct_to_01((1.0 + Math.cos(tmp2) + 4.0 * B
				* Math.pow(tmp1, 2.0))
				/ (B + 2.0));
	}

	public static double r_sum(double[] y, double[] w) {
		assert y.length != 0;
		assert w.length == y.length;
		assert Misc.vector_in_01(y);

		double numerator = 0.0;
		double denominator = 0.0;

		for (int i = 0; i < y.length; i++) {
			assert w[i] > 0.0;

			numerator += w[i] * y[i];
			denominator += w[i];
		}

		return Misc.correct_to_01(numerator / denominator);
	}

	public static double r_nonsep(double[] y, int A) {
		assert y.length != 0;
		assert Misc.vector_in_01(y);
		assert (A >= 1) && (A <= y.length);
		assert y.length % A == 0;

		double numerator = 0.0;

		for (int j = 0; j < y.length; j++) {
			numerator += y[j];

			for (int k = 0; k <= A - 2; k++) {
				numerator += Math.abs(y[j] - y[(j + k + 1) % y.length]);
			}
		}

		double tmp = Math.ceil(A / 2.0);
		double denominator = y.length * tmp * (1.0 + 2.0 * A - 2.0 * tmp) / A;

		return Misc.correct_to_01(numerator / denominator);
	}

}
