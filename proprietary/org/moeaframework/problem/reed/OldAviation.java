package org.moeaframework.problem.reed;

import static java.lang.Math.pow;

import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Original implementation of the {@link Aviation} problem, now used during
 * testing to ensure equality between the implementations.
 */
class OldAviation extends AbstractProblem {

	/**
	 * Constructs the General Aviation Aircraft (GAA) problem instance.
	 */
	public OldAviation() {
		super(27, 10, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] xreal = CoreUtils.castVariablesToDoubleArray(solution);
		double[] obj = new double[solution.getNumberOfObjectives()];
		double[] constr = new double[solution.getNumberOfConstraints()];

		double WEMP2_GOAL = 1900;
		double DOC2_GOAL = 60;
		double WFUEL2_GOAL = 450;
		double PURCH2_GOAL = 41000;
		double RANGE2_GOAL = 2500;
		double LDMAX2_GOAL = 17;
		double VCMAX2_GOAL = 200;

		double WEMP4_GOAL = 1950;
		double DOC4_GOAL = 60;
		double WFUEL4_GOAL = 400;
		double PURCH4_GOAL = 42000;
		double RANGE4_GOAL = 2500;
		double LDMAX4_GOAL = 17;
		double VCMAX4_GOAL = 200;

		double WEMP6_GOAL = 2000;
		double DOC6_GOAL = 60;
		double WFUEL6_GOAL = 350;
		double PURCH6_GOAL = 43000;
		double RANGE6_GOAL = 2500;
		double LDMAX6_GOAL = 17;
		double VCMAX6_GOAL = 200;

		double max_NOISE = 0;
		double max_WEMP = 0;
		double max_DOC = 0;
		double max_ROUGH = 0;
		double max_WFUEL = 0;
		double max_PURCH = 0;
		double min_RANGE = 200000;
		double min_LDMAX = 200000;
		double min_VCMAX = 200000;

		// Decision Variables
		double CSPD2 = (xreal[0] - 0.36) / 0.12;
		double AR2 = (xreal[1] - 9) / 2;
		double SWEEP2 = (xreal[2] - 3) / 3;
		double DPROP2 = (xreal[3] - 5.734) / 0.234;
		double WINGLD2 = (xreal[4] - 22) / 3;
		double AF2 = (xreal[5] - 97.5) / 12.5;
		double SEATW2 = (xreal[6] - 17) / 3;
		double ELODT2 = (xreal[7] - 3.375) / 0.375;
		double TAPER2 = (xreal[8] - 0.73) / 0.27;

		double CSPD4 = (xreal[9] - 0.36) / 0.12;
		double AR4 = (xreal[10] - 9) / 2;
		double SWEEP4 = (xreal[11] - 3) / 3;
		double DPROP4 = (xreal[12] - 5.734) / 0.234;
		double WINGLD4 = (xreal[13] - 22) / 3;
		double AF4 = (xreal[14] - 97.5) / 12.5;
		double SEATW4 = (xreal[15] - 17) / 3;
		double ELODT4 = (xreal[16] - 3.375) / 0.375;
		double TAPER4 = (xreal[17] - 0.73) / 0.27;

		double CSPD6 = (xreal[18] - 0.36) / 0.12;
		double AR6 = (xreal[19] - 9) / 2;
		double SWEEP6 = (xreal[20] - 3) / 3;
		double DPROP6 = (xreal[21] - 5.734) / 0.234;
		double WINGLD6 = (xreal[22] - 22) / 3;
		double AF6 = (xreal[23] - 97.5) / 12.5;
		double SEATW6 = (xreal[24] - 17) / 3;
		double ELODT6 = (xreal[25] - 3.375) / 0.375;
		double TAPER6 = (xreal[26] - 0.73) / 0.27;

		// Product Family Penalty Function
		double x1 = (xreal[0] + xreal[9] + xreal[18]) / 3;
		double x2 = (xreal[1] + xreal[10] + xreal[19]) / 3;
		double x3 = (xreal[2] + xreal[11] + xreal[20]) / 3;
		double x4 = (xreal[3] + xreal[12] + xreal[21]) / 3;
		double x5 = (xreal[4] + xreal[13] + xreal[22]) / 3;
		double x6 = (xreal[5] + xreal[14] + xreal[23]) / 3;
		double x7 = (xreal[6] + xreal[15] + xreal[24]) / 3;
		double x8 = (xreal[7] + xreal[16] + xreal[25]) / 3;
		double x9 = (xreal[8] + xreal[17] + xreal[26]) / 3;

		double var1 = pow(
				((pow((xreal[0] - x1), 2) + pow((xreal[9] - x1), 2) + pow(
						(xreal[18] - x1), 2)) / 2), 0.5);
		double var2 = pow(
				((pow((xreal[1] - x2), 2) + pow((xreal[10] - x2), 2) + pow(
						(xreal[19] - x2), 2)) / 2), 0.5);
		double var3 = pow(
				((pow((xreal[2] - x3), 2) + pow((xreal[11] - x3), 2) + pow(
						(xreal[20] - x3), 2)) / 2), 0.5);
		double var4 = pow(
				((pow((xreal[3] - x4), 2) + pow((xreal[12] - x4), 2) + pow(
						(xreal[21] - x4), 2)) / 2), 0.5);
		double var5 = pow(
				((pow((xreal[4] - x5), 2) + pow((xreal[13] - x5), 2) + pow(
						(xreal[22] - x5), 2)) / 2), 0.5);
		double var6 = pow(
				((pow((xreal[5] - x6), 2) + pow((xreal[14] - x6), 2) + pow(
						(xreal[23] - x6), 2)) / 2), 0.5);
		double var7 = pow(
				((pow((xreal[6] - x7), 2) + pow((xreal[15] - x7), 2) + pow(
						(xreal[24] - x7), 2)) / 2), 0.5);
		double var8 = pow(
				((pow((xreal[7] - x8), 2) + pow((xreal[16] - x8), 2) + pow(
						(xreal[25] - x8), 2)) / 2), 0.5);
		double var9 = pow(
				((pow((xreal[8] - x9), 2) + pow((xreal[17] - x9), 2) + pow(
						(xreal[26] - x9), 2)) / 2), 0.5);

		double pvar1 = var1 / x1;
		double pvar2 = var2 / x2;
		double pvar3 = var3 / x3;
		double pvar4 = var4 / x4;
		double pvar5 = var5 / x5;
		double pvar6 = var6 / x6;
		double pvar7 = var7 / x7;
		double pvar8 = var8 / x8;
		double pvar9 = var9 / x9;

		// Response Variables
		double NOISE2 = 74.099998 - 0.0004 * CSPD2 - 0.0156 * AR2 + 0.0003
				* SWEEP2 + 0.9684 * DPROP2 + 0.0316 * WINGLD2 - 0.0053 * AF2
				- 0.0015 * SEATW2 - 0.0002 * ELODT2 + 0.0007 * TAPER2 - 0.0001
				* CSPD2 * DPROP2 - 0.0001 * CSPD2 * WINGLD2 - 0.0001 * CSPD2
				* AF2 + 0 * CSPD2 * SEATW2 + 0.0001 * CSPD2 * ELODT2 + 0 * AR2
				* SWEEP2 - 0.0012 * AR2 * DPROP2 - 0.0014 * AR2 * WINGLD2
				- 0.0002 * AR2 * AF2 + 0 * AR2 * SEATW2 + 0.0003 * AR2 * ELODT2
				+ 0.0001 * AR2 * TAPER2 + 0 * SWEEP2 * TAPER2 - 0.0003 * DPROP2
				* WINGLD2 + 0.0058 * DPROP2 * AF2 - 0.0001 * DPROP2 * SEATW2
				+ 0.0002 * DPROP2 * ELODT2 - 0.0001 * DPROP2 * TAPER2 + 0.0002
				* WINGLD2 * AF2 - 0.0003 * WINGLD2 * SEATW2 - 0.0002 * WINGLD2
				* ELODT2 + 0.0001 * WINGLD2 * TAPER2 - 0.0001 * AF2 * SEATW2
				+ 0 * AF2 * TAPER2 - 0.0001 * SEATW2 * ELODT2 - 0.0001 * SEATW2
				* TAPER2 + 0 * ELODT2 * TAPER2 + 0.0008 * pow(CSPD2, 2)
				+ 0.0016 * pow(AR2, 2) + 0.0011 * pow(SWEEP2, 2) + 0.1105
				* pow(DPROP2, 2) + 0.0004 * pow(WINGLD2, 2) - 0.0019
				* pow(AF2, 2) + 0.0009 * pow(SEATW2, 2) + 0.001
				* pow(ELODT2, 2) + 0.0007 * pow(TAPER2, 2);
		double WEMP2 = 1917 + 5.979 * CSPD2 + 35.130001 * AR2 - 0.7119 * SWEEP2
				+ 11.11 * DPROP2 - 32.290001 * WINGLD2 + 5.739 * AF2
				+ 48.110001 * SEATW2 + 0.3376 * ELODT2 + 15.28 * TAPER2 + 1.244
				* CSPD2 * AR2 - 0.1315 * CSPD2 * SWEEP2 + 1.129 * CSPD2
				* DPROP2 - 2.393 * CSPD2 * WINGLD2 + 0.3954 * CSPD2 * AF2
				- 0.4978 * CSPD2 * SEATW2 - 0.3882 * CSPD2 * ELODT2 + 0.5742
				* CSPD2 * TAPER2 - 0.2236 * AR2 * SWEEP2 - 0.0739 * AR2
				* DPROP2 - 3.805 * AR2 * WINGLD2 - 0.0164 * AR2 * AF2 - 0.0923
				* AR2 * SEATW2 - 0.9326 * AR2 * ELODT2 + 3.135 * AR2 * TAPER2
				- 0.0385 * SWEEP2 * DPROP2 + 0.4376 * SWEEP2 * WINGLD2 + 0.0259
				* SWEEP2 * AF2 + 0.4009 * SWEEP2 * SEATW2 + 0.3002 * SWEEP2
				* ELODT2 + 0.7036 * SWEEP2 * TAPER2 - 0.2083 * DPROP2 * WINGLD2
				+ 1.165 * DPROP2 * AF2 - 0.2119 * DPROP2 * SEATW2 - 0.1934
				* DPROP2 * ELODT2 + 0.1462 * DPROP2 * TAPER2 - 0.0644 * WINGLD2
				* AF2 + 3.194 * WINGLD2 * SEATW2 + 2.672 * WINGLD2 * ELODT2
				- 0.4407 * WINGLD2 * TAPER2 - 0.057 * AF2 * SEATW2 - 0.0529
				* AF2 * ELODT2 + 0.0341 * AF2 * TAPER2 + 4.88 * SEATW2 * ELODT2
				+ 1.349 * SEATW2 * TAPER2 + 0.8836 * ELODT2 * TAPER2 + 0.958
				* pow(CSPD2, 2) - 1.812 * pow(AR2, 2) + 1.173 * pow(SWEEP2, 2)
				+ 0.753 * pow(DPROP2, 2) + 3.638 * pow(WINGLD2, 2) + 0.133
				* pow(AF2, 2) + 5.323 * pow(SEATW2, 2) + 1.478 * pow(ELODT2, 2)
				- 0.192 * pow(TAPER2, 2);
		double DOC2 = 83.17 + 12.53 * CSPD2 - 0.0477 * AR2 - 0.0215 * SWEEP2
				+ 3.597 * DPROP2 - 0.7367 * WINGLD2 + 0.7481 * AF2 + 0.733
				* SEATW2 - 0.2029 * ELODT2 + 0.0393 * TAPER2 + 0.6526 * CSPD2
				* AR2 + 0.0481 * CSPD2 * SWEEP2 + 1.208 * CSPD2 * DPROP2
				+ 0.6802 * CSPD2 * WINGLD2 + 0.0992 * CSPD2 * AF2 - 0.7074
				* CSPD2 * SEATW2 + 0.2768 * CSPD2 * ELODT2 + 0.0109 * CSPD2
				* TAPER2 + 0.0031 * AR2 * SWEEP2 + 0.2146 * AR2 * DPROP2
				- 0.0721 * AR2 * WINGLD2 - 0.2445 * AR2 * AF2 - 0.0172 * AR2
				* SEATW2 + 0.0127 * AR2 * ELODT2 + 0.0087 * AR2 * TAPER2
				+ 0.0169 * SWEEP2 * DPROP2 + 0.0151 * SWEEP2 * WINGLD2 - 0.0063
				* SWEEP2 * AF2 - 0.0001 * SWEEP2 * SEATW2 - 0.0042 * SWEEP2
				* ELODT2 - 0.0059 * SWEEP2 * TAPER2 + 0.0789 * DPROP2 * WINGLD2
				+ 0.676 * DPROP2 * AF2 - 0.1912 * DPROP2 * SEATW2 + 0.0519
				* DPROP2 * ELODT2 - 0.0265 * DPROP2 * TAPER2 + 0.0136 * WINGLD2
				* AF2 + 0.0804 * WINGLD2 * SEATW2 + 0.0577 * WINGLD2 * ELODT2
				+ 0.017 * WINGLD2 * TAPER2 - 0.0617 * AF2 * SEATW2 + 0.0058
				* AF2 * ELODT2 - 0.0178 * AF2 * TAPER2 + 0.0901 * SEATW2
				* ELODT2 + 0.0047 * SEATW2 * TAPER2 - 0.003 * ELODT2 * TAPER2
				- 11.37 * pow(CSPD2, 2) - 0.2836 * pow(AR2, 2) - 0.3149
				* pow(SWEEP2, 2) + 5.337 * pow(DPROP2, 2) - 0.3711
				* pow(WINGLD2, 2) - 0.071 * pow(AF2, 2) - 0.2177
				* pow(SEATW2, 2) - 0.2354 * pow(ELODT2, 2) - 0.238
				* pow(TAPER2, 2);
		double ROUGH2 = 2.197 - 0.0002 * CSPD2 + 0.1541 * AR2 - 0.0012 * SWEEP2
				+ 0.0222 * DPROP2 - 0.1611 * WINGLD2 - 0.0012 * AF2 - 0.0628
				* SEATW2 - 0.011 * ELODT2 + 0.0068 * TAPER2 + 0.0006 * CSPD2
				* AR2 + 0.0001 * CSPD2 * SWEEP2 + 0.001 * CSPD2 * WINGLD2
				+ 0.0001 * CSPD2 * SEATW2 - 0.0002 * CSPD2 * ELODT2 - 0.0006
				* CSPD2 * TAPER2 - 0.0001 * AR2 * SWEEP2 + 0.0006 * AR2
				* DPROP2 - 0.0113 * AR2 * WINGLD2 - 0.0001 * AR2 * AF2 - 0.0045
				* AR2 * SEATW2 - 0.0017 * AR2 * ELODT2 - 0.0011 * AR2 * TAPER2
				- 0.0002 * SWEEP2 * DPROP2 - 0.0004 * SWEEP2 * WINGLD2 - 0.0006
				* SWEEP2 * SEATW2 - 0.0003 * SWEEP2 * ELODT2 - 0.0001 * SWEEP2
				* TAPER2 - 0.0051 * DPROP2 * WINGLD2 - 0.0038 * DPROP2 * AF2
				+ 0.0022 * DPROP2 * SEATW2 - 0.0012 * DPROP2 * ELODT2 - 0.0002
				* DPROP2 * TAPER2 - 0.001 * WINGLD2 * AF2 - 0.0036 * WINGLD2
				* SEATW2 - 0.0025 * WINGLD2 * ELODT2 - 0.0025 * WINGLD2
				* TAPER2 + 0.0008 * AF2 * SEATW2 - 0.0003 * AF2 * ELODT2
				- 0.0001 * AF2 * TAPER2 - 0.001 * SEATW2 * ELODT2 - 0.0007
				* SEATW2 * TAPER2 + 0.0012 * pow(CSPD2, 2) - 0.0273
				* pow(AR2, 2) - 0.0048 * pow(SWEEP2, 2) + 0.0033
				* pow(DPROP2, 2) + 0.0062 * pow(WINGLD2, 2) + 0.0021
				* pow(AF2, 2) - 0.0016 * pow(SEATW2, 2) - 0.0011
				* pow(ELODT2, 2) - 0.0048 * pow(TAPER2, 2);
		double WFUEL2 = 416.399994 - 6.093 * CSPD2 - 31.91 * AR2 + 0.7968
				* SWEEP2 - 19.17 * DPROP2 + 34.189999 * WINGLD2 - 7.57 * AF2
				- 49.610001 * SEATW2 + 0.2331 * ELODT2 - 15.33 * TAPER2 - 1.201
				* CSPD2 * AR2 + 0.1735 * CSPD2 * SWEEP2 - 1.247 * CSPD2
				* DPROP2 + 1.703 * CSPD2 * WINGLD2 - 0.4588 * CSPD2 * AF2
				+ 0.1585 * CSPD2 * SEATW2 + 0.6156 * CSPD2 * ELODT2 - 0.528
				* CSPD2 * TAPER2 + 0.2215 * AR2 * SWEEP2 - 0.4976 * AR2
				* DPROP2 + 4.058 * AR2 * WINGLD2 - 0.108 * AR2 * AF2 + 0.2679
				* AR2 * SEATW2 + 0.8514 * AR2 * ELODT2 - 3.182 * AR2 * TAPER2
				+ 0.0359 * SWEEP2 * DPROP2 - 0.482 * SWEEP2 * WINGLD2 - 0.0207
				* SWEEP2 * AF2 - 0.3878 * SWEEP2 * SEATW2 - 0.3249 * SWEEP2
				* ELODT2 - 0.715 * SWEEP2 * TAPER2 + 0.3374 * DPROP2 * WINGLD2
				- 2.403 * DPROP2 * AF2 + 0.4519 * DPROP2 * SEATW2 + 0.1352
				* DPROP2 * ELODT2 - 0.123 * DPROP2 * TAPER2 + 0.2498 * WINGLD2
				* AF2 - 2.896 * WINGLD2 * SEATW2 - 3.016 * WINGLD2 * ELODT2
				+ 0.3662 * WINGLD2 * TAPER2 - 0.114 * AF2 * SEATW2 + 0.0571
				* AF2 * ELODT2 - 0.0222 * AF2 * TAPER2 - 4.689 * SEATW2
				* ELODT2 - 1.339 * SEATW2 * TAPER2 - 0.9311 * ELODT2 * TAPER2
				- 0.7538 * pow(CSPD2, 2) + 1.13 * pow(AR2, 2) - 1.078
				* pow(SWEEP2, 2) - 5.989 * pow(DPROP2, 2) - 3.043
				* pow(WINGLD2, 2) + 0.0627 * pow(AF2, 2) - 4.958
				* pow(SEATW2, 2) - 1.41 * pow(ELODT2, 2) + 0.3532
				* pow(TAPER2, 2);
		double PURCH2 = 43280 + 133.1 * CSPD2 + 780.400024 * AR2 - 1.501
				* SWEEP2 + 494 * DPROP2 + 191.2 * AF2 + 786 * SEATW2 + 102
				* ELODT2 + 333.9 * TAPER2 + 25.95 * CSPD2 * AR2 - 2.442 * CSPD2
				* SWEEP2 + 28.280001 * CSPD2 * DPROP2 - 50.509998 * CSPD2
				* WINGLD2 + 11.55 * CSPD2 * AF2 - 6.188 * CSPD2 * SEATW2
				- 4.326 * CSPD2 * ELODT2 + 13.78 * CSPD2 * TAPER2 - 3.335 * AR2
				* SWEEP2 + 0.4272 * AR2 * DPROP2 - 69.110001 * AR2 * WINGLD2
				- 331.8 * WINGLD2 - 0.8834 * AR2 * AF2 + 7.221 * AR2 * SEATW2
				- 20.389999 * AR2 * ELODT2 + 69.580002 * AR2 * TAPER2 - 1.107
				* SWEEP2 * DPROP2 + 7.078 * SWEEP2 * WINGLD2 + 0.0705 * SWEEP2
				* AF2 + 5.359 * SWEEP2 * SEATW2 + 4.936 * SWEEP2 * ELODT2
				+ 14.77 * SWEEP2 * TAPER2 - 21.82 * DPROP2 * WINGLD2 + 22.17
				* DPROP2 * AF2 + 14.2 * DPROP2 * SEATW2 - 7.537 * DPROP2
				* ELODT2 + 5.813 * DPROP2 * TAPER2 - 6.771 * WINGLD2 * AF2
				+ 27.99 * WINGLD2 * SEATW2 + 47.080002 * WINGLD2 * ELODT2
				- 9.888 * WINGLD2 * TAPER2 + 3.135 * AF2 * SEATW2 - 2.658 * AF2
				* ELODT2 + 1.033 * AF2 * TAPER2 + 82.230003 * SEATW2 * ELODT2
				+ 27.969999 * SEATW2 * TAPER2 + 16.110001 * ELODT2 * TAPER2
				+ 10.51 * pow(CSPD2, 2) - 49.189999 * pow(AR2, 2) + 26.51
				* pow(SWEEP2, 2) + 35.310001 * pow(DPROP2, 2) + 33.560001
				* pow(WINGLD2, 2) + 10.66 * pow(AF2, 2) + 74.309998
				* pow(SEATW2, 2) + 8.461 * pow(ELODT2, 2) - 0.7392
				* pow(TAPER2, 2);
		double RANGE2 = 1971 - 6.807 * CSPD2 - 76.230003 * AR2 + 2.885 * SWEEP2
				- 363.799988 * DPROP2 - 383.100006 * WINGLD2 - 68.099998 * AF2
				- 67.480003 * SEATW2 + 18.09 * ELODT2 - 165.699997 * TAPER2
				+ 0.084 * CSPD2 * AR2 + 0.2793 * CSPD2 * SWEEP2 + 1.674 * CSPD2
				* DPROP2 + 3.342 * CSPD2 * WINGLD2 + 0.1543 * CSPD2 * AF2
				- 0.4629 * CSPD2 * SEATW2 + 0.8574 * CSPD2 * ELODT2 + 2.537
				* CSPD2 * TAPER2 + 0.0098 * AR2 * SWEEP2 - 23.23 * AR2 * DPROP2
				- 25.799999 * AR2 * WINGLD2 - 1.787 * AR2 * AF2 + 9.432 * AR2
				* SEATW2 - 1.912 * AR2 * ELODT2 - 40.200001 * AR2 * TAPER2
				- 1.479 * SWEEP2 * DPROP2 - 1.311 * SWEEP2 * WINGLD2 - 0.2168
				* SWEEP2 * AF2 - 0.7168 * SWEEP2 * SEATW2 - 0.584 * SWEEP2
				* ELODT2 - 0.8652 * SWEEP2 * TAPER2 + 121.099998 * DPROP2
				* WINGLD2 - 26.709999 * DPROP2 * AF2 + 25.280001 * DPROP2
				* SEATW2 - 10.74 * DPROP2 * ELODT2 + 37.080002 * DPROP2
				* TAPER2 + 21.290001 * WINGLD2 * AF2 + 19.299999 * WINGLD2
				* SEATW2 - 4.318 * WINGLD2 * ELODT2 - 20.360001 * WINGLD2
				* TAPER2 + 3.611 * AF2 * SEATW2 - 1.678 * AF2 * ELODT2 + 7.541
				* AF2 * TAPER2 - 7.232 * SEATW2 * ELODT2 + 16.860001 * SEATW2
				* TAPER2 - 3.006 * ELODT2 * TAPER2 - 11.61 * pow(CSPD2, 2)
				+ 4.892 * pow(AR2, 2) - 11.61 * pow(SWEEP2, 2) - 84.110001
				* pow(DPROP2, 2) + 65.389999 * pow(WINGLD2, 2) - 7.108
				* pow(AF2, 2) - 15.61 * pow(SEATW2, 2) - 13.11 * pow(ELODT2, 2)
				+ 8.892 * pow(TAPER2, 2);
		double LDMAX2 = 17.780001 + 0.4845 * CSPD2 + 1.625 * AR2 + 0.0267
				* SWEEP2 - 0.0153 * DPROP2 - 0.5289 * WINGLD2 - 0.007 * AF2
				- 0.4965 * SEATW2 + 0.2108 * ELODT2 + 0.0302 * TAPER2 + 0.0598
				* CSPD2 * AR2 + 0.0019 * CSPD2 * SWEEP2 - 0.0085 * CSPD2
				* DPROP2 - 0.0146 * CSPD2 * WINGLD2 - 0.0018 * CSPD2 * AF2
				- 0.0014 * CSPD2 * SEATW2 + 0.0104 * CSPD2 * ELODT2 + 0.0044
				* CSPD2 * TAPER2 + 0.0053 * AR2 * SWEEP2 - 0.0024 * AR2
				* DPROP2 - 0.0612 * AR2 * WINGLD2 - 0.001 * AR2 * AF2 - 0.0517
				* AR2 * SEATW2 + 0.0128 * AR2 * ELODT2 + 0.0043 * AR2 * TAPER2
				- 0.0001 * SWEEP2 * DPROP2 - 0.0054 * SWEEP2 * WINGLD2 + 0
				* SWEEP2 * AF2 - 0.0072 * SWEEP2 * SEATW2 - 0.0039 * SWEEP2
				* ELODT2 + 0 * SWEEP2 * TAPER2 + 0.0002 * DPROP2 * WINGLD2
				- 0.0012 * DPROP2 * AF2 + 0.0016 * DPROP2 * SEATW2 - 0.0001
				* DPROP2 * ELODT2 + 0.0007 * DPROP2 * TAPER2 - 0.0001 * WINGLD2
				* AF2 - 0.0727 * WINGLD2 * SEATW2 - 0.0256 * WINGLD2 * ELODT2
				- 0.0033 * WINGLD2 * TAPER2 + 0.0008 * AF2 * SEATW2 - 0.0003
				* AF2 * ELODT2 + 0.0003 * AF2 * TAPER2 - 0.0525 * SEATW2
				* ELODT2 - 0.0071 * SEATW2 * TAPER2 - 0.0059 * ELODT2 * TAPER2
				- 0.0897 * pow(CSPD2, 2) - 0.1488 * pow(AR2, 2) - 0.0116
				* pow(SWEEP2, 2) - 0.0009 * pow(DPROP2, 2) - 0.0305
				* pow(ELODT2, 2) - 0.0009 * pow(TAPER2, 2) + 0.0157
				* pow(WINGLD2, 2) - 0.001 * pow(AF2, 2) - 0.0577
				* pow(SEATW2, 2);
		double VCMAX2 = 200.4 - 0.3799 * CSPD2 + 0.8236 * AR2 + 0.2168 * SWEEP2
				+ 1.74 * DPROP2 + 5.589 * WINGLD2 - 0.1683 * AF2 - 3.522
				* SEATW2 + 1.559 * ELODT2 + 0.2442 * TAPER2 - 0.0215 * CSPD2
				* AR2 + 0.0054 * CSPD2 * SWEEP2 - 0.0465 * CSPD2 * DPROP2
				- 0.0144 * CSPD2 * WINGLD2 - 0.0069 * CSPD2 * AF2 + 0.0578
				* CSPD2 * SEATW2 + 0.0557 * CSPD2 * ELODT2 + 0.0221 * CSPD2
				* TAPER2 + 0.0175 * AR2 * SWEEP2 - 0.0942 * AR2 * DPROP2
				+ 0.1402 * AR2 * WINGLD2 - 0.0079 * AR2 * AF2 + 0.0147 * AR2
				* SEATW2 - 0.071 * AR2 * ELODT2 - 0.0104 * AR2 * TAPER2
				- 0.0067 * SWEEP2 * DPROP2 - 0.0306 * SWEEP2 * WINGLD2 - 0.0078
				* SWEEP2 * AF2 - 0.0534 * SWEEP2 * SEATW2 - 0.0224 * SWEEP2
				* ELODT2 + 0.0026 * SWEEP2 * TAPER2 - 0.2771 * DPROP2 * WINGLD2
				- 0.3476 * DPROP2 * AF2 + 0.2093 * DPROP2 * SEATW2 - 0.0864
				* DPROP2 * ELODT2 - 0.0051 * DPROP2 * TAPER2 - 0.0948 * WINGLD2
				* AF2 - 0.7081 * WINGLD2 * SEATW2 - 0.1219 * WINGLD2 * ELODT2
				- 0.016 * WINGLD2 * TAPER2 + 0.0555 * AF2 * SEATW2 - 0.0342
				* AF2 * ELODT2 - 0.0049 * AF2 * TAPER2 - 0.3658 * SEATW2
				* ELODT2 - 0.046 * SEATW2 * TAPER2 - 0.0504 * ELODT2 * TAPER2
				- 0.1452 * pow(CSPD2, 2) - 0.2937 * pow(AR2, 2) + 0.0303
				* pow(SWEEP2, 2) + 0.1378 * pow(DPROP2, 2) - 0.6522
				* pow(WINGLD2, 2) + 0.1378 * pow(AF2, 2) - 0.6102
				* pow(SEATW2, 2) - 0.3722 * pow(ELODT2, 2) + 0.0303
				* pow(TAPER2, 2);

		double NOISE4 = 74.099998 - 0.0005 * CSPD4 - 0.0158 * AR4 + 0.0003
				* SWEEP4 + 0.9682 * DPROP4 + 0.0316 * WINGLD4 - 0.0053 * AF4
				- 0.0014 * SEATW4 - 0.0003 * ELODT4 + 0.0008 * TAPER4 + 0
				* CSPD4 * SWEEP4 - 0.0001 * CSPD4 * DPROP4 - 0.0001 * CSPD4
				* WINGLD4 - 0.0001 * CSPD4 * AF4 + 0.0001 * CSPD4 * ELODT4 + 0
				* AR4 * SWEEP4 - 0.0012 * AR4 * DPROP4 - 0.0014 * AR4 * WINGLD4
				- 0.0002 * AR4 * AF4 + 0.0002 * AR4 * ELODT4 + 0.0002 * AR4
				* TAPER4 + 0 * SWEEP4 * TAPER4 - 0.0003 * DPROP4 * WINGLD4
				+ 0.0057 * DPROP4 * AF4 - 0.0001 * DPROP4 * SEATW4 + 0.0001
				* DPROP4 * ELODT4 + 0.0002 * WINGLD4 * AF4 - 0.0002 * WINGLD4
				* SEATW4 - 0.0001 * WINGLD4 * ELODT4 + 0.0001 * WINGLD4
				* TAPER4 - 0.0001 * AF4 * SEATW4 + 0 * AF4 * TAPER4 - 0.0001
				* SEATW4 * ELODT4 + 0.0008 * pow(CSPD4, 2) + 0.0016
				* pow(AR4, 2) + 0.0011 * pow(SWEEP4, 2) + 0.1104
				* pow(DPROP4, 2) + 0.0003 * pow(WINGLD4, 2) - 0.0019
				* pow(AF4, 2) + 0.001 * pow(SEATW4, 2) + 0.001 * pow(ELODT4, 2)
				+ 0.0007 * pow(TAPER4, 2);
		double WEMP4 = 1947 + 6.338 * CSPD4 + 33.869999 * AR4 - 0.448 * SWEEP4
				+ 11 * DPROP4 - 30.85 * WINGLD4 + 5.723 * AF4 + 53.220001
				* SEATW4 + 1.896 * ELODT4 + 15.26 * TAPER4 + 1.963 * CSPD4
				* AR4 - 0.1599 * CSPD4 * SWEEP4 + 1.073 * CSPD4 * DPROP4
				- 1.699 * CSPD4 * WINGLD4 + 0.462 * CSPD4 * AF4 - 0.9528
				* CSPD4 * SEATW4 - 0.9851 * CSPD4 * ELODT4 + 0.5956 * CSPD4
				* TAPER4 + 0.0065 * AR4 * SWEEP4 + 0.0874 * AR4 * DPROP4
				- 3.447 * AR4 * WINGLD4 + 0.1024 * AR4 * AF4 - 0.1814 * AR4
				* SEATW4 + 0.7878 * AR4 * ELODT4 + 1.592 * AR4 * TAPER4
				+ 0.0288 * SWEEP4 * DPROP4 + 0.3498 * SWEEP4 * WINGLD4 + 0.034
				* SWEEP4 * AF4 + 0.3134 * SWEEP4 * SEATW4 + 0.2289 * SWEEP4
				* ELODT4 + 0.5603 * SWEEP4 * TAPER4 - 0.1862 * DPROP4 * WINGLD4
				+ 1.061 * DPROP4 * AF4 - 0.0774 * DPROP4 * SEATW4 - 0.2335
				* DPROP4 * ELODT4 + 0.1385 * DPROP4 * TAPER4 - 0.0914 * WINGLD4
				* AF4 + 1.932 * WINGLD4 * SEATW4 + 1.853 * WINGLD4 * ELODT4
				- 0.8019 * WINGLD4 * TAPER4 - 0.0754 * AF4 * SEATW4 - 0.1306
				* AF4 * ELODT4 + 0.1086 * AF4 * TAPER4 + 4.81 * SEATW4 * ELODT4
				+ 1.309 * SEATW4 * TAPER4 + 1.265 * ELODT4 * TAPER4 + 0.4046
				* pow(CSPD4, 2) + 1.065 * pow(AR4, 2) + 0.7346 * pow(SWEEP4, 2)
				+ 0.4896 * pow(DPROP4, 2) + 5.815 * pow(WINGLD4, 2) - 0.1304
				* pow(AF4, 2) + 3.595 * pow(SEATW4, 2) + 0.6296
				* pow(ELODT4, 2) + 1.26 * pow(TAPER4, 2);
		double DOC4 = 83.150002 + 12.02 * CSPD4 - 0.072 * AR4 - 0.0126 * SWEEP4
				+ 3.428 * DPROP4 - 0.704 * WINGLD4 + 0.7248 * AF4 + 0.7224
				* SEATW4 - 0.1421 * ELODT4 + 0.0407 * TAPER4 + 0.613 * CSPD4
				* AR4 + 0.046 * CSPD4 * SWEEP4 + 1.155 * CSPD4 * DPROP4
				+ 0.7144 * CSPD4 * WINGLD4 + 0.0944 * CSPD4 * AF4 - 0.8399
				* CSPD4 * SEATW4 + 0.2251 * CSPD4 * ELODT4 + 0.0082 * CSPD4
				* TAPER4 + 0.0082 * AR4 * SWEEP4 + 0.1826 * AR4 * DPROP4
				- 0.0606 * AR4 * WINGLD4 - 0.2352 * AR4 * AF4 - 0.0486 * AR4
				* SEATW4 + 0.0162 * AR4 * ELODT4 + 0.0211 * AR4 * TAPER4
				+ 0.0199 * SWEEP4 * DPROP4 + 0.0088 * SWEEP4 * WINGLD4 - 0.0152
				* SWEEP4 * AF4 + 0.0082 * SWEEP4 * SEATW4 - 0.0123 * SWEEP4
				* ELODT4 - 0.0142 * SWEEP4 * TAPER4 + 0.1207 * DPROP4 * WINGLD4
				+ 0.6576 * DPROP4 * AF4 - 0.2728 * DPROP4 * SEATW4 + 0.0435
				* DPROP4 * ELODT4 - 0.0326 * DPROP4 * TAPER4 + 0.0196 * WINGLD4
				* AF4 + 0.0952 * WINGLD4 * SEATW4 + 0.0351 * WINGLD4 * ELODT4
				+ 0.0091 * WINGLD4 * TAPER4 - 0.0628 * AF4 * SEATW4 - 0.0201
				* AF4 * ELODT4 - 0.0244 * AF4 * TAPER4 + 0.0651 * SEATW4
				* ELODT4 + 0.0048 * SEATW4 * TAPER4 - 0.0042 * ELODT4 * TAPER4
				- 10.95 * pow(CSPD4, 2) - 0.2401 * pow(AR4, 2) - 0.2203
				* pow(SWEEP4, 2) + 5.223 * pow(DPROP4, 2) - 0.2669
				* pow(WINGLD4, 2) - 0.0884 * pow(AF4, 2) - 0.2169
				* pow(SEATW4, 2) - 0.306 * pow(ELODT4, 2) - 0.2231
				* pow(TAPER4, 2);
		double ROUGH4 = 2.191 - 0.0001 * CSPD4 + 0.1584 * AR4 - 0.0017 * SWEEP4
				+ 0.0238 * DPROP4 - 0.1632 * WINGLD4 - 0.0008 * AF4 - 0.0666
				* SEATW4 - 0.0142 * ELODT4 + 0.0069 * TAPER4 - 0.0002 * CSPD4
				* AR4 + 0.0001 * CSPD4 * SWEEP4 + 0.0001 * CSPD4 * WINGLD4
				- 0.0002 * CSPD4 * AF4 + 0.0008 * CSPD4 * SEATW4 + 0.0005
				* CSPD4 * ELODT4 - 0.0007 * CSPD4 * TAPER4 - 0.0003 * AR4
				* SWEEP4 + 0.0006 * AR4 * DPROP4 - 0.012 * AR4 * WINGLD4
				- 0.0003 * AR4 * AF4 - 0.0051 * AR4 * SEATW4 - 0.0037 * AR4
				* ELODT4 + 0.0007 * AR4 * TAPER4 - 0.0002 * SWEEP4 * DPROP4
				- 0.0003 * SWEEP4 * WINGLD4 - 0.0001 * SWEEP4 * AF4 - 0.0004
				* SWEEP4 * SEATW4 - 0.0002 * SWEEP4 * ELODT4 - 0.0001 * SWEEP4
				* TAPER4 - 0.005 * DPROP4 * WINGLD4 - 0.0037 * DPROP4 * AF4
				+ 0.0022 * DPROP4 * SEATW4 - 0.0008 * DPROP4 * ELODT4 - 0.0003
				* DPROP4 * TAPER4 - 0.0008 * WINGLD4 * AF4 - 0.0018 * WINGLD4
				* SEATW4 - 0.0012 * WINGLD4 * ELODT4 - 0.0021 * WINGLD4
				* TAPER4 + 0.0009 * AF4 * SEATW4 - 0.0001 * AF4 * ELODT4
				- 0.0002 * AF4 * TAPER4 - 0.0011 * SEATW4 * ELODT4 - 0.0012
				* SEATW4 * TAPER4 - 0.0008 * ELODT4 * TAPER4 - 0.0001
				* pow(CSPD4, 2) - 0.0302 * pow(AR4, 2) - 0.0015
				* pow(SWEEP4, 2) + 0.0041 * pow(DPROP4, 2) + 0.005
				* pow(WINGLD4, 2) + 0.0029 * pow(AF4, 2) + 0.0009
				* pow(SEATW4, 2) - 0.0007 * pow(ELODT4, 2) - 0.0058
				* pow(TAPER4, 2);
		double WFUEL4 = 385.5 - 6.707 * CSPD4 - 30.57 * AR4 + 0.5048 * SWEEP4
				- 18.91 * DPROP4 + 33.009998 * WINGLD4 - 7.543 * AF4
				- 55.169998 * SEATW4 - 1.447 * ELODT4 - 15.32 * TAPER4 - 1.938
				* CSPD4 * AR4 + 0.18 * CSPD4 * SWEEP4 - 1.201 * CSPD4 * DPROP4
				+ 1.434 * CSPD4 * WINGLD4 - 0.5591 * CSPD4 * AF4 + 0.272
				* CSPD4 * SEATW4 + 1.172 * CSPD4 * ELODT4 - 0.5566 * CSPD4
				* TAPER4 - 0.0178 * AR4 * SWEEP4 - 0.6466 * AR4 * DPROP4
				+ 3.714 * AR4 * WINGLD4 - 0.1913 * AR4 * AF4 + 0.3522 * AR4
				* SEATW4 - 0.828 * AR4 * ELODT4 - 1.633 * AR4 * TAPER4 - 0.0415
				* SWEEP4 * DPROP4 - 0.3772 * SWEEP4 * WINGLD4 - 0.0287 * SWEEP4
				* AF4 - 0.3237 * SWEEP4 * SEATW4 - 0.2653 * SWEEP4 * ELODT4
				- 0.5519 * SWEEP4 * TAPER4 + 0.5867 * DPROP4 * WINGLD4 - 2.332
				* DPROP4 * AF4 + 0.0168 * DPROP4 * SEATW4 + 0.2074 * DPROP4
				* ELODT4 - 0.1206 * DPROP4 * TAPER4 + 0.3465 * WINGLD4 * AF4
				- 1.509 * WINGLD4 * SEATW4 - 2.134 * WINGLD4 * ELODT4 + 0.7418
				* WINGLD4 * TAPER4 - 0.0803 * AF4 * SEATW4 + 0.1825 * AF4
				* ELODT4 - 0.1052 * AF4 * TAPER4 - 4.86 * SEATW4 * ELODT4
				- 1.355 * SEATW4 * TAPER4 - 1.289 * ELODT4 * TAPER4 + 0.1451
				* pow(CSPD4, 2) - 1.698 * pow(AR4, 2) - 0.6064 * pow(SWEEP4, 2)
				- 6.025 * pow(DPROP4, 2) - 4.978 * pow(WINGLD4, 2) + 0.2611
				* pow(AF4, 2) - 3.229 * pow(SEATW4, 2) - 0.4999
				* pow(ELODT4, 2) - 1.068 * pow(TAPER4, 2);
		double PURCH4 = 43730 + 142.5 * CSPD4 + 756.5 * AR4 + 2.004 * SWEEP4
				+ 504.799988 * DPROP4 - 314.799988 * WINGLD4 + 194.100006 * AF4
				+ 890.5 * SEATW4 + 114.099998 * ELODT4 + 334.5 * TAPER4
				+ 43.029999 * CSPD4 * AR4 - 2.865 * CSPD4 * SWEEP4 + 27.42
				* CSPD4 * DPROP4 - 35.060001 * CSPD4 * WINGLD4 + 13.28 * CSPD4
				* AF4 - 17.02 * CSPD4 * SEATW4 - 18.83 * CSPD4 * ELODT4 + 14.26
				* CSPD4 * TAPER4 + 0.8277 * AR4 * SWEEP4 + 3.724 * AR4 * DPROP4
				- 60.380001 * AR4 * WINGLD4 + 3.039 * AR4 * AF4 + 7.395 * AR4
				* SEATW4 + 18.809999 * AR4 * ELODT4 + 35.360001 * AR4 * TAPER4
				- 0.0758 * SWEEP4 * DPROP4 + 6.451 * SWEEP4 * WINGLD4 + 0.5043
				* SWEEP4 * AF4 + 4.745 * SWEEP4 * SEATW4 + 3.97 * SWEEP4
				* ELODT4 + 11.97 * SWEEP4 * TAPER4 - 20.6 * DPROP4 * WINGLD4
				+ 20.549999 * DPROP4 * AF4 + 18.09 * DPROP4 * SEATW4 - 7.554
				* DPROP4 * ELODT4 + 5.429 * DPROP4 * TAPER4 - 8.093 * WINGLD4
				* AF4 + 2.433 * WINGLD4 * SEATW4 + 29.82 * WINGLD4 * ELODT4
				- 17.860001 * WINGLD4 * TAPER4 + 3.676 * AF4 * SEATW4 - 3.442
				* AF4 * ELODT4 + 2.995 * AF4 * TAPER4 + 86.110001 * SEATW4
				* ELODT4 + 29.129999 * SEATW4 * TAPER4 + 26.49 * ELODT4
				* TAPER4 - 1.456 * pow(CSPD4, 2) + 16.049999 * pow(AR4, 2)
				- 0.7055 * pow(SWEEP4, 2) + 45.990002 * pow(DPROP4, 2) + 87.14
				* pow(WINGLD4, 2) + 0.8945 * pow(AF4, 2) + 51.650002
				* pow(SEATW4, 2) - 2.256 * pow(ELODT4, 2) + 25.59
				* pow(TAPER4, 2);
		double RANGE4 = 1941 - 6.768 * CSPD4 - 68.910004 * AR4 + 2.315 * SWEEP4
				- 346.799988 * DPROP4 - 365 * WINGLD4 - 65.25 * AF4 - 77.599998
				* SEATW4 + 13.19 * ELODT4 - 155.300003 * TAPER4 + 0.1113
				* CSPD4 * AR4 + 0.2207 * CSPD4 * SWEEP4 + 1.229 * CSPD4
				* DPROP4 + 3.721 * CSPD4 * WINGLD4 + 0.0176 * CSPD4 * AF4
				- 0.0527 * CSPD4 * SEATW4 + 0.6777 * CSPD4 * ELODT4 + 2.674
				* CSPD4 * TAPER4 + 0.0762 * AR4 * SWEEP4 - 24.07 * AR4 * DPROP4
				- 31.309999 * AR4 * WINGLD4 - 1.979 * AR4 * AF4 + 11.8 * AR4
				* SEATW4 - 0.623 * AR4 * ELODT4 - 38.310001 * AR4 * TAPER4
				- 1.15 * SWEEP4 * DPROP4 - 0.9785 * SWEEP4 * WINGLD4 - 0.2051
				* SWEEP4 * AF4 - 0.5879 * SWEEP4 * SEATW4 - 0.4512 * SWEEP4
				* ELODT4 - 0.7754 * SWEEP4 * TAPER4 + 115.400002 * DPROP4
				* WINGLD4 - 26.84 * DPROP4 * AF4 + 27.91 * DPROP4 * SEATW4
				- 7.897 * DPROP4 * ELODT4 + 34.349998 * DPROP4 * TAPER4
				+ 20.610001 * WINGLD4 * AF4 + 28.92 * WINGLD4 * SEATW4 - 3.006
				* WINGLD4 * ELODT4 - 27.870001 * WINGLD4 * TAPER4 + 4.107 * AF4
				* SEATW4 - 1.17 * AF4 * ELODT4 + 7.186 * AF4 * TAPER4 - 7.389
				* SEATW4 * ELODT4 + 18.02 * SEATW4 * TAPER4 - 2.451 * ELODT4
				* TAPER4 - 12.83 * pow(CSPD4, 2) + 4.667 * pow(AR4, 2) - 12.33
				* pow(SWEEP4, 2) - 85.330002 * pow(DPROP4, 2) + 57.169998
				* pow(WINGLD4, 2) - 8.833 * pow(AF4, 2) - 16.33
				* pow(SEATW4, 2) - 13.33 * pow(ELODT4, 2) + 8.167
				* pow(TAPER4, 2);
		double LDMAX4 = 17.43 + 0.4811 * CSPD4 + 1.584 * AR4 + 0.0212 * SWEEP4
				- 0.0128 * DPROP4 - 0.5456 * WINGLD4 - 0.0054 * AF4 - 0.4984
				* SEATW4 + 0.1607 * ELODT4 + 0.0288 * TAPER4 + 0.0635 * CSPD4
				* AR4 + 0.0013 * CSPD4 * SWEEP4 - 0.0078 * CSPD4 * DPROP4
				- 0.0123 * CSPD4 * WINGLD4 - 0.0011 * CSPD4 * AF4 - 0.0065
				* CSPD4 * SEATW4 + 0.0049 * CSPD4 * ELODT4 + 0.0038 * CSPD4
				* TAPER4 + 0.0038 * AR4 * SWEEP4 - 0.0014 * AR4 * DPROP4
				- 0.0601 * AR4 * WINGLD4 - 0.0003 * AR4 * AF4 - 0.0503 * AR4
				* SEATW4 + 0.017 * AR4 * ELODT4 - 0.0029 * AR4 * TAPER4
				+ 0.0002 * SWEEP4 * DPROP4 - 0.0032 * SWEEP4 * WINGLD4 + 0.0001
				* SWEEP4 * AF4 - 0.0043 * SWEEP4 * SEATW4 - 0.0021 * SWEEP4
				* ELODT4 - 0.0001 * SWEEP4 * TAPER4 - 0.0007 * DPROP4 * WINGLD4
				- 0.0015 * DPROP4 * AF4 + 0.0009 * DPROP4 * SEATW4 - 0.0002
				* DPROP4 * ELODT4 + 0.0008 * DPROP4 * TAPER4 - 0.0008 * WINGLD4
				* AF4 - 0.0682 * WINGLD4 * SEATW4 - 0.0243 * WINGLD4 * ELODT4
				- 0.0047 * WINGLD4 * TAPER4 + 0 * AF4 * SEATW4 - 0.0004 * AF4
				* ELODT4 + 0.0007 * AF4 * TAPER4 - 0.0406 * SEATW4 * ELODT4
				- 0.0052 * SEATW4 * TAPER4 - 0.0018 * ELODT4 * TAPER4 - 0.0923
				* pow(CSPD4, 2) - 0.1312 * pow(AR4, 2) - 0.0127
				* pow(SWEEP4, 2) - 0.0041 * pow(DPROP4, 2) + 0.0304
				* pow(WINGLD4, 2) - 0.0044 * pow(AF4, 2) - 0.0545
				* pow(SEATW4, 2) - 0.0239 * pow(ELODT4, 2) + 0.0066
				* pow(TAPER4, 2);
		double VCMAX4 = 197.800003 - 0.3562 * CSPD4 + 0.7729 * AR4 + 0.1807
				* SWEEP4 + 1.886 * DPROP4 + 5.332 * WINGLD4 - 0.1262 * AF4
				- 3.585 * SEATW4 + 1.18 * ELODT4 + 0.2323 * TAPER4 + 0.0123
				* CSPD4 * AR4 + 0.0073 * CSPD4 * SWEEP4 - 0.0462 * CSPD4
				* DPROP4 + 0.0075 * CSPD4 * WINGLD4 - 0.003 * CSPD4 * AF4
				+ 0.0375 * CSPD4 * SEATW4 + 0.0225 * CSPD4 * ELODT4 + 0.017
				* CSPD4 * TAPER4 + 0.0071 * AR4 * SWEEP4 - 0.0914 * AR4
				* DPROP4 + 0.1716 * AR4 * WINGLD4 - 0.0115 * AR4 * AF4 + 0.0278
				* AR4 * SEATW4 - 0.0074 * AR4 * ELODT4 - 0.0583 * AR4 * TAPER4
				- 0.0124 * SWEEP4 * DPROP4 - 0.0116 * SWEEP4 * WINGLD4 - 0.0041
				* SWEEP4 * AF4 - 0.0337 * SWEEP4 * SEATW4 - 0.0132 * SWEEP4
				* ELODT4 + 0.0028 * SWEEP4 * TAPER4 - 0.2578 * DPROP4 * WINGLD4
				- 0.3341 * DPROP4 * AF4 + 0.2057 * DPROP4 * SEATW4 - 0.0696
				* DPROP4 * ELODT4 - 0.0125 * DPROP4 * TAPER4 - 0.1043 * WINGLD4
				* AF4 - 0.6842 * WINGLD4 * SEATW4 - 0.1276 * WINGLD4 * ELODT4
				- 0.0217 * WINGLD4 * TAPER4 + 0.0663 * AF4 * SEATW4 - 0.0185
				* AF4 * ELODT4 + 0.0001 * AF4 * TAPER4 - 0.2758 * SEATW4
				* ELODT4 - 0.039 * SEATW4 * TAPER4 - 0.0162 * ELODT4 * TAPER4
				- 0.1479 * pow(CSPD4, 2) - 0.1989 * pow(AR4, 2) - 0.2574
				* pow(SWEEP4, 2) + 0.3991 * pow(DPROP4, 2) - 0.5109
				* pow(WINGLD4, 2) + 0.0706 * pow(AF4, 2) - 0.4174
				* pow(SEATW4, 2) - 0.2574 * pow(ELODT4, 2) - 0.0384
				* pow(TAPER4, 2);

		double NOISE6 = 74.099998 - 0.0004 * CSPD6 - 0.0156 * AR6 + 0.0003
				* SWEEP6 + 0.9682 * DPROP6 + 0.0314 * WINGLD6 - 0.0053 * AF6
				- 0.0015 * SEATW6 - 0.0004 * ELODT6 + 0.0007 * TAPER6 + 0
				* CSPD6 * AR6 + 0 * CSPD6 * SWEEP6 - 0.0001 * CSPD6 * DPROP6
				- 0.0001 * CSPD6 * WINGLD6 - 0.0001 * CSPD6 * AF6 + 0 * AR6
				* SWEEP6 - 0.0013 * AR6 * DPROP6 - 0.0014 * AR6 * WINGLD6
				- 0.0002 * AR6 * AF6 - 0.0001 * AR6 * SEATW6 + 0.0002 * AR6
				* ELODT6 + 0.0002 * AR6 * TAPER6 + 0 * SWEEP6 * TAPER6 - 0.0003
				* DPROP6 * WINGLD6 + 0.0057 * DPROP6 * AF6 - 0.0002 * DPROP6
				* SEATW6 + 0.0001 * DPROP6 * ELODT6 - 0.0001 * DPROP6 * TAPER6
				+ 0.0002 * WINGLD6 * AF6 - 0.0002 * WINGLD6 * SEATW6 - 0.0002
				* WINGLD6 * ELODT6 + 0.0001 * WINGLD6 * TAPER6 - 0.0001 * AF6
				* SEATW6 + 0 * AF6 * ELODT6 + 0 * AF6 * TAPER6 - 0.0001
				* SEATW6 * ELODT6 + 0.0006 * pow(CSPD6, 2) + 0.0017
				* pow(AR6, 2) + 0.0011 * pow(SWEEP6, 2) + 0.1103
				* pow(DPROP6, 2) + 0.0004 * pow(WINGLD6, 2) - 0.0021
				* pow(AF6, 2) + 0.001 * pow(SEATW6, 2) + 0.001 * pow(ELODT6, 2)
				+ 0.0008 * pow(TAPER6, 2);
		double WEMP6 = 1972 + 5.386 * CSPD6 + 33.290001 * AR6 - 0.0222 * SWEEP6
				+ 10.82 * DPROP6 - 28.889999 * WINGLD6 + 5.588 * AF6 + 61.32
				* SEATW6 + 4.65 * ELODT6 + 16.620001 * TAPER6 + 1.32 * CSPD6
				* AR6 - 0.2549 * CSPD6 * SWEEP6 + 0.9089 * CSPD6 * DPROP6
				- 1.403 * CSPD6 * WINGLD6 + 0.3601 * CSPD6 * AF6 - 0.0118
				* CSPD6 * SEATW6 + 0.0123 * CSPD6 * ELODT6 + 0.4761 * CSPD6
				* TAPER6 + 0.0455 * AR6 * SWEEP6 - 0.0596 * AR6 * DPROP6
				- 3.818 * AR6 * WINGLD6 - 0.0408 * AR6 * AF6 + 0.5044 * AR6
				* SEATW6 + 0.2867 * AR6 * ELODT6 + 2.497 * AR6 * TAPER6
				+ 0.0011 * SWEEP6 * DPROP6 + 0.151 * SWEEP6 * WINGLD6 + 0.0524
				* SWEEP6 * AF6 + 0.1164 * SWEEP6 * SEATW6 + 0.0491 * SWEEP6
				* ELODT6 + 0.391 * SWEEP6 * TAPER6 + 0.1823 * DPROP6 * WINGLD6
				+ 1.254 * DPROP6 * AF6 + 0.0491 * DPROP6 * SEATW6 + 0.1011
				* DPROP6 * ELODT6 + 0.0538 * DPROP6 * TAPER6 + 0.098 * WINGLD6
				* AF6 + 1.807 * WINGLD6 * SEATW6 + 1.119 * WINGLD6 * ELODT6
				- 1.437 * WINGLD6 * TAPER6 + 0.0664 * AF6 * SEATW6 + 0.0566
				* AF6 * ELODT6 + 0.0508 * AF6 * TAPER6 + 2.914 * SEATW6
				* ELODT6 + 0.0969 * SEATW6 * TAPER6 - 0.1483 * ELODT6 * TAPER6
				+ 1.446 * pow(CSPD6, 2) - 2.524 * pow(AR6, 2) + 1.646
				* pow(SWEEP6, 2) + 1.381 * pow(DPROP6, 2) + 2.651
				* pow(WINGLD6, 2) + 0.7561 * pow(AF6, 2) + 3.116
				* pow(SEATW6, 2) + 1.211 * pow(ELODT6, 2) - 0.4489
				* pow(TAPER6, 2);
		double DOC6 = 83.260002 + 11.86 * CSPD6 - 0.0805 * AR6 - 0.0218
				* SWEEP6 + 3.345 * DPROP6 - 0.6443 * WINGLD6 + 0.7039 * AF6
				+ 0.8256 * SEATW6 - 0.0905 * ELODT6 + 0.0305 * TAPER6 + 0.6
				* CSPD6 * AR6 + 0.0284 * CSPD6 * SWEEP6 + 1.109 * CSPD6
				* DPROP6 + 0.6928 * CSPD6 * WINGLD6 + 0.0795 * CSPD6 * AF6
				- 0.9926 * CSPD6 * SEATW6 + 0.1119 * CSPD6 * ELODT6 - 0.0183
				* CSPD6 * TAPER6 + 0.0018 * AR6 * SWEEP6 + 0.1731 * AR6
				* DPROP6 - 0.0631 * AR6 * WINGLD6 - 0.246 * AR6 * AF6 - 0.0581
				* AR6 * SEATW6 - 0.0008 * AR6 * ELODT6 - 0.0048 * AR6 * TAPER6
				+ 0.0024 * SWEEP6 * DPROP6 + 0.0011 * SWEEP6 * WINGLD6 - 0.0043
				* SWEEP6 * AF6 + 0.0048 * SWEEP6 * SEATW6 - 0.0019 * SWEEP6
				* ELODT6 + 0.0025 * SWEEP6 * TAPER6 + 0.1331 * DPROP6 * WINGLD6
				+ 0.6487 * DPROP6 * AF6 - 0.353 * DPROP6 * SEATW6 - 0.0025
				* DPROP6 * ELODT6 - 0.0586 * DPROP6 * TAPER6 + 0.005 * WINGLD6
				* AF6 + 0.1121 * WINGLD6 * SEATW6 + 0.0345 * WINGLD6 * ELODT6
				+ 0.0103 * WINGLD6 * TAPER6 - 0.0674 * AF6 * SEATW6 + 0.0003
				* AF6 * ELODT6 - 0.0133 * AF6 * TAPER6 + 0.0316 * SEATW6
				* ELODT6 - 0.0007 * SEATW6 * TAPER6 + 0.0063 * ELODT6 * TAPER6
				- 10.77 * pow(CSPD6, 2) - 0.288 * pow(AR6, 2) - 0.288
				* pow(SWEEP6, 2) + 5.182 * pow(DPROP6, 2) - 0.2313
				* pow(WINGLD6, 2) - 0.0895 * pow(AF6, 2) - 0.2136
				* pow(SEATW6, 2) - 0.2562 * pow(ELODT6, 2) - 0.2686
				* pow(TAPER6, 2);
		double ROUGH6 = 2.161 + 0.0007 * CSPD6 + 0.156 * AR6 - 0.0022 * SWEEP6
				+ 0.0239 * DPROP6 - 0.1649 * WINGLD6 - 0.0007 * AF6 - 0.0675
				* SEATW6 - 0.0135 * ELODT6 + 0.0056 * TAPER6 + 0.0006 * CSPD6
				* AR6 + 0.0001 * CSPD6 * SWEEP6 + 0.0001 * CSPD6 * DPROP6 + 0
				* CSPD6 * SEATW6 - 0.0003 * CSPD6 * ELODT6 - 0.0003 * CSPD6
				* TAPER6 - 0.0003 * AR6 * SWEEP6 + 0.0008 * AR6 * DPROP6
				- 0.0119 * AR6 * WINGLD6 + 0 * AR6 * AF6 - 0.0054 * AR6
				* SEATW6 - 0.0026 * AR6 * ELODT6 - 0.0003 * AR6 * TAPER6
				- 0.0002 * SWEEP6 * DPROP6 - 0.0002 * SWEEP6 * WINGLD6 - 0.0001
				* SWEEP6 * AF6 - 0.0002 * SWEEP6 * SEATW6 - 0.0002 * SWEEP6
				* ELODT6 + 0.0001 * SWEEP6 * TAPER6 - 0.0051 * DPROP6 * WINGLD6
				- 0.0038 * DPROP6 * AF6 + 0.0024 * DPROP6 * SEATW6 - 0.0009
				* DPROP6 * ELODT6 - 0.0002 * DPROP6 * TAPER6 - 0.001 * WINGLD6
				* AF6 - 0.0018 * WINGLD6 * SEATW6 - 0.0009 * WINGLD6 * ELODT6
				- 0.0017 * WINGLD6 * TAPER6 + 0.0009 * AF6 * SEATW6 - 0.0002
				* AF6 * ELODT6 - 0.0001 * AF6 * TAPER6 - 0.0005 * SEATW6
				* ELODT6 - 0.0001 * SEATW6 * TAPER6 + 0.0003 * ELODT6 * TAPER6
				- 0.0006 * pow(CSPD6, 2) - 0.0255 * pow(AR6, 2) - 0.0043
				* pow(SWEEP6, 2) + 0.0039 * pow(DPROP6, 2) + 0.0092
				* pow(WINGLD6, 2) + 0.0014 * pow(AF6, 2) + 0.0003
				* pow(SEATW6, 2) - 0.0007 * pow(ELODT6, 2) - 0.0052
				* pow(TAPER6, 2);
		double WFUEL6 = 359.700012 - 5.78 * CSPD6 - 29.99 * AR6 + 0.0682
				* SWEEP6 - 18.709999 * DPROP6 + 31 * WINGLD6 - 7.332 * AF6
				- 63.599998 * SEATW6 - 4.44 * ELODT6 - 16.709999 * TAPER6
				- 1.317 * CSPD6 * AR6 + 0.2715 * CSPD6 * SWEEP6 - 1.069 * CSPD6
				* DPROP6 + 1.185 * CSPD6 * WINGLD6 - 0.3931 * CSPD6 * AF6
				- 0.8091 * CSPD6 * SEATW6 + 0.0543 * CSPD6 * ELODT6 - 0.448
				* CSPD6 * TAPER6 - 0.0418 * AR6 * SWEEP6 - 0.5179 * AR6
				* DPROP6 + 4.113 * AR6 * WINGLD6 - 0.0407 * AR6 * AF6 - 0.3222
				* AR6 * SEATW6 - 0.3059 * AR6 * ELODT6 - 2.526 * AR6 * TAPER6
				- 0.0077 * SWEEP6 * DPROP6 - 0.1761 * SWEEP6 * WINGLD6 - 0.0543
				* SWEEP6 * AF6 - 0.1319 * SWEEP6 * SEATW6 - 0.0633 * SWEEP6
				* ELODT6 - 0.3999 * SWEEP6 * TAPER6 + 0.2791 * DPROP6 * WINGLD6
				- 2.456 * DPROP6 * AF6 - 0.1367 * DPROP6 * SEATW6 - 0.155
				* DPROP6 * ELODT6 - 0.0405 * DPROP6 * TAPER6 + 0.0736 * WINGLD6
				* AF6 - 1.298 * WINGLD6 * SEATW6 - 1.258 * WINGLD6 * ELODT6
				+ 1.389 * WINGLD6 * TAPER6 - 0.1952 * AF6 * SEATW6 - 0.0011
				* AF6 * ELODT6 - 0.0391 * AF6 * TAPER6 - 2.965 * SEATW6
				* ELODT6 - 0.1262 * SEATW6 * TAPER6 + 0.1433 * ELODT6 * TAPER6
				- 0.8956 * pow(CSPD6, 2) + 1.729 * pow(AR6, 2) - 1.613
				* pow(SWEEP6, 2) - 6.309 * pow(DPROP6, 2) - 1.949
				* pow(WINGLD6, 2) - 0.6581 * pow(AF6, 2) - 2.723
				* pow(SEATW6, 2) - 1.181 * pow(ELODT6, 2) + 0.5244
				* pow(TAPER6, 2);
		double PURCH6 = 44220 + 124.400002 * CSPD6 + 749.200012 * AR6 + 9.32
				* SWEEP6 + 506.899994 * DPROP6 - 290.600006 * WINGLD6
				+ 192.800003 * AF6 + 1034 * SEATW6 + 144.5 * ELODT6
				+ 364.799988 * TAPER6 + 27.370001 * CSPD6 * AR6 - 5.918 * CSPD6
				* SWEEP6 + 24.51 * CSPD6 * DPROP6 - 29.690001 * CSPD6 * WINGLD6
				+ 10.97 * CSPD6 * AF6 + 2.652 * CSPD6 * SEATW6 + 2.262 * CSPD6
				* ELODT6 + 11.44 * CSPD6 * TAPER6 + 2.046 * AR6 * SWEEP6
				+ 0.2715 * AR6 * DPROP6 - 67.760002 * AR6 * WINGLD6 - 0.3336
				* AR6 * AF6 + 25.07 * AR6 * SEATW6 + 7.377 * AR6 * ELODT6
				+ 56.369999 * AR6 * TAPER6 - 0.1879 * SWEEP6 * DPROP6 + 1.594
				* SWEEP6 * WINGLD6 + 1.086 * SWEEP6 * AF6 + 1.375 * SWEEP6
				* SEATW6 + 0.4313 * SWEEP6 * ELODT6 + 8.3 * SWEEP6 * TAPER6
				- 11.7 * DPROP6 * WINGLD6 + 24.440001 * DPROP6 * AF6 + 23.85
				* DPROP6 * SEATW6 + 1.195 * DPROP6 * ELODT6 + 3.797 * DPROP6
				* TAPER6 - 3.027 * WINGLD6 * AF6 + 4.452 * WINGLD6 * SEATW6
				+ 18.059999 * WINGLD6 * ELODT6 - 30.709999 * WINGLD6 * TAPER6
				+ 7.103 * AF6 * SEATW6 + 1.01 * AF6 * ELODT6 + 1.463 * AF6
				* TAPER6 + 53.560001 * SEATW6 * ELODT6 + 4.063 * SEATW6
				* TAPER6 - 3.483 * ELODT6 * TAPER6 + 15.24 * pow(CSPD6, 2)
				- 71.910004 * pow(AR6, 2) + 33.990002 * pow(SWEEP6, 2)
				+ 60.540001 * pow(DPROP6, 2) + 15.24 * pow(WINGLD6, 2) + 28.49
				* pow(AF6, 2) + 50.490002 * pow(SEATW6, 2) + 18.190001
				* pow(ELODT6, 2) - 9.011 * pow(TAPER6, 2);
		double RANGE6 = 1932 - 6.389 * CSPD6 - 64.400002 * AR6 + 1.778 * SWEEP6
				- 342.799988 * DPROP6 - 356.399994 * WINGLD6 - 64.639999 * AF6
				- 91.739998 * SEATW6 + 5.658 * ELODT6 - 150.5 * TAPER6 - 0.2363
				* CSPD6 * AR6 + 0.0566 * CSPD6 * SWEEP6 + 1.346 * CSPD6
				* DPROP6 + 3.529 * CSPD6 * WINGLD6 + 0.0215 * CSPD6 * AF6
				- 0.291 * CSPD6 * SEATW6 + 0.0293 * CSPD6 * ELODT6 + 2.416
				* CSPD6 * TAPER6 + 0.0449 * AR6 * SWEEP6 - 24.780001 * AR6
				* DPROP6 - 35.150002 * AR6 * WINGLD6 - 2.131 * AR6 * AF6
				+ 13.65 * AR6 * SEATW6 - 0.0059 * AR6 * ELODT6 - 35.599998
				* AR6 * TAPER6 - 0.9512 * SWEEP6 * DPROP6 - 0.6113 * SWEEP6
				* WINGLD6 - 0.166 * SWEEP6 * AF6 - 0.3457 * SWEEP6 * SEATW6
				- 0.1191 * SWEEP6 * ELODT6 - 0.5762 * SWEEP6 * TAPER6
				+ 114.300003 * DPROP6 * WINGLD6 - 26.83 * DPROP6 * AF6
				+ 33.290001 * DPROP6 * SEATW6 - 4.76 * DPROP6 * ELODT6
				+ 33.189999 * DPROP6 * TAPER6 + 20.6 * WINGLD6 * AF6
				+ 38.630001 * WINGLD6 * SEATW6 + 0.4238 * WINGLD6 * ELODT6
				- 31.940001 * WINGLD6 * TAPER6 + 5.057 * AF6 * SEATW6 - 0.8809
				* AF6 * ELODT6 + 6.873 * AF6 * TAPER6 - 3.928 * SEATW6 * ELODT6
				+ 20.360001 * SEATW6 * TAPER6 + 0.0918 * ELODT6 * TAPER6
				- 11.73 * pow(CSPD6, 2) + 4.268 * pow(AR6, 2) - 10.73
				* pow(SWEEP6, 2) - 83.730003 * pow(DPROP6, 2) + 39.27
				* pow(WINGLD6, 2) - 7.232 * pow(AF6, 2) - 14.23
				* pow(SEATW6, 2) - 11.73 * pow(ELODT6, 2) + 9.268
				* pow(TAPER6, 2);
		double LDMAX6 = 17.34 + 0.4817 * CSPD6 + 1.573 * AR6 + 0.0179 * SWEEP6
				- 0.0117 * DPROP6 - 0.5804 * WINGLD6 - 0.0053 * AF6 - 0.5638
				* SEATW6 + 0.0986 * ELODT6 + 0.0278 * TAPER6 + 0.0603 * CSPD6
				* AR6 + 0.0004 * CSPD6 * SWEEP6 - 0.0074 * CSPD6 * DPROP6
				- 0.0132 * CSPD6 * WINGLD6 - 0.0014 * CSPD6 * AF6 - 0.0059
				* CSPD6 * SEATW6 + 0.0053 * CSPD6 * ELODT6 + 0.0024 * CSPD6
				* TAPER6 + 0.0032 * AR6 * SWEEP6 - 0.0021 * AR6 * DPROP6
				- 0.0632 * AR6 * WINGLD6 - 0.0009 * AR6 * AF6 - 0.0553 * AR6
				* SEATW6 + 0.0072 * AR6 * ELODT6 + 0.0011 * AR6 * TAPER6
				+ 0.0002 * SWEEP6 * DPROP6 - 0.0028 * SWEEP6 * WINGLD6 + 0
				* SWEEP6 * AF6 - 0.0035 * SWEEP6 * SEATW6 - 0.0016 * SWEEP6
				* ELODT6 - 0.0006 * SWEEP6 * TAPER6 + 0.0003 * DPROP6 * WINGLD6
				- 0.0006 * DPROP6 * AF6 + 0.0014 * DPROP6 * SEATW6 + 0.001
				* DPROP6 * ELODT6 + 0.0004 * DPROP6 * TAPER6 - 0.0001 * WINGLD6
				* AF6 - 0.0563 * WINGLD6 * SEATW6 - 0.0173 * WINGLD6 * ELODT6
				- 0.0049 * WINGLD6 * TAPER6 + 0.0006 * AF6 * SEATW6 + 0.0004
				* AF6 * ELODT6 + 0.0001 * AF6 * TAPER6 - 0.0254 * SEATW6
				* ELODT6 - 0.0069 * SEATW6 * TAPER6 - 0.0037 * ELODT6 * TAPER6
				- 0.0838 * pow(CSPD6, 2) - 0.1499 * pow(AR6, 2) - 0.0058
				* pow(SWEEP6, 2) + 0.0016 * pow(DPROP6, 2) + 0.0214
				* pow(WINGLD6, 2) + 0.0013 * pow(AF6, 2) - 0.0337
				* pow(SEATW6, 2) - 0.0133 * pow(ELODT6, 2) + 0.0028
				* pow(TAPER6, 2);
		double VCMAX6 = 197.100006 - 0.3331 * CSPD6 + 0.7564 * AR6 + 0.153
				* SWEEP6 + 1.918 * DPROP6 + 5.044 * WINGLD6 - 0.1139 * AF6
				- 4.07 * SEATW6 + 0.7276 * ELODT6 + 0.2242 * TAPER6 - 0.0231
				* CSPD6 * AR6 - 0.009 * CSPD6 * SWEEP6 - 0.0365 * CSPD6
				* DPROP6 - 0.0011 * CSPD6 * WINGLD6 - 0.0064 * CSPD6 * AF6
				+ 0.0355 * CSPD6 * SEATW6 + 0.028 * CSPD6 * ELODT6 + 0.013
				* CSPD6 * TAPER6 + 0.013 * AR6 * SWEEP6 - 0.0947 * AR6 * DPROP6
				+ 0.1791 * AR6 * WINGLD6 - 0.0178 * AR6 * AF6 + 0.0598 * AR6
				* SEATW6 - 0.0216 * AR6 * ELODT6 - 0.0274 * AR6 * TAPER6
				- 0.006 * SWEEP6 * DPROP6 - 0.0243 * SWEEP6 * WINGLD6 - 0.001
				* SWEEP6 * AF6 - 0.0233 * SWEEP6 * SEATW6 - 0.0108 * SWEEP6
				* ELODT6 - 0.0011 * SWEEP6 * TAPER6 - 0.2368 * DPROP6 * WINGLD6
				- 0.334 * DPROP6 * AF6 + 0.2329 * DPROP6 * SEATW6 - 0.0419
				* DPROP6 * ELODT6 - 0.0148 * DPROP6 * TAPER6 - 0.0851 * WINGLD6
				* AF6 - 0.6179 * WINGLD6 * SEATW6 - 0.0865 * WINGLD6 * ELODT6
				- 0.0158 * WINGLD6 * TAPER6 + 0.0691 * AF6 * SEATW6 - 0.0088
				* AF6 * ELODT6 - 0.0056 * AF6 * TAPER6 - 0.1699 * SEATW6
				* ELODT6 - 0.0462 * SEATW6 * TAPER6 - 0.028 * ELODT6 * TAPER6
				- 0.2315 * pow(CSPD6, 2) - 0.384 * pow(AR6, 2) - 0.013
				* pow(SWEEP6, 2) + 0.315 * pow(DPROP6, 2) - 0.612
				* pow(WINGLD6, 2) + 0.206 * pow(AF6, 2) - 0.2825
				* pow(SEATW6, 2) - 0.1225 * pow(ELODT6, 2) - 0.013
				* pow(TAPER6, 2);

		// Aggregate Objective Function
		double WEMP2_DEV = (WEMP2 - WEMP2_GOAL) / WEMP2_GOAL;
		double DOC2_DEV = (DOC2 - DOC2_GOAL) / DOC2_GOAL;
		double WFUEL2_DEV = (WFUEL2 - WFUEL2_GOAL) / WFUEL2_GOAL;
		double PURCH2_DEV = (PURCH2 - PURCH2_GOAL) / PURCH2_GOAL;
		double RANGE2_DEV = -(RANGE2 - RANGE2_GOAL) / RANGE2_GOAL;
		double LDMAX2_DEV = -(LDMAX2 - LDMAX2_GOAL) / LDMAX2_GOAL;
		double VCMAX2_DEV = -(VCMAX2 - VCMAX2_GOAL) / VCMAX2_GOAL;

		double WEMP4_DEV = (WEMP4 - WEMP4_GOAL) / WEMP4_GOAL;
		double DOC4_DEV = (DOC4 - DOC4_GOAL) / DOC4_GOAL;
		double WFUEL4_DEV = (WFUEL4 - WFUEL4_GOAL) / WFUEL4_GOAL;
		double PURCH4_DEV = (PURCH4 - PURCH4_GOAL) / PURCH4_GOAL;
		double RANGE4_DEV = -(RANGE4 - RANGE4_GOAL) / RANGE4_GOAL;
		double LDMAX4_DEV = -(LDMAX4 - LDMAX4_GOAL) / LDMAX4_GOAL;
		double VCMAX4_DEV = -(VCMAX4 - VCMAX4_GOAL) / VCMAX4_GOAL;

		double WEMP6_DEV = (WEMP6 - WEMP6_GOAL) / WEMP6_GOAL;
		double DOC6_DEV = (DOC6 - DOC6_GOAL) / DOC6_GOAL;
		double WFUEL6_DEV = (WFUEL6 - WFUEL6_GOAL) / WFUEL6_GOAL;
		double PURCH6_DEV = (PURCH6 - PURCH6_GOAL) / PURCH6_GOAL;
		double RANGE6_DEV = -(RANGE6 - RANGE6_GOAL) / RANGE6_GOAL;
		double LDMAX6_DEV = -(LDMAX6 - LDMAX6_GOAL) / LDMAX6_GOAL;
		double VCMAX6_DEV = -(VCMAX6 - VCMAX6_GOAL) / VCMAX6_GOAL;

		if (WEMP2_DEV < 0)
			WEMP2_DEV = 0;
		if (DOC2_DEV < 0)
			DOC2_DEV = 0;
		if (WFUEL2_DEV < 0)
			WFUEL2_DEV = 0;
		if (PURCH2_DEV < 0)
			PURCH2_DEV = 0;
		if (RANGE2_DEV < 0)
			RANGE2_DEV = 0;
		if (LDMAX2_DEV < 0)
			LDMAX2_DEV = 0;
		if (VCMAX2_DEV < 0)
			VCMAX2_DEV = 0;

		if (WEMP4_DEV < 0)
			WEMP4_DEV = 0;
		if (DOC4_DEV < 0)
			DOC4_DEV = 0;
		if (WFUEL4_DEV < 0)
			WFUEL4_DEV = 0;
		if (PURCH4_DEV < 0)
			PURCH4_DEV = 0;
		if (RANGE4_DEV < 0)
			RANGE4_DEV = 0;
		if (LDMAX4_DEV < 0)
			LDMAX4_DEV = 0;
		if (VCMAX4_DEV < 0)
			VCMAX4_DEV = 0;

		if (WEMP6_DEV < 0)
			WEMP6_DEV = 0;
		if (DOC6_DEV < 0)
			DOC6_DEV = 0;
		if (WFUEL6_DEV < 0)
			WFUEL6_DEV = 0;
		if (PURCH6_DEV < 0)
			PURCH6_DEV = 0;
		if (RANGE6_DEV < 0)
			RANGE6_DEV = 0;
		if (LDMAX6_DEV < 0)
			LDMAX6_DEV = 0;
		if (VCMAX6_DEV < 0)
			VCMAX6_DEV = 0;

		if (NOISE2 > max_NOISE)
			max_NOISE = NOISE2;
		if (NOISE4 > max_NOISE)
			max_NOISE = NOISE4;
		if (NOISE6 > max_NOISE)
			max_NOISE = NOISE6;

		if (WEMP2 > max_WEMP)
			max_WEMP = WEMP2;
		if (WEMP4 > max_WEMP)
			max_WEMP = WEMP4;
		if (WEMP6 > max_WEMP)
			max_WEMP = WEMP6;

		if (DOC2 > max_DOC)
			max_DOC = DOC2;
		if (DOC4 > max_DOC)
			max_DOC = DOC4;
		if (DOC6 > max_DOC)
			max_DOC = DOC6;

		if (ROUGH2 > max_ROUGH)
			max_ROUGH = ROUGH2;
		if (ROUGH4 > max_ROUGH)
			max_ROUGH = ROUGH4;
		if (ROUGH6 > max_ROUGH)
			max_ROUGH = ROUGH6;

		if (WFUEL2 > max_WFUEL)
			max_WFUEL = WFUEL2;
		if (WFUEL4 > max_WFUEL)
			max_WFUEL = WFUEL4;
		if (WFUEL6 > max_WFUEL)
			max_WFUEL = WFUEL6;

		if (PURCH2 > max_PURCH)
			max_PURCH = PURCH2;
		if (PURCH4 > max_PURCH)
			max_PURCH = PURCH4;
		if (PURCH6 > max_PURCH)
			max_PURCH = PURCH6;

		if (RANGE2 < min_RANGE)
			min_RANGE = RANGE2;
		if (RANGE4 < min_RANGE)
			min_RANGE = RANGE4;
		if (RANGE6 < min_RANGE)
			min_RANGE = RANGE6;

		if (LDMAX2 < min_LDMAX)
			min_LDMAX = LDMAX2;
		if (LDMAX4 < min_LDMAX)
			min_LDMAX = LDMAX4;
		if (LDMAX6 < min_LDMAX)
			min_LDMAX = LDMAX6;

		if (VCMAX2 < min_VCMAX)
			min_VCMAX = VCMAX2;
		if (VCMAX4 < min_VCMAX)
			min_VCMAX = VCMAX4;
		if (VCMAX6 < min_VCMAX)
			min_VCMAX = VCMAX6;

		// Objectives for the non-scaled formulation
		obj[0] = (max_NOISE);
		obj[1] = (max_WEMP);
		obj[2] = (max_DOC);
		obj[3] = (max_ROUGH);
		obj[4] = (max_WFUEL);
		obj[5] = (max_PURCH);
		obj[6] = (min_RANGE);
		obj[7] = (min_LDMAX);
		obj[8] = (min_VCMAX);
		obj[9] = pvar1 + pvar2 + pvar3 + pvar4 + pvar5 + pvar6 + pvar7 + pvar8
				+ pvar9;

		// Constraints -- Similar to Laura's Study
		double NOISE2_CV = (NOISE2 - 75) / 75;
		double WEMP2_CV = (WEMP2 - 2200) / 2200;
		double DOC2_CV = (DOC2 - 80) / 80;
		double ROUGH2_CV = (ROUGH2 - 2) / 2;
		double WFUEL2_CV = (WFUEL2 - 450) / 450;
		double RANGE2_CV = -(RANGE2 - 2000) / 2000;

		double NOISE4_CV = (NOISE4 - 75) / 75;
		double WEMP4_CV = (WEMP4 - 2200) / 2200;
		double DOC4_CV = (DOC4 - 80) / 80;
		double ROUGH4_CV = (ROUGH4 - 2) / 2;
		double WFUEL4_CV = (WFUEL4 - 475) / 475;
		double RANGE4_CV = -(RANGE4 - 2000) / 2000;

		double NOISE6_CV = (NOISE6 - 75) / 75;
		double WEMP6_CV = (WEMP6 - 2200) / 2200;
		double DOC6_CV = (DOC6 - 80) / 80;
		double ROUGH6_CV = (ROUGH6 - 2) / 2;
		double WFUEL6_CV = (WFUEL6 - 500) / 500;
		double RANGE6_CV = -(RANGE6 - 2000) / 2000;

		if (NOISE2_CV < 0)
			NOISE2_CV = 0;
		if (WEMP2_CV < 0)
			WEMP2_CV = 0;
		if (DOC2_CV < 0)
			DOC2_CV = 0;
		if (ROUGH2_CV < 0)
			ROUGH2_CV = 0;
		if (WFUEL2_CV < 0)
			WFUEL2_CV = 0;
		if (RANGE2_CV < 0)
			RANGE2_CV = 0;

		if (NOISE4_CV < 0)
			NOISE4_CV = 0;
		if (WEMP4_CV < 0)
			WEMP4_CV = 0;
		if (DOC4_CV < 0)
			DOC4_CV = 0;
		if (ROUGH4_CV < 0)
			ROUGH4_CV = 0;
		if (WFUEL4_CV < 0)
			WFUEL4_CV = 0;
		if (RANGE4_CV < 0)
			RANGE4_CV = 0;

		if (NOISE6_CV < 0)
			NOISE6_CV = 0;
		if (WEMP6_CV < 0)
			WEMP6_CV = 0;
		if (DOC6_CV < 0)
			DOC6_CV = 0;
		if (ROUGH6_CV < 0)
			ROUGH6_CV = 0;
		if (WFUEL6_CV < 0)
			WFUEL6_CV = 0;
		if (RANGE6_CV < 0)
			RANGE6_CV = 0;

		double CV_TOTAL = NOISE2_CV + WEMP2_CV + DOC2_CV + ROUGH2_CV
				+ WFUEL2_CV + RANGE2_CV + NOISE4_CV + WEMP4_CV + DOC4_CV
				+ ROUGH4_CV + WFUEL4_CV + RANGE4_CV + NOISE6_CV + WEMP6_CV
				+ DOC6_CV + ROUGH6_CV + WFUEL6_CV + RANGE6_CV;

		constr[0] = CV_TOTAL > 0.03 ? CV_TOTAL : 0.0;

		// convert maximization objectives to minimization
		obj[6] = -obj[6];
		obj[7] = -obj[7];
		obj[8] = -obj[8];

		solution.setObjectives(obj);
		solution.setConstraints(constr);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(27, 10, 1);

		solution.setVariable(0, new RealVariable(0.24, 0.48));
		solution.setVariable(1, new RealVariable(7, 11));
		solution.setVariable(2, new RealVariable(0, 6));
		solution.setVariable(3, new RealVariable(5.5, 5.968));
		solution.setVariable(4, new RealVariable(19, 25));
		solution.setVariable(5, new RealVariable(85, 110));
		solution.setVariable(6, new RealVariable(14, 20));
		solution.setVariable(7, new RealVariable(3, 3.75));
		solution.setVariable(8, new RealVariable(0.46, 1));
		solution.setVariable(9, new RealVariable(0.24, 0.48));
		solution.setVariable(10, new RealVariable(7, 11));
		solution.setVariable(11, new RealVariable(0, 6));
		solution.setVariable(12, new RealVariable(5.5, 5.968));
		solution.setVariable(13, new RealVariable(19, 25));
		solution.setVariable(14, new RealVariable(85, 110));
		solution.setVariable(15, new RealVariable(14, 20));
		solution.setVariable(16, new RealVariable(3, 3.75));
		solution.setVariable(17, new RealVariable(0.46, 1));
		solution.setVariable(18, new RealVariable(0.24, 0.48));
		solution.setVariable(19, new RealVariable(7, 11));
		solution.setVariable(20, new RealVariable(0, 6));
		solution.setVariable(21, new RealVariable(5.5, 5.968));
		solution.setVariable(22, new RealVariable(19, 25));
		solution.setVariable(23, new RealVariable(85, 110));
		solution.setVariable(24, new RealVariable(14, 20));
		solution.setVariable(25, new RealVariable(3, 3.75));
		solution.setVariable(26, new RealVariable(0.46, 1));

		return solution;
	}

}
