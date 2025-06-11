package org.moeaframework.analysis.plot;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
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
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.validate.Validate;

public class XYPlotBuilder extends PlotBuilder {
	
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
	public JFreeChart build() {
		return build(plot);
	}
	
	/**
	 * Sets the x-axis label.
	 * 
	 * @param label the label for the x-axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder setXLabel(String label) {
		xAxis.setLabel(label);
		return this;
	}

	/**
	 * Sets the y-axis label.
	 * 
	 * @param label the label for the y-axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder setYLabel(String label) {
		yAxis.setLabel(label);
		return this;
	}
	
	/**
	 * Sets the x-axis limits.
	 * 
	 * @param min the minimum bound for the x-axis
	 * @param max the maximum bound for the x-axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder setXLim(double min, double max) {
		xAxis.setRange(min, max);
		return this;
	}
	
	public XYPlotBuilder setXLim(Range range) {
		xAxis.setRange(range);
		return this;
	}

	/**
	 * Sets the y-axis limits.
	 * 
	 * @param min the minimum bound for the y-axis
	 * @param max the maximum bound for the y-axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder setYLim(double min, double max) {
		yAxis.setRange(min, max);
		return this;
	}
	
	public XYPlotBuilder setYLim(Range range) {
		yAxis.setRange(range);
		return this;
	}
	
	/**
	 * Sets the grid line paint for both axes.
	 * 
	 * @param paint the paint
	 * @return a reference to this builder
	 */
	public XYPlotBuilder setGridPaint(Paint paint) {
		plot.setRangeGridlinePaint(paint);
		plot.setDomainGridlinePaint(paint);
		return this;
	}
	
	/**
	 * Modifies the line thickness or point size in the last dataset.  The size is applied to all series in the dataset.
	 * 
	 * @param size the size
	 * @return a reference to this builder
	 */
	public XYPlotBuilder withSize(float size) {
		if (plot.getRendererCount() == 0) {
			System.err.println("WARNING: Unable to set size, no renderer found. Add a series first.");
			return this;
		}
		
		XYItemRenderer renderer = plot.getRenderer(plot.getDatasetCount() > 0 ? plot.getDatasetCount() - 1 : 0);

		if (renderer instanceof XYDotRenderer xyDotRenderer) {
			xyDotRenderer.setDotWidth((int)(size*2));
			xyDotRenderer.setDotHeight((int)(size*2));
		}
		
		if (renderer.getDefaultShape() instanceof RectangularShape shape) {
			shape.setFrame(-size / 2.0f, -size / 2.0f, size, size);
			renderer.setDefaultShape(shape);
		}
		
		if (renderer.getDefaultStroke() instanceof BasicStroke stroke) {
			renderer.setDefaultStroke(new BasicStroke(
					size,
					stroke.getEndCap(),
					stroke.getLineJoin(),
					stroke.getMiterLimit(),
					stroke.getDashArray(),
					stroke.getDashPhase()));
		} else {
			renderer.setDefaultStroke(new BasicStroke(size, 1, 1));
		}

		return this;
	}
	
	/**
	 * Modifies the paint (e.g,. color) of the last dataset.
	 * 
	 * @param paint the paint
	 * @return a reference to this builder
	 */
	public XYPlotBuilder withPaint(Paint paint) {
		if (plot.getRendererCount() == 0) {
			System.err.println("WARNING: Unable to set paint, no renderer found. Add a series first.");
			return this;
		}
		
		XYItemRenderer renderer = plot.getRenderer(plot.getDatasetCount() > 0 ? plot.getDatasetCount() - 1 : 0);
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);

		if (plot.getDatasetCount() > 0) {
			XYDataset dataset = plot.getDataset(plot.getDatasetCount() - 1);
			
			for (int i = 0; i < dataset.getSeriesCount(); i++) {
				paintHelper.set(dataset.getSeriesKey(i), paint);
			}
		}
		
