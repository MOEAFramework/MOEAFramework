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
package org.moeaframework.examples.regression;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.analysis.plot.XYPlotBuilder;
import org.moeaframework.core.Solution;
import org.moeaframework.util.mvc.ExampleUI;
import org.moeaframework.util.mvc.UI;

/**
 * A GUI for displaying the actual and approximated functions used in a symbolic regression problem instance.
 */
public class SymbolicRegressionGUI extends ExampleUI<GeneticAlgorithm> {

	private static final long serialVersionUID = -2137650953653684495L;

	/**
	 * The symbolic regression problem instance.
	 */
	private final AbstractSymbolicRegression problem;

	/**
	 * The container for the actual and approximated function plot.
	 */
	private JPanel container;

	/**
	 * The text area containing details of the result, displaying a generation counter, the objective value, and the
	 * approximated function expression tree.
	 */
	private JTextArea details;

	/**
	 * Constructs a new GUI for displaying the actual and approximated functions used in a symbolic regression problem
	 * instance.
	 * 
	 * @param problem the symbolic regression problem instance
	 * @param algorithm the algorithm used to solve the problem
	 */
	public SymbolicRegressionGUI(AbstractSymbolicRegression problem, GeneticAlgorithm algorithm) {
		super("Symbolic Regression Demo", algorithm);
		this.problem = problem;

		initialize();
		layoutComponents();

		Dimension size = new Dimension(600, 600);
		setMinimumSize(size);
		setSize(size);
	}

	private void initialize() {
		container = new JPanel(new BorderLayout());

		details = new JTextArea();
		details.setWrapStyleWord(true);
		details.setEditable(false);
		details.setLineWrap(true);
	}

	private void layoutComponents() {
		container.setMinimumSize(new Dimension(300, 300));

		JScrollPane detailsPane = new JScrollPane(details);
		detailsPane.setMinimumSize(new Dimension(150, 150));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, container, detailsPane);
		splitPane.setResizeWeight(0.7);
		splitPane.setDividerLocation(0.7);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	@Override
	public void update(GeneticAlgorithm algorithm, int iteration) {
		double[] x = problem.getX();
		double[] y = problem.getY();

		Solution solution = algorithm.getResult().get(0);
		double[] approximatedY = problem.eval(solution);

		// generate the plot
		XYPlotBuilder builder = new XYPlotBuilder()
			.line("Target Function", x, y)
			.line("Estimated Function", x, approximatedY)
			.xLabel("x")
			.yLabel("f(x)")
			.title("Symbolic Regression Demo");

		// update the details
		details.setText("Iteration: " + iteration + System.lineSeparator() +
				"Objective value: " + solution.getObjectiveValue(0) + System.lineSeparator() + System.lineSeparator() +
				problem.getExpression(solution));

		container.removeAll();
		container.add(builder.buildPanel(), BorderLayout.CENTER);
		container.revalidate();
		container.repaint();
	}
	
	/**
	 * Runs the given symbolic regression problem instance, displaying intermediate results in a GUI.
	 * 
	 * @param problem the symbolic regression problem instance
	 */
	public static void runDemo(AbstractSymbolicRegression problem) {
		UI.showAndWait(() -> {
			GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
			algorithm.setInitialPopulationSize(500);
			
			return new SymbolicRegressionGUI(problem, algorithm);
		}).start();
	}

}
