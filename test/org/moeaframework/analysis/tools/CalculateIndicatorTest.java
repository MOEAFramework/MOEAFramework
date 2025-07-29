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
import org.moeaframework.TestEnvironment;
import org.moeaframework.util.io.LineReader;

public class CalculateIndicatorTest extends AbstractToolTest {
	
	@Test
	public void testHypervolume() throws Exception {
		File referenceSetFile = TestEnvironment.getResourceAsFile("pf/DTLZ2.2D.pf");
		File outputFile = TempFiles.createFile();
		
		CalculateIndicator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getPath(),
			"--output", outputFile.getPath(),
			"--indicator", "hypervolume",
			referenceSetFile.getPath() });
		
		try (LineReader reader = new LineReader(new FileReader(outputFile))) {
			Assert.assertEquals(referenceSetFile.getPath() + " 0.210671", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testGenerationalDistance() throws Exception {
		File referenceSetFile = TestEnvironment.getResourceAsFile("pf/DTLZ2.2D.pf");
		File outputFile = TempFiles.createFile();
		
		CalculateIndicator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--output", outputFile.getPath(),
			"--indicator", "GenerationalDistance",
			referenceSetFile.getPath() });
		
		try (LineReader reader = new LineReader(new FileReader(outputFile))) {
			Assert.assertEquals(referenceSetFile.getPath() + " 0.000000", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}

}
