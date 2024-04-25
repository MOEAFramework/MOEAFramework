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
package org.moeaframework.util.progress;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.Wait;

@RunWith(CIRunner.class)
@Retryable
public class ProgressHelperTest {
	
	/**
	 * Tests progress reporting for a single seed.
	 */
	@Test
	public void testTimingSingleSeed() {
		test(1, 100000, 10000, 50);
	}
	
	/**
	 * Tests progress reporting for many seeds.
	 */
	@Test
	public void testTimingManySeeds() {
		test(5, 100000, 10000, 50);
	}
	
	/**
	 * Tests progress reporting for a single seed with fine-grain step sizes.
	 */
	@Test
	public void testTimingFineGrained() {
		test(1, 50, 1, 50);
	}
	
	/**
	 * Tests if ProgressHelper functions correctly by simulating the execution of an algorithm.
	 * 
	 * @param totalSeeds the total number of seeds to simulate
	 * @param maxNFE the maximum NFE per seed to simulate
	 * @param frequency the frequency of progress updates
	 * @param time the simulated time per step, in milliseconds
	 */
	private void test(int totalSeeds, int maxNFE, int frequency, int time) {
		ProgressHelper helper = new ProgressHelper(null);		
		final List<ProgressEvent> events = new ArrayList<ProgressEvent>();
		
		helper.addProgressListener(new ProgressListener() {

			@Override
			public void progressUpdate(ProgressEvent event) {
				events.add(event);
			}
			
		});
		
		// force an event to fire before starting the actual test, as there appears to be a relatively large
		// overhead on the first event
		helper.start(totalSeeds, maxNFE, -1);
		helper.setCurrentNFE(frequency);
		helper.stop();
		events.clear();
		
		helper.start(totalSeeds, maxNFE, -1);
		long startTime = System.currentTimeMillis();
		
		for (int i = 0; i < totalSeeds; i++) {
			for (int j = 0; j <= maxNFE-frequency; j += frequency) {
				Wait.spinFor(Duration.ofMillis(time));				
				helper.setCurrentNFE(j+frequency);
			}
			
			helper.nextSeed();
		}
				
		int expectedCount = totalSeeds * (maxNFE/frequency + 1);
		double expectedTime = (System.currentTimeMillis() - startTime) / 1000.0;
		double error = 2 * time / 1000.0;
		
		Assert.assertEquals(expectedCount, events.size());
		Assert.assertFalse(events.get(0).isSeedFinished());
		Assert.assertTrue(events.get(events.size() - 1).isSeedFinished());
		
		// test seed count
		Assert.assertEquals(1, events.get(0).getCurrentSeed());
		Assert.assertEquals(totalSeeds/2 + 1, events.get(events.size()/2).getCurrentSeed());
		Assert.assertEquals(totalSeeds, events.get(events.size() - 2).getCurrentSeed());
		
		// test elapsed time
		Assert.assertEquals(expectedTime / 2.0, events.get(events.size()/2 - 1).getElapsedTime(), error);
		Assert.assertEquals(expectedTime, events.get(events.size()-1).getElapsedTime(), error);
		
		// test remaining time
		Assert.assertEquals(expectedTime / 2.0, events.get(events.size()/2 - 1).getRemainingTime(), error);
		Assert.assertEquals(0.0, events.get(events.size() - 1).getRemainingTime(), error);
		Assert.assertEquals(events.get(events.size()-1).getElapsedTime(),
				events.get(events.size()/2).getElapsedTime() +
				events.get(events.size()/2).getRemainingTime(), error);
		
		// test percent complete
		Assert.assertEquals(0.5, events.get(events.size()/2 - 1).getPercentComplete(), 0.05);
		Assert.assertEquals(1.0, events.get(events.size() - 1).getPercentComplete(), 0.05);
		
		// test constant attributes
		for (ProgressEvent event : events) {
			Assert.assertEquals(totalSeeds, event.getTotalSeeds());
			Assert.assertEquals(maxNFE, event.getMaxNFE());
		}
	}

	/**
	 * Tests if progress reporting handles situations where no change in NFE occurs.
	 */
	@Test
	public void testNoProgress() {
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
		Wait.spinFor(Duration.ofMillis(50));
		helper.setCurrentNFE(0);
		Wait.spinFor(Duration.ofMillis(50));
		helper.nextSeed();
		
		Assert.assertEquals(3, events.size());
		Assert.assertTrue(Double.isNaN(events.get(0).getRemainingTime()));
		Assert.assertTrue(Double.isNaN(events.get(1).getRemainingTime()));
		Assert.assertTrue(events.get(2).getRemainingTime() > 0.0);
	}
	
	/**
	 * Tests if progress reporting handles the situation where no change in time occurs.
	 */
	@Test
	public void testNoTime() {
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
		Wait.spinFor(Duration.ofMillis(50));
		helper.setCurrentNFE(100000);
		
		Assert.assertEquals(3, events.size());
		Assert.assertTrue(Double.isNaN(events.get(0).getRemainingTime()));
		Assert.assertTrue(Double.isNaN(events.get(1).getRemainingTime()) || events.get(1).getRemainingTime() > 0.0);
		Assert.assertTrue(events.get(2).getRemainingTime() > 0.0);
	}
}
