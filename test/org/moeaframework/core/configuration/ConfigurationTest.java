package org.moeaframework.core.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

public class ConfigurationTest {
	
	@Test
	public void testDefaultValues() {
		TestConfigurable configurable = new TestConfigurable();
		TypedProperties properties = configurable.getConfiguration();

		Assert.assertTrue(properties.contains("byteValue"));
		Assert.assertTrue(properties.contains("shortValue"));
		Assert.assertTrue(properties.contains("intValue"));
		Assert.assertTrue(properties.contains("longValue"));
		Assert.assertTrue(properties.contains("floatValue"));
		Assert.assertTrue(properties.contains("doubleValue"));
		Assert.assertTrue(properties.contains("booleanValue"));
		
		// these all default to null and will not show up
		Assert.assertFalse(properties.contains("enumValue"));
		Assert.assertFalse(properties.contains("stringValue"));
		Assert.assertFalse(properties.contains("mutation"));
		Assert.assertFalse(properties.contains("variation"));
	}
	
	@Test
	public void testGetAndSetValues() {
		TestConfigurable configurable = new TestConfigurable();
		
		TypedProperties properties = new TypedProperties();
		properties.setByte("byteValue", (byte)5);
		properties.setShort("shortValue", (short)5);
		properties.setInt("intValue", 5);
		properties.setLong("longValue", 5L);
		properties.setFloat("floatValue", 5.0f);
		properties.setDouble("doubleValue", 5.0);
		properties.setBoolean("booleanValue", true);
		properties.setEnum("enumValue", FooBar.BAR);
		properties.setString("stringValue", "foo");
		properties.setString("mutation", "pm");
		properties.setString("variation", "sbx+pm");
		
		ConfigurationUtils.applyConfiguration(properties, configurable, new MockRealProblem());
		
		TypedProperties newProperties = configurable.getConfiguration();
		Assert.assertEquals((byte)5, newProperties.getByte("byteValue", (byte)0));
		Assert.assertEquals((short)5, newProperties.getShort("shortValue", (short)0));
		Assert.assertEquals(5, newProperties.getInt("intValue", 0));
		Assert.assertEquals(5L, newProperties.getLong("longValue", 0L));
		Assert.assertEquals(5.0f, newProperties.getFloat("floatValue", 0.0f), Settings.EPS);
		Assert.assertEquals(5.0, newProperties.getDouble("doubleValue", 0.0), Settings.EPS);
		Assert.assertEquals(true, newProperties.getBoolean("booleanValue", false));
		Assert.assertEquals(FooBar.BAR, newProperties.getEnum("enumValue", FooBar.class));
		Assert.assertEquals("foo", newProperties.getString("stringValue", null));
		Assert.assertEquals("pm", newProperties.getString("mutation", null));
		Assert.assertEquals("sbx+pm", newProperties.getString("variation", null));
	}
	
	@Test
	public void testDoubleToIntConversion() {
		TestConfigurable configurable = new TestConfigurable();
		
		TypedProperties properties = new TypedProperties();
		properties.setDouble("intValue", 5.5);

		ConfigurationUtils.applyConfiguration(properties, configurable, new MockRealProblem());
		
		TypedProperties newProperties = configurable.getConfiguration();
		Assert.assertEquals(5, newProperties.getInt("intValue", 0));
	}
	
	@Test
	public void testNestedProperties() {
		TestConfigurable configurable = new TestConfigurable();
		
		TypedProperties properties = new TypedProperties();
		properties.setString("mutation", "pm");
		
		ConfigurationUtils.applyConfiguration(properties, configurable, new MockRealProblem());
		
		TypedProperties newProperties = configurable.getConfiguration();
		Assert.assertTrue(newProperties.contains("pm.rate"));
		Assert.assertTrue(newProperties.contains("pm.distributionIndex"));
	}
	
