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
package org.moeaframework.util;

import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;

public class TimingTest {
	
	@Test(expected = IllegalStateException.class)
	public void testNonexistentTimer() {
		Timing.clear();
		
		Timing.stopTimer("testNonExistentTimer");
	}

	@Test(expected = IllegalStateException.class)
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
	public void testDisplay() throws IOException {
		Timing.clear();
		Capture.stream(Timing::display).assertThat(out -> Assert.assertLineCount(2, out.toString()));
		
		Timing.startTimer("timer1");
		Timing.stopTimer("timer1");
		Timing.startTimer("timer2");
		Timing.stopTimer("timer2");
		Capture.stream(Timing::display).assertThat(out -> Assert.assertLineCount(4, out.toString()));
	}
	
	@Test
	public void testClear() {
		Timing.clear();
		
		Timing.startTimer("timer1");
		Timing.stopTimer("timer1");
		Timing.startTimer("timer2");
		Timing.stopTimer("timer2");
		
		Assert.assertEquals(1, Timing.getStatistics("timer1").getN());
		Assert.assertEquals(1, Timing.getStatistics("timer2").getN());
		
		Timing.clear();
		Assert.assertEquals(0, Timing.getStatistics("timer1").getN());
		Assert.assertEquals(0, Timing.getStatistics("timer2").getN());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testClearWithActiveTimers() {
		Timing.clear();
		
		Timing.startTimer("timer1");
		Timing.clear();
		Timing.stopTimer("timer1");
	}
	
}
