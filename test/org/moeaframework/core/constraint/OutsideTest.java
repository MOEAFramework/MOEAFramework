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
package org.moeaframework.core.constraint;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class OutsideTest {
	
	@Test
	public void testCopy() {
		Outside expected = Outside.values(5.0, 10.0, 0.1);
		Outside actual = expected.copy();
		
		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected.getValue(), actual.getValue(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(expected.getLower(), actual.getLower(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(expected.getUpper(), actual.getUpper(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(expected.getEpsilon(), actual.getEpsilon(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testEquals() {
		Outside expected = Outside.values(5.0, 10.0, 0.1);
		Outside actual = Outside.values(5.0, 10.0, 0.1);
		
		Assert.assertEquals(expected, actual);
		
		actual.setValue(5.0);
		Assert.assertNotEquals(expected, actual);
	}
	
	@Test
	public void test() {
		Assert.assertEquals(Math.nextUp(0.0), Outside.values(5.0, 5.0).withValue(5.0).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.5, Outside.values(4.0, 6.0).withValue(4.5).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.5, Outside.values(4.0, 6.0).withValue(5.5).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Outside.values(4.0, 6.0).withValue(3.9).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Outside.values(4.0, 6.0).withValue(6.1).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testWithEpsilon() {
		Assert.assertEquals(Math.nextUp(0.0), Outside.values(5.0, 5.0, 0.1).withValue(5.0).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.09, Outside.values(4.0, 6.0, 0.1).withValue(3.91).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.09, Outside.values(4.0, 6.0, 0.1).withValue(6.09).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Outside.values(4.0, 6.0, 0.1).withValue(3.8).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Outside.values(4.0, 6.0, 0.1).withValue(6.2).getMagnitudeOfViolation(), TestThresholds.HIGH_PRECISION);
	}

}
