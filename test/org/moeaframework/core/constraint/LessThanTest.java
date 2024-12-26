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
package org.moeaframework.core.constraint;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class LessThanTest {
	
	@Test
	public void testCopy() {
		LessThan expected = LessThan.value(5.0, 0.1);
		LessThan actual = expected.copy();
		
		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected.getValue(), actual.getValue(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(expected.getThreshold(), actual.getThreshold(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(expected.getEpsilon(), actual.getEpsilon(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testEquals() {
		LessThan expected = LessThan.value(5.0, 0.1);
		LessThan actual = LessThan.value(5.0, 0.1);
		
		Assert.assertEquals(expected, actual);
		
		actual.setValue(5.0);
		Assert.assertNotEquals(expected, actual);
	}
	
	@Test
	public void test() {
		Assert.assertEquals(Math.nextUp(0.0), LessThan.value(5.0).withValue(5.0).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.1, LessThan.value(5.0).withValue(5.1).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, LessThan.value(5.0).withValue(4.9).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testWithEpsilon() {
		Assert.assertEquals(Math.nextUp(0.0), LessThan.value(5.0, 0.1).withValue(5.0).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.1, LessThan.value(5.0, 0.1).withValue(5.1).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.1, LessThan.value(5.0, 0.1).withValue(4.9).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.2, LessThan.value(5.0, 0.1).withValue(5.2).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, LessThan.value(5.0, 0.1).withValue(4.8).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
	}

}
