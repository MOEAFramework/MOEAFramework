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
package org.moeaframework.problem.CEC2009;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.problem.ProblemTest;

public class CEC2009ProblemProviderTest extends ProblemTest {
	
	@Test
	public void testCaseSensitivity() {
		Assert.assertNotNull(new CEC2009ProblemProvider().getProblem("uf1"));
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new CEC2009ProblemProvider().getProblem("UF14"));
		Assert.assertNull(new CEC2009ProblemProvider().getProblem("CF11"));
	}

}
