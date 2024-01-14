/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.problem.ZCAT;

/**
 * Shape function {@code f} for the Pareto front.
 */
interface PFShapeFunction {
	
	/**
	 * Applies the shape function to produce the Pareto front.
	 * 
	 * @param y the normalized decision variables
	 * @param M the number of objectives
	 * @return the result of applying the shape function
	 */
	public double[] apply(double[] y, int M);
	
	public static final PFShapeFunction F1 = (y, M) -> {
		double[] F = new double[M];
		
		F[0] = 1.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] *= Math.sin(y[i] * Math.PI / 2.0);
		}
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 1.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] *= Math.sin(y[i] * Math.PI / 2.0);
			}
			
			F[j] *= Math.cos(y[M - j - 1] * Math.PI / 2.0);
		}
		
		F[M - 1] = 1.0 - Math.sin(y[0] * Math.PI / 2.0);
		
		return fixTo01(F);
	};
	
	public static final PFShapeFunction F2 = (y, M) -> {
		double[] F = new double[M];
		
		F[0] = 1.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] *= 1.0 - Math.cos(y[i] * Math.PI / 2.0);
		}
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 1.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] *= 1.0 - Math.cos(y[i] * Math.PI / 2.0);
			}
			
			F[j] *= 1.0 - Math.sin(y[M - j - 1] * Math.PI / 2.0);
		}
		
		F[M - 1] = 1.0 - Math.sin(y[0] * Math.PI / 2.0);
		
		return F;
	};
	
	public static final PFShapeFunction F3 = (y, M) -> {
		double[] F = new double[M];
		
		F[0] = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] += y[i];
		}
		
		F[0] /= (M - 1);
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 0.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] += y[i];
			}
			
			F[j] += 1.0 - y[M - j - 1];
			F[j] /= (M - j);
		}
		
		F[M - 1] = 1.0 - y[0];
		
		return F;
	};
	
	public static final PFShapeFunction F4 = (y, M) -> {
		double[] F = new double[M];
		double sum = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = y[i];
			sum += y[i];
		}

		F[M - 1] = 1.0 - sum / (M - 1);
		
		return F;
	};
	
	public static final PFShapeFunction F5 = (y, M) -> {
		double[] F = new double[M];
		double sum = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = y[i];
			sum += 1.0 - y[i];
		}

		F[M - 1] = (Math.pow(Math.exp(sum / (M - 1)), 8.0) - 1.0) / (Math.pow(Math.exp(1.0), 8.0) - 1.0);
		
		return F;
	};
	
	public static final PFShapeFunction F6 = (y, M) -> {
		double[] F = new double[M];
		double mu = 0.0;
		final double k = 40.0;
		final double r = 0.05;
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = y[i];
			mu += y[i];
		}

		mu /= (M - 1);
		F[M - 1] = (Math.pow(1.0 + Math.exp(2.0 * k * mu - k), -1.0) - r * mu - Math.pow(1.0 + Math.exp(k), -1.0) + r) /
				(Math.pow(1.0 + Math.exp(-k), -1.0) - Math.pow(1.0 + Math.exp(k), -1.0) + r);
		
		return F;
	};
	
	public static final PFShapeFunction F7 = (y, M) -> {
		double[] F = new double[M];
		double sum = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = y[i];
			sum += Math.pow(0.5 - y[i], 5.0);
		}

		sum /= (2.0 * (M - 1) * Math.pow(0.5, 5.0));
		F[M - 1] = sum + 0.5;
		
		return F;
	};
	
	public static final PFShapeFunction F8 = (y, M) -> {
		double[] F = new double[M];
		
		F[0] = 1.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] *= 1.0 - Math.sin(y[i] * Math.PI / 2.0);
		}
		
		F[0] = 1.0 - F[0];
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 1.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] *= 1.0 - Math.sin(y[i] * Math.PI / 2.0);
			}
			
			F[j] *= 1.0 - Math.cos(y[M - j - 1] * Math.PI / 2.0);
			F[j] = 1.0 - F[j];
		}
		
		F[M - 1] = Math.cos(y[0] * Math.PI / 2.0);
		
		return F;
	};
	
	public static final PFShapeFunction F9 = (y, M) -> {
		double[] F = new double[M];
		
		F[0] = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] += Math.sin(y[i] * Math.PI / 2.0);
		}
		
		F[0] /= (M - 1);
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 0.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] += Math.sin(y[i] * Math.PI / 2.0);
			}
			
			F[j] += Math.cos(y[M - j - 1] * Math.PI / 2.0);
			F[j] /= (M - j);
		}
		
		F[M - 1] = Math.cos(y[0] * Math.PI / 2.0);
		
		return F;
	};
	
	public static final PFShapeFunction F10 = (y, M) -> {
		double[] F = new double[M];
		double sum = 0.0;
		final double r = 0.02;
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = y[i];
			sum += 1.0 - y[i];
		}

		F[M - 1] = (Math.pow(r, -1.0) - Math.pow(sum / (M - 1) + r, -1.0)) /
				(Math.pow(r, -1.0) - Math.pow(1.0 + r, -1.0));
		
		return F;
	};
	
	public static final PFShapeFunction F11 = (y, M) -> {
		double[] F = new double[M];
		final double k = 4.0;
		
		F[0] = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] += y[i];
		}
		
		F[0] /= (M - 1);
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 0.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] += y[i];
			}
			
			F[j] += 1.0 - y[M - j - 1];
			F[j] /= (M - j);
		}
		
		F[M - 1] = (Math.cos((2.0 * k - 1.0) * y[0] * Math.PI) + 2.0 * y[0] + 4.0 * k * (1.0 - y[0]) - 1.0) / (4.0 * k);
		
		return F;
	};
	
	public static final PFShapeFunction F12 = (y, M) -> {
		double[] F = new double[M];
		final double k = 3.0;
		
		F[0] = 1.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] *= (1.0 - y[i]);
		}
		
		F[0] = 1.0 - F[0];
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 1.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] *= (1.0 - y[i]);
			}
			
			F[j] *= y[M - j - 1];
			F[j] = 1.0 - F[j];
		}
		
		F[M - 1] = (Math.cos((2.0 * k - 1.0) * y[0] * Math.PI) + 2.0 * y[0] + 4.0 * k * (1.0 - y[0]) - 1.0) / (4.0 * k);
		
		return F;
	};
	
	public static final PFShapeFunction F13 = (y, M) -> {
		double[] F = new double[M];
		final double k = 3.0;
		
		F[0] = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[0] += Math.sin(y[i] * Math.PI / 2.0);
		}
		
		F[0] = 1.0 - F[0] / (M - 1.0);
				
		for (int j = 1; j < M - 1; j++) {
			F[j] = 0.0;
			
			for (int i = 0; i < M - j - 1; i++) {
				F[j] += Math.sin(y[i] * Math.PI / 2.0);
			}
			
			F[j] += Math.cos(y[M - j - 1] * Math.PI / 2.0);
			F[j] = 1.0 - F[j] / (M - j);
		}
		
		F[M - 1] = 1.0 - (Math.cos((2.0 * k - 1.0) * y[0] * Math.PI) + 2.0 * y[0] + 4.0 * k * (1.0 - y[0]) - 1.0) / (4.0 * k);
		
		return F;
	};
	
	public static final PFShapeFunction F14 = (y, M) -> {
		double[] F = new double[M];
		
		F[0] = Math.pow(Math.sin(y[0] * Math.PI / 2.0), 2.0);
		
		for (int i = 1; i < M - 2; i++) {
			F[i] = Math.pow(Math.sin(y[0] * Math.PI / 2.0), 2.0 + i / (M - 2.0));
		}
		
		if (M > 2) {
			F[M - 2] = 0.5 * (1.0 + Math.sin(6.0 * y[0] * Math.PI / 2.0 - Math.PI / 2.0));
		}
		
		F[M - 1] = Math.cos(y[0] * Math.PI / 2.0);
		
		return F;
	};
	
	public static final PFShapeFunction F15 = (y, M) -> {
		double[] F = new double[M];
		final double k = 3.0;
		
		for (int i = 0; i < M - 1; i++) {
			F[i] = Math.pow(y[0], 1.0 + i / (4.0 * M));
		}

		F[M - 1] = (Math.cos((2.0 * k - 1.0) *  y[0] * Math.PI) + 2.0 * y[0] + 4.0 * k * (1.0 - y[0]) - 1.0) / (4.0 * k);
		
		return F;
	};
	
	public static final PFShapeFunction F16 = (y, M) -> {
		double[] F = new double[M];
		final double k = 5.0;
		
		F[0] = Math.sin(y[0] * Math.PI / 2.0);
		
		for (int i = 1; i < M - 2; i++) {
			F[i] = Math.pow(Math.sin(y[0] * Math.PI / 2.0), 1.0 + i / (M - 2.0));
		}
		
		if (M > 2) {
			F[M - 2] = 0.5 * (1.0 + Math.sin(10.0 * y[0] * Math.PI / 2.0 - Math.PI / 2.0));
		}
		
		F[M - 1] = (Math.cos((2.0 * k - 1.0) * y[0] * Math.PI) + 2.0 * y[0] + 4.0 * k * (1.0 - y[0]) - 1.0) / (4.0 * k);
		
		return F;
	};
	
	public static final PFShapeFunction F17 = (y, M) -> {
		double[] F = new double[M];
		boolean wedge = allValuesBetween(y, M - 1, 0.0, 0.5);
		double sum = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			if (wedge) {
				F[i] = y[0];
			} else {
				F[i] = y[i];
				sum += 1.0 - y[i];
			}
		}
		
		if (wedge) {
			F[M - 1] = (Math.pow(Math.exp(1.0 - y[0]), 8.0) - 1.0) / (Math.pow(Math.exp(1.0), 8.0) - 1.0);
		} else {
			F[M - 1] = (Math.pow(Math.exp(sum / (M - 1)), 8.0) - 1.0) / (Math.pow(Math.exp(1.0), 8.0) - 1.0);
		}

		return F;
	};
	
	public static final PFShapeFunction F18 = (y, M) -> {
		double[] F = new double[M];
		boolean wedge = allValuesBetween(y, M - 1, 0.0, 0.4) || allValuesBetween(y, M - 1, 0.6, 1.0);
		double sum = 0.0;
		
		for (int i = 0; i < M - 1; i++) {
			if (wedge) {
				F[i] = y[0];
			} else {
				F[i] = y[i];
				sum += Math.pow(0.5 - y[i], 5.0);
			}
		}
		
		if (wedge) {
			F[M - 1] = (Math.pow(0.5 - y[0], 5.0) + Math.pow(0.5, 5.0)) / (2.0 * Math.pow(0.5, 5.0));
		} else {
			F[M - 1] = sum / (2.0 * (M - 1.0)* Math.pow(0.5, 5.0)) + 0.5;
		}

		return F;
	};
	
	public static final PFShapeFunction F19 = (y, M) -> {
		double[] F = new double[M];
		final double A = 5.0;
		double mu = 0.0;
		
		boolean flag = valueBetween(y[0], 0.0, 0.2) || valueBetween(y[0], 0.4, 0.6);

		for (int i = 0; i < M - 1; i++) {
			mu += y[i];
			F[i] = flag ? y[0] : y[i];
		}
		
		mu = flag ? y[0] : mu / (M - 1);
		F[M - 1] = (1.0 - mu - Math.cos(2.0 * A * Math.PI * mu + Math.PI / 2.0) / (2.0 * A * Math.PI));

		return fixTo01(F);
	};
	
	public static final PFShapeFunction F20 = (y, M) -> {
		double[] F = new double[M];
		double sum = 0.0;
		
		boolean flag = valueBetween(y[0], 0.1, 0.4) || valueBetween(y[0], 0.6, 0.9);

		for (int i = 0; i < M - 1; i++) {
			sum += Math.pow(0.5 - y[i], 5.0);
			F[i] = flag ? y[0] : y[i];
		}
		
		if (flag) {
			F[M - 1] = (Math.pow(0.5 - y[0], 5.0) + Math.pow(0.5, 5.0)) / (2.0 * Math.pow(0.5, 5.0));
		} else {
			F[M - 1] = sum / (2.0 * (M - 1.0) * Math.pow(0.5, 5.0)) + 0.5;
		}
		
		return F;
	};

	static double fixTo01(double value) {
		if (value <= 0.0 && value >= 0.0 - ZCAT.EPSILON) {
			return 0.0;
		}
		
		if (value >= 1.0 && value <= 1.0 + ZCAT.EPSILON) {
			return 1.0;
		}
		
		return value;
	}
	
	static double[] fixTo01(double[] F) {
		for (int i = 0; i < F.length; i++) {
			F[i] = fixTo01(F[i]);
		}
		
		return F;
	}
	
	public static boolean allValuesBetween(double[] y, int m, double lb, double ub) {
		for (int i = 0; i < m; i++) {
			if (!valueBetween(y[i], lb, ub)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean valueBetween(double y, double lb, double ub) {
		if (!(lessThan(lb, y) && lessThan(y, ub))) {
			return false;
		}
		
		return true;
	}
	
	static boolean lessThan(double x, double y) {
		if (x < y) {
			return true;
		}
		
		if (Math.abs(y - x) < ZCAT.EPSILON) {
			return true;
		}
		
		return false;
	}
	
}
