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
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.TempFiles;
import org.moeaframework.TestResources;
import org.moeaframework.analysis.io.MetricFileWriter.Metric;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;

/**
 * Integration tests for the command line utilities.  These tests only automate checks to ensure the command line
 * utilities work together and that their command line interfaces function correctly; not that their internal
 * behavior is valid.  Unit tests of the internal components ensure validity.
 */
public class IntegrationTest {
	
	private static final String PARAMETER_FILE = """
			populationSize 10 100
			maxEvaluations 1000 10000
			""";
	
	private AlgorithmFactoryTestWrapper algorithmFactory;
	
	private ProblemFactoryTestWrapper problemFactory;
	
	@Before
	public void setUp() {
		algorithmFactory = new AlgorithmFactoryTestWrapper();
		problemFactory = new ProblemFactoryTestWrapper();
		
		AlgorithmFactory.setInstance(algorithmFactory);
		ProblemFactory.setInstance(problemFactory);
	}
	
	@After
	public void tearDown() {
		algorithmFactory = null;
		problemFactory = null;
		
		AlgorithmFactory.setInstance(new AlgorithmFactory());
		ProblemFactory.setInstance(new ProblemFactory());
	}
	
	/**
	 * Tests the interoperability between the main sensitivity command line utilities.
	 */
	@Test
	public void test() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TempFiles.createFile().withContent(PARAMETER_FILE);
		File parameterFile = TempFiles.createFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "la",
				"-o", parameterFile.getPath() });
		
		Assert.assertLineCount(11, parameterFile);
		Assert.assertLinePattern(parameterFile, Assert.getSpaceSeparatedNumericPattern(2));
		
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
		File setHypervolumeOutput = Capture.output(SetHypervolume.class, combinedFile.getPath()).toFile();
		
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
		File metricFile1 = TempFiles.createFile();
		File metricFile2 = TempFiles.createFile();
		
		MetricsEvaluator.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile1.getPath(),
				"-o", metricFile1.getPath(),
				"-r", combinedFile.getPath() });
		
		MetricsEvaluator.main(new String[] {
				"-i", resultFile2.getPath(),
				"-o", metricFile2.getPath(),
				"-r", combinedFile.getPath() });
		
		Assert.assertLineCount(11, metricFile1);
		Assert.assertLineCount(11, metricFile2);
		Assert.assertLinePattern(metricFile1, Assert.getSpaceSeparatedNumericPattern(Metric.getNumberOfMetrics()));
		Assert.assertLinePattern(metricFile2, Assert.getSpaceSeparatedNumericPattern(Metric.getNumberOfMetrics()));
		
		//compute the average metric value
		File averageMetrics = TempFiles.createFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", averageMetrics.getPath(),
				metricFile1.getPath(),
				metricFile2.getPath() });
		
		Assert.assertLineCount(10, averageMetrics);
		Assert.assertLinePattern(averageMetrics, Assert.getSpaceSeparatedNumericPattern(Metric.getNumberOfMetrics()));
		
		//perform the analysis
		File analysisFile = TempFiles.createFile();
		
		MetricsAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-m", "1",
				"-o", analysisFile.getPath(),
				averageMetrics.getPath() });
		
		Assert.assertLineCount(3, analysisFile);
		
		MetricsAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-c", "-e",
				"-m", "1",
				"-o", analysisFile.getPath(),
				averageMetrics.getPath() });
		
		Assert.assertLineCount(5, analysisFile);
	}
	
	/**
	 * Tests the Sobol sensitivity analysis command line utility interoperability.
	 */
	@Test
	public void testSensitivity() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TempFiles.createFile().withContent(PARAMETER_FILE);
		File parameterFile = TempFiles.createFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "sa",
				"-o", parameterFile.getPath() });
		
		Assert.assertLineCount(61, parameterFile);
		Assert.assertLinePattern(parameterFile, Assert.getSpaceSeparatedNumericPattern(2));
		
		//evaluate MOEA
		File resultFile = TempFiles.createFile();

		EndOfRunEvaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2" });
		
		File metricFile = TempFiles.createFile();
		
		MetricsEvaluator.main(new String[] { 
				"-i", resultFile.getPath(),
				"-o", metricFile.getPath(),
				"-b", "DTLZ2_2" });
		
		Assert.assertLineCount(61, metricFile);
		Assert.assertLinePattern(metricFile, Assert.getSpaceSeparatedNumericPattern(Metric.getNumberOfMetrics()));

		
		//compute sensitivity results
		File analysisFile1 = TempFiles.createFile();
		File analysisFile2 = TempFiles.createFile();
		
		SobolAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-m", "0",
				"-i", metricFile.getPath(),
				"-o", analysisFile1.getPath() });
		
		SobolAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-m", "0",
				"-i", metricFile.getPath(),
				"-o", analysisFile2.getPath(),
				"-s" });
		
		Assert.assertLineCount(9, analysisFile1);
		Assert.assertLineCount(4, analysisFile2);
	}
	
	@Test
	public void testMerger() throws Exception {
		File file1 = TestResources.asFile("pf/DTLZ2.2D.pf");
		File file2 = TestResources.asFile("pf/DTLZ3.2D.pf");
		File file3 = TestResources.asFile("pf/DTLZ4.2D.pf");
		
		//test reference set merger
		File mergedFile = TempFiles.createFile();
		File mergerOutput = Capture.output(ReferenceSetMerger.class,
				"-o", mergedFile.getPath(),
				file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath()).toFile();
		
		Assert.assertLineCount(3, mergerOutput);
		Assert.assertLinePattern(mergerOutput, "^.+ [0-9]+ / [0-9]+$");
		
		//test set contribution
		File setContributionOutput = Capture.output(SetContribution.class, 
				"-r", mergedFile.getPath(),
				file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath()).toFile();
		
		Assert.assertLineCount(3, setContributionOutput);
		Assert.assertLinePattern(mergerOutput, "^.+ [0-9]*(?:.[0-9]+)?$");
	}
	
	/**
	 * Test to ensure the {@code close} method is called on problems, and the {@code terminate} method is called on
	 * algorithms.
	 */
	@Test
	public void testClosedAndTerminated() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TempFiles.createFile().withContent(PARAMETER_FILE);
		File parameterFile = TempFiles.createFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "la",
				"-o", parameterFile.getPath() });

		//evaluate MOEA
		File resultFile = TempFiles.createFile();
		
		EndOfRunEvaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2" });

		//validate the number of entries in the result files
		File resultValidatorFile = TempFiles.createFile();
		
		ResultFileValidator.main(new String[] {
				"-b", "DTLZ2_2",
				"-c", "10",
				"-o", resultValidatorFile.getPath(),
				resultFile.getPath() });

		//combine the results into a combined reference set
		File combinedFile = TempFiles.createFile();
		
		ResultFileMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", combinedFile.getPath(),
				resultFile.getPath()});
		
		//run the seed merger
		File seedMerger = TempFiles.createFile();
		
		ResultFileSeedMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", seedMerger.getPath(),
				resultFile.getPath()});

		//evaluate the results using the combined reference set
		File metricFile = TempFiles.createFile();
		
		MetricsEvaluator.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile.getPath(),
				"-o", metricFile.getPath(),
				"-r", combinedFile.getPath()});
		
		//generate a reference set
		File referenceFile = TempFiles.createFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "0",
				"-o", referenceFile.getPath() });

		Assert.assertEquals(10, algorithmFactory.getTerminateCount());
		Assert.assertEquals(6, problemFactory.getCloseCount());
	}
	
	@Test
	public void testRuntimeEvaluator() throws Exception {
		File parameterDescriptionFile = TempFiles.createFile().withContent("sbx.rate 0.0 1.0");
		File parameterFile = TempFiles.createFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "la",
				"-o", parameterFile.getPath() });
		
		File resultFolder = TempFiles.createDirectory();
		
		RuntimeEvaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", Paths.get(resultFolder.getPath(), "resultFile_%d.dat").toString(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-f", "100",
				"-X", "maxEvaluations=10000" });
		
		Assert.assertEquals(10, resultFolder.listFiles().length);
		
		for (File file : resultFolder.listFiles()) {
			File resultInfoFile = TempFiles.createFile();
			
			ResultFileValidator.main(new String[] {
					"-c", "100",
					"-o", resultInfoFile.getPath(),
					file.getPath() });
		}
	}
	
}
