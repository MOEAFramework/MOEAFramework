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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link ProblemFactory} class.  Note that most of the
 * functionality is indirectly tested by other test functions.
 */
public class ProblemFactoryTest {
	
	@Test
	public void testCustomProvider() {
		ProblemProvider provider = new ProblemProvider() {

			@Override
			public Problem getProblem(String name) {
				if (name.equals("testProblem")) {
					return new MockRealProblem();
				} else {
					return null;
				}
			}

			@Override
			public NondominatedPopulation getReferenceSet(String name) {
				if (name.equals("testProblem")) {
					return new NondominatedPopulation();
				} else {
					return null;
				}
			}
			
		};
		
		ProblemFactory originalFactory = ProblemFactory.getInstance();
		
		ProblemFactory factory = new ProblemFactory();
		factory.addProvider(provider);
		ProblemFactory.setInstance(factory);
		
		Assert.assertNotNull(factory.getProblem("testProblem"));
		Assert.assertNotNull(factory.getReferenceSet("testProblem"));
		
		try {
			factory.getProblem("testProblemNonExistant");
			
			Assert.fail("failed to throw ProviderNotFoundException");
		} catch (ProviderNotFoundException e) {
			// ok
		}
		
		Assert.assertNull(factory.getReferenceSet("testProblemNonExistant"));
		
		ProblemFactory.setInstance(originalFactory);
	}

}
