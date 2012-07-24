/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.examples.gp.regression;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Rules;

public abstract class SymbolicRegression extends AbstractProblem implements
WindowListener {
	
	private final Rules rules;
	
	private final String symbol;
	
	private final double lowerBound;
	
	private final double upperBound;
	
	private final int steps;
	
	/**
	 * The frame displaying the plot.
	 */
	private JFrame frame;

	/**
	 * The container for the plot.
	 */
	private JPanel container;
	
	private JTextArea details;
	
	private int generation;
	
	private int maxGenerations;
	
	private boolean isCanceled;

	public SymbolicRegression(Rules rules, String symbol, double lowerBound,
			double upperBound, int steps) {
		super(1, 1);
		this.rules = rules;
		this.symbol = symbol;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.steps = steps;
		
		if (!Number.class.isAssignableFrom(rules.getReturnType())) {
			throw new IllegalArgumentException("return type must be Number");
		}
		
		initialize();
	}
	
	protected void initialize() {
		frame = new JFrame("Symbolic Regression Demo");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.addWindowListener(SymbolicRegression.this);

		container = new JPanel(new BorderLayout());
		container.setMinimumSize(new Dimension(300, 300));
		
		details = new JTextArea();
		details.setWrapStyleWord(true);
		details.setEditable(false);
		details.setLineWrap(true);
		
		JScrollPane detailsPane = new JScrollPane(details);
		detailsPane.setMinimumSize(new Dimension(150, 150));
		
		JSplitPane splitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, container, detailsPane);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(0.4);
		
		JButton close = new JButton(new AbstractAction("Close") {

			private static final long serialVersionUID = 2365513341407994400L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
			
		});
		
		JPanel buttonPane = new JPanel(new FlowLayout(
				FlowLayout.CENTER));
		buttonPane.add(close);
		
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}
	
	public abstract double evaluate(double x);

	@Override
	public void evaluate(Solution solution) {
		Program program = (Program)solution.getVariable(0);
		double difference = 0.0;
		
		for (int i = 0; i < steps; i++) {
			double x = lowerBound + (i / (steps - 1.0)) * 
					(upperBound - lowerBound);
			
			// evaluate the actual function
			double f1 = evaluate(x);
			
			// evaluate the symbolic regression approximation
			Environment environment = new Environment();
			environment.set(symbol, x);
			double f2 = ((Number)program.evaluate(environment)).doubleValue();
			
			// calculate the difference
			difference += Math.pow(Math.abs(f1 - f2), 2.0);
		}
		
		difference = Math.sqrt(difference);
		
		// protect against NaN
		if (Double.isNaN(difference)) {
			difference = Double.POSITIVE_INFINITY;
		}

		solution.setObjective(0, difference);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Program(rules));
		return solution;
	}
	
	public void update(final Solution solution) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				if (!frame.isVisible() && !isCanceled) {
					frame.setVisible(true);
				}
		
				// generate the line series
				Program program = (Program)solution.getVariable(0);
				DefaultTableXYDataset dataset = new DefaultTableXYDataset();
				XYSeries targetSeries = new XYSeries("Target Function", false,
						false);
				XYSeries estimatedSeries = new XYSeries("Estimated Function",
						false, false);
				
				for (int i = 0; i < steps; i++) {
					double x = lowerBound + (i / (steps - 1.0)) *
							(upperBound - lowerBound);
					
					// evaluate the actual function
					double f1 = evaluate(x);
					
					// evaluate the symbolic regression approximation
					Environment environment = new Environment();
					environment.set(symbol, x);
					double f2 = ((Number)program.evaluate(environment))
							.doubleValue();
					
					// add to the series
					targetSeries.add(x, f1);
					estimatedSeries.add(x, f2);
				}
		
				dataset.addSeries(targetSeries);
				dataset.addSeries(estimatedSeries);
		
				// generate the plot
				JFreeChart chart = ChartFactory.createXYLineChart(
						"Symbolic Regression Demo", "x", "f(x)", dataset,
						PlotOrientation.VERTICAL, true, true, false);
				XYPlot plot = chart.getXYPlot();
				plot.setRenderer(new XYLineAndShapeRenderer());
				
				// update the details
				details.setText("Generation " + generation + " / " +
						maxGenerations + "\nObjective value: " +
						solution.getObjective(0) + "\n\n" +
						program.toString());
				
				container.removeAll();
				container.add(new ChartPanel(chart), BorderLayout.CENTER);
				container.revalidate();
				container.repaint();
			}
			
		});
	}
	
	public void runDemo(int maxGenerations) {
		this.maxGenerations = maxGenerations;
		
		Properties properties = new Properties();
		properties.setProperty("populationSize", "500");
		
		Algorithm algorithm = null;
		
		try {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					"NSGAII", properties, this);
			
			while ((generation < maxGenerations) && !isCanceled) {
				algorithm.step();
				generation++;
				
				update(algorithm.getResult().get(0));
			}
		} finally {
			if (algorithm != null) {
				algorithm.terminate();
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// do nothing
	}

	@Override
	public void windowClosed(WindowEvent e) {
		isCanceled = true;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// do nothing
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// do nothing
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// do nothing
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// do nothing
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// do nothing
	}

}
