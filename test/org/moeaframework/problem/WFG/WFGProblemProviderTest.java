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
package org.moeaframework.problem.WFG;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.ProblemTest;

public class WFGProblemProviderTest extends ProblemTest {
	
	@Test
	public void testCaseSensitivity() {
		Assert.assertNotNull(new WFGProblemProvider().getProblem("wfg1"));
	}

	@Test
	public void testHigherObjectives() {
		assertProblemDefined("WFG1_4", 4, false);
		assertProblemDefined("WFG2_4", 4, false);
		assertProblemDefined("WFG3_4", 4, false);
		assertProblemDefined("WFG4_4", 4, false);
		assertProblemDefined("WFG5_4", 4, false);
		assertProblemDefined("WFG6_4", 4, false);
		assertProblemDefined("WFG7_4", 4, false);
		assertProblemDefined("WFG8_4", 4, false);
		assertProblemDefined("WFG9_4", 4, false);
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new WFGProblemProvider().getProblem("WFGFoo_2"));
	}

}
