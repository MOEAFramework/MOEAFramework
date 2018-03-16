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
package org.moeaframework.analysis.sensitivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the fractal dimension for a set of points.  
 */
class FractalDimension {

	/**
	 * The maximum radius.
	 */
	private static final double R_MAX = 0.25;
	
	/**
	 * The minimum radius.
	 */
	private static final double R_MIN = 0.0005;
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private FractalDimension() {
		super();
	}
	
	/**
	 * Returns the correlation sum for the specified points using the specified
	 * radius.
	 * 
	 * @param points the points
	 * @param r the radius
	 * @return the correlation sum for the specified points using the specified
	 *         radius
	 */
	private static double computeCorrelationSum(double[][] points, double r) {
		double sum = 0.0;
		int n = points.length;
		
		for (int i=0; i<=n-2; i++) {
			for (int j=i+1; j<=n-1; j++) {
				if ((r - distance(points[i], points[j])) >= 0) {
					sum += 1.0;
				}
			}
		}
		
		return sum / (n*(n-1)/2.0);
	}
	
	/**
	 * Returns the Euclidean distance between two points.
	 */
	private static double distance(double[] p1, double[] p2) {
		double sum = 0.0;
		
		for (int i=0; i<p1.length; i++) {
			sum += Math.pow(p2[i] - p1[i], 2.0);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * Identifies the "plateau region" --- the region where the slope is a
	 * constant.  For different values of i, this method removes the first and
	 * last i points from consideration.  Computes the correlation coefficient
	 * of the remaining points to determine how well those points approximate
	 * a line.
	 */
	private static int getCorrelationDimensionBounds(List<Double> X, 
			List<Double> Y) {
		int n = X.size();
		
		for (int i=0; i<(n - 4)/2; i++) {
			double sumX = 0.0;
			double sumXX = 0.0;
			double sumY = 0.0;
			double sumXY = 0.0;
			double sumYY = 0.0;
			
			for (int j=i; j<n-i; j++) {
				sumX += X.get(j);
				sumXX += X.get(j)*X.get(j);
				sumY += Y.get(j);
				sumXY += X.get(j)*Y.get(j);
				sumYY += Y.get(j)*Y.get(j);
			}
			
			int k = n - 2*i;
			double correlationCoefficient =  (k*sumXY - sumX*sumY) /
					(Math.sqrt(k*sumXX - sumX*sumX)*Math.sqrt(k*sumYY - 
							sumY*sumY));
			
			if (correlationCoefficient >= 0.99) {
				return i;
			}
		}
		
		return 0;
	}
	
	/**
	 * Returns the fractal dimension for the specified points.
	 * 
	 * @param parameters the points
	 * @return the fractal dimension for the specified points
	 */
	public static double computeDimension(double[][] parameters) {
		if (parameters.length == 0) {
			return 0.0;
		}
		
		List<Double> X = new ArrayList<Double>();
		List<Double> Y = new ArrayList<Double>();

		for (double r=R_MAX; r>= R_MIN; r-= R_MIN) {
			double lr = Math.log(r);
			double lc = Math.log(computeCorrelationSum(parameters, r));
			
			if (lc == Double.NEGATIVE_INFINITY) {
				break;
			}
				
			X.add(lr);
			Y.add(lc);
		}
		
		//perform linear least-squares regression to find slope
		int n = X.size();
		
		if (n < 5) {
			return 0.0;
		}
		
		double sumX = 0.0;
		double sumXX = 0.0;
		double sumY = 0.0;
		double sumXY = 0.0;
		int remove = getCorrelationDimensionBounds(X, Y);

		for (int i=remove; i<n-remove; i++) {
			sumX += X.get(i);
			sumXX += X.get(i)*X.get(i);
			sumY += Y.get(i);
			sumXY += X.get(i)*Y.get(i);
		}
		
		return (sumX*sumY - n*sumXY) / (sumX*sumX - n*sumXX);
	}

}

