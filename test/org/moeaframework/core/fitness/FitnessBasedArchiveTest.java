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
package org.moeaframework.core.fitness;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Fitness;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;

public class FitnessBasedArchiveTest {
	
	private Problem mockProblem;
	private FitnessEvaluator mockEvaluator;
	
	@Before
	public void setUp() {
		mockProblem = new MockRealProblem(2);
		
		mockEvaluator = new FitnessEvaluator() {

			@Override
			public void evaluate(Population population) {
				for (Solution solution : population) {
					Fitness.setAttribute(solution, EncodingUtils.getReal(solution.getVariable(0)));
				}
			}

			@Override
			public boolean areLargerValuesPreferred() {
				return true;
			}
			
		};
	}
	
	@After
	public void tearDown() {
		mockProblem = null;
		mockEvaluator = null;
	}
	
	@Test
	public void test() {
		FitnessBasedArchive archive = new FitnessBasedArchive(mockEvaluator, 1);
		
		Solution solution1 = MockSolution.of(mockProblem).at(0.25).withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of(mockProblem).at(0.75).withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of(mockProblem).at(0.5).withObjectives(1.0, 0.0);
		Solution solution4 = MockSolution.of(mockProblem).at(1.0).withObjectives(2.0, 2.0);
		
		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2)); // non-dominated and better fitness
		Assert.assertTrue(archive.add(solution3)); // non-dominated but has worse fitness
		Assert.assertFalse(archive.add(solution4)); // dominated

		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(solution2, archive.get(0));
	}
	
	@Test
	public void testCopy() {
		FitnessBasedArchive archive = new FitnessBasedArchive(mockEvaluator, 1);
		archive.add(MockSolution.of(mockProblem).at(0.75).withObjectives(0.0, 1.0));
		archive.update();
		
		FitnessBasedArchive copy = archive.copy();
		
		Assert.assertNotSame(archive, copy);
		Assert.assertSame(archive.fitnessEvaluator, copy.fitnessEvaluator);
		Assert.assertSame(archive.getComparator(), copy.getComparator());
		Assert.assertEquals(archive.getCapacity(), copy.getCapacity());
		Assert.assertEquals(archive, copy, true);
	}

}
