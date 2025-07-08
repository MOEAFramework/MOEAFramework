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

public class EndOfRunEvaluatorTest extends AbstractToolTest {
	
	@Test
	public void test() throws Exception {
		File parameterDescriptionFile = TempFiles.createFile().withContent(PARAMETER_DESCRIPTION_FILE);
		File parameterSamplesFile = TempFiles.createFile().withContent(PARAMETER_SAMPLES_FILE);
		File resultFile = TempFiles.createFile();
		
		EndOfRunEvaluator.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterSamplesFile.getPath(),
				"-o", resultFile.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-X", "maxEvaluations=10000" });

		Assert.assertFileWithContent(resultFile);
		
		ResultFileValidator.main(new String[] { "-c", "6" , resultFile.getPath() });
	}
	
}
