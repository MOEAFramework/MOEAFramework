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
package org.moeaframework.core.operator.real;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.variable.RealVariable;

/**
 * Differential evolution (DE) variation operator.  Differential evolution works by randomly selecting three distinct
 * individuals from a population.  A difference vector is calculated between the first two individuals (shown as
 * the left-most arrow in the figure below), which is subsequently applied to the third individual (shown as the
 * right-most arrow in the figure below).
 * <p>
 * <img src="doc-files/DifferentialEvolution-1.png" alt="Example DifferentialEvolution operator distribution" />
 * <p>
 * The scaling factor parameter adjusts the magnitude of the difference vector, allowing the user to decrease or
 * increase the magnitude in relation to the actual difference between the individuals.  The crossover rate parameter 
 * controls the fraction of decision variables which are modified by the DE operator.  
 * <p>
 * References:
 * <ol>
 *   <li>Storn and Price. "Differential Evolution - A Simple and Efficient Heuristic for Global Optimization over
 *       Continuous Spaces." Journal of Global Optimization, 11:341-359, 1997.
 * </ol>
 */
@Prefix("de")
public class DifferentialEvolutionVariation implements Variation {

	/**
	 * The crossover rate.
	 */
	private double crossoverRate;

	/**
	 * The scaling factor or step size.
	 */
	private double scalingFactor;
	
	/**
	 * Constructs a differential evolution operator with default settings, including a crossover rate of {@code 0.1}
	 * and scaling factor of {@code 0.5}.
	 */
	public DifferentialEvolutionVariation() {
		this(0.1, 0.5);
	}

	/**
	 * Constructs a differential evolution operator with the specified crossover rate and scaling factor.
	 * 
	 * @param crossoverRate the crossover rate
	 * @param scalingFactor the scaling factor
	 */
	public DifferentialEvolutionVariation(double crossoverRate, double scalingFactor) {
		super();
		setCrossoverRate(crossoverRate);
		setScalingFactor(scalingFactor);
	}
	
	@Override
	public String getName() {
		return "de";
	}

	/**
	 * Returns the crossover rate of this differential evolution operator.
	 * 
	 * @return the crossover rate
	 */
	public double getCrossoverRate() {
		return crossoverRate;
	}
	
	/**
	 * Sets the crossover rate of this differential evolution operator.  The default value is {@code 0.1}.
	 * 
	 * @param crossoverRate the crossover rate
	 */
	@Property
	public void setCrossoverRate(double crossoverRate) {
		Validate.probability("crossoverRate", crossoverRate);
		this.crossoverRate = crossoverRate;
	}

	/**
	 * Returns the scaling factor of this differential evolution operator.
	 * 
	 * @return the scaling factor
	 */
	public double getScalingFactor() {
		return scalingFactor;
	}
	
	/**
	 * Sets the scaling factor of this differential evolution operator.  The default value is {@code 0.5}.
	 * 
	 * @param scalingFactor the scaling factor
	 */
	@Property("stepSize")
	public void setScalingFactor(double scalingFactor) {
		Validate.greaterThanZero("scalingFactor", scalingFactor);
		this.scalingFactor = scalingFactor;
	}

	@Override
	public int getArity() {
		return 4;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();

		int jrand = PRNG.nextInt(result.getNumberOfVariables());

		for (int j = 0; j < result.getNumberOfVariables(); j++) {
			if ((PRNG.nextDouble() <= crossoverRate) || (j == jrand)) {
				RealVariable v0 = (RealVariable)result.getVariable(j);
				RealVariable v1 = (RealVariable)parents[1].getVariable(j);
				RealVariable v2 = (RealVariable)parents[2].getVariable(j);
				RealVariable v3 = (RealVariable)parents[3].getVariable(j);

				double y = v3.getValue() + scalingFactor * (v1.getValue() - v2.getValue());

				if (y < v0.getLowerBound()) {
					y = v0.getLowerBound();
				}

				if (y > v0.getUpperBound()) {
					y = v0.getUpperBound();
				}

				v0.setValue(y);
			}
		}

		return new Solution[] { result };
	}

}
