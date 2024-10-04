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
package org.moeaframework.util.clustering;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockSolution;

public class ClusterableSolutionTest {
	
	@Test
	public void testObjectives() {
		Solution solution = MockSolution.of().withObjectives(0.0, 1.0);
		ClusterableSolution clusterableSolution = ClusterableSolution.withObjectives(solution);
		
		Assert.assertSame(solution, clusterableSolution.getSolution());
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, clusterableSolution.getPoint(), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testVariables() {
		Solution solution = MockSolution.of().withVariables(new RealVariable(0.25, 0.0, 1.0), new BinaryIntegerVariable(2, 0, 5));
		ClusterableSolution clusterableSolution = ClusterableSolution.withVariables(solution);
		
		Assert.assertSame(solution, clusterableSolution.getSolution());
		Assert.assertArrayEquals(new double[] { 0.25, 2.0 }, clusterableSolution.getPoint(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUnsupportedVariable() {
		Solution solution = MockSolution.of().withVariables(new Permutation(5));
		ClusterableSolution.withVariables(solution);
	}
	
}
