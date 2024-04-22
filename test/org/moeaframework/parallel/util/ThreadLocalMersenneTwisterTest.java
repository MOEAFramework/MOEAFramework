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
package org.moeaframework.parallel.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Wait;
import org.moeaframework.core.PRNG;

public class ThreadLocalMersenneTwisterTest {
	
	@Test
	public void testUniqueInstancePerThread() throws InterruptedException {
		List<Integer> samples1 = Collections.synchronizedList(new ArrayList<Integer>());
		List<Integer> samples2 = Collections.synchronizedList(new ArrayList<Integer>());
		
		PRNG.setRandom(ThreadLocalMersenneTwister.getInstance());
		
		Thread thread1 = new SamplerThread(samples1);
		Thread thread2 = new SamplerThread(samples2);
		
		thread1.start();
		thread2.start();
		
		thread1.join();
		thread2.join();
		
		Assert.assertEquals(samples1, samples2);
	}
	
	private static class SamplerThread extends Thread {
		
		private final List<Integer> samples;
		
		public SamplerThread(List<Integer> samples) {
			super();
			this.samples = samples;
		}
		
		@Override
		public void run() {
			PRNG.setSeed(12345);
				
			for (int i = 0; i < 100; i++) {
				samples.add(PRNG.nextInt());
				Wait.spinFor(Duration.ofMillis(1));
			}
		}
		
	}

}
