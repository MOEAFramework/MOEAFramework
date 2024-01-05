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
package org.moeaframework.algorithm.single;

import java.util.Comparator;

import org.moeaframework.algorithm.sa.AbstractSimulatedAnnealingAlgorithm;
import org.moeaframework.algorithm.sa.CoolingSchedule;
import org.moeaframework.algorithm.sa.GeometricCoolingSchedule;
import org.moeaframework.algorithm.sa.TemperatureBasedTerminationCondition;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of single-objective simulated annealing.
 * <p>
 * References:
 * <ol>
 *   <li> Kirkpatrick, S., Gelatt Jr, C. D., and Vecchi, M. P. (1983). "Optimization by Simulated Annealing". Science.
 *        220 (4598): 671–680.
 * </ol>
 */
public class SimulatedAnnealing extends AbstractSimulatedAnnealingAlgorithm {

	/**
	 * The aggregate objective comparator.
	 */
	protected AggregateObjectiveComparator comparator;

	/**
	 * The number of iterations at each temperature
	 */
	protected int numberOfIterationsPerTemperature;
	
	/**
	 * Creates a new instance of the Simulated Annealing (SA) algorithm with default settings.
	 * 
	 * @param problem the problem to solve
	 */
	public SimulatedAnnealing(Problem problem) {
		this(problem,
				1.0,
				new GeometricCoolingSchedule(0.8),
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getMutation(problem),
				null,
				500);
	}

	/**
	 * Creates a new instance of the Simulated Annealing (SA) algorithm.
	 * 
	 * @param problem the problem to solve
	 * @param initialTemperature the initial temperature
	 * @param coolingSchedule the cooling schedule
	 * @param initialization the method for initializing solutions
	 * @param mutation the mutation operator
	 * @param terminationCondition the termination condition
	 * @param numberOfIterationsPerTemperature the number of iterations at each temperature
	 */
	public SimulatedAnnealing(Problem problem, double initialTemperature, CoolingSchedule coolingSchedule,
			Initialization initialization, Mutation mutation, TemperatureBasedTerminationCondition terminationCondition,
			int numberOfIterationsPerTemperature) {
		super(problem, initialTemperature, coolingSchedule, initialization, mutation);
		setTerminationCondition(terminationCondition);
		setNumberOfIterationsPerTemperature(numberOfIterationsPerTemperature);
		setComparator(new LinearDominanceComparator());
	}

	/**
	 * Returns the number of iterations performed at each temperature.  Note that all iterations for the same
	 * temperature are evaluated in a single call to {@link #step()}.
	 * 
	 * @return the number of iterations
	 */
	public int getNumberOfIterationsPerTemperature() {
		return numberOfIterationsPerTemperature;
	}

	/**
	 * Sets the number of iterations performed at each temperature.
	 * 
	 * @param numberOfIterationsPerTemperature the number of iterations
	 */
	@Property(alias="iter")
	public void setNumberOfIterationsPerTemperature(int numberOfIterationsPerTemperature) {
		Validate.greaterThanZero("numberOfIterationsPerTemperature", numberOfIterationsPerTemperature);
		this.numberOfIterationsPerTemperature = numberOfIterationsPerTemperature;
	}
	
	/**
	 * Returns the aggregate objective comparator that scalarizes multiple objectives into a single fitness value.
	 * 
	 * @return the aggregate objective comparator
	 */
	public AggregateObjectiveComparator getComparator() {
		return comparator;
	}

	/**
	 * Sets the aggregate objective comparator that scalarizes multiple objectives into a single fitness value.
	 * 
	 * @param comparator the aggregate objective comparator
	 */
	public void setComparator(AggregateObjectiveComparator comparator) {
		Validate.notNull("comparator", comparator);
		this.comparator = comparator;
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		currentPoint = initialization.initialize(1)[0];
		evaluate(currentPoint);
		
		if (archive != null) {
			archive.add(currentPoint);
		}
	}

	@Override
	protected void iterate() {
		for (int i = 0; i < numberOfIterationsPerTemperature; i++) {
			Solution newPoint = mutation.mutate(currentPoint);
			evaluate(newPoint);
			
			int comparisonResult = ((Comparator<Solution>)comparator).compare(currentPoint, newPoint);
			
			if (comparisonResult < 0) {
				double currentPointFitness = comparator.calculateFitness(currentPoint);
				double newPointFitness = comparator.calculateFitness(newPoint);
				double probability = Math.exp(-(newPointFitness - currentPointFitness) / temperature);
				
				if (PRNG.nextDouble() < probability) {
					updateCurrentPoint(newPoint);
				}
			} else {
				updateCurrentPoint(newPoint);
			}
		}
	}
	
	/**
	 * Updates the current point and, if configured, adds the point to the archive.
	 * 
	 * @param newPoint the new point
	 */
	protected void updateCurrentPoint(Solution newPoint) {
		currentPoint = newPoint;
		
		if (archive != null) {
			archive.add(newPoint);
		}
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		AggregateObjectiveComparator comparator = AggregateObjectiveComparator.fromConfiguration(properties);
		
		if (comparator != null) {
			setComparator(comparator);
		}
		
		super.applyConfiguration(properties);
		
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties configuration = super.getConfiguration();
		configuration.addAll(AggregateObjectiveComparator.toConfiguration(comparator));
		return configuration;
	}

}
