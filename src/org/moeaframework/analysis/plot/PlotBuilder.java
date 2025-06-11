package org.moeaframework.analysis.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.util.mvc.UI;

/**
 * Base class for constructing plots using a builder pattern.
 */
public abstract class PlotBuilder {
	
	private static final String WINDOW_TITLE = "MOEA Framework Plot";
	
	private String title;
		
	private boolean showLegend;
		
	private ChartTheme theme;
	
	/**
	 * Maps labels to their assigned color.
	 */
	protected final PaintHelper paintHelper;
	
	/**
	 * Constructs a new plot builder with default settings.
	 */
	public PlotBuilder() {
		super();
		this.paintHelper = new PaintHelper();
		this.showLegend = true;
		this.theme = ChartFactory.getChartTheme();
	}
	
	public abstract JFreeChart build();
	
	public ChartPanel buildPanel() {
		return new ChartPanel(build());
	}
	
	protected JFreeChart build(Plot plot) {
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, showLegend);
		
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
	public PlotBuilder setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public PlotBuilder withLegend(boolean showLegend) {
		this.showLegend = showLegend;
		return this;
	}
	
	public PlotBuilder withTheme(ChartTheme theme) {
		this.theme = theme;
		return this;
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param filename the filename
	 * @return a reference to this builder
	 * @throws IOException if an I/O error occurred
	 */
	public PlotBuilder save(String filename) throws IOException {
		ImageUtils.save(build(), filename);
		return this;
	}

	/**
	 * Saves the plot to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param file the file
	 * @return a reference to this builder
	 * @throws IOException if an I/O error occurred
	 */
	public PlotBuilder save(File file) throws IOException {
		ImageUtils.save(build(), file);
		return this;
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
	public PlotBuilder save(File file, ImageFileType fileType, int width, int height) throws IOException {
		ImageUtils.save(build(), file, fileType, width, height);
		return this;
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
	 * Converts a double array to a list.
	 * 
	 * @param x the double array
	 * @return the list of doubles
	 */
	protected List<Double> toList(double[] x) {
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
	protected List<List<Double>> toList(double[][] x) {
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
	
	public static void main(String[] args) {
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.line("Line", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.stacked("Stacked 1", new double[] { 0.5, 1.5 }, new double[] { 0.5, 0.6 })
			.stacked("Stacked 2", new double[] { 0.5, 1.5 }, new double[] { 0.3, 0.2 })
			.area("Area", new double[] { 0, 1, 2 }, new double[] { 0, 0.5, 0 })
			.setTitle("Basic Shapes")
//			.setXLabel("X")
//			.setYLabel("Y")
			.show();
	}
	
}
