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

import org.moeaframework.core.Settings;

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
class Misc {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Misc() {
		super();
	}

	public static double correct_to_01(double a) {
		if ((a <= 0.0) && (a >= 0.0 - Settings.EPS)) {
			return 0.0;
		} else if ((a >= 1.0) && (a <= 1.0 + Settings.EPS)) {
			return 1.0;
		} else {
			return a;
		}
	}

	public static boolean vector_in_01(double[] x) {
		for (int i = 0; i < x.length; i++) {
			if ((x[i] < 0.0) || (x[i] > 1.0)) {
				return false;
			}
		}
		return true;
	}

}
