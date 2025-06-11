package org.moeaframework.analysis.plot;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.Arrays;
import java.util.List;

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
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.moeaframework.analysis.series.IndexType;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.validate.Validate;

/**
 * Builds 2D plots with numeric X and Y axes, including line, scatter, area, stacked, and deviation plots.
 */
public class XYPlotBuilder extends PlotBuilder<XYPlotBuilder> {
	
	private final NumberAxis xAxis;
	
	private final NumberAxis yAxis;
	
	private final XYPlot plot;
	
	public XYPlotBuilder() {
		super();
		
		xAxis = new NumberAxis("");
		xAxis.setAutoRangeIncludesZero(false);
		
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
	 * @param min the minimum bound for the X axis
	 * @param max the maximum bound for the X axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder xLim(double min, double max) {
		xAxis.setRange(min, max);
		return getInstance();
	}
	
	/**
	 * Sets the X axis limits.
	 * 
	 * @param range the minimum and maximum bounds for the X axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder xLim(Range range) {
		xAxis.setRange(range);
		return getInstance();
	}

	/**
	 * Sets the Y axis limits.
	 * 
	 * @param min the minimum bound for the Y axis
	 * @param max the maximum bound for the Y axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder yLim(double min, double max) {
		yAxis.setRange(min, max);
		return getInstance();
	}
	
	/**
	 * Sets the Y axis limits.
	 * 
	 * @param range the minimum and maximum bounds for the Y axis
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
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, double[] x, double[] y, StyleAttribute... style) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}
		
		return scatter(series, style);
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Partition<? extends Number, ? extends Number> partition, StyleAttribute... style) {
		return scatter(label, partition.keys(), partition.values(), style);
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, List<? extends Number> x, List<? extends Number> y, StyleAttribute... style) {
		return scatter(label, toArray(x), toArray(y), style);
	}
	
	/**
	 * Displays the feasible solutions in the given population in a 2D scatter plot.  Only the first two objectives
	 * will be displayed.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Population population, StyleAttribute... style) {
		Solution solution = population.get(0);

		if (solution.getNumberOfObjectives() == 1) {
			return scatter(label, population, 0, 0, style);
		} else {
			return scatter(label, population, 0, 1, style);
		}
	}

	/**
	 * Displays the feasible solutions in the given population in a 2D scatter plot.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param x the objective to plot on the X axis
	 * @param y the objective to plot on the Y axis
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Population population, int x, int y, StyleAttribute... style) {
		XYSeries series = new XYSeries(label, false, true);

		for (Solution solution : population) {
			if (solution.isFeasible()) {
				series.add(solution.getObjectiveValue(x), solution.getObjectiveValue(y));
			}
		}

		return scatter(series, style);
	}
	
	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param series the XY series of points
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(XYSeries series, StyleAttribute... style) {
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
		
		applyStyle(plot, index, style);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, double[] x, double[] y, StyleAttribute... style) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}
		
		return line(series, style);
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, Partition<? extends Number, ? extends Number>  partition, StyleAttribute... style) {
		return line(label, partition.keys(), partition.values(), style);
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, List<? extends Number> x, List<? extends Number> y, StyleAttribute... style) {
		return line(label, toArray(x), toArray(y), style);
	}

	/**
	 * Displays the runtime data for the given property as a line plot.  If the series does not define the property,
	 * this method no-ops.
	 * 
	 * @param label the label for the series
	 * @param series the result series
	 * @param property the name of the property to plot
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, ResultSeries series, String property, StyleAttribute... style) {
		if (!series.getDefinedProperties().contains(property)) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label + "', no data provided");
			return getInstance();
		}
		
		XYSeries xySeries = new XYSeries(label, false, false);

		try {
			for (IndexedResult result : series) {
				xySeries.add(result.getIndex(), result.getProperties().getDouble(property));
			}
		} catch (NumberFormatException e) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label + "', not a numeric type");
			return getInstance();
		}
		
		if (xAxis.getLabel() == null || xAxis.getLabel().isBlank()) {
			xAxis.setLabel("NFE");
		}
		
		if (yAxis.getLabel() == null || yAxis.getLabel().isBlank()) {
			yAxis.setLabel("Value");
		}

		return line(xySeries, style);
	}
	
	/**
	 * Creates a new line plot series.
	 * 
	 * @param series the XY series containing the data
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(XYSeries series, StyleAttribute... style) {
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
		
		applyStyle(plot, index, style);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}
	
	/**
	 * 
	 * @param series
	 * @param style the style attributes
	 * @return
	 */
	public XYPlotBuilder lines(ResultSeries series, StyleAttribute... style) {
		for (String key : series.getDefinedProperties()) {
			if (series.getIndexType().equals(IndexType.NFE) && key.equals(ResultEntry.NFE)) {
				continue;
			}
			
			line(key, series, key, style);
		}
		
		return getInstance();
	}
	
	public XYPlotBuilder histogram(String label, double[] x, StyleAttribute... style) {
		return histogram(label, x, 100, style);
	}
	
	public XYPlotBuilder histogram(String label, List<? extends Number> x, StyleAttribute... style) {
		return histogram(label, toArray(x), style);
	}
	
	public XYPlotBuilder histogram(String label, double[] x, int steps, StyleAttribute... style) {
		x = x.clone();
		Arrays.sort(x);
		
		double minX = x[0];
		double maxX = x[x.length - 1];
		double stepSize = (maxX - minX) / steps;
		int index = 0;
		XYSeries series = new XYSeries(label, false, false);
		
		for (int i = 0; i < steps; i++) {
			int count = 0;
			
			while (index < x.length && x[index] < minX + (i + 1.0) * stepSize) {
				count++;
				index++;
			}
						
			series.add(minX + ((i + 0.5) * stepSize), count);
		}
		
		return line(series, style);
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(String label, double[] x, double[] y, StyleAttribute... style) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, false, true);
		
		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}

