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

import org.moeaframework.analysis.plot.XYPlotBuilder;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.Localization;

/**
 * Displays the end-of-run Pareto approximation set.
 */
public class ApproximationSetPlot extends ResultPlot {

	private static final long serialVersionUID = -6915212513300959375L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static final Localization LOCALIZATION = Localization.getLocalization(ApproximationSetPlot.class);
	
	/**
	 * The &epsilon; value used when displaying the approximation set.
	 */
	private static final double EPSILON = 0.01;
	
	/**
	 * Constructs a new plot do display the Pareto approximation set.
	 * 
	 * @param frame the {@code DiagnosticTool} instance containing this plot
	 */
	public ApproximationSetPlot(DiagnosticTool frame) {
		super(frame, "Approximation Set");
		
		setLayout(new BorderLayout());
	}
	
	@Override
	protected void update() {
		XYPlotBuilder builder = new XYPlotBuilder();

		// generate the plot data
		for (ResultKey key : frame.getSelectedResults()) {
			NondominatedPopulation population = new EpsilonBoxDominanceArchive(EPSILON);
			
			for (ResultSeries series : controller.get(key)) {
				population.addAll(series.last().getPopulation());
			}
			
			if (!population.isEmpty()) {
				builder.scatter(key.toString(), population).withPaint(frame.getPaintHelper().get(key));
			}
		}
		
		// add last trace overlay
		if (controller.showLastTrace().get() && (controller.getLastSeries() != null)) {
			NondominatedPopulation population = new EpsilonBoxDominanceArchive(EPSILON);
			population.addAll(controller.getLastSeries().last().getPopulation());
			
			if (!population.isEmpty()) {
				builder.scatter(LOCALIZATION.getString("text.last"), population).withPaint(Color.BLACK);
			}
		}
		
		builder.setTitle(metric);
		builder.setXLabel(LOCALIZATION.getString("text.objective", 1));
		builder.setYLabel(LOCALIZATION.getString("text.objective", 2));

		removeAll();
		add(builder.buildPanel(), BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
