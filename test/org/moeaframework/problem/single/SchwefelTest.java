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
package org.moeaframework.problem.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.ProblemTest;

public class SchwefelTest extends ProblemTest {
	
	@Test
	public void test() {		
		Schwefel problem = new Schwefel();
		
		Assert.assertEquals(0.0, MockSolution.of(problem).fill(420.9687).evaluate().getObjective(0), 0.0001);
		Assert.assertTrue(MockSolution.of(problem).fill(420.97).evaluate().getObjective(0) > 0.0);
		Assert.assertTrue(MockSolution.of(problem).fill(420.96).evaluate().getObjective(0) > 0.0);
	}
	
	@Test
	public void testProblemProvider() {
		assertProblemDefined("Schwefel", 1, false);
	}

}
