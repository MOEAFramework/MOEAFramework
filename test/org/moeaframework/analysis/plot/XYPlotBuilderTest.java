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

import java.io.IOException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.jfree.chart.StandardChartTheme;
import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.stream.DataStream;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.PRNG;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

public class XYPlotBuilderTest extends AbstractPlotBuilderTest {

	@Test
	public void linePlotXYData() {
		int N = 100;
		double[] x = new double[N];
		double[] y = new double[N];
		
		for (int i = 0; i < N; i++) {
			x[i] = -1.0 + 2.0 * i / (N - 1);
			y[i] = Math.pow(x[i], 2.0);
		}
		
		new XYPlotBuilder()
				.line("Series", x, y)
				.title("Line Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void linePlotPartition() {
		Partition<Double, Double> data = DataStream.range(-1.0, 1.0, 100).map(d -> Math.pow(d, 2));
		
		new XYPlotBuilder()
				.line("Series", data)
				.title("Line Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void scatterPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		new XYPlotBuilder()
				.scatter("Series", x, y)
				.title("Scatter Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void combinedPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
		double[] y1 = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		double[] y2 = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, 0.05)).toArray();
		
		new XYPlotBuilder()
				.line("Series1", x, y1, Style.blue(), Style.large())
				.scatter("Series2", x, y2, Style.red(), Style.circle())
				.title("Combined Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void areaPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
		double[] y = DoubleStream.of(x).map(d -> 1.0 - Math.pow(d, 2)).toArray();
		
		new XYPlotBuilder()
				.area("Series", x, y)
				.title("Area Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void stackedAreaPlot() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
		double[] y1 = DoubleStream.of(x).map(d -> 1.0 - Math.pow(d, 2)).toArray();
		double[] y2 = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
		
		new XYPlotBuilder()
				.stacked("Series1", x, y1)
				.stacked("Series2", x, y2)
				.title("Stacked Area Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void histogram() {
		double[] values = IntStream.range(0, 10000).mapToDouble(i -> PRNG.nextGaussian()).toArray();
		
		new XYPlotBuilder()
				.histogram("Values", values)
				.title("Histogram")
				.xLabel("Value")
				.yLabel("Count")
				.show();
	}
	
	@Test
	public void deviationPlot() {
		double[] x = IntStream.range(0, 10000).mapToDouble(i -> i / 1000.0).toArray();
		double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, d)).toArray();
		
		new XYPlotBuilder()
				.deviation("Series", x, y)
				.title("Deviation Plot")
				.xLabel("X")
				.yLabel("Y")
				.show();
	}
	
	@Test
	public void paretoFront() {
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
	public void resultSeries() throws IOException {
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
	
	@Test
	public void darkTheme() throws IOException {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
		double[] y = DoubleStream.of(x).map(d -> 1.0 - Math.pow(d, 2)).toArray();
		
		new XYPlotBuilder()
				.scatter("Points", x, y)
				.theme(StandardChartTheme.createDarknessTheme())
				.show();
	}

}
