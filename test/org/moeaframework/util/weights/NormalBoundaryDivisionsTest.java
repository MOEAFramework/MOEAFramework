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
package org.moeaframework.util.weights;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.util.TypedProperties;

public class NormalBoundaryDivisionsTest {
	
	@Test
	public void testEquals() {
		Assert.assertEquals(new NormalBoundaryDivisions(30), new NormalBoundaryDivisions(30));
		Assert.assertNotEquals(new NormalBoundaryDivisions(30), new NormalBoundaryDivisions(40));
		
		Assert.assertEquals(new NormalBoundaryDivisions(30), new NormalBoundaryDivisions(30, 0));
		Assert.assertNotEquals(new NormalBoundaryDivisions(30), new NormalBoundaryDivisions(30, 1));
		
		Assert.assertEquals(new NormalBoundaryDivisions(30, 1), new NormalBoundaryDivisions(30, 1));
		Assert.assertNotEquals(new NormalBoundaryDivisions(30, 1), new NormalBoundaryDivisions(30, 2));
		Assert.assertNotEquals(new NormalBoundaryDivisions(30, 1), new NormalBoundaryDivisions(31, 1));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(new NormalBoundaryDivisions(30).hashCode(), new NormalBoundaryDivisions(30).hashCode());
		Assert.assertEquals(new NormalBoundaryDivisions(30).hashCode(), new NormalBoundaryDivisions(30, 0).hashCode());
		
		// it's possible for two different objects to have the same hash, so this isn't strictly required
		Assert.assertNotEquals(new NormalBoundaryDivisions(30).hashCode(), new NormalBoundaryDivisions(40).hashCode());
	}
	
	@Test
	public void testIsTwoLayer() {
		Assert.assertFalse(new NormalBoundaryDivisions(100).isTwoLayer());
		Assert.assertTrue(new NormalBoundaryDivisions(100, 50).isTwoLayer());
	}
	
	@Test
	public void testReferencePoints() {
		// 100 choose 100
		Assert.assertEquals(1, new NormalBoundaryDivisions(100).getNumberOfReferencePoints(1));
		
		// 101 choose 100
		Assert.assertEquals(101, new NormalBoundaryDivisions(100).getNumberOfReferencePoints(2));
		
		// 101 choose 100 + 51 choose 50
		Assert.assertEquals(152, new NormalBoundaryDivisions(100, 50).getNumberOfReferencePoints(2));
	}
	
	@Test
	public void testPropertiesSingleLevel() {
		NormalBoundaryDivisions original = new NormalBoundaryDivisions(100);
		TypedProperties properties = original.toProperties();
		
		Assert.assertEquals(100, properties.getInt("divisions"));
		Assert.assertFalse(properties.contains("divisionsOuter"));
		Assert.assertFalse(properties.contains("divisionsInner"));
		
		NormalBoundaryDivisions actual = NormalBoundaryDivisions.tryFromProperties(properties);
		Assert.assertEquals(original, actual);
	}
	
	@Test
	public void testPropertiesTwoLevel() {
		NormalBoundaryDivisions original = new NormalBoundaryDivisions(100, 50);
		TypedProperties properties = original.toProperties();
		
		Assert.assertEquals(100, properties.getInt("divisionsOuter"));
		Assert.assertEquals(50, properties.getInt("divisionsInner"));
		Assert.assertFalse(properties.contains("divisions"));
		
		NormalBoundaryDivisions actual = NormalBoundaryDivisions.tryFromProperties(properties);
		Assert.assertEquals(original, actual);
	}
	
	@Test
	public void testNoPropertiesIsNull() {
		Assert.assertNull(NormalBoundaryDivisions.tryFromProperties(new TypedProperties()));
	}
	
	@Test
	public void testFractionalProperties() {
		NormalBoundaryDivisions expected = new NormalBoundaryDivisions(100);
		NormalBoundaryDivisions actual = NormalBoundaryDivisions.tryFromProperties(
				TypedProperties.of("divisions", "100.2"));
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testZeroDivisions() {
		NormalBoundaryDivisions divisions = new NormalBoundaryDivisions(0);
		
		Assert.assertEquals(1, divisions.getNumberOfReferencePoints(1));
		Assert.assertEquals(1, divisions.getNumberOfReferencePoints(2));
		Assert.assertEquals(1, divisions.getNumberOfReferencePoints(3));
	}
	
	@Test
	public void testInvalidValues() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new NormalBoundaryDivisions(-1));
		Assert.assertThrows(IllegalArgumentException.class, () -> new NormalBoundaryDivisions(100, -1));
	}
	
}
