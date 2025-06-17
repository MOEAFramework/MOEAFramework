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
package org.moeaframework.examples.misc;

import java.io.IOException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.jfree.chart.StandardChartTheme;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.plot.BarGraphBuilder;
import org.moeaframework.analysis.plot.BoxAndWhiskerPlotBuilder;
import org.moeaframework.analysis.plot.HeatMapBuilder;
import org.moeaframework.analysis.plot.Style;
import org.moeaframework.analysis.plot.XYPlotBuilder;
import org.moeaframework.analysis.plot.style.HighPercentileAttribute;
import org.moeaframework.analysis.plot.style.LowPercentileAttribute;
import org.moeaframework.analysis.plot.style.StepsAttribute;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.PRNG;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates various plotting capabilities.
 */
public class PlottingExample {

	public static void main(String[] args) throws IOException {
		linePlot();
		styledLinePlot();
		
		scatterPlot();
		styledScatterPlot();
		
		areaPlot();
		styledAreaPlot();
		
		histogram();
		styledHistogram();
		
		deviationPlot();
		styledDeviationPlot();
		
		paretoFront();
		
		resultSeries();
		
		heatMap();
		
		boxAndWhiskerPlot();
		
		barGraph();
		
		darkTheme();
	}
	
	public static void linePlot() {
		// begin-example: linePlot
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		new XYPlotBuilder()
				.line("Series", x, y)
				.title("Line Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: linePlot
	}
	
	public static void styledLinePlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: styledLinePlot
		new XYPlotBuilder()
				.line("Series", x, y, Style.blue(), Style.large())
				.title("Styled Line Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: styledLinePlot
	}
	
	public static void scatterPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: scatterPlot
		new XYPlotBuilder()
				.scatter("Series", x, y)
				.title("Scatter Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: scatterPlot
	}
	
	public static void styledScatterPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: styledScatterPlot
		new XYPlotBuilder()
				.scatter("Series", x, y, Style.blue(), Style.square(), Style.large())
				.title("Styled Scatter Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: styledScatterPlot
	}
	
	public static void areaPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: areaPlot
		new XYPlotBuilder()
				.area("Series", x, y)
				.title("Area Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: areaPlot
	}
	
	public static void styledAreaPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i - 50).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		// begin-example: styledAreaPlot
		new XYPlotBuilder()
				.area("Series", x, y, Style.blue())
				.title("Styled Area Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: styledAreaPlot
	}
	
	public static void histogram() {
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
	
	public static void styledHistogram() {
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
	
	public static void deviationPlot() {
		double[] x = IntStream.range(0, 10000).mapToDouble(i -> i / 1000.0).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, d)).toArray();
		
		// begin-example: deviationPlot
		new XYPlotBuilder()
				.deviation("Series", x, y)
				.title("Deviation Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: deviationPlot
	}
	
	public static void styledDeviationPlot() {
		double[] x = IntStream.range(0, 10000).mapToDouble(i -> i / 1000.0).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, d)).toArray();
		
		// begin-example: styledDeviationPlot
		new XYPlotBuilder()
				.deviation("Series", x, y, Style.blue(), LowPercentileAttribute.of(0.0), HighPercentileAttribute.of(100.0))
				.title("Styled Deviation Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
		// end-example: styledDeviationPlot
	}
	
	public static void paretoFront() {
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
	
	public static void resultSeries() throws IOException {
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
				.title("Result Series");
	}
	
	public static void heatMap() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i).toArray();
		double[] y = IntStream.range(0, 200).mapToDouble(i -> i).toArray();
		double[][] z = new double[x.length][y.length];
		
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				z[i][j] = i * Math.sqrt(j);
			}
		}
		
		new HeatMapBuilder()
				.xCoords(x)
				.yCoords(y)
				.zData(z)
				.style(Style.showToolTips(), Style.red())
				.xLabel("X")
				.yLabel("Y")
				.zLabel("Value")
				.show();
	}
	
	public static void boxAndWhiskerPlot() {
		new BoxAndWhiskerPlotBuilder()
				.add("Set 1", IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray())
				.add("Set 2", IntStream.range(0, 50).mapToDouble(i -> 2 * PRNG.nextDouble()).toArray())
				.add("Set 3", IntStream.range(0, 100).mapToDouble(i -> PRNG.nextDouble()).toArray())
				.show();
	}
	
	public static void barGraph() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> i).toArray();
		double[] y1 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		double[] y2 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new BarGraphBuilder()
				.bars("Set 1", x, y1)
				.bars("Set 2", x, y2)
				.show();
	}
	
	public static void darkTheme() throws IOException {
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.theme(StandardChartTheme.createDarknessTheme())
			.show();
	}

}
