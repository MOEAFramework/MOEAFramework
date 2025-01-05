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
package org.moeaframework.algorithm.single;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.AlgorithmTest;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.comparator.LinearDominanceComparator;
import org.moeaframework.core.comparator.MinMaxDominanceComparator;
import org.moeaframework.core.comparator.VectorAngleDistanceScalingComparator;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.single.Rosenbrock;

@RunWith(CIRunner.class)
@Ignore("Abstract test class")
public abstract class AbstractSingleObjectiveAlgorithmTest<T extends Algorithm & Configurable> extends AlgorithmTest {
	
	public abstract T createInstance(Problem problem);

	@Test
	public void testConfiguration() {
		T algorithm = createInstance(new MockRealProblem());
		TypedProperties properties = algorithm.getConfiguration();
		
		Assume.assumeInstanceOf(SingleObjectiveEvolutionaryAlgorithm.class, algorithm);
		
		Assert.assertEquals("linear", properties.getString("method"));
		Assert.assertInstanceOf(LinearDominanceComparator.class,
				((SingleObjectiveEvolutionaryAlgorithm)algorithm).getComparator());
			
		properties.setString("method", "min-max");
		algorithm.applyConfiguration(properties);
		Assert.assertInstanceOf(MinMaxDominanceComparator.class,
				((SingleObjectiveEvolutionaryAlgorithm)algorithm).getComparator());
			
		properties.setString("method", "angle");
		algorithm.applyConfiguration(properties);
		Assert.assertInstanceOf(VectorAngleDistanceScalingComparator.class,
				((SingleObjectiveEvolutionaryAlgorithm)algorithm).getComparator());
	}
	
	@Test(expected = ConfigurationException.class)
	public void testConfigurationInvalidIndicator() {
		T algorithm = createInstance(new MockRealProblem());
		algorithm.applyConfiguration(TypedProperties.of("method", "foo"));
	}
	
	@Test
	@Retryable
	public void testRosenbrock() {
		Rosenbrock problem = new Rosenbrock();
		
		Algorithm algorithm = createInstance(problem);
		algorithm.run(100000);
		
		Assert.assertEquals(1, algorithm.getResult().size());
		
		Solution solution = algorithm.getResult().get(0);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, RealVariable.getReal(solution), 0.1);
		Assert.assertArrayEquals(new double[] { 0.0 }, solution.getObjectiveValues(), 0.1);
	}

}
