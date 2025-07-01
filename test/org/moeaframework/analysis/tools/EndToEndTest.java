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
import org.moeaframework.Capture;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.io.MetricFileWriter.Metric;

public class EndToEndTest extends AbstractToolTest {
	
	public static final String PARAMETER_DESCRIPTION_FILE = """
			populationSize int 10 100
			maxEvaluations int 1000 10000
			sbx.rate double 0.0 1.0
			""";
	
	@Test
	public void test() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TempFiles.createFile().withContent(PARAMETER_DESCRIPTION_FILE);
		File parameterFile = TempFiles.createFile();
		
		SampleGenerator.main(new String[] {
				"-n", "10",
				"-p", parameterDescriptionFile.getPath(),
				"-m", "la",
				"-o", parameterFile.getPath() });
		
		Assert.assertLineCount(11, parameterFile);
		Assert.assertLinePattern(parameterFile, Assert.getSpaceSeparatedNumericPattern(3));
		
		//evaluate two MOEAs
		File resultFile1 = TempFiles.createFile();
		File resultFile2 = TempFiles.createFile();
		
		EndOfRunEvaluator.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile1.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2" });
		
		EndOfRunEvaluator.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile2.getPath(),
				"-a", "eMOEA",
				"-b", "DTLZ2_2" });

		Assert.assertFileWithContent(resultFile1);
		Assert.assertFileWithContent(resultFile2);
		
		//validate the number of entries in the result files
		File resultValidatorFile = TempFiles.createFile();
		
		ResultFileValidator.main(new String[] {
				"-b", "DTLZ2_2",
				"-c", "10",
				"-o", resultValidatorFile.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath() });
				
		Assert.assertLinePattern(resultValidatorFile, "^.* PASS$");
		
		//combine their results into a combined reference set
		File combinedFile = TempFiles.createFile();
		
		ResultFileMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", combinedFile.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath() });
		
		Assert.assertFileWithContent(combinedFile);
		
		//evaluate the combined set hypervolume
		File setHypervolumeOutput = Capture.output(CalculateIndicator.class,
				"-i", "hypervolume",
				"-b", "DTLZ2_2",
				combinedFile.getPath()).toFile();
		
		Assert.assertLineCount(1, setHypervolumeOutput);
		Assert.assertLinePattern(setHypervolumeOutput, "^.+ [0-9]*(?:.[0-9]+)?$");
		
		//test the seed merger
		File seedMerger = TempFiles.createFile();
		
		ResultFileSeedMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", seedMerger.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath() });
		
		Assert.assertFileWithContent(seedMerger);
		
		//evaluate the results using the combined reference set
		File metricFile = TempFiles.createFile();
		
		MetricsEvaluator.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", seedMerger.getPath(),
				"-o", metricFile.getPath(),
				"-r", combinedFile.getPath() });
		
		Assert.assertLineCount(11, metricFile);
		Assert.assertLinePattern(metricFile, Assert.getSpaceSeparatedNumericPattern(Metric.getNumberOfMetrics()));

		//perform the analysis
		File analysisFile = TempFiles.createFile();
		
		MetricsAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-m", "1",
				"-o", analysisFile.getPath(),
				metricFile.getPath() });
		
		Assert.assertLineCount(3, analysisFile);
		
		MetricsAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-c", "-e",
				"-m", "1",
				"-o", analysisFile.getPath(),
				metricFile.getPath() });
		
		Assert.assertLineCount(5, analysisFile);
	}
	
}
