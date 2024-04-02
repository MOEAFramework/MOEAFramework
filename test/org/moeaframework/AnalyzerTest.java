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
package org.moeaframework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;

@RunWith(CIRunner.class)
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
	
	@Test
	public void testNoProblem() throws IOException {
		// in this case, we print a warning instead of failing to avoid data loss
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
		
		try (ByteArrayOutputStream result = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(result)) {
			analyzer.printAnalysis(ps);
			Assert.assertEquals(0, result.size());
		}
	}
	
	@Test
	public void testAll() throws IOException {
		Analyzer analyzer = generate();
		File tempFile = TempFiles.createFile();
		
		try (ByteArrayOutputStream expected = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(expected)) {
			analyzer.printAnalysis(ps);
	
			analyzer.saveData(tempFile.getParentFile(), tempFile.getName(), ".dat");
			analyzer.clear();
			analyzer.loadData(tempFile.getParentFile(), tempFile.getName(), ".dat");
			
			File actualFile = TempFiles.createFile();
			analyzer.saveAnalysis(actualFile);
			
			//20 closes from generate(), 2 from saveData, 2 from loadData, 1 from printAnalysis, 1 from saveAnalysis
			Assert.assertEquals(26, problemFactory.getCloseCount());
			Assert.assertArrayEquals(expected.toByteArray(), Files.readAllBytes(actualFile.toPath()));
		}
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
				analyzer.add(algorithm, executor.withAlgorithm(algorithm).run());
			}
		}

		return analyzer;
	}
	
	@Test
	public void testReferenceSetFromProblemFactory() throws IOException {
		NondominatedPopulation actual = generate().getReferenceSet();
		
		NondominatedPopulation expected = new EpsilonBoxDominanceArchive(0.01, Population.loadObjectives(
				new File("./pf/DTLZ2.2D.pf")));
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testReferenceSetFromFile() throws IOException {
		NondominatedPopulation actual = generate()
				.withReferenceSet(new File("./pf/DTLZ1.2D.pf"))
				.getReferenceSet();
		
		NondominatedPopulation expected = new EpsilonBoxDominanceArchive(0.01, Population.loadObjectives(
				new File("./pf/DTLZ1.2D.pf")));
		
		Assert.assertEquals(expected, actual);
	}

}
