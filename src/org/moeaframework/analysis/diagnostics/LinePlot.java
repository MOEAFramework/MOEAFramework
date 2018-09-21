/* Copyright 2009-2018 David Hadka
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
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
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.util.Localization;

/**
 * Displays a line plot of the runtime dynamics of a metric, displaying the
 * 25, 50, and 75 quantiles.
 */
public class LinePlot extends ResultPlot {

	private static final long serialVersionUID = -6265976590259977358L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			LinePlot.class);
	
	/**
	 * The resolution of the line plot, controlling the number of collected
	 * samples are included in each plotted point.
	 */
	private final int RESOLUTION = 500;

	/**
	 * A data point, storing the NFE and corresponding metric value.
	 */
	private static class DataPoint implements Comparable<DataPoint> {
		
		/**
		 * The number of evaluations of this data point.
		 */
		private final int NFE;
		
		/**
		 * The metric value of this data point.
		 */
		private final double value;
	
		/**
		 * Constructs a data point with the specified number of evaluations and
		 * metric value.
		 * 
		 * @param NFE the number of evaluations of this data point
		 * @param value the metric value of this data point
		 */
		public DataPoint(int NFE, double value) {
			super();
			this.NFE = NFE;
			this.value = value;
		}
	
		@Override
		public int compareTo(DataPoint rhs) {
			return new CompareToBuilder().append(NFE, rhs.NFE).toComparison();
		}

		public int getNFE() {
			return NFE;
		}

		public double getValue() {
			return value;
		}
		
	}
	
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
	protected void generateIndividualSeries(ResultKey key, 
			DefaultTableXYDataset dataset) {
		for (Accumulator accumulator : controller.get(key)) {
			if (!accumulator.keySet().contains(metric)) {
				continue;
			}
		
			XYSeries series = new XYSeries(key, false, false);

			for (int i=0; i<accumulator.size(metric); i++) {
				series.add((Number)accumulator.get("NFE", i), 
						(Number)accumulator.get(metric, i));
			}
			
			dataset.addSeries(series);
		}
	}

	/**
	 * Generates the quantile series for the specified key.
	 * 
	 * @param key the key identifying which result to plot
	 * @param dataset the dataset to store the generated series
	 */
	protected void generateQuantileSeries(ResultKey key, 
			YIntervalSeriesCollection dataset) {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		
		for (Accumulator accumulator : controller.get(key)) {
			if (!accumulator.keySet().contains(metric)) {
				continue;
			}
			
			for (int i=0; i<accumulator.size(metric); i++) {
				dataPoints.add(new DataPoint(
						(Integer)accumulator.get("NFE", i), 
						((Number)accumulator.get(metric, i)).doubleValue()));
			}
		}
			
		Collections.sort(dataPoints);

		YIntervalSeries series = new YIntervalSeries(key);
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		int index = 0;
		int currentNFE = RESOLUTION;

		while (index < dataPoints.size()) {
			DataPoint point = dataPoints.get(index);

			if (point.getNFE() <= currentNFE) {
				statistics.addValue(point.getValue());
				index++;
			} else {
				if (statistics.getN() > 0) {
					series.add(currentNFE, 
							statistics.getPercentile(50), 
							statistics.getPercentile(25), 
							statistics.getPercentile(75));
				}

				statistics.clear();
				currentNFE += RESOLUTION;
			}
		}

		if (statistics.getN() > 0) {
			//if only entry, add extra point to display non-zero width
			if (series.isEmpty()) {
				series.add(currentNFE-RESOLUTION, 
						statistics.getPercentile(50), 
						statistics.getPercentile(25), 
						statistics.getPercentile(75));
			}

			series.add(currentNFE, 
					statistics.getPercentile(50), 
					statistics.getPercentile(25), 
					statistics.getPercentile(75));
		}
		
		dataset.addSeries(series);
	}
	
	@Override
	protected void update() {
		XYDataset dataset = null;
		
		//generate the plot data
		if (controller.getShowIndividualTraces()) {
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
		if (controller.getShowIndividualTraces()) {
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, 
					false);
			
			for (int i=0; i<dataset.getSeriesCount(); i++) {
				Paint paint = frame.getPaintHelper().get(
						dataset.getSeriesKey(i));
	
				renderer.setSeriesStroke(i, new BasicStroke(1f, 1, 1));
				renderer.setSeriesPaint(i, paint);
			}
			
			plot.setRenderer(renderer);
		} else {
			DeviationRenderer renderer = new DeviationRenderer(true, false);
	
			for (int i=0; i<dataset.getSeriesCount(); i++) {
				Paint paint = frame.getPaintHelper().get(
						dataset.getSeriesKey(i));
	
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
		
		LegendItemSource source = new LegendItemSource() {

			@Override
			public LegendItemCollection getLegendItems() {
				return items;
			}
			
		};
		
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
		if (controller.getShowLastTrace() && 
				!controller.getShowIndividualTraces() &&
				(controller.getLastAccumulator() != null) && 
				controller.getLastAccumulator().keySet().contains(metric)) {
			DefaultTableXYDataset dataset2 = new DefaultTableXYDataset();
			XYSeries series = new XYSeries(
					localization.getString("text.last"),
					false, false);
			
			for (int i=0; i<controller.getLastAccumulator().size(metric); i++) {
				series.add(
						(Number)controller.getLastAccumulator().get("NFE", i), 
						(Number)controller.getLastAccumulator().get(metric, i));
			}
			
			dataset2.addSeries(series);
			
			XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, 
					false);
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
