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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TestResources;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;
import org.moeaframework.problem.Problem;

public class ExtractDataTest {
	
	public static final String COMPLETE = """
		# Problem = DTLZ2_2
		# Variables = 11
		# Objectives = 2
		//ElapsedTime=0.0125
		//TotalTime=0.214
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25
		#
		//ElapsedTime=0.01549
		//TotalTime=0.209186
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25
		#
		""";
	
	public static final String MISSING_PROPERTY = """
		# Problem = DTLZ2_2
		# Variables = 11
		# Objectives = 2
		//ElapsedTime=0.0125
		//TotalTime=0.214
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25
		#
		//ElapsedTime=0.01549
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
		0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.75 0.25
		#
		""";
	
	@Test
	public void testComplete() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"ElapsedTime", "TotalTime"});
		
		try (BufferedReader reader = new BufferedReader(new FileReader(output))) {
			Assert.assertEquals("#ElapsedTime TotalTime", reader.readLine());
			Assert.assertEquals("0.0125 0.214", reader.readLine());
			Assert.assertEquals("0.01549 0.209186", reader.readLine());
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testClose() throws Exception {
		ProblemFactoryTestWrapper problemFactory = new ProblemFactoryTestWrapper();
		ProblemFactory.setInstance(problemFactory);
		
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		
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
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--input", input.getPath(),
			"--output", output.getPath(),
			"GenerationalDistance", "Hypervolume", "Spacing" });
		
		Indicators indicators = Indicators.standard(problem, referenceSet);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(output));
				ResultFileReader resultReader = new ResultFileReader(problem, input)) {
			Assert.assertEquals("#GenerationalDistance Hypervolume Spacing", reader.readLine());
				
			NondominatedPopulation population = resultReader.next().getPopulation();
			IndicatorValues values = indicators.apply(population);
			Assert.assertEquals(values.getGenerationalDistance() + " " + 
					values.getHypervolume() + " " +
					values.getSpacing(), 
					reader.readLine());
				
			population = resultReader.next().getPopulation();
			values = indicators.apply(population);
			Assert.assertEquals(values.getGenerationalDistance() + " " + 
					values.getHypervolume() + " " +
					values.getSpacing(), 
					reader.readLine());
				
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testContributionWithEpsilon() throws Exception {
		double epsilon = 0.1;
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.2D.pf");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getPath(),
			"--output", output.getPath(),
			"--epsilon", Double.toString(epsilon),
			"Contribution" });

		Contribution contribution = new Contribution(referenceSet, epsilon);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(output));
				ResultFileReader resultReader = new ResultFileReader(problem, input)) {
			Assert.assertEquals("#Contribution", reader.readLine());
				
			NondominatedPopulation population = resultReader.next().getPopulation();
			Assert.assertEquals("" + contribution.evaluate(population), reader.readLine());
				
			population = resultReader.next().getPopulation();
			Assert.assertEquals("" + contribution.evaluate(population), reader.readLine());
				
			Assert.assertNull(reader.readLine());
		}
	}
	
	@Test
	public void testContributionWithoutEpsilon() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.2D.pf");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		
		ExtractData.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getPath(),
			"--output", output.getPath(),
			"Contribution" });
		
		Contribution contribution = new Contribution(referenceSet);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(output));
				ResultFileReader resultReader = new ResultFileReader(problem, input)) {
			Assert.assertEquals("#Contribution", reader.readLine());
				
			NondominatedPopulation population = resultReader.next().getPopulation();
			Assert.assertEquals("" + contribution.evaluate(population), reader.readLine());
				
			population = resultReader.next().getPopulation();
			Assert.assertEquals("" + contribution.evaluate(population), reader.readLine());
				
			Assert.assertNull(reader.readLine());
		}
	}
	
}
