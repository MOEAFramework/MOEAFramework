package org.moeaframework.core.termination;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link CompoundTerminationCondition} class.
 */
public class CompoundTerminationConditionTest {
	
	@Test
	public void testNFE() throws InterruptedException {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxElapsedTime timeTermination = new MaxElapsedTime(1000);
		MaxFunctionEvaluations nfeTermination = new MaxFunctionEvaluations(10000);
		CompoundTerminationCondition compound = new CompoundTerminationCondition(
				timeTermination, nfeTermination);
		
		compound.initialize(algorithm);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Thread.sleep(250);
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Thread.sleep(250);
		algorithm.setNumberOfEvaluations(10001);
		Assert.assertTrue(compound.shouldTerminate(algorithm));
	}
	
	@Test
	public void testTime() throws InterruptedException {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxElapsedTime timeTermination = new MaxElapsedTime(1000);
		MaxFunctionEvaluations nfeTermination = new MaxFunctionEvaluations(10000);
		CompoundTerminationCondition compound = new CompoundTerminationCondition(
				timeTermination, nfeTermination);
		
		compound.initialize(algorithm);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Thread.sleep(550);
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Thread.sleep(550);
		algorithm.setNumberOfEvaluations(2000);
		Assert.assertTrue(compound.shouldTerminate(algorithm));
	}

}
