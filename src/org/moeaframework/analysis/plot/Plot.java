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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Window;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.XYZToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sensitivity.FirstOrderSensitivity;
import org.moeaframework.analysis.sensitivity.SecondOrderSensitivity;
import org.moeaframework.analysis.sensitivity.Sensitivity;
import org.moeaframework.analysis.sensitivity.SensitivityResult;
import org.moeaframework.analysis.sensitivity.TotalOrderSensitivity;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.mvc.UI;

/**
 * Provides simple 2D plotting capabilities.  This is intended to allow the rapid creation of 2D plots, supporting:
 * <ol>
 *   <li>scatter plots of bi-objective populations,</li>
 *   <li>line plots of runtime dynamics (via a {@link ResultSeries},</li>
 *   <li>box-and-whisker plots of performance statistics (via an {@link IndicatorStatistics}), and</li>
 *   <li>other plots of basic data types (e.g., line, scatter, area, stacked, heat map).</li>
 * </ol>
 * It is possible to combine datasets by calling more than one {@code add} method, but you can not mix different plot
 * types (e.g., XY plots versus categorical plots).  Thus, box-and-whisker plots can not be overlaid on a line plot.
 * <p>
 * In general, one should first generate the plot artifacts by calling {@link #line}, {@link #scatter}, {@link #area},
 * {@link #stacked}, {@link #heatMap} or any of the {@link #add} methods.  Artifacts can be customized by immediately
 * calling one of the {@code with*} methods after generating the artifact (the customization is only applied to the
 * last artifact).  Lastly, call the {@code set*} methods to customize the overall appearance of the chart.  Call
 * {@link #show} to display the chart in a window or {@link #save} to create an image file.  For example:
 * <pre>
 *       new Plot()
 *      		.scatter("Point", new double[] { 0.0, 1.0, 2.0 }, new double[] { 3.0, 4.0, 5.0 })
 *      			.withPaint(Color.BLACK)
 *      		.line("Line", new double[] { 0.0, 2.0 }, new double[] { 3.0, 5.0 })
 *      			.withSize(5)
 *      		.setXLabel("X")
 *      		.setYLabel("Y")
 *      		.setTitle("Example")
 *      		.show();
 * </pre>
 * <p>
 * This class is not intended to be a fully featured plotting library.  To generate more sophisticated plots or
 * customize their appearance, one must instead use JFreeChart, JZY3D, or another plotting library.
 * 
 * @deprecated Use {@link PlotBuilder} instead
 */
@Deprecated
public class Plot {

	private static final String WINDOW_TITLE = "MOEA Framework Plot";

	/**
	 * The internal JFreeChart instance.
	 */
	private JFreeChart chart;

	/**
	 * Maps labels to their assigned color.
	 */
	private PaintHelper paintHelper;

	/**
	 * The index of the current dataset.
	 */
	private int currentDataset;

	/**
	 * Creates a new, empty plot.
	 */
	public Plot() {
		super();
		paintHelper = new PaintHelper();
		currentDataset = -1;
	}

	/**
	 * If the chart has not yet been initialized, creates a chart for XY data.  If the chart is already initialized,
	 * checks if the chart is for XY data.
	 * 
	 * @throws FrameworkException if the chart does not support XY data
	 */
	private void createXYPlot() {
		if (chart == null) {
			NumberAxis xAxis = new NumberAxis("");
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis("");
			yAxis.setAutoRangeIncludesZero(false);

			XYPlot plot = new XYPlot();
			plot.setDomainAxis(xAxis);
			plot.setRangeAxis(yAxis);

			XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();

			XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
			renderer.setDefaultToolTipGenerator(toolTipGenerator);
			plot.setRenderer(renderer);
			plot.setOrientation(PlotOrientation.VERTICAL);

			chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			ChartFactory.getChartTheme().apply(chart);
		} else if (!(chart.getPlot() instanceof XYPlot)) {
			throw new FrameworkException("Can not combine XY plot and categorial plot");
		}
	}

	/**
	 * If the chart has not yet been initialized, creates a chart for categorical data.  If the chart is already
	 * initialized, checks if the chart is for categorical data.
	 * 
	 * @throws FrameworkException if the chart does not support categorical data
	 */
	private void createCategoryPlot() {
		if (chart == null) {
			CategoryAxis xAxis = new CategoryAxis("");

			NumberAxis yAxis = new NumberAxis("Value");
			yAxis.setAutoRangeIncludesZero(false);

			final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
			renderer.setFillBox(true);
			renderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

			final CategoryPlot plot = new CategoryPlot();
			plot.setDomainAxis(xAxis);
			plot.setRangeAxis(yAxis);
			plot.setRenderer(renderer);

			chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			ChartFactory.getChartTheme().apply(chart);
		} else if (!(chart.getPlot() instanceof CategoryPlot)) {
			throw new FrameworkException("Can not combine XY plot and categorial plot");
		}
	}