	@Test
	public void testNaming() {
		TestNaming configurable = new TestNaming();
		
		TypedProperties properties = configurable.getConfiguration();
		Assert.assertTrue(properties.contains("foo"));
		Assert.assertFalse(properties.contains("value"));
		Assert.assertFalse(properties.contains("bar"));
		
		// setting proeprty name works
		properties.setInt("foo", 5);
		configurable.applyConfiguration(properties);
		Assert.assertTrue(properties.getUnaccessedProperties().isEmpty());
		
		properties = configurable.getConfiguration();
		Assert.assertTrue(properties.contains("foo"));
		Assert.assertEquals(5, properties.getInt("foo", 0));
		Assert.assertFalse(properties.contains("value"));
		Assert.assertFalse(properties.contains("bar"));
		
		// setting alternate name works
		properties.clear();
		properties.setInt("bar", 10);
		configurable.applyConfiguration(properties);
		Assert.assertTrue(properties.getUnaccessedProperties().isEmpty());
		
		properties = configurable.getConfiguration();
		Assert.assertTrue(properties.contains("foo"));
		Assert.assertEquals(10, properties.getInt("foo", 0));
		Assert.assertFalse(properties.contains("value"));
		Assert.assertFalse(properties.contains("bar"));
		
		// setting field name does not work (since name is overridden)
		properties.clear();
		properties.setInt("value", 15);
		configurable.applyConfiguration(properties);
		Assert.assertFalse(properties.getUnaccessedProperties().isEmpty());
		
		properties = configurable.getConfiguration();
		Assert.assertTrue(properties.contains("foo"));
		Assert.assertEquals(10, properties.getInt("foo", 0));
		Assert.assertFalse(properties.contains("value"));
		Assert.assertFalse(properties.contains("bar"));
		
		// setting both property and alternate name - alternate name ignored
		properties.clear();
		properties.setInt("foo", 20);
		properties.setInt("bar", 25);
		configurable.applyConfiguration(properties);
		Assert.assertFalse(properties.getUnaccessedProperties().isEmpty());
		
		properties = configurable.getConfiguration();
		Assert.assertTrue(properties.contains("foo"));
		Assert.assertEquals(20, properties.getInt("foo", 0));
		Assert.assertFalse(properties.contains("value"));
		Assert.assertFalse(properties.contains("bar"));
	}
	
	enum FooBar {
		FOO,
		BAR
	}
	
	public class TestConfigurable implements Configurable {
		
		private byte byteValue;
		
		private short shortValue;
		
		private int intValue;
		
		private long longValue;
		
		private float floatValue;
		
		private double doubleValue;
		
		private boolean booleanValue;
		
		private FooBar enumValue;
		
		private String stringValue;
		
		private Mutation mutation;
		
		private Variation variation;

		public byte getByteValue() {
			return byteValue;
		}

		@Property
		public void setByteValue(byte byteValue) {
			this.byteValue = byteValue;
		}

		public short getShortValue() {
			return shortValue;
		}

		@Property
		public void setShortValue(short shortValue) {
			this.shortValue = shortValue;
		}

		public int getIntValue() {
			return intValue;
		}

		@Property
		public void setIntValue(int intValue) {
			this.intValue = intValue;
		}
		
		public long getLongValue() {
			return longValue;
		}

		@Property
		public void setLongValue(long longValue) {
			this.longValue = longValue;
		}

		public double getDoubleValue() {
			return doubleValue;
		}

		public float getFloatValue() {
			return floatValue;
		}

		@Property
		public void setFloatValue(float floatValue) {
			this.floatValue = floatValue;
		}

		@Property
		public void setDoubleValue(double doubleValue) {
			this.doubleValue = doubleValue;
		}

		public boolean isBooleanValue() {
			return booleanValue;
		}

		@Property
		public void setBooleanValue(boolean booleanValue) {
			this.booleanValue = booleanValue;
		}

		public String getStringValue() {
			return stringValue;
		}

		public FooBar getEnumValue() {
			return enumValue;
		}

		@Property
		public void setEnumValue(FooBar enumValue) {
			this.enumValue = enumValue;
		}

		@Property
		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}

		public Mutation getMutation() {
			return mutation;
		}

		@Property
		public void setMutation(Mutation mutation) {
			this.mutation = mutation;
		}

		public Variation getVariation() {
			return variation;
		}

		@Property
		public void setVariation(Variation variation) {
			this.variation = variation;
		}
		
	}

	public class TestNaming implements Configurable {

		private int value;

		public int getValue() {
			return value;
		}

		@Property(value="foo", synonym="bar")
		public void setValue(int value) {
			this.value = value;
		}

	}
	
}
