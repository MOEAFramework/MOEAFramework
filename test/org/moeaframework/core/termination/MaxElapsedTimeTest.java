package org.moeaframework.core.termination;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link MaxElapsedTime} class.
 */
public class MaxElapsedTimeTest {

	@Test
	public void test() throws InterruptedException {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxElapsedTime termination = new MaxElapsedTime(1000);
		
		termination.initialize(algorithm);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		Thread.sleep(550);
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		Thread.sleep(550);
		algorithm.setNumberOfEvaluations(2000);
		Assert.assertTrue(termination.shouldTerminate(algorithm));
	}
	
}
