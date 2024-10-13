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
package org.moeaframework.examples.algorithm;

import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.operator.Variation;

/**
 * A configurable version of the "random walker" algorithm, providing setters and properties for the population size
 * and variation operator.
 */
public class ConfigurableRandomWalker extends RandomWalker {

	public ConfigurableRandomWalker(Problem problem) {
		super(problem);
	}

	@Override
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}
	
}