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

public class UF11Test {
	
	@Test
	public void test() {
		UF11 uf11 = new UF11();
		RandomInitialization initialization = new RandomInitialization(uf11, 
				TestThresholds.SAMPLES);
		
		for (Solution solution : initialization.initialize()) {
			double[] x = EncodingUtils.getReal(solution);
			double[] f = new double[uf11.getNumberOfObjectives()];
			
			R2_DTLZ2_M5(x, f, uf11.getNumberOfVariables(),
					uf11.getNumberOfObjectives());
			
			uf11.evaluate(solution);
			
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
	
	/**
	 * Evaluates the UF11 problem.  This is the version of the code provided by
	 * the CEC 2009 test suite.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 * @param n_obj the number of objectives
	 */
	public static void R2_DTLZ2_M5(double[] x, double[] f, int nx, int n_obj) {
		int k = nx - n_obj + 1;
		double g = 0;
		double[][] M_10D = {
				{ 0.0346, -0.7523, 0.3561, -0.2958, 0.4675, 0, 0, 0, 0, 0 },
				{ 0.8159, -0.0423, 0.4063, 0.3455, -0.2192, 0, 0, 0, 0, 0 },
				{ -0.3499, 0.3421, 0.8227, -0.2190, -0.1889, 0, 0, 0, 0, 0 },
				{ -0.0963, -0.4747, -0.0998, -0.2429, -0.8345, 0, 0, 0, 0, 0 },
				{ -0.4487, -0.2998, 0.1460, 0.8283, -0.0363, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_10D = { 0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1,
				1 };
		double[][] M_30D = {
				{ 0.0128, 0.2165, 0.4374, -0.0800, 0.0886, -0.2015, 0.1071,
						0.2886, 0.2354, 0.2785, -0.1748, 0.2147, 0.1649,
						-0.3043, 0.5316, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.4813, 0.2420, -0.3663, -0.0420, -0.0088, -0.4945, -0.3073,
						0.1990, 0.0441, -0.0627, 0.0191, 0.3880, -0.0618,
						-0.0319, -0.1833, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.4816, -0.2254, 0.0663, 0.4801, 0.2009, -0.0008, -0.1501,
						0.0269, -0.2037, 0.4334, -0.2157, -0.3175, -0.0923,
						0.1451, 0.1118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.0876, -0.2667, -0.0063, 0.2114, 0.4506, 0.0823, -0.0125,
						0.2313, 0.0840, -0.2376, 0.1938, -0.0030, 0.3391,
						0.0863, 0.1231, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1025, 0.4011, -0.0117, 0.2076, 0.2585, 0.1124, -0.0288,
						0.3095, -0.6146, -0.2376, 0.1938, -0.0030, 0.3391,
						0.0863, 0.1231, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.4543, -0.2761, -0.2985, -0.2837, 0.0634, 0.1070, 0.2996,
						-0.2690, -0.1634, -0.1452, 0.1799, -0.0014, 0.2394,
						-0.2745, 0.3969, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1422, -0.4364, 0.0751, -0.2235, 0.3966, -0.0252, 0.0908,
						0.0477, -0.2254, 0.1801, -0.0552, 0.5770, -0.0396,
						0.3765, -0.0522, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.3542, -0.2245, 0.3497, -0.1609, -0.1107, 0.0079, 0.2241,
						0.4517, 0.1309, -0.3355, -0.1123, -0.1831, 0.3000,
						0.2045, -0.3191, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.0005, 0.0377, -0.2808, -0.0641, 0.1316, 0.2191, 0.0207,
						0.3308, 0.4117, 0.3839, 0.5775, -0.1219, 0.1192,
						0.2435, 0.0414, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1177, -0.0001, -0.1992, -0.4533, 0.4234, -0.0191, -0.3740,
						0.1325, 0.0972, -0.2042, -0.3493, -0.4018, -0.1087,
						0.0918, 0.2217, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.1818, 0.3022, -0.1388, -0.2380, -0.0773, 0.6463, 0.0450,
						0.1030, -0.0958, 0.2837, -0.3969, 0.1779, -0.0251,
						-0.1543, -0.2452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ -0.1889, -0.4397, -0.2206, 0.0981, -0.5203, 0.1325, -0.3427,
						0.4242, -0.1271, -0.0291, -0.0795, 0.1213, 0.0565,
						-0.1092, 0.2720, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ -0.1808, -0.0624, -0.2689, 0.2289, 0.1128, -0.0844, -0.0549,
						-0.2202, 0.2450, 0.0825, -0.3319, 0.0513, 0.7523,
						0.0043, -0.1472, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ -0.0983, 0.0611, -0.4145, 0.3017, 0.0410, -0.0703, 0.6250,
						0.2449, 0.1307, -0.1714, -0.3045, 0.0218, -0.2837,
						0.1408, 0.1633, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0.2026, 0.0324, 0.1496, 0.3129, 0.1437, 0.4331, -0.2629,
						-0.1498, 0.3746, -0.4366, 0.0163, 0.3316, -0.0697,
						0.1833, 0.2412, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_30D = { 0.113, 0.105, 0.117, 0.119, 0.108, 0.110,
				0.101, 0.107, 0.111, 0.109, 0.120, 0.108, 0.101, 0.105, 0.116,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
				1.000, 1.000, 1.000, 1.000, 1.000, 1.000 };
		double[] z = new double[nx];
		double[] zz = new double[nx];
		double[] p = new double[nx];
		double[] psum = new double[n_obj];
		double[][] M = new double[nx][nx];
		double[] lamda_l = new double[nx];
		
		if (nx == 10) {
			for (int i = 0; i < nx; i++) {
				for (int j = 0; j < nx; j++) {
					M[i][j] = M_10D[i][j];
				}
				
				lamda_l[i] = lamda_l_10D[i];
			}
		} else {
			for (int i = 0; i < nx; i++) {
				for (int j = 0; j < nx; j++) {
					M[i][j] = M_30D[i][j];
				}
				
				lamda_l[i] = lamda_l_30D[i];
			}
		}
		
		for (int i = 0; i < nx; i++) {
			z[i] = 0;
			
			for (int j = 0; j < nx; j++) {
				z[i] += M[i][j] * x[j];
			}
			
			if (z[i] >= 0 && z[i] <= 1) {
				zz[i] = z[i];
				p[i] = 0;
			} else if (z[i] < 0) {
				zz[i] = -lamda_l[i] * z[i];
				p[i] = -z[i];
			} else {
				zz[i] = 1 - lamda_l[i] * (z[i] - 1);
				p[i] = z[i] - 1;
			}
		}
		
		for (int j = 0; j < n_obj; j++) {
			psum[j] = 0;
		}
		
		for (int i = nx - k + 1; i <= nx; i++) {
			g += Math.pow(zz[i - 1] - 0.5, 2);
			
			for (int j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		
		for (int i = 1; i <= n_obj; i++) {
			double ff = (1 + g);
			
			for (int j = n_obj - i; j >= 1; j--) {
				ff *= Math.cos(zz[j - 1] * PI / 2.0);
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[j - 1], 2));
			}
			
			if (i > 1) {
				ff *= Math.sin(zz[(n_obj - i + 1) - 1] * PI / 2.0);
				psum[i - 1] = Math.sqrt(Math.pow(psum[i - 1], 2)
						+ Math.pow(p[(n_obj - i + 1) - 1], 2));
			}
			
			f[i - 1] = 2.0 / (1 + Math.exp(-psum[i - 1])) * (ff + 1);
		}
	}

}
