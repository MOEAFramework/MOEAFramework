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
class Transitions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Transitions() {
		super();
	}

	/**
	 * Construct a vector with the elements v[head], ..., v[tail-1].
	 */
	private static double[] subvector(double[] v, int head, int tail) {
		assert (head >= 0) && (head < tail);
		assert tail <= v.length;

		double[] result = new double[tail - head];

		for (int i = head; i < tail; i++) {
			result[i - head] = v[i];
		}

		return result;
	}

	public static double[] WFG1_t1(double[] y, int k) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);

		double[] t = new double[n];

		for (int i = 0; i < k; i++) {
			t[i] = y[i];
		}

		for (int i = k; i < n; i++) {
			t[i] = TransFunctions.s_linear(y[i], 0.35);
		}

		return t;
	}

	public static double[] WFG1_t2(double[] y, int k) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);

		double[] t = new double[n];

		for (int i = 0; i < k; i++) {
			t[i] = y[i];
		}

		for (int i = k; i < n; i++) {
			t[i] = TransFunctions.b_flat(y[i], 0.8, 0.75, 0.85);
		}

		return t;
	}

	public static double[] WFG1_t3(double[] y) {
		int n = y.length;

		assert Misc.vector_in_01(y);

		double[] t = new double[n];

		for (int i = 0; i < n; i++) {
			t[i] = TransFunctions.b_poly(y[i], 0.02);
		}

		return t;
	}

	public static double[] WFG1_t4(double[] y, int k, int M) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);
		assert M >= 2;
		assert k % (M - 1) == 0;

		double[] w = new double[n];

		for (int i = 1; i <= n; i++) {
			w[i - 1] = 2.0 * i;
		}

		double[] t = new double[M];

		for (int i = 1; i <= M - 1; i++) {
			int head = (i - 1) * k / (M - 1);
			int tail = i * k / (M - 1);

			double[] y_sub = subvector(y, head, tail);
			double[] w_sub = subvector(w, head, tail);

			t[i - 1] = TransFunctions.r_sum(y_sub, w_sub);
		}

		double[] y_sub = subvector(y, k, n);
		double[] w_sub = subvector(w, k, n);

		t[M - 1] = TransFunctions.r_sum(y_sub, w_sub);

		return t;
	}

	public static double[] WFG2_t2(double[] y, int k) {
		int n = y.length;
		int l = n - k;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);
		assert l % 2 == 0;

		double[] t = new double[k + l / 2];

		for (int i = 0; i < k; i++) {
			t[i] = y[i];
		}

		for (int i = k + 1; i <= k + l / 2; i++) {
			int head = k + 2 * (i - k) - 2;
			int tail = k + 2 * (i - k);

			t[i - 1] = TransFunctions.r_nonsep(subvector(y, head, tail), 2);
		}

		return t;
	}

	public static double[] WFG2_t3(double[] y, int k, int M) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);
		assert M >= 2;
		assert k % (M - 1) == 0;

		double[] w = new double[n];
		Arrays.fill(w, 1.0);

		double[] t = new double[M];

		for (int i = 1; i <= M - 1; i++) {
			int head = (i - 1) * k / (M - 1);
			int tail = i * k / (M - 1);

			double[] y_sub = subvector(y, head, tail);
			double[] w_sub = subvector(w, head, tail);

			t[i - 1] = TransFunctions.r_sum(y_sub, w_sub);
		}

		double[] y_sub = subvector(y, k, n);
		double[] w_sub = subvector(w, k, n);

		t[M - 1] = TransFunctions.r_sum(y_sub, w_sub);

		return t;
	}

	public static double[] WFG4_t1(double[] y) {
		int n = y.length;

		assert Misc.vector_in_01(y);

		double[] t = new double[n];

		for (int i = 0; i < n; i++) {
			t[i] = TransFunctions.s_multi(y[i], 30, 10, 0.35);
		}

		return t;
	}

	public static double[] WFG5_t1(double[] y) {
		int n = y.length;

		assert Misc.vector_in_01(y);

		double[] t = new double[n];

		for (int i = 0; i < n; i++) {
			t[i] = TransFunctions.s_decept(y[i], 0.35, 0.001, 0.05);
		}

		return t;
	}

	public static double[] WFG6_t2(double[] y, int k, int M) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);
		assert M >= 2;
		assert k % (M - 1) == 0;

		double[] t = new double[M];

		for (int i = 1; i <= M - 1; i++) {
			int head = (i - 1) * k / (M - 1);
			int tail = i * k / (M - 1);

			double[] y_sub = subvector(y, head, tail);

			t[i - 1] = TransFunctions.r_nonsep(y_sub, k / (M - 1));
		}

		double[] y_sub = subvector(y, k, n);

		t[M - 1] = TransFunctions.r_nonsep(y_sub, n - k);

		return t;
	}

	public static double[] WFG7_t1(double[] y, int k) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);

		double[] w = new double[n];
		Arrays.fill(w, 1.0);

		double[] t = new double[n];

		for (int i = 0; i < k; i++) {
			double[] y_sub = subvector(y, i + 1, n);
			double[] w_sub = subvector(w, i + 1, n);

			double u = TransFunctions.r_sum(y_sub, w_sub);

			t[i] = TransFunctions.b_param(y[i], u, 0.98 / 49.98, 0.02, 50);
		}

		for (int i = k; i < n; i++) {
			t[i] = y[i];
		}

		return t;
	}

	public static double[] WFG8_t1(double[] y, int k) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);

		double[] w = new double[n];
		Arrays.fill(w, 1.0);

		double[] t = new double[n];

		for (int i = 0; i < k; i++) {
			t[i] = y[i];
		}

		for (int i = k; i < n; i++) {
			double[] y_sub = subvector(y, 0, i);
			double[] w_sub = subvector(w, 0, i);

			double u = TransFunctions.r_sum(y_sub, w_sub);

			t[i] = TransFunctions.b_param(y[i], u, 0.98 / 49.98, 0.02, 50);
		}

		return t;
	}

	public static double[] WFG9_t1(double[] y) {
		int n = y.length;

		assert Misc.vector_in_01(y);

		double[] w = new double[n];
		Arrays.fill(w, 1.0);

		double[] t = new double[n];

		for (int i = 0; i < n - 1; i++) {
			double[] y_sub = subvector(y, i + 1, n);
			double[] w_sub = subvector(w, i + 1, n);

			double u = TransFunctions.r_sum(y_sub, w_sub);

			t[i] = TransFunctions.b_param(y[i], u, 0.98 / 49.98, 0.02, 50);
		}

		t[n - 1] = y[n - 1];

		return t;
	}

	public static double[] WFG9_t2(double[] y, int k) {
		int n = y.length;

		assert Misc.vector_in_01(y);
		assert (k >= 1) && (k < n);

		double[] t = new double[n];

		for (int i = 0; i < k; i++) {
			t[i] = TransFunctions.s_decept(y[i], 0.35, 0.001, 0.05);
		}

		for (int i = k; i < n; i++) {
			t[i] = TransFunctions.s_multi(y[i], 30, 95, 0.35);
		}

		return t;
	}

	public static double[] I1_t2(double[] y, int k) {
		return WFG1_t1(y, k);
	}

	public static double[] I1_t3(double[] y, int k, int M) {
		return WFG2_t3(y, k, M);
	}

	public static double[] I2_t1(double[] y) {
		return WFG9_t1(y);
	}

	public static double[] I3_t1(double[] y) {
		int n = y.length;

		assert Misc.vector_in_01(y);

		double[] w = new double[n];
		Arrays.fill(w, 1.0);

		double[] t = new double[n];

		t[0] = y[0];

		for (int i = 1; i < n; i++) {
			double[] y_sub = subvector(y, 0, i);
			double[] w_sub = subvector(w, 0, i);

			double u = TransFunctions.r_sum(y_sub, w_sub);

			t[i] = TransFunctions.b_param(y[i], u, 0.98 / 49.98, 0.02, 50);
		}

		return t;
	}

	public static double[] I4_t3(double[] y, int k, int M) {
		return WFG6_t2(y, k, M);
	}

}
