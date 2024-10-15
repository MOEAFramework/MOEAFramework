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
package org.moeaframework.core.objective;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class MaximizeTest {
	
	@Test
	public void testCopy() {
		Maximize expected = new Maximize();
		Maximize actual = expected.copy();
		
		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected.getValue(), actual.getValue(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testEquals() {
		Maximize expected = new Maximize();
		Maximize actual = expected.copy();
		
		Assert.assertEquals(expected, actual);
		
		actual.setValue(5.0);
		Assert.assertNotEquals(expected, actual);
	}
	
	@Test
	public void testGetCanonicalValue() {
		Assert.assertEquals(-1.0, new Maximize(1.0).getCanonicalValue());
		Assert.assertEquals(1.0, new Maximize(-1.0).getCanonicalValue());
	}
	
	@Test
	public void testCompareTo() {
		Assert.assertEquals(-1, new Maximize(2.0).compareTo(new Maximize(1.0)));
		Assert.assertEquals(0, new Maximize(1.0).compareTo(new Maximize(1.0)));
		Assert.assertEquals(1, new Maximize(1.0).compareTo(new Maximize(2.0)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCompareToDifferentType() {
		Assert.assertEquals(-1, new Maximize(1.0).compareTo(new Minimize(2.0)));
	}
	
	@Test
	public void testNormalize() {
		Assert.assertEquals(new NormalizedObjective(1.0), new Maximize(0.0).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(0.0), new Maximize(1.0).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(0.5), new Maximize(0.5).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(0.75), new Maximize(0.5).normalize(0.0, 2.0));
	}
	
	// TODO: Fill in tests

}