		return area(series, style);
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param style the style attributes
	 * @return a reference to this instance
	 */
	public XYPlotBuilder area(String label, Partition<? extends Number, ? extends Number>  partition, StyleAttribute... style) {
		return area(label, partition.keys(), partition.values(), style);
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(String label, List<? extends Number> x, List<? extends Number> y, StyleAttribute... style) {
		return area(label, toArray(x), toArray(y), style);
	}
	
	/**
	 * Creates a new area plot series.
	 * 
	 * @param series the series containing the XY data
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(XYSeries series, StyleAttribute... style) {
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
		
		applyStyle(plot, index, style);
		
		if (!renderer.getDefaultPaint().equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getDefaultPaint());
		}

		return getInstance();
	}

	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, double[] x, double[] y, StyleAttribute... style) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		XYSeries series = new XYSeries(label, true, false);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}

		return stacked(series, style);
	}

	/**
	 * Creates a new stacked area plot series.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, Partition<? extends Number, ? extends Number>  partition, StyleAttribute... style) {
		return stacked(label, partition.keys(), partition.values(), style);
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, List<? extends Number> x, List<? extends Number> y, StyleAttribute... style) {
		return stacked(label, toArray(x), toArray(y), style);
	}
	
	/**
	 * 
	 * @param series
	 * @param style the style attributes
	 * @return
	 */
	public XYPlotBuilder stacked(XYSeries series, StyleAttribute... style) {
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
		
		applyStyle(plot, index, style);
		
		if (!renderer.getSeriesPaint(seriesIndex).equals(paint)) {
			paintHelper.set(series.getKey(), renderer.getSeriesPaint(seriesIndex));
		}

		return getInstance();
	}
	
	/**
	 * Adds a deviation series to the plot displaying the 25-, 50-, and 75-th percentiles.
	 * 
	 * @param label the label for the series
	 * @param data a collection of the {@link ResultSeries} to display
	 * @param property the property to display
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, List<ResultSeries> data, String property, StyleAttribute... style) {
		return deviation(label, data, property, 25.0, 75.0, style);
	}
	
	/**
	 * Adds a deviation series to the plot, with a solid line indicating the median (50-th percentile) and a shaded
	 * area denoting the bounds between the low and high percentiles.
	 * 
	 * @param label the label for the series
	 * @param data a collection of the {@link ResultSeries} to display
	 * @param property the property to display
	 * @param lowPercent the low percentile, a value between {@code 0.0} and {@code 50.0}
	 * @param highPercent the high percentile, a value between {@code 50.0} and {@code 100.0}
	 * @param style the style attributes
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, List<ResultSeries> data, String property, double lowPercent,
			double highPercent, StyleAttribute... style) {
		Validate.that("lowPercent", lowPercent).isBetween(0.0, 50.0);
		Validate.that("highPercent", highPercent).isBetween(50.0, 100.0);
		
		YIntervalSeries ySeries = new YIntervalSeries(label);
		int currentNFE = 0;
		int minNFE = Integer.MAX_VALUE;
		int maxNFE = 0;
		int steps = 0;
		
		for (ResultSeries series : data) {
			minNFE = Math.min(minNFE, series.getStartingIndex());
			maxNFE = Math.max(maxNFE, series.getEndingIndex());
			steps = Math.max(steps, series.size());
		}
		
		int deltaNFE = (maxNFE - minNFE) / steps;

		while (currentNFE <= maxNFE) {
			DescriptiveStatistics statistics = new DescriptiveStatistics();

			for (ResultSeries series : data) {
				for (IndexedResult result : series) {
					if (result.getProperties().contains(property) && result.getIndex() >= currentNFE &&
							result.getIndex() < currentNFE + deltaNFE) {
						statistics.addValue(result.getProperties().getDouble(property));
					}
				}
			}
			
			if (statistics.getN() > 0) {
				ySeries.add(currentNFE,
						statistics.getPercentile(50.0),
						statistics.getPercentile(lowPercent),
						statistics.getPercentile(highPercent));
			}

			currentNFE += deltaNFE;
		}
		
		if (ySeries.getItemCount() == 0) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label + "', no data provided");
			return getInstance();
		}
		
		// the series will not render properly if it contains a single point, therefore add a non-zero width
		if (ySeries.getItemCount() == 1) {
			ySeries.add(ySeries.getX(0).intValue() - deltaNFE,
					ySeries.getYValue(0),
					ySeries.getYLowValue(0),
					ySeries.getYHighValue(0));
		}
		
		return deviation(ySeries, style);
	}
	
	public XYPlotBuilder deviation(String label, double[] x, double[] yLow, double[] y, double[] yHigh, StyleAttribute... style) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		Validate.that("x.length", x.length).isEqualTo("yLow.length", yLow.length);
		Validate.that("x.length", x.length).isEqualTo("yHigh.length", yHigh.length);
				
		YIntervalSeries series = new YIntervalSeries(label, false, true);

		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i], yLow[i], yHigh[i]);
		}
		
		return deviation(series, style);
	}
	
	public XYPlotBuilder deviation(String label, List<? extends Number> x, List<? extends Number> yLow,
			List<? extends Number> y, List<? extends Number> yHigh, StyleAttribute... style) {
		return deviation(label, toArray(x), toArray(yLow), toArray(y), toArray(yHigh), style);
	}
	
	public XYPlotBuilder deviation(YIntervalSeries series, StyleAttribute... style) {
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
		
		applyStyle(plot, index, style);
		
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
	
	protected Stroke getDefaultStroke() {
		return new BasicStroke(3f, 1, 1);
	}
	
	protected RectangularShape getDefaultShape() {
		return new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0);
	}
	
	public static void main(String[] args) {		
		new XYPlotBuilder()
				.scatter("Test", new double[] { 0.0, 1.0, 2.0 }, new double[] { 0.0, 1.0, 2.0 }, SeriesPaint.red(), SeriesSize.medium())
				.xLabel("X")
				.yLabel("Count")
				.show();
	}
	
}
