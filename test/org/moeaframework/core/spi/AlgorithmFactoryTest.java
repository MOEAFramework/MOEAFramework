/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.spi;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link AlgorithmFactory} class.  Note that most of the
 * functionality is indirectly tested by other test functions.
 */
public class AlgorithmFactoryTest {

	@Test
	public void testCustomProvider() {
		AlgorithmProvider provider = new AlgorithmProvider() {

			@Override
			public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
				if (name.equalsIgnoreCase("testAlgorithm")) {
					return new AbstractEvolutionaryAlgorithm(
							problem,
							new Population(),
							null,
							new RandomInitialization(problem, 100)) {

						@Override
						protected void iterate() {
							// do nothing
						}
						
					};
				} else {
					return null;
				}
			}
			
		};
		
		AlgorithmFactory originalFactory = AlgorithmFactory.getInstance();
		
		AlgorithmFactory factory = new AlgorithmFactory();
		factory.addProvider(provider);
		AlgorithmFactory.setInstance(factory);
		
		Assert.assertNotNull(factory.getAlgorithm("testAlgorithm",
				new Properties(),
				new MockRealProblem()));
		
		try {
			factory.getAlgorithm("testAlgorithmNonExistant",
					new Properties(),
					new MockRealProblem());
			
			Assert.fail("failed to throw ProviderNotFoundException");
		} catch (ProviderNotFoundException e) {
			// ok
		}
		
		AlgorithmFactory.setInstance(originalFactory);
	}
	
}
