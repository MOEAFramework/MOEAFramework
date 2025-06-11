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

public class BarGraphBuilder extends PlotBuilder {
	
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
	
	public JFreeChart build() {
		return build(plot);
	}
	
	public BarGraphBuilder add(String label, double[] x, double[] y) {
		return add(label, toList(x), toList(y));
	}
	
	public BarGraphBuilder add(String label, List<? extends Number> x, List<? extends Number> y) {
		for (int i = 0; i < x.size(); i++) {
			dataset.addValue(y.get(i), label, x.get(i).doubleValue());
		}
				
		Paint paint = paintHelper.get(label);
		renderer.setSeriesPaint(dataset.getRowCount() - 1, paint);
		renderer.setSeriesFillPaint(dataset.getRowCount() - 1, paint);
				
		return this;
	}
	
}
