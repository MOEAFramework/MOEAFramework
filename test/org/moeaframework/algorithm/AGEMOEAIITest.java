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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.algorithm.AGEMOEAII.DistanceMap;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;

@RunWith(CIRunner.class)
@Retryable
public class AGEMOEAIITest extends JMetalAlgorithmTest {
	
	public AGEMOEAIITest() {
		super("AGE-MOEA-II");
	}
	
	@Test
	public void testDistanceMap() {
		DistanceMap<Solution> distances = new DistanceMap<Solution>();
		
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution s2 = MockSolution.of().withObjectives(1.0, 0.0);
		
		distances.set(s1, s2, 5.0);
		
		Assert.assertEquals(5.0, distances.get(s1, s2), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(5.0, distances.get(s2, s1), TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDistanceMapThrows() {
		DistanceMap<Solution> distances = new DistanceMap<Solution>();
		
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution s2 = MockSolution.of().withObjectives(1.0, 0.0);
				
		Assert.assertEquals(5.0, distances.get(s1, s2), TestThresholds.HIGH_PRECISION);
	}

}
