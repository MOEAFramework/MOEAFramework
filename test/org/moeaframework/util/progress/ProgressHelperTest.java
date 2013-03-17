package org.moeaframework.util.progress;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link ProgressHelper} class.
 */
public class ProgressHelperTest {
	
	/**
	 * Tests progress reporting for a single seed with small evaluation times.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTiming1() throws InterruptedException {
		test(1, 100000, 10000, 60);
	}
	
	/**
	 * Tests progress reporting for a single seed with large evaluation times.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTiming2() throws InterruptedException {
		test(1, 100000, 10000, 600);
	}
	
	/**
	 * Tests progress reporting for many seeds with small evaluation times.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTiming3() throws InterruptedException {
		test(10, 100000, 10000, 60);
	}
	
	/**
	 * Tests progress reporting for many seeds with large evaluation times.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTiming4() throws InterruptedException {
		test(10, 100000, 10000, 600);
	}
	
	/**
	 * Tests progress reporting for a single seed with fine-grain step sizes.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTiming5() throws InterruptedException {
		test(1, 1000, 1, 6);
	}
	
	/**
	 * Tests if ProgressHelper functions correctly by simulating the execution
	 * of an algorithm.
	 * 
	 * @param totalSeeds the total number of seeds to simulate
	 * @param maxNFE the maximum NFE per seed to simulate
	 * @param frequency the frequency of progress updates
	 * @param time the simulated time per step
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	private void test(int totalSeeds, int maxNFE, int frequency, int time)
			throws InterruptedException {
		ProgressHelper helper = new ProgressHelper(null);
		final List<ProgressEvent> events = new ArrayList<ProgressEvent>();
		
		helper.addProgressListener(new ProgressListener() {

			@Override
			public void progressUpdate(ProgressEvent event) {
				events.add(event);
			}
			
		});
		
		helper.start(totalSeeds, maxNFE);
		
		for (int i = 0; i < totalSeeds; i++) {
			for (int j = 0; j <= maxNFE-frequency; j += frequency) {
				Thread.sleep(time);
				helper.setCurrentNFE(j+frequency);
			}
			
			helper.nextSeed();
		}
		
		int expectedCount = totalSeeds * (maxNFE/frequency + 1);
		double expectedTime = ((expectedCount - totalSeeds) * time) / 1000.0;
		double error = 0.05 * expectedTime;
		
		Assert.assertEquals(expectedCount, events.size());
		Assert.assertEquals(1, events.get(0).getCurrentSeed());
		Assert.assertEquals(totalSeeds/2 + 1, 
				events.get(events.size()/2).getCurrentSeed());
		Assert.assertEquals(totalSeeds, 
				events.get(events.size() - 2).getCurrentSeed());
		Assert.assertEquals(expectedTime / 2.0, 
				events.get(events.size()/2 - 1).getElapsedTime(), error);
		Assert.assertEquals(expectedTime, 
				events.get(events.size()-1).getElapsedTime(), error);
		Assert.assertEquals(expectedTime / 2.0, 
				events.get(events.size()/2 - 1).getRemainingTime(), error);
		Assert.assertEquals(0.0, 
				events.get(events.size() - 1).getRemainingTime(), error);
		Assert.assertEquals(events.get(events.size()-1).getElapsedTime(), 
				events.get(events.size()/2).getElapsedTime() + 
				events.get(events.size()/2).getRemainingTime(), error);
	}

	/**
	 * Tests if progress reporting handles situations where no change in NFE
	 * occurs.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testNoProgress() throws InterruptedException {
		ProgressHelper helper = new ProgressHelper(null);
		final List<ProgressEvent> events = new ArrayList<ProgressEvent>();
		
		helper.addProgressListener(new ProgressListener() {

			@Override
			public void progressUpdate(ProgressEvent event) {
				events.add(event);
			}
			
		});
		
		helper.start(10, 100000);
		helper.setCurrentNFE(0);
		Thread.sleep(1000);
		helper.setCurrentNFE(0);
		Thread.sleep(1000);
		helper.nextSeed();
		
		Assert.assertEquals(3, events.size());
		Assert.assertTrue(Double.isNaN(events.get(0).getRemainingTime()));
		Assert.assertTrue(Double.isNaN(events.get(1).getRemainingTime()));
		Assert.assertTrue(events.get(2).getRemainingTime() > 0.0);
	}
	
	/**
	 * Tests if progress reporting handles the situation where no change in
	 * time occurs.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption@throws InterruptedException
	 */
	@Test
	public void testNoTime() throws InterruptedException {
		ProgressHelper helper = new ProgressHelper(null);
		final List<ProgressEvent> events = new ArrayList<ProgressEvent>();
		
		helper.addProgressListener(new ProgressListener() {

			@Override
			public void progressUpdate(ProgressEvent event) {
				events.add(event);
			}
			
		});
		
		helper.start(10, 100000);
		helper.setCurrentNFE(0);
		helper.setCurrentNFE(50000);
		Thread.sleep(1000);
		helper.setCurrentNFE(100000);
		
		Assert.assertEquals(4, events.size());
		Assert.assertTrue(Double.isNaN(events.get(0).getRemainingTime()));
		Assert.assertTrue(Double.isNaN(events.get(1).getRemainingTime()));
		Assert.assertTrue(events.get(2).getRemainingTime() > 0.0);
	}
	
}
