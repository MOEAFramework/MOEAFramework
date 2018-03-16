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
class ShapeFunctions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ShapeFunctions() {
		super();
	}

	// True if all elements of "x" are in [0,1], and m is in [1, x.length]
	public static boolean shape_args_ok(double[] x, int m) {
		return Misc.vector_in_01(x) && (m >= 1) && (m <= x.length);
	}

	public static double linear(double[] x, int m) {
		assert shape_args_ok(x, m);

		int M = x.length;
		double result = 1.0;

		for (int i = 1; i <= M - m; i++) {
			result *= x[i - 1];
		}

		if (m != 1) {
			result *= 1 - x[M - m];
		}

		return Misc.correct_to_01(result);
	}

	public static double convex(double[] x, int m) {
		assert shape_args_ok(x, m);

		int M = x.length;
		double result = 1.0;

		for (int i = 1; i <= M - m; i++) {
			result *= 1.0 - Math.cos(x[i - 1] * Math.PI / 2.0);
		}

		if (m != 1) {
			result *= 1.0 - Math.sin(x[M - m] * Math.PI / 2.0);
		}

		return Misc.correct_to_01(result);
	}

	public static double concave(double[] x, int m) {
		assert shape_args_ok(x, m);

		int M = x.length;
		double result = 1.0;

		for (int i = 1; i <= M - m; i++) {
			result *= Math.sin(x[i - 1] * Math.PI / 2.0);
		}

		if (m != 1) {
			result *= Math.cos(x[M - m] * Math.PI / 2.0);
		}

		return Misc.correct_to_01(result);
	}

	public static double mixed(double[] x, int A, double alpha) {
		assert Misc.vector_in_01(x);
		assert x.length != 0;
		assert A >= 1;
		assert alpha > 0.0;

		double tmp = 2.0 * A * Math.PI;
		return Misc.correct_to_01(Math.pow(1.0 - x[0]
				- Math.cos(tmp * x[0] + Math.PI / 2.0) / tmp, alpha));
	}

	public static double disc(double[] x, int A, double alpha, double beta) {
		assert Misc.vector_in_01(x);
		assert x.length != 0;
		assert A >= 1;
		assert alpha > 0.0;
		assert beta > 0.0;

		double tmp = A * Math.pow(x[0], beta) * Math.PI;
		return Misc.correct_to_01(1.0 - Math.pow(x[0], alpha)
				* Math.pow(Math.cos(tmp), 2.0));

	}

}
