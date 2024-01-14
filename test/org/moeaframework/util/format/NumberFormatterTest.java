package org.moeaframework.util.format;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class NumberFormatterTest {
	
	@Test
	public void testDefault() {
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

}
