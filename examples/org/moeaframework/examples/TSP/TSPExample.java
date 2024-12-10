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
package org.moeaframework.examples.TSP;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.mvc.ExampleUI;

/**
 * Demonstration of optimizing a TSP problem using the MOEA Framework.  A window will appear showing the progress of
 * the optimization algorithm.  The window will contain a visual representation of the TSP problem instance, with a
 * thick red line indicating the best tour found by the optimization algorithm.  Light gray lines are the other
 * (sub-optimal) tours in the population.
 */
public class TSPExample extends ExampleUI<GeneticAlgorithm> {

	private static final long serialVersionUID = -3279247471680695959L;

	private static final Color LIGHT_GRAY = new Color(128, 128, 128, 64);
	
	private final TSPInstance instance;
	
	private TSPPanel panel;
	
	private JTextArea details;
		
	public TSPExample(TSPInstance instance, GeneticAlgorithm algorithm) {
		super("Traveling Salesman Problem - " + instance.getName(), algorithm);
		this.instance = instance;
		
		initialize();
		layoutComponents();
		
		setSize(500, 400);
		setLocationRelativeTo(null);
	}
	
	private void initialize() {
		panel = new TSPPanel(instance);
		panel.setAutoRepaint(false);
		
		details = new JTextArea();
		details.setEditable(false);
	}
	
	private void layoutComponents() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(panel);
		splitPane.setBottomComponent(new JScrollPane(details));
		splitPane.setDividerLocation(300);
		splitPane.setResizeWeight(1.0);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}
	
	@Override
	public void update(GeneticAlgorithm algorithm, int iteration) {
		panel.clearTours();

		// display population with light gray lines
		for (Solution solution : algorithm.getPopulation()) {
			panel.displayTour(TSPProblem.toTour(solution), LIGHT_GRAY);
		}
		
		// display current optimal solutions with red line
		Tour best = TSPProblem.toTour(algorithm.getResult().get(0));
		panel.displayTour(best, Color.RED, new BasicStroke(2.0f));
		panel.repaint();
		
		// update the details
		details.setText("Iteration: " + iteration + System.lineSeparator() +
				"Best tour length: " + best.distance(instance));
	}
	
	/**
	 * Solves this TSPLIB instance while displaying a GUI showing the optimization progress.
	 * 
	 * @param instance the TSPLIB instance to solve
	 */
	public static void solve(TSPInstance instance) {
		// create the optimization problem and evolutionary algorithm
		Problem problem = new TSPProblem(instance);
		
		TypedProperties properties = new TypedProperties();
		properties.setDouble("swap.rate", 0.7);
		properties.setDouble("insertion.rate", 0.9);
		properties.setDouble("pmx.rate", 0.4);
		
		GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
		algorithm.applyConfiguration(properties);
		
		TSPExample example = new TSPExample(instance, algorithm);
		example.start();
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param file the file containing the TSPLIB instance
	 * @throws IOException if an I/O error occurred
	 */
	public static void solve(File file) throws IOException {
		solve(new TSPInstance(file));
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param reader the reader containing the TSPLIB instance
	 * @throws IOException if an I/O error occurred
	 */
	public static void solve(Reader reader) throws IOException {
		solve(new TSPInstance(reader));
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param stream the stream containing the TSPLIB instance
	 * @throws IOException if an I/O error occurred
	 */
	public static void solve(InputStream stream) throws IOException {
		solve(new TSPInstance(new InputStreamReader(stream)));
	}

}
