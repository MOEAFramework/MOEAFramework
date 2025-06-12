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

import java.util.List;
import java.util.stream.DoubleStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.analysis.plot.style.StyleAttribute;
import org.moeaframework.analysis.stream.DataStream;

/**
 * Constructs a box-and-whisker plot.
 */
public class BoxAndWhiskerPlotBuilder extends PlotBuilder<BoxAndWhiskerPlotBuilder> {
	
	private final CategoryAxis xAxis;
	
	private final NumberAxis yAxis;
	
	private final CategoryPlot plot;
	
	private final DefaultBoxAndWhiskerCategoryDataset dataset;
	
	/**
	 * Constructs a new, empty box-and-whisker plot.
	 */
	public BoxAndWhiskerPlotBuilder() {
		super();
		
		xAxis = new CategoryAxis("");

		yAxis = new NumberAxis("Value");
		yAxis.setAutoRangeIncludesZero(false);
		
		dataset = new DefaultBoxAndWhiskerCategoryDataset();

		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot = new CategoryPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setRenderer(renderer);
		plot.setDataset(dataset);
		
		noLegend();
	}
	
	@Override
	protected BoxAndWhiskerPlotBuilder getInstance() {
		return this;
	}
	
	@Override
	public JFreeChart build() {
		return build(plot);
	}
	
	/**
	 * Adds a new box-and-whisker entry to this plot.
	 * 
	 * @param label the label for the series
	 * @param values the values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public BoxAndWhiskerPlotBuilder add(String label, double[] values, StyleAttribute... style) {
		return add(label, DoubleStream.of(values).boxed().toList(), style);
	}
	
	/**
	 * Adds a new box-and-whisker entry to this plot.
	 * 
	 * @param label the label for the series
	 * @param stream the data stream containing the values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public BoxAndWhiskerPlotBuilder add(String label, DataStream<? extends Number> stream, StyleAttribute... style) {
		return add(label, stream.values(), style);
	}
	
	/**
	 * Adds a new box-and-whisker entry to this plot.
	 * 
	 * @param label the label for the series
	 * @param values the values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public BoxAndWhiskerPlotBuilder add(String label, List<? extends Number> values, StyleAttribute... style) {
		dataset.add(values, "", label);
		
		applyStyle(plot, 0, dataset.getColumnCount() - 1, style);
		
		return getInstance();
	}
	
	/**
	 * Adds a new box-and-whisker entry for each indicator in the given statistics.
	 * 
	 * @param statistics the indicator statistics
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public BoxAndWhiskerPlotBuilder add(IndicatorStatistics statistics, StyleAttribute... style) {
		for (String name : statistics.getGroupNames()) {
			add(name, statistics.getValues(name), style);
		}

		return getInstance();
	}
	
}
