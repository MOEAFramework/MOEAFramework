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
package org.moeaframework.core.objective;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class MinimizeTest {
	
	@Test
	public void testCopy() {
		Minimize expected = Minimize.value();
		Minimize actual = expected.copy();
		
		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected.getValue(), actual.getValue(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testEquals() {
		Minimize expected = Minimize.value();
		Minimize actual = expected.copy();
		
		Assert.assertEquals(expected, actual);
		
		actual.setValue(5.0);
		Assert.assertNotEquals(expected, actual);
	}
	
	@Test
	public void testGetCanonicalValue() {
		Assert.assertEquals(1.0, Minimize.value().withValue(1.0).getCanonicalValue());
		Assert.assertEquals(-1.0, Minimize.value().withValue(-1.0).getCanonicalValue());
	}
	
	@Test
	public void testCompareTo() {
		Assert.assertEquals(-1, Minimize.value().withValue(1.0).compareTo(Minimize.value().withValue(2.0)));
		Assert.assertEquals(0, Minimize.value().withValue(1.0).compareTo(Minimize.value().withValue(1.0)));
		Assert.assertEquals(1, Minimize.value().withValue(2.0).compareTo(Minimize.value().withValue(1.0)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCompareToDifferentType() {
		Assert.assertEquals(-1, Minimize.value().withValue(1.0).compareTo(Maximize.value().withValue(2.0)));
	}
	
	@Test
	public void testNormalize() {
		Assert.assertEquals(new NormalizedObjective(null, 0.0), Minimize.value().withValue(0.0).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(null, 1.0), Minimize.value().withValue(1.0).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(null, 0.5), Minimize.value().withValue(0.5).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(null, 0.25), Minimize.value().withValue(0.5).normalize(0.0, 2.0));
	}
	
	@Test
	public void testApplyWeight() {
		Assert.assertEquals(-0.5, Minimize.value().withValue(-1.0).applyWeight(0.5));
		Assert.assertEquals(0.0, Minimize.value().withValue(0.0).applyWeight(0.5));
		Assert.assertEquals(0.5, Minimize.value().withValue(1.0).applyWeight(0.5));
	}
	
	@Test
	public void testGetEpsilonIndex() {
		Assert.assertEquals(0, Minimize.value().withValue(0.0).getEpsilonIndex(0.5));
		Assert.assertEquals(0, Minimize.value().withValue(0.1).getEpsilonIndex(0.5));
		Assert.assertEquals(1, Minimize.value().withValue(0.5).getEpsilonIndex(0.5));
		Assert.assertEquals(1, Minimize.value().withValue(0.6).getEpsilonIndex(0.5));
		Assert.assertEquals(2, Minimize.value().withValue(1.0).getEpsilonIndex(0.5));
	}
	
	@Test
	public void testGetEpsilonDistance() {
		Assert.assertEquals(0.0, Minimize.value().withValue(0.0).getEpsilonDistance(0.5));
		Assert.assertEquals(0.1, Minimize.value().withValue(0.1).getEpsilonDistance(0.5));
		Assert.assertEquals(0.0, Minimize.value().withValue(0.5).getEpsilonDistance(0.5));
		Assert.assertEquals(0.1, Minimize.value().withValue(0.6).getEpsilonDistance(0.5));
		Assert.assertEquals(0.0, Minimize.value().withValue(1.0).getEpsilonDistance(0.5));
	}
	
	@Test
	public void testDistanceTo() {
		Assert.assertEquals(0.0, Minimize.value().withValue(0.0).distanceTo(0.0));
		Assert.assertEquals(0.5, Minimize.value().withValue(0.0).distanceTo(0.5));
		Assert.assertEquals(0.5, Minimize.value().withValue(0.0).distanceTo(-0.5));
	}
	
	@Test
	public void testIdeal() {
		Assert.assertEquals(0.0, Objective.ideal(Minimize.value().withValue(0.0), Minimize.value().withValue(0.0)).getValue());
		Assert.assertEquals(0.0, Objective.ideal(Minimize.value().withValue(0.0), Minimize.value().withValue(0.5)).getValue());
		Assert.assertEquals(0.0, Objective.ideal(Minimize.value().withValue(0.5), Minimize.value().withValue(0.0)).getValue());
	}

}
