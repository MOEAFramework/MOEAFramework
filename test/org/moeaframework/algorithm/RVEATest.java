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
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

public class RVEATest {
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorRequiresAtLeastTwoObjectives() {
		new RVEA(new MockRealProblem());
	}
	
	@Test(expected=ProviderNotFoundException.class)
	public void testProviderRequiresAtLeastTwoObjectives() {
		AlgorithmFactory.getInstance().getAlgorithm("RVEA", new TypedProperties(), new MockRealProblem());
	}
	
	@Test
	public void testDefaults() {
		Problem problem = new MockRealProblem(2);
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.forProblem(problem);
		
		RVEA algorithm = new RVEA(problem);
		
		Assert.assertEquals(divisions, algorithm.getPopulation().getDivisions());
	}
	
	@Test
	public void testMaxIterations() {
		Problem problem = new MockRealProblem(2);
		
		RVEA algorithm = new RVEA(problem);
		Assert.assertEquals(-1, algorithm.getMaxIterations());
		
		algorithm.run(10000);
		Assert.assertEquals(100, algorithm.getMaxIterations());
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);
		NormalBoundaryDivisions divisions = new NormalBoundaryDivisions(10);
		
		RVEA algorithm = new RVEA(problem);
		algorithm.applyConfiguration(divisions.toProperties());
		Assert.assertEquals(divisions, algorithm.getPopulation().getDivisions());
		
		algorithm.applyConfiguration(TypedProperties.of("maxIterations", 20));
		Assert.assertEquals(20, algorithm.getMaxIterations());
	}
	
	@Test
	public void testAlpha() {
		Problem problem = new MockRealProblem(2);
		RVEA algorithm = new RVEA(problem);
		
		TypedProperties properties = algorithm.getConfiguration();
		Assert.assertEquals(algorithm.getPopulation().getAlpha(), properties.getDouble("alpha"), TestThresholds.HIGH_PRECISION);
		
		properties.setDouble("alpha", 0.5);
		algorithm.applyConfiguration(properties);
		
		Assert.assertEquals(0.5, algorithm.getPopulation().getAlpha(), TestThresholds.HIGH_PRECISION);
	}

}
