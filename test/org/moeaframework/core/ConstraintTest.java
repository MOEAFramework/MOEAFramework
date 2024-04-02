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
package org.moeaframework.core;

import org.junit.Test;
import org.moeaframework.Assert;

public class ConstraintTest {
	
	// Note: A number of these test cases provide a relatively large epsilon value (0.1), such that two numbers are
	// considered equal if their difference is smaller.  In practice, epsilon should be a very small number near
	// machine precision.
	
	@Test
	public void testEqual() {
		Assert.assertEquals(0.0, Constraint.equal(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.equal(5.0, 5.1), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.equal(5.0, 4.9), Settings.EPS);
		
		Assert.assertEquals(0.0, Constraint.equal(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.equal(5.0, 5.1, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.equal(5.0, 4.9, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.equal(5.0, 5.2, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.equal(5.0, 4.8, 0.1), Settings.EPS);
	}
	
	@Test
	public void testNotEqual() {
		Assert.assertEquals(1.0, Constraint.notEqual(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.notEqual(5.0, 5.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.notEqual(5.0, 4.9), Settings.EPS);
		
		Assert.assertEquals(1.0, Constraint.notEqual(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(1.0, Constraint.notEqual(5.0, 5.1, 0.1), Settings.EPS);
		Assert.assertEquals(1.0, Constraint.notEqual(5.0, 4.9, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.notEqual(5.0, 5.2, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.notEqual(5.0, 4.8, 0.1), Settings.EPS);
	}
	
	@Test
	public void testLessThanOrEqual() {
		Assert.assertEquals(0.0, Constraint.lessThanOrEqual(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.lessThanOrEqual(5.1, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.lessThanOrEqual(4.9, 5.0), Settings.EPS);
		
		Assert.assertEquals(0.0, Constraint.lessThanOrEqual(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.lessThanOrEqual(5.1, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.lessThanOrEqual(4.9, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.lessThanOrEqual(5.2, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.lessThanOrEqual(4.8, 5.0, 0.1), Settings.EPS);
	}
	
	@Test
	public void testGreaterThanOrEqual() {
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.1, 5.0), Settings.EPS);
		Assert.assertEquals(-0.1, Constraint.greaterThanOrEqual(4.9, 5.0), Settings.EPS);
		
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.1, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(4.9, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.2, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(-0.2, Constraint.greaterThanOrEqual(4.8, 5.0, 0.1), Settings.EPS);
	}
	
	@Test
	public void testLessThan() {
		Assert.assertEquals(Math.nextUp(0.0), Constraint.lessThan(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.lessThan(5.1, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.lessThan(4.9, 5.0), Settings.EPS);
		
		Assert.assertEquals(Math.nextUp(0.0), Constraint.lessThan(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.lessThan(5.1, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.lessThan(4.9, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.lessThan(5.2, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.lessThan(4.8, 5.0, 0.1), Settings.EPS);
	}
	
	@Test
	public void testGreaterThan() {
		Assert.assertEquals(Math.nextDown(0.0), Constraint.greaterThan(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThan(5.1, 5.0), Settings.EPS);
		Assert.assertEquals(-0.1, Constraint.greaterThan(4.9, 5.0), Settings.EPS);
		
		Assert.assertEquals(Math.nextDown(0.0), Constraint.greaterThan(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(-0.1, Constraint.greaterThan(5.1, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(-0.1, Constraint.greaterThan(4.9, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThan(5.2, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(-0.2, Constraint.greaterThan(4.8, 5.0, 0.1), Settings.EPS);
	}
	
	@Test
	public void testBetween() {
		Assert.assertEquals(0.0, Constraint.between(5.0, 5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.between(4.0, 5.0, 6.0), Settings.EPS);
		Assert.assertEquals(-0.1, Constraint.between(4.0, 3.9, 6.0), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.between(4.0, 6.1, 6.0), Settings.EPS);
		
		Assert.assertEquals(0.0, Constraint.between(5.0, 5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.between(4.0, 3.91, 6.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.between(4.0, 6.09, 6.0, 0.1), Settings.EPS);
		Assert.assertEquals(-0.2, Constraint.between(4.0, 3.8, 6.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.between(4.0, 6.2, 6.0, 0.1), Settings.EPS);
	}
	
	@Test
	public void testOutside() {
		Assert.assertEquals(Math.nextUp(0.0), Constraint.outside(5.0, 5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.5, Constraint.outside(4.0, 4.5, 6.0), Settings.EPS);
		Assert.assertEquals(-0.5, Constraint.outside(4.0, 5.5, 6.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.outside(4.0, 3.9, 6.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.outside(4.0, 6.1, 6.0), Settings.EPS);
		
		Assert.assertEquals(Math.nextUp(0.0), Constraint.outside(5.0, 5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.09, Constraint.outside(4.0, 3.91, 6.0, 0.1), Settings.EPS);
		Assert.assertEquals(-0.09, Constraint.outside(4.0, 6.09, 6.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.outside(4.0, 3.8, 6.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.outside(4.0, 6.2, 6.0, 0.1), Settings.EPS);
	}

}
