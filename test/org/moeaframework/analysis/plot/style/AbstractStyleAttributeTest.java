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
package org.moeaframework.analysis.plot.style;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.analysis.plot.PlotBuilder;

@Ignore("Abstract test class")
public abstract class AbstractStyleAttributeTest<T extends StyleAttribute> {
	
	public abstract T createInstance();
	
	public void assertStyle(Plot plot) {
	
	}
	
	@Test
	public void testEmptyXYPlot() {
		T instance = createInstance();
		
		JFreeChart chart = PlotBuilder.xy().build();
		instance.apply(chart.getPlot(), 0);
		
		assertStyle(chart.getPlot());
	}
	
	@Test
	public void testXYPlot() {
		T instance = createInstance();

		JFreeChart chart = PlotBuilder.xy()
				.scatter("test", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.build();
		instance.apply(chart.getPlot(), 0);
		instance.apply(chart.getPlot(), 0, 0);
		
		assertStyle(chart.getPlot());
	}
	
	@Test
	public void testEmptyCategoricallot() {
		T instance = createInstance();
		
		JFreeChart chart = PlotBuilder.barGraph().build();
		instance.apply(chart.getPlot(), 0);
		
		assertStyle(chart.getPlot());
	}
	
	@Test
	public void testCategoricalPlot() {
		T instance = createInstance();

		JFreeChart chart = PlotBuilder.barGraph()
				.bars("test", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
				.build();
		instance.apply(chart.getPlot(), 0);
		instance.apply(chart.getPlot(), 0, 0);
		
		assertStyle(chart.getPlot());
	}
	
	@Test
	public void testHeatMap() {
		T instance = createInstance();

		JFreeChart chart = PlotBuilder.heatMap()
				.xCoords(new double[] { 0, 1 })
				.yCoords(new double[] { 0, 1 })
				.zData(new double[][] {{ 0.0, 0.0 }, { 1.0, 1.0 }})
				.build();
		instance.apply(chart.getPlot(), 0);
		instance.apply(chart.getPlot(), 0, 0);
		
		assertStyle(chart.getPlot());
	}
	
	@Test
	public void testBoxAndWhisker() {
		T instance = createInstance();

		JFreeChart chart = PlotBuilder.boxAndWhisker()
				.add("test", new double[] { 0, 1, 2 })
				.build();
		instance.apply(chart.getPlot(), 0);
		instance.apply(chart.getPlot(), 0, 0);
		
		assertStyle(chart.getPlot());
	}

}
