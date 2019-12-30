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
package org.moeaframework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;

/**
 * Tests the {@link Timing} class.
 */
public class TimingTest {
	
	@Test(expected = Exception.class)
	public void testNonexistentTimer() {
		Timing.clear();
		
		Timing.stopTimer("testNonExistentTimer");
	}

	@Test(expected = Exception.class)
	public void testDuplicateTimer() {
		Timing.clear();
		
		Timing.startTimer("testDuplicateTimer1");
		Timing.startTimer("testDuplicateTimer2");
		Timing.startTimer("testDuplicateTimer1");
	}
	
	@Test
	public void testNormalUse() {
		Timing.clear();
		
		Timing.startTimer("timer1");
		Timing.startTimer("timer2");
		Timing.stopTimer("timer2");
		Timing.startTimer("timer2");
		Timing.stopTimer("timer1");
		Timing.stopTimer("timer2");
		
		Assert.assertEquals(1, Timing.getStatistics("timer1").getN());
		Assert.assertEquals(2, Timing.getStatistics("timer2").getN());
	}
	
	@Test
	public void testClear() throws IOException {
		Timing.clear();
		Assert.assertEquals(0, TestUtils.lineCount(saveStatistics()));
		Assert.assertEquals(0, TestUtils.lineCount(saveMagnitudes()));
		
		Timing.startTimer("timer1");
		Timing.stopTimer("timer1");
		Timing.startTimer("timer2");
		Timing.stopTimer("timer2");
		
		Assert.assertEquals(2, TestUtils.lineCount(saveStatistics()));
		Assert.assertEquals(2, TestUtils.lineCount(saveMagnitudes()));
		Assert.assertNotNull(Timing.getStatistics("timer1"));
		Assert.assertNotNull(Timing.getStatistics("timer2"));
		
		Timing.clear();
		Assert.assertEquals(0, TestUtils.lineCount(saveStatistics()));
		Assert.assertEquals(0, TestUtils.lineCount(saveMagnitudes()));
		Assert.assertNull(Timing.getStatistics("timer1"));
		Assert.assertNull(Timing.getStatistics("timer2"));
	}
	
	@Test
	public void testClearWithActiveTimers() {
		Timing.clear();
		
		Timing.startTimer("timer1");
		Timing.clear();
		Timing.stopTimer("timer1");
		
		Assert.assertEquals(1, Timing.getStatistics("timer1").getN());
	}
	
	private File saveStatistics() throws IOException {
		File file = TestUtils.createTempFile();
		PrintStream ps = null;
		
		try {
			ps = new PrintStream(new FileOutputStream(file));
			
			Timing.printStatistics(ps);
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		
		return file;
	}
	
	private File saveMagnitudes() throws IOException {
		File file = TestUtils.createTempFile();
		PrintStream ps = null;
		
		try {
			ps = new PrintStream(new FileOutputStream(file));
			
			Timing.printMagnitudes(ps);
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		
		return file;
	}
	
}
