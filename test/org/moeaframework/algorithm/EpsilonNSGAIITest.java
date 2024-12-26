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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;

public class EpsilonNSGAIITest {
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);
		EpsilonNSGAII algorithm = new EpsilonNSGAII(problem);
		
		Assert.assertArrayEquals(algorithm.getArchive().getComparator().getEpsilons().toArray(),
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				TestThresholds.HIGH_PRECISION);
		
		algorithm.applyConfiguration(TypedProperties.of("epsilon", 0.1));
		Assert.assertArrayEquals(new double[] { 0.1 },
				algorithm.getArchive().getComparator().getEpsilons().toArray(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 0.1 },
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				TestThresholds.HIGH_PRECISION);

		algorithm.applyConfiguration(TypedProperties.of("epsilon", 0.1, 0.2));
		Assert.assertArrayEquals(new double[] { 0.1, 0.2 },
				algorithm.getArchive().getComparator().getEpsilons().toArray(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 0.1, 0.2 },
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				TestThresholds.HIGH_PRECISION);
	}

}
