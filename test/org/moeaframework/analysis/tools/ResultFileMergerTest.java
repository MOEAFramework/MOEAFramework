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

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.io.ResultFileReader;

public class ResultFileMergerTest extends AbstractToolTest {
	
	public static final String SECOND_RESULT_FILE = """
			# Problem = DTLZ2_2
			# Variables = 11
			# Objectives = 2
			//NFE=100
			//ElapsedTime=0.214
			0.5 0.5 0.5 0.5 0.5 0.5 0.5 0.5 0.5 0.5 0.5 0.5 0.5
			#
			""";
	
	@Test
	public void test() throws Exception {
		File resultFile1 = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File resultFile2 = TempFiles.createFile().withContent(SECOND_RESULT_FILE);
		File combinedFile = TempFiles.createFile();
		
		ResultFileMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", combinedFile.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath() });
		
		try (ResultFileReader reader = ResultFileReader.open(null, combinedFile)) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertEquals(3, reader.next().getPopulation().size());
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test(expected = ParseException.class)
	public void testNoArgs() throws Exception {
		File combinedFile = TempFiles.createFile();
		
		ResultFileMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", combinedFile.getPath() });
	}
	
}
