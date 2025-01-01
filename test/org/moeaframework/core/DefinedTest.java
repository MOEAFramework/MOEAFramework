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

public class DefinedTest {

	@Test
	public void testNoArguments() {
		Assert.assertInstanceOf(Minimize.class, Defined.createInstance(Objective.class, "Minimize"));
		Assert.assertInstanceOf(Minimize.class, Defined.createInstance(Objective.class, "Minimize()"));
		Assert.assertInstanceOf(Minimize.class, Defined.createInstance(Objective.class, "org.moeaframework.core.objective.Minimize"));
		Assert.assertInstanceOf(Minimize.class, Defined.createInstance(Objective.class, "org.moeaframework.core.objective.Minimize()"));
	}
	
	@Test
	public void testArguments() {
		Assert.assertInstanceOf(LessThan.class, Defined.createInstance(Constraint.class, "LessThan(2.0)"));
		Assert.assertInstanceOf(LessThan.class, Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2.0)"));
		Assert.assertInstanceOf(LessThan.class, Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2)"));
		Assert.assertInstanceOf(LessThan.class, Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(2, 0.000001)"));
		
		LessThan constraint = (LessThan)Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(\"foo\", 2, 0.000001)");
		Assert.assertEquals("foo", constraint.getName());
		Assert.assertEquals(2.0, constraint.getThreshold(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.000001, constraint.getEpsilon(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = FrameworkException.class)
	public void testUnquotedString() {
		Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(foo, 2, 0.000001)");
	}
	
	@Test(expected = FrameworkException.class)
	public void testInvalidArgument() {
		Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(\"error\")");
	}
	
	@Test(expected = FrameworkException.class)
	public void testMissingArgument() {
		Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan()");
	}
	
	@Test(expected = FrameworkException.class)
	public void testAdditionalArgument() {
		Defined.createInstance(Constraint.class, "org.moeaframework.core.constraint.LessThan(\"foo\", 0.000001, 5.0, bar)");
	}
	
	@Test(expected = FrameworkException.class)
	public void testUnrecognizedClassName() {
		Defined.createInstance(Objective.class, "Foo()");
	}
	
	@Test(expected = FrameworkException.class)
	public void testMissingCloseParenthesis() {
		Defined.createInstance(Objective.class, "Minimize(");
	}
	
	@Test(expected = FrameworkException.class)
	public void testMissingOpenParenthesis() {
		Defined.createInstance(Objective.class, "Minimize)");
	}
	
	@Test(expected = FrameworkException.class)
	public void testMissingCloseParenthesisWithArgs() {
		Defined.createInstance(Objective.class, "LessThan(\"foo\",");
	}
	
	@Test
	public void testCreateDefinition() {
		Assert.assertEquals("Minimize", Defined.createDefinition(Objective.class, Minimize.class));
		Assert.assertEquals("LessThan(2.0)", Defined.createDefinition(Constraint.class, LessThan.class, 2.0));
		Assert.assertEquals("org.moeaframework.core.DefinedTest$TestNestedConstraint(2.0)",
				Defined.createDefinition(Constraint.class, TestNestedConstraint.class, 2.0));
	}
	
	@Test
	public void testUnsupportedDefinition() {
		Assert.assertEquals("!Minimize", Defined.createUnsupportedDefinition(Objective.class, Minimize.class));
		Assert.assertNull(Defined.createInstance(Objective.class, "!Minimize"));
	}
	
	@Test
	public void testDifferentPackage() {
		Assert.assertInstanceOf(TestNestedConstraint.class, Defined.createInstance(Constraint.class,
				"org.moeaframework.core.DefinedTest$TestNestedConstraint(2.0)"));
	}
	
	public static class TestNestedConstraint extends ThresholdConstraint {

		private static final long serialVersionUID = 4343261347377782831L;

		public TestNestedConstraint(double threshold) {
			super(threshold);
		}
		
		public TestNestedConstraint(String name, double threshold) {
			super(name, threshold);
		}

		@Override
		public double getMagnitudeOfViolation() {
			return value;
		}

		@Override
		public Constraint copy() {
			return new TestNestedConstraint(name, threshold);
		}
		
	}
	
}
