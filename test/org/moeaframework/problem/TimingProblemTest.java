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
package org.moeaframework.problem;

import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;

@SuppressWarnings("resource")
public class TimingProblemTest {
	
	@Test
	public void testEmpty() throws IOException {
		TimingProblem problem = new TimingProblem(new MockRealProblem(2));
		
		Assert.assertEquals(0.0, problem.getTotalSeconds(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, problem.getTotalNanoseconds(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0, problem.getTotalNFE());
	}
	
	@Test
	public void testWithTimings() {
		TimingProblem problem = new TimingProblem(new MockRealProblem(2));
		problem.evaluate(MockSolution.of(problem).randomize());
		
		Assert.assertGreaterThan(problem.getTotalSeconds(), 0.0);
		Assert.assertGreaterThan(problem.getTotalNanoseconds(), 0L);
		Assert.assertEquals(1, problem.getTotalNFE());
	}

}
