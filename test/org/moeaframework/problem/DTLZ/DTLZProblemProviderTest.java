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
package org.moeaframework.problem.DTLZ;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.ProblemTest;

public class DTLZProblemProviderTest extends ProblemTest {
	
	@Test
	public void testCaseSensitivity() {
		Assert.assertNotNull(new DTLZProblemProvider().getProblem("dtlz1_2"));
		Assert.assertNotNull(new DTLZProblemProvider().getProblem("inverted_dtlz1_2"));
	}

	@Test
	public void testHigherObjectives() {
		assertProblemDefined("DTLZ1_4", 4, false);
		assertProblemDefined("DTLZ2_4", 4, false);
		assertProblemDefined("DTLZ3_4", 4, false);
		assertProblemDefined("DTLZ4_4", 4, false);
		assertProblemDefined("DTLZ5_4", 4, false);
		assertProblemDefined("DTLZ6_4", 4, false);
		assertProblemDefined("DTLZ7_4", 4, false);
		assertProblemDefined("Inverted_DTLZ1_4", 4, false);
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new DTLZProblemProvider().getProblem(""));
		Assert.assertNull(new DTLZProblemProvider().getProblem("Foo"));
		Assert.assertNull(new DTLZProblemProvider().getProblem("DTLZ"));
		Assert.assertNull(new DTLZProblemProvider().getProblem("DTLZ8"));
		Assert.assertNull(new DTLZProblemProvider().getProblem("DTLZ8_2"));
		Assert.assertNull(new DTLZProblemProvider().getProblem("DTLZ8_Foo"));
		Assert.assertNull(new DTLZProblemProvider().getProblem("DTLZFoo"));
		Assert.assertNull(new DTLZProblemProvider().getProblem("Foo_DTLZ1_2"));
	}

}
