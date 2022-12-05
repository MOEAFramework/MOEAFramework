/* Copyright 2009-2022 David Hadka
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

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.util.TypedProperties;

/**
 * Abstract class for building single-objective evolutionary algorithms.  These use an aggregating
 * or scalarizing function that converts multiple objective values into a single fitness value.
 */
public abstract class SingleObjectiveEvolutionaryAlgorithm extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The aggregate objective comparator.
	 */
	protected AggregateObjectiveComparator comparator;

	/**
	 * Constructs a new single-objective algorithm.
	 * 
	 * @param problem the problem to solve
	 * @param initialPopulationSize the initial population size
	 * @param population the population
	 * @param archive the archive storing the non-dominated solutions
	 * @param comparator the aggregate objective comparator
	 * @param initialization the initialization method
	 * @param variation the variation operator
	 */
	public SingleObjectiveEvolutionaryAlgorithm(Problem problem, int initialPopulationSize, Population population,
			NondominatedPopulation archive, AggregateObjectiveComparator comparator, Initialization initialization,
			Variation variation) {
		super(problem, initialPopulationSize, population, archive, initialization, variation);
		setComparator(comparator);
	}
	
	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation(comparator);
		result.addAll(getPopulation());
		return result;
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
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}

	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("method") || properties.contains("weights")) {
			String method = properties.getString("method", "linear");
			double[] weights = properties.getDoubleArray("weights", new double[] { 1.0 });

			if (method.equalsIgnoreCase("linear")) {
				setComparator(new LinearDominanceComparator(weights));
			} else if (method.equalsIgnoreCase("min-max")) {
				setComparator(new MinMaxDominanceComparator(weights));
			} else {
				throw new ConfigurationException("unrecognized weighting method: " + method);
			}
		}
		
		super.applyConfiguration(properties);
		
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		
		if (comparator instanceof LinearDominanceComparator) {
			properties.setString("method", "linear");
		} else if (comparator instanceof MinMaxDominanceComparator) {
			properties.setString("method", "min-max");
		}
		
		properties.setDoubleArray("weights", comparator.getWeights());
		
		return properties;
	}

}
