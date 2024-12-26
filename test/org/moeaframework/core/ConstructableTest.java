/* Copyright 2009-2025 David Hadka
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
import org.moeaframework.TestThresholds;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.constraint.LessThan;
import org.moeaframework.core.constraint.ThresholdConstraint;
import org.moeaframework.core.objective.Minimize;
import org.moeaframework.core.objective.Objective;

public class ConstructableTest {

	@Test
	public void testNoArguments() {
		Assert.assertInstanceOf(Minimize.class, Constructable.createInstance(Objective.class, "Minimize"));
		Assert.assertInstanceOf(Minimize.class, Constructable.createInstance(Objective.class, "org.moeaframework.core.objective.Minimize"));
	}
	
	@Test
	public void testArguments() {
		Assert.assertInstanceOf(LessThan.class, Constructable.createInstance(Constraint.class, "LessThan(2.0)"));
		Assert.assertInstanceOf(LessThan.class, Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2.0)"));
		Assert.assertInstanceOf(LessThan.class, Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2)"));
		Assert.assertInstanceOf(LessThan.class, Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2, 0.000001)"));
		
		LessThan constraint = (LessThan)Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2, 0.000001)");
		Assert.assertEquals(2.0, constraint.getThreshold(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.000001, constraint.getEpsilon(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testDifferentPackage() {
		Assert.assertInstanceOf(TestConstraint.class, Constructable.createInstance(Constraint.class,
				"org.moeaframework.core.ConstructableTest$TestConstraint(2.0)"));
	}
	
	@Test
	public void testString() {
		String definition = "ConstructableTest$TestConstraint(foo\\,\\ bar,2.0)";
		
		TestConstraint result = Constructable.createInstance(TestConstraint.class, definition);
		Assert.assertEquals("foo, bar", result.getName());
		Assert.assertEquals(2.0, result.getThreshold(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testUnsupported() {
		Assert.assertNull(Constructable.createInstance(Objective.class, "!Foo"));
	}
	
	@Test(expected = FrameworkException.class)
	public void testInvalidArgument() {
		Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(error)");
	}
	
	@Test(expected = FrameworkException.class)
	public void testMissingArgument() {
		Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan()");
	}
	
	@Test(expected = FrameworkException.class)
	public void testAdditionalArgument() {
		Constructable.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(foo, 0.000001, 5.0, bar)");
	}
	
	@Test
	public void testCreateDefinition() {
		Assert.assertEquals("Minimize", Constructable.createDefinition(Objective.class, Minimize.class));
		Assert.assertEquals("LessThan(2.0)", Constructable.createDefinition(Constraint.class, LessThan.class, 2.0));
		Assert.assertEquals("org.moeaframework.core.ConstructableTest$TestConstraint(2.0)",
				Constructable.createDefinition(Constraint.class, TestConstraint.class, 2.0));
	}
	
	@Test
	public void testCreateUnsupportedDefinition() {
		Assert.assertEquals("!Minimize", Constructable.createUnsupportedDefinition(Objective.class, Minimize.class));
	}
	
	public static class TestConstraint extends ThresholdConstraint {

		private static final long serialVersionUID = 4343261347377782831L;

		public TestConstraint(double threshold) {
			super(threshold);
		}
		
		public TestConstraint(String name, double threshold) {
			super(name, threshold);
		}

		@Override
		public double getMagnitudeOfViolation() {
			return value;
		}

		@Override
		public Constraint copy() {
			return new TestConstraint(name, threshold);
		}
		
	}
	
}
