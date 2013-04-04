package org.moeaframework.core;

import org.junit.Test;

import junit.framework.Assert;

/**
 * Tests the {@link Objective} class.
 */
public class ObjectiveTest {

	/**
	 * Tests that the constructor correctly initializes the internal state.
	 */
	@Test
	public void testConstructor() {
		Objective objective1 = new Objective();
		Assert.assertEquals(Direction.MINIMIZE, objective1.getDirection());
		Assert.assertTrue(Double.isNaN(objective1.getValue()));
		
		Objective objective2 = new Objective(Direction.MAXIMIZE);
		Assert.assertEquals(Direction.MAXIMIZE, objective2.getDirection());
		Assert.assertTrue(Double.isNaN(objective2.getValue()));
	}
	
	/**
	 * Tests that the objective value getters and setters work correctly.
	 */
	@Test
	public void testValue() {
		Objective objective1 = new Objective();
		objective1.setValue(100.0);
		Assert.assertEquals(100.0, objective1.getValue(), Settings.EPS);
		objective1.setValue(-50.0);
		Assert.assertEquals(-50.0, objective1.getValue(), Settings.EPS);
		
		Objective objective2 = new Objective(Direction.MAXIMIZE);
		objective2.setValue(100.0);
		Assert.assertEquals(100.0, objective2.getValue(), Settings.EPS);
		objective2.setValue(-50.0);
		Assert.assertEquals(-50.0, objective2.getValue(), Settings.EPS);
	}
	
	/**
	 * Tests that the canonical value is correctly negated for maximized
	 * objective.
	 */
	@Test
	public void testCanonicalValue() {
		Objective objective1 = new Objective();
		objective1.setValue(50.0);
		Assert.assertEquals(50.0, objective1.getValue(), Settings.EPS);
		Assert.assertEquals(50.0, objective1.getCanonicalValue(), Settings.EPS);
		objective1.setValue(-50.0);
		Assert.assertEquals(-50.0, objective1.getValue(), Settings.EPS);
		Assert.assertEquals(-50.0, objective1.getCanonicalValue(), Settings.EPS);
		
		Objective objective2 = new Objective(Direction.MAXIMIZE);
		objective2.setValue(50.0);
		Assert.assertEquals(50.0, objective2.getValue(), Settings.EPS);
		Assert.assertEquals(-50.0, objective2.getCanonicalValue(), Settings.EPS);
		objective2.setValue(-50.0);
		Assert.assertEquals(-50.0, objective2.getValue(), Settings.EPS);
		Assert.assertEquals(50.0, objective2.getCanonicalValue(), Settings.EPS);
	}
	
	/**
	 * Tests that the copy method and copy constructor produce an independent
	 * copy of the original objective.
	 */
	@Test
	public void testCopy() {
		Objective objective1 = new Objective(Direction.MAXIMIZE);
		objective1.setValue(50.0);
		
		Objective objective2 = objective1.copy();
		Assert.assertFalse(objective1 == objective2);
		Assert.assertEquals(Direction.MAXIMIZE, objective2.getDirection());
		Assert.assertEquals(50.0, objective2.getValue());
		
		objective1.setValue(100.0);
		Assert.assertEquals(Direction.MAXIMIZE, objective2.getDirection());
		Assert.assertEquals(50.0, objective2.getValue());
		
		objective2.setValue(0.0);
		Assert.assertEquals(Direction.MAXIMIZE, objective1.getDirection());
		Assert.assertEquals(100.0, objective1.getValue(), Settings.EPS);
	}
	
}
