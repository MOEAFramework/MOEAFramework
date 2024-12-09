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
package org.moeaframework.problem.ZCAT;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.ProblemTest;

public class ZCATProblemProviderTest extends ProblemTest {
	
	@Test
	public void testCaseSensitivity() {
		Assert.assertNotNull(new ZCATProblemProvider().getProblem("zcat1_2"));
	}

	@Test
	public void testHigherObjectives() {
		assertProblemDefined("ZCAT1_4", 4, false);
		assertProblemDefined("ZCAT2_4", 4, false);
		assertProblemDefined("ZCAT3_4", 4, false);
		assertProblemDefined("ZCAT4_4", 4, false);
		assertProblemDefined("ZCAT5_4", 4, false);
		assertProblemDefined("ZCAT6_4", 4, false);
		assertProblemDefined("ZCAT7_4", 4, false);
		assertProblemDefined("ZCAT8_4", 4, false);
		assertProblemDefined("ZCAT9_4", 4, false);
		assertProblemDefined("ZCAT10_4", 4, false);
		assertProblemDefined("ZCAT11_4", 4, false);
		assertProblemDefined("ZCAT12_4", 4, false);
		assertProblemDefined("ZCAT13_4", 4, false);
		assertProblemDefined("ZCAT14_4", 4, false);
		assertProblemDefined("ZCAT15_4", 4, false);
		assertProblemDefined("ZCAT16_4", 4, false);
		assertProblemDefined("ZCAT17_4", 4, false);
		assertProblemDefined("ZCAT18_4", 4, false);
		assertProblemDefined("ZCAT19_4", 4, false);
		assertProblemDefined("ZCAT20_4", 4, false);
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new ZCATProblemProvider().getProblem("ZCATFoo_2"));
		Assert.assertNull(new ZCATProblemProvider().getProblem("ZCAT1_Foo"));
	}

}
