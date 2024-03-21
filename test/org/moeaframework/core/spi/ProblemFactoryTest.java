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
package org.moeaframework.core.spi;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.MockRealProblem;

/**
 * Note that most of the functionality is indirectly tested by other test functions.
 */
public class ProblemFactoryTest extends AbstractFactoryTest<ProblemProvider, ProblemFactory> {
	
	@Override
	public Class<ProblemProvider> getProviderType() {
		return ProblemProvider.class;
	}
	
	@Override
	public ProblemFactory createFactory() {
		return ProblemFactory.getInstance();
	}
	
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
		
		ProblemFactory factory = new ProblemFactory();
		factory.addProvider(provider);
		
		Assert.assertNotNull(factory.getProblem("testProblem"));
		Assert.assertNotNull(factory.getReferenceSet("testProblem"));
	}
	
	@Test
	public void testNoProvider() {
		ProblemFactory factory = new ProblemFactory();
		
		Assert.assertThrows(ProviderNotFoundException.class, () -> factory.getProblem("testProblem"));
		Assert.assertNull(factory.getReferenceSet("testProblem"));
	}
	
	@Test
	public void testRegisteredProblemProvider() {
		RegisteredProblemProvider provider = new RegisteredProblemProvider();
		provider.register("testProblem", MockRealProblem::new, null);
		provider.registerDiagnosticToolProblem("testProblem");
		
		ProblemFactory factory = new ProblemFactory();
		factory.addProvider(provider);
		
		Assert.assertNotNull(factory.getProblem("testProblem"));
		Assert.assertNull(factory.getReferenceSet("testProblem"));
		Assert.assertTrue(factory.getAllRegisteredProblems().contains("testProblem"));
		Assert.assertTrue(factory.getAllDiagnosticToolProblems().contains("testProblem"));	
	}
	
	@Test
	public void testDiagnosticToolProblems() {
		for (String name : ProblemFactory.getInstance().getAllDiagnosticToolProblems()) {
			System.out.println("Testing " + name);
			
			Problem problem = ProblemFactory.getInstance().getProblem(name);
			Assert.assertNotNull(problem);
			
			NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(name);
			Assert.assertNotNull(referenceSet);
			Assert.assertTrue(referenceSet.size() > 0);

			Assert.assertEquals(problem.getNumberOfObjectives(), referenceSet.get(0).getNumberOfObjectives());
			
			String swapCaseName = StringUtils.swapCase(name);
			Assert.assertNotNull(ProblemFactory.getInstance().getProblem(swapCaseName));
			Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet(swapCaseName));
		}
	}

}
