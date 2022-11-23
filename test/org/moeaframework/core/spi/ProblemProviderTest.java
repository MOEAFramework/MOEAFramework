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
