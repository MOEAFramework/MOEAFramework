package org.moeaframework.analysis.plot;

import java.awt.Paint;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.moeaframework.util.validate.Validate;

public class BarGraphBuilder extends PlotBuilder<BarGraphBuilder> {
	
	private final CategoryAxis xAxis;
	
	private final NumberAxis yAxis;
	
	private final CategoryPlot plot;
	
	private final BarRenderer renderer;
	
	private final DefaultCategoryDataset dataset;
			
	public BarGraphBuilder() {
		super();
		
		xAxis = new CategoryAxis("");
		
		yAxis = new NumberAxis("");
		
		dataset = new DefaultCategoryDataset();

		renderer = new BarRenderer();
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);
		
		plot = new CategoryPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setRangeZeroBaselineVisible(true);
		plot.setDataset(dataset);
		plot.setRenderer(renderer);
	}
	
	@Override
	public BarGraphBuilder getInstance() {
		return this;
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
	public BarGraphBuilder xLabel(String label) {
		xAxis.setLabel(label);
		return getInstance();
	}

	/**
	 * Sets the y-axis label.
	 * 
	 * @param label the label for the y-axis
	 * @return a reference to this builder
	 */
	public BarGraphBuilder yLabel(String label) {
		yAxis.setLabel(label);
		return getInstance();
	}
	
	public BarGraphBuilder bars(String label, double[] x, double[] y, StyleAttribute... style) {
		Validate.that("x.length", x.length).isEqualTo("y.length", y.length);
		
		for (int i = 0; i < x.length; i++) {
			dataset.addValue((Number)y[i], label, x[i]);
		}
		
		int seriesIndex = dataset.getRowCount() - 1;
		Paint paint = paintHelper.get(label);
		renderer.setSeriesPaint(seriesIndex, paint);
		renderer.setSeriesFillPaint(seriesIndex, paint);
		
		applyStyle(plot, 0, seriesIndex, style);
		
		if (!renderer.getSeriesPaint(seriesIndex).equals(paint)) {
			paintHelper.set(label, renderer.getSeriesPaint(seriesIndex));
		}
				
		return getInstance();
	}
	
	public BarGraphBuilder bars(String label, List<? extends Number> x, List<? extends Number> y, StyleAttribute... style) {
		return bars(label, toArray(x), toArray(y), style);
	}
	
}
