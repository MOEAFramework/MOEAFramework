/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.problem.CEC2009;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * Tests if the {@link UF13} class (which is now an alias to {@code WFG1})
 * matches the test function defined in the CEC2009 code.
 */
public class UF13Test {
	
	@Test
	public void test() {
		UF13 uf13 = new UF13();
		RandomInitialization initialization = new RandomInitialization(uf13, 
				TestThresholds.SAMPLES);
		
		for (Solution solution : initialization.initialize()) {
			double[] x = EncodingUtils.getReal(solution);
			double[] f = new double[uf13.getNumberOfObjectives()];
			
			WFG1_M5(x, f, uf13.getNumberOfVariables(),
					uf13.getNumberOfObjectives());
			
			uf13.evaluate(solution);
			
			Assert.assertArrayEquals(f, solution.getObjectives(),
					TestThresholds.SOLUTION_EPS);
		}
	}
	
	/* 
     * The following source code is modified from the CEC 2009 test problem
     * suite available at {@link http://dces.essex.ac.uk/staff/qzhang/}.
     * Permission to distribute these modified source codes under the GNU
     * Lesser General Public License was obtained via e-mail correspondence
     * with the original authors.
     */
	
	private static final double PI = 3.1415926535897932384626433832795;
	
	private static final double EPSILON = 1.0e-10;
	
	/**
	 * Evaluates the UF13 problem.  This is the version of the code provided in
	 * the CEC2009 test suite.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 * @param M the number of objectives
	 */
	private static void WFG1_M5(double[] z, double[] f, int nx, int M) {
		double[] y = new double[30];
		double[] t1 = new double[30];
		double[] t2 = new double[30];
		double[] t3 = new double[30];
		double[] t4 = new double[5];
		int k = M == 2 ? 4 : 2 * (M - 1);
		
		for (int i = 0; i < nx; i++) {
			y[i] = z[i] / (2.0 * (i + 1));
		}
		
		for (int i = 0; i < k; i++) {
			t1[i] = y[i];
		}
		
		for (int i = k; i < nx; i++) {
			t1[i] = s_linear(y[i], 0.35);
		}
		
		for (int i = 0; i < k; i++) {
			t2[i] = t1[i];
		}
		
		for (int i = k; i < nx; i++) {
			t2[i] = b_flat(t1[i], 0.8, 0.75, 0.85);
		}
		
		for (int i = 0; i < nx; i++) {
			t3[i] = b_poly(t2[i], 0.02);
		}
		
		double[] w = new double[30];
		double[] y_sub = new double[30];
		double[] w_sub = new double[30];
		double[] y_sub2 = new double[30];
		double[] w_sub2 = new double[30];
		
		for (int i = 1; i <= nx; i++) {
			w[i - 1] = 2.0 * i;
		}
		
		for (int i = 1; i <= M - 1; i++) {
			int head = (i - 1) * k / (M - 1);
			int tail = i * k / (M - 1);
			
			for (int j = head; j < tail; j++) {
				y_sub[j - head] = t3[j];
				w_sub[j - head] = w[j];
			}
			
			t4[i - 1] = r_sum(y_sub, w_sub, tail - head);
		}
		
		for (int j = k; j < nx; j++) {
			y_sub2[j - k] = t3[j];
			w_sub2[j - k] = w[j];
		}
		
		t4[M - 1] = r_sum(y_sub2, w_sub2, nx - k);
		
		int[] A = new int[5];
		double[] x = new double[5];
		double[] h = new double[5];
		double[] S = new double[5];
		
		A[0] = 1;
		
		for (int i = 1; i < M - 1; i++) {
			A[i] = 1;
		}
		
		for (int i = 0; i < M - 1; i++) {
			double tmp1 = t4[M - 1];
			
			if (A[i] > tmp1) {
				tmp1 = A[i];
			}
			
			x[i] = tmp1 * (t4[i] - 0.5) + 0.5;
		}
		
		x[M - 1] = t4[M - 1];
		
		for (int m = 1; m <= M - 1; m++) {
			h[m - 1] = convex(x, m, M);
		}
		
		h[M - 1] = mixed(x, 5, 1.0);
		
		for (int m = 1; m <= M; m++) {
			S[m - 1] = m * 2.0;
		}
		
		for (int i = 0; i < M; i++) {
			f[i] = 1.0 * x[M - 1] + S[i] * h[i];
		}
	}

	private static double correct_to_01(double aa, double epsilon) {
		double min = 0.0;
		double max = 1.0;
		double min_epsilon = min - epsilon;
		double max_epsilon = max + epsilon;
		
		if (aa <= min && aa >= min_epsilon) {
			return min;
		} else if (aa >= max && aa <= max_epsilon) {
			return max;
		} else {
			return aa;
		}
	}

	private static double convex(double[] x, int m, int M) {
		double result = 1.0;
		
		for (int i = 1; i <= M - m; i++) {
			result *= 1.0 - Math.cos(x[i - 1] * PI / 2.0);
		}
		
		if (m != 1) {
			result *= 1.0 - Math.sin(x[M - m] * PI / 2.0);
		}
		
		return correct_to_01(result, EPSILON);
	}

	private static double mixed(double[] x, int A, double alpha) {
		double tmp = 2.0 * A * PI;
		
		return correct_to_01(Math.pow(1.0 - x[0]
				- Math.cos(tmp * x[0] + PI / 2.0) / tmp, alpha), EPSILON);
	}

	private static double min_double(double aa, double bb) {
		return aa < bb ? aa : bb;
	}

	private static double b_poly(double y, double alpha) {
		return correct_to_01(Math.pow(y, alpha), EPSILON);
	}

	private static double b_flat(double y, double A, double B, double C) {
		double tmp1 = min_double(0.0, Math.floor(y - B)) * A * (B - y) / B;
		double tmp2 = min_double(0.0, Math.floor(C - y)) * (1.0 - A) * (y - C)
				/ (1.0 - C);
		
		return correct_to_01(A + tmp1 - tmp2, EPSILON);
	}

	private static double s_linear(double y, double A) {
		return correct_to_01(Math.abs(y - A) / Math.abs(Math.floor(A - y) + A),
				EPSILON);
	}

	private static double r_sum(double[] y, double[] w, int ny) {
		double numerator = 0.0;
		double denominator = 0.0;
		
		for (int i = 0; i < ny; i++) {
			numerator += w[i] * y[i];
			denominator += w[i];
		}
		
		return correct_to_01(numerator / denominator, EPSILON);
	}

}
