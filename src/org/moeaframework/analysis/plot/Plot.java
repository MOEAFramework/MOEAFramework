/* Copyright 2009-2016 David Hadka
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Dialog.ModalityType;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.moeaframework.Analyzer;
import org.moeaframework.Analyzer.AnalyzerResults;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

/**
 * Provides simple 2D plotting capabilities.  This is intended to allow the
 * rapid creation of 2D plots, supporting:
 * <ol>
 *   <li>scatter plots of bi-objective populations,</li>
 *   <li>line plots of runtime dynamics (via an {@link Accumulator},</li>
 *   <li>box-and-whisker plots of performance statistics (via an {@link Analyzer}), and</li>
 *   <li>other plots of basic data types (e.g., line, scatter, area, stacked).</li>
 * </ol>
 * It is possible to combine datasets by calling more than one {@code add}
 * method, but you can not mix different plot types (e.g., XY plots versus
 * categorical plots).  Thus, box-and-whisker plots can not be overlaid on a
 * line plot.
 * <p>
 * In general, one should first generate the plot artifacts by calling
 * {@code line}, {@code scatter}, {@code area}, {@code stacked}, or any of the
 * {@code add} methods.  Artifacts can be customized by immediately calling
 * one of the {@code with*} methods after generating the artifact (the
 * customization is only applied to the last artifact).  Lastly, call the
 * {@code set*} methods to customize the overall appearance of the chart.
 * Call {@code show} to display the chart in a window or {@code save} to
 * create an image file.  For example:
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
 * This class is not intended to be a fully featured plotting library.  To
 * generate more sophisticated plots or customize their appearance, one must
 * instead use JFreeChart, JZY3D, or another Java plotting library.
 * <p>
 * Generated plots can be saved to PNG or JPEG files.  If JFreeSVG is available
 * on the classpath, SVG files can be generated.  JFreeSVG can be obtained from
 * http://www.jfree.org/jfreesvg/.
 */
public class Plot {

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
	 * If the chart has not yet been initialized, creates a chart for XY data.
	 * If the chart is already initialized, checks if the chart is for XY data.
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
			renderer.setBaseToolTipGenerator(toolTipGenerator);
			plot.setRenderer(renderer);
			plot.setOrientation(PlotOrientation.VERTICAL);

			chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			ChartFactory.getChartTheme().apply(chart);
		} else if (!(chart.getPlot() instanceof XYPlot)) {
			throw new FrameworkException("Can not combine XY plot and categorial plot");
		}
	}

	/**
	 * If the chart has not yet been initialized, creates a chart for
	 * categorical data.  If the chart is already initialized, checks if the
	 * chart is for categorical data.
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
			renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

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
	 * Sets the chart title.
	 * 
	 * @param title the title
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot setTitle(String title) {
		chart.setTitle(title);
		return this;
	}

	/**
	 * Sets the x-axis label.
	 * 
	 * @param label the label for the x-axis
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
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
	 * Creates a new scatter plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot scatter(String label, List<? extends Number> x, List<? extends Number> y) {
		return scatter(label, x, y, null);
	}
	
	/**
	 * Creates a new scatter plot series.  The series is added to the given
	 * dataset, or if {@code null} a new dataset is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be
	 *        created
	 * @return a reference to this {@code Plot} instance
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
		renderer.setBasePaint(paint);
		renderer.setBaseFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}
	
	/**
	 * Converts a double array to a list.
	 * 
	 * @param x the double array
	 * @return the list of doubles
	 */
	private List<Double> toList(double[] x) {
		List<Double> result = new ArrayList<Double>();
		
		for (int i = 0; i < x.length; i++) {
			result.add(x[i]);
		}
		
		return result;
	}

	/**
	 * Displays the solutions in the given population in a 2D scatter plot.
	 * Only two objectives will be displayed.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @return a reference to this {@code Plot} instance
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
	 * Displays the solutions in the given population in a 2D scatter plot.
	 * The two given objectives will be displayed.
	 * 
	 * @param label the label for the series
	 * @param population the population
	 * @param x the objective to plot on the x-axis
	 * @param y the objective to plot on the y-axis
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot add(String label, Population population, int x, int y) {
		List<Number> xs = new ArrayList<Number>();
		List<Number> ys = new ArrayList<Number>();
		
		for (Solution solution : population) {
			if (!solution.violatesConstraints()) {
				xs.add(solution.getObjective(x));
				ys.add(solution.getObjective(y));
			}
		}
		
		scatter(label, xs, ys);
		setLabelsIfBlank("Objective " + (x+1), "Objective " + (y+1));
		
		return this;
	}

	/**
	 * Displays the runtime data stored in an {@link Accumulator} as one or
	 * more line plots.
	 * 
	 * @param accumulator the {@code Accumulator} instance
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot add(Accumulator accumulator) {
		createXYPlot();
		currentDataset++;
		XYSeriesCollection dataset = new XYSeriesCollection();

		for (String key : accumulator.keySet()) {
			if (!key.equals("NFE")) {
				add(key, accumulator, key, dataset);
			}
		}

		return this;
	}

	/**
	 * Displays the runtime data for the given metric as a line plot.
	 * 
	 * @param label the label for the series
	 * @param accumulator the {@code Accumulator} instance
	 * @param metric the name of the performance metric to plot
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot add(String label, Accumulator accumulator, String metric) {
		return add(label, accumulator, metric, null);
	}

	/**
	 * Displays the runtime data for the given metric as a line plot.  The
	 * series is added to the given dataset, or if {@code null} a new dataset
	 * is created.
	 * 
	 * @param label the label for the series
	 * @param accumulator the {@code Accumulator} instance
	 * @param metric the name of the performance metric to plot
	 * @param dataset the dataset, or {@code null} if a new dataset should be
	 *        created
	 * @return a reference to this {@code Plot} instance
	 */
	private Plot add(String label, Accumulator accumulator, String metric, XYSeriesCollection dataset) {
		List<Number> xs = new ArrayList<Number>();
		List<Number> ys = new ArrayList<Number>();
		
		try {
			for (int i = 0; i < accumulator.size("NFE"); i++) {
				xs.add((Number)accumulator.get("NFE", i));
				ys.add((Number)accumulator.get(metric, i));
			}
		} catch (ClassCastException e) {
			return this;
		}
		
		line(label, xs, ys, dataset);
		setLabelsIfBlank("NFE", "Value");

		return this;
	}
	
	/**
	 * Creates a new line plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot line(String label, List<? extends Number> x, List<? extends Number> y) {
		return line(label, x, y, null);
	}
	
	/**
	 * Creates a new line plot series.  The series is added to the given
	 * dataset, or if {@code null} a new dataset is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be
	 *        created
	 * @return a reference to this {@code Plot} instance
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

		renderer.setBaseStroke(new BasicStroke(3f, 1, 1));
		renderer.setBasePaint(paint);
		renderer.setBaseFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}
	
	/**
	 * Creates a new area plot series.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this {@code Plot} instance
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
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot area(String label, List<? extends Number> x, List<? extends Number> y) {
		return area(label, x, y, null);
	}
	
	/**
	 * Creates a new area plot series.  The series is added to the given
	 * dataset, or if {@code null} a new dataset is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be
	 *        created
	 * @return a reference to this {@code Plot} instance
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

		renderer.setBaseStroke(new BasicStroke(3f, 1, 1));
		renderer.setBasePaint(paint);
		renderer.setBaseFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}
	
	/**
	 * Creates a new stacked area plot series.  The data will be stacked with
	 * any preceding calls to {@code stacked}.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot stacked(String label, double[] x, double[] y) {
		return stacked(label, toList(x), toList(y));
	}
	
	/**
	 * Creates a new stacked area plot series.  The data will be stacked with
	 * any preceding calls to {@code stacked}.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot stacked(String label, List<? extends Number> x, List<? extends Number> y) {
		return stacked(label, x, y, null);
	}
	
	/**
	 * Creates a new area plot series.  The data will be stacked with
	 * any preceding calls to {@code stacked}.  The series is added to the given
	 * dataset, or if {@code null} a new dataset is created.
	 * 
	 * @param label the label for the series
	 * @param x the x values
	 * @param y the y values
	 * @param dataset the dataset, or {@code null} if a new dataset should be
	 *        created
	 * @return a reference to this {@code Plot} instance
	 */
	private Plot stacked(String label, List<? extends Number> x, List<? extends Number> y, DefaultTableXYDataset dataset) {
		if (dataset == null) {
			createXYPlot();
			
			XYPlot plot = chart.getXYPlot();
			
			if (plot.getDataset(currentDataset) instanceof DefaultTableXYDataset) {
				dataset = (DefaultTableXYDataset)plot.getDataset(currentDataset);
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

		renderer.setBaseStroke(new BasicStroke(3f, 1, 1));
		renderer.setBasePaint(paint);
		renderer.setBaseFillPaint(paint);

		plot.setRenderer(currentDataset, renderer);

		return this;
	}
	
	/**
	 * Displays the statistical results from an {@link Analyzer} as a
	 * box-and-whisker plot.
	 * 
	 * @param analyzer the {@code Analyzer} instance
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot add(Analyzer analyzer) {
		return add(analyzer.getAnalysis());
	}

	/**
	 * Displays the statistical results from an {@link AnalyzerResults} as a
	 * box-and-whisker plot.
	 * 
	 * @param result the {@code AnalyzerResults} instance
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot add(AnalyzerResults result) {
		createCategoryPlot();

		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		for (String algorithm : result.getAlgorithms()) {
			for (String indicator : result.get(algorithm).getIndicators()) {
				List<Double> values = new ArrayList<Double>();

				for (double value : result.get(algorithm).get(indicator).getValues()) {
					values.add(value);
				}

				dataset.add(values, algorithm, indicator);
			}
		}

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setDataset(dataset);

		return this;
	}

	/**
	 * Modifies the line thickness or point size in the last dataset.  The
	 * size is applied to all series in the dataset.
	 * 
	 * @param size the size
	 * @return a reference to this {@code Plot} instance
	 */
	public Plot withSize(float size) {
		if (chart.getPlot() instanceof XYPlot) {
			XYPlot plot = chart.getXYPlot();
			XYItemRenderer renderer = plot.getRenderer(currentDataset);

			if (renderer instanceof XYDotRenderer) {
				((XYDotRenderer)renderer).setDotWidth((int)(size*2));
				((XYDotRenderer)renderer).setDotHeight((int)(size*2));
			} else if (renderer.getBaseStroke() instanceof BasicStroke) {
				BasicStroke oldStroke = (BasicStroke)renderer.getBaseStroke();

				BasicStroke newStroke = new BasicStroke(
						size,
						oldStroke.getEndCap(),
						oldStroke.getLineJoin(),
						oldStroke.getMiterLimit(),
						oldStroke.getDashArray(),
						oldStroke.getDashPhase());

				renderer.setBaseStroke(newStroke);	
			} else {
				renderer.setBaseStroke(new BasicStroke(size, 1, 1));
			}
		}

		return this;
	}

	/**
	 * Modifies the paint (e.g,. color) of each series in the last dataset.
	 * If the dataset contains more series than the number of arguments, the
	 * arguments are reused as needed.
	 * 
	 * @param paint one or more paint instances
	 * @return a reference to this {@code Plot} instance
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

				if (renderer instanceof XYLineAndShapeRenderer) {
					((XYLineAndShapeRenderer)renderer).setSeriesFillPaint(i, p);
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
	 * Saves the plot to an image file.  The type of image is determined from
	 * the filename extension.  See {@link #save(File, String, int, int)} for
	 * a list of supported types.
	 * 
	 * @param filename the filename
	 * @return a reference to this {@code Plot} instance
	 * @throws IOException if an I/O error occurred
	 */
	public Plot save(String filename) throws IOException {
		return save(new File(filename));
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from
	 * the filename extension.  See {@link #save(File, String, int, int)} for
	 * a list of supported types.
	 * 
	 * @param file the file
	 * @return a reference to this {@code Plot} instance
	 * @throws IOException if an I/O error occurred
	 */
	public Plot save(File file) throws IOException {
		String filename = file.getName();
		String extension = filename.substring(filename.lastIndexOf('.')+1, filename.length());
		
		return save(file, extension, 800, 600);
	}

	/**
	 * Saves the plot to an image file.  The format must be one of {@code png},
	 * {@code jpeg}, or {@code svg} (requires JFreeSVG).
	 * 
	 * @param file the file
	 * @param format the image format
	 * @param width the image width
	 * @param height the image height
	 * @return a reference to this {@code Plot} instance
	 * @throws IOException if an I/O error occurred
	 */
	public Plot save(File file, String format, int width, int height) throws IOException {
		if (format.equalsIgnoreCase("PNG")) {
			ChartUtilities.saveChartAsPNG(
					file,
					chart,
					width,
					height);
		} else if (format.equalsIgnoreCase("JPG") || format.equalsIgnoreCase("JPEG")) {
			ChartUtilities.saveChartAsJPEG(
					file,
					chart,
					width,
					height);
		} else if (format.equalsIgnoreCase("SVG")) {
			String svg = generateSVG(width, height);
			BufferedWriter writer = null;
			
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
				writer.write(svg);
				writer.write("\n");
				writer.flush();
			} finally {
				if (writer != null) {
					writer.close();
				}
			} 
		}
		
		return this;
	}

	/**
	 * Generates a string containing a rendering of the chart in SVG format.
	 * This feature is only supported if the JFreeSVG library is included on 
	 * the classpath.
	 * 
	 * This is copied from JFreeChart's ChartPanel class (version 1.0.19).
	 * 
	 * @return A string containing an SVG element for the current chart, or 
	 *     <code>null</code> if there is a problem with the method invocation
	 *     by reflection.
	 */
	private String generateSVG(int width, int height) {
		Graphics2D g2 = createSVGGraphics2D(width, height);
		
		if (g2 == null) {
			throw new IllegalStateException("JFreeSVG library is not present.");
		}
		
		// we suppress shadow generation, because SVG is a vector format and
		// the shadow effect is applied via bitmap effects...
		g2.setRenderingHint(new RenderingHints.Key(0) {
	        @Override
	        public boolean isCompatibleValue(Object val) {
	            return val instanceof Boolean;
	        }
	    }, true);
		
		String svg = null;
		Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(g2, drawArea);
		
		try {
			Method m = g2.getClass().getMethod("getSVGElement");
			svg = (String) m.invoke(g2);
		} catch (NoSuchMethodException e) {
			// null will be returned
		} catch (SecurityException e) {
			// null will be returned
		} catch (IllegalAccessException e) {
			// null will be returned
		} catch (IllegalArgumentException e) {
			// null will be returned
		} catch (InvocationTargetException e) {
			// null will be returned
		}
		
		return svg;
	}

	/**
	 * This is copied from JFreeChart's ChartPanel class (version 1.0.19).
	 */
	private Graphics2D createSVGGraphics2D(int w, int h) {
		try {
			Class<?> svgGraphics2d = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
			Constructor<?> ctor = svgGraphics2d.getConstructor(int.class, int.class);
			return (Graphics2D)ctor.newInstance(w, h);
		} catch (ClassNotFoundException ex) {
			return null;
		} catch (NoSuchMethodException ex) {
			return null;
		} catch (SecurityException ex) {
			return null;
		} catch (InstantiationException ex) {
			return null;
		} catch (IllegalAccessException ex) {
			return null;
		} catch (IllegalArgumentException ex) {
			return null;
		} catch (InvocationTargetException ex) {
			return null;
		}
	}

	/**
	 * Returns the internal chart.  Allows further modification of the
	 * appearance of the chart.
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
		JFrame frame = new JFrame();
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(getChartPanel(), BorderLayout.CENTER);
		
		frame.setPreferredSize(new Dimension(width, height));
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("MOEA Framework Plot");
		frame.setVisible(true);
		
		return frame;
	}
	
	/**
	 * Displays the chart in a blocking JDialog.
	 */
	public JDialog showDialog() {
		return showDialog(800, 600);
	}
	
	/**
	 * Displays the chart in a blocking JDialog.
	 * 
	 * @param width the width of the chart
	 * @param height the height of the chart
	 * @return the window that was created
	 */
	public JDialog showDialog(int width, int height) {
		JDialog frame = new JDialog();
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(getChartPanel(), BorderLayout.CENTER);
		
		frame.setPreferredSize(new Dimension(width, height));
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("MOEA Framework Plot");
		frame.setModalityType(ModalityType.APPLICATION_MODAL);
		frame.setVisible(true);
		
		return frame;
	}

}
