/* Copyright 2009-2019 David Hadka
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
	 * Tests progress reporting for a single seed.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTimingSingleSeed() throws InterruptedException {
		test(1, 100000, 10000, 500);
	}
	
	/**
	 * Tests progress reporting for many seeds.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTimingManySeeds() throws InterruptedException {
		test(10, 100000, 10000, 500);
	}
	
	/**
	 * Tests progress reporting for a single seed with fine-grain step sizes.
	 * 
	 * @throws InterruptedException if the simulation failed to execute
	 *         properly due to an interruption
	 */
	@Test
	public void testTimingFineGrained() throws InterruptedException {
		test(1, 1000, 1, 50);
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
		
		helper.start(totalSeeds, maxNFE, -1);
		
		for (int i = 0; i < totalSeeds; i++) {
			for (int j = 0; j <= maxNFE-frequency; j += frequency) {
				long start = System.nanoTime();
				
				while (System.nanoTime() - start < time*1000000) {
					//loop for the given amount of time
				}
				
				helper.setCurrentNFE(j+frequency);
			}
			
			helper.nextSeed();
		}
		
		int expectedCount = totalSeeds * (maxNFE/frequency + 1);
		double expectedTime = ((expectedCount - totalSeeds) * time) / 1000.0;
		double error = 0.05 * expectedTime;
		
		Assert.assertEquals(expectedCount, events.size());
		Assert.assertFalse(events.get(0).isSeedFinished());
		Assert.assertTrue(events.get(events.size() - 1).isSeedFinished());
		
		// test seed count
		Assert.assertEquals(1, events.get(0).getCurrentSeed());
		Assert.assertEquals(totalSeeds/2 + 1,
				events.get(events.size()/2).getCurrentSeed());
		Assert.assertEquals(totalSeeds,
				events.get(events.size() - 2).getCurrentSeed());
		
		// test elapsed time
		Assert.assertEquals(expectedTime / 2.0,
				events.get(events.size()/2 - 1).getElapsedTime(), error);
		Assert.assertEquals(expectedTime,
				events.get(events.size()-1).getElapsedTime(), error);
		
		// test remaining time
		Assert.assertEquals(expectedTime / 2.0,
				events.get(events.size()/2 - 1).getRemainingTime(), error);
		Assert.assertEquals(0.0,
				events.get(events.size() - 1).getRemainingTime(), error);
		Assert.assertEquals(events.get(events.size()-1).getElapsedTime(),
				events.get(events.size()/2).getElapsedTime() +
				events.get(events.size()/2).getRemainingTime(), error);
		
		// test percent complete
		Assert.assertEquals(0.5,
				events.get(events.size()/2 - 1).getPercentComplete(), 0.05);
		Assert.assertEquals(1.0,
				events.get(events.size() - 1).getPercentComplete(), 0.05);
		
		// test constant attributes
		for (ProgressEvent event : events) {
			Assert.assertEquals(totalSeeds, event.getTotalSeeds());
			Assert.assertEquals(maxNFE, event.getMaxNFE());
		}
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
		
		helper.start(10, 100000, -1);
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
		
		helper.start(10, 100000, -1);
		helper.setCurrentNFE(0);
		helper.setCurrentNFE(50000);
		Thread.sleep(1000);
		helper.setCurrentNFE(100000);
		
		Assert.assertEquals(3, events.size());
		Assert.assertTrue(Double.isNaN(events.get(0).getRemainingTime()));
		Assert.assertTrue(Double.isNaN(events.get(1).getRemainingTime()) ||
				events.get(1).getRemainingTime() > 0.0);
		Assert.assertTrue(events.get(2).getRemainingTime() > 0.0);
	}
	
}
