package org.moeaframework.analysis.collector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccumulatorTest {
	
	private Accumulator accumulator;
	
	@Before
	public void setUp() {
		accumulator = new Accumulator();
		accumulator.add("test", 5);
		accumulator.add("test", 2);
	}
	
	@After
	public void tearDown() {
		accumulator = null;
	}
	
	@Test
	public void test() {
		Assert.assertEquals(1, accumulator.keySet().size());
		Assert.assertTrue(accumulator.keySet().contains("test"));
		
		Assert.assertEquals(2, accumulator.size("test"));
		
		Assert.assertEquals(5, accumulator.get("test", 0));
		Assert.assertEquals(2, accumulator.get("test", 1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSizeIllegalKey() {
		accumulator.size("missing");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetIllegalKey() {
		accumulator.get("missing", 0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetInvalidIndex() {
		accumulator.get("test", 2);
	}

}
