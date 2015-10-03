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
