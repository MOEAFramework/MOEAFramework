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
package org.moeaframework.problem.ZDT;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.problem.ProblemTest;

public class ZDT5Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new ZDT5();
		
		Assert.assertArrayEquals(new double[] { 1.0, 20.0 },
				evaluateWith(problem, (bv, i) -> bv.clear()).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 31.0, 10.0/31.0 }, 
				evaluateWith(problem, (bv, i) -> IntStream.range(0, bv.getNumberOfBits()).forEach(j -> bv.set(j, true))).getObjectives(),
				0.000001);
	}
	
	private Solution evaluateWith(Problem problem, BiConsumer<BinaryVariable, Integer> setup) {
		Solution solution = problem.newSolution();
		
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			BinaryVariable bv = (BinaryVariable)solution.getVariable(i);
			setup.accept(bv, i);
		}
		
		problem.evaluate(solution);
		
		return solution;
	}

	@Test
	public void testJMetal() {
		testAgainstJMetal("ZDT5");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZDT5", 2);
	}
}
