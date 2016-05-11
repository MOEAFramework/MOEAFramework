package org.moeaframework.algorithm;

import java.util.Properties;

import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.problem.MockBinaryProblem;

/**
 * Tests the {@link RVEA} class.
 */
public class RVEATest {
	
	/**
	 * Ensure RVEA can not be applied to problems with fewer than two
	 * objectives.
	 */
	@Test(expected=ProviderNotFoundException.class)
	public void testInitialConditions() {
		Problem problem = new MockBinaryProblem();
		AlgorithmFactory.getInstance().getAlgorithm("RVEA", new Properties(), problem);
	}

}
