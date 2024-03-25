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
import org.junit.Assume;
import org.junit.Test;
import org.moeaframework.algorithm.AlgorithmTest;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.RosenbrockTestProblem;
import org.moeaframework.util.TypedProperties;

public abstract class AbstractSingleObjectiveAlgorithmTest<T extends Algorithm & Configurable> extends AlgorithmTest {
	
	public abstract T createInstance(Problem problem);

	@Test
	public void testConfiguration() {
		T algorithm = createInstance(new MockRealProblem());
		TypedProperties properties = algorithm.getConfiguration();
		
		if (algorithm instanceof SingleObjectiveEvolutionaryAlgorithm soAlgorithm) {
			Assert.assertEquals("linear", properties.getString("method"));
			Assert.assertTrue(soAlgorithm.getComparator() instanceof LinearDominanceComparator);
			
			properties.setString("method", "min-max");
			algorithm.applyConfiguration(properties);
			Assert.assertTrue(soAlgorithm.getComparator() instanceof MinMaxDominanceComparator);
		} else {
			Assume.assumeTrue("algorithm is not SingleObjectiveEvolutionaryAlgorithm, skipping test", false);
		}
	}
	
	@Test(expected = ConfigurationException.class)
	public void testConfigurationInvalidIndicator() {
		T algorithm = createInstance(new MockRealProblem());
		algorithm.applyConfiguration(TypedProperties.withProperty("method", "foo"));
	}
	
	@Test
	public void testRosenbrock() {
		RosenbrockTestProblem problem = new RosenbrockTestProblem();
		T algorithm = createInstance(problem);
		algorithm.run(100000);
		
		Assert.assertEquals(1, algorithm.getResult().size());
		
		Solution solution = algorithm.getResult().get(0);
		
		Assert.assertArrayEquals(problem.getIdealVariables(), EncodingUtils.getReal(solution), 0.1);
		Assert.assertEquals(problem.getIdealObjectiveValue(), solution.getObjective(0), 0.1);
	}

}
