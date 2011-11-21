/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.studies.sobol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FractalDimension {

	public static final double R_MAX = 0.1;
	public static final double R_MIN = 0.001;
	
//	/**
//	 * Returns the number of occupied boxes.  This method was replaced by
//	 * countUnique for performance.
//	 */
//	private int count(double[][] points, int sections) {
//		int n = points[0].length;
//		int[] count = new int[(int)Math.pow(sections, n)];
//		for (double[] point : points) {
//			count[index(point, sections)]++;
//		}
//		
//		int sum = 0;
//		for (int i=0; i<count.length; i++)
//			if (count[i] > 0)
//				sum++;
//		return sum;
//	}
	
	/**
	 * Returns the number of occupied boxes.
	 */
	private static int countUnique(double[][] points, int sections) {
		int[][] discpoints = new int[points.length][];
		for (int i=0; i<points.length; i++)
			discpoints[i] = discretizePoint(points[i], sections);
		
		Arrays.sort(discpoints, new Comparator<int[]>() {

			public int compare(int[] arg0, int[] arg1) {
				for (int i=0; i<arg0.length; i++) {
					if (arg0[i] < arg1[i])
						return -1;
					else if (arg0[i] > arg1[i])
						return 1;
				}
				return 0;
			}
			
		});
		
		int count = 1;
		int[] last = discpoints[0];
		for (int i=1; i<discpoints.length; i++) {
			boolean equal = true;
			for (int j=0; j<last.length; j++)
				if (discpoints[i][j] != last[j])
					equal = false;
			
			if (!equal) {
				count++;
				last = discpoints[i];
			}
		}
		return count;
	}
	
	/**
	 * Discretizes the point given that each dimension is divided up into the
	 * specified number of sections.  Assumes the points are bounded in [0, 1].
	 */
	private static int[] discretizePoint(double[] point, int sections) {
		int[] disc = new int[point.length];
		for (int i=0; i<point.length; i++) {
			double min = 0.0;
			double max = 1.0;
			double step = (max - min) / sections;
			disc[i] = (int)Math.floor((point[i] - min) / step);
		}
		return disc;
	}
	
//	/**
//	 * Returns the box index for the box counting dimension.
//	 */
//	private int index(double[] point, int sections) {
//		int[] dpoint = discretizePoint(point, sections);
//		
//		int sum = 0;
//		for (int i=0; i<dpoint.length; i++) {
//			int prod = 1;
//			prod *= (int)Math.pow(sections, i);
//			sum += dpoint[i] * prod;
//		}
//		return sum;
//	}
	
//	/**
//	 * Returns the correlation sum using radius r.  Bounds the space by the
//	 * radius to ensure complete saturation.
//	 */
//	private double computeCorrelationSumBounded(double[][] points, double r) {
//		int n = points.length;
//		double sum = 0.0;
//		int count = 0;
//		
//		outer: for (int i=0; i<n; i++) {
//			for (int j=0; j<points[i].length; j++) {
//				if ((points[i][j] < R_MAX) ||(points[i][j] > 1.0-R_MAX)) {
//					continue outer;
//				}
//			}
//			
//			for (int j=0; j<n; j++) {
//				if (i == j) {
//					continue;
//				}
//				
//				count++;
//				
//				if (r - distance(points[i], points[j]) >= 0) {
//					sum += 1.0;
//				}
//			}
//		}
//		
//		return sum / count;
//	}
	
//	/**
//	 * Precomputes the pairwise distances between points.  The distance between
//	 * points i and j is stored in index pdisti(i, j, points.length).
//	 */
//	private double[] pdist(double[][] points) {
//		int n = points.length;
//		double[] distance = new double[n*(n-1)/2];
//		
//		for (int i=0; i<=n-2; i++) {
//			for (int j=i+1; j<=n-1; j++) {
//				distance[pdisti(i, j, n)] = distance(points[i], points[j]);
//			}
//		}
//		
//		return distance;
//	}
	
//	/**
//	 * Returns the index in the precomputed distance array of the distance
//	 * between points i and j.
//	 */
//	private int pdisti(int i, int j, int n) {
//		if (i > j) {
//			int tmp = i;
//			i = j;
//			j = tmp;
//		}
//		
//		return n*(n-1)/2 - (n-i)*(n-i-1)/2 + (j-i) - 1;
//	}
	
//	/**
//	 * Computes the correlation sum using the precomputed distances in pdist.
//	 */
//	private double computeCorrelationSumUsingDistance(int n, double[] pdist, double r) {
//		double sum = 0.0;
//		
//		for (int i=0; i<=n-2; i++) {
//			for (int j=i+1; j<=n-1; j++) {
//				if ((r - pdist[pdisti(i, j, n)]) >= 0)
//					sum += 1.0;
//			}
//		}
//		
//		return sum / (n*(n-1)/2);
//	}
	
	private static double computeCorrelationSum(double[][] points, double r) {
		double sum = 0.0;
		int n = points.length;
		
		for (int i=0; i<=n-2; i++) {
			for (int j=i+1; j<=n-1; j++) {
				if ((r - distance(points[i], points[j])) >= 0)
					sum += 1.0;
			}
		}
		
		return sum / (n*(n-1)/2.0);
	}
	
	/**
	 * Returns the Euclidean distance between two points.
	 */
	private static double distance(double[] p1, double[] p2) {
		double sum = 0.0;
		for (int i=0; i<p1.length; i++)
			sum += Math.pow(p2[i] - p1[i], 2.0);
		return Math.sqrt(sum);
	}
	
	static enum Mode {
		BoxCounting,
		Correlation
	}
	
	/**
	 * For testing purposes, sets data to a uniformly distributed set of data.
	 */
	public static double[][] uniform(int k, int m) {
		Random random = new Random();
		double[][] data = new double[k][m];

		for (int i=0; i<k; i++) {
			for (int j=0; j<m; j++)
				data[i][j] = random.nextDouble();
		}
		
		return data;
	}
	
	public static double[][] halfUniform(int k, int m) {
		Random random = new Random();
		double[][] data = new double[k][m];

		for (int i=0; i<k; i++) {
			for (int j=0; j<m; j++)
				data[i][j] = random.nextDouble()/2;
		}
		
		return data;
	}
	
	/**
	 * For testing purposes, sets data to a normally distributed set of data.
	 */
	public static double[][] gaussian(int k, int m, double s) {
		Random random = new Random();
		double[][] data = new double[k][m];

		for (int i=0; i<k; i++) {
			for (int j=0; j<m; j++)
				data[i][j] = 0.5 + s * random.nextGaussian();
		}
		
		return data;
	}
	
	/**
	 * Identifies the "plateau region" --- the region where the slope is a
	 * constant.  For different values of i, this method removes the first and
	 * last i points from consideration.  Computes the correlation coefficient
	 * of the remaining points to determine how well those points approximate
	 * a line.
	 */
	private static int getCorrelationDimensionBounds(List<Double> X, List<Double> Y) {
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
			double correlationCoefficient =  (k*sumXY - sumX*sumY)/(Math.sqrt(k*sumXX - sumX*sumX)*Math.sqrt(k*sumYY - sumY*sumY));
			if (correlationCoefficient >= 0.99)
				return i;
		}
		
		return 0;
	}
	
	public static double computeDimension(double[][] parameters) {
		if (parameters.length == 0) {
			return 0.0;
		}
		
		List<Double> X = new ArrayList<Double>();
		List<Double> Y = new ArrayList<Double>();
		
		Mode mode = Mode.Correlation;
		
		switch (mode) {
		case Correlation:
			//double[] distance = pdist(parameters);
			for (double r=R_MAX; r>= R_MIN; r-= R_MIN) {
				double lr = Math.log(r);
				//double lc = Math.log(computeCorrelationSumUsingDistance(parameters.length, distance, r));
				double lc = Math.log(computeCorrelationSum(parameters, r));
				if (lc == Double.NEGATIVE_INFINITY)
					break;		
					
				X.add(lr);
				Y.add(lc);
			}
			break;
		
		case BoxCounting:
			for (int i=2; i<=50; i++) {
				double li = Math.log(i);
				double lc = Math.log(countUnique(parameters, i));
				if (lc == Double.NEGATIVE_INFINITY)
					break;
				
				X.add(li);
				Y.add(lc);
			}
			break;
		}
		
		//perform linear least-squares regression to find slope
		int n = X.size();
		
		if (n < 5)
			return 0.0;
		
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
		
		
		double m = (sumX*sumY - n*sumXY) / (sumX*sumX - n*sumXX);
		return m;
	}

}

