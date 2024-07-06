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
package org.moeaframework.util.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

/**
 * Interface for computing the distance between two objects.  A distance measure must be:
 * <ul>
 *   <li>Non-negative - {@code compute(A, B) >= 0 for all A and B}
 *   <li>Zero identity - {@code compute(A, B) == 0 when A == B}
 *   <li>Finite - {@code compute(A, B) < Inf && compute(A, B) != NaN}
 * </ul>
 * Additionally, while not a requirement, distance measures should be symmetric, where
 * {@code compute(A, B) == compute(B, A)}.  Be mindful if caching distances that non-symmetric measures must store
 * both directions.
 * <p>
 * <strong>Implementation Note:</strong> While we use the Apache Commons Math clustering code behind the scenes, this
 * interface is distinct from Apache's {@link org.apache.commons.math3.ml.distance.DistanceMeasure} in order to
 * (1) support generic types; and (2) provide a functional interface to simplify use with lambdas and method references.
 * The static methods return a distance measure compatible with both.
 * 
 * @param <T> the type of object
 */
@FunctionalInterface
public interface DistanceMeasure<T> {

	/**
	 * Returns the distance between the two objects.
	 * 
	 * @param first the first object
	 * @param second the second object
	 * @return the distance
	 */
	public double compute(T first, T second);

	/**
	 * Returns the Euclidean distance measure, also known as the L<sub>2</sub>-norm.
	 * 
	 * @param <T> the type, which must be {@link Clusterable}
	 * @return the distance measure
	 */
	public static <T extends Clusterable> ApacheDistanceMeasure<T> euclideanDistance() {
		return new ApacheDistanceMeasure<T>(new EuclideanDistance());
	}
	
	/**
	 * Returns the Manhattan distance measure, also known as the L<sub>1</sub>-norm.
	 * 
	 * @param <T> the type, which must be {@link Clusterable}
	 * @return the distance measure
	 */
	public static <T extends Clusterable> ApacheDistanceMeasure<T> manhattanDistance() {
		return new ApacheDistanceMeasure<T>(new ManhattanDistance());
	}
	
	/**
	 * Returns the Chebyshev distance measure, also known as the L<sub>inf</sub>-norm.
	 * 
	 * @param <T> the type, which must be {@link Clusterable}
	 * @return the distance measure
	 */
	public static <T extends Clusterable> ApacheDistanceMeasure<T> chebyshevDistance() {
		return new ApacheDistanceMeasure<T>(new ChebyshevDistance());
	}
	
	/**
	 * Wraps one of the Apache Commons Math distance measures to be compatible with our generic distance measure.
	 * Computed distances are also cached / memoized.
	 * 
	 * @param <T> the type, which must be {@link Clusterable}
	 */
	static class ApacheDistanceMeasure<T extends Clusterable> implements DistanceMeasure<T>,
	org.apache.commons.math3.ml.distance.DistanceMeasure {
		
		private static final long serialVersionUID = 5873759932277642726L;

		private final CachedDistanceMeasure<double[]> cache;
		
		public ApacheDistanceMeasure(org.apache.commons.math3.ml.distance.DistanceMeasure measure) {
			super();
			cache = new CachedDistanceMeasure<>(measure::compute, true);
		}

		@Override
		public double compute(double[] first, double[] second) {
			return cache.compute(first, second);
		}

		@Override
		public double compute(T first, T second) {
			return compute(first.getPoint(), second.getPoint());
		}
		
	}

}
