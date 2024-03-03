package org.moeaframework.util.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class NumberFormatterTest {
	
	@Test
	public void testDefaultSettings() {
		NumberFormatter formatter = new NumberFormatter();
		
		Assert.assertEquals("3", formatter.format(3));
		Assert.assertEquals("3.140000", formatter.format(3.14));
		Assert.assertEquals("-3", formatter.format(-3));
		Assert.assertEquals("-3.140000", formatter.format(-3.14));
		Assert.assertEquals("1000", formatter.format(1000));
	}
	
	@Test
	public void testWidth() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setWidth(10);
		
		Assert.assertEquals("         3", formatter.format(3));
		Assert.assertEquals("  3.140000", formatter.format(3.14));
		Assert.assertEquals("        -3", formatter.format(-3));
		Assert.assertEquals(" -3.140000", formatter.format(-3.14));
	}
	
	@Test
	public void testPrecision() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setPrecision(1);
		
		Assert.assertEquals("3", formatter.format(3));
		Assert.assertEquals("3.1", formatter.format(3.14));
		Assert.assertEquals("-3", formatter.format(-3));
		Assert.assertEquals("-3.1", formatter.format(-3.14));
	}
	
	@Test
	public void testScientificNotation() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setPrecision(2);
		formatter.setScientificNotation(true);
		
		Assert.assertEquals("3", formatter.format(3));
		Assert.assertEquals("3.14e+00", formatter.format(3.14));
		Assert.assertEquals("-3", formatter.format(-3));
		Assert.assertEquals("-3.14e+00", formatter.format(-3.14));
	}
	
	@Test
	public void testSign() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setLeadingSpaceForSign(true);
		
		Assert.assertEquals(" 3", formatter.format(3));
		Assert.assertEquals(" 3.140000", formatter.format(3.14));
		Assert.assertEquals("-3", formatter.format(-3));
		Assert.assertEquals("-3.140000", formatter.format(-3.14));
	}
	
	@Test
	public void testGroupings() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setLocaleSpecificGroupings(true);
		
		Assert.assertEquals("1,000", formatter.format(1000));
	}
	
	@Test
	public void testLocale() {
		NumberFormatter formatter = new NumberFormatter();
		formatter.setLocale(Locale.GERMANY);
		formatter.setLocaleSpecificGroupings(true);
		
		Assert.assertEquals("1.000", formatter.format(1000));
	}
	
	@Test
	public void testGetAndSetDefault() {
		Assert.assertNotNull(NumberFormatter.getDefault());
		Assert.assertEquals(NumberFormatter.getDefault(), NumberFormatter.getDefault());
		
		NumberFormatter.getDefault().setPrecision(10);
		Assert.assertEquals(10, NumberFormatter.getDefault().getPrecision());
		
		NumberFormatter customFormatter = new NumberFormatter();
		NumberFormatter.setDefault(customFormatter);
		Assert.assertEquals(customFormatter, NumberFormatter.getDefault());
		
		NumberFormatter.setDefault(null);
		Assert.assertNotNull(NumberFormatter.getDefault());
		Assert.assertNotEquals(10, NumberFormatter.getDefault().getPrecision());
	}
	
	@Test
	public void testArrays() {
		NumberFormatter formatter = new NumberFormatter();
		
		Assert.assertEquals("[]", formatter.format(new int[0]));
		Assert.assertEquals("[]", formatter.format(new double[0]));
		
		Assert.assertEquals("[3, 5]", formatter.format(new int[] { 3, 5 }));
		Assert.assertEquals("[3.140000, -3.140000]", formatter.format(new double[] { 3.14, -3.14 }));
	}
	
	@Test
	public void testList() {
		NumberFormatter formatter = new NumberFormatter();
		List<Number> values = new ArrayList<Number>();
		
		Assert.assertEquals("[]", formatter.format(values));
		
		values.add(3.14);
		values.add(1000);
		Assert.assertEquals("[3.140000, 1000]", formatter.format(values));
	}

}
