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
package org.moeaframework.analysis.runtime;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.indicator.Indicator;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;

/**
 * Collects performance indicator values from an {@link Algorithm}.
 */
public class IndicatorCollector implements Collector {

	/**
	 * The performance indicator used by this collector.
	 */
	private final Indicator indicator;

	/**
	 * The algorithm instance used by this collector; or {@code null} if this collector has not yet been attached.
	 */
	private final Algorithm algorithm;
	
	/**
	 * The &epsilon;-box dominance archive used to prune the approximation set to a desired resolution; or {@code null}
	 * if no such pruning is used.
	 */
	private final EpsilonBoxDominanceArchive archive;
	
	/**
	 * Constructs an unattached collector for recording performance indicator values from an algorithm.
	 * 
	 * @param indicator the performance indicator used by this collector
	 */
	public IndicatorCollector(Indicator indicator) {
		this(indicator, null, null);
	}
	
	/**
	 * Constructs an unattached collector for recording performance indicator values from an algorithm, with an
	 * &epsilon;-box dominance archive to prune the approximation set to a desired resolution.
	 * 
	 * @param indicator the performance indicator used by this collector
	 * @param archive the &epsilon;-box dominance archive used to prune the approximation set to a desired resolution;
	 *        or {@code null} if no such pruning is used
	 */
	public IndicatorCollector(Indicator indicator, EpsilonBoxDominanceArchive archive) {
		this(indicator, archive, null);
	}

	/**
	 * Constructs a collector for recording performance indicator values from the specified algorithm.
	 * 
	 * @param indicator the performance indicator used by this collector
	 * @param archive the &epsilon;-box dominance archive used to prune the approximation set to a desired resolution;
	 *        or {@code null} if no such pruning is used
	 * @param algorithm the algorithm this collector records data from
	 */
	public IndicatorCollector(Indicator indicator, EpsilonBoxDominanceArchive archive, Algorithm algorithm) {
		super();
		this.indicator = indicator;
		this.archive = archive;
		this.algorithm = algorithm;
	}

	@Override
	public void collect(ResultEntry result) {
		if (archive == null) {
			result.getProperties().setDouble(indicator.getName(), indicator.evaluate(algorithm.getResult()));
		} else {
			archive.clear();
			archive.addAll(algorithm.getResult());
			
			result.getProperties().setDouble(indicator.getName(), indicator.evaluate(archive));
		}
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(Algorithm.class).and(
				AttachPoint.not(AttachPoint.isNestedIn(Algorithm.class)));
	}

	@Override
	public Collector attach(Object object) {
		return new IndicatorCollector(indicator, archive, (Algorithm)object);
	}
	
	/**
	 * Reads the indicator value from the result.
	 * 
	 * @param result the result
	 * @param metric the name of the indicator or metric
	 * @return the indicator value
	 */
	public static double getIndicatorValue(ResultEntry result, String metric) {
		return result.getProperties().getDouble(metric);
	}
	
	/**
	 * Reads the indicator value from the result.
	 * 
	 * @param result the result
	 * @param type the class type of the indicator
	 * @param <T> the type of indicator
	 * @return the indicator value
	 */
	public static <T extends Indicator> double getIndicatorValue(ResultEntry result, Class<T> type) {
		return getIndicatorValue(result, type.getSimpleName());
	}

}
