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
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;

public class RuntimeEvaluatorTest extends AbstractToolTest {
	
	@Test
	public void test() throws Exception {
		File parameterDescriptionFile = TempFiles.createFile().withContent(PARAMETER_DESCRIPTION_FILE);
		File parameterFile = TempFiles.createFile().withContent(PARAMETER_SAMPLES_FILE);
		File resultFolder = TempFiles.createDirectory();
		
		RuntimeEvaluator.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", Paths.get(resultFolder.getPath(), "resultFile_%d.dat").toString(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-f", "100",
				"-X", "maxEvaluations=10000" });
		
		Assert.assertEquals(6, resultFolder.listFiles().length);
		
		ResultFileValidator.main(ArrayUtils.addAll(new String[] { "-c", "100" },
				Stream.of(resultFolder.listFiles()).map(f -> f.getPath()).toArray(String[]::new)));
	}
	
}
