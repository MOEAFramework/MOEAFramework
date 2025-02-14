/* Copyright 2009-2025 David Hadka
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

import org.moeaframework.core.variable.RealVariable;

/*
 * The following source code is derived from the Coco Framework available at <https://github.com/numbbo/coco> under the
 * 3-clause BSD license.  The original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS file located in
 * the Coco Framework repository for more details.
 */

class BBOBUtils {
	
	/**
	 * The lower bound returned by {@link #createTransformedVariable()}.
	 */
	private static double lowerBound = -100000.0;
	
	/**
	 * The upper bound returned by {@link #createTransformedVariable()}.
	 */
	private static double upperBound = 100000.0;
	
	private BBOBUtils() {
		super();
	}
	
	/**
	 * Overrides the lower and upper bounds returned by {@link #createTransformedVariable()}.  The bounds must be
	 * sufficiently large, otherwise an exception may be thrown.
	 * 
	 * @param lower the new lower bounds
	 * @param upper the new upper bounds
	 */
	public static void setBounds(double lower, double upper) {
		lowerBound = lower;
		upperBound = upper;
	}
	
	/**
	 * All BBOB problems are defined with the domain {@code [-5, 5]}, but the inner, transformed functions are defined
	 * over the entire real domain.  This method returns a real decision variable bounded from
	 * {@code [LOWER_BOUNDS, UPPER_BOUNDS]}, which practically covers the entire real domain.
	 * 
	 * @return a real decision variable used by the BBOB inner functions
	 */
	public static RealVariable createTransformedVariable() {
		return new RealVariable(lowerBound, upperBound);
	}

	public static double[] uniform(int N, long inseed) {
		long aktseed;
		int tmp;
		long[] rgrand = new long[32];
		long aktrand;
		int i;
		double[] r = new double[N];

		if (inseed < 0) {
			inseed = -inseed;
		}

		if (inseed < 1) {
			inseed = 1;
		}

		aktseed = inseed;

		for (i = 39; i >= 0; i--) {
			tmp = (int)Math.floor((double) aktseed / (double) 127773);
			aktseed = 16807 * (aktseed - tmp * 127773) - 2836 * tmp;

			if (aktseed < 0) {
				aktseed = aktseed + 2147483647;
			}

			if (i < 32) {
				rgrand[i] = aktseed;
			}
		}

		aktrand = rgrand[0];

		for (i = 0; i < N; i++) {
			tmp = (int)Math.floor((double) aktseed / (double) 127773);
			aktseed = 16807 * (aktseed - tmp * 127773) - 2836 * tmp;

			if (aktseed < 0) {
				aktseed = aktseed + 2147483647;
			}

			tmp = (int)Math.floor((double) aktrand / (double) 67108865);
			aktrand = rgrand[tmp];
			rgrand[tmp] = aktseed;
			r[i] = (double) aktrand / 2.147483647e9;

			if (r[i] == 0.0) {
				r[i] = 1e-99;
			}
		}
		return r;
	}

	public static double[] computeXOpt(long seed, int N) {
		double[] xopt = uniform(N, seed);

		for (int i = 0; i < N; i++) {
			xopt[i] = 8 * Math.floor(1e4 * xopt[i]) / 1e4 - 4;

			if (xopt[i] == 0.0) {
				xopt[i] = -1e-5;
			}
		}

		return xopt;
	}

	public static double computeFOpt(int function, int instance) {
		long rseed, rrseed;

		rseed = switch (function) {
			case 4 -> 3;
			case 18 -> 17;
			case 101, 102, 103, 107, 108, 109 -> 1;
			case 104, 105, 106, 110, 111, 112 -> 8;
			case 113, 114, 115 -> 7;
			case 116, 117, 118 -> 10;
			case 119, 120, 121 -> 14;
			case 122, 123, 124 -> 17;
			case 125, 126, 127 -> 19;
			case 128, 129, 130 -> 21;
			default -> function;
		};

		rrseed = rseed + (10000 * instance);
		double gval = gauss(1, rrseed)[0];
		double gval2 = gauss(1, rrseed + 1)[0];
		return fmin(1000., fmax(-1000., round(100. * 100. * gval / gval2) / 100.));
	}

	public static double fmin(double a, double b) {
		return (a < b) ? a : b;
	}

	public static double fmax(double a, double b) {
		return (a > b) ? a : b;
	}

	public static double round(double x) {
		return Math.floor(x + 0.5);
	}

	public static double[] gauss(int N, long seed) {
		int i;
		double[] uniftmp = uniform(2 * N, seed);
		double[] g = new double[N];

		for (i = 0; i < N; i++) {
			g[i] = Math.sqrt(-2 * Math.log(uniftmp[i])) * Math.cos(2 * Math.PI * uniftmp[N + i]);

			if (g[i] == 0.0) {
				g[i] = 1e-99;
			}
		}

		return g;
	}

	public static double[][] reshape(double[] vector, int m, int n) {
		double[][] B = new double[m][n];

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = vector[j * m + i];
			}
		}

		return B;
	}

	public static double[][] computeRotation(long seed, int N) {
		double prod;
		double[] gvect = gauss(N * N, seed);
		double[][] B = reshape(gvect, N, N);

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < i; j++) {
				prod = 0;

				for (int k = 0; k < N; k++) {
					prod += B[k][i] * B[k][j];
				}

				for (int k = 0; k < N; k++) {
					B[k][i] -= prod * B[k][j];
				}
			}

			prod = 0;

			for (int k = 0; k < N; k++) {
				prod += B[k][i] * B[k][i];
			}

			for (int k = 0; k < N; k++) {
				B[k][i] /= Math.sqrt(prod);
			}
		}
		
		return B;
	}
	
}
