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
package org.moeaframework.core.termination;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.mock.MockAlgorithm;
import org.moeaframework.mock.MockSolution;

public class TargetObjectiveTerminationTest {

	@Test
	public void test() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		MockAlgorithm algorithm = new MockAlgorithm() {
			
			@Override
			public NondominatedPopulation getResult() {
				return result;
			}
			
		};
		
		TargetObjectiveTermination termination = new TargetObjectiveTermination(new double[] { 0, 0 });
		
		termination.initialize(algorithm);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		result.add(MockSolution.of().withObjectives(0, 0));
		Assert.assertTrue(termination.shouldTerminate(algorithm));
		
		result.clear();
		result.add(MockSolution.of().withObjectives(0.1, 0.1));
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		result.clear();
		result.add(MockSolution.of().withObjectives(0.1, 0.1));
		result.add(MockSolution.of().withObjectives(0, 0));
		Assert.assertTrue(termination.shouldTerminate(algorithm));
	}
	
	@Test
	public void testEpsilon() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		MockAlgorithm algorithm = new MockAlgorithm() {
			
			@Override
			public NondominatedPopulation getResult() {
				return result;
			}
			
		};
		
		TargetObjectiveTermination termination = new TargetObjectiveTermination(new double[] { 0, 0 }, 0.5);
		
		termination.initialize(algorithm);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		result.add(MockSolution.of().withObjectives(0, 0));
		Assert.assertTrue(termination.shouldTerminate(algorithm));
		
		result.clear();
		result.add(MockSolution.of().withObjectives(0.1, 0.1));
		Assert.assertTrue(termination.shouldTerminate(algorithm));
		
		result.clear();
		result.add(MockSolution.of().withObjectives(0.6, 0.6));
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		result.clear();
		result.add(MockSolution.of().withObjectives(0.6, 0.6));
		result.add(MockSolution.of().withObjectives(0.1, 0.1));
		Assert.assertTrue(termination.shouldTerminate(algorithm));
	}
	
}
