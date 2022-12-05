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
package org.moeaframework.core.spi;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.util.TypedProperties;

public class TestAlgorithmProvider extends AlgorithmProvider {

	@Override
	public Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		if (name.equalsIgnoreCase("testAlgorithm")) {
			return new AbstractEvolutionaryAlgorithm(
					problem,
					100,
					new Population(),
					null,
					new RandomInitialization(problem),
					OperatorFactory.getInstance().getVariation(problem)) {

				@Override
				protected void iterate() {
					// do nothing
				}
				
			};
		} else {
			return null;
		}
	}
	
};
