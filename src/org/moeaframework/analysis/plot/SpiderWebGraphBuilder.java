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
package org.moeaframework.analysis.plot;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.plot.style.PlotAttribute;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.validate.Validate;

/**
 * Constructs a spider web graph.
 */
public class SpiderWebGraphBuilder extends PlotBuilder<SpiderWebGraphBuilder> {

	private final SpiderWebPlot plot;
		
	private final DefaultCategoryDataset dataset;
	
	/**
	 * Constructs a new, empty bar graph.
	 */
	public SpiderWebGraphBuilder() {
		super();

		dataset = new DefaultCategoryDataset();

		plot = new SpiderWebPlot();
		plot.setDataset(dataset);
	}
	
	@Override
	public SpiderWebGraphBuilder getInstance() {
		return this;
	}
	
	@Override
	public JFreeChart build() {
		return build(plot);
	}
	
	/**
	 * Adds a new series to the spider web plot.
	 * 
	 * @param label the label for the series
	 * @param categories the categories
	 * @param values the values for each category
	 * @param attributes the attributes configuring the plot
	 * @return a reference to this builder
	 */
	public SpiderWebGraphBuilder add(String label, List<? extends Comparable<?>> categories, List<? extends Number> values, PlotAttribute... attributes) {
		Validate.that("categories.size()", categories.size()).isEqualTo("values.size()", categories.size());
		
		for (int i = 0; i < categories.size(); i++) {
			dataset.addValue(values.get(i), label, categories.get(i));
		}
		
		int seriesIndex = dataset.getRowCount() - 1;
		Paint paint = paintHelper.get(label);
		
		plot.setSeriesPaint(seriesIndex, paint);

		applyStyle(plot, 0, seriesIndex, attributes);
		
		if (!plot.getSeriesPaint(seriesIndex).equals(paint)) {
			paintHelper.set(label, plot.getSeriesPaint(seriesIndex));
		}
		
		plot.setWebFilled(false);
		
		noLegend();
				
		return getInstance();
	}
	
	public SpiderWebGraphBuilder add(String label, Population population, PlotAttribute... attributes) {
		for (Solution solution : population) {
			List<String> categories = new ArrayList<>();
			List<Double> values = new ArrayList<>();
			
			for (int i = 0; i < solution.getNumberOfVariables(); i++) {
				categories.add("Variable " + (i + 1));
				values.add(RealVariable.getReal(solution.getVariable(i)));
			}
			
			add(label + " " + solution.toString(), categories, values, attributes);
		}
		
		return getInstance();
	}

	public static void main(String[] args) {
		Problem problem = new UF1();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		new SpiderWebGraphBuilder()
				.add("NSGAII", algorithm.getResult())
				.title("Pareto Front")
				.show();
	}
	
}
