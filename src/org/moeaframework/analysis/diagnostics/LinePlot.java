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
package org.moeaframework.analysis.diagnostics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.moeaframework.analysis.plot.SeriesPaint;
import org.moeaframework.analysis.plot.XYPlotBuilder;
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
	private static final Localization LOCALIZATION = Localization.getLocalization(LinePlot.class);
	
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
	
	@Override
	protected void update() {
		XYPlotBuilder builder = new XYPlotBuilder();
		builder.paintHelper(frame.getPaintHelper());
		
		// generate the plot data
		if (controller.showIndividualTraces().get()) {
			for (ResultKey key : frame.getSelectedResults()) {
				for (ResultSeries series : controller.get(key)) {
					if (!series.getDefinedProperties().contains(metric)) {
						continue;
					}
				
					builder.line(key.toString(), series, metric);
				}
			}
		} else {
			for (ResultKey key : frame.getSelectedResults()) {				
				builder.deviation(key.toString(), controller.get(key), metric);
			}
		}
		
		// add last trace overlay
		if (controller.showLastTrace().get() &&
				(controller.getLastSeries() != null) &&
				controller.getLastSeries().getDefinedProperties().contains(metric)) {
			builder.line(LOCALIZATION.getString("text.last"), controller.getLastSeries(), metric, SeriesPaint.black());
		}
		
		builder.title(metric);
		builder.xLabel(LOCALIZATION.getString("text.NFE"));
		builder.yLabel(LOCALIZATION.getString("text.value"));
		builder.legend(false);

		JFreeChart chart = builder.build();
		
		// custom legend to eliminate duplicates
		LegendItemCollection items = chart.getPlot().getLegendItems();
		Iterator<?> iterator = items.iterator();
		Set<Comparable<?>> uniqueKeys = new HashSet<>();
		
		while (iterator.hasNext()) {
			LegendItem item = (LegendItem)iterator.next();
			
			if (uniqueKeys.contains(item.getSeriesKey())) {
				iterator.remove();
			} else {
				uniqueKeys.add(item.getSeriesKey());
			}
		}
		
		LegendItemSource source = () -> items;
		
		LegendTitle legend = new LegendTitle(source);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setFrame(new LineBorder());
		legend.setBackgroundPaint(Color.WHITE);
		legend.setPosition(RectangleEdge.BOTTOM);
		chart.addLegend(legend);
		
		//update the chart in the GUI
		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
