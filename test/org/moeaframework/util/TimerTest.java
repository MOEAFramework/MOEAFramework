/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TempFiles;
import org.moeaframework.Wait;

@RunWith(CIRunner.class)
@Retryable
public class TimerTest {
	
	@Test
	public void testUnnamed() throws IOException {
		Timer timer = new Timer();
		Assert.assertNull(timer.getName());
		
		run(timer, 5, Duration.ofMillis(100));
	}
	
	@Test
	public void testNamed() throws IOException {
		Timer timer = new Timer("foo");
		Assert.assertEquals("foo", timer.getName());
		
		run(timer, 5, Duration.ofMillis(100));
	}
	
	@Test
	public void testClear() throws IOException {
		Timer timer = new Timer("foo");
		run(timer, 5, Duration.ofMillis(100));
		
		timer.clear();
		
		Assert.assertEquals("foo", timer.getName());
		Assert.assertEquals(0, timer.getStatistics().getN());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testMultipleStarts() {
		Timer timer = new Timer();
		timer.start();
		timer.start();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testMultipleStops() {
		Timer timer = new Timer();
		timer.start();
		timer.stop();
		timer.stop();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStopWithoutStart() {
		Timer timer = new Timer();
		timer.stop();
	}
	
	private void run(Timer timer, int iterations, Duration delay) throws IOException {
		for (int i = 0; i < iterations; i++) {
			timer.start();
			Wait.spinFor(delay);
			timer.stop();
		}
		
		StatisticalSummary statistics = timer.getStatistics();
		Assert.assertEquals(iterations, statistics.getN());
		Assert.assertBetween(iterations * DurationUtils.toSeconds(delay),
				(iterations + 1)*DurationUtils.toSeconds(delay),
				statistics.getSum());
		
		Assert.assertLineCount(3, saveDisplayToFile(timer));
	}
	
	private File saveDisplayToFile(Timer timer) throws IOException {
		File file = TempFiles.createFile();
		
		try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
			timer.display(ps);
		}
		
		return file;
	}
	
}
