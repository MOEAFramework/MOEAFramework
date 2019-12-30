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
package org.moeaframework;

import org.junit.Assert;
import org.moeaframework.util.progress.ProgressEvent;
import org.moeaframework.util.progress.ProgressListener;

public class TestProgressListener implements ProgressListener {

	private int seedCount = 0;
	
	private int callCount = 0;
	
	private ProgressEvent lastEvent = null;
	
	@Override
	public void progressUpdate(ProgressEvent event) {
		if (event.isSeedFinished()) {
			Assert.assertNull(event.getCurrentAlgorithm());
			Assert.assertTrue(event.getCurrentSeed() >= 0);
			Assert.assertTrue(event.getCurrentNFE() == 0);
			seedCount++;
		}
		else {
			Assert.assertNotNull(event.getCurrentAlgorithm());
			Assert.assertTrue(event.getCurrentSeed() >= 0 && event.getCurrentSeed() <= event.getTotalSeeds());
			Assert.assertTrue(event.getCurrentNFE() >= 0);
		}
		
		Assert.assertNotNull(event.getExecutor());
		Assert.assertTrue(event.getElapsedTime() >= 0.0);
		Assert.assertTrue(event.getRemainingTime() >= 0.0 || Double.isNaN(event.getRemainingTime()));
		Assert.assertTrue(event.getMaxTime() == -1.0); // Will be negative if not set

		
		callCount++;
		lastEvent = event;
	}
	
	public int getSeedCount() {
		return seedCount;
	}
	
	public int getCallCount() {
		return callCount;
	}
	
	public ProgressEvent getLastEvent() {
		return lastEvent;
	}

}
