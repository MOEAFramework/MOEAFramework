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
package org.moeaframework.algorithm.jmetal;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link DoubleProblemAdapter} class.
 */
public class DoubleProblemAdapterTest {
	
	@Test
	public void testBounds() {
		MockRealProblem problem = new MockRealProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);
		
		Solution schema = problem.newSolution();
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			RealVariable variable = (RealVariable)schema.getVariable(0);
			Assert.assertEquals(variable.getLowerBound(), adapter.getLowerBound(i), Settings.EPS);
			Assert.assertEquals(variable.getUpperBound(), adapter.getUpperBound(i), Settings.EPS);
		}
	}
	
	@Test
	public void testConvert() {
		MockRealProblem problem = new MockRealProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);
		
		org.uma.jmetal.solution.DoubleSolution theirSolution = adapter.createSolution();
		Solution mySolution = adapter.convert(theirSolution);
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			Assert.assertEquals(theirSolution.getVariableValue(i),
					EncodingUtils.getReal(mySolution.getVariable(i)), Settings.EPS);
		}
	}

}
