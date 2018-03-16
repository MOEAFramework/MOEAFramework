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
class Shapes {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Shapes() {
		super();
	}

	/**
	 * Construct a vector of length M-1, with values "1,0,0,..." if "degenerate"
	 * is true, otherwise with values "1,1,1,..." if "degenerate" is false.
	 */
	private static int[] WFG_create_A(int M, boolean degenerate) {
		assert M >= 2;

		if (degenerate) {
			int[] A = new int[M - 1];
			A[0] = 1;
			return A;
		} else {
			int[] A = new int[M - 1];
			Arrays.fill(A, 1);
			return A;
		}
	}

	/**
	 * Given the vector "x" (the last value of which is the sole distance
	 * parameter), and the shape function results in "h", calculate the scaled
	 * fitness values for a WFG problem.
	 */
	private static double[] WFG_calculate_f(double[] x, double[] h) {
		assert Misc.vector_in_01(x);
		assert Misc.vector_in_01(h);
		assert x.length == h.length;

		int M = h.length;

		double[] S = new double[M];

		for (int m = 1; m <= M; m++) {
			S[m - 1] = m * 2.0;
		}

		return FrameworkFunctions.calculate_f(1.0, x, h, S);
	}

	public static double[] WFG1_shape(double[] t_p) {
		assert Misc.vector_in_01(t_p);
		assert t_p.length >= 2;

		int M = t_p.length;

		int[] A = WFG_create_A(M, false);
		double[] x = FrameworkFunctions.calculate_x(t_p, A);

		double[] h = new double[M];

		for (int m = 1; m <= M - 1; m++) {
			h[m - 1] = ShapeFunctions.convex(x, m);
		}
		h[M - 1] = ShapeFunctions.mixed(x, 5, 1.0);

		return WFG_calculate_f(x, h);
	}

	public static double[] WFG2_shape(double[] t_p) {
		assert Misc.vector_in_01(t_p);
		assert t_p.length >= 2;

		int M = t_p.length;

		int[] A = WFG_create_A(M, false);
		double[] x = FrameworkFunctions.calculate_x(t_p, A);

		double[] h = new double[M];

		for (int m = 1; m <= M - 1; m++) {
			h[m - 1] = ShapeFunctions.convex(x, m);
		}
		h[M - 1] = ShapeFunctions.disc(x, 5, 1.0, 1.0);

		return WFG_calculate_f(x, h);
	}

	public static double[] WFG3_shape(double[] t_p) {
		assert Misc.vector_in_01(t_p);
		assert t_p.length >= 2;

		int M = t_p.length;

		int[] A = WFG_create_A(M, true);
		double[] x = FrameworkFunctions.calculate_x(t_p, A);

		double[] h = new double[M];

		for (int m = 1; m <= M; m++) {
			h[m - 1] = ShapeFunctions.linear(x, m);
		}

		return WFG_calculate_f(x, h);
	}

	public static double[] WFG4_shape(double[] t_p) {
		assert Misc.vector_in_01(t_p);
		assert t_p.length >= 2;

		int M = t_p.length;

		int[] A = WFG_create_A(M, false);
		double[] x = FrameworkFunctions.calculate_x(t_p, A);

		double[] h = new double[M];

		for (int m = 1; m <= M; m++) {
			h[m - 1] = ShapeFunctions.concave(x, m);
		}

		return WFG_calculate_f(x, h);
	}

	public static double[] I1_shape(double[] t_p) {
		assert Misc.vector_in_01(t_p);
		assert t_p.length >= 2;

		int M = t_p.length;

		int[] A = WFG_create_A(M, false);
		double[] x = FrameworkFunctions.calculate_x(t_p, A);

		double[] h = new double[M];

		for (int m = 1; m <= M; m++) {
			h[m - 1] = ShapeFunctions.concave(x, m);
		}

		double[] S = new double[M];
		Arrays.fill(S, 1.0);

		return FrameworkFunctions.calculate_f(1.0, x, h, S);
	}

}
