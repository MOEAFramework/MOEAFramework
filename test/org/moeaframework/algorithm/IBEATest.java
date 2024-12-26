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
package org.moeaframework.algorithm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;

@RunWith(CIRunner.class)
@Retryable
public class IBEATest extends JMetalAlgorithmTest {
	
	public IBEATest() {
		super("IBEA", true);
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem();
		IBEA algorithm = new IBEA(problem);
		
		Assert.assertEquals("hypervolume", algorithm.getConfiguration().getString("indicator"));
		
		algorithm.applyConfiguration(TypedProperties.of("indicator", "epsilon"));
		
		Assert.assertInstanceOf(AdditiveEpsilonIndicatorFitnessEvaluator.class, algorithm.getFitnessEvaluator());
		Assert.assertEquals("epsilon", algorithm.getConfiguration().getString("indicator"));
	}
	
	@Test(expected = ConfigurationException.class)
	public void testConfigurationInvalidIndicator() {
		Problem problem = new MockRealProblem();
		IBEA algorithm = new IBEA(problem);
		
		algorithm.applyConfiguration(TypedProperties.of("indicator", "foo"));
	}

}
