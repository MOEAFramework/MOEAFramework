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
package org.moeaframework.analysis.parameter;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.sample.Sample;

public class DecimalRangeTest {

	@Test
	public void testApply() {
		Sample sample = new Sample();
		DecimalRange parameter = new DecimalRange("foo", 100, 1000);
		
		parameter.apply(sample, 0.0);
		Assert.assertEquals(100.0, sample.getDouble("foo"));
		
		parameter.apply(sample, 1.0);
		Assert.assertEquals(1000.0, sample.getDouble("foo"));
		
		parameter.apply(sample, 0.5);
		Assert.assertEquals(550.0, sample.getDouble("foo"));
	}
	
	@Test
	public void testApplyOutOfBounds() {
		Sample sample = new Sample();
		DecimalRange parameter = new DecimalRange("foo", 100, 1000);
		
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.apply(sample, -0.001));
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.apply(sample, 1.001));
	}

}
