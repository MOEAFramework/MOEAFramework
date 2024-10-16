/* Copyright 2009-2024 David Hadka
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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.AbstractProgramOperatorTest;
import org.moeaframework.core.variable.Program;

public class PointMutationTest extends AbstractProgramOperatorTest<PointMutation> {
	
	@Override
	public PointMutation createInstance() {
		return new PointMutation(1.0);
	}
	
	@Test
	public void test() {
		PointMutation mutation = createInstance();
		
		for (int i = 0; i < 100; i++) {
			Program program = createTestVariable();
			program.setBody(program.getRules().buildTreeFull(Number.class, 4));
			
			int fixedNodeIndex = PRNG.nextInt(program.getBody().getNumberOfNodes());
			program.getBody().getNodeAt(fixedNodeIndex).setFixed(true);

			Assert.assertTrue(program.getBody().isValid());
			Assert.assertEquals(4, program.getBody().getMinimumHeight());
			Assert.assertEquals(4, program.getBody().getMaximumHeight());
			
			Solution solution = new Solution(1, 1);
			solution.setVariable(0, program);
			
			Solution[] offspring = mutation.evolve(new Solution[] { solution });
			Assert.assertEquals(1, offspring.length);
			
			Program result = (Program)offspring[0].getVariable(0);
			
			Assert.assertTrue(result.getBody().isValid());
			Assert.assertEquals(4, result.getBody().getMinimumHeight());
			Assert.assertEquals(4, result.getBody().getMaximumHeight());
			Assert.assertEquals(program.getBody().getNodeAt(fixedNodeIndex).getClass(),
					result.getBody().getNodeAt(fixedNodeIndex).getClass());
		}
	}

}