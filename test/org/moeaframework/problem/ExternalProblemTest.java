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

import java.util.function.Function;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockExternalProblem;

/**
 * Tests basic functionality without requiring an executable.
 */
public class ExternalProblemTest {
	
	@Test
	public void testValidResponse() throws Exception {
		test(s -> "0.2 0.8 0.5");
	}
	
	@Test(expected = ProblemException.class)
	public void testTooFewResponses() throws Exception {
		test(s -> "0.2 0.8");
	}
	
	@Test(expected = ProblemException.class)
	public void testTooManyResponses() throws Exception {
		test(s -> "0.2 0.8 0.5 0.1");
	}
	
	@Test(expected = ProblemException.class)
	public void testUnparseableResponse() throws Exception {
		test(s -> "0.2 0.8foo 0.5");
	}
	
	@Test(expected = ProblemException.class)
	public void testEmptyResponse() throws Exception {
		test(s -> "");
	}
	
	@Test(expected = ProblemException.class)
	public void testNoResponse() throws Exception {
		test(s -> {
			throw new FrameworkException("test close with no response");
		});
	}
	
	private void test(final Function<String, String> callback) throws Exception {
		try (MockExternalProblem problem = new MockExternalProblem(callback)) {
			for (int i=0; i<100; i++) {
				Solution solution = problem.newSolution();
				problem.evaluate(solution);
				
				Assert.assertEquals(0.2, solution.getObjective(0), TestThresholds.HIGH_PRECISION);
				Assert.assertEquals(0.8, solution.getObjective(1), TestThresholds.HIGH_PRECISION);
				Assert.assertEquals(0.5, solution.getConstraint(0), TestThresholds.HIGH_PRECISION);
			}
			
			Assert.assertEquals(100, problem.getCallCount());
		}
	}
	
}
