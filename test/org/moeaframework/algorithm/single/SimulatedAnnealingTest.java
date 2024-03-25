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
package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.util.TypedProperties;

public class SimulatedAnnealingTest extends AbstractSingleObjectiveAlgorithmTest<SimulatedAnnealing> {
	
	public SimulatedAnnealing createInstance(Problem problem) {
		return new SimulatedAnnealing(problem);
	}

	// Overridden since SimulatedAnnealing does not extend from SingleObjectiveEvolutionaryAlgorithm
	@Test
	@Override
	public void testConfiguration() {
		SimulatedAnnealing algorithm = createInstance(new MockRealProblem());
		
		TypedProperties properties = algorithm.getConfiguration();
		
		Assert.assertEquals("linear", properties.getString("method"));
		Assert.assertTrue(algorithm.getComparator() instanceof LinearDominanceComparator);
		
		properties.setString("method", "min-max");
		algorithm.applyConfiguration(properties);
		Assert.assertTrue(algorithm.getComparator() instanceof MinMaxDominanceComparator);
	}

}
