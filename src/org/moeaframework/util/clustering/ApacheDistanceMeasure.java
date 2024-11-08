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
