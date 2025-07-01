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

public class ResultFileConverterTest extends AbstractToolTest {
	
	@Test
	public void testPlaintext() throws Exception {
		File resultFile = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File outputFile = TempFiles.createFile();
		
		ResultFileConverter.main(new String[] {
				"-f", "Plaintext",
				"-i", resultFile.getPath(),
				"-o", outputFile.getPath() });
		
		Assert.assertLineCount(4, outputFile);
		Assert.assertLinePattern(outputFile, "^([V-].*)|(" + Assert.getSpaceSeparatedNumericPattern(13) + ")$");
	}
	
	@Test(expected = FrameworkException.class)
	public void testEmpty() throws Exception {
		File resultFile = TempFiles.createFile().withContent(EMPTY_RESULT_FILE);
		File outputFile = TempFiles.createFile();
		
		ResultFileConverter.main(new String[] {
				"-i", resultFile.getPath(),
				"-o", outputFile.getPath() });
	}
	
	@Test
	public void testArff() throws Exception {
		File resultFile = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File outputFile = TempFiles.createFile();
		
		ResultFileConverter.main(new String[] {
				"-f", "ARFF",
				"-i", resultFile.getPath(),
				"-o", outputFile.getPath() });
				
		Assert.assertLineCount(17, outputFile);
		Assert.assertLinePattern(outputFile, "^([@%].*)|(" + Assert.getCommaSeparatedNumericPattern(13) + ")$");
	}

}
