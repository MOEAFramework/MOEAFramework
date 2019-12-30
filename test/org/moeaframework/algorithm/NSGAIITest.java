/* Copyright 2009-2019 David Hadka
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
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TravisRunner;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Tests the {@link NSGAII} class.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class NSGAIITest extends AlgorithmTest {
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "NSGAII", "NSGAII-JMetal");
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "NSGAII", "NSGAII-JMetal");
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "NSGAII", "NSGAII-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "NSGAII", "NSGAII-JMetal");
	}
	
	/**
	 * Selection with and without replacement should produce statistically
	 * similar results.  Differences may appear throughout search due to better
	 * diversity when using without replacement, but end-of-run indicators
	 * should be identical on simple problems.
	 */
	@Test
	public void testSelection() {
		test("UF1",
				"NSGAII",
				TypedProperties.withProperty("withReplacement", "false")
							   .getProperties(),
				"NSGAII",
				TypedProperties.withProperty("withReplacement", "true")
							   .getProperties(),
				false,
				AlgorithmFactory.getInstance());
	}
	
	/**
	 * When using selection without replacement, the same parent should never
	 * be selected twice.  This only hold true if the population size is
	 * a multiple of {@code 2*variation.getArity()}.
	 */
	@Test
	public void testSelectionIsUnique() {
		Problem problem = ProblemFactory.getInstance().getProblem("UF1");

		Variation variation = new SBX(1.0, 20.0) {

			@Override
			public Solution[] evolve(Solution[] parents) {
				Assert.assertFalse(parents[0] == parents[1]);
				return super.evolve(parents);
			}
			
		};

		NSGAII nsgaii = new NSGAII(problem, new NondominatedSortingPopulation(),
				null, null, variation, new RandomInitialization(problem, 100));
		
		while (nsgaii.getNumberOfEvaluations() < 100000) {
			nsgaii.step();
		}
	}

}
