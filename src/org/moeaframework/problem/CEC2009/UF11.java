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
package org.moeaframework.problem.CEC2009;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * The unconstrained UF11 test problem from the CEC 2009 special session and
 * competition.
 */
public class UF11 extends AbstractProblem {

	/**
	 * The decision variable lower and upper bounds for the instance with 10 
	 * decision variables.
	 */
	private static final double[][] bound_10D = {
			{ -1.118, -0.951, -2.055, -0.472, -1.070, 0, 0, 0, 0, 0 },
			{ 0.899, 1.257, 0, 1.244, 0.869, 1, 1, 1, 1, 1 } };

	/**
	 * The rotation matrix for the instance with 10 decision variables.
	 */
	private static final double[][] M_10D = {
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
	
	/**
	 * The scaling factors for the instance with 10 decision variables.
	 */
	private static final double[] lamda_l_10D = { 
			0.313, 0.312, 0.321, 0.316, 0.456, 1, 1, 1, 1, 1 };
	
	/**
	 * The decision variable lower and upper bounds for the instance with 30 
	 * decision variables.
	 */
	private static final double[][] bound_30D = {
			{ -1.773, -1.846, -1.053, -2.370, -1.603, -1.878, -1.677, -0.935,
					-1.891, -0.964, -0.885, -1.690, -2.235, -1.541, -0.720,
					0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000,
					0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000 },
			{ 1.403, 1.562, 2.009, 0.976, 1.490, 1.334, 1.074, 2.354, 1.462,
					2.372, 2.267, 1.309, 0.842, 1.665, 2.476, 1.000, 1.000,
					1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
					1.000, 1.000, 1.000, 1.000, 1.000 } };
	
	/**
	 * The rotation matrix for the instance with 30 decision variables.
	 */
	private static final double[][] M_30D = {
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
	
	/**
	 * The scaling factors for the instance with 30 decision variables.
	 */
	private static final double[] lamda_l_30D = { 
			0.113, 0.105, 0.117, 0.119, 0.108, 0.110, 0.101, 0.107, 0.111,
			0.109, 0.120, 0.108, 0.101, 0.105, 0.116, 1.000, 1.000, 1.000,
			1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
			1.000, 1.000, 1.000 };
	
	/**
	 * The nested DTLZ2 problem instance.
	 */
	private final DTLZ2 problem;

	/**
	 * Constructs a UF11 test problem with 30 decision variables and 5
	 * objectives.
	 */
	public UF11() {
		this(30, 5);
	}

	/**
	 * Constructs a UF11 test problem with the specified number of decision
	 * variables and objectives.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 */
	public UF11(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		
		if ((numberOfVariables != 10) && (numberOfVariables != 30)) {
			throw new IllegalArgumentException(
					"number of variables must be 10 or 30");
		}
		
		problem = new DTLZ2(numberOfVariables, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] psum = new double[numberOfObjectives];
		double[] zz = new double[numberOfVariables];
		
		//apply transform to convert from UF11 to DTLZ2
		CEC2009.transform(x, zz, psum, 
				numberOfVariables == 10 ? M_10D : M_30D, 
				numberOfVariables == 10 ? lamda_l_10D : lamda_l_30D,
				numberOfVariables, numberOfObjectives);
		
		//evaluate the transformed solution with DTLZ2
		Solution transformedSolution = problem.newSolution();
		EncodingUtils.setReal(transformedSolution, zz);
		problem.evaluate(transformedSolution);
		
		//convert the DTLZ2 results back to UF11
		for (int i=0; i<numberOfObjectives; i++) {
			solution.setObjective(i, 2.0 / (1.0 + Math.exp(-psum[i])) * 
					(transformedSolution.getObjective(i) + 1));
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(
				numberOfVariables == 10 ? bound_10D[0][i] : bound_30D[0][i],
				numberOfVariables == 10 ? bound_10D[1][i] : bound_30D[1][i]));
		}

		return solution;
	}

}
