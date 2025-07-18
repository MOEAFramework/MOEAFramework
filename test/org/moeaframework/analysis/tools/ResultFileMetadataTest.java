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
import java.io.FileReader;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.util.io.LineReader;

public class ResultFileMetadataTest extends AbstractToolTest {
	
	public static final String MISSING_PROPERTY_RESULT_FILE = """
		# Problem = DTLZ2_2
		# Variables = 11
		# Objectives = 2
		//NFE=100
		//ElapsedTime=0.214
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25
		#
		//NFE=200
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25
		#
		""";
	
	@Test
	public void testComplete() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File output = TempFiles.createFile();
		
		ResultFileMetadata.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"NFE", "ElapsedTime"});
		
		try (LineReader reader = LineReader.wrap(new FileReader(output))) {
			Assert.assertEqualsNormalized("NFE ElapsedTime", reader.readLine());
			Assert.assertStringMatches(reader.readLine(), "[ \\-]+");
			Assert.assertEqualsNormalized("100 0.214", reader.readLine());
			Assert.assertEqualsNormalized("200 0.209186", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testEmpty() throws Exception {
		File input = TempFiles.createFile().withContent(EMPTY_RESULT_FILE);
		File output = TempFiles.createFile();
		
		ResultFileMetadata.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"NFE", "ElapsedTime"});
		
		try (LineReader reader = LineReader.wrap(new FileReader(output))) {
			Assert.assertEqualsNormalized("NFE ElapsedTime", reader.readLine());
			Assert.assertStringMatches(reader.readLine(), "[ \\-]+");
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testIncludeIndex() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File output = TempFiles.createFile();
		
		ResultFileMetadata.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"--includeIndex",
			"NFE", "ElapsedTime"});
		
		try (LineReader reader = LineReader.wrap(new FileReader(output))) {
			Assert.assertEqualsNormalized("Index NFE ElapsedTime", reader.readLine());
			Assert.assertStringMatches(reader.readLine(), "[ \\-]+");
			Assert.assertEqualsNormalized("0 100 0.214", reader.readLine());
			Assert.assertEqualsNormalized("1 200 0.209186", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMissingProperty() throws Exception {
		File input = TempFiles.createFile().withContent(MISSING_PROPERTY_RESULT_FILE);
		File output = TempFiles.createFile();
		
		ResultFileMetadata.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"NFE", "ElapsedTime"});
	}
	
	@Test
	public void testMetrics() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File output = TempFiles.createFile();
		
		ResultFileMetadata.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"GenerationalDistance", "Hypervolume", "Spacing" });
				
		try (LineReader reader = LineReader.wrap(new FileReader(output))) {
			Assert.assertEqualsNormalized("GenerationalDistance Hypervolume Spacing", reader.readLine());
			Assert.assertStringMatches(reader.readLine(), "[ \\-]+");
			Assert.assertStringMatches(reader.readLine(), Assert.getSpaceSeparatedNumericPattern(3));
			Assert.assertStringMatches(reader.readLine(), Assert.getSpaceSeparatedNumericPattern(3));
			Assert.assertNull(reader.readLine());
		}
	}
	
}
