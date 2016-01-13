/* Copyright 2009-2016 David Hadka
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

import javax.swing.JFrame;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;

/**
 * Tests the {@link Plot} class.  These tests do not check for the correctness
 * of the plots, only that the code runs without error.
 */
public class PlotTest {
	
	public static boolean isJUnitTest() {
	    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	    
	    for (StackTraceElement element : stackTrace) {
	        if (element.getClassName().startsWith("org.junit.")) {
	            return true;
	        }           
	    }
	    
	    return false;
	}
	
	@Test
	@Ignore("Fails on TravisCI due to missing display")
	public void testEmpty() {
		runTest(new Plot());
	}
	
	@Test
	@Ignore("Fails on TravisCI due to missing display")
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
	@Ignore("Fails on TravisCI due to missing display")
	public void testOutOfOrder() {
		runTest(new Plot()
				.scatter("Points", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.area("Area", new double[] { 0, 2, 1 }, new double[] { 0, 0.5, 0 }));
	}
	
	@Test
	@Ignore("Fails on TravisCI due to missing display")
	public void testParetoFront() {
		NondominatedPopulation result = new Executor()
				.withProblem("UF1")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(20)
				.withProperty("populationSize", 20)
				.run();
		
		runTest(new Plot().add("NSGAII", result));
	}
	
	@Test
	@Ignore("Fails on TravisCI due to missing display")
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
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(10));
		}

		runTest(new Plot().add(analyzer));
	}
	
	@Test
	@Ignore("Fails on TravisCI due to missing display")
	public void testAccumulator() {
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

		Accumulator accumulator = instrumenter.getLastAccumulator();
		
		runTest(new Plot().add(accumulator));
	}
	
	public void runTest(Plot plot) {
		if (isJUnitTest()) {
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
		new PlotTest().testAnalyzer();
		new PlotTest().testAccumulator();
	}

}
