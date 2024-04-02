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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.util.PropertyScope;

public class PropertiesProblemsTest {
	
	@Test
	public void testWithoutReferenceSet() {
		try (PropertyScope scope = Settings.createScope()
				.with("org.moeaframework.problem.TestWithoutReferenceSet.class", "org.moeaframework.problem.ZDT.ZDT1")) {
			Assert.assertNotNull(ProblemFactory.getInstance().getProblem("TestWithoutReferenceSet"));
			Assert.assertNull(ProblemFactory.getInstance().getReferenceSet("TestWithoutReferenceSet"));
		}
	}
	
	@Test
	public void testWithReferenceSet() {
		try (PropertyScope scope = Settings.createScope()
				.with("org.moeaframework.problem.TestWithReferenceSet.class", "org.moeaframework.problem.ZDT.ZDT1")
				.with("org.moeaframework.problem.TestWithReferenceSet.referenceSet", "./pf/ZDT1.pf")) {
			Assert.assertNotNull(ProblemFactory.getInstance().getProblem("TestWithReferenceSet"));
			Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("TestWithReferenceSet"));
		}
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testNoEmptyConstructor() {
		try (PropertyScope scope = Settings.createScope()
				.with("org.moeaframework.problem.TestNoEmptyConstructor.class", "org.moeaframework.problem.DTLZ.DTLZ2")) {
			ProblemFactory.getInstance().getProblem("TestNoEmptyConstructor");
		}
	}
	
	@Test
	public void testCaseSensitivity() {
		try (PropertyScope scope = Settings.createScope()
				.with("org.moeaframework.problem.problems", "TestCaseSensitivity")
				.with("org.moeaframework.problem.TestCaseSensitivity.class", "org.moeaframework.problem.ZDT.ZDT1")
				.with("org.moeaframework.problem.TestCaseSensitivity.referenceSet", "./pf/ZDT1.pf")) {
			Assert.assertNotNull(ProblemFactory.getInstance().getProblem("testcasesensitivity"));
			Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("TESTCASESENSITIVITY"));
		}
	}

}
