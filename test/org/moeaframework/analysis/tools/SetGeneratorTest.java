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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.FrameworkException;

public class SetGeneratorTest {
	
	@Test
	public void test() throws Exception {
		File referenceSet = TempFiles.createFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "100", 
				"-o", referenceSet.getPath()});
		
		Assert.assertLineCount(100, referenceSet);
		Assert.assertLinePattern(referenceSet, Assert.getSpaceSeparatedNumericPattern(2));
	}
	
	@Test
	public void testEpsilon() throws Exception {
		File referenceSet = TempFiles.createFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "1000", 
				"-e", "0.01",
				"-o", referenceSet.getPath()});
		
		Assert.assertLinePattern(referenceSet, Assert.getSpaceSeparatedNumericPattern(2));
	}
	
	@Test(expected = FrameworkException.class)
	public void testNonAnalytical() throws Exception {
		File referenceSet = TempFiles.createFile();
		
		SetGenerator.main(new String[] {
				"-b", "UF1",
				"-n", "10", 
				"-o", referenceSet.getPath()});
	}

}
