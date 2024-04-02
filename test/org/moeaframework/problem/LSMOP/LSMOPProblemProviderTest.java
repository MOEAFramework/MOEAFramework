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
package org.moeaframework.problem.LSMOP;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.ProblemTest;

public class LSMOPProblemProviderTest extends ProblemTest {
	
	@Test
	public void testCaseSensitivity() {
		Assert.assertNotNull(new LSMOPProblemProvider().getProblem("lsmop1_2"));
	}

	@Test
	public void testHigherObjectives() {
		assertProblemDefined("LSMOP1_4", 4, false);
		assertProblemDefined("LSMOP2_4", 4, false);
		assertProblemDefined("LSMOP3_4", 4, false);
		assertProblemDefined("LSMOP4_4", 4, false);
		assertProblemDefined("LSMOP5_4", 4, false);
		assertProblemDefined("LSMOP6_4", 4, false);
		assertProblemDefined("LSMOP7_4", 4, false);
		assertProblemDefined("LSMOP8_4", 4, false);
		assertProblemDefined("LSMOP9_4", 4, false);
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new LSMOPProblemProvider().getProblem("LSMOPFoo_2"));
	}

}
