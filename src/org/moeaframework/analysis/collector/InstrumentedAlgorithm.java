/* Copyright 2009-2023 David Hadka
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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.PeriodicAction;
import org.moeaframework.core.Algorithm;

/**
 * Decorates an algorithm to periodically collect information about its runtime
 * behavior.  The {@code NFE} field is automatically recorded by this class.
 */
public class InstrumentedAlgorithm extends PeriodicAction {
	
	/**
	 * The observations recorded from this algorithm.
	 */
	private final Observations observations;
	
	/**
	 * The collectors responsible for recording the necessary information.
	 */
	private final List<Collector> collectors;

	/**
	 * Decorates the specified algorithm to periodically collect information
	 * about its runtime behavior.  Frequency is given in number of evaluations.
	 * 
	 * @param algorithm the algorithm to decorate
	 * @param frequency the frequency, in evaluations, that data is collected
	 */
	public InstrumentedAlgorithm(Algorithm algorithm, int frequency) {
		this(algorithm, frequency, FrequencyType.EVALUATIONS);
	}
	
	/**
	 * Decorates the specified algorithm to periodically collect information
	 * about its runtime behavior.
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
	 * Adds a collector to this instrumented algorithm.  The collector should
	 * have already been attached to the algorithm.
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
	
	/**
	 * Proxy for serializing and deserializing the state of an
	 * {@code InstrumentedAlgorithm} instance. This proxy supports saving
	 * the underlying algorithm state and the observations.
	 */
	private static class InstrumentedAlgorithmState implements Serializable {

		private static final long serialVersionUID = -313598408729472790L;

		/**
		 * The state of the underlying algorithm.
		 */
		private final Serializable algorithmState;
		
		/**
		 * The {@code observations} from the {@code InstrumentedAlgorithm} instance.
		 */
		private final Observations observations;

		/**
		 * Constructs a proxy for storing the state of an {@code InstrumentedAlgorithm} instance.
		 * 
		 * @param algorithmState the state of the underlying algorithm
		 * @param observations the {@code observations} from the {@code InstrumentedAlgorithm} instance
		 */
		public InstrumentedAlgorithmState(Serializable algorithmState, Observations observations) {
			super();
			this.algorithmState = algorithmState;
			this.observations = observations;
		}

		/**
		 * Returns the underlying algorithm state.
		 * 
		 * @return the underlying algorithm state
		 */
		public Serializable getAlgorithmState() {
			return algorithmState;
		}

		/**
		 * Returns the {@code observations} from the {@code InstrumentedAlgorithm} instance.
		 * 
		 * @return the {@code observations} from the {@code InstrumentedAlgorithm} instance
		 */
		public Observations getObservations() {
			return observations;
		}
		
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		return new InstrumentedAlgorithmState(super.getState(), observations);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		InstrumentedAlgorithmState state = (InstrumentedAlgorithmState)objState;
		
		super.setState(state.getAlgorithmState());
		
		//copy the stored observations content
		Observations storedObservations = state.getObservations();
		
		for (Observation observation : storedObservations) {
			observations.add(observation);
		}
	}
	
}
