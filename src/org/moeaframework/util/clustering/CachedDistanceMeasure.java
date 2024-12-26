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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Wraps a {@link DistanceMeasure} to cache or memoize the calculations in memory.  This is similar to using a
 * precomputed distance matrix, except the distance calculations are performed lazily.
 * 
 * @param <T> the type of object being stored
 */
public class CachedDistanceMeasure<T> implements DistanceMeasure<T> {
	
	private final DistanceMeasure<T> distanceMeasure;
	
	private final boolean isSymmetric;
	
	private final Map<Pair<T, T>, Double> distanceCache;

	/**
	 * Wraps the provided distance measure with a cached version.
	 * 
	 * @param distanceMeasure the uncached distance measure
	 * @param isSymmetric {@code true} if the distance measure is symmetric; see {@link CachedDistanceMeasure} for the
	 *        definition of symmetry.
	 */
	public CachedDistanceMeasure(DistanceMeasure<T> distanceMeasure, boolean isSymmetric) {
		super();
		this.distanceMeasure = distanceMeasure;
		this.isSymmetric = isSymmetric;
		this.distanceCache = new HashMap<>();
	}

	/**
	 * Constructs a key for the objects.  If {@link #isSymmetric} is {@code true}, then the generated key will be
	 * ordered to support symmetry.
	 * 
	 * @param first the first object
	 * @param second the second object
	 * @return the key
	 */
	private final Pair<T, T> getKey(T first, T second) {
		return isSymmetric && first.hashCode() > second.hashCode() ? Pair.of(second, first) : Pair.of(first, second);
	}

	@Override
	public double compute(T first, T second) {
		Pair<T, T> key = getKey(first, second);
		Double result = distanceCache.get(key);
		
		if (result == null) {
			result = distanceMeasure.compute(first, second);
			distanceCache.put(key, result);
		}
		
		return result;
	}

}