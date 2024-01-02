package org.moeaframework.core;

import org.junit.Assert;
import org.junit.Test;

public class ConstraintTest {
	
	// Note: A number of these test cases provide a relatively large epsilon value (0.1), such that
	// two numbers are considered equal if their difference is smaller.  In practice, epsilon should
	// be a very small number near machine precision.
	
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
		Assert.assertEquals(0.1, Constraint.greaterThanOrEqual(4.9, 5.0), Settings.EPS);
		
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.1, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(4.9, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThanOrEqual(5.2, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.greaterThanOrEqual(4.8, 5.0, 0.1), Settings.EPS);
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
		Assert.assertEquals(Math.nextUp(0.0), Constraint.greaterThan(5.0, 5.0), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThan(5.1, 5.0), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.greaterThan(4.9, 5.0), Settings.EPS);
		
		Assert.assertEquals(Math.nextUp(0.0), Constraint.greaterThan(5.0, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.greaterThan(5.1, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.1, Constraint.greaterThan(4.9, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.0, Constraint.greaterThan(5.2, 5.0, 0.1), Settings.EPS);
		Assert.assertEquals(0.2, Constraint.greaterThan(4.8, 5.0, 0.1), Settings.EPS);
	}

}
