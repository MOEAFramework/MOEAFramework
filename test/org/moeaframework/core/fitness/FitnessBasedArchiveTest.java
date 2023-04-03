/* Copyright 2009-2023 David Hadka
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
package org.moeaframework.core.fitness;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.MockRealProblem;

public class FitnessBasedArchiveTest {
	
	@SuppressWarnings("resource")
	@Test
	public void test() {
		Problem mockProblem = new MockRealProblem(2);
		
		FitnessEvaluator mockEvaluator = new FitnessEvaluator() {

			@Override
			public void evaluate(Population population) {
				for (Solution solution : population) {
					solution.setAttribute(FITNESS_ATTRIBUTE, EncodingUtils.getReal(solution.getVariable(0)));
				}
			}

			@Override
			public boolean areLargerValuesPreferred() {
				return true;
			}
			
		};
		
		FitnessBasedArchive archive = new FitnessBasedArchive(mockEvaluator, 1);
		
		Solution solution1 = mockProblem.newSolution();
		Solution solution2 = mockProblem.newSolution();
		Solution solution3 = mockProblem.newSolution();
		Solution solution4 = mockProblem.newSolution();
		
		EncodingUtils.setReal(solution1, new double[] { 0.25 });
		EncodingUtils.setReal(solution2, new double[] { 0.75 });
		EncodingUtils.setReal(solution3, new double[] { 0.5 });
		EncodingUtils.setReal(solution4, new double[] { 1.0 });
		
		solution1.setObjectives(new double[] { 0.5, 0.5 });
		solution2.setObjectives(new double[] { 0.0, 1.0 });
		solution3.setObjectives(new double[] { 1.0, 0.0 });
		solution4.setObjectives(new double[] { 2.0, 2.0 });

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2)); // non-dominated and better fitness
		Assert.assertTrue(archive.add(solution3)); // non-dominated but has worse fitness
		Assert.assertFalse(archive.add(solution4)); // dominated

		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(solution2, archive.get(0));
	}

}
