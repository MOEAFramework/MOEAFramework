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
package org.moeaframework.analysis.collector;

import org.junit.Assert;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Population;

public class PopulationCollectorTest extends AbstractCollectorTest<PopulationCollector> {
	
	@Override
	public void validate(Observation observation) {
		Population population = PopulationCollector.getPopulation(observation);
		
		Assert.assertNotNull(population);
		Assert.assertTrue(population.size() > 0);
	}
	
	@Override
	public PopulationCollector createInstance() {
		return new PopulationCollector();
	}
	
	@Override
	public boolean shouldAttach(Algorithm algorithm) {
		return algorithm instanceof EvolutionaryAlgorithm;
	}

}
