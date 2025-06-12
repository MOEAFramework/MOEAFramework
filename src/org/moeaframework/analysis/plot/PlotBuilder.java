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

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.analysis.plot.style.StyleAttribute;
import org.moeaframework.util.mvc.UI;
import org.moeaframework.util.validate.Validate;

/**
 * Base class for constructing plots using a builder pattern.
 * <p>
 * These plot builders are intended for quickly generating plots from common data structures produced by this library.
 * The ability to customize the plot style is therefore limited.  If different plotting options or styles is required,
 * you will need to utilize the underlying graphing library, JFreeChart, or a different library of your choice.
 * 
 * @param <T> the specific plot builder type
 */
public abstract class PlotBuilder<T extends PlotBuilder<?>> {
	
	private static final String WINDOW_TITLE = "MOEA Framework Plot";
	
	private String title;

	private Function<Plot, LegendTitle> legendBuilder;
		
	private ChartTheme theme;
	
	/**
	 * Maps labels to their assigned color.  This can be assigned an externally managed paint helper to keep colors
	 * synchronized.
	 */
	protected PaintHelper paintHelper;
	
	/**
	 * Constructs a new plot builder with default settings.
	 */
	public PlotBuilder() {
		super();
		this.paintHelper = new PaintHelper();
		this.theme = ChartFactory.getChartTheme();
	}
	
	/**
	 * Returns an instance of this builder cast to the concrete implementation type {@link T}.  This is used by any
	 * intermediate classes to returns the concrete implementation.
	 * 
	 * @return a reference to this builder instance
	 */
	protected abstract T getInstance();
	
	/**
	 * Builds and returns the configured plot.
	 * 
	 * @return the resulting plot
	 */
	public abstract JFreeChart build();
	
	/**
	 * Builds and returns a panel that can be added to a Swing UI.
	 * 
	 * @return the panel
	 */
	public ChartPanel buildPanel() {
		return new ChartPanel(build());
	}
	
	/**
	 * Sets the chart title.
	 * 
	 * @param title the title
	 * @return a reference to this builder
	 */
	public T title(String title) {
		this.title = title;
		return getInstance();
	}
	
	/**
	 * Configures the plot with the default legend, if any, for the chart type.  This is the default behavior.
	 * 
	 * @return a reference to this builder
	 */
	public T defaultLegend() {
		return legend(null);
	}
	
	/**
	 * Configures the plot to not display a legend.
	 * 
	 * @return a reference to this builder
	 */
	public T noLegend() {
		return legend((plot) -> null);
	}
	
	/**
	 * Provides a function to generate a custom legend for the plot.
	 * <ol>
	 *   <li>If the function is {@code null}, the default plot is generated for the plot type.
	 *   <li>If the function returns {@code null}, no legend is displayed in the plot.
	 *   <li>Otherwise, the returned legend is displayed in the plot.
	 * </ol>
	 * 
	 * @param legendBuilder the function to create the legend
	 * @return a reference to this builder
	 */
	public T legend(Function<Plot, LegendTitle> legendBuilder) {
		this.legendBuilder = legendBuilder;
		return getInstance();
	}
	
	/**
	 * Sets the overall style for plots.  However, please note that styles affecting individual series (size, shape,
	 * color) are ignored since we provide our own defaults.  Instead, use {@link #paintHelper(PaintHelper)} or
	 * {@link StyleAttribute} to customize the rendering of individual series.
	 * 
	 * @param theme the theme, or {@code null} to use the default theme
	 * @return a reference to this builder
	 */
	public T theme(ChartTheme theme) {
		this.theme = theme;
		return getInstance();
	}
	
	/**
	 * Sets the {@link PaintHelper} for assigning paints / colors to individual series.  The paint helper instance is
	 * updated with any new mappings.
	 * 
	 * @param paintHelper the paint helper
	 * @return a reference to this builder
	 */
	public T paintHelper(PaintHelper paintHelper) {
		Validate.that("paintHelper", paintHelper).isNotNull();
		this.paintHelper = paintHelper;
		return getInstance();
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param filename the filename
	 * @return a reference to this builder
	 * @throws IOException if an I/O error occurred
	 */
	public T save(String filename) throws IOException {
		ImageUtils.save(build(), filename);
		return getInstance();
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param file the file
	 * @return a reference to this builder
	 * @throws IOException if an I/O error occurred
	 */
	public T save(File file) throws IOException {
		ImageUtils.save(build(), file);
		return getInstance();
	}

	/**
	 * Saves the plot to an image file.
	 * 
	 * @param file the file
	 * @param fileType the image file format
	 * @param width the image width
	 * @param height the image height
	 * @return a reference to this builder
	 * @throws IOException if an I/O error occurred
	 */
	public T save(File file, ImageFileType fileType, int width, int height) throws IOException {
		ImageUtils.save(build(), file, fileType, width, height);
		return getInstance();
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
			frame.getContentPane().add(buildPanel(), BorderLayout.CENTER);
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
			dialog.getContentPane().add(buildPanel(), BorderLayout.CENTER);
	
			dialog.setPreferredSize(new Dimension(width, height));
			dialog.pack();
	
			dialog.setModalityType(ModalityType.APPLICATION_MODAL);	
			return dialog;
		});
	}
	
