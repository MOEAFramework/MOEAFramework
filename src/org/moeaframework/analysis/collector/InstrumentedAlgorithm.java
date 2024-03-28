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

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.PeriodicAction;

/**
 * Decorates an algorithm to periodically collect information about its runtime behavior.  The {@code NFE} field is
 * automatically recorded by this class.
 */
public class InstrumentedAlgorithm extends PeriodicAction {
	
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
	public InstrumentedAlgorithm(Algorithm algorithm, int frequency) {
		this(algorithm, frequency, FrequencyType.EVALUATIONS);
	}
	
	/**
	 * Decorates the specified algorithm to periodically collect information about its runtime behavior.
	 * 
	 * @param algorithm the algorithm to decorate
	 * @param frequency the frequency that data is collected
	 * @param frequencyType if frequency is defined by EVALUATIONS or STEPS
	 */
	public InstrumentedAlgorithm(Algorithm algorithm, int frequency, FrequencyType frequencyType) {
		super(algorithm, frequency, frequencyType);
		
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
	public void doAction() {
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
