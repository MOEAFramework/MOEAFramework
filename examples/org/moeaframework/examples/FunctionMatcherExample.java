/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.examples;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.cli.CommandLine;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.grammar.GrammarCrossover;
import org.moeaframework.core.operator.grammar.GrammarMutation;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.problem.FunctionMatcher;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.Timing;

/**
 * Example of grammatical evolution using the {@link FunctionMatcher} problem.
 */
public class FunctionMatcherExample extends CommandLineUtility {

	/**
	 * The problem being solved.
	 */
	private FunctionMatcher problem;

	/**
	 * The frame displaying the plot.
	 */
	private JFrame frame;

	/**
	 * The container for the plot.
	 */
	private JPanel container;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private FunctionMatcherExample() {
		super();
	}

	/**
	 * Updates the {@link JFrame} displaying the target and estimated functions.
	 * 
	 * @param estimatedFunction the estimated function
	 * @throws ScriptException if an error occurred evaluating the target or
	 *         estimated functions
	 */
	private void update(String estimatedFunction) throws ScriptException {
		if (frame == null) {
			frame = new JFrame("Function Matcher");
			frame.getContentPane().setLayout(new BorderLayout());
			frame.setSize(600, 400);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			frame.setVisible(true);

			container = new JPanel(new BorderLayout());
			frame.getContentPane().add(container);
		}

		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		XYSeries targetSeries = new XYSeries("Target Function", false, false);
		XYSeries estimatedSeries = new XYSeries("Estimated Function", false,
				false);

		ScriptEngine engine = problem.getEngine();
		String targetFunction = problem.getTargetFunction();
		double lowerBound = problem.getLowerBound();
		double upperBound = problem.getUpperBound();
		int numberOfSamples = problem.getNumberOfSamples();

		for (double i = lowerBound; i <= upperBound; i += (upperBound - lowerBound)
				/ numberOfSamples) {
			Bindings b = new SimpleBindings();
			b.put("x", i);

			double v1 = ((Number)engine.eval(targetFunction, b)).doubleValue();
			double v2 = ((Number)engine.eval(estimatedFunction, b))
					.doubleValue();

			targetSeries.add(i, v1);
			estimatedSeries.add(i, v2);
		}

		dataset.addSeries(targetSeries);
		dataset.addSeries(estimatedSeries);

		JFreeChart chart = ChartFactory.createXYLineChart("Function Matcher",
				"x", "f(x)", dataset, PlotOrientation.VERTICAL, true, true,
				false);
		final XYPlot plot = chart.getXYPlot();

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);

		container.removeAll();
		container.add(new ChartPanel(chart), BorderLayout.CENTER);
		container.revalidate();
		container.repaint();
	}

	@Override
	public void run(CommandLine commandLine) throws IOException,
			ScriptException {
		problem = new FunctionMatcher("Math.sin(Math.sqrt(x))", 0.0, 10.0, 100);

		Variation crossover = new GrammarCrossover(0.75);
		Variation mutation = new GrammarMutation(0.15);
		Selection selection = new TournamentSelection();
		Initialization initialization = new RandomInitialization(problem, 100);

		Algorithm algorithm = new NSGAII(problem,
				new NondominatedSortingPopulation(), null, selection,
				new CompoundVariation(crossover, mutation), initialization);

		while (!algorithm.isTerminated() && (algorithm.getNumberOfEvaluations() < 10000)) {
			algorithm.step();

			Solution best = algorithm.getResult().get(0);
			String estimatedFunction = problem.getGrammar().build(
					((Grammar)best.getVariable(0)).toArray());

			System.out
					.println(best.getObjective(0) + " - " + estimatedFunction);
			update(estimatedFunction);

			if (best.getObjective(0) == 0) {
				System.out.println("Match found!");
				break;
			}
		}

		Timing.printStatistics(System.out);
	}

	/**
	 * Starts an example of grammatical evolution using the 
	 * {@link FunctionMatcher} problem.
	 * 
	 * @param args the command line arguments
	 * @throws IOException if an I/O error occurred
	 * @throws ScriptException if an error occurred evaluating the target or
	 *         estimated functions
	 */
	public static void main(String[] args) throws IOException, ScriptException {
		new FunctionMatcherExample().start(args);
	}

}
