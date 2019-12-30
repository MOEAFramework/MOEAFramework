/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.analysis.sensitivity;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;
import org.moeaframework.util.ReferenceSetMerger;

/**
 * Integration tests for the sensitivity command line utilities.  These tests
 * only automate checks to ensure the command line utilities interoperate and
 * that their command line interfaces function appropriately; not that their
 * internal behavior is valid.  Unit tests of the internal components ensure
 * validity.
 */
public class IntegrationTest {
	
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
	
	@Test
	public void testSetGenerator() throws Exception {
		File referenceSet = TestUtils.createTempFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "10", 
				"-o", referenceSet.getPath()});
		
		Assert.assertEquals(10, TestUtils.lineCount(referenceSet));
		TestUtils.assertLinePattern(referenceSet,
				TestUtils.getSpaceSeparatedNumericPattern(2));
	}
	
	/**
	 * Tests the interoperability between the main sensitivity command line
	 * utilities.
	 */
	@Test
	public void test() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TestUtils.createTempFile(
				"populationSize 10 100\r\nmaxEvaluations 1000 10000");
		File parameterFile = TestUtils.createTempFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "la",
				"-o", parameterFile.getPath()});
		
		Assert.assertEquals(10, TestUtils.lineCount(parameterFile));
		TestUtils.assertLinePattern(parameterFile,
				TestUtils.getSpaceSeparatedNumericPattern(2));
		
		//evaluate two MOEAs
		File resultFile1 = TestUtils.createTempFile();
		File resultFile2 = TestUtils.createTempFile();
		
		Evaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile1.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-x", "maxEvaluations=10000"});
		
		Evaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile2.getPath(),
				"-a", "eMOEA",
				"-b", "DTLZ2_2",
				"-x", "maxEvaluations=10000"});

		Assert.assertTrue(TestUtils.lineCount(resultFile1) > 0);
		Assert.assertTrue(TestUtils.lineCount(resultFile2) > 0);
		
		//count the number of entries in the result files
		File resultInfoFile = TestUtils.createTempFile();
		
		ResultFileInfo.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", resultInfoFile.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath()});
		
		TestUtils.assertLinePattern(resultInfoFile, "^.* 10$");
		
		//combine their results into a combined reference set
		File combinedFile = TestUtils.createTempFile();
		
		ResultFileMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", combinedFile.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath()});
		
		Assert.assertTrue(TestUtils.lineCount(combinedFile) > 0);
		
		//evaluate the combined set hypervolume
		File setHypervolumeOutput = TestUtils.createTempFile();
		
		TestUtils.pipeCommandLine(setHypervolumeOutput, 
				SetHypervolume.class, combinedFile.getPath());
		
		Assert.assertEquals(1, TestUtils.lineCount(setHypervolumeOutput));
		TestUtils.assertLinePattern(setHypervolumeOutput, 
				"^.+ [0-9]*(?:.[0-9]+)?$");
		
		//test the seed merger
		File seedMerger = TestUtils.createTempFile();
		
		ResultFileSeedMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", seedMerger.getPath(),
				resultFile1.getPath(),
				resultFile2.getPath()});
		
		Assert.assertTrue(TestUtils.lineCount(seedMerger) > 0);
		
		//evaluate the results using the combined reference set
		File metricFile1 = TestUtils.createTempFile();
		File metricFile2 = TestUtils.createTempFile();
		
		ResultFileEvaluator.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile1.getPath(),
				"-o", metricFile1.getPath(),
				"-r", combinedFile.getPath()});
		
		ResultFileEvaluator.main(new String[] {
				"-d", "2",
				"-i", resultFile2.getPath(),
				"-o", metricFile2.getPath(),
				"-r", combinedFile.getPath()});
		
		Assert.assertEquals(11, TestUtils.lineCount(metricFile1));
		Assert.assertEquals(11, TestUtils.lineCount(metricFile2));
		TestUtils.assertLinePattern(metricFile1, TestUtils.getSpaceSeparatedNumericPattern(
				MetricFileWriter.NUMBER_OF_METRICS));
		TestUtils.assertLinePattern(metricFile2, TestUtils.getSpaceSeparatedNumericPattern(
				MetricFileWriter.NUMBER_OF_METRICS));
		
		//compute the average metric value
		File averageMetrics = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", averageMetrics.getPath(),
				metricFile1.getPath(),
				metricFile2.getPath()});
		
		Assert.assertEquals(10, TestUtils.lineCount(averageMetrics));
		TestUtils.assertLinePattern(averageMetrics, TestUtils.getSpaceSeparatedNumericPattern(
				MetricFileWriter.NUMBER_OF_METRICS));
		
		//perform the analysis
		File analysisFile = TestUtils.createTempFile();
		
		Analysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-m", "1",
				"-o", analysisFile.getPath(),
				averageMetrics.getPath()});
		
		Assert.assertEquals(3, TestUtils.lineCount(analysisFile));
		
		Analysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-c", "-e",
				"-m", "1",
				"-o", analysisFile.getPath(),
				averageMetrics.getPath()});
		
		Assert.assertEquals(5, TestUtils.lineCount(analysisFile));
	}
	
	/**
	 * Tests the Sobol sensitivity analysis command line utility
	 * interoperability.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testSensitivity() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TestUtils.createTempFile(
				"populationSize 10 100\r\nmaxEvaluations 1000 10000");
		File parameterFile = TestUtils.createTempFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "sa",
				"-o", parameterFile.getPath()});
		
		Assert.assertEquals(60, TestUtils.lineCount(parameterFile));
		TestUtils.assertLinePattern(parameterFile,
				TestUtils.getSpaceSeparatedNumericPattern(2));
		
		//evaluate MOEA
		File metricFile = TestUtils.createTempFile();

		Evaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", metricFile.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-m"});
		
		Assert.assertEquals(61, TestUtils.lineCount(metricFile));
		TestUtils.assertLinePattern(metricFile, TestUtils.getSpaceSeparatedNumericPattern(
				MetricFileWriter.NUMBER_OF_METRICS));

		//compute sensitivity results
		File analysisFile1 = TestUtils.createTempFile();
		File analysisFile2 = TestUtils.createTempFile();
		
		SobolAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-m", "0",
				"-i", metricFile.getPath(),
				"-o", analysisFile1.getPath()});
		
		SobolAnalysis.main(new String[] {
				"-p", parameterDescriptionFile.getPath(),
				"-m", "0",
				"-i", metricFile.getPath(),
				"-o", analysisFile2.getPath(),
				"-s"});
		
		Assert.assertEquals(9, TestUtils.lineCount(analysisFile1));
		Assert.assertEquals(4, TestUtils.lineCount(analysisFile2));
	}
	
	@Test
	public void testMerger() throws Exception {
		//test reference set merger
		File mergerOutput = TestUtils.createTempFile();
		File mergedFile = TestUtils.createTempFile();
		
		TestUtils.pipeCommandLine(mergerOutput, ReferenceSetMerger.class,
				"-o", mergedFile.getPath(),
				"pf/DTLZ2.2D.pf", "pf/DTLZ3.2D.pf", "pf/DTLZ4.2D.pf");
		
		Assert.assertEquals(3, TestUtils.lineCount(mergerOutput));
		TestUtils.assertLinePattern(mergerOutput, "^.+ [0-9]+ / [0-9]+$");
		
		//test set contribution
		File setContributionOutput = TestUtils.createTempFile();
		
		TestUtils.pipeCommandLine(setContributionOutput, SetContribution.class, 
				"-r", mergedFile.getPath(),
				"pf/DTLZ2.2D.pf", "pf/DTLZ3.2D.pf", "pf/DTLZ4.2D.pf");
		
		Assert.assertEquals(3, TestUtils.lineCount(setContributionOutput));
		TestUtils.assertLinePattern(mergerOutput, "^.+ [0-9]*(?:.[0-9]+)?$");
	}
	
	/**
	 * Test to ensure the {@code close} method is called on problems, and the
	 * {@code terminate} method is called on algorithms.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testClosedAndTerminated() throws Exception {
		//create the sample file
		File parameterDescriptionFile = TestUtils.createTempFile(
				"populationSize 10 100\r\nmaxEvaluations 1000 10000");
		File parameterFile = TestUtils.createTempFile();
		
		SampleGenerator.main(new String[] { 
				"-n", "10", 
				"-p", parameterDescriptionFile.getPath(),
				"-m", "la",
				"-o", parameterFile.getPath()});

		//evaluate MOEA
		File resultFile = TestUtils.createTempFile();
		
		Evaluator.main(new String[] { 
				"-p", parameterDescriptionFile.getPath(),
				"-i", parameterFile.getPath(),
				"-o", resultFile.getPath(),
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-x", "maxEvaluations=10000"});

		//count the number of entries in the result files
		File resultInfoFile = TestUtils.createTempFile();
		
		ResultFileInfo.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", resultInfoFile.getPath(),
				resultFile.getPath() });

		//combine the results into a combined reference set
		File combinedFile = TestUtils.createTempFile();
		
		ResultFileMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", combinedFile.getPath(),
				resultFile.getPath()});
		
		//run the seed merger
		File seedMerger = TestUtils.createTempFile();
		
		ResultFileSeedMerger.main(new String[] {
				"-b", "DTLZ2_2",
				"-o", seedMerger.getPath(),
				resultFile.getPath()});

		//evaluate the results using the combined reference set
		File metricFile = TestUtils.createTempFile();
		
		ResultFileEvaluator.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile.getPath(),
				"-o", metricFile.getPath(),
				"-r", combinedFile.getPath()});
		
		//generate a reference set
		File referenceFile = TestUtils.createTempFile();
		
		SetGenerator.main(new String[] {
				"-b", "DTLZ2_2",
				"-n", "0",
				"-o", referenceFile.getPath() });

		Assert.assertEquals(10, algorithmFactory.getTerminateCount());
		Assert.assertEquals(6, problemFactory.getCloseCount());
	}

}
