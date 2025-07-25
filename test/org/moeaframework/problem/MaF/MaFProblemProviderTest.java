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
package org.moeaframework.problem.MaF;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.ProblemTest;

public class MaFProblemProviderTest extends ProblemTest {

	@Test
	public void testNameFormats() {
		assertProblemDefined("maf1_4", 4, false);
		assertProblemDefined("MaF1_4", 4, false);
		assertProblemDefined("MaF01_4", 4, false);
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new MaFProblemProvider().getProblem(""));
		Assert.assertNull(new MaFProblemProvider().getProblem("Foo"));
		Assert.assertNull(new MaFProblemProvider().getProblem("MaF"));
		Assert.assertNull(new MaFProblemProvider().getProblem("MaF16"));
		Assert.assertNull(new MaFProblemProvider().getProblem("MaF16_2"));
		Assert.assertNull(new MaFProblemProvider().getProblem("MaFFoo"));
		Assert.assertNull(new MaFProblemProvider().getProblem("MaFFoo_2"));
		Assert.assertNull(new MaFProblemProvider().getProblem("MaF1_Foo"));
	}

}
