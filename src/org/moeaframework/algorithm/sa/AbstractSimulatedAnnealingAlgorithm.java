/* Copyright 2018-2019 Ibrahim DEMIR, 2023 David Hadka
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.algorithm.AlgorithmInitializationException;
import org.moeaframework.algorithm.AlgorithmTerminationException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.operator.Mutation;

/**
 * Abstract class of fundamental simulated annealing algorithm. While the iterations of evolving SA algorithms vary,
 * fundamental mechanics of SA algorithm stands on solidification of fluids. This includes the current, initial (max),
 * and stopping (min) temperatures.
 */
public abstract class AbstractSimulatedAnnealingAlgorithm extends AbstractAlgorithm implements Configurable {

	/**
	 * The initial, or maximum, temperature.
	 */
	protected double initialTemperature;
	
	/**
	 * The cooling (or reduction) schedule that determines how the temperature decreases over time.
	 */
	protected CoolingSchedule coolingSchedule;
	
	/**
	 * Self-terminates the execution of this algorithm when the temperature drops below a minimum threshold.
	 * If {@code null}, will not self-terminate and instead runs until some other termination condition is
	 * reached.
	 */
	protected TemperatureBasedTerminationCondition terminationCondition;
	
	/**
	 * The initialization operator.
	 */
	protected Initialization initialization;
	
	/**
	 * The mutation operator used to generate neighbors of the current point.
	 */
	protected Mutation mutation;
	
	/**
	 * The current temperature.
	 */
	protected double temperature;
	
	/**
	 * The current point.
	 */
	protected Solution currentPoint;
	
	/**
	 * The archive storing the non-dominated solutions.
	 */
	protected NondominatedPopulation archive;

	/**
	 * Constructs a new, abstract simulated annealing algorithm.
	 * 
	 * @param problem the problem to solve
	 * @param initialTemperature the initial, or maximum, temperature
	 * @param coolingSchedule the cooling schedule that determines how the temperature decreases over time
	 * @param initialization the initialization method
	 * @param mutation the mutation operator used to generate neighbors of the current point
	 */
	public AbstractSimulatedAnnealingAlgorithm(Problem problem, double initialTemperature,
			CoolingSchedule coolingSchedule, Initialization initialization, Mutation mutation) {
		super(problem);
		setInitialTemperature(initialTemperature);
		setCoolingSchedule(coolingSchedule);
		setMutation(mutation);
		setInitialization(initialization);
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

	/**
	 * Returns the cooling (or reduction) schedule that determines how the temperature decreases over time.
	 * 
	 * @return the cooling schedule
	 */
	public CoolingSchedule getCoolingSchedule() {
		return coolingSchedule;
	}
	
	/**
	 * Sets the cooling (or reduction) schedule that determines how the temperature decreases over time.  This can only
	 * be set before initializing the algorithm.
	 * 
	 * @param coolingSchedule the cooling schedule
	 */
	protected void setCoolingSchedule(CoolingSchedule coolingSchedule) {
		assertNotInitialized();
		Validate.notNull("coolingSchedule", coolingSchedule);
		this.coolingSchedule = coolingSchedule;
	}
	
	/**
	 * Returns the temperature-based termination condition.
	 * 
	 * @return the temperature-based termination condition, or {@code null} if not set
	 */
	public TemperatureBasedTerminationCondition getTerminationCondition() {
		return terminationCondition;
	}
	
	/**
	 * Sets the temperature-based termination condition.
	 * 
	 * @param terminationCondition the temperature-based termination condition, or {@code null} if none is used
	 */
	protected void setTerminationCondition(TemperatureBasedTerminationCondition terminationCondition) {
		this.terminationCondition = terminationCondition;
	}
	
	/**
	 * Returns the initialization method for generating solutions in the initial population.
	 * 
	 * @return the initialization method
	 */
	public Initialization getInitialization() {
		return initialization;
	}

	/**
	 * Sets the initialization method for generating solutions in the initial population.  This can only
	 * be set before initializing the algorithm.
	 * 
	 * @param initialization the initialization method
	 */
	public void setInitialization(Initialization initialization) {
		assertNotInitialized();
		Validate.notNull("initialization", initialization);
		this.initialization = initialization;
	}
	
	/**
	 * Returns the mutation operator.
	 * 
	 * @return the mutation operator
	 */
	public Mutation getMutation() {
		return mutation;
	}

	/**
	 * Sets the mutation operator.
	 * 
	 * @param mutation the mutation operator
	 */
	@Property("operator")
	public void setMutation(Mutation mutation) {
		Validate.notNull("mutation", mutation);
		this.mutation = mutation;
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
	 * Returns the current point.
	 * 
	 * @return the current point
	 */
	public Solution getCurrentPoint() {
		return currentPoint;
	}
	
	/**
	 * Returns the archive used by this algorithm.
	 * 
	 * @return the archive used by this algorithm
	 */
	protected NondominatedPopulation getArchive() {
		return archive;
	}
	
	/**
	 * Sets the archive used by this algorithm.  This value can not be set after initialization.
	 * 
	 * @param archive the archive
	 */
	protected void setArchive(NondominatedPopulation archive) {
		assertNotInitialized();
		this.archive = archive;
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		if (mutation == null) {
			throw new AlgorithmInitializationException(this,
					"no mutation operator set, must set one by calling setMutation(...)");
		}
		
		temperature = initialTemperature;
		
		if (terminationCondition != null) {
			terminationCondition.initialize(this);
		}
	}
	
	@Override
	public void step() {
		if (isTerminated()) {
			throw new AlgorithmTerminationException(this, "algorithm already terminated");
		} else if (!isInitialized()) {
			initialize();
		} else {
			if (terminationCondition != null && terminationCondition.shouldTerminate(this)) {
				terminate();
				return;
			}
			
			iterate();
			temperature = coolingSchedule.nextTemperature(temperature);
		}
	}
	
	@Override
	public NondominatedPopulation getResult() {
		if (archive == null) {
			NondominatedPopulation result = new NondominatedPopulation();
			
			if (currentPoint != null) {
				result.add(currentPoint);
			}
			
			return result;
		} else {
			return archive;
		}
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeDouble(temperature);
		stream.writeObject(currentPoint);
		
		if (archive != null) {
			archive.saveState(stream);
		}
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		temperature = stream.readDouble();
		currentPoint = (Solution)stream.readObject();
		
		if (this.archive != null) {
			archive.loadState(stream);
		}
	}
	
}
