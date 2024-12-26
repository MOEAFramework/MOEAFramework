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
package org.moeaframework.util.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Wraps one of the Apache Commons Math distance measures to be compatible with our generic distance measure.
 * Computed distances are also cached / memoized.
 * 
 * @param <T> the type, which must be {@link Clusterable}
 */
class ApacheDistanceMeasure<T extends Clusterable> implements DistanceMeasure<T>,
org.apache.commons.math3.ml.distance.DistanceMeasure {
	
	private static final long serialVersionUID = 5873759932277642726L;

	private final CachedDistanceMeasure<double[]> cache;
	
	/**
	 * Constructs a new distance measure implemented by Apache Commons Math.
	 * 
	 * @param measure the underlying distance measure
	 */
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
