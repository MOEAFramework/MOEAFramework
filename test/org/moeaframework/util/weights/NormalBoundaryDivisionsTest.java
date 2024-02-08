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

import org.junit.Assert;
import org.junit.Test;

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
	public void test() {
		Assert.assertEquals(101, new NormalBoundaryDivisions(100).getNumberOfReferencePoints(2));
	}
	
}
