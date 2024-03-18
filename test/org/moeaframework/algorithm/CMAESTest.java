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
package org.moeaframework.algorithm;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.examples.single.RosenbrockProblem;

/**
 * Tests the {@link CMAES} class.
 */
public class CMAESTest {
	
	// TODO: extend with better tests
	
	@Test
	public void testSingleObjective() {
		Problem problem = new RosenbrockProblem();
		CMAES algorithm = new CMAES(problem);

		for (int i = 0; i < 100; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(1, algorithm.getResult().size());
		
		Solution solution = algorithm.getResult().get(0);
		
		TestUtils.assertEquals(1.0, EncodingUtils.getReal(solution.getVariable(0)));
		TestUtils.assertEquals(1.0, EncodingUtils.getReal(solution.getVariable(1)));
		TestUtils.assertEquals(0.0, solution.getObjective(0));
	}

	@Test
	public void testMultiObjective() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		CMAES algorithm = new CMAES(problem);

		for (int i = 0; i < 100; i++) {
			algorithm.step();
		}
	}
	
}
