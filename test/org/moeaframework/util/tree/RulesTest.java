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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.PRNG;

/**
 * Tests the {@link Rules} class.
 */
public class RulesTest {
	
	private int SAMPLES = 100;
	
	@Test(expected = NoValidNodeException.class)
	public void testFullWithMissingNode1() {
		Rules rules = new Rules();
		rules.add(new Add());
		rules.buildTreeFull(String.class, 5);
	}
	
	@Test(expected = UnsatisfiedArgumentException.class)
	public void testFullWithMissingNode2() {
		Rules rules = new Rules();
		rules.add(new Add());
		rules.buildTreeFull(Number.class, 5);
	}

	@Test(expected = NoValidNodeException.class)
	public void testFullWithMissingNode3() {
		Rules rules = new Rules();
		rules.add(new Constant(5.0));
		rules.buildTreeFull(String.class, 5);
	}
	
	@Test(expected = NoValidNodeException.class)
	public void testgrowWithMissingNode1() {
		Rules rules = new Rules();
		rules.add(new Add());
		rules.buildTreeGrow(String.class, 5);
	}
	
	@Test(expected = UnsatisfiedArgumentException.class)
	public void testGrowWithMissingNode2() {
		Rules rules = new Rules();
		rules.add(new Add());
		rules.buildTreeGrow(Number.class, 5);
	}
	
	@Test(expected = NoValidNodeException.class)
	public void testGrowWithMissingNode3() {
		Rules rules = new Rules();
		rules.add(new Constant(5.0));
		rules.buildTreeGrow(String.class, 5);
	}
	
	@Test
	public void testGrowType() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		for (int i = 0; i < SAMPLES; i++) {
			int depth = PRNG.nextInt(2, 10);
			Node node = rules.buildTreeGrow(Number.class, depth);
			Assert.assertTrue(node.isValid());
			Assert.assertTrue(node.getMaximumHeight() <= depth);
		}
	}
	
	@Test
	public void testFullType() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		for (int i = 0; i < SAMPLES; i++) {
			int depth = PRNG.nextInt(2, 10);
			Node node = rules.buildTreeFull(Number.class, depth);
			Assert.assertTrue(node.isValid());
			Assert.assertTrue(node.getMaximumHeight() == depth);
			Assert.assertTrue(node.getMinimumHeight() == depth);
		}
	}
	
	@Test
	public void testGrowScaffolding() {
		Node scaffolding = new IfElse().setArgument(0, new Or());
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		for (int i = 0; i < SAMPLES; i++) {
			int depth = PRNG.nextInt(2, 10);
			Node node = rules.buildTreeGrow(scaffolding, depth);
			Assert.assertTrue(node.getClass().equals(scaffolding.getClass()));
			Assert.assertTrue(node.isValid());
			Assert.assertTrue(node.getMaximumHeight() <= depth);
		}
	}
	
	@Test
	public void testFullScaffolding() {
		Node scaffolding = new IfElse().setArgument(0, new Or());
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		for (int i = 0; i < SAMPLES; i++) {
			int depth = PRNG.nextInt(2, 10);
			Node node = rules.buildTreeFull(scaffolding, depth);
			Assert.assertTrue(node.getClass().equals(scaffolding.getClass()));
			Assert.assertTrue(node.isValid());
			Assert.assertTrue(node.getMaximumHeight() == depth);
			Assert.assertTrue(node.getMinimumHeight() == depth);
		}
	}
	
	@Test
	public void testListAvailableMutations() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		for (int i = 0; i < SAMPLES; i++) {
			Node node = PRNG.nextItem(rules.getAvailableNodes());
	
			for (Node mutation : rules.listAvailableMutations(node)) {
				Assert.assertTrue(node.getReturnType().isAssignableFrom(
						mutation.getReturnType()));
				Assert.assertEquals(node.getNumberOfArguments(),
						mutation.getNumberOfArguments());
				
				for (int j = 0; j < node.getNumberOfArguments(); j++) {
					Assert.assertTrue(node.getArgumentType(j).isAssignableFrom(
							mutation.getArgumentType(j)));
				}
			}
		}
	}
	
	@Test
	public void testListAvailableCrossoverNodes() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		Class<?>[] types = new Class<?>[] { Number.class, Boolean.class,
				Object.class, Void.class };
		
		for (int i = 0; i < SAMPLES; i++) {
			Class<?> type = types[PRNG.nextInt(types.length)];
			Node tree = rules.buildTreeFull(Number.class, 10);
			List<Node> nodes = rules.listAvailableCrossoverNodes(tree, type);
			
			for (Node crossover : nodes) {
				Assert.assertTrue(type.isAssignableFrom(
						crossover.getReturnType()));
			}
		}
	}

}
