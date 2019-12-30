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
package org.moeaframework.algorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.TravisRunner;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link PESA2} class.  The MOEA Framework's implementation tends
 * to outperform the JMetal implementation.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class PESA2Test extends AlgorithmTest {
	
	@Test
	public void testGridMap() {
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Solution solution3 = TestUtils.newSolution(0.001, 0.999);
		
		Problem problem = new MockRealProblem();
		PESA2 pesa2 = new PESA2(problem, null, null, 8, 100);
		pesa2.getArchive().add(solution1);
		pesa2.getArchive().add(solution2);
		pesa2.getArchive().add(solution3);
		
		Map<Integer, List<Solution>> map = pesa2.createGridMap();
		Assert.assertEquals(2, map.size());
		
		for (List<Solution> list : map.values()) {
			if (list.size() == 1) {
				Assert.assertTrue(list.contains(solution2));
			} else {
				Assert.assertTrue(list.contains(solution1) && list.contains(solution3));
			}
		}
	}
	
	@Test
	public void testSelect() {
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Solution solution3 = TestUtils.newSolution(0.001, 0.999);
		
		Problem problem = new MockRealProblem();
		PESA2 pesa2 = new PESA2(problem, null, null, 8, 100);
		pesa2.getArchive().add(solution1);
		pesa2.getArchive().add(solution2);
		pesa2.getArchive().add(solution3);
		
		// since we're not calling iterate(), force the creation of gridMap
		pesa2.gridMap = pesa2.createGridMap();
		
		Map<Solution, Integer> count = new HashMap<Solution, Integer>();
		count.put(solution1, 0);
		count.put(solution2, 0);
		count.put(solution3, 0);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution[] solutions = pesa2.selection.select(2, null);
			
			for (Solution solution : solutions) {
				count.put(solution, count.get(solution)+1);
			}
		}
		
		// 25% of time, pick from grid 2 (containing solution 2)
		// 25% of time, pick from grid 1 (containing solutions 1 and 2)
		// 50% of time, pick both grids, favor grid 2 due to better density
		// when grid 1 is selected, each solution as 50% chance of selection
		Assert.assertEquals(0.75, count.get(solution2) / (2.0*TestThresholds.SAMPLES), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.125, count.get(solution1) / (2.0*TestThresholds.SAMPLES), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.125, count.get(solution3) / (2.0*TestThresholds.SAMPLES), TestThresholds.STATISTICS_EPS);
	}
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "PESA2", "PESA2-JMetal", true);
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "PESA2", "PESA2-JMetal", true);
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "PESA2", "PESA2-JMetal", true);
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "PESA2", "PESA2-JMetal", true);
	}

}
