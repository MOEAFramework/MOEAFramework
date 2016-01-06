/* Copyright 2009-2015 David Hadka
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
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.moeaframework.Analyzer.AnalyzerResults;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

public class Plot {

	private JFreeChart chart;

	private PaintHelper paintHelper;

	private int currentDataset;

	public Plot() {
		super();
		paintHelper = new PaintHelper();
		currentDataset = -1;
	}

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

	public Plot setTitle(String title) {
		chart.setTitle(title);
		return this;
	}

	public Plot setXLabel(String label) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getDomainAxis().setLabel(label);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().getDomainAxis().setLabel(label);
		}

		return this;
	}

	public Plot setYLabel(String label) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getRangeAxis().setLabel(label);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().getRangeAxis().setLabel(label);
		}
		return this;
	}

	public Plot setLabelsIfBlank(String xlabel, String ylabel) {
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
	
	public Plot setBackgroundPaint(Paint paint) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().setBackgroundPaint(paint);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().setBackgroundPaint(paint);
		}
		
		return this;
	}
	
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

	public Plot setXLim(double min, double max) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getDomainAxis().setRange(min, max);
		}

		return this;
	}

	public Plot setYLim(double min, double max) {
		if (chart.getPlot() instanceof XYPlot) {
			chart.getXYPlot().getRangeAxis().setRange(min, max);
		} else if (chart.getPlot() instanceof CategoryPlot) {
			chart.getCategoryPlot().getRangeAxis().setRange(min, max);
		}

		return this;
	}

	public Plot add(String label, Population population) {
		Solution solution = population.get(0);

		if (solution.getNumberOfObjectives() == 1) {
			return add(label, population, 0, 0);
		} else {
			return add(label, population, 0, 1);
		}
	}

	public Plot add(String label, Population population, int x, int y) {
		createXYPlot();
		currentDataset++;

		// generate the dataset
		XYSeries series = new XYSeries(label, false, true);

		for (Solution solution : population) {
			series.add(solution.getObjective(x), 
					solution.getObjective(y));
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
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

		// set the labels
		setLabelsIfBlank("Objective " + (x+1), "Objective " + (y+1));

		return this;
	}

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

	public Plot add(String label, Accumulator accumulator, String metric) {
		return add(label, accumulator, metric, null);
	}

	private Plot add(String label, Accumulator accumulator, String metric, XYSeriesCollection dataset) {
		if (dataset == null) {
			createXYPlot();
			currentDataset++;
			dataset = new XYSeriesCollection();
		}

		// generate the dataset
		XYSeries series = new XYSeries(label, true, false);

		for (int i = 0; i < accumulator.size("NFE"); i++) {
			series.add((Number)accumulator.get("NFE", i),
					(Number)accumulator.get(metric, i));
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

		// set the labels
		setLabelsIfBlank("NFE", "Value");

		return this;
	}

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

			plot.setRenderer(currentDataset, renderer, true);
		}

		return this;
	}

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
	
	public Plot save(String filename) throws IOException {
		return save(new File(filename));
	}

	public Plot save(File file) throws IOException {
		String filename = file.getName();
		String extension = filename.substring(filename.lastIndexOf('.')+1, filename.length());
		
		return save(file, extension, 800, 600);
	}

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
		g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
		String svg = null;
		Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
		this.chart.draw(g2, drawArea);
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
			return (Graphics2D) ctor.newInstance(w, h);
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

	public JFreeChart getChart() {
		return chart;
	}

	public ChartPanel getChartPanel() {
		return new ChartPanel(chart);
	}

	public void show() {
		show(800, 600);
	}
	
	public void show(int width, int height) {
		JFrame frame = new JFrame();
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(getChartPanel(), BorderLayout.CENTER);
		
		frame.setPreferredSize(new Dimension(width, height));
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("MOEA Framework Plot");
		frame.setVisible(true);
	}

}