	/**
	 * Wraps the plot in a {@link JFreeChart}, which includes the title, legend, and the theme.
	 * 
	 * @param plot the plot
	 * @return the chart
	 */
	protected JFreeChart build(Plot plot) {
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legendBuilder == null);
		
		if (legendBuilder != null) {
			LegendTitle legend = legendBuilder.apply(plot);
			
			if (legend != null) {
				chart.addLegend(legend);
			}
		}
		
		if (theme != null) {
			theme.apply(chart);
		}
								
		return chart;
	}
	
	/**
	 * Converts a list of numbers into a double array.  Any null values are translated into {@value Double#NaN}.
	 * 
	 * @param values the list of numbers
	 * @return the double array
	 */
	protected double[] toArray(List<? extends Number> values) {
		double[] result = new double[values.size()];
		
		for (int i = 0; i < values.size(); i++) {
			Number value = values.get(i);
			
			if (value == null) {
				result[i] = Double.NaN;
			} else {
				result[i] = value.doubleValue();
			}
		}
		
		return result;
	}
	
	/**
	 * Converts a 2D list of numbers into a 2D double array.  Any null values are translated into {@value Double#NaN}.
	 * 
	 * @param values the 2D list of numbers
	 * @return the 2D double array
	 */
	protected double[][] to2DArray(List<? extends List<? extends Number>> values) {
		double[][] result = new double[values.size()][];
		
		for (int i = 0; i < values.size(); i++) {
			result[i] = toArray(values.get(i));
		}
		
		return result;
	}
	
	/**
	 * Applies the given style attributes to all series in a dataset.
	 * 
	 * @param plot the plot
	 * @param dataset the index of the dataset
	 * @param attributes the style attributes
	 */
	protected void applyStyle(Plot plot, int dataset, StyleAttribute[] attributes) {
		for (StyleAttribute attribute : attributes) {
			attribute.apply(plot, dataset);
		}
	}
	
	/**
	 * Applies the given style attributes to the specified series in a dataset.
	 * 
	 * @param plot the plot
	 * @param dataset the index of the dataset
	 * @param series the index of the series
	 * @param attributes the style attributes
	 */
	protected void applyStyle(Plot plot, int dataset, int series, StyleAttribute[] attributes) {
		for (StyleAttribute attribute : attributes) {
			attribute.apply(plot, dataset, series);
		}
	}
	
	/**
	 * Returns {@code true} if a style attribute exists of the specified type.
	 * 
	 * @param attributes the style attributes
	 * @param type the style attribute type
	 * @return {@code true} if a style attribute exists, {@code false} otherwise
	 */
	protected boolean hasStyleAttribute(StyleAttribute[] attributes, Class<? extends StyleAttribute> type) {
		for (StyleAttribute attribute : attributes) {
			if (type.isInstance(attribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns a new XY plot builder.
	 * 
	 * @return the plot builder
	 */
	public static XYPlotBuilder xy() {
		return new XYPlotBuilder();
	}
	
	/**
	 * Returns a new bar graph plot builder.
	 * 
	 * @return the plot builder
	 */
	public static BarGraphBuilder barGraph() {
		return new BarGraphBuilder();
	}
	
	/**
	 * Returns a new box-and-whisker plot builder.
	 * 
	 * @return the plot builder
	 */
	public static BoxAndWhiskerPlotBuilder boxAndWhisker() {
		return new BoxAndWhiskerPlotBuilder();
	}
	
	/**
	 * Returns a new heat map plot builder.
	 * 
	 * @return the plot builder
	 */
	public static HeatMapBuilder heatMap() {
		return new HeatMapBuilder();
	}
	
	/**
	 * Returns a new sensitivity analysis result plot builder.
	 * 
	 * @return the plot builder
	 */
	public static SensitivityPlotBuilder sensitivity() {
		return new SensitivityPlotBuilder();
	}
	
}
