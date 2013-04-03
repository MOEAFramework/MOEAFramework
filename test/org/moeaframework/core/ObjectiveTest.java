package org.moeaframework.core;

import org.junit.Test;

import junit.framework.Assert;

/**
 * Tests the {@link Objective} class.
 */
public class ObjectiveTest {

	@Test
	public void testConstructor1() {
		Objective objective = new Objective();
		Assert.assertEquals(Direction.MINIMIZE, objective.getDirection());
		Assert.assertTrue(Double.isNaN(objective.getValue()));
	}
	
	@Test
	public void testConstructor2() {
		Objective objective = new Objective(Direction.MAXIMIZE);
		Assert.assertEquals(Direction.MAXIMIZE, objective.getDirection());
		Assert.assertTrue(Double.isNaN(objective.getValue()));
	}
	
	@Test
	public void testDirection() {
		Objective objective = new Objective();
		objective.setDirection(Direction.MAXIMIZE);
		Assert.assertEquals(Direction.MAXIMIZE, objective.getDirection());
		
		objective.setDirection(Direction.MINIMIZE);
		Assert.assertEquals(Direction.MINIMIZE, objective.getDirection());
	}
	
	@Test
	public void testValue() {
		Objective objective = new Objective();
		objective.setValue(100.0);
		Assert.assertEquals(100.0, objective.getValue(), Settings.EPS);
		
		objective.setDirection(Direction.MAXIMIZE);
		Assert.assertEquals(100.0, objective.getValue(), Settings.EPS);
		
		objective.setValue(-50.0);
		Assert.assertEquals(-50.0, objective.getValue(), Settings.EPS);
	}
	
	@Test
	public void testCopy() {
		Objective objective1 = new Objective(Direction.MAXIMIZE);
		objective1.setValue(50.0);
		
		Objective objective2 = objective1.copy();
		Assert.assertFalse(objective1 == objective2);
		Assert.assertEquals(Direction.MAXIMIZE, objective2.getDirection());
		Assert.assertEquals(50.0, objective2.getValue());
		
		objective1.setDirection(Direction.MINIMIZE);
		objective1.setValue(100.0);
		Assert.assertEquals(Direction.MAXIMIZE, objective2.getDirection());
		Assert.assertEquals(50.0, objective2.getValue());
		
		objective2.setDirection(Direction.MAXIMIZE);
		objective2.setValue(200.0);
		Assert.assertEquals(Direction.MINIMIZE, objective1.getDirection());
		Assert.assertEquals(100.0, objective1.getValue(), Settings.EPS);
	}
	
}
