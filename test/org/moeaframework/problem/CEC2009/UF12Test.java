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

public class UF12Test {
	
	@Test
	public void test() {
		UF12 uf12 = new UF12();
		RandomInitialization initialization = new RandomInitialization(uf12, 
				TestThresholds.SAMPLES);
		
		for (Solution solution : initialization.initialize()) {
			double[] x = EncodingUtils.getReal(solution);
			double[] f = new double[uf12.getNumberOfObjectives()];
			
			R3_DTLZ3_M5(x, f, uf12.getNumberOfVariables(),
					uf12.getNumberOfObjectives());
			
			uf12.evaluate(solution);
			
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
	 * Evaluates the UF12 problem.  This is the version of the code provided by
	 * the CEC 2009 test suite.
	 * 
	 * @param x the decision variables
	 * @param f the objective function output
	 * @param nx the number of decision variables
	 * @param n_obj the number of objectives
	 */
	public static void R3_DTLZ3_M5(double[] x, double[] f, int nx, int n_obj) {
		int k = nx - n_obj + 1;
		double g = 0;
		double[][] M_10D = {
				{ -0.2861, 0.2796, -0.8507, 0.2837, 0.1893, 0, 0, 0, 0, 0 },
				{ 0.2837, 0.8861, 0.1219, -0.3157, 0.1407, 0, 0, 0, 0, 0 },
				{ 0.6028, 0.1119, -0.0810, 0.5963, -0.5119, 0, 0, 0, 0, 0 },
				{ -0.6450, 0.3465, 0.4447, 0.4753, -0.2005, 0, 0, 0, 0, 0 },
				{ 0.2414, -0.0635, 0.2391, 0.4883, 0.8013, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
		double[] lamda_l_10D = { 0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1,
				1 };
		double[][] M_30D = {
				{ -0.1565, -0.2418, 0.5427, -0.2191, 0.2522, -0.0563, 0.1991,
						0.1166, 0.2140, -0.0973, -0.0755, 0.4073, 0.4279,
						-0.1876, -0.0968, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.1477, -0.2396, -0.0022, 0.4180, 0.2675, -0.1365, -0.0729,
						0.4761, -0.0685, 0.2105, 0.1388, 0.1465, -0.0256,
						0.0292, 0.5767, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.0322, 0.3727, -0.0467, 0.1651, -0.0672, 0.0638, -0.1168,
						0.4055, 0.6714, -0.1948, -0.1451, 0.1734, -0.2788,
						-0.0769, -0.1433, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ -0.3688, 0.1935, 0.3691, 0.4298, 0.2340, 0.2593, -0.3081,
						-0.2013, -0.2779, -0.0932, 0.0003, 0.0149, -0.2303,
						-0.3261, -0.0517, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.0580, -0.0609, 0.0004, -0.1831, 0.0003, 0.4742, -0.2530,
						-0.0750, 0.0839, 0.1606, 0.6020, 0.4103, -0.0857,
						0.2954, -0.0819, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.2145, -0.0056, -0.0251, 0.2288, -0.4870, -0.5486, 0.1253,
						-0.1512, -0.0390, 0.0722, 0.3074, 0.4160, -0.1304,
						-0.1610, -0.0848, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.2557, -0.1087, 0.0679, -0.3120, 0.3567, -0.4644, -0.3535,
						0.1060, -0.2158, -0.1330, -0.0154, 0.0911, -0.4154,
						0.0356, -0.3085, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.2303, 0.4996, 0.1883, 0.1870, 0.1850, -0.0216, 0.4409,
						-0.0573, -0.2396, 0.1471, -0.1540, 0.2731, -0.0398,
						0.4505, -0.1131, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ -0.1576, -0.0023, 0.2588, 0.2105, 0.2250, -0.2978, 0.0175,
						-0.1157, 0.3717, 0.0562, 0.4068, -0.5081, 0.0718,
						0.3443, -0.1488, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.1047, -0.0568, -0.2771, 0.3803, 0.0046, 0.0188, -0.1500,
						0.2053, -0.2290, -0.4582, 0.1191, 0.0639, 0.4946,
						0.1121, -0.4018, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0.000, 0.000 },
				{ 0.3943, -0.0374, 0.3004, 0.1472, -0.2988, 0.0443, -0.2483,
						0.1350, -0.0160, 0.5834, -0.1095, -0.1398, 0.1711,
						-0.1867, -0.3518, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0.000, 0.000 },
				{ 0.1244, -0.6134, 0.1823, 0.3012, -0.1968, 0.1616, 0.1025,
						-0.1972, 0.1162, -0.2079, -0.3062, 0.0585, -0.3286,
						0.3187, -0.0812, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0.1832, -0.1559, -0.4327, 0.2059, 0.4677, 0.0317, 0.2233,
						-0.3589, 0.2393, 0.2468, 0.0148, 0.1193, -0.0279,
						-0.3600, -0.2261, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0 },
				{ 0.5027, 0.1935, 0.1571, 0.0503, -0.0503, -0.1443, -0.3080,
						-0.4939, 0.1847, -0.2762, 0.0042, 0.0960, 0.2239,
						-0.0579, 0.3840, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0 },
				{ 0.3948, -0.0002, 0.2172, -0.0293, -0.0835, 0.1614, 0.4559,
						0.1626, -0.1155, -0.3087, 0.4331, -0.2223, -0.2213,
						-0.3658, -0.0188, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0 },
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
			g += Math.pow(zz[i - 1] - 0.5, 2)
					- Math.cos(20 * PI * (zz[i - 1] - 0.5));
			
			for (int j = 0; j < n_obj; j++) {
				psum[j] = Math.sqrt(Math.pow(psum[j], 2)
						+ Math.pow(p[i - 1], 2));
			}
		}
		
		g = 100 * (k + g);
		
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
