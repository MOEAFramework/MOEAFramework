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
package org.moeaframework.analysis.collector;

import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.AdaptiveMultimethodVariation;

/**
 * Collects the individual operator probabilities from {@link AdaptiveMultimethodVariation}.
 */
public class AdaptiveMultimethodVariationCollector implements Collector {

	/**
	 * The adaptive multimethod variation instance used by this collector; or {@code null} if this collector has not
	 * yet been attached.
	 */
	private final AdaptiveMultimethodVariation variation;

	/**
	 * Constructs an unattached collector for recording the individual operator probabilities from
	 * {@code AdaptiveMultimethodVariation}.
	 */
	public AdaptiveMultimethodVariationCollector() {
		this(null);
	}
	
	/**
	 * Constructs a collector for recording the individual operator probabilities from the specified
	 * {@code AdaptiveMultimethodVariation}.
	 * 
	 * @param variation the {@code AdaptiveMultimethodVariation} instance this collector records data from
	 */
	public AdaptiveMultimethodVariationCollector(AdaptiveMultimethodVariation variation) {
		super();
		this.variation = variation;
	}

	@Override
	public void collect(Observation observation) {
		for (int i = 0; i < variation.getNumberOfOperators(); i++) {
			Variation operator = variation.getOperator(i);
			observation.set(operator.getName(), variation.getOperatorProbability(i));
		}
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(AdaptiveMultimethodVariation.class);
	}

	@Override
	public Collector attach(Object object) {
		return new AdaptiveMultimethodVariationCollector((AdaptiveMultimethodVariation)object);
	}
	
	/**
	 * Reads the operator probability from the observation.
	 * 
	 * @param observation the observation
	 * @param name the name of the operator
	 * @return the operator probability
	 */
	public static double getOperatorProbability(Observation observation, String name) {
		return (Double)observation.get(name);
	}
	
	/**
	 * Reads the operator probability from the observation.
	 * 
	 * @param observation the observation
	 * @param operator the operator
	 * @return the operator probability
	 */
	public static double getOperatorProbability(Observation observation, Variation operator) {
		return (Double)observation.get(operator.getName());
	}

}
