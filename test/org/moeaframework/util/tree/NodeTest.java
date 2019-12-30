/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.util.tree;

import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Node} class, specific node instances, and the methods in
 * {@link NumberArithmetic}.
 */
public class NodeTest {
	
	public static class UnusedEnvironment extends Environment {

		@Override
		public <T> T get(Class<T> type, String name) {
			Assert.fail("attempted to get environment variable");
			return null;
		}

		@Override
		public void set(String name, Object value) {
			Assert.fail("attempted to set environment variable");
		}
		
	}
	
	@Test
	public void testAbs() {
		Node node1 = new Abs()
				.setArgument(0, new Constant(-5));
		Node node2 = new Abs()
				.setArgument(0, new Constant(5));
		
		Assert.assertTrue(NumberArithmetic.equals(5,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(5,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAcos() {
		Node node = new Acos()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.acos(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAcosh() {
		Node node = new Acosh()
				.setArgument(0, new Constant(1.324));

		Assert.assertTrue(NumberArithmetic.equals(FastMath.acosh(1.324),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAdd() {
		Node node = new Add()
				.setArgument(0, new Constant(-1))
				.setArgument(1, new Constant(2.5));
		
		Assert.assertTrue(NumberArithmetic.equals(1.5, 
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAnd() {
		Node node1 = new And()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(true));
		Node node2 = new And()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(false));
		Node node3 = new And()
				.setArgument(0, new Constant(false))
				.setArgument(1, new Constant(true));
		Node node4 = new And()
				.setArgument(0, new Constant(false))
				.setArgument(1, new Constant(false));

		Assert.assertTrue((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node2.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node3.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node4.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testAsin() {
		Node node = new Asin()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.asin(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAsinh() {
		Node node = new Asinh()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(FastMath.asinh(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAtan() {
		Node node = new Atan()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.atan(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testAtanh() {
		Node node = new Atanh()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(FastMath.atanh(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testCeil() {
		Node node1 = new Ceil()
				.setArgument(0, new Constant(-5.2));
		Node node2 = new Ceil()
				.setArgument(0, new Constant(-5.0));
		
		Assert.assertTrue(NumberArithmetic.equals(-5,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(-5,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testConstant() {
		Assert.assertTrue(NumberArithmetic.equals(5,
				(Number)new Constant(5).evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(-5.2,
				(Number)new Constant(-5.2).evaluate(new UnusedEnvironment())));
		Assert.assertFalse(
				(Boolean)new Constant(false).evaluate(new UnusedEnvironment()));
		Assert.assertTrue(
				(Boolean)new Constant(true).evaluate(new UnusedEnvironment()));
		Assert.assertEquals(null, new Constant(String.class, null)
				.evaluate(new UnusedEnvironment()));
		Assert.assertEquals("test", new Constant(String.class, "test")
				.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testCos() {
		Node node = new Cos()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.cos(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testCosh() {
		Node node = new Cosh()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.cosh(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testDefineAndCall() {
		/*
		 * fact(n):
		 *   if (n <= 1) return 1;
		 *   else return n * fact(n-1);
		 */
		Define factorial = (Define)new Define("factorial", Number.class, "n", Number.class).setArgument(0, 
				new IfElse(Number.class)
						.setArgument(0, new LessThanOrEqual()
								.setArgument(0, new Get(Number.class, "n"))
								.setArgument(1, new Constant(1)))
						.setArgument(1, new Constant(1))
						.setArgument(2, new Multiply()
								.setArgument(0, new Get(Number.class, "n"))
								.setArgument(1, new Call("factorial", Number.class, "n", Number.class)
										.setArgument(0, new Subtract()
												.setArgument(0, new Get(Number.class, "n"))
												.setArgument(1, new Constant(1))))));
		
		Node program = new Sequence()
				.setArgument(0, factorial)
				.setArgument(1, new Call(factorial)
						.setArgument(0, new Constant(5)));
		
		Assert.assertTrue(NumberArithmetic.equals(120, 
				(Number)program.evaluate(new Environment())));
	}
	
	@Test
	public void testDivide() {
		Node node1 = new Divide()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(7));
		Node node2 = new Divide()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(7.0));
		
		Assert.assertTrue(NumberArithmetic.equals(5 / 7, 
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(5.0 / 7.0, 
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testEquals() {
		Node node1 = new Equals()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new Equals()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(-5));
		
		Assert.assertTrue((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node2.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testExp() {
		Node node = new Exp()
				.setArgument(0, new Constant(2.5));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.exp(2.5),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testFloor() {
		Node node1 = new Floor()
				.setArgument(0, new Constant(-5.2));
		Node node2 = new Floor()
				.setArgument(0, new Constant(-5.0));
		
		Assert.assertTrue(NumberArithmetic.equals(-6,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(-5,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testFor() {
		Node node = new For("i")
				.setArgument(0, new Constant(0))
				.setArgument(1, new Constant(5))
				.setArgument(2, new Constant(1))
				.setArgument(3, new Set(Number.class, "x")
						.setArgument(0, new Add()
								.setArgument(0, new Get(Number.class, "x"))
								.setArgument(1, new Get(Number.class, "i"))));
		
		Assert.assertTrue(NumberArithmetic.equals(10, 
				(Number)node.evaluate(new Environment())));
	}
	
	@Test
	public void testGet() {
		Node node1 = new Get(Number.class, "x");
		Node node2 = new Get(Boolean.class, "y");
		Environment environment = new Environment();
		
		Assert.assertTrue(NumberArithmetic.equals(0,
				(Number)node1.evaluate(environment)));
		Assert.assertFalse((Boolean)node2.evaluate(environment));
		
		environment.set("x", 5);
		environment.set("y", true);
		
		Assert.assertTrue(NumberArithmetic.equals(5,
				(Number)node1.evaluate(environment)));
		Assert.assertTrue((Boolean)node2.evaluate(environment));
	}
	
	@Test
	public void testGreaterThan() {
		Node node1 = new GreaterThan()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new GreaterThan()
				.setArgument(0, new Constant(4))
				.setArgument(1, new Constant(5.0));
		Node node3 = new GreaterThan()
				.setArgument(0, new Constant(6.0))
				.setArgument(1, new Constant(5));

		Assert.assertFalse((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node2.evaluate(new UnusedEnvironment()));
		Assert.assertTrue((Boolean)node3.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testGreaterThanOrEqual() {
		Node node1 = new GreaterThanOrEqual()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new GreaterThanOrEqual()
				.setArgument(0, new Constant(4))
				.setArgument(1, new Constant(5.0));
		Node node3 = new GreaterThanOrEqual()
				.setArgument(0, new Constant(6.0))
				.setArgument(1, new Constant(5));

		Assert.assertTrue((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node2.evaluate(new UnusedEnvironment()));
		Assert.assertTrue((Boolean)node3.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testIfElse() {
		Node node1 = new IfElse()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(1))
				.setArgument(2, new Constant(2));
		Node node2 = new IfElse(Number.class)
				.setArgument(0, new Constant(false))
				.setArgument(1, new Constant(1))
				.setArgument(2, new Constant(2));
		
		Assert.assertTrue(NumberArithmetic.equals(1,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(2,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testLambda() {
		Lambda lambda = new Lambda(new Multiply()
				.setArgument(0, new Get(Number.class, "x"))
				.setArgument(1, new Get(Number.class, "x")), 
				 "x", Number.class);
		
		Node node = lambda
				.setArgument(0, new Constant(5));
		
		// note the use of UnusedEnvironment, as the variable x is locally
		// scoped and should not impact the enclosing environment
		Assert.assertTrue(NumberArithmetic.equals(25,
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testLessThan() {
		Node node1 = new LessThan()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new LessThan()
				.setArgument(0, new Constant(4))
				.setArgument(1, new Constant(5.0));
		Node node3 = new LessThan()
				.setArgument(0, new Constant(6.0))
				.setArgument(1, new Constant(5));

		Assert.assertFalse((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertTrue((Boolean)node2.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node3.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testLessThanOrEqual() {
		Node node1 = new LessThanOrEqual()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new LessThanOrEqual()
				.setArgument(0, new Constant(4))
				.setArgument(1, new Constant(5.0));
		Node node3 = new LessThanOrEqual()
				.setArgument(0, new Constant(6.0))
				.setArgument(1, new Constant(5));

		Assert.assertTrue((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertTrue((Boolean)node2.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node3.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testLog() {
		Node node = new Log()
				.setArgument(0, new Constant(5));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.log(5),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testLog10() {
		Node node = new Log10()
				.setArgument(0, new Constant(5));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.log10(5),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testMax() {
		Node node1 = new Max()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new Max()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(-5));
		
		Assert.assertTrue(NumberArithmetic.equals(5, 
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(5,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testMin() {
		Node node1 = new Min()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(5.0));
		Node node2 = new Min()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(-5));
		
		Assert.assertTrue(NumberArithmetic.equals(5, 
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(-5,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testModulus() {
		Node node1 = new Modulus()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(7));
		Node node2 = new Modulus()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(7.0));
		
		Assert.assertTrue(NumberArithmetic.equals(5 % 7, 
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(5.0 % 7.0, 
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testMultiply() {
		Node node1 = new Multiply()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(7));
		Node node2 = new Multiply()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(-7.0));
		
		Assert.assertTrue(NumberArithmetic.equals(5 * 7, 
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(5.0 * -7.0, 
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testNOP() {
		Node node = new NOP();
		
		Assert.assertEquals(null, node.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testNot() {
		Node node1 = new Not()
				.setArgument(0, new Constant(false));
		Node node2 = new Not()
				.setArgument(0, new Constant(true));
		
		Assert.assertTrue((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node2.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testOr() {
		Node node1 = new Or()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(true));
		Node node2 = new Or()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(false));
		Node node3 = new Or()
				.setArgument(0, new Constant(false))
				.setArgument(1, new Constant(true));
		Node node4 = new Or()
				.setArgument(0, new Constant(false))
				.setArgument(1, new Constant(false));

		Assert.assertTrue((Boolean)node1.evaluate(new UnusedEnvironment()));
		Assert.assertTrue((Boolean)node2.evaluate(new UnusedEnvironment()));
		Assert.assertTrue((Boolean)node3.evaluate(new UnusedEnvironment()));
		Assert.assertFalse((Boolean)node4.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testPower() {
		Node node = new Power()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(7));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.pow(5, 7), 
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testRound() {
		Node node1 = new Round()
				.setArgument(0, new Constant(-5.2));
		Node node2 = new Round()
				.setArgument(0, new Constant(-5.0));
		
		Assert.assertTrue(NumberArithmetic.equals(-5,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(-5,
				(Number)node2.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testSequence() {
		Node node = new Sequence()
				.setArgument(0, new Constant(5))
				.setArgument(1, new Constant(true));
		
		Assert.assertTrue((Boolean)node.evaluate(new UnusedEnvironment()));
	}
	
	@Test
	public void testSet() {
		Node node1 = new Set(Number.class, "x")
				.setArgument(0, new Constant(5));
		Node node2 = new Set(Boolean.class, "y")
				.setArgument(0, new Constant(true));
		Environment environment = new Environment();
		
		node1.evaluate(environment);
		node2.evaluate(environment);

		Assert.assertTrue(NumberArithmetic.equals(5,
				environment.get(Number.class, "x")));
		Assert.assertTrue(environment.get(Boolean.class, "y"));
	}
	
	@Test
	public void testSign() {
		Node node1 = new Sign()
				.setArgument(0, new Constant(-5));
		Node node2 = new Sign()
				.setArgument(0, new Constant(0.5));
		Node node3 = new Sign()
				.setArgument(0, new Constant(0.0));
		
		Assert.assertTrue(NumberArithmetic.equals(-1,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(1,
				(Number)node2.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(0,
				(Number)node3.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testSin() {
		Node node = new Sin()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.sin(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testSinh() {
		Node node = new Sinh()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.sinh(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testSquare() {
		Node node = new Square()
				.setArgument(0, new Constant(5));
		
		Assert.assertTrue(NumberArithmetic.equals(25,
				(Number)node.evaluate(new Environment())));
	}
	
	@Test
	public void testSquareRoot() {
		Node node = new SquareRoot()
				.setArgument(0, new Constant(5.5));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.sqrt(5.5),
				(Number)node.evaluate(new Environment())));
	}
	
	@Test
	public void testSubtract() {
		Node node = new Subtract()
				.setArgument(0, new Constant(-1))
				.setArgument(1, new Constant(2.5));
		
		Assert.assertTrue(NumberArithmetic.equals(-3.5, 
				(Number)node.evaluate(new Environment())));
	}
	
	@Test
	public void testTan() {
		Node node = new Tan()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.tan(Math.PI / 4.0),
				(Number)node.evaluate(new Environment())));
	}
	
	@Test
	public void testTanh() {
		Node node = new Tanh()
				.setArgument(0, new Constant(Math.PI / 4.0));
		
		Assert.assertTrue(NumberArithmetic.equals(Math.tanh(Math.PI / 4.0),
				(Number)node.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testTruncate() {
		Node node1 = new Truncate(-2, 5)
				.setArgument(0, new Constant(-5));
		Node node2 = new Truncate(-2, 5)
				.setArgument(0, new Constant(5.5));
		Node node3 = new Truncate(-2, 5)
				.setArgument(0, new Constant(1.5));
		
		Assert.assertTrue(NumberArithmetic.equals(-2,
				(Number)node1.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(5,
				(Number)node2.evaluate(new UnusedEnvironment())));
		Assert.assertTrue(NumberArithmetic.equals(1.5,
				(Number)node3.evaluate(new UnusedEnvironment())));
	}
	
	@Test
	public void testWhile() {
		Node node = new While()
				.setArgument(0, new LessThan()
						.setArgument(0, new Get(Number.class, "i"))
						.setArgument(1, new Constant(5)))
				.setArgument(1, new Sequence()
						.setArgument(0, new Set(Number.class, "x")
								.setArgument(0, new Add()
										.setArgument(0, new Get(Number.class, "x"))
										.setArgument(1, new Get(Number.class, "i"))))
						.setArgument(1, new Set(Number.class, "i")
								.setArgument(0, new Add()
										.setArgument(0, new Get(Number.class, "i"))
										.setArgument(1, new Constant(1)))));
		Environment environment = new Environment();
		
		node.evaluate(environment);
		
		Assert.assertTrue(NumberArithmetic.equals(10, 
				environment.get(Number.class, "x")));
	}
	
	@Test
	public void testTreeProperties() {
		Node booleanConstant = new Constant(false);
		Node numberConstant = new Constant(3);
		
		Node lessThan = new LessThan()
				.setArgument(0, numberConstant)
				.setArgument(1, new Constant(5));
		
		Node and = new And()
				.setArgument(0, booleanConstant)
				.setArgument(1, lessThan);
		
		Assert.assertTrue(and.isValid());
		
		Assert.assertTrue(booleanConstant.isTerminal());
		Assert.assertTrue(booleanConstant.isTerminal());
		Assert.assertFalse(lessThan.isTerminal());
		Assert.assertFalse(and.isTerminal());
		
		Assert.assertEquals(5, and.size());
		Assert.assertEquals(3, lessThan.size());
		Assert.assertEquals(0, and.getDepth());
		Assert.assertEquals(1, lessThan.getDepth());
		Assert.assertEquals(1, lessThan.getMinimumHeight());
		Assert.assertEquals(1, lessThan.getMaximumHeight());
		Assert.assertEquals(1, and.getMinimumHeight());
		Assert.assertEquals(2, and.getMaximumHeight());
		
		Assert.assertEquals(5, and.getNumberOfNodes());
		Assert.assertEquals(3, and.getNumberOfNodes(Boolean.class));
		Assert.assertEquals(2, and.getNumberOfNodes(Number.class));
		Assert.assertEquals(2, and.getNumberOfFunctions());
		Assert.assertEquals(2, and.getNumberOfFunctions(Boolean.class));
		Assert.assertEquals(0, and.getNumberOfFunctions(Number.class));
		Assert.assertEquals(3, and.getNumberOfTerminals());
		Assert.assertEquals(1, and.getNumberOfTerminals(Boolean.class));
		Assert.assertEquals(2, and.getNumberOfTerminals(Number.class));
		
		Assert.assertSame(booleanConstant, and.getNodeAt(1));
		Assert.assertSame(lessThan, and.getNodeAt(Boolean.class, 2));
		Assert.assertSame(numberConstant, and.getNodeAt(Number.class, 0));
		Assert.assertSame(booleanConstant, and.getTerminalAt(0));
		Assert.assertSame(numberConstant, and.getTerminalAt(Number.class, 0));
		Assert.assertSame(lessThan, and.getFunctionAt(1));
		Assert.assertSame(lessThan, and.getFunctionAt(Boolean.class, 1));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetNodeAtInvalid() {
		Node node = new Or()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(false));
		
		node.getNodeAt(3);
	}
	
	@Test
	public void testTreeCopy() {
		Node node = new Or()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(false));
		
		Node nodeCopy = node.copyNode();
		Assert.assertTrue(nodeCopy instanceof Or);
		Assert.assertNull(nodeCopy.getParent());
		Assert.assertEquals(2, nodeCopy.getNumberOfArguments());
		Assert.assertEquals(Boolean.class, nodeCopy.getArgumentType(0));
		Assert.assertEquals(Boolean.class, nodeCopy.getArgumentType(1));
		Assert.assertNull(nodeCopy.getArgument(0));
		Assert.assertNull(nodeCopy.getArgument(1));
		
		Node treeCopy = node.copyTree();
		Assert.assertEquals(node.size(), treeCopy.size());
		Assert.assertNull(treeCopy.getParent());
		Assert.assertNotSame(node, treeCopy);
		Assert.assertNotSame(node.getArgument(0), treeCopy.getArgument(0));
		Assert.assertNotSame(node.getArgument(1), treeCopy.getArgument(1));
		Assert.assertSame(treeCopy, treeCopy.getArgument(0).getParent());
		Assert.assertSame(treeCopy, treeCopy.getArgument(1).getParent());
	}
	
	@Test
	public void testIsValid() {
		Node valid = new Or()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(false));
		
		Node invalid1 = new Or()
				.setArgument(0, new Constant(true))
				.setArgument(1, new Constant(5));

		Node invalid2 = new Or()
				.setArgument(0, new Constant(true));
		
		Assert.assertTrue(valid.isValid());
		Assert.assertFalse(invalid1.isValid());
		Assert.assertFalse(invalid2.isValid());
	}
	
	@Test
	public void testFixed() {
		Node node1 = new Constant(true);
		Node node2 = new Constant(false);
		Node node3 = new Or().setArgument(0, node1).setArgument(1, node2);
		
		Assert.assertFalse(node1.isFixed());
		Assert.assertFalse(node2.isFixed());
		Assert.assertFalse(node3.isFixed());
		
		node3.setFixed(true);
		
		Assert.assertFalse(node1.isFixed());
		Assert.assertFalse(node2.isFixed());
		Assert.assertTrue(node3.isFixed());
		
		node3.setFixedTree(true);
		
		Assert.assertTrue(node1.isFixed());
		Assert.assertTrue(node2.isFixed());
		Assert.assertTrue(node3.isFixed());
	}

}
