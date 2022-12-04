/* Copyright 2018-2019 Ibrahim DEMIR
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

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;

/**
 * Abstract class of fundamental simulated annealing algorithm. While the iterations of evolving SA algorithms vary,
 * fundamental mechanics of SA algorithm stands on solidification of fluids. This includes the current, initial (max),
 * and stopping (min) temperatures.
 * 
 * @preview
 */
public abstract class AbstractSimulatedAnnealingAlgorithm extends AbstractAlgorithm implements Configurable {

	/**
	 * The stopping, or minimum, temperature at which point the algorithm stops.
	 */
	protected double stoppingTemperature;
	
	/**
	 * The initial, or maximum, temperature.
	 */
	protected double initialTemperature;
	
	/**
	 * The current temperature.
	 */
	protected double temperature;
	
	/**
	 * Constructs a new, abstract simulated annealing algorithm.
	 * 
	 * @param problem the problem to solve
	 * @param stoppingTemperature the stopping, or minimum, temperature
	 * @param initialTemperature the initial, or maximum temperature
	 */
	public AbstractSimulatedAnnealingAlgorithm(Problem problem, double stoppingTemperature, double initialTemperature) {
		super(problem);
		this.stoppingTemperature = stoppingTemperature;
		this.initialTemperature = initialTemperature;
	}

	/**
	 * Returns the current temperature.
	 * 
	 * @return the current temperature
	 */
	public double getTemperature() {
		return temperature;
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
		this.stoppingTemperature = stoppingTemperature;
	}

	/**
	 * Returns the initial, or maximum, temperature.
	 * 
	 * @return the initial temperature
	 */
	public double getInitialTemperature() {
		return initialTemperature;
	}
	
	/**
	 * Sets the initial, or maximum, temperature.  This value can only be set before initialization.
	 * 
	 * @param initialTemperature the initial temperature
	 */
	@Property(alias="tMax")
	public void setInitialTemperature(double initialTemperature) {
		assertNotInitialized();
		this.initialTemperature = initialTemperature;
	}
		
}
