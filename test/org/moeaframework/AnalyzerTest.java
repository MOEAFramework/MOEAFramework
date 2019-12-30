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
package org.moeaframework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;

/**
 * Tests the {@link Analyzer} class.
 */
@RunWith(TravisRunner.class)
public class AnalyzerTest {
	
	private AlgorithmFactoryTestWrapper algorithmFactory;
	
	private ProblemFactoryTestWrapper problemFactory;
	
	@Before
	public void setUp() {
		algorithmFactory = new AlgorithmFactoryTestWrapper();
		problemFactory = new ProblemFactoryTestWrapper();
	}
	
	@After
	public void tearDown() {
		algorithmFactory = null;
		problemFactory = null;
	}
	
	@Test
	public void testOneSampleStatisticalResults() throws IOException {
		new Analyzer()
				.withProblem("DTLZ2_2")
				.includeGenerationalDistance()
				.showStatisticalSignificance()
				.addAll("NSGAII", new Executor()
						.withProblem("DTLZ2_2")
						.withAlgorithm("NSGAII").runSeeds(5))
				.printAnalysis();
	}
	
	@Test
	public void testNoIndicators() throws IOException {
		new Analyzer()
				.withProblem("DTLZ2_2")
				.add("NSGAII", new Executor()
						.withProblem("DTLZ2_2")
						.withAlgorithm("NSGAII").run())
				.printAnalysis();
	}
	
	//TODO: this should result in a warning and not an exception, to avoid data
	//loss in case printAnalysis() is followed by saveData()
	@Test(expected = IllegalArgumentException.class)
	public void testNoProblem() throws IOException {
		new Analyzer()
				.add("NSGAII", new Executor()
						.withProblem("DTLZ2_2")
						.withAlgorithm("NSGAII").run())
				.printAnalysis();
	}
	
	@Test
	public void testEmpty() throws IOException {
		Analyzer analyzer = new Analyzer()
				.withProblem("DTLZ2_2")
				.withEpsilon(new double[] { 0.01 })
				.includeAllMetrics()
				.showAll();
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		analyzer.printAnalysis(new PrintStream(result));
		
		Assert.assertEquals(0, result.size());
	}
	
	@Test
	public void testAll() throws IOException {
		Analyzer analyzer = generate();
		File tempFile = TestUtils.createTempFile();
		
		ByteArrayOutputStream expected = new ByteArrayOutputStream();
		analyzer.printAnalysis(new PrintStream(expected));

		analyzer.saveData(tempFile.getParentFile(), tempFile.getName(), ".dat");
		analyzer.clear();
		analyzer.loadData(tempFile.getParentFile(), tempFile.getName(), ".dat");
		
		File actualFile = TestUtils.createTempFile();
		analyzer.saveAnalysis(actualFile);
		
		//20 closes from generate(), 2 from saveData, 2 from loadData,
		//1 from printAnalysis, 1 from saveAnalysis
		Assert.assertEquals(26, problemFactory.getCloseCount());
		
		Assert.assertArrayEquals(expected.toByteArray(), 
				TestUtils.loadFile(actualFile));
	}
	
	private Analyzer generate() {
		String[] algorithms = { "eMOEA", "NSGAII" };
		
		Executor executor = new Executor()
				.usingAlgorithmFactory(algorithmFactory)
				.usingProblemFactory(problemFactory)
				.withProblem("DTLZ2_2")
				.withEpsilon(new double[] { 0.01 })
				.withMaxEvaluations(1000)
				.distributeOnAllCores();
		
		Analyzer analyzer = new Analyzer()
				.usingProblemFactory(problemFactory)
				.withProblem("DTLZ2_2")
				.withEpsilon(new double[] { 0.01 })
				.includeAllMetrics()
				.showAll();

		//run each algorithm for 10 seeds
		for (String algorithm : algorithms) {
			for (int i=0; i<10; i++) {
				analyzer.add(algorithm, 
						executor.withAlgorithm(algorithm).run());
			}
		}

		return analyzer;
	}
	
	@Test
	public void testReferenceSetFromProblemFactory() throws IOException {
		NondominatedPopulation actual = generate().getReferenceSet();
		
		NondominatedPopulation expected = new EpsilonBoxDominanceArchive(0.01,
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		TestUtils.assertEquals(expected, actual);
	}
	
	@Test
	public void testReferenceSetFromFile() throws IOException {
		NondominatedPopulation actual = generate()
				.withReferenceSet(new File("./pf/DTLZ1.2D.pf"))
				.getReferenceSet();
		
		NondominatedPopulation expected = new EpsilonBoxDominanceArchive(0.01,
				PopulationIO.readObjectives(new File("./pf/DTLZ1.2D.pf")));
		
		TestUtils.assertEquals(expected, actual);
	}

}