		return this;
	}
	
	/**
	 * Modifies the shape of points of the last dataset.
	 * 
	 * @param paint the paint
	 * @return a reference to this builder
	 */
	public XYPlotBuilder withShape(Shape shape) {
		if (plot.getRendererCount() == 0) {
			System.err.println("WARNING: Unable to set shape, no renderer found. Add a series first.");
			return this;
		}
		
		XYItemRenderer renderer = plot.getRenderer(plot.getDatasetCount() > 0 ? plot.getDatasetCount() - 1 : 0);
		renderer.setDefaultShape(shape);
		
		return this;
	}
	
	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, double[] x, double[] y) {
		return scatter(label, toList(x), toList(y));
	}

	/**
	 * Creates a new scatter plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Partition<? extends Number, ? extends Number> partition) {
		return scatter(label, partition.keys(), partition.values());
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, List<? extends Number> x, List<? extends Number> y) {
		Validate.that("x.size()", x.size()).isEqualTo("y.size()", y.size());
				
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}
		
		return scatter(series);
	}
	
	/**
	 * Displays the feasible solutions in the given population in a 2D scatter plot.  Only the first two objectives
	 * will be displayed.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Population population) {
		Solution solution = population.get(0);

		if (solution.getNumberOfObjectives() == 1) {
			return scatter(label, population, 0, 0);
		} else {
			return scatter(label, population, 0, 1);
		}
	}

	/**
	 * Displays the feasible solutions in the given population in a 2D scatter plot.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param x the objective to plot on the x-axis
	 * @param y the objective to plot on the y-axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder scatter(String label, Population population, int x, int y) {
		XYSeries series = new XYSeries(label, false, true);

		for (Solution solution : population) {
			if (solution.isFeasible()) {
				series.add(solution.getObjectiveValue(x), solution.getObjectiveValue(y));
			}
		}

		return scatter(series);
	}
	
	public XYPlotBuilder scatter(XYSeries series) {
		int index = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		Shape shape = new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
		renderer.setDefaultShape(shape);
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

		return this;
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, double[] x, double[] y) {
		return line(label, toList(x), toList(y));
	}

	/**
	 * Creates a new line plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, Partition<? extends Number, ? extends Number>  partition) {
		return line(label, partition.keys(), partition.values());
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, List<? extends Number> x, List<? extends Number> y) {
		Validate.that("x.size()", x.size()).isEqualTo("y.size()", y.size());
				
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}
		
		return line(series);
	}

	/**
	 * Displays the runtime data for the given property as a line plot.  If the series does not define the property,
	 * this method no-ops.
	 * 
	 * @param label the label for the series
	 * @param series the result series
	 * @param property the name of the property to plot
	 * @return a reference to this builder
	 */
	public XYPlotBuilder line(String label, ResultSeries series, String property) {
		if (!series.getDefinedProperties().contains(property)) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label + "', no data provided");
			return this;
		}
		
		XYSeries xySeries = new XYSeries(label, false, false);

		try {
			for (IndexedResult result : series) {
				xySeries.add(result.getIndex(), result.getProperties().getDouble(property));
			}
		} catch (NumberFormatException e) {
			System.err.println("WARNING: Unable to plot '" + property + "' with label '" + label + "', not a numeric type");
			return this;
		}
		
		if (xAxis.getLabel() == null || xAxis.getLabel().isBlank()) {
			xAxis.setLabel("NFE");
		}
		
		if (yAxis.getLabel() == null || yAxis.getLabel().isBlank()) {
			yAxis.setLabel("Value");
		}

		return line(xySeries);
	}
	
	public XYPlotBuilder line(XYSeries series) {
		int index = plot.getDatasetCount();

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
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

		return this;
	}
	
	public XYPlotBuilder lines(ResultSeries series) {
		for (String key : series.getDefinedProperties()) {
			line(key, series, key);
		}
		
		return this;
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(String label, double[] x, double[] y) {
		return area(label, toList(x), toList(y));
	}

	/**
	 * Creates a new area plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public XYPlotBuilder area(String label, Partition<? extends Number, ? extends Number>  partition) {
		return area(label, partition.keys(), partition.values());
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder area(String label, List<? extends Number> x, List<? extends Number> y) {
		Validate.that("x.size()", x.size()).isEqualTo("y.size()", y.size());
		
		// generate the dataset
		XYSeries series = new XYSeries(label, false, true);
		
		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}

		return area(series);
	}
	
	public XYPlotBuilder area(XYSeries series) {
		int index = plot.getDatasetCount();
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		XYAreaRenderer renderer = new XYAreaRenderer();
		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
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

		return this;
	}

	/**
	 * Creates a new stacked area plot series.  The data will be stacked with any preceding calls to {@code stacked}.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, double[] x, double[] y) {
		return stacked(label, toList(x), toList(y));
	}

	/**
	 * Creates a new stacked area plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, Partition<? extends Number, ? extends Number>  partition) {
		return stacked(label, partition.keys(), partition.values());
	}

	/**
	 * Creates a new area plot series.  The data will be stacked with any preceding calls to {@code stacked}.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this builder
	 */
	public XYPlotBuilder stacked(String label, List<? extends Number> x, List<? extends Number> y) {
		Validate.that("x.size()", x.size()).isEqualTo("y.size()", y.size());
		
		XYSeries series = new XYSeries(label, true, false);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}

		return stacked(series);
	}
	
	public XYPlotBuilder stacked(XYSeries series) {
		int index = plot.getDatasetCount() - 1;
		DefaultTableXYDataset dataset = null;
		
		if (index >= 0 && plot.getDataset(index) instanceof DefaultTableXYDataset xyDataset) {
			dataset = xyDataset;
		} else {
			index = plot.getDatasetCount();
			dataset = new DefaultTableXYDataset();
		}
		
		dataset.addSeries(series);

		Paint paint = paintHelper.get(series.getKey());
		
		StackedXYAreaRenderer renderer = new StackedXYAreaRenderer();
		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
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

		return this;
	}
	
	/**
	 * Adds a deviation series to the plot displaying the 25-, 50-, and 75-th percentiles.
	 * 
	 * @param label the label for the series
	 * @param data a collection of the {@link ResultSeries} to display
	 * @param property the property to display
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, List<ResultSeries> data, String property) {
		return deviation(label, data, property, 25.0, 75.0);
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
	 * @return a reference to this builder
	 */
	public XYPlotBuilder deviation(String label, List<ResultSeries> data, String property, double lowPercent,
			double highPercent) {
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
			return this;
		}
		
		// the series will not render properly if it contains a single point, therefore add a non-zero width
		if (ySeries.getItemCount() == 1) {
			ySeries.add(ySeries.getX(0).intValue() - deltaNFE,
					ySeries.getYValue(0),
					ySeries.getYLowValue(0),
					ySeries.getYHighValue(0));
		}
		
		return deviation(ySeries);
	}
	
	public XYPlotBuilder deviation(String label, double[] x, double[] yLow, double[] y, double[] yHigh) {
		return deviation(label, toList(x), toList(yLow), toList(y), toList(yHigh));
	}
	
	public XYPlotBuilder deviation(String label, List<? extends Number> x, List<? extends Number> yLow,
			List<? extends Number> y, List<? extends Number> yHigh) {
		Validate.that("x.size()", x.size()).isEqualTo("y.size()", y.size());
		Validate.that("x.size()", x.size()).isEqualTo("yLow.size()", yLow.size());
		Validate.that("x.size()", x.size()).isEqualTo("yHigh.size()", yHigh.size());
				
		YIntervalSeries series = new YIntervalSeries(label, false, true);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i).doubleValue(), y.get(i).doubleValue(), yLow.get(i).doubleValue(), yHigh.get(i).doubleValue());
		}
		
		return deviation(series);
	}
	
	public XYPlotBuilder deviation(YIntervalSeries series) {
		int index = plot.getDatasetCount();
		
		YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
		dataset.addSeries(series);
		
		Paint paint = paintHelper.get(series.getKey());

		DeviationRenderer renderer = new DeviationRenderer(true, false);
		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
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
		
		return this;
	}
	
	/**
	 * Adds a label annotation to the plot.
	 * 
	 * @param text the text to display
	 * @param x the position of the label along the x-axis
	 * @param y the position of hte label along the y-axis
	 * @return a reference to this builder
	 */
	public XYPlotBuilder text(String text, double x, double y) {
		plot.addAnnotation(new XYTextAnnotation(text, x, y));
		return this;
	}
	
	/**
	 * Adds a pointer annotation, which includes a label and an arrow, to the plot.
	 * 
	 * @param text the text to display
	 * @param x the target position of the arrow along the x-axis
	 * @param y the target position of the arrow along the y-axis
	 * @param angle the angle, in radians, of the arrow and label in relation to the target position
	 * @return a reference to this builder
	 */
	public XYPlotBuilder pointer(String text, double x, double y, double angle) {
		plot.addAnnotation(new XYPointerAnnotation(text, x, y, angle));
		return this;
	}
	
}
