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
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.swing.JFrame;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.NondominatedPopulation;

/**
 * These tests do not check for the correctness of the plots, only that the code runs without error.
 */
public class PlotTest {
	
	public static boolean hasDisplay() {
		return !GraphicsEnvironment.isHeadless() &&
				GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length > 0;
	}
	
	@Test
	public void testEmpty() {
		runTest(new Plot());
	}
	
	@Test
	public void testBasicShapes() {
		runTest(new Plot()
				.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.stacked("Stacked 1", new double[] { 0.5, 1.5 }, new double[] { 0.5, 0.6 })
				.stacked("Stacked 2", new double[] { 0.5, 1.5 }, new double[] { 0.3, 0.2 })
				.area("Area", new double[] { 0, 1, 2 }, new double[] { 0, 0.5, 0 })
				.setTitle("Basic Shapes")
				.setXLabel("X")
				.setYLabel("Y"));
	}
	
	@Test
	public void testOutOfOrder() {
		runTest(new Plot()
				.scatter("Points", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.area("Area", new double[] { 0, 2, 1 }, new double[] { 0, 0.5, 0 }));
	}
	
	@Test
	public void testParetoFront() {
		NondominatedPopulation result = new Executor()
				.withProblem("UF1")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(20)
				.withProperty("populationSize", 20)
				.run();
		
		runTest(new Plot().add("NSGAII", result).withSize(5.0f).withPaint(Color.BLACK));
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
		
		runTest(new Plot().heatMap("Test", x, y, z).setXLabel("X").setYLabel("Y"));
	}
	
	@Test
	public void testAnalyzer() {
		String problem = "ZDT1";
		String[] algorithms = { "NSGAII", "eMOEA", "OMOPSO" };

		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeGenerationalDistance()
				.includeAdditiveEpsilonIndicator()
				.includeInvertedGenerationalDistance();

		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, executor.withAlgorithm(algorithm).runSeeds(10));
		}

		runTest(new Plot().add(analyzer));
	}
	
	@Test
	public void testObservations() {
		Instrumenter instrumenter = new Instrumenter()
				.withProblem("UF1")
				.withFrequency(100)
				.attachElapsedTimeCollector()
				.attachGenerationalDistanceCollector();

		new Executor()
				.withProblem("UF1")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.withInstrumenter(instrumenter)
				.run();

		Observations observations = instrumenter.getObservations();
		
		runTest(new Plot().add(observations));
	}
	
	@Test
	public void testSavePNG() throws IOException {
		File tempFile = File.createTempFile("test", ".png");
		tempFile.deleteOnExit();
		
		new Plot()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
		
		Assert.assertTrue(tempFile.exists());
		Assert.assertTrue(tempFile.length() > 0);
	}
	
	@Test
	public void testSaveSVG() throws IOException {
		Assume.assumeTrue("Skipping test as JFreeSVG library is not found", Plot.supportsSVG());
		
		File tempFile = File.createTempFile("test", ".svg");
		tempFile.deleteOnExit();
			
		new Plot()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
			
		Assert.assertTrue(tempFile.exists());
		Assert.assertTrue(tempFile.length() > 0);
	}
	
	public void runTest(Plot plot) {
		if (TestUtils.isJUnitTest()) {
			Assume.assumeTrue("Skipping test as the system has no display", hasDisplay());
			
			JFrame frame = plot.show();
			frame.dispose();
		} else {
			plot.showDialog();
		}
	}
	
	public static void main(String[] args) {
		new PlotTest().testEmpty();
		new PlotTest().testBasicShapes();
		new PlotTest().testOutOfOrder();
		new PlotTest().testParetoFront();
		new PlotTest().testHeatMap();
		new PlotTest().testAnalyzer();
		new PlotTest().testObservations();
	}

}
