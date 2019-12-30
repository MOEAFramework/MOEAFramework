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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;

/**
 * Tests the {@link ExtractData} class.
 */
public class ExtractDataTest {
	
	public static final String COMPLETE = 
		"# Problem = DTLZ2\n" +
		"# Variables = 11\n" +
		"# Objectives = 2\n" + 
		"//ElapsedTime=0.0125\n" +
		"//TotalTime=0.214\n" +
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75\n" + 
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25\n" + 
		"#\n" +
		"//ElapsedTime=0.01549\n" +
		"//TotalTime=0.209186\n" +
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75\n" + 
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25\n" + 
		"#\n";
	
	public static final String MISSING_PROPERTY = 
		"# Problem = DTLZ2\n" +
		"# Variables = 11\n" +
		"# Objectives = 2\n" + 
		"//ElapsedTime=0.0125\n" +
		"//TotalTime=0.214\n" +
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75\n" + 
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25\n" + 
		"#\n" +
		"//ElapsedTime=0.01549\n" +
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75\n" + 
		"0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25\n" + 
		"#\n";
	
	@Test
	public void testComplete() throws Exception {
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"ElapsedTime", "TotalTime"});
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(output));
			Assert.assertEquals("#ElapsedTime TotalTime", reader.readLine());
			Assert.assertEquals("0.0125 0.214", reader.readLine());
			Assert.assertEquals("0.01549 0.209186", reader.readLine());
			Assert.assertNull(reader.readLine());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test
	public void testClose() throws Exception {
		ProblemFactoryTestWrapper problemFactory = new ProblemFactoryTestWrapper();
		ProblemFactory.setInstance(problemFactory);
		
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"ElapsedTime", "TotalTime"});
		
		Assert.assertEquals(1, problemFactory.getCloseCount());
		ProblemFactory.setInstance(new ProblemFactory());
	}
	
	@Test
	public void testMetrics() throws Exception {
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"+ge", "+hyp", "+inv", "+err", "+spa", "+eps" });
		
		BufferedReader reader = null;
		ResultFileReader resultReader = null;
		
		QualityIndicator indicator = new QualityIndicator(problem, 
				referenceSet);
		
		try {
			reader = new BufferedReader(new FileReader(output));
			
			try {
				resultReader = new ResultFileReader(problem, input);
				
				Assert.assertEquals("#+ge +hyp +inv +err +spa +eps", 
						reader.readLine());
				
				NondominatedPopulation population = 
						resultReader.next().getPopulation();
				indicator.calculate(population);
				Assert.assertEquals(indicator.getGenerationalDistance() + " " + 
						indicator.getHypervolume() + " " +
						indicator.getInvertedGenerationalDistance() + " " +
						indicator.getMaximumParetoFrontError() + " " + 
						indicator.getSpacing() + " " + 
						indicator.getAdditiveEpsilonIndicator(), 
						reader.readLine());
				
				population = resultReader.next().getPopulation();
				indicator.calculate(population);
				Assert.assertEquals(indicator.getGenerationalDistance() + " " + 
						indicator.getHypervolume() + " " +
						indicator.getInvertedGenerationalDistance() + " " +
						indicator.getMaximumParetoFrontError() + " " + 
						indicator.getSpacing() + " " + 
						indicator.getAdditiveEpsilonIndicator(), 
						reader.readLine());
				
				Assert.assertNull(reader.readLine());
			} finally {
				if (resultReader != null) {
					resultReader.close();
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test
	public void testContributionWithEpsilon() throws Exception {
		double epsilon = 0.1;
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");
		
		ExtractData.main(new String[] {
			"--dimension", "2",
			"--reference", new File("./pf/DTLZ2.2D.pf").getAbsolutePath(),
			"--input", input.getPath(),
			"--output", output.getPath(),
			"--epsilon", Double.toString(epsilon),
			"+con" });
		
		BufferedReader reader = null;
		ResultFileReader resultReader = null;
		Contribution contribution = new Contribution(referenceSet, epsilon);
		
		try {
			reader = new BufferedReader(new FileReader(output));
			
			try {
				resultReader = new ResultFileReader(problem, input);
				
				Assert.assertEquals("#+con", reader.readLine());
				
				NondominatedPopulation population = 
						resultReader.next().getPopulation();
				Assert.assertEquals("" + contribution.evaluate(population), 
						reader.readLine());
				
				population = resultReader.next().getPopulation();
				Assert.assertEquals("" + contribution.evaluate(population), 
						reader.readLine());
				
				Assert.assertNull(reader.readLine());
			} finally {
				if (resultReader != null) {
					resultReader.close();
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test
	public void testContributionWithoutEpsilon() throws Exception {
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");
		
		ExtractData.main(new String[] {
			"--dimension", "2",
			"--reference", new File("./pf/DTLZ2.2D.pf").getAbsolutePath(),
			"--input", input.getPath(),
			"--output", output.getPath(),
			"+con" });
		
		BufferedReader reader = null;
		ResultFileReader resultReader = null;
		Contribution contribution = new Contribution(referenceSet);
		
		try {
			reader = new BufferedReader(new FileReader(output));
			
			try {
				resultReader = new ResultFileReader(problem, input);
				
				Assert.assertEquals("#+con", reader.readLine());
				
				NondominatedPopulation population = 
						resultReader.next().getPopulation();
				Assert.assertEquals("" + contribution.evaluate(population), 
						reader.readLine());
				
				population = resultReader.next().getPopulation();
				Assert.assertEquals("" + contribution.evaluate(population), 
						reader.readLine());
				
				Assert.assertNull(reader.readLine());
			} finally {
				if (resultReader != null) {
					resultReader.close();
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
}
