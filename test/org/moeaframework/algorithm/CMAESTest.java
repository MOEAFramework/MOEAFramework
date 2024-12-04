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
package org.moeaframework.algorithm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.IgnoreOnCI;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeFitnessEvaluator;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.single.Rosenbrock;

@RunWith(CIRunner.class)
public class CMAESTest extends AlgorithmTest {
	
	@Test
	public void testSingleObjective() {
		Rosenbrock problem = new Rosenbrock();
		CMAES algorithm = new CMAES(problem);

		for (int i = 0; i < 100; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(1, algorithm.getResult().size());
		
		Solution solution = algorithm.getResult().get(0);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0 }, RealVariable.getReal(solution), 0.001);
		Assert.assertArrayEquals(new double[] { 0.0 }, solution.getObjectiveValues(), 0.001);
	}

	@Test
	public void testMultiObjective() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		CMAES algorithm = new CMAES(problem);

		for (int i = 0; i < 100; i++) {
			algorithm.step();
		}
	}
	
	@Test
	@IgnoreOnCI("5 minute runtime")
	public void testCheckConsistency() {
		test("DTLZ2_2",
				"CMA-ES", new TypedProperties(),
				"CMA-ES", TypedProperties.of("checkConsistency", true),
				false, AlgorithmFactory.getInstance());
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem();
		CMAES algorithm = new CMAES(problem);
		
		Assert.assertEquals("crowding", algorithm.getConfiguration().getString("indicator"));
		
		algorithm.applyConfiguration(TypedProperties.of("indicator", "epsilon"));
		Assert.assertInstanceOf(AdditiveEpsilonIndicatorFitnessEvaluator.class, algorithm.getFitnessEvaluator());
		Assert.assertEquals("epsilon", algorithm.getConfiguration().getString("indicator"));
		
		algorithm.applyConfiguration(TypedProperties.of("indicator", "hypervolume"));
		Assert.assertInstanceOf(HypervolumeFitnessEvaluator.class, algorithm.getFitnessEvaluator());
		Assert.assertEquals("hypervolume", algorithm.getConfiguration().getString("indicator"));
		
		algorithm.applyConfiguration(TypedProperties.of("indicator", "crowding"));
		Assert.assertNull(algorithm.getFitnessEvaluator());
	}
	
	@Test(expected = ConfigurationException.class)
	public void testConfigurationInvalidIndicator() {
		Problem problem = new MockRealProblem();
		CMAES algorithm = new CMAES(problem);
		
		algorithm.applyConfiguration(TypedProperties.of("indicator", "foo"));
	}
	
}
