package org.moeaframework.analysis.plot;

import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.analysis.stream.DataStream;

public class BoxAndWhiskerPlotBuilder extends PlotBuilder {
	
	private final CategoryAxis xAxis;
	
	private final NumberAxis yAxis;
	
	private final CategoryPlot plot;
	
	private final DefaultBoxAndWhiskerCategoryDataset dataset;
	
	public BoxAndWhiskerPlotBuilder() {
		super();
		
		xAxis = new CategoryAxis("");

		yAxis = new NumberAxis("Value");
		yAxis.setAutoRangeIncludesZero(false);
		
		dataset = new DefaultBoxAndWhiskerCategoryDataset();

		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		renderer.setAutoPopulateSeriesFillPaint(false);
		renderer.setAutoPopulateSeriesOutlinePaint(false);
		renderer.setAutoPopulateSeriesOutlineStroke(false);
		renderer.setAutoPopulateSeriesPaint(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesStroke(false);

		plot = new CategoryPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setRenderer(renderer);
		plot.setDataset(dataset);
	}
	
	public JFreeChart build() {
		return build(plot);
	}
	
	public BoxAndWhiskerPlotBuilder add(String label, double[] values) {
		return add(label, toList(values));
	}
	
	public BoxAndWhiskerPlotBuilder add(String label, DataStream<? extends Number> stream) {
		return add(label, stream.values());
	}
	
	public BoxAndWhiskerPlotBuilder add(String label, List<? extends Number> values) {
		dataset.add(values, label, "");
		return this;
	}
	
	public BoxAndWhiskerPlotBuilder add(IndicatorStatistics statistics) {
		for (String name : statistics.getGroupNames()) {
			add(name, statistics.getValues(name));
		}

		return this;
	}
	
}
