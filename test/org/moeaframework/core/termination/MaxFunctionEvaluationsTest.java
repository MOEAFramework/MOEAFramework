package org.moeaframework.core.termination;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link MaxFunctionEvaluations} class.
 */
public class MaxFunctionEvaluationsTest {

	@Test
	public void test() {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxFunctionEvaluations termination = new MaxFunctionEvaluations(10000);
		
		termination.initialize(algorithm);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		algorithm.setNumberOfEvaluations(10001);
		Assert.assertTrue(termination.shouldTerminate(algorithm));
	}
	
}
