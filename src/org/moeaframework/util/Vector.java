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
package org.moeaframework.util;

import org.moeaframework.core.Settings;

/**
 * Mathematical operators for manipulating vectors (double arrays).
 */
public class Vector {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Vector() {
		super();
	}

	/**
	 * Returns the length of the two specified vectors.
	 * 
	 * @param u the first vector
	 * @param v the second vector
	 * @return the length of the two specified vectors
	 * @throws IllegalArgumentException if the two vectors are not the same length
	 */
	private static int length(double[] u, double[] v) {
		if (u.length != v.length) {
			throw new IllegalArgumentException("vectors must have same length");
		}

		return u.length;
	}

	/**
	 * Returns the difference between the two specified vectors, {@code u - v}.
	 * The two vectors must be of the same length.
	 * 
	 * @param u the first vector
	 * @param v the second vector
	 * @return the difference between the two specified vectors, {@code u - v}
	 * @throws IllegalArgumentException if the two vectors are not the same length
	 */
	public static double[] subtract(double[] u, double[] v) {
		int n = length(u, v);
		double[] w = new double[n];

		for (int i = 0; i < n; i++) {
			w[i] = u[i] - v[i];
		}

		return w;
	}

	/**
	 * Returns the sum of the two specified vectors, {@code u + v}. The two
	 * vectors must be of the same length.
	 * 
	 * @param u the first vector
	 * @param v the second vector
	 * @return the sum of the two specified vectors, {@code u + v}
	 * @throws IllegalArgumentException if the two vectors are not the same length
	 */
	public static double[] add(double[] u, double[] v) {
		int n = length(u, v);
		double[] w = new double[n];

		for (int i = 0; i < n; i++) {
			w[i] = u[i] + v[i];
		}

		return w;
	}

	/**
	 * Returns the scalar multiple of the specified vector, {@code a * u}.
	 * 
	 * @param a the scalar value
	 * @param u the vector
	 * @return the scalar multiple of the specified vector, {@code a * u}
	 */
	public static double[] multiply(double a, double[] u) {
		int n = u.length;
		double[] w = new double[n];

		for (int i = 0; i < n; i++) {
			w[i] = a * u[i];
		}

		return w;
	}

	/**
	 * Returns the negation of the specified vector, {@code -u}. This is
	 * equivalent to calling {@code multiply(-1, u)}.
	 * 
	 * @param u the vector
	 * @return the negation of the specified vector, {@code -u}
	 */
	public static double[] negate(double[] u) {
		return multiply(-1.0, u);
	}

	/**
	 * Returns the scalar division of the specified vector, {@code u / a}.
	 * 
	 * @param u the vector
	 * @param a the scalar value (the denominator)
	 * @return the scalar division of the specified vector, {@code u / a}
	 */
	public static double[] divide(double[] u, double a) {
		return multiply(1.0 / a, u);
	}

	/**
	 * Returns the dot (inner) product of the two specified vectors. The two
	 * vectors must be the same length.
	 * 
	 * @param u the first vector
	 * @param v the second vector
	 * @return the dot (inner) product of the two specified vectors
	 * @throws IllegalArgumentException if the two vectors are not the same length
	 */
	public static double dot(double[] u, double[] v) {
		int n = length(u, v);
		double dot = 0.0;

		for (int i = 0; i < n; i++) {
			dot += u[i] * v[i];
		}

		return dot;
	}

	/**
	 * Returns the magnitude (Euclidean norm) of the specified vector.
	 * 
	 * @param u the vector
	 * @return the magnitude (Euclidean norm) of the specified vector
	 */
	public static double magnitude(double[] u) {
		return Math.sqrt(dot(u, u));
	}

	/**
	 * Returns the specified vector normalized to have a magnitude of 1. The
	 * specified vector must contain at least one non-zero component; otherwise
	 * an exception is thrown.
	 * 
	 * @param u the vector
	 * @return the specified vector normalized to have a magnitude of 1
	 * @throws IllegalArgumentException if the specified vector contains all zeros
	 */
	public static double[] normalize(double[] u) {
		if (isZero(u)) {
			throw new IllegalArgumentException("can not normalize zero vector");
		}

		return multiply(1.0 / magnitude(u), u);
	}

	/**
	 * Returns the projection of {@code u} onto {@code v}. The two vectors must
	 * be the same length.
	 * 
	 * @param u the vector being projected
	 * @param v the vector onto which {@code u} is being projected
	 * @return the projection of {@code u} onto {@code v}
	 * @throws IllegalArgumentException if the two vectors are not the same 
	 *         length
	 */
	public static double[] project(double[] u, double[] v) {
		return multiply(dot(u, v) / dot(v, v), v);
	}

	/**
	 * Returns the orthogonal basis for the specified vectors using the
	 * Gram-Schmidt process.
	 * 
	 * @param vs the vectors to be orthogonalized
	 * @return the orthogonal basis
	 */
	public static double[][] orthogonalize(double[][] vs) {
		vs = vs.clone();

		for (int i = 1; i < vs.length; i++) {
			for (int j = 0; j < i; j++) {
				vs[i] = subtract(vs[i], project(vs[i], vs[j]));
			}
		}

		return vs;
	}

	/**
	 * Returns the vector {@code u} orthogonal to the already orthogonalized
	 * vectors {@code vs}. This method is provided to allow incremental
	 * construction of the orthogonal basis:
	 * 
	 * <pre>
	 * List&lt;double[]&gt; basis = new ArrayList&lt;double[]&gt;();
	 * for (double[] v : vectors) {
	 * 	double[] e = orthogonalize(v, basis);
	 * 	basis.add(e);
	 * }
	 * </pre>
	 * 
	 * @param u the vector
	 * @param vs the already orthogonalized vectors
	 * @return the vector {@code u} orthogonal to the already orthogonalized
	 *         vectors {@code vs}
	 */
	public static double[] orthogonalize(double[] u, Iterable<double[]> vs) {
		for (double[] v : vs) {
			u = subtract(u, project(u, v));
		}

		return u;
	}

	/**
	 * Returns the mean vector of the specified vectors.
	 * 
	 * @param vs the vectors
	 * @return the mean vector of the specified vectors
	 * @throws IllegalArgumentException if the specified vectors is empty
	 */
	public static double[] mean(double[][] vs) {
		int k = vs.length;

		if (k == 0) {
			throw new IllegalArgumentException("empty vector");
		}

		int n = vs[0].length;
		double[] mean = new double[n];

		for (int j = 0; j < n; j++) {
			for (int i = 0; i < k; i++) {
				mean[j] += vs[i][j];
			}

			mean[j] /= k;
		}

		return mean;
	}

	/**
	 * Returns {@code true} if the specified vector contains all zeros;
	 * {@code false} otherwise.
	 * 
	 * @param u the vector
	 * @return {@code true} if the specified vector contains all zeros;
	 *         {@code false} otherwise
	 */
	public static boolean isZero(double[] u) {
		for (int i = 0; i < u.length; i++) {
			if (Math.abs(u[i]) > Settings.EPS) {
				return false;
			}
		}

		return true;
	}

}