	/**
	 * If the chart has not yet been initialized, creates a histogram chart.  If the chart is already initialized,
	 * checks if the chart is for XY data.
	 * 
	 * @throws FrameworkException if the chart does not support XY data
	 */
	private void createHistogramPlot() {
		if (chart == null) {
			NumberAxis xAxis = new NumberAxis("");
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis("");

			XYPlot plot = new XYPlot();
			plot.setDomainAxis(xAxis);
			plot.setRangeAxis(yAxis);

			XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();

			XYItemRenderer renderer = new XYBarRenderer();
			renderer.setDefaultToolTipGenerator(toolTipGenerator);
			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setDomainZeroBaselineVisible(true);
			plot.setRangeZeroBaselineVisible(true);
			plot.setRenderer(renderer);

			chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			ChartFactory.getChartTheme().apply(chart);
		} else if (!(chart.getPlot() instanceof XYPlot)) {
			throw new FrameworkException("Can not combine XY plot and categorial plot");
		}
	}

	/**
	 * If the chart has not yet been initialized, creates a chart for HeatMap data.  If the chart is already
	 * initialized, checks if the chart is for HeatMap data.
	 * 
	 * @throws FrameworkException if the chart does not support HeatMap data
	 */
	private void createHeatMapPlot() {
		if (chart == null) {
			NumberAxis xAxis = new NumberAxis("");
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis("");
			yAxis.setAutoRangeIncludesZero(false);
			NumberAxis zAxis = new NumberAxis("");
			zAxis.setAutoRangeIncludesZero(false);

			xAxis.setLowerMargin(0.0);
			xAxis.setUpperMargin(0.0);
			yAxis.setLowerMargin(0.0);
			yAxis.setUpperMargin(0.0);
			zAxis.setLowerMargin(0.0);
			zAxis.setUpperMargin(0.0);
			zAxis.setVisible(false);

			XYPlot plot = new XYPlot();
			plot.setDomainAxis(xAxis);
			plot.setRangeAxis(yAxis);
			plot.setRangeAxis(1, zAxis);
			plot.setOrientation(PlotOrientation.VERTICAL);

			chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			ChartFactory.getChartTheme().apply(chart);
		} else if (!(chart.getPlot() instanceof XYPlot)) {
			throw new FrameworkException("Can not combine XY plot and categorial plot");
		}
	}

	/**
	 * Sets the chart title.
	 * 
	 * @param title the title
	 * @return a reference to this instance
	 */
	public Plot setTitle(String title) {
		chart.setTitle(title);
		return this;
	}

