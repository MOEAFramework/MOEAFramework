/* Copyright 2009-2025 David Hadka
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

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Counter;
import org.moeaframework.Retryable;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

@RunWith(CIRunner.class)
@Retryable
public class PESA2Test extends JMetalAlgorithmTest {
	
	public PESA2Test() {
		super("PESA2", true);
	}
	
	@Test
	public void testGridMap() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.001, 0.999);
		
		Problem problem = new MockRealProblem(2);
		PESA2 pesa2 = new PESA2(problem);
		pesa2.getArchive().addAll(List.of(solution1, solution2, solution3));
		
		Map<Integer, List<Solution>> map = pesa2.createGridMap();
		Assert.assertEquals(2, map.size());
		
		for (List<Solution> list : map.values()) {
			if (list.size() == 1) {
				Assert.assertContains(list, solution2);
			} else {
				Assert.assertContains(list, solution1);
				Assert.assertContains(list, solution3);
			}
		}
	}
	
	@Test
	public void testSelect() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.001, 0.999);
		
		Problem problem = new MockRealProblem(2);
		PESA2 pesa2 = new PESA2(problem);
		pesa2.getArchive().addAll(List.of(solution1, solution2, solution3));
		
		// since we're not calling iterate(), force the creation of gridMap
		pesa2.gridMap = pesa2.createGridMap();
		
		Counter<Solution> counter = new Counter<>();
		
		for (int i = 0; i < TestEnvironment.SAMPLES; i++) {
			Solution[] solutions = pesa2.selection.select(2, null);
			counter.incrementAll(solutions);
		}
		
		// 25% of time, pick from grid 2 (containing solution 2)
		// 25% of time, pick from grid 1 (containing solutions 1 and 2)
		// 50% of time, pick both grids, favor grid 2 due to better density
		// when grid 1 is selected, each solution as 50% chance of selection
		Assert.assertEquals(0.75, counter.get(solution2) / (2.0*TestEnvironment.SAMPLES), TestEnvironment.LOW_PRECISION);
		Assert.assertEquals(0.125, counter.get(solution1) / (2.0*TestEnvironment.SAMPLES), TestEnvironment.LOW_PRECISION);
		Assert.assertEquals(0.125, counter.get(solution3) / (2.0*TestEnvironment.SAMPLES), TestEnvironment.LOW_PRECISION);
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);
		PESA2 algorithm = new PESA2(problem);
		
		Assert.assertEquals(algorithm.getArchive().getCapacity(), algorithm.getConfiguration().getInt("archiveSize"));
		Assert.assertEquals(algorithm.getArchive().getBisections(), algorithm.getConfiguration().getInt("bisections"));
		
		TypedProperties properties = new TypedProperties();
		properties.setInt("archiveSize", 200);
		properties.setInt("bisections", 3);
		
		algorithm.applyConfiguration(properties);
		Assert.assertEquals(200, algorithm.getArchive().getCapacity());
		Assert.assertEquals(3, algorithm.getArchive().getBisections());
	}

}
