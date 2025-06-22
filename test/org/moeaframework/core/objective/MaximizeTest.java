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

public class MaximizeTest {
	
	@Test
	public void testCopy() {
		Maximize expected = Maximize.value();
		Maximize actual = expected.copy();
		
		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected.getValue(), actual.getValue(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testEquals() {
		Maximize expected = Maximize.value();
		Assert.assertEquals(expected, expected);
		
		Maximize copy = expected.copy();
		Assert.assertEquals(expected, copy);
		
		copy.setValue(5.0);
		Assert.assertNotEquals(expected, copy);
		
		Assert.assertEquals(Maximize.value("foo"), Maximize.value("foo"));
		Assert.assertNotEquals(Maximize.value("foo"), Maximize.value("bar"));
	}
	
	@Test
	public void testGetCanonicalValue() {
		Assert.assertEquals(-1.0, Maximize.value().withValue(1.0).getCanonicalValue());
		Assert.assertEquals(1.0, Maximize.value().withValue(-1.0).getCanonicalValue());
	}
	
	@Test
	public void testCompareTo() {
		Assert.assertEquals(-1, Maximize.value().withValue(2.0).compareTo(Maximize.value().withValue(1.0)));
		Assert.assertEquals(0, Maximize.value().withValue(1.0).compareTo(Maximize.value().withValue(1.0)));
		Assert.assertEquals(1, Maximize.value().withValue(1.0).compareTo(Maximize.value().withValue(2.0)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCompareToDifferentType() {
		Assert.assertEquals(-1, Maximize.value().withValue(1.0).compareTo(Minimize.value().withValue(2.0)));
	}
	
	@Test
	public void testNormalize() {
		Assert.assertEquals(new NormalizedObjective(null, 1.0), Maximize.value().withValue(0.0).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(null, 0.0), Maximize.value().withValue(1.0).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(null, 0.5), Maximize.value().withValue(0.5).normalize(0.0, 1.0));
		Assert.assertEquals(new NormalizedObjective(null, 0.75), Maximize.value().withValue(0.5).normalize(0.0, 2.0));
	}
	
	@Test
	public void testApplyWeight() {
		Assert.assertEquals(0.5, Maximize.value().withValue(-1.0).applyWeight(0.5));
		Assert.assertEquals(0.0, Maximize.value().withValue(0.0).applyWeight(0.5));
		Assert.assertEquals(-0.5, Maximize.value().withValue(1.0).applyWeight(0.5));
	}
	
	@Test
	public void testGetEpsilonIndex() {
		Assert.assertEquals(0, Maximize.value().withValue(0.0).getEpsilonIndex(0.5));
		Assert.assertEquals(-1, Maximize.value().withValue(0.1).getEpsilonIndex(0.5));
		Assert.assertEquals(-1, Maximize.value().withValue(0.5).getEpsilonIndex(0.5));
		Assert.assertEquals(-2, Maximize.value().withValue(0.6).getEpsilonIndex(0.5));
		Assert.assertEquals(-2, Maximize.value().withValue(1.0).getEpsilonIndex(0.5));
	}
	
	@Test
	public void testGetEpsilonDistance() {
		Assert.assertEquals(0.0, Maximize.value().withValue(0.0).getEpsilonDistance(0.5));
		Assert.assertEquals(0.1, Maximize.value().withValue(0.4).getEpsilonDistance(0.5));
		Assert.assertEquals(0.0, Maximize.value().withValue(0.5).getEpsilonDistance(0.5));
		Assert.assertEquals(0.1, Maximize.value().withValue(0.9).getEpsilonDistance(0.5));
		Assert.assertEquals(0.0, Maximize.value().withValue(1.0).getEpsilonDistance(0.5));
	}
	
	@Test
	public void testDistanceTo() {
		Assert.assertEquals(0.0, Maximize.value().withValue(0.0).distanceTo(0.0));
		Assert.assertEquals(0.5, Maximize.value().withValue(0.0).distanceTo(0.5));
		Assert.assertEquals(0.5, Maximize.value().withValue(0.0).distanceTo(-0.5));
	}
	
	@Test
	public void testIdeal() {
		Assert.assertEquals(0.0, Objective.ideal(Maximize.value().withValue(0.0), Maximize.value().withValue(0.0)).getValue());
		Assert.assertEquals(0.5, Objective.ideal(Maximize.value().withValue(0.0), Maximize.value().withValue(0.5)).getValue());
		Assert.assertEquals(0.5, Objective.ideal(Maximize.value().withValue(0.5), Maximize.value().withValue(0.0)).getValue());
	}
	
	@Test
	public void testDefinition() {
		Assert.assertEquals("Maximize", new Maximize().getDefinition());
		Assert.assertEquals("Maximize(\"foo\")", new Maximize("foo").getDefinition());
	}

}
