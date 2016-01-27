/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/* 
 * The following source code is derived from the Coco Framework available at
 * <https://github.com/numbbo/coco> under the 3-clause BSD license. The
 * original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS
 * file located in the Coco Framework repository for more details.
 */

/**
 * The 101-peak Gallagher function.  It is not intended for this function to be
 * used directly since the BBOB test suite applies additional transformations
 * to the test functions.
 * <p>
 * Properties:
 * <ul>
 *   <li>Multimodal (with random locations of local optima)
 * </ul>
 */
public class Gallagher extends BBOBFunction {

	/**
	 * The rotation matrix.
	 */
	private final double[][] rotation;
	
	/**
	 * The location of the local optima.
	 */
	private final double[][] xLocal;
	
	/**
	 * Controls the steepness of the peaks.
	 */
	private final double[][] arrScales;
	
	/**
	 * The magnitude of the peaks.
	 */
	private final double[] peaks;
	
	/**
	 * Constructs a new instance of the 101-peak Gallagher function.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param rotation the rotation matrix
	 * @param xLocal the location of the local optima
	 * @param arrScales controls the steepness of the peaks
	 * @param peaks the magnitude of the peaks
	 */
	public Gallagher(int numberOfVariables, double[][] rotation, double[][] xLocal, double[][] arrScales, double[] peaks) {
		super(numberOfVariables);
		this.rotation = rotation;
		this.xLocal = xLocal;
		this.arrScales = arrScales;
		this.peaks = peaks;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] tmx = new double[x.length];
		double f = 0.0;
		double a = 0.1;
		double penalty = 0.0;
		double ftrue = 0.0;
		double fac = -0.5 / x.length;
		
		/* Boundary handling */
		for (int i = 0; i < x.length; i++) {
			double tmp = Math.abs(x[i]) - 5.0;
			
			if (tmp > 0.0) {
				penalty += tmp*tmp;
			}
		}
		
		/* Transformation in search space */
		for (int i = 0; i < x.length; i++) {
			tmx[i] = 0.0;
			
			for (int j = 0; j < x.length; j++) {
				tmx[i] += rotation[i][j] * x[j];
			}
		}
		
		/* Computation core */
		for (int i = 0; i < peaks.length; i++) {
			double tmp2 = 0.0;
			
			for (int j = 0; j < x.length; j++) {
				double tmp1 = (tmx[j] - xLocal[j][i]);
				tmp2 += arrScales[i][j] * tmp1 * tmp1;
			}
			
			tmp2 = peaks[i] * Math.exp(fac * tmp2);
			f = Math.max(f, tmp2);
		}
		
		f = 10.0 - f;
		
		if (f > 0.0) {
			ftrue = Math.log(f) / a;
			ftrue = Math.pow(Math.exp(ftrue + 0.49 * (Math.sin(ftrue) + Math.sin(0.79 * ftrue))), a);
		} else if (f < 0.0) {
			ftrue = Math.log(-f) / a;
			ftrue = -Math.pow(Math.exp(ftrue + 0.49 * (Math.sin(0.55 * ftrue) + Math.sin(0.31 * ftrue))), a);
		} else {
			ftrue = f;
		}
		
		solution.setObjective(0, ftrue*ftrue + penalty);
	}

}
