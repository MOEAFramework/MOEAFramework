package org.moeaframework.analysis.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
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
	
	/**
	 * Function used to generate a custom legend for the plot.  If {@code null}, the default legend is generated.
	 */
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
	
	public abstract JFreeChart build();
	
	public ChartPanel buildPanel() {
		return new ChartPanel(build());
	}
	
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
	 * Sets the chart title.
	 * 
	 * @param title the title
	 * @return a reference to this builder
	 */
	public T title(String title) {
		this.title = title;
		return getInstance();
	}
	
	public T defaultLegend() {
		return legend(null);
	}
	
	public T noLegend() {
		return legend((plot) -> null);
	}
	
	public T legend(Function<Plot, LegendTitle> legendBuilder) {
		this.legendBuilder = legendBuilder;
		return getInstance();
	}
	
	public T theme(ChartTheme theme) {
		this.theme = theme;
		return getInstance();
	}
	
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
	
	protected double[][] to2DArray(List<? extends List<? extends Number>> values) {
		double[][] result = new double[values.size()][];
		
		for (int i = 0; i < values.size(); i++) {
			result[i] = toArray(values.get(i));
		}
		
		return result;
	}
	
	protected void applyStyle(Plot plot, int dataset, StyleAttribute[] attributes) {
		for (StyleAttribute attribute : attributes) {
			attribute.apply(plot, dataset);
		}
	}
	
	protected void applyStyle(Plot plot, int dataset, int series, StyleAttribute[] attributes) {
		for (StyleAttribute attribute : attributes) {
			attribute.apply(plot, dataset, series);
		}
	}
	
	public static void main(String[] args) {
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.line("Line", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.stacked("Stacked 1", new double[] { 0.5, 1.5 }, new double[] { 0.5, 0.6 })
			.stacked("Stacked 2", new double[] { 0.5, 1.5 }, new double[] { 0.3, 0.2 })
			.area("Area", new double[] { 0, 1, 2 }, new double[] { 0, 0.5, 0 })
			.title("Basic Shapes")
			.xLabel("X")
			.yLabel("Y")
			.show();
	}
	
}
