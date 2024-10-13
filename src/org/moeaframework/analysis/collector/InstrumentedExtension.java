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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.extension.FrequencyType;
import org.moeaframework.algorithm.extension.PeriodicExtension;
import org.moeaframework.core.Stateful;

/**
 * Decorates an algorithm to periodically collect information about its runtime behavior.  The {@code NFE} field is
 * automatically recorded by this class.
 */
public class InstrumentedExtension extends PeriodicExtension implements Stateful {
	
	/**
	 * The observations recorded from this algorithm.
	 */
	private Observations observations;
	
	/**
	 * The collectors responsible for recording the necessary information.
	 */
	private List<Collector> collectors;

	/**
	 * Decorates the specified algorithm to periodically collect information about its runtime behavior.  Frequency is
	 * given in number of evaluations.
	 * 
	 * @param algorithm the algorithm to decorate
	 * @param frequency the frequency, in evaluations, that data is collected
	 */
	public InstrumentedExtension(int frequency) {
		this(frequency, FrequencyType.EVALUATIONS);
	}
	
	/**
	 * Decorates the specified algorithm to periodically collect information about its runtime behavior.
	 * 
	 * @param algorithm the algorithm to decorate
	 * @param frequency the frequency that data is collected
	 * @param frequencyType if frequency is defined by EVALUATIONS or STEPS
	 */
	public InstrumentedExtension(int frequency, FrequencyType frequencyType) {
		super(frequency, frequencyType);
		
		observations = new Observations();
		collectors = new ArrayList<Collector>();
	}
	
	/**
	 * Adds a collector to this instrumented algorithm.  The collector should have already been attached to the
	 * algorithm.
	 * 
	 * @param collector the collector
	 */
	public void addCollector(Collector collector) {
		collectors.add(collector);
	}
	
	/**
	 * Returns the observations collected from this algorithm.
	 * 
	 * @return the observations
	 */
	public Observations getObservations() {
		return observations;
	}

	@Override
	public void doAction(Algorithm algorithm) {
		Observation observation = new Observation(algorithm.getNumberOfEvaluations());
		
		for (Collector collector : collectors) {
			collector.collect(observation);
		}
		
		observations.add(observation);
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeObject(observations);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		observations = (Observations)stream.readObject();
	}
	
}
