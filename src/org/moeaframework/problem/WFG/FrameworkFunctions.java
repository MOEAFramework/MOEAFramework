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
class FrameworkFunctions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private FrameworkFunctions() {
		super();
	}

	public static double[] normalize_z(double[] z, double[] z_max) {
		double[] result = new double[z.length];

		for (int i = 0; i < z.length; i++) {
			assert (z[i] >= 0.0) && (z[i] <= z_max[i]);
			assert z_max[i] > 0.0;

			result[i] = z[i] / z_max[i];
		}

		return result;
	}

	public static double[] calculate_x(double[] t_p, int[] A) {
		assert Misc.vector_in_01(t_p);
		assert t_p.length != 0;
		assert A.length == t_p.length - 1;

		double[] result = new double[t_p.length];

		for (int i = 0; i < t_p.length - 1; i++) {
			assert (A[i] == 0) || (A[i] == 1);

			double tmp = Math.max(t_p[t_p.length - 1], A[i]);
			result[i] = tmp * (t_p[i] - 0.5) + 0.5;
		}

		result[t_p.length - 1] = t_p[t_p.length - 1];

		return result;
	}

	public static double[] calculate_f(double D, double[] x, double[] h,
			double[] S) {
		assert D > 0.0;
		assert Misc.vector_in_01(x);
		assert Misc.vector_in_01(h);
		assert x.length == h.length;
		assert h.length == S.length;

		double[] result = new double[h.length];

		for (int i = 0; i < h.length; i++) {
			assert S[i] > 0.0;

			result[i] = D * x[x.length - 1] + S[i] * h[i];
		}

		return result;
	}

}
