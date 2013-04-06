package org.moeaframework.core;

import org.junit.Test;

import junit.framework.Assert;

/**
 * Tests the {@link Constraint} class.
 */
public class ConstraintTest {

	/**
	 * Tests that the constructor correctly initializes the internal state.
	 */
	@Test
	public void testConstructor() {
		Constraint constraint1 = new Constraint(ConstraintType.EQUAL);
		Assert.assertEquals(ConstraintType.EQUAL, constraint1.getType());
		Assert.assertTrue(Double.isNaN(constraint1.getValue()));
		Assert.assertTrue(Double.isNaN(constraint1.getAbsoluteValue()));
		Assert.assertTrue(constraint1.isConstraintViolated());
		
		Constraint constraint2 = new Constraint(ConstraintType.LESS_THAN_OR_EQUAL);
		Assert.assertEquals(ConstraintType.LESS_THAN_OR_EQUAL, constraint2.getType());
		Assert.assertTrue(Double.isNaN(constraint2.getValue()));
		Assert.assertTrue(Double.isNaN(constraint2.getAbsoluteValue()));
		Assert.assertTrue(constraint2.isConstraintViolated());
		
		Constraint constraint3 = new Constraint(ConstraintType.GREATER_THAN_OR_EQUAL);
		Assert.assertEquals(ConstraintType.GREATER_THAN_OR_EQUAL, constraint3.getType());
		Assert.assertTrue(Double.isNaN(constraint3.getValue()));
		Assert.assertTrue(Double.isNaN(constraint3.getAbsoluteValue()));
		Assert.assertTrue(constraint3.isConstraintViolated());
	}
	
	/**
	 * Tests that the constraint value getters and setters work correctly.
	 */
	@Test
	public void testValue() {
		Constraint constraint1 = new Constraint(ConstraintType.EQUAL);
		constraint1.setValue(100.0);
		Assert.assertEquals(100.0, constraint1.getValue(), Settings.EPS);
		constraint1.setValue(-50.0);
		Assert.assertEquals(-50.0, constraint1.getValue(), Settings.EPS);
		
		Constraint constraint2 = new Constraint(ConstraintType.LESS_THAN_OR_EQUAL);
		constraint2.setValue(100.0);
		Assert.assertEquals(100.0, constraint2.getValue(), Settings.EPS);
		constraint2.setValue(-50.0);
		Assert.assertEquals(-50.0, constraint2.getValue(), Settings.EPS);
		
		Constraint constraint3 = new Constraint(ConstraintType.GREATER_THAN_OR_EQUAL);
		constraint3.setValue(100.0);
		Assert.assertEquals(100.0, constraint3.getValue(), Settings.EPS);
		constraint3.setValue(-50.0);
		Assert.assertEquals(-50.0, constraint3.getValue(), Settings.EPS);
	}
	
	/**
	 * Tests that the absolute value reports the correct value given the
	 * constraint type.
	 */
	@Test
	public void testCanonicalValue() {
		Constraint constraint1 = new Constraint(ConstraintType.EQUAL);
		constraint1.setValue(100.0);
		Assert.assertEquals(100.0, constraint1.getAbsoluteValue(), Settings.EPS);
		constraint1.setValue(-50.0);
		Assert.assertEquals(50.0, constraint1.getAbsoluteValue(), Settings.EPS);
		
		Constraint constraint2 = new Constraint(ConstraintType.LESS_THAN_OR_EQUAL);
		constraint2.setValue(100.0);
		Assert.assertEquals(100.0, constraint2.getAbsoluteValue(), Settings.EPS);
		constraint2.setValue(-50.0);
		Assert.assertEquals(0.0, constraint2.getAbsoluteValue(), Settings.EPS);
		
		Constraint constraint3 = new Constraint(ConstraintType.GREATER_THAN_OR_EQUAL);
		constraint3.setValue(100.0);
		Assert.assertEquals(0.0, constraint3.getAbsoluteValue(), Settings.EPS);
		constraint3.setValue(-50.0);
		Assert.assertEquals(50.0, constraint3.getAbsoluteValue(), Settings.EPS);
	}
	
	/**
	 * Tests that the constraint violation check works correctly given the
	 * constraint type.
	 */
	@Test
	public void testIsConstraintViolated() {
		Constraint constraint1 = new Constraint(ConstraintType.EQUAL);
		constraint1.setValue(-100.0);
		Assert.assertTrue(constraint1.isConstraintViolated());
		constraint1.setValue(0.0);
		Assert.assertFalse(constraint1.isConstraintViolated());
		constraint1.setValue(100.0);
		Assert.assertTrue(constraint1.isConstraintViolated());

		Constraint constraint2 = new Constraint(ConstraintType.LESS_THAN_OR_EQUAL);
		constraint2.setValue(-100.0);
		Assert.assertFalse(constraint2.isConstraintViolated());
		constraint2.setValue(0.0);
		Assert.assertFalse(constraint2.isConstraintViolated());
		constraint2.setValue(100.0);
		Assert.assertTrue(constraint2.isConstraintViolated());
		
		Constraint constraint3 = new Constraint(ConstraintType.GREATER_THAN_OR_EQUAL);
		constraint3.setValue(-100.0);
		Assert.assertTrue(constraint3.isConstraintViolated());
		constraint3.setValue(0.0);
		Assert.assertFalse(constraint3.isConstraintViolated());
		constraint3.setValue(100.0);
		Assert.assertFalse(constraint3.isConstraintViolated());
	}
	
	/**
	 * Tests that the copy method and copy constructor produce an independent
	 * copy of the original constraint.
	 */
	@Test
	public void testCopy() {
		Constraint constraint1 = new Constraint(ConstraintType.EQUAL);
		constraint1.setValue(50.0);
		
		Constraint constraint2 = constraint1.copy();
		Assert.assertFalse(constraint1 == constraint2);
		Assert.assertEquals(ConstraintType.EQUAL, constraint2.getType());
		Assert.assertEquals(50.0, constraint2.getValue());
		
		constraint1.setValue(100.0);
		Assert.assertEquals(ConstraintType.EQUAL, constraint2.getType());
		Assert.assertEquals(50.0, constraint2.getValue());
		
		constraint2.setValue(0.0);
		Assert.assertEquals(ConstraintType.EQUAL, constraint1.getType());
		Assert.assertEquals(100.0, constraint1.getValue(), Settings.EPS);
	}
	
}
