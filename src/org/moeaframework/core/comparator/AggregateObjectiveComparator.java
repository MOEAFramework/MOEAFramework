/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.comparator;

import java.util.Comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.configuration.ConfigurationException;

/**
 * Compares solutions based on a computed aggregate value from the objective values.  This comparator and its
 * implementations are intended for use with single-objective algorithms.
 */
public interface AggregateObjectiveComparator extends DominanceComparator, Comparator<Solution> {
	
	/**
	 * Returns the weights used by this aggregate function.
	 * 
	 * @return the weights
	 */
	public double[] getWeights();
	
	/**
	 * Calculates the aggregate value of the solution using this aggregate function.
	 * 
	 * @param solution the solution
	 * @return the aggregate value (smaller is better)
	 */
	public double calculate(Solution solution);
	
	/**
	 * Creates an aggregate objective comparator from the given configuration.
	 * 
	 * @param properties the configuration
	 * @return the comparator, or {@code null} if one is not explicitly configured
	 */
	public static AggregateObjectiveComparator fromConfiguration(TypedProperties properties) {
		if (properties.contains("method") || properties.contains("weights")) {
			String method = properties.getString("method", "linear");
			double[] weights = properties.getDoubleArray("weights", new double[] { 1.0 });

			if (method.equalsIgnoreCase("linear")) {
				return new LinearDominanceComparator(weights);
			} else if (method.equalsIgnoreCase("min-max")) {
				return new MinMaxDominanceComparator(weights);
			} else if (method.equalsIgnoreCase("angle")) {
				double q = properties.getDouble("angleScalingFactor", 100.0);
				return new VectorAngleDistanceScalingComparator(weights, q);
			} else {
				throw new ConfigurationException("Unsupported weighting method: " + method);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the configuration for the given aggregate objective comparator.
	 * 
	 * @param comparator the comparator
	 * @return the configuration
	 */
	public static TypedProperties toConfiguration(AggregateObjectiveComparator comparator) {
		TypedProperties properties = new TypedProperties();
		
		if (comparator instanceof LinearDominanceComparator) {
			properties.setString("method", "linear");
		} else if (comparator instanceof MinMaxDominanceComparator) {
			properties.setString("method", "min-max");
		} else if (comparator instanceof VectorAngleDistanceScalingComparator vadsc) {
			properties.setString("method", "angle");
			properties.setDouble("angleScalingFactor", vadsc.getAngleScalingFactor());
		}
		
		properties.setDoubleArray("weights", comparator.getWeights());
		
		return properties;
	}

}
