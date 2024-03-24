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
package org.moeaframework.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.mock.MockSolution;

public class EpsilonBoxDominanceArchiveTest {

	private EpsilonBoxDominanceArchive archive;

	@Before
	public void setUp() {
		archive = new EpsilonBoxDominanceArchive(0.5);
	}

	@After
	public void tearDown() {
		archive = null;
	}

	@Test
	public void testDiscardedSolutions() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 0.0);

		Assert.assertTrue(archive.add(solution1));
		Assert.assertFalse(archive.add(solution2));
		Assert.assertFalse(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(0, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(1, archive.getNumberOfImprovements());
		Assert.assertTrue(archive.get(0) == solution1);
	}

	@Test
	public void testDominatingSolutions() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.0, 0.0);

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertTrue(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(2, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(3, archive.getNumberOfImprovements());
		Assert.assertTrue(archive.get(0) == solution3);
	}

	@Test
	public void testNondominatedSolutions() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.25, 0.75);
		Solution solution3 = MockSolution.of().withObjectives(0.75, 0.25);

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertTrue(archive.add(solution3));
		Assert.assertEquals(2, archive.size());
		Assert.assertEquals(1, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(3, archive.getNumberOfImprovements());
	}

	@Test
	public void testSameBoxDominatedSolutions() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.4, 0.4);
		Solution solution3 = MockSolution.of().withObjectives(0.3, 0.3);

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertTrue(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(1, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(2, archive.getNumberOfImprovements());
	}

	@Test
	public void testSameBoxNonDominatedSolutions() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.24, 0.26);
		Solution solution3 = MockSolution.of().withObjectives(0.26, 0.24);

		Assert.assertTrue(archive.add(solution1));
		Assert.assertTrue(archive.add(solution2));
		Assert.assertFalse(archive.add(solution3));
		Assert.assertEquals(1, archive.size());
		Assert.assertEquals(1, archive.getNumberOfDominatingImprovements());
		Assert.assertEquals(2, archive.getNumberOfImprovements());
	}
	
	@Test
	public void testAgainstPlatypus() {
		// Mirrors test cases used by Project-Platypus/Platypus
		Solution s1 = MockSolution.of().withObjectives(0.25, 0.25);     // First solution (improvement)
		Solution s2 = MockSolution.of().withObjectives(0.10, 0.10);     // Dominating improvement
		Solution s3 = MockSolution.of().withObjectives(0.24, 0.24);     // Dominated
		Solution s4 = MockSolution.of().withObjectives(0.09, 0.50);     // Non-dominated (improvement)
		Solution s5 = MockSolution.of().withObjectives(0.50, 0.50);     // Dominated
		Solution s6 = MockSolution.of().withObjectives(0.05, 0.05);     // Dominating improvement
		Solution s7 = MockSolution.of().withObjectives(0.04, 0.04);     // Dominating improvement (within same box)
		Solution s8 = MockSolution.of().withObjectives(0.02, 0.02);     // Dominating improvement (within same box)
		Solution s9 = MockSolution.of().withObjectives(0.00, 0.00);     // Dominating improvement (within same box)
		Solution s10 = MockSolution.of().withObjectives(-0.01, -0.01);  // Dominating improvement (new box)
		
		Solution[] solutions = new Solution[] { s1, s2, s3, s4, s5, s6, s7, s8, s9, s10 };
		int[] expectedImprovements = new int[] { 1, 2, 2, 3, 3, 4, 4, 4, 4, 5 };
		int[] expectedDominatingImprovements = new int[] { 0, 1, 1, 1, 1, 2, 2, 2, 2, 3 };
		boolean[] expectedResult = new boolean[] { true, true, false, true, false, true, true, true, true, true };
		
		archive = new EpsilonBoxDominanceArchive(0.1);
		
		for (int i = 0; i < solutions.length; i++) {
			boolean result = archive.add(solutions[i]);
			
			Assert.assertEquals(expectedImprovements[i], archive.getNumberOfImprovements());
			Assert.assertEquals(expectedDominatingImprovements[i], archive.getNumberOfDominatingImprovements());
			Assert.assertEquals(expectedResult[i], result);
		}
	}

}
