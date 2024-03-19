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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FrameworkException;

public class SetGeneratorTest {
	
	@Test
	public void test() throws Exception {
		File referenceSet = TestUtils.createTempFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "100", 
				"-o", referenceSet.getPath()});
		
		Assert.assertEquals(100, TestUtils.lineCount(referenceSet));
		TestUtils.assertLinePattern(referenceSet, TestUtils.getSpaceSeparatedNumericPattern(2));
	}
	
	@Test
	public void testEpsilon() throws Exception {
		File referenceSet = TestUtils.createTempFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "1000", 
				"-e", "0.01",
				"-o", referenceSet.getPath()});
		
		Assert.assertTrue(TestUtils.lineCount(referenceSet) < 1000);
		TestUtils.assertLinePattern(referenceSet, TestUtils.getSpaceSeparatedNumericPattern(2));
	}
	
	@Test(expected = FrameworkException.class)
	public void testNonAnalytical() throws Exception {
		File referenceSet = TestUtils.createTempFile();
		
		SetGenerator.main(new String[] {
				"-b", "UF1",
				"-n", "10", 
				"-o", referenceSet.getPath()});
	}

}
