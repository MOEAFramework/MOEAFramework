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
package org.moeaframework.algorithm.sa;

import org.moeaframework.algorithm.AlgorithmInitializationException;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;

/**
 * Terminates a simulated annealing algorithm when the temperature drops below a stopping, or minimum, temperature.
 */
public class TemperatureBasedTerminationCondition implements TerminationCondition, Configurable {
	
	/**
	 * The stopping temperature.
	 */
	private double stoppingTemperature;
	
	/**
	 * Constructs a new termination condition based on the simulated annealing temperature.
	 */
	public TemperatureBasedTerminationCondition() {
		this(0.0000001);
	}
	
	/**
	 * Constructs a new termination condition based on the simulated annealing temperature.
	 * 
	 * @param stoppingTemperature the stopping temperature
	 */
	public TemperatureBasedTerminationCondition(double stoppingTemperature) {
		super();
		setStoppingTemperature(stoppingTemperature);
	}
	
	/**
	 * Returns the stopping, or minimum, temperature at which point the algorithm stops.
	 * 
	 * @return the stopping temperature
	 */
	public double getStoppingTemperature() {
		return stoppingTemperature;
	}
	
	/**
	 * Sets the stopping, or minimum, temperature at which point the algorithm stops.
	 * 
	 * @param stoppingTemperature the stopping temperature
	 */
	@Property(alias="tMin")
	public void setStoppingTemperature(double stoppingTemperature) {
		Validate.greaterThanOrEqual("stoppingTemperature", 0.0, stoppingTemperature);
		this.stoppingTemperature = stoppingTemperature;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		if (!(algorithm instanceof AbstractSimulatedAnnealingAlgorithm)) {
			throw new AlgorithmInitializationException(algorithm, getClass().getSimpleName() +
					" can only be used with " + AbstractSimulatedAnnealingAlgorithm.class.getSimpleName());
		}
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {	
		return ((AbstractSimulatedAnnealingAlgorithm)algorithm).getTemperature() <= stoppingTemperature;
	}

}
