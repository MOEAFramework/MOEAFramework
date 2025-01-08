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
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.population.NondominatedSortingPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;

@RunWith(CIRunner.class)
@Retryable
public class NSGAIITest extends JMetalAlgorithmTest {
	
	public NSGAIITest() {
		super("NSGAII");
	}
	
	// Use symmetric SBX to match JMetal's implementation.
	@Override
	public void test(String problem, String algorithm1, String algorithm2, boolean allowBetterPerformance) {
		test(problem, algorithm1, TypedProperties.of("sbx.symmetric", true), algorithm2,
				new TypedProperties(), allowBetterPerformance, AlgorithmFactory.getInstance());
	}
	
	@Test
	public void testSelectionWithAndWithoutReplacementEquivalent() {
		test("UF1",
				"NSGAII",
				TypedProperties.of("withReplacement", false),
				"NSGAII",
				TypedProperties.of("withReplacement", true),
				false,
				AlgorithmFactory.getInstance());
	}
	
	@Test
	public void testSelectionWithoutReplacementIsUnique() {
		Problem problem = ProblemFactory.getInstance().getProblem("UF1");

		Variation variation = new SBX(1.0, 20.0) {

			@Override
			public Solution[] evolve(Solution[] parents) {
				Assert.assertFalse(parents[0] == parents[1]);
				return super.evolve(parents);
			}
			
		};

		NSGAII nsgaii = new NSGAII(problem, 100, new NondominatedSortingPopulation(), null, null, variation,
				new RandomInitialization(problem));
		
		while (nsgaii.getNumberOfEvaluations() < 100000) {
			nsgaii.step();
		}
	}
	
	@Test
	public void testConfiguration() {
		NSGAII algorithm = new NSGAII(new MockRealProblem(2));
		
		TypedProperties properties = algorithm.getConfiguration();
		Assert.assertTrue(properties.getBoolean("withReplacement", true));
		
		properties.setBoolean("withReplacement", false);
		algorithm.applyConfiguration(properties);
		
		properties = algorithm.getConfiguration();
		Assert.assertFalse(properties.getBoolean("withReplacement"));
	}

}
