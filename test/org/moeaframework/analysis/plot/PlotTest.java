package org.moeaframework.analysis.plot;

import javax.swing.JFrame;

import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;

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
		
		runTest(new Plot().add("NSGAII", result));
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
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(10));
		}

		runTest(new Plot().add(analyzer));
	}
	
	@Test
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
