/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.analysis.diagnostics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.util.Localization;

/**
 * Displays a line plot of the runtime dynamics of a metric, displaying the 25, 50, and 75-th quantiles.
 */
public class LinePlot extends ResultPlot {

	private static final long serialVersionUID = -6265976590259977358L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(LinePlot.class);
	
	/**
	 * The resolution of the line plot, controlling the number of collected samples are included in each plotted point.
	 */
	private final int RESOLUTION = 500;
	
	/**
	 * Constructs a line plot for the specified metric.
	 * 
	 * @param frame the {@code DiagnosticTool} instance containing this plot
	 * @param metric the metric to display
	 */
	public LinePlot(DiagnosticTool frame, String metric) {
		super(frame, metric);
		
		setLayout(new BorderLayout());
	}
	
	/**
	 * Generate the individual series for the specified key.
	 * 
	 * @param key the key identifying which result to plot
	 * @param dataset the dataset to store the generated series
	 */
	protected void generateIndividualSeries(ResultKey key, DefaultTableXYDataset dataset) {
		for (ResultSeries series : controller.get(key)) {
			if (!series.getDefinedProperties().contains(metric)) {
				continue;
			}
		
			XYSeries xySeries = new XYSeries(key, false, false);

			for (IndexedResult result : series) {
				xySeries.add(result.getIndex(), result.getProperties().getDouble(metric));
			}

			dataset.addSeries(xySeries);
		}
	}

	/**
	 * Generates the quantile series for the specified key.
	 * 
	 * @param key the key identifying which result to plot
	 * @param dataset the dataset to store the generated series
	 */
	protected void generateQuantileSeries(ResultKey key, YIntervalSeriesCollection dataset) {
		YIntervalSeries ySeries = new YIntervalSeries(key);
		int currentNFE = 0;
		int maxNFE = 0;
		
		for (ResultSeries series : controller.get(key)) {
			maxNFE = Math.max(maxNFE, series.getEndingIndex());
		}

		while (currentNFE <= maxNFE) {
			DescriptiveStatistics statistics = new DescriptiveStatistics();

			for (ResultSeries series : controller.get(key)) {
				for (IndexedResult result : series) {
					if (result.getProperties().contains(metric) && result.getIndex() >= currentNFE &&
							result.getIndex() < currentNFE + RESOLUTION) {
						statistics.addValue(result.getProperties().getDouble(metric));
					}
				}
			}

			if (statistics.getN() > 0) {
				ySeries.add(currentNFE,
						statistics.getPercentile(50),
						statistics.getPercentile(25),
						statistics.getPercentile(75));
			}

			currentNFE += RESOLUTION;
		}
		
		dataset.addSeries(ySeries);
	}
	
	@Override
	protected void update() {
		XYDataset dataset = null;
		
		//generate the plot data
		if (controller.showIndividualTraces().get()) {
			dataset = new DefaultTableXYDataset();
			
			for (ResultKey key : frame.getSelectedResults()) {
				generateIndividualSeries(key, (DefaultTableXYDataset)dataset);
			}
		} else {
			dataset = new YIntervalSeriesCollection();

			for (ResultKey key : frame.getSelectedResults()) {
				generateQuantileSeries(key, (YIntervalSeriesCollection)dataset);
			}
		}

		//create the chart
		JFreeChart chart = ChartFactory.createXYLineChart(
				metric,
				localization.getString("text.NFE"),
				localization.getString("text.value"),
				dataset,
				PlotOrientation.VERTICAL,
				false,
				true,
				false);
		final XYPlot plot = chart.getXYPlot();
		
		//setup the series renderer
		if (controller.showIndividualTraces().get()) {
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
			
			for (int i=0; i<dataset.getSeriesCount(); i++) {
				Paint paint = frame.getPaintHelper().get(dataset.getSeriesKey(i));
	
				renderer.setSeriesStroke(i, new BasicStroke(1f, 1, 1));
				renderer.setSeriesPaint(i, paint);
			}
			
			plot.setRenderer(renderer);
		} else {
			DeviationRenderer renderer = new DeviationRenderer(true, false);
	
			for (int i=0; i<dataset.getSeriesCount(); i++) {
				Paint paint = frame.getPaintHelper().get(dataset.getSeriesKey(i));
	
				renderer.setSeriesStroke(i, new BasicStroke(3f, 1, 1));
				renderer.setSeriesPaint(i, paint);
				renderer.setSeriesFillPaint(i, paint);
			}
	
			plot.setRenderer(renderer);
		}
		
		//create the legend
		final LegendItemCollection items = plot.getLegendItems();
		Iterator<?> iterator = items.iterator();
		Set<ResultKey> uniqueKeys = new HashSet<ResultKey>();
		
		while (iterator.hasNext()) {
			LegendItem item = (LegendItem)iterator.next();
			
			if (uniqueKeys.contains(item.getSeriesKey())) {
				iterator.remove();
			} else {
				uniqueKeys.add((ResultKey)item.getSeriesKey());
			}
		}
		
		LegendItemSource source = () -> items;
		
		LegendTitle legend = new LegendTitle(source);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setFrame(new LineBorder());
		legend.setBackgroundPaint(Color.WHITE);
		legend.setPosition(RectangleEdge.BOTTOM);
		chart.addLegend(legend);

		//scale the axes
		final NumberAxis domainAxis = new NumberAxis();
		domainAxis.setAutoRange(true);
		plot.setDomainAxis(domainAxis);
		
		//add overlay
		if (controller.showLastTrace().get() &&
				!controller.showIndividualTraces().get() &&
				(controller.getLastSeries() != null) &&
				controller.getLastSeries().getDefinedProperties().contains(metric)) {
			DefaultTableXYDataset dataset2 = new DefaultTableXYDataset();
			XYSeries xySeries = new XYSeries(localization.getString("text.last"), false, false);
			
			for (IndexedResult result : controller.getLastSeries()) {
				xySeries.add(result.getIndex(), result.getProperties().getDouble(metric));
			}
			
			dataset2.addSeries(xySeries);
			
			XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
			renderer2.setSeriesStroke(0, new BasicStroke(1f, 1, 1));
			renderer2.setSeriesPaint(0, Color.BLACK);
			
			plot.setDataset(1, dataset2);
			plot.setRenderer(1, renderer2);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		}
		
		//update the chart in the GUI
		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
