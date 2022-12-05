/* Copyright 2009-2022 David Hadka
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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

public class ProblemProviderTest {
	
	/**
	 * Checks that all named and registered problems have a readable reference set, the problem
	 * can be instantiated, and the reference set has the expected number of objective values.
	 */
	@Test
	public void testBuiltInProblems() {
		ServiceLoader<ProblemProvider> providers = ServiceLoader.load(ProblemProvider.class);
		Iterator<ProblemProvider> iterator = providers.iterator();
		
		while (iterator.hasNext()) {
			ProblemProvider provider = iterator.next();
			
			if (provider instanceof RegisteredProblemProvider) {
				RegisteredProblemProvider registeredProvider = (RegisteredProblemProvider)provider;
				
				for (String name : registeredProvider.getTestableProblems()) {
					if (name.endsWith("-JMetal")) {
						continue;
					}
					
					System.out.println("Testing " + name);
					
					NondominatedPopulation referenceSet = registeredProvider.getReferenceSet(name);
					Assert.assertNotNull(referenceSet);
					Assert.assertTrue(referenceSet.size() > 0);
					
					Problem problem = registeredProvider.getProblem(name);
					Assert.assertNotNull(problem);
					
					Assert.assertEquals(problem.getNumberOfObjectives(), referenceSet.get(0).getNumberOfObjectives());
					
					String swapCaseName = StringUtils.swapCase(name);
					Assert.assertNotNull(registeredProvider.getReferenceSet(swapCaseName));
					Assert.assertNotNull(registeredProvider.getProblem(swapCaseName));
				}
			}
		}
	}

}
