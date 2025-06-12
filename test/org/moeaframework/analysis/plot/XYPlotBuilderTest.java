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
import java.util.stream.IntStream;

import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.Frequency;
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
	public void testBasicShapes() {
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
	public void testStyle() {
		new XYPlotBuilder()
				.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 }, Style.green(), Style.large(),
						Style.square(), Style.showLabels(), Style.showToolTips())
				.line("Line", new double[] { 0, 1, 2 }, new double[] { 1, 2, 0 }, Style.red(), Style.large(),
						Style.showLabels(), Style.showToolTips())
				.title("Style")
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
				.show();
	}
	
	@Test
	public void testParetoFront() {
		Problem problem = new UF1();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(20);
		
		new XYPlotBuilder()
				.scatter("NSGAII", algorithm.getResult(), SizeAttribute.of(5f), Style.black())
				.show();
	}
	
	@Test
	public void testHistogram() {
		double[] values = IntStream.range(0, 1000).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new XYPlotBuilder()
				.histogram("Test", values)
				.xLabel("Value")
				.yLabel("Count")
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
				.show();
	}
	
	public static void main(String[] args) throws Exception {
		new XYPlotBuilderTest().runAll();
	}

}
