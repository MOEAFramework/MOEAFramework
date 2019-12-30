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
package org.moeaframework.core.operator.program;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Rules;

/**
 * Tests the {@link PointMutation} class.
 */
public class PointMutationTest {
	
	@Test
	public void test() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		rules.setReturnType(Number.class);
		
		PointMutation mutation = new PointMutation(1.0);
		
		for (int i = 0; i < 100; i++) {
			Program program = new Program(rules);
			program.setArgument(0, rules.buildTreeFull(Number.class, 4));
			
			int fixedNodeIndex = PRNG.nextInt(program.getNumberOfNodes());
			program.getNodeAt(fixedNodeIndex).setFixed(true);

			Assert.assertTrue(program.isValid());
			Assert.assertEquals(5, program.getMinimumHeight());
			Assert.assertEquals(5, program.getMaximumHeight());
			
			Solution solution = new Solution(1, 1);
			solution.setVariable(0, program);
			
			Solution[] offspring = mutation.evolve(new Solution[] { solution });
			Assert.assertEquals(1, offspring.length);
			
			Program result = (Program)offspring[0].getVariable(0);
			
			Assert.assertTrue(result.isValid());
			Assert.assertEquals(5, result.getMinimumHeight());
			Assert.assertEquals(5, result.getMaximumHeight());
			Assert.assertEquals(program.getNodeAt(fixedNodeIndex).getClass(),
					result.getNodeAt(fixedNodeIndex).getClass());
		}
	}

}