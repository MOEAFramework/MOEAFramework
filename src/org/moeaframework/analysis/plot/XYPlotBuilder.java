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

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.moeaframework.analysis.plot.style.HighPercentileAttribute;
import org.moeaframework.analysis.plot.style.LowPercentileAttribute;
import org.moeaframework.analysis.plot.style.PlotAttribute;
import org.moeaframework.analysis.plot.style.StepsAttribute;
import org.moeaframework.analysis.series.IndexType;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.validate.Validate;

/**
 * Builds 2D plots with numeric X and Y axes, including line, scatter, area, stacked area, and deviation plots.
 */
public class XYPlotBuilder extends PlotBuilder<XYPlotBuilder> {
	
	private final NumberAxis xAxis;
	
	private final NumberAxis yAxis;
	
	private final XYPlot plot;
	
	/**
	 * Constructs a new, empty XY plot.
	 */
	public XYPlotBuilder() {
		super();
		
		xAxis = new NumberAxis("");
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setAutoRangeStickyZero(false);
		
		yAxis = new NumberAxis("");
		yAxis.setAutoRangeIncludesZero(false);

		plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	@Override
	protected XYPlotBuilder getInstance() {
		return this;
	}
	
	@Override
	public JFreeChart build() {
		return build(plot);
	}
	
	/**
	 * Sets the X axis label.
	 * 
	 * @param label the label for the X axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder xLabel(String label) {
		xAxis.setLabel(label);
		return getInstance();
	}

	/**
	 * Sets the Y axis label.
	 * 
	 * @param label the label for the Y axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder yLabel(String label) {
		yAxis.setLabel(label);
		return getInstance();
	}
	
	/**
	 * Sets the X axis limits.
	 * 
	 * @param min the minimum bound
	 * @param max the maximum bound
	 * @return a reference to this builder
	 */
	public XYPlotBuilder xLim(double min, double max) {
		xAxis.setRange(min, max);
		return getInstance();
	}
	
	/**
	 * Sets the X axis limits.
	 * 
	 * @param range the minimum and maximum bounds
	 * @return a reference to this builder
	 */
	public XYPlotBuilder xLim(Range range) {
		xAxis.setRange(range);
		return getInstance();
	}

	/**
	 * Sets the Y axis limits.
	 * 
	 * @param min the minimum bound
	 * @param max the maximum bound
	 * @return a reference to this builder
	 */
	public XYPlotBuilder yLim(double min, double max) {
		yAxis.setRange(min, max);
		return getInstance();
	}
	
	/**
	 * Sets the Y axis limits.
	 * 
	 * @param range the minimum and maximum bounds
	 * @return a reference to this builder
	 */
	public XYPlotBuilder yLim(Range range) {
		yAxis.setRange(range);
		return getInstance();
	}
	
	/**
	 * Sets the grid line paint for both axes.
	 * 
	 * @param paint the paint
	 * @return a reference to this builder
	 */
	public XYPlotBuilder gridPaint(Paint paint) {
		plot.setRangeGridlinePaint(paint);
		plot.setDomainGridlinePaint(paint);
		return getInstance();
	}
	
	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, double[] x, double[] y, PlotAttribute... attributes) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}
		
		return scatter(series, attributes);
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Partition<? extends Number, ? extends Number> partition, PlotAttribute... attributes) {
		return scatter(label, partition.keys(), partition.values(), attributes);
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, List<? extends Number> x, List<? extends Number> y, PlotAttribute... attributes) {
		return scatter(label, toArray(x), toArray(y), attributes);
	}
	
	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param <D> the type of each object
	 * @param label the label for the series
	 * @param data the list of objects
	 * @param xAxis a function converting each object into a numeric value for the X axis
	 * @param yAxis a function converting each object into a numeric value for the Y axis
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public <D> XYPlotBuilder scatter(String label, List<D> data, Function<D, ? extends Number> xAxis,
			Function<D, ? extends Number> yAxis, PlotAttribute... attributes) {
		return scatter(label, toArray(data, xAxis), toArray(data, yAxis), attributes);
	}
	
	/**
	 * Creates a scatter plot representation of the feasible solutions in a population.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Population population, PlotAttribute... attributes) {
		if (population.isEmpty()) {
			return getInstance();
		}
		
		Solution solution = population.get(0);

		if (solution.getNumberOfObjectives() == 1) {
			return scatter(label, population, 0, 0, attributes);
		} else {
			return scatter(label, population, 0, 1, attributes);
		}
	}

	/**
	 * Creates a scatter plot representation of the feasible solutions in a population.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param x the objective to plot on the X axis
	 * @param y the objective to plot on the Y axis
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Population population, int x, int y, PlotAttribute... attributes) {
		return scatter(label, Iterators.materialize(population), (s) -> s.getObjectiveValue(x), (s) -> s.getObjectiveValue(y), attributes);
	}
	
	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param series the XY series of points
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	private XYPlotBuilder scatter(XYSeries series, PlotAttribute... attributes) {
		int index = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
		renderer.setDefaultShape(getDefaultShape());
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot.setDataset(index, dataset);
		plot.setRenderer(index, renderer);
		
		applyStyle(plot, index, attributes);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, double[] x, double[] y, PlotAttribute... attributes) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}
		
		return line(series, attributes);
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, Partition<? extends Number, ? extends Number>  partition, PlotAttribute... attributes) {
		return line(label, partition.keys(), partition.values(), attributes);
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, List<? extends Number> x, List<? extends Number> y, PlotAttribute... attributes) {
		return line(label, toArray(x), toArray(y), attributes);
	}
	
	/**
	 * Creates a new line plot series.
	 * 
	 * @param <D> the type of each object
	 * @param label the label for the series
	 * @param data the list of objects
	 * @param xAxis a function converting each object into a numeric value for the X axis
	 * @param yAxis a function converting each object into a numeric value for the Y axis
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public <D> XYPlotBuilder line(String label, List<D> data, Function<D, ? extends Number> xAxis,
			Function<D, ? extends Number> yAxis, PlotAttribute... attributes) {
		return line(label, toArray(data, xAxis), toArray(data, yAxis), attributes);
	}
	
	/**
	 * Creates a new line plot series.
	 * 
	 * @param series the XY series containing the data
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	private XYPlotBuilder line(XYSeries series, PlotAttribute... attributes) {
		int index = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setDefaultStroke(getDefaultStroke());
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot.setDataset(index, dataset);
		plot.setRenderer(index, renderer);
		
		applyStyle(plot, index, attributes);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}
	
	/**
	 * Creates a new line plot series of NFE versus the property value.
	 * 
	 * @param label the label for the series
	 * @param series the result series
	 * @param property the name of the property to plot
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, ResultSeries series, String property, PlotAttribute... attributes) {
		if (!series.getDefinedProperties().contains(property)) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label +
					"', the series does not define the property");
			return getInstance();
		}

		try {
			line(label, Iterators.materialize(series), (x) -> x.getIndex(), (x) -> x.getProperties().getDouble(property), attributes);
		} catch (NumberFormatException e) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label +
					"', value is not numeric: " + e.getMessage());
			return getInstance();
		}
		
		if (xAxis.getLabel() == null || xAxis.getLabel().isBlank()) {
			xAxis.setLabel("NFE");
		}
		
		if (yAxis.getLabel() == null || yAxis.getLabel().isBlank()) {
			yAxis.setLabel("Value");
		}

		return getInstance();
	}
	
	/**
	 * Creates a new line plot series of NFE versus the property value.  Each property is rendered as a separate line
	 * series.
	 * 
	 * @param series the result series
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder lines(ResultSeries series, PlotAttribute... attributes) {
		for (String key : series.getDefinedProperties()) {
			if (series.getIndexType().equals(IndexType.NFE) && key.equals(ResultEntry.NFE)) {
				continue;
			}
			
			line(key, series, key, attributes);
		}
		
		return getInstance();
	}
	
	/**
	 * Creates a new histogram showing the number of times each value occurs in the input.
	 * 
	 * @param label the label for the series
	 * @param values the values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder histogram(String label, List<? extends Number> values, PlotAttribute... attributes) {
		return histogram(label, toArray(values), attributes);
	}
	
	/**
	 * Creates a new histogram showing the number of times each value occurs in the input.
	 * 
	 * @param <D> the type of each object
	 * @param label the label for the series
	 * @param data the list of objects
	 * @param getter a function converting each object into a numeric value
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public <D> XYPlotBuilder histogram(String label, List<D> data, Function<D, ? extends Number> getter,
			PlotAttribute... attributes) {
		return histogram(label, toArray(data, getter), attributes);
	}
	
	/**
	 * Creates a new histogram showing the number of times each value occurs in the input.
	 * 
	 * @param label the label for the series
	 * @param values the values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder histogram(String label, double[] values, PlotAttribute... attributes) {
		if (values.length == 0) {
			return getInstance();
		}
		
		values = values.clone();
		Arrays.sort(values);
		
		int steps = getValueOrDefault(StepsAttribute.class, StepsAttribute.DEFAULT_VALUE, attributes);
		double min = values[0];
		double max = values[values.length - 1];
		double stepSize = (max - min) / steps;
		
		int index = 0;
		XYSeries series = new XYSeries(label, false, false);
		
		for (int i = 0; i < steps; i++) {
			int count = 0;
			
			while (index < values.length && values[index] < min + (i + 1.0) * stepSize) {
				count++;
				index++;
			}
				
			series.add(min + ((i + 0.5) * stepSize), count);
		}
		
		return histogram(series, attributes);
	}
	
	/**
	 * Creates a new histogram.
	 * 
	 * @param series the XY series containing the data
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	private XYPlotBuilder histogram(XYSeries series, PlotAttribute... attributes) {
		int index = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		XYStepAreaRenderer renderer = new XYStepAreaRenderer();
		renderer.setStepPoint(0.5); // draw step halfway between two points
		renderer.setDefaultStroke(getDefaultStroke());
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot.setDataset(index, dataset);
		plot.setRenderer(index, renderer);
		
		applyStyle(plot, index, attributes);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(String label, double[] x, double[] y, PlotAttribute... attributes) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, false, true);
		
		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}

		return area(series, attributes);
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this instance
	 */
	public XYPlotBuilder area(String label, Partition<? extends Number, ? extends Number>  partition, PlotAttribute... attributes) {
		return area(label, partition.keys(), partition.values(), attributes);
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(String label, List<? extends Number> x, List<? extends Number> y, PlotAttribute... attributes) {
		return area(label, toArray(x), toArray(y), attributes);
	}
	
	/**
	 * Creates a new area plot series.
	 * 
	 * @param <D> the type of each object
	 * @param label the label for the series
	 * @param data the list of objects
	 * @param xAxis a function converting each object into a numeric value for the X axis
	 * @param yAxis a function converting each object into a numeric value for the Y axis
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public <D> XYPlotBuilder area(String label, List<D> data, Function<D, ? extends Number> xAxis,
			Function<D, ? extends Number> yAxis, PlotAttribute... attributes) {
		return area(label, toArray(data, xAxis), toArray(data, yAxis), attributes);
	}
	
	/**
	 * Creates a new area plot series.
	 * 
	 * @param series the series containing the XY data
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	private XYPlotBuilder area(XYSeries series, PlotAttribute... attributes) {
		int index = plot.getDatasetCount();
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		XYAreaRenderer renderer = new XYAreaRenderer();
		renderer.setDefaultStroke(getDefaultStroke());
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot.setDataset(index, dataset);
		plot.setRenderer(index, renderer);
		
		applyStyle(plot, index, attributes);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}

	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, double[] x, double[] y, PlotAttribute... attributes) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, true, false);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}

		return stacked(series, attributes);
	}

	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, Partition<? extends Number, ? extends Number>  partition, PlotAttribute... attributes) {
		return stacked(label, partition.keys(), partition.values(), attributes);
	}

	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, List<? extends Number> x, List<? extends Number> y, PlotAttribute... attributes) {
		return stacked(label, toArray(x), toArray(y), attributes);
	}
	
	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param <D> the type of each object
	 * @param label the label for the series
	 * @param data the list of objects
	 * @param xAxis a function converting each object into a numeric value for the X axis
	 * @param yAxis a function converting each object into a numeric value for the Y axis
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public <D> XYPlotBuilder stacked(String label, List<D> data, Function<D, ? extends Number> xAxis,
			Function<D, ? extends Number> yAxis, PlotAttribute... attributes) {
		return stacked(label, toArray(data, xAxis), toArray(data, yAxis), attributes);
	}
	
	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param series the series containing the XY data
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	private XYPlotBuilder stacked(XYSeries series, PlotAttribute... attributes) {
		int index = plot.getDatasetCount() - 1;
		DefaultTableXYDataset dataset = null;
		StackedXYAreaRenderer renderer = null;
		
		if (index >= 0 &&
				plot.getDataset(index) instanceof DefaultTableXYDataset xyDataset &&
				plot.getRenderer(index) instanceof StackedXYAreaRenderer stackedXYAreaRenderer) {
			dataset = xyDataset;
			renderer = stackedXYAreaRenderer;
		} else {
			index = plot.getDatasetCount();
			dataset = new DefaultTableXYDataset();
			
			renderer = new StackedXYAreaRenderer();
			renderer.setDefaultStroke(getDefaultStroke());
			renderer.setAutoPopulateSeriesFillPaint(false);
			renderer.setAutoPopulateSeriesOutlinePaint(false);
			renderer.setAutoPopulateSeriesOutlineStroke(false);
			renderer.setAutoPopulateSeriesPaint(false);
			renderer.setAutoPopulateSeriesShape(false);
			renderer.setAutoPopulateSeriesStroke(false);
			
			plot.setDataset(index, dataset);
			plot.setRenderer(index, renderer);
		}
		
		int seriesIndex = dataset.getSeriesCount();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		renderer.setSeriesPaint(seriesIndex, paint);
		renderer.setSeriesFillPaint(seriesIndex, paint);
		
		applyStyle(plot, index, attributes);
		
		if (!renderer.getSeriesPaint(seriesIndex).equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getSeriesPaint(seriesIndex));
		}

		return getInstance();
	}
	
	/**
	 * Creates a new deviation series, with a solid line indicating the median (50-th percentile) and a shaded
	 * area denoting the lower and upper percentiles.
	 * 
	 * @param label the label for the series
	 * @param data a collection of the {@link ResultSeries} to display
	 * @param property the property to display
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, List<ResultSeries> data, String property, PlotAttribute... attributes) {
		List<Integer> x = new ArrayList<>();
		List<Double> y = new ArrayList<>();
		
		for (ResultSeries series : data) {
			for (IndexedResult result : series) {
				if (result.getProperties().contains(property)) {
					x.add(result.getIndex());
					y.add(result.getProperties().getDouble(property));
				}
			}
		}
		
		return deviation(label, x, y, attributes);
	}
	
	/**
	 * Creates a new deviation series.
	 * 
	 * @param <D> the type of each object
	 * @param label the label for the series
	 * @param data the list of objects
	 * @param xAxis a function converting each object into a numeric value for the X axis
	 * @param yAxis a function converting each object into a numeric value for the Y axis
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public <D> XYPlotBuilder deviation(String label, List<D> data, Function<D, ? extends Number> xAxis,
			Function<D, ? extends Number> yAxis, PlotAttribute... attributes) {
		return deviation(label, toArray(data, xAxis), toArray(data, yAxis), attributes);
	}
	
	/**
	 * Creates a new deviation series.  The data is aggregated using a given step size, with the median, low-percentile,
	 * and high-percentile values computed from the points lying within each step.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, List<? extends Number> x, List<? extends Number> y, PlotAttribute... attributes) {
		return deviation(label, toArray(x), toArray(y), attributes);
	}
	
	/**
	 * Creates a new deviation series.  The data is aggregated using a given step size, with the median, low-percentile,
	 * and high-percentile values computed from the points lying within each step.
	 * 
	 * @param label the label for the series
	 * @param x the X values
	 * @param y the Y values
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, double[] x, double[] y, PlotAttribute... attributes) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		double lowPercent = getValueOrDefault(LowPercentileAttribute.class, LowPercentileAttribute.DEFAULT_VALUE, attributes);
		double highPercent = getValueOrDefault(HighPercentileAttribute.class, HighPercentileAttribute.DEFAULT_VALUE, attributes);
		int steps = getValueOrDefault(StepsAttribute.class, StepsAttribute.DEFAULT_VALUE, attributes);
		
		YIntervalSeries ySeries = new YIntervalSeries(label);
		double minX = StatUtils.min(x);
		double maxX = StatUtils.max(x) + Settings.EPS;
		double deltaX = (maxX - minX) / steps;
		
		// TODO: The current implementation makes multiple passes of the data since the inputs are not sorted.  While
		// performance isn't necessarily a concern here, we could make this more efficient by sorting the values
		// and performing a single scan.
		double currentX = minX;

		while (currentX <= maxX) {
			DescriptiveStatistics statistics = new DescriptiveStatistics();
			
			for (int i = 0; i < x.length; i++) {
				if (x[i] >= currentX && x[i] < currentX + deltaX) {
					statistics.addValue(y[i]);
				}
			}
			
			if (statistics.getN() > 0) {
				ySeries.add(currentX,
						statistics.getPercentile(50.0),
						lowPercent < Settings.EPS ? statistics.getMin() : statistics.getPercentile(lowPercent),
						highPercent > 100.0 - Settings.EPS ? statistics.getMax() : statistics.getPercentile(highPercent));
			}

			currentX += deltaX;
		}
		
		if (ySeries.getItemCount() == 0) {
			System.err.println("WARNING: Unable to generate deviation plot, no data was provided");
			return getInstance();
		}
		
		// the series will not render properly if it contains a single point, therefore add a non-zero width
		if (ySeries.getItemCount() == 1) {
			ySeries.add(ySeries.getX(0).intValue() - deltaX,
					ySeries.getYValue(0),
					ySeries.getYLowValue(0),
					ySeries.getYHighValue(0));
		}
		
		return deviation(ySeries, attributes);
	}
	
	/**
	 * Creates a new deviation series.
	 * 
	 * @param series the series containing the deviation data
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	private XYPlotBuilder deviation(YIntervalSeries series, PlotAttribute... attributes) {
		int index = plot.getDatasetCount();
		
		YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
		dataset.addSeries(series);
		
		Paint paint = paintHelper.get(series.getKey());

		DeviationRenderer renderer = new DeviationRenderer(true, false);
		renderer.setDefaultStroke(getDefaultStroke());
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot.setDataset(index, dataset);
		plot.setRenderer(index, renderer);
		
		applyStyle(plot, index, attributes);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}
		
		return getInstance();
	}
	
	/**
	 * Adds a label annotation to the plot.
	 * 
	 * @param text the text to display
	 * @param x the position of the label along the X axis
	 * @param y the position of hte label along the Y axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder text(String text, double x, double y) {
		plot.addAnnotation(new XYTextAnnotation(text, x, y));
		return getInstance();
	}
	
	/**
	 * Adds a pointer annotation, which includes a label and an arrow, to the plot.
	 * 
	 * @param text the text to display
	 * @param x the target position of the arrow along the X axis
	 * @param y the target position of the arrow along the Y axis
	 * @param angle the angle, in radians, of the arrow and label in relation to the target position
	 * @return a reference to this builder
	 */
	public XYPlotBuilder pointer(String text, double x, double y, double angle) {
		plot.addAnnotation(new XYPointerAnnotation(text, x, y, angle));
		return getInstance();
	}
	
	/**
	 * Returns the default stroke used when rendering lines.
	 * 
	 * @return the default stroke
	 */
	protected Stroke getDefaultStroke() {
		return new BasicStroke((float)(Style.DEFAULT_SIZE / 2.0), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}
	
	/**
	 * Returns the default shape used when rendering points.
	 * 
	 * @return the default shape
	 */
	protected RectangularShape getDefaultShape() {
		return new Ellipse2D.Double(-Style.DEFAULT_SIZE / 2.0, -Style.DEFAULT_SIZE / 2.0, Style.DEFAULT_SIZE,
				Style.DEFAULT_SIZE);
	}
	
}
