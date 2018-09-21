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
import org.moeaframework.problem.DTLZ.DTLZ3;

/**
 * The unconstrained UF12 test problem from the CEC 2009 special session and
 * competition.
 */
public class UF12 extends AbstractProblem {

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
	
	/**
	 * The scaling factors for the instance with 30 decision variables.
	 */
	private static final double[] lamda_l_30D = { 
			0.113, 0.105, 0.117, 0.119, 0.108, 0.110, 0.101, 0.107, 0.111,
			0.109, 0.120, 0.108, 0.101, 0.105, 0.116, 1.000, 1.000, 1.000,
			1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
			1.000, 1.000, 1.000 };
	
	/**
	 * The nested DTLZ3 problem instance.
	 */
	private final DTLZ3 problem;

	/**
	 * Constructs a UF12 test problem with 30 decision variables and 5
	 * objectives.
	 */
	public UF12() {
		this(30, 5);
	}

	/**
	 * Constructs a UF12 test problem with the specified number of decision
	 * variables and objectives.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 */
	public UF12(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		
		if ((numberOfVariables != 10) && (numberOfVariables != 30)) {
			throw new IllegalArgumentException(
					"number of variables must be 10 or 30");
		}
		
		problem = new DTLZ3(numberOfVariables, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] psum = new double[numberOfObjectives];
		double[] zz = new double[numberOfVariables];
		
		//apply transform to convert from UF12 to DTLZ3
		CEC2009.transform(x, zz, psum, 
				numberOfVariables == 10 ? M_10D : M_30D, 
				numberOfVariables == 10 ? lamda_l_10D : lamda_l_30D,
				numberOfVariables, numberOfObjectives);
		
		//evaluate the transformed solution with DTLZ3
		Solution transformedSolution = problem.newSolution();
		EncodingUtils.setReal(transformedSolution, zz);
		problem.evaluate(transformedSolution);
		
		//convert the DTLZ3 results back to UF12
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