	/**
	 * Sets the x-axis label.
	 * 
	 * @param label the label for the x-axis
	 * @return a reference to this instance
	 */
	public Plot setXLabel(String label) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getDomainAxis().setLabel(label);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().getDomainAxis().setLabel(label);
		}

		return this;
	}

	/**
	 * Sets the y-axis label.
	 * 
	 * @param label the label for the y-axis
	 * @return a reference to this instance
	 */
	public Plot setYLabel(String label) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getRangeAxis().setLabel(label);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().getRangeAxis().setLabel(label);
		}
		return this;
	}

	/**
	 * Sets the x and y labels if they haven't already been set.
	 * 
	 * @param xlabel the label for the x-axis
	 * @param ylabel the label for the y-axis
	 * @return a reference to this instance
	 */
	private Plot setLabelsIfBlank(String xlabel, String ylabel) {
		if (chart.getPlot() instanceof XYPlot) {
			XYPlot plot = chart.getXYPlot();

			if (plot.getDomainAxis().getLabel().isEmpty()) {
				plot.getDomainAxis().setLabel(xlabel);
			}

			if (plot.getRangeAxis().getLabel().isEmpty()) {
				plot.getRangeAxis().setLabel(ylabel);
			}
		} else if (chart.getPlot() instanceof CategoryPlot) {
			CategoryPlot plot = chart.getCategoryPlot();

			if (plot.getDomainAxis().getLabel().isEmpty()) {
				plot.getDomainAxis().setLabel(xlabel);
			}

			if (plot.getRangeAxis().getLabel().isEmpty()) {
				plot.getRangeAxis().setLabel(ylabel);
			}
		}

		return this;
	}

	/**
	 * Sets the background paint.
	 * 
	 * @param paint the background paint
	 * @return a reference to this instance
	 */
	public Plot setBackgroundPaint(Paint paint) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().setBackgroundPaint(paint);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().setBackgroundPaint(paint);
		}

		return this;
	}

	/**
	 * Sets the grid line paint.
	 * 
	 * @param paint the grid line paint
	 * @return a reference to this instance
	 */
	public Plot setGridPaint(Paint paint) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().setRangeGridlinePaint(paint);
			chart.getXYPlot().setDomainGridlinePaint(paint);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().setRangeGridlinePaint(paint);
			chart.getCategoryPlot().setDomainGridlinePaint(paint);
		}

		return this;
	}

	/**
	 * Sets the x-axis limits.
	 * 
	 * @param min the minimum bound for the x-axis
	 * @param max the maximum bound for the x-axis
	 * @return a reference to this instance
	 */
	public Plot setXLim(double min, double max) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getDomainAxis().setRange(min, max);
		}

		return this;
	}

	/**
	 * Sets the y-axis limits.
	 * 
	 * @param min the minimum bound for the y-axis
	 * @param max the maximum bound for the y-axis
	 * @return a reference to this instance
	 */
	public Plot setYLim(double min, double max) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getRangeAxis().setRange(min, max);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().getRangeAxis().setRange(min, max);
		}

		return this;
	}

	/**
	 * Converts a double array to a list.
	 * 
	 * @param x the double array
	 * @return the list of doubles
	 */
	private List<Double> toList(double[] x) {
		List<Double> result = new ArrayList<>();

		for (int i = 0; i < x.length; i++) {
			result.add(x[i]);
		}

		return result;
	}

	/**
	 * Converts a 2D double array to a list of lists.
	 * 
	 * @param x the 2D double array
	 * @return the list of lists of doubles
	 */
	private List<List<Double>> toList(double[][] x) {
		List<List<Double>> result = new ArrayList<>();

		for (int i = 0; i < x.length; i++) {
			List<Double> row = new ArrayList<>();

			for (int j = 0; j < x[i].length; j++) {
				row.add(x[i][j]);
			}

			result.add(row);
		}

		return result;
	}

	/**
	 * Displays the solutions in the given population in a 2D scatter plot.  Only two objectives will be displayed.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @return a reference to this instance
	 */
	public Plot add(String label, Population population) {
		Solution solution = population.get(0);

		if (solution.getNumberOfObjectives() == 1) {
			return add(label, population, 0, 0);
		} else {
			return add(label, population, 0, 1);
		}
	}

	/**
	 * Displays the solutions in the given population in a 2D scatter plot.  The two given objectives will be displayed.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param x the objective to plot on the x-axis
	 * @param y the objective to plot on the y-axis
	 * @return a reference to this instance
	 */
	public Plot add(String label, Population population, int x, int y) {
		List<Number> xs = new ArrayList<>();
		List<Number> ys = new ArrayList<>();

		for (Solution solution : population) {
			if (solution.isFeasible()) {
				xs.add(solution.getObjectiveValue(x));
				ys.add(solution.getObjectiveValue(y));
			}
		}

		scatter(label, xs, ys);
		setLabelsIfBlank("Objective " + (x+1), "Objective " + (y+1));

		return this;
	}

	/**
	 * Displays the runtime data stored in an {@link ResultSeries} as one or more line plots.
	 * 
	 * @param series the result series
	 * @return a reference to this instance
	 */
	public Plot add(ResultSeries series) {
		createXYPlot();
		currentDataset++;
		XYSeriesCollection dataset = new XYSeriesCollection();

		for (String key : series.getDefinedProperties()) {
			add(key, series, key, dataset);
		}

		return this;
	}

	/**
	 * Displays the runtime data for the given property as a line plot.
	 * 
	 * @param label the label for the series
	 * @param series the result series
	 * @param property the name of the property to plot
	 * @return a reference to this instance
	 */
	public Plot add(String label, ResultSeries series, String property) {
		return add(label, series, property, null);
	}

	/**
	 * Displays the runtime data for the given property as a line plot.  The series is added to the given dataset, or
	 * if {@code null} a new dataset is created.
	 * 
	 * @param label the label for the series
	 * @param series the result series
	 * @param property the name of the property to plot
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot add(String label, ResultSeries series, String property, XYSeriesCollection dataset) {
		List<Number> xs = new ArrayList<>();
		List<Number> ys = new ArrayList<>();

		try {
			for (IndexedResult result : series) {
				xs.add(result.getIndex());
				ys.add(result.getProperties().getDouble(property));
			}
		} catch (NumberFormatException e) {
			System.err.println("WARNING: Unable to plot " + property + ", not a numeric type");
			return this;
		}

		line(label, xs, ys, dataset);
		setLabelsIfBlank("NFE", "Value");

		return this;
	}

	/**
	 * Displays the statistical results from an {@link IndicatorStatistics} as a box-and-whisker plot.
	 * 
	 * @param statistics the indicator statistics
	 * @return a reference to this instance
	 */
	public Plot add(IndicatorStatistics statistics) {
		createCategoryPlot();

		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		for (String name : statistics.getGroupNames()) {
			List<Double> values = DoubleStream.of(statistics.getValues(name)).boxed().toList();
			dataset.add(values, name, "Indicator Value");
		}

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setDataset(dataset);

		return this;
	}

	/**
	 * Displays sensitivity analysis results in a "spider web" plot.
	 * 
	 * @param result the sensitivity analysis results
	 * @return a reference to this instance
	 */
	public Plot add(SensitivityResult result) {
		return add(result, Color.BLACK, Color.GRAY, 0.5, 1.0, 1.3);
	}

	/**
	 * Displays sensitivity analysis results in a "spider web" plot, where:
	 * <ol>
	 *   <li>First-order effects are rendered as a solid circle / ellipse,
	 *   <li>Total-order effects are rendered as a ring around the first-order effects, and
	 *   <li>Second-order effects are rendered as lines joining the circles.
	 * </ol>
	 * The scale of the circles / lines reflect the relative magnitude of the sensitivities.
	 * 
	 * @param result the sensitivity analysis results
	 * @param shapeColor the shape color
	 * @param lineColor the line color
	 * @param sensitivityScaling scaling factor applied to the sensitivity values
	 * @param sizeScaling scaling factor applied to the shape size / thickness
	 * @param labelOffset offsets labels from their shapes
	 * @return a reference to this instance
	 */
	public Plot add(SensitivityResult result, Color shapeColor, Color lineColor, double sensitivityScaling,
			double sizeScaling, double labelOffset) {
		createXYPlot();
		XYPlot plot = (XYPlot)chart.getPlot();
		ParameterSet parameterSet = result.getParameterSet();
		int n = parameterSet.size();
		double[] angles = IntStream.range(0, n).mapToDouble(x -> 2.0 * Math.PI * x / n).toArray();
		double[] xs = DoubleStream.of(angles).map(Math::cos).toArray();
		double[] ys = DoubleStream.of(angles).map(Math::sin).toArray();

		if (result instanceof SecondOrderSensitivity secondOrder) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					Sensitivity<?> value = secondOrder.getSecondOrder(parameterSet.get(i), parameterSet.get(j));
					double size = sizeScaling * Math.pow(value.getSensitivity(), sensitivityScaling) / 2.0;

					double angle = Math.atan((ys[j] - ys[i]) / (xs[j] - xs[i]));

					if (ys[j] - ys[i] < 0) {
						angle += Math.PI;
					}

					Path2D path = new Path2D.Double();
					path.moveTo(xs[i] - size * Math.sin(angle), ys[i] + size * Math.cos(angle));
					path.lineTo(xs[i] + size * Math.sin(angle), ys[i] - size * Math.cos(angle));
					path.lineTo(xs[j] + size * Math.sin(angle), ys[j] - size * Math.cos(angle));
					path.lineTo(xs[j] - size * Math.sin(angle), ys[j] + size * Math.cos(angle));
					path.closePath();

					XYShapeAnnotation annotation = new XYShapeAnnotation(path,
							plot.getRenderer().getDefaultStroke(),
							lineColor,
							lineColor);

					plot.addAnnotation(annotation);
				}
			}
		}

		if (result instanceof FirstOrderSensitivity firstOrder) {
			for (int i = 0; i < n; i++) {
				Sensitivity<?> value = firstOrder.getFirstOrder(parameterSet.get(i));
				double size = sizeScaling * Math.pow(value.getSensitivity(), sensitivityScaling) / 2.0;

				XYShapeAnnotation annotation = new XYShapeAnnotation(
						new Ellipse2D.Double(xs[i] - size / 2.0, ys[i] - size / 2.0, size, size),
						plot.getRenderer().getDefaultStroke(),
						shapeColor,
						shapeColor);

				plot.addAnnotation(annotation);
			}
		}

		if (result instanceof TotalOrderSensitivity totalOrder) {
			for (int i = 0; i < n; i++) {
				Sensitivity<?> value = totalOrder.getTotalOrder(parameterSet.get(i));
				double size = sizeScaling * Math.pow(value.getSensitivity(), sensitivityScaling) / 2.0;

				XYShapeAnnotation annotation = new XYShapeAnnotation(
						new Ellipse2D.Double(xs[i] - size / 2.0, ys[i] - size / 2.0, size, size),
						plot.getRenderer().getDefaultStroke(),
						shapeColor);

				plot.addAnnotation(annotation);
			}
		}

		for (int i = 0; i < n; i++) {
			XYTextAnnotation annotation = new XYTextAnnotation(parameterSet.get(i).getName(),
					labelOffset * xs[i], labelOffset * ys[i]);
			annotation.setTextAnchor(TextAnchor.CENTER);
			annotation.setFont(plot.getRenderer().getDefaultItemLabelFont());
			plot.addAnnotation(annotation);
		}

		plot.setBackgroundPaint(Color.WHITE);

		plot.setDomainGridlinesVisible(false);
		plot.setDomainMinorGridlinesVisible(false);

		plot.setRangeGridlinesVisible(false);
		plot.setRangeMinorGridlinesVisible(false);

		plot.getDomainAxis().setTickLabelsVisible(false);
		plot.getDomainAxis().setAutoRange(false);
		plot.getDomainAxis().setLowerBound(DoubleStream.of(xs).min().getAsDouble() - 0.5);
		plot.getDomainAxis().setUpperBound(DoubleStream.of(xs).max().getAsDouble() + 0.5);

		plot.getRangeAxis().setTickLabelsVisible(false);
		plot.getRangeAxis().setAutoRange(false);
		plot.getRangeAxis().setLowerBound(DoubleStream.of(ys).min().getAsDouble() - 0.5);
		plot.getRangeAxis().setUpperBound(DoubleStream.of(ys).max().getAsDouble() + 0.5);

		return this;
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot scatter(String label, double[] x, double[] y) {
		return scatter(label, toList(x), toList(y));
	}

	/**
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot scatter(String label, List<? extends Number> x, List<? extends Number> y) {
		return scatter(label, x, y, null);
	}

	/**
	 * Creates a new scatter plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public Plot scatter(String label, Partition<? extends Number, ? extends Number> partition) {
		return scatter(label, partition.keys(), partition.values(), null);
	}

	/**
	 * Creates a new scatter plot series.  The series is added to the given dataset, or if {@code null} a new dataset
	 * is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot scatter(String label, List<? extends Number> x, List<? extends Number> y, XYSeriesCollection dataset) {
		if (dataset == null) {
			createXYPlot();
			currentDataset++;
			dataset = new XYSeriesCollection();
		}

		// generate the dataset
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}

		dataset.addSeries(series);

		// add the dataset to the plot
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(currentDataset, dataset);

		// setup the renderer
		Paint paint = paintHelper.get(dataset.getSeriesKey(0));
		XYDotRenderer renderer = new XYDotRenderer();

		renderer.setDotHeight(6);
		renderer.setDotWidth(6);
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot line(String label, double[] x, double[] y) {
		return line(label, toList(x), toList(y));
	}

	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot line(String label, List<? extends Number> x, List<? extends Number> y) {
		return line(label, x, y, null);
	}

	/**
	 * Creates a new line plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public Plot line(String label, Partition<? extends Number, ? extends Number>  partition) {
		return line(label, partition.keys(), partition.values(), null);
	}

	/**
	 * Creates a new line plot series.  The series is added to the given dataset, or if {@code null} a new dataset is
	 * created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot line(String label, List<? extends Number> x, List<? extends Number> y, XYSeriesCollection dataset) {
		if (dataset == null) {
			createXYPlot();
			currentDataset++;
			dataset = new XYSeriesCollection();
		}

		// generate the dataset
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}

		dataset.addSeries(series);

		// add the dataset to the plot
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(currentDataset, dataset);

		// setup the renderer
		Paint paint = paintHelper.get(dataset.getSeriesKey(0));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setAutoPopulateSeriesStroke(false);

		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}

	/**
	 * Creates a new histogram plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot histogram(String label, double[] x, double[] y) {
		return histogram(label, toList(x), toList(y));
	}

	/**
	 * Creates a new histogram plot.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot histogram(String label, List<? extends Number> x, List<? extends Number> y) {
		return histogram(label, x, y, null);
	}

	/**
	 * Creates a new histogram plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public Plot histogram(String label, Partition<? extends Number, ? extends Number>  partition) {
		return histogram(label, partition.keys(), partition.values(), null);
	}

	/**
	 * Creates a new histogram plot.  The series is added to the given dataset, or if {@code null} a new dataset
	 * is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot histogram(String label, List<? extends Number> x, List<? extends Number> y,
			XYSeriesCollection dataset) {
		if (dataset == null) {
			createHistogramPlot();
			currentDataset++;
			dataset = new XYSeriesCollection();
		}

		// generate the dataset
		XYSeries series = new XYSeries(label, false, false);
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
			minX = Math.min(minX, x.get(i).doubleValue());
			maxX = Math.max(maxX, x.get(i).doubleValue());
		}

		dataset.addSeries(series);
		dataset.setIntervalWidth(0.75 * (maxX - minX) / x.size());

		// add the dataset to the plot
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(currentDataset, dataset);

		// setup the renderer
		Paint paint = paintHelper.get(dataset.getSeriesKey(0));
		XYBarRenderer renderer = new XYBarRenderer();
		renderer.setAutoPopulateSeriesStroke(false);

		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}


	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot area(String label, double[] x, double[] y) {
		return area(label, toList(x), toList(y));
	}

	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot area(String label, List<? extends Number> x, List<? extends Number> y) {
		return area(label, x, y, null);
	}

	/**
	 * Creates a new area plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public Plot area(String label, Partition<? extends Number, ? extends Number>  partition) {
		return area(label, partition.keys(), partition.values(), null);
	}

	/**
	 * Creates a new area plot series.  The series is added to the given dataset, or if {@code null} a new dataset is
	 * created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot area(String label, List<? extends Number> x, List<? extends Number> y, XYSeriesCollection dataset) {
		if (dataset == null) {
			createXYPlot();
			currentDataset++;
			dataset = new XYSeriesCollection();
		}

		// generate the dataset
		XYSeries series = new XYSeries(label, false, true);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}

		dataset.addSeries(series);

		// add the dataset to the plot
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(currentDataset, dataset);

		// setup the renderer
		Paint paint = paintHelper.get(dataset.getSeriesKey(0));
		XYAreaRenderer renderer = new XYAreaRenderer();
		renderer.setAutoPopulateSeriesStroke(false);

		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}

	/**
	 * Creates a new stacked area plot series.  The data will be stacked with any preceding calls to {@code stacked}.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot stacked(String label, double[] x, double[] y) {
		return stacked(label, toList(x), toList(y));
	}

	/**
	 * Creates a new stacked area plot series.  The data will be stacked with any preceding calls to {@code stacked}.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this instance
	 */
	public Plot stacked(String label, List<? extends Number> x, List<? extends Number> y) {
		return stacked(label, x, y, null);
	}

	/**
	 * Creates a new stacked area plot series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public Plot stacked(String label, Partition<? extends Number, ? extends Number>  partition) {
		return stacked(label, partition.keys(), partition.values(), null);
	}

	/**
	 * Creates a new area plot series.  The data will be stacked with any preceding calls to {@code stacked}.  The
	 * series is added to the given dataset, or if {@code null} a new dataset is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot stacked(String label, List<? extends Number> x, List<? extends Number> y, DefaultTableXYDataset dataset) {
		if (dataset == null) {
			createXYPlot();

			XYPlot plot = chart.getXYPlot();

			if (plot.getDataset(currentDataset) instanceof DefaultTableXYDataset xyDataset) {
				dataset = xyDataset;
			} else {
				currentDataset++;
				dataset = new DefaultTableXYDataset();
			}
		}

		// generate the dataset
		XYSeries series = new XYSeries(label, true, false);

		for (int i = 0; i < x.size(); i++) {
			series.add(x.get(i), y.get(i));
		}

		dataset.addSeries(series);

		// add the dataset to the plot
		XYPlot plot = chart.getXYPlot();
		plot.setDataset(currentDataset, dataset);

		// setup the renderer
		Paint paint = paintHelper.get(dataset.getSeriesKey(0));
		StackedXYAreaRenderer renderer = new StackedXYAreaRenderer();
		renderer.setAutoPopulateSeriesStroke(false);

		renderer.setDefaultStroke(new BasicStroke(3f, 1, 1));
		renderer.setDefaultPaint(paint);
		renderer.setDefaultFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}

	/**
	 * Creates a new heat map series.  The series is added to the given dataset, or if {@code null} a new dataset is
	 * created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param z the z values
	 * @return a reference to this instance
	 */
	public Plot heatMap(String label, double[] x, double[] y, double[][] z) {
		return heatMap(label, toList(x), toList(y), toList(z));
	}

	/**
	 * Creates a new heat map series.  The series is added to the given dataset, or if {@code null} a new dataset is
	 * created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param z the z values
	 * @return a reference to this instance
	 */
	public Plot heatMap(String label, List<? extends Number> x, List<? extends Number> y, List<? extends List<? extends Number>> z) {
		return heatMap(label, x, y, z, null);
	}

	/**
	 * Creates a new heat map series using the keys and values from a {@link Partition}.
	 * 
	 * @param label the label for the series
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public Plot heatMap(String label, Partition<? extends Pair<? extends Number, ? extends Number>, ? extends Number> partition) {
		List<? extends Number> xs = partition.keys().stream().map(Pair::getLeft).distinct().sorted().toList();
		List<? extends Number> ys = partition.keys().stream().map(Pair::getRight).distinct().sorted().toList();
		List<List<Number>> zs = new ArrayList<>();

		for (Number x : xs) {
			List<Number> row = new ArrayList<>();

			for (Number y : ys) {
				row.add(partition.filter(v -> v.getLeft().equals(x) && v.getRight().equals(y)).single().getValue());
			}

			zs.add(row);
		}

		return heatMap(label, xs, ys, zs, null);
	}

	/**
	 * Creates a new heat map series.  The series is added to the given dataset, or if {@code null} a new dataset is
	 * created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param z the z values
	 * @param dataset the dataset, or {@code null} if a new dataset should be created
	 * @return a reference to this instance
	 */
	private Plot heatMap(String label, List<? extends Number> x, List<? extends Number> y, List<? extends List<? extends Number>> z, DefaultXYZDataset dataset) {
		if (dataset == null) {
			createHeatMapPlot();
			currentDataset++;
			dataset = new DefaultXYZDataset();
		}

		// generate the dataset
		double[] xValues = new double[x.size() * y.size()];
		double[] yValues = new double[x.size() * y.size()];
		double[] zValues = new double[x.size() * y.size()];

		for (int i = 0; i < x.size(); i++) {
			for (int j = 0; j < y.size(); j++) {
				xValues[i * y.size() + j] = x.get(i).doubleValue();
				yValues[i * y.size() + j] = y.get(j).doubleValue();
				zValues[i * y.size() + j] = z.get(i).get(j).doubleValue();
			}
		}

		dataset.addSeries(label, new double[][] { xValues, yValues, zValues });

		// add the dataset to the plot
		XYZToolTipGenerator toolTipGenerator = new StandardXYZToolTipGenerator();

		XYBlockRenderer renderer = new XYBlockRenderer();
		renderer.setBlockWidth((StatUtils.max(xValues) - StatUtils.min(xValues)) / (x.size() - 1));
		renderer.setBlockHeight((StatUtils.max(yValues) - StatUtils.min(yValues)) / (y.size() - 1));
		renderer.setDefaultToolTipGenerator(toolTipGenerator);

		PaintScale paintScale = new GrayPaintScale(StatUtils.min(zValues), StatUtils.max(zValues));

		PaintScaleLegend psl = new PaintScaleLegend(paintScale, new NumberAxis(label));
		psl.setPosition(RectangleEdge.RIGHT);
		psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
		psl.setMargin(10.0, 10.0, 10.0, 10.0);

		renderer.setPaintScale(paintScale);

		XYPlot plot = chart.getXYPlot();
		plot.setDataset(currentDataset, dataset);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setRenderer(currentDataset, renderer);

		chart.addSubtitle(psl);
		chart.removeLegend();

		return this;
	}

	/**
	 * Modifies the line thickness or point size in the last dataset.  The size is applied to all series in the dataset.
	 * 
	 * @param size the size
	 * @return a reference to this instance
	 */
	public Plot withSize(float size) {
		if (chart.getPlot() instanceof XYPlot) {
			XYPlot plot = chart.getXYPlot();
			XYItemRenderer renderer = plot.getRenderer(currentDataset);

			if (renderer instanceof XYDotRenderer xyDotRenderer) {
				xyDotRenderer.setDotWidth((int)(size*2));
				xyDotRenderer.setDotHeight((int)(size*2));
			} else if (renderer.getDefaultStroke() instanceof BasicStroke oldStroke) {
				BasicStroke newStroke = new BasicStroke(
						size,
						oldStroke.getEndCap(),
						oldStroke.getLineJoin(),
						oldStroke.getMiterLimit(),
						oldStroke.getDashArray(),
						oldStroke.getDashPhase());

				renderer.setDefaultStroke(newStroke);
			} else {
				renderer.setDefaultStroke(new BasicStroke(size, 1, 1));
			}
		}

		return this;
	}

	/**
	 * Modifies the paint (e.g,. color) of each series in the last dataset.  If the dataset contains more series than
	 * the number of arguments, the arguments are reused as needed.
	 * 
	 * @param paint one or more paint instances
	 * @return a reference to this instance
	 */
	public Plot withPaint(Paint... paint) {
		if (chart.getPlot() instanceof XYPlot) {
			XYPlot plot = chart.getXYPlot();
			XYDataset dataset = plot.getDataset(currentDataset);
			XYItemRenderer renderer = plot.getRenderer(currentDataset);

			for (int i = 0; i < dataset.getSeriesCount(); i++) {
				Paint p = paint[i % paint.length];

				paintHelper.set(dataset.getSeriesKey(i), p);
				renderer.setSeriesPaint(i, p);

				if (renderer instanceof XYLineAndShapeRenderer xyRenderer) {
					xyRenderer.setSeriesFillPaint(i, p);
				}
			}
		} else if (chart.getPlot() instanceof CategoryPlot) {
			CategoryPlot plot = chart.getCategoryPlot();
			CategoryDataset dataset = plot.getDataset();
			CategoryItemRenderer renderer = plot.getRenderer();

			for (int i = 0; i < dataset.getRowCount(); i++) {
				Paint p = paint[i % paint.length];

				paintHelper.set(dataset.getRowKey(i), p);
				renderer.setSeriesPaint(i, p);
			}
		}

		return this;
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param filename the filename
	 * @return a reference to this instance
	 * @throws IOException if an I/O error occurred
	 */
	public Plot save(String filename) throws IOException {
		ImageUtils.save(getChart(), filename);
		return this;
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param file the file
	 * @return a reference to this instance
	 * @throws IOException if an I/O error occurred
	 */
	public Plot save(File file) throws IOException {
		ImageUtils.save(getChart(), file);
		return this;
	}

	/**
	 * Saves the plot to an image file.
	 * 
	 * @param file the file
	 * @param fileType the image file format
	 * @param width the image width
	 * @param height the image height
	 * @return a reference to this instance
	 * @throws IOException if an I/O error occurred
	 */
	public Plot save(File file, ImageFileType fileType, int width, int height) throws IOException {
		ImageUtils.save(getChart(), file, fileType, width, height);
		return this;
	}

	/**
	 * Returns the internal chart.  Allows further modification of the appearance of the chart.
	 * 
	 * @return the internal JFreeChart instance
	 */
	public JFreeChart getChart() {
		return chart;
	}

	/**
	 * Returns the chart embedded in a Swing panel for display.
	 * 
	 * @return a Swing panel for displaying the chart
	 */
	public ChartPanel getChartPanel() {
		return new ChartPanel(chart);
	}

	/**
	 * Displays the chart in a standalone window.
	 * 
	 * @return the window that was created
	 */
	public JFrame show() {
		return show(800, 600);
	}

	/**
	 * Displays the chart in a standalone window.
	 * 
	 * @param width the width of the chart
	 * @param height the height of the chart
	 * @return the window that was created
	 */
	public JFrame show(int width, int height) {
		return UI.showAndWait(() -> {
			JFrame frame = new JFrame(WINDOW_TITLE);
	
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(getChartPanel(), BorderLayout.CENTER);
			frame.setPreferredSize(new Dimension(width, height));
	
			return frame;
		});
	}

	/**
	 * Displays the chart in a blocking JDialog.
	 * 
	 * @param owner the owner of this dialog, which will be blocked until this dialog is closed
	 * @return the window that was created
	 */
	public JDialog showDialog(Window owner) {
		return showDialog(owner, 800, 600);
	}

	/**
	 * Displays the chart in a blocking JDialog.
	 * 
	 * @param owner the owner of this dialog, which will be blocked until this dialog is closed
	 * @param width the width of the chart
	 * @param height the height of the chart
	 * @return the window that was created
	 */
	public JDialog showDialog(Window owner, int width, int height) {
		return UI.showAndWait(() -> {
			JDialog dialog = new JDialog(owner, WINDOW_TITLE);
	
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add(getChartPanel(), BorderLayout.CENTER);
	
			dialog.setPreferredSize(new Dimension(width, height));
			dialog.pack();
	
			dialog.setModalityType(ModalityType.APPLICATION_MODAL);	
			return dialog;
		});
	}

}
