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
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.analysis.plot.style.PlotAttribute;
import org.moeaframework.analysis.plot.style.StyleAttribute;
import org.moeaframework.analysis.plot.style.ValueAttribute;
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
	
	/**
	 * Interface for display drivers, which are responsible for displaying the resulting plot to the user, typically
	 * in some windowing system.
	 */
	@FunctionalInterface
	public static interface DisplayDriver {
		
		/**
		 * The default display driver that wraps the plot in a non-blocking window.
		 */
		public static final DisplayDriver DEFAULT = new DisplayDriver() {
			
			@Override
			public void show(PlotBuilder<?> builder, int width, int height) {
				UI.show(() -> {
					JFrame frame = new JFrame(WINDOW_TITLE);
			
					frame.getContentPane().setLayout(new BorderLayout());
					frame.getContentPane().add(builder.buildPanel(), BorderLayout.CENTER);
					frame.setPreferredSize(new Dimension(width, height));
			
					return frame;
				});
			}
			
		};
		
		/**
		 * Shows the given plot.
		 * 
		 * @param builder the plot builder
		 * @param width the requested display width
		 * @param height the requested display height
		 */
		public void show(PlotBuilder<?> builder, int width, int height);
		
	}
	
	/**
	 * The current display driver.
	 */
	private static DisplayDriver DISPLAY_DRIVER;
	
	/**
	 * Sets the display driver, which will be used by subsequent calls to {@link #show(int, int)} when displaying this
	 * plot.
	 * 
	 * @param displayDriver the new display driver
	 */
	public static synchronized void setDisplayDriver(DisplayDriver displayDriver) {
		Validate.that("displayDriver", displayDriver).isNotNull();
		DISPLAY_DRIVER = displayDriver;
	}
	
	/**
	 * Returns the current display driver.
	 * 
	 * @return the current display driver
	 */
	public static synchronized DisplayDriver getDisplayDriver() {
		return DISPLAY_DRIVER;
	}
	
	static {
		setDisplayDriver(DisplayDriver.DEFAULT);
	}
	
	private String title;
	
	private String subtitle;

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
	 * Sets the chart subtitle.
	 * 
	 * @param subtitle the subtitle
	 * @return a reference to this builder
	 */
	public T subtitle(String subtitle) {
		this.subtitle = subtitle;
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
	 * Sets the overall theme for the chart, specifying styles for titles, labels, axes, etc.
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
	 * @param width the image width
	 * @param height the image height
	 * @return a reference to this builder
	 * @throws IOException if an I/O error occurred
	 */
	public T save(File file, int width, int height) throws IOException {
		ImageUtils.save(build(), file, width, height);
		return getInstance();
	}

	/**
	 * Displays the chart.
	 */
	public void show() {
		show(800, 600);
	}

	/**
	 * Displays the chart.
	 * 
	 * @param width the width of the chart
	 * @param height the height of the chart
	 */
	public void show(int width, int height) {
		getDisplayDriver().show(getInstance(), width, height);
	}

	/**
	 * Wraps the plot in a {@link JFreeChart}, which includes the title, legend, and the theme.
	 * 
	 * @param plot the plot
	 * @return the chart
	 */
	protected JFreeChart build(Plot plot) {
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legendBuilder == null);
		
		if (subtitle != null) {
			chart.addSubtitle(new TextTitle(subtitle));
		}
		
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
	 * Converts a list of objects into a double array.
	 * 
	 * @param <D> the type of each object
	 * @param <V> type numeric type returned by the getter
	 * @param data the list of objects
	 * @param getter a function converting each object into a numeric value
	 * @return the double array
	 */
	protected <D, V extends Number> double[] toArray(List<D> data, Function<D, V> getter) {
		List<V> values = new ArrayList<>();
		
		for (D obj : data) {
			values.add(getter.apply(obj));
		}
		
		return toArray(values);
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
	 * Applies the style attributes to all series in a dataset.
	 * 
	 * @param plot the plot
	 * @param dataset the index of the dataset
	 * @param attributes the attributes configuring the plot
	 */
	protected void applyStyle(Plot plot, int dataset, PlotAttribute... attributes) {
		for (PlotAttribute attribute : attributes) {
			if (attribute instanceof StyleAttribute styleAttribute) {
				styleAttribute.apply(plot, dataset);
			}
		}
	}
	
	/**
	 * Applies the style attributes to the specified series in a dataset.
	 * 
	 * @param plot the plot
	 * @param dataset the index of the dataset
	 * @param series the index of the series
	 * @param attributes the attributes configuring the plot
	 */
	protected void applyStyle(Plot plot, int dataset, int series, PlotAttribute... attributes) {
		for (PlotAttribute attribute : attributes) {
			if (attribute instanceof StyleAttribute styleAttribute) {
				styleAttribute.apply(plot, dataset, series);
			}
		}
	}
	
	/**
	 * Returns the first attribute matching the specified type.
	 * 
	 * @param <A> the type of the attribute
	 * @param type the class of the attribute
	 * @param attributes the attributes configuring the plot
	 * @return the matching attribute, or {@code null} if no such attribute was found
	 */
	protected <A extends PlotAttribute> A getAttribute(Class<A> type, PlotAttribute... attributes) {
		for (PlotAttribute attribute : attributes) {
			if (type.isInstance(attribute)) {
				return type.cast(attribute);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the value stored in a {@link ValueAttribute} or the default value.
	 * 
	 * @param <A> the type of the attribute
	 * @param <V> the type of the value
	 * @param type the class of the value attribute
	 * @param defaultVaalue the default value if the attribute is not set
	 * @param attributes the attributes configuring the plot
	 * @return the value from the matching attribute, or the default value if no match was found
	 */
	protected <V, A extends ValueAttribute<V>> V getValueOrDefault(Class<A> type, V defaultValue, PlotAttribute... attributes) {
		ValueAttribute<V> attribute = getAttribute(type, attributes);
		return attribute != null ? attribute.get() : defaultValue;
	}
	
	/**
	 * Returns the value stored in a {@link ValueAttribute} or the default value.
	 * 
	 * @param <A> the type of the attribute
	 * @param <V> the type of the value
	 * @param type the class of the value attribute
	 * @param defaultVaalue the default value if the attribute is not set
	 * @param attributes the attributes configuring the plot
	 * @return the value from the matching attribute, or the default value if no match was found
	 */
	protected <V, A extends ValueAttribute<V>> V getValueOrDefault(Class<A> type, A defaultValue, PlotAttribute... attributes) {
		ValueAttribute<V> attribute = getAttribute(type, attributes);
		return attribute != null ? attribute.get() : defaultValue.get();
	}
	
	/**
	 * Returns {@code true} if an attribute exists matching the specified type.
	 * 
	 * @param type class of the attribute
	 * @param attributes the attributes configuring the plot
	 * @return {@code true} if a matching attribute exists, {@code false} otherwise
	 */
	protected boolean hasAttribute(Class<? extends PlotAttribute> type, PlotAttribute... attributes) {
		return getAttribute(type, attributes) != null;
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
