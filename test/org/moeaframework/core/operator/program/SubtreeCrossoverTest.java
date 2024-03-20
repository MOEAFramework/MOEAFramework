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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.AbstractProgramOperatorTest;
import org.moeaframework.core.variable.Program;

public class SubtreeCrossoverTest extends AbstractProgramOperatorTest<SubtreeCrossover> {
	
	@Override
	public SubtreeCrossover createInstance() {
		return new SubtreeCrossover(1.0);
	}
	
	@Test
	public void test() {
		SubtreeCrossover crossover = createInstance();
		
		for (int i = 0; i < 100; i++) {
			Program program1 = createTestVariable();
			program1.setArgument(0, program1.getRules().buildTreeFull(Number.class, 4));
			
			Program program2 = createTestVariable();
			program2.setArgument(0, program2.getRules().buildTreeFull(Number.class, 4));
			
			int fixedArgument = PRNG.nextInt(program1.getNumberOfArguments());
			program1.setFixed(true);
			program1.getArgument(fixedArgument).setFixed(true);

			Assert.assertTrue(program1.isValid());
			Assert.assertEquals(5, program1.getMinimumHeight());
			Assert.assertEquals(5, program1.getMaximumHeight());
			Assert.assertTrue(program2.isValid());
			Assert.assertEquals(5, program2.getMinimumHeight());
			Assert.assertEquals(5, program2.getMaximumHeight());
			
			Solution solution1 = new Solution(1, 1);
			solution1.setVariable(0, program1);
			
			Solution solution2 = new Solution(1, 1);
			solution2.setVariable(0, program2);
			
			Solution[] offspring = crossover.evolve(new Solution[] { solution1, solution2 });
			Assert.assertEquals(1, offspring.length);
			
			Program result = (Program)offspring[0].getVariable(0);
			
			Assert.assertTrue(result.isValid());
			Assert.assertTrue(result.getMaximumHeight() <= 5);
			Assert.assertEquals(program1.getArgument(fixedArgument).getClass(),
					result.getArgument(fixedArgument).getClass());
		}
	}

}