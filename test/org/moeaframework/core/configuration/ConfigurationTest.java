/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.core.configuration;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAIII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.problem.ZDT.ZDT5;
import org.moeaframework.util.TypedProperties;

public class ConfigurationTest {
	
	@Test
	public void testEndToEnd() {
		Problem problem = new ZDT5();
		NSGAIII algorithm = new NSGAIII(problem);
		
		TypedProperties expectedProperties = new TypedProperties();
		expectedProperties.setInt("populationSize", 200);
		expectedProperties.setDouble("hux.rate", 0.8);
		expectedProperties.setDouble("bf.rate", 2.0 / 500.0);

		algorithm.applyConfiguration(expectedProperties);

		// Verify getConfiguration returns the correct values
		TypedProperties actualProperties = algorithm.getConfiguration();
		
		Assert.assertEquals("hux+bf", actualProperties.getString("operator"));
		Assert.assertEquals(expectedProperties.getInt("populationSize"), actualProperties.getInt("populationSize"));
		Assert.assertEquals(expectedProperties.getDouble("hux.rate"), actualProperties.getDouble("hux.rate"), Settings.EPS);
		Assert.assertEquals(expectedProperties.getDouble("bf.rate"), actualProperties.getDouble("bf.rate"), Settings.EPS);
		
		// Verify the actual algorithm setup
		Assert.assertEquals(expectedProperties.getInt("populationSize"), algorithm.getInitialPopulationSize());
		
		Variation variation = algorithm.getVariation();
		Assert.assertTrue(variation instanceof CompoundVariation);
		
		List<Variation> operators = ((CompoundVariation)variation).getOperators();
		Assert.assertTrue(operators.get(0) instanceof HUX);
		Assert.assertEquals(expectedProperties.getDouble("hux.rate"), ((HUX)operators.get(0)).getProbability(), Settings.EPS);
		Assert.assertTrue(operators.get(1) instanceof BitFlip);
		Assert.assertEquals(expectedProperties.getDouble("bf.rate"), ((BitFlip)operators.get(1)).getProbability(), Settings.EPS);
	}
	
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

		@Property(value="foo", alias="bar")
		public void setValue(int value) {
			this.value = value;
		}

	}
	
}
