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
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.util.Localization;

/**
 * Displays the end-of-run Pareto approximation set.
 */
public class ApproximationSetPlot extends ResultPlot {

	private static final long serialVersionUID = -6915212513300959375L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			ApproximationSetPlot.class);
	
	/**
	 * The &epsilon; value used when displaying the approximation set.
	 */
	private static final double EPSILON = 0.01;
	
	/**
	 * Constructs a new plot do display the Pareto approximation set.
	 * 
	 * @param frame the {@code DiagnosticTool} instance containing this plot
	 * @param metric the metric to display
	 */
	public ApproximationSetPlot(DiagnosticTool frame, String metric) {
		super(frame, metric);
		
		setLayout(new BorderLayout());
	}
	
	@Override
	protected void update() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for (ResultKey key : frame.getSelectedResults()) {
			NondominatedPopulation population = new EpsilonBoxDominanceArchive(
					EPSILON);
			
			for (Accumulator accumulator : controller.get(key)) {
				if (!accumulator.keySet().contains(metric)) {
					continue;
				}
				
				List<?> list = (List<?>)accumulator.get(metric, 
						accumulator.size(metric)-1);
				
				for (Object object : list) {
					population.add((Solution)object);
				}
			}
			
			if (!population.isEmpty()) {
				XYSeries series = new XYSeries(key, false, true);
				
				for (Solution solution : population) {
					if (solution.getNumberOfObjectives() == 1) {
						series.add(solution.getObjective(0), 
								solution.getObjective(0));
					} else if (solution.getNumberOfObjectives() > 1) {
						series.add(solution.getObjective(0), 
								solution.getObjective(1));
					}
				}
				
				dataset.addSeries(series);
			}
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(metric,
				localization.getString("text.objective", 1),
				localization.getString("text.objective", 2),
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, 
				true);
		
		for (int i=0; i<dataset.getSeriesCount(); i++) {
			Paint paint = frame.getPaintHelper().get(dataset.getSeriesKey(i));

			renderer.setSeriesStroke(i, new BasicStroke(3f, 1, 1));
			renderer.setSeriesPaint(i, paint);
			renderer.setSeriesFillPaint(i, paint);
		}
		
		plot.setRenderer(renderer);
		
		//add overlay
		if (controller.getShowLastTrace() &&
				(controller.getLastAccumulator() != null) && 
				controller.getLastAccumulator().keySet().contains(metric)) {
			XYSeriesCollection dataset2 = new XYSeriesCollection();
			NondominatedPopulation population = new EpsilonBoxDominanceArchive(
					EPSILON);
			
			if (controller.getLastAccumulator().keySet().contains(metric)) {
				List<?> list = (List<?>)controller.getLastAccumulator().get(
						metric, controller.getLastAccumulator().size(metric)-1);
				
				for (Object object : list) {
					population.add((Solution)object);
				}
			}
			
			if (!population.isEmpty()) {
				XYSeries series = new XYSeries(
						localization.getString("text.last"),
						false,
						true);
				
				for (Solution solution : population) {
					series.add(solution.getObjective(0), 
							solution.getObjective(1));
				}
				
				dataset2.addSeries(series);
			}
			
			XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(false,
					true);
			renderer2.setSeriesPaint(0, Color.BLACK);
			
			plot.setDataset(1, dataset2);
			plot.setRenderer(1, renderer2);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		}

		removeAll();
		add(new ChartPanel(chart), BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
