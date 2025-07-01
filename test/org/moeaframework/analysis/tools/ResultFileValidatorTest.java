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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.FrameworkException;

public class ResultFileValidatorTest extends AbstractToolTest {
	
	@Test
	public void testLegacy() throws Exception {
		File inputFile = TempFiles.createFile().withContent("#\n0 0\n1 1\n#");
		File outputFile = TempFiles.createFile();
		
		ResultFileValidator.main(new String[] {
				"-c", "1",
				"-o", outputFile.getPath(),
				inputFile.getPath() });
		
		Assert.assertLineCount(1, outputFile);
		Assert.assertLinePattern(outputFile, "^(.*)\\s+PASS$");
	}
	
	@Test
	public void testPass() throws Exception {
		File resultFile = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File outputFile = TempFiles.createFile();
		
		ResultFileValidator.main(new String[] {
				"-c", "2",
				"-o", outputFile.getPath(),
				resultFile.getPath() });
				
		Assert.assertLineCount(1, outputFile);
		Assert.assertLinePattern(outputFile, "^(.*)\\s+PASS$");
	}
	
	@Test
	public void testFail() throws Exception {
		File resultFile = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File outputFile = TempFiles.createFile();
		
		Assert.assertThrows(FrameworkException.class, () -> ResultFileValidator.main(new String[] {
				"-c", "5",
				"-o", outputFile.getPath(),
				resultFile.getPath() }));
				
		Assert.assertLineCount(1, outputFile);
		Assert.assertLinePattern(outputFile, "^(.*)\\s+FAIL.*$");
	}
	
	@Test
	public void testEmpty() throws Exception {
		File resultFile = TempFiles.createFile().withContent(EMPTY_RESULT_FILE);
		File outputFile = TempFiles.createFile();
		
		Assert.assertThrows(FrameworkException.class, () -> ResultFileValidator.main(new String[] {
				"-c", "5",
				"-o", outputFile.getPath(),
				resultFile.getPath() }));
				
		Assert.assertLineCount(1, outputFile);
		Assert.assertLinePattern(outputFile, "^(.*)\\s+FAIL.*$");
	}

}
