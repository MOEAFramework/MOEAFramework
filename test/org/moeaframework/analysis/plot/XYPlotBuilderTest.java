/* Copyright 2009-2025 David Hadka
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
import java.io.IOException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.plot.style.HighPercentileAttribute;
import org.moeaframework.analysis.plot.style.LowPercentileAttribute;
import org.moeaframework.analysis.plot.style.StepsAttribute;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.PRNG;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

public class XYPlotBuilderTest extends AbstractPlotTest {

	@Test
	public void testEmpty() {
		new XYPlotBuilder().show();
	}
	

	@Test
	public void testLineSeries() {
		// begin-example: lineSeries
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		new XYPlotBuilder()
				.line("Series", x, y)
				.title("Line Series")
				.show();
		// end-example: lineSeries
	}
	
	@Test
	public void testStyledLineSeries() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: styledLineSeries
		new XYPlotBuilder()
				.line("Series", x, y, Style.color(Color.BLUE), Style.size(20))
				.title("Styled Line Series")
				.show();
		// end-example: styledLineSeries
	}
	
	@Test
	public void testTitleAndLabels() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: titleAndLabels
		new XYPlotBuilder()
				.line("Series", x, y)
				.title("Line Plot")
				.xLabel("x")
				.yLabel("f(x)")
				.show();
		// end-example: titleAndLabels
	}
	
	@Test
	public void testScatterPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: scatterPlot
		new XYPlotBuilder()
				.scatter("Series", x, y)
				.title("Scatter Plot")
				.xLabel("x")
				.yLabel("f(x)")
				.show();
		// end-example: scatterPlot
	}
	
	@Test
	public void testStyledScatterPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: styledScatterPlot
		new XYPlotBuilder()
				.scatter("Series", x, y, Style.blue(), Style.square(), Style.large())
				.title("Styled Scatter Plot")
				.xLabel("x")
				.yLabel("f(x)")
				.show();
		// end-example: styledScatterPlot
	}
	
	@Test
	public void testHistogram() {
		// begin-example: histogram
		double[] values = IntStream.range(0, 10000).mapToDouble(i -> PRNG.nextGaussian()).toArray();
		
		new XYPlotBuilder()
				.histogram("Values", values)
				.title("Histogram")
				.xLabel("Value")
				.yLabel("Count")
				.show();
		// end-example: histogram
	}
	
	@Test
	public void testStyledHistogram() {
		double[] values = IntStream.range(0, 10000).mapToDouble(i -> PRNG.nextGaussian()).toArray();
		
		// begin-example: styledHistogram
		new XYPlotBuilder()
				.histogram("Values", values, StepsAttribute.of(20))
				.title("Styled Histogram")
				.xLabel("Value")
				.yLabel("Count")
				.show();
		// end-example: styledHistogram
	}
	
	@Test
	public void testDeviationPlot() {
		double[] x = IntStream.range(0, 10000).mapToDouble(i -> i / 1000.0).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, d)).toArray();
		
		// begin-example: deviationPlot
		new XYPlotBuilder()
				.deviation("Series", x, y)
				.title("Deviation Plot")
				.xLabel("x")
				.yLabel("f(x)")
				.show();
		// end-example: deviationPlot
	}
	
	@Test
	public void testStyledDeviationPlot() {
		double[] x = IntStream.range(0, 10000).mapToDouble(i -> i / 1000.0).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, d)).toArray();
		
		// begin-example: styledDeviationPlot
		new XYPlotBuilder()
				.deviation("Series", x, y, Style.blue(), LowPercentileAttribute.of(0.0), HighPercentileAttribute.of(100.0))
				.title("Styled Deviation Plot")
				.xLabel("x")
				.yLabel("f(x)")
				.show();
		// end-example: styledDeviationPlot
	}
	
	@Test
	public void testMultipleSeries() {
		new XYPlotBuilder()
				.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 1, 2 }, new double[] { 0, 2, 0 })
				.stacked("Stacked 1", new double[] { 0.5, 1.5 }, new double[] { 0.5, 0.6 })
				.stacked("Stacked 2", new double[] { 0.5, 1.5 }, new double[] { 0.3, 0.2 })
				.area("Area", new double[] { 0, 1, 2 }, new double[] { 0, 0.5, 0 })
				.title("Basic Shapes")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void testOutOfOrder() {
		new XYPlotBuilder()
				.scatter("Points", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.line("Line", new double[] { 0, 2, 1 }, new double[] { 0, 1, 2 })
				.area("Area", new double[] { 0, 2, 1 }, new double[] { 0, 0.5, 0 })
				.title("Out of Order")
				.show();
	}
	
	@Test
	public void testParetoFront() {
		Problem problem = new UF1();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		new XYPlotBuilder()
				.scatter("NSGAII", algorithm.getResult())
				.title("Pareto Front")
				.xLabel("Objective 1")
				.yLabel("Objective 2")
				.show();
	}
	
	@Test
	public void testResultSeries() throws IOException {
		Problem problem = new UF1();
		
		Instrumenter instrumenter = new Instrumenter()
				.withReferenceSet("pf/UF1.pf")
				.withFrequency(Frequency.ofEvaluations(100))
				.attachElapsedTimeCollector()
				.attachGenerationalDistanceCollector();
		
		NSGAII algorithm = new NSGAII(problem);
		
		InstrumentedAlgorithm<?> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		instrumentedAlgorithm.run(10000);

		ResultSeries series = instrumentedAlgorithm.getSeries();
		
		new XYPlotBuilder()
				.lines(series)
				.title("Result Series")
				.show();
	}
	
	public static void main(String[] args) throws Exception {
		new XYPlotBuilderTest().runAll();
	}

}
