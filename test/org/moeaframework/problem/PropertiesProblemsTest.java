/* Copyright 2009-2019 David Hadka
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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Tests the {@link PropertiesProblems} class.
 */
public class PropertiesProblemsTest {
	
	@Test
	public void testWithoutReferenceSet() {
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.TestWithoutReferenceSet.class", 
				"org.moeaframework.problem.ZDT.ZDT1");
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem(
				"TestWithoutReferenceSet"));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet(
				"TestWithoutReferenceSet"));
	}
	
	@Test
	public void testWithReferenceSet() {
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.TestWithReferenceSet.class", 
				"org.moeaframework.problem.ZDT.ZDT1");
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.TestWithReferenceSet.referenceSet", 
				"./pf/ZDT1.pf");
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem(
				"TestWithReferenceSet"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet(
				"TestWithReferenceSet"));
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testNoEmptyConstructor() {
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.TestNoEmptyConstructor.class", 
				"org.moeaframework.problem.DTLZ.DTLZ2");
		
		ProblemFactory.getInstance().getProblem("TestNoEmptyConstructor");
	}
	
	@Test
	public void testCaseSensitivity() {
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.problems", 
				"TestCaseSensitivity");
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.TestCaseSensitivity.class", 
				"org.moeaframework.problem.ZDT.ZDT1");
		Settings.PROPERTIES.setString(
				"org.moeaframework.problem.TestCaseSensitivity.referenceSet", 
				"./pf/ZDT1.pf");
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem(
				"testcasesensitivity"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet(
				"TESTCASESENSITIVITY"));
	}

}
