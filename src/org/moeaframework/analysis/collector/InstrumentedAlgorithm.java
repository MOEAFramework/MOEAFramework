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

import org.moeaframework.algorithm.extension.AlgorithmWrapper;
import org.moeaframework.algorithm.extension.FrequencyType;
import org.moeaframework.core.Algorithm;

/**
 * Wraps an algorithm to indicate it is instrumented to collect runtime data.
 */
public class InstrumentedAlgorithm<T extends Algorithm> extends AlgorithmWrapper<T> {
	
	/**
	 * Wraps the given algorithm to create an instrumented version.
	 * 
	 * @param algorithm the algorithm
	 */
	public InstrumentedAlgorithm(T algorithm) {
		super(algorithm);
	}
	
	public InstrumentedExtension registerExtension(int frequency, FrequencyType frequencyType) {
		InstrumentedExtension extension = getExtensions().get(InstrumentedExtension.class);
		
		if (extension == null) {
			extension = new InstrumentedExtension(frequency, frequencyType);
			getExtensions().add(extension);
		}
		
		return extension;
	}
	
	/**
	 * Adds a collector to this instrumented algorithm.  The collector should have already been attached to the
	 * algorithm.
	 * 
	 * @param collector the collector
	 */
	public void addCollector(Collector collector) {
		getExtensions().get(InstrumentedExtension.class).addCollector(collector);
	}
	
	/**
	 * Returns the observations collected from this algorithm.
	 * 
	 * @return the observations
	 */
	public Observations getObservations() {
		return getExtensions().get(InstrumentedExtension.class).getObservations();
	}
	
}
