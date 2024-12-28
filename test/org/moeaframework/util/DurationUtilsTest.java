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

import java.time.Duration;

import org.junit.Test;
import org.moeaframework.Assert;

public class DurationUtilsTest {
	
	@Test
	public void testIsGreaterThanOrEqual() {
		Assert.assertTrue(DurationUtils.isGreaterThanOrEqual(Duration.ofSeconds(10), Duration.ofSeconds(10)));
		Assert.assertTrue(DurationUtils.isGreaterThanOrEqual(Duration.ofSeconds(11), Duration.ofSeconds(10)));
		Assert.assertFalse(DurationUtils.isGreaterThanOrEqual(Duration.ofSeconds(9), Duration.ofSeconds(10)));
		
		Assert.assertTrue(DurationUtils.isGreaterThanOrEqual(Duration.ofMillis(10), Duration.ofMillis(10)));
		Assert.assertTrue(DurationUtils.isGreaterThanOrEqual(Duration.ofMillis(11), Duration.ofMillis(10)));
		Assert.assertFalse(DurationUtils.isGreaterThanOrEqual(Duration.ofMillis(9), Duration.ofMillis(10)));
	}
	
	@Test
	public void testPercentage() {
		Assert.assertEquals(0.0, DurationUtils.toPercentage(Duration.ofSeconds(0), Duration.ofSeconds(60)));
		Assert.assertEquals(50.0, DurationUtils.toPercentage(Duration.ofSeconds(30), Duration.ofSeconds(60)));
		Assert.assertEquals(100.0, DurationUtils.toPercentage(Duration.ofSeconds(60), Duration.ofSeconds(60)));
		Assert.assertEquals(200.0, DurationUtils.toPercentage(Duration.ofSeconds(120), Duration.ofSeconds(60)));
		
		Assert.assertEquals(0.0, DurationUtils.toPercentage(Duration.ofMillis(0), Duration.ofMillis(500)));
		Assert.assertEquals(50.0, DurationUtils.toPercentage(Duration.ofMillis(250), Duration.ofMillis(500)));
		Assert.assertEquals(100.0, DurationUtils.toPercentage(Duration.ofMillis(500), Duration.ofMillis(500)));
		Assert.assertEquals(200.0, DurationUtils.toPercentage(Duration.ofMillis(1000), Duration.ofMillis(500)));
	}
	
	@Test
	public void testToMilliseconds() {
		Assert.assertEquals(5000, DurationUtils.toMilliseconds(Duration.ofSeconds(5)));
		Assert.assertEquals(500, DurationUtils.toMilliseconds(Duration.ofMillis(500)));
		Assert.assertEquals(300000, DurationUtils.toMilliseconds(Duration.ofMinutes(5)));
		Assert.assertEquals(0, DurationUtils.toMilliseconds(Duration.ofNanos(5)));
	}
	
	@Test
	public void testToNanoseconds() {
		Assert.assertEquals(5000000000L, DurationUtils.toNanoseconds(Duration.ofSeconds(5)));
		Assert.assertEquals(500000000L, DurationUtils.toNanoseconds(Duration.ofMillis(500)));
		Assert.assertEquals(300000000000L, DurationUtils.toNanoseconds(Duration.ofMinutes(5)));
		Assert.assertEquals(5L, DurationUtils.toNanoseconds(Duration.ofNanos(5)));
	}
	
	@Test
	public void testFormat() {
		Assert.assertEquals("00:00:00", DurationUtils.format(Duration.ofSeconds(0)));
		Assert.assertEquals("00:01:30", DurationUtils.format(Duration.ofSeconds(90)));
		Assert.assertEquals("01:00:00", DurationUtils.format(Duration.ofHours(1)));
		Assert.assertEquals("24:00:00", DurationUtils.format(Duration.ofDays(1)));
		Assert.assertEquals("48:00:00", DurationUtils.format(Duration.ofDays(2)));
	}
	
	@Test
	public void testFormatHighResolution() {
		Assert.assertEquals("0.0 s", DurationUtils.formatHighResolution(Duration.ofSeconds(0)));
		Assert.assertEquals("0.25 s", DurationUtils.formatHighResolution(Duration.ofMillis(250)));
		Assert.assertEquals("1.0 s", DurationUtils.formatHighResolution(Duration.ofSeconds(1)));
		Assert.assertEquals("75.0 s", DurationUtils.formatHighResolution(Duration.ofSeconds(75)));
	}
	
}
