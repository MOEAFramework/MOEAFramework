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
package org.moeaframework.analysis.plot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.TempFiles;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.analysis.runtime.Observations;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.CEC2009.UF1;

/**
 * These tests do not check for the correctness of the plots, only that the code runs without error.
 */
public class PlotTest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@After
	public void tearDown() {
		if (isJUnitTest()) {
			Plot.disposeAll();
		}
	}
	
	@Test
	public void testEmpty() {
		new Plot().show();
	}
	
	@Test
	public void testBasicShapes() {
		new Plot()
				.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.stacked("Stacked 1", new double[] { 0.5, 1.5 }, new double[] { 0.5, 0.6 })
				.stacked("Stacked 2", new double[] { 0.5, 1.5 }, new double[] { 0.3, 0.2 })
				.area("Area", new double[] { 0, 1, 2 }, new double[] { 0, 0.5, 0 })
				.setTitle("Basic Shapes")
				.setXLabel("X")
				.setYLabel("Y")
				.show();
	}
	
	@Test
	public void testOutOfOrder() {
		new Plot()
				.scatter("Points", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.area("Area", new double[] { 0, 2, 1 }, new double[] { 0, 0.5, 0 })
				.show();
	}
	
	@Test
	public void testParetoFront() {
		Problem problem = new UF1();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(20);
		
		new Plot().add("NSGAII", algorithm.getResult())
				.withSize(5.0f)
				.withPaint(Color.BLACK)
				.show();
	}
	
	@Test
	public void testHeatMap() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> (double)i).toArray();
		double[] y = IntStream.range(0, 20).mapToDouble(i -> (double)i).toArray();
		double[][] z = new double[x.length][y.length];
		
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				z[i][j] = i*j;
			}
		}
		
		new Plot()
				.heatMap("Test", x, y, z)
				.setXLabel("X")
				.setYLabel("Y")
				.show();
	}
	
	@Test
	public void testHistogram() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> (double)i).toArray();
		double[] y = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new Plot()
				.histogram("Test", x, y)
				.setXLabel("X")
				.setYLabel("Y")
				.show();
	}
	
	@Test
	public void testIndicatorStatistics() {
		Problem problem = ProblemFactory.getInstance().getProblem("ZDT1");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("ZDT1");
		
		String[] algorithms = { "NSGAII", "eMOEA", "OMOPSO" };
		
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		IndicatorStatistics statistics = new IndicatorStatistics(hypervolume);
		
		for (String name : algorithms) {
			for (int i = 0; i < 10; i++) {
				Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, problem);
				algorithm.run(10000);
				statistics.add(name, algorithm.getResult());
			}
		}

		new Plot().add(statistics).show();
	}
	
	@Test
	public void testObservations() throws IOException {
		Problem problem = new UF1();
		
		Instrumenter instrumenter = new Instrumenter()
				.withReferenceSet("pf/UF1.pf")
				.withFrequency(100)
				.attachElapsedTimeCollector()
				.attachGenerationalDistanceCollector();
		
		NSGAII algorithm = new NSGAII(problem);
		
		InstrumentedAlgorithm<?> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		instrumentedAlgorithm.run(10000);

		Observations observations = instrumentedAlgorithm.getObservations();
		
		new Plot().add(observations).show();
	}
	
	@Test
	public void testSavePNG() throws IOException {
		File tempFile = TempFiles.createFileWithExtension(".png");
		
		new Plot()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
		
		Assert.assertFileWithContent(tempFile);
	}
	
	@Test
	public void testSaveSVG() throws IOException {
		File tempFile = TempFiles.createFileWithExtension(".svg");
			
		new Plot()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
			
		Assert.assertFileWithContent(tempFile);
	}

	public static boolean isJUnitTest() {
	    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	    
	    for (StackTraceElement element : stackTrace) {
	        if (element.getClassName().startsWith("org.junit.")) {
	            return true;
	        }           
	    }
	    
	    return false;
	}
	
	public static void main(String[] args) throws IOException {
		new PlotTest().testEmpty();
		new PlotTest().testBasicShapes();
		new PlotTest().testOutOfOrder();
		new PlotTest().testParetoFront();
		new PlotTest().testHeatMap();
		new PlotTest().testHistogram();
		new PlotTest().testIndicatorStatistics();
		new PlotTest().testObservations();
	}

}
