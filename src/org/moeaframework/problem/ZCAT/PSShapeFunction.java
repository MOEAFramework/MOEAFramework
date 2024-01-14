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

import org.moeaframework.util.Vector;

/**
 * Shape function {@code g} for the Pareto set.
 */
interface PSShapeFunction {
	
	/**
	 * Applies the shape function to the Pareto set.
	 * 
	 * @param y the normalized decision variables
	 * @param m the dimension of the Pareto set
	 * @param n the total number of decision variables
	 * @return the result of applying the shape function
	 */
	public double[] apply(double[] y, int m, int n);
	
	public static final PSShapeFunction G0 = (y, m, n) -> {
		return Vector.of(n - m, 0.2210);
	};
	
	public static final PSShapeFunction G1 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.sin(1.5 * Math.PI * y[i] + theta(j, m, n));
			}
			
			g[j] = sum / (2.0 * m) + 0.5;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G2 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.pow(y[i], 2.0) * Math.sin(4.5 * Math.PI * y[i] + theta(j, m, n));
			}
			
			g[j] = sum / (2.0 * m) + 0.5;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G3 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.pow(Math.cos(Math.PI * y[i] + theta(j, m, n)), 2.0);
			}
			
			g[j] = sum / m;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G4 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double mu = 0.0;
			
			for (int i = 0; i < m; i++) {
				mu += y[i];
			}
			
			mu /= m;
			g[j] = (mu / 2.0) * Math.cos(4.0 * Math.PI * mu + theta(j, m, n)) + 0.5;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G5 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.pow(Math.sin(2.0 * Math.PI * y[i] + theta(j, m, n) - 1.0), 3.0);
			}
			
			g[j] = sum / (2.0 * m) + 0.5;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G6 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum1 = 0.0;
			double sum2 = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum1 += Math.pow(y[i], 2.0);
				sum2 += Math.pow(Math.cos(11.0 * Math.PI * y[i] + theta(j, m, n)), 3.0);
			}
			
			sum1 /= m;
			sum2 /= m;
			g[j] = (-10.0 * Math.exp((-2.0 / 5.0) * Math.sqrt(sum1)) - Math.exp(sum2) + 10.0 + Math.exp(1.0)) /
					(-10.0 * Math.exp(-2.0 / 5.0) - Math.pow(Math.exp(1.0), -1.0) + 10.0 + Math.exp(1.0));
		}
		
		return g;
	};
	
	public static final PSShapeFunction G7 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double mu = 0.0;
			
			for (int i = 0; i < m; i++) {
				mu += y[i];
			}
			
			mu /= m;
			g[j] = (mu + Math.exp(Math.sin(7.0 * Math.PI * mu - Math.PI / 2.0 + theta(j, m, n))) - Math.exp(-1.0)) /
					(1.0 + Math.exp(1.0) - Math.exp(-1.0));
		}
		
		return g;
	};
	
	public static final PSShapeFunction G8 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.abs(Math.sin(2.5 * Math.PI * (y[i] - 0.5) + theta(j, m, n)));
			}
			
			g[j] = sum / m;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G9 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			double mu = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.abs(Math.sin(2.5 * Math.PI * y[i] - Math.PI / 2.0 + theta(j, m, n)));
				mu += y[i];
			}
			
			mu /= m;
			g[j] = mu / 2.0 - sum / (2.0 * m) + 0.5;
		}
		
		return g;
	};
	
	public static final PSShapeFunction G10 = (y, m, n) -> {
		double[] g = new double[n - m];
		
		for (int j = 0; j < n - m; j++) {
			double sum = 0.0;
			
			for (int i = 0; i < m; i++) {
				sum += Math.sin((4.0 * y[i] - 2.0) * Math.PI + theta(j, m, n));
			}
			
			g[j] = Math.pow(sum, 3.0) / (2.0 * Math.pow(m, 3.0)) + 0.5;
		}
		
		return g;
	};
	
	static double theta(int j, int m, int n) {
		return 2.0 * Math.PI * j / (n - m);
	}

}
