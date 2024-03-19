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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.CIRunner;
import org.moeaframework.Flaky;
import org.moeaframework.Retryable;
import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

/**
 * Tests the {@link IBEA} class.
 */
@RunWith(CIRunner.class)
@Retryable
public class IBEATest extends AlgorithmTest {
	
	@Test
	public void testDTLZ1() throws IOException {
		assumeJMetalExists();
		test("DTLZ1_2", "IBEA", "IBEA-JMetal");
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		assumeJMetalExists();
		test("DTLZ2_2", "IBEA", "IBEA-JMetal");
	}
	
	@Test
	@Flaky
	public void testDTLZ7() throws IOException {
		assumeJMetalExists();
		test("DTLZ7_2", "IBEA", "IBEA-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		assumeJMetalExists();
		test("UF1", "IBEA", "IBEA-JMetal");
	}
	
	@Test
	public void testConfigureIndicator() {
		Problem problem = new MockRealProblem();	
		IBEA algorithm = new IBEA(problem);
		
		Assert.assertEquals("hypervolume", algorithm.getConfiguration().getString("indicator"));
		
		algorithm.applyConfiguration(TypedProperties.withProperty("indicator", "epsilon"));
		
		Assert.assertTrue(algorithm.getFitnessEvaluator() instanceof AdditiveEpsilonIndicatorFitnessEvaluator);
		Assert.assertEquals("epsilon", algorithm.getConfiguration().getString("indicator"));
	}
	
	@Test(expected = ConfigurationException.class)
	public void testConfigureInvalidIndicator() {
		Problem problem = new MockRealProblem();	
		IBEA algorithm = new IBEA(problem);
		
		algorithm.applyConfiguration(TypedProperties.withProperty("indicator", "foo"));
	}

}
