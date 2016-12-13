/* Copyright 2009-2016 David Hadka
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
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;

/**
 * A GUI for displaying the actual and approximated functions used in a
 * symbolic regression problem instance.
 */
public class SymbolicRegressionGUI extends JFrame implements WindowListener {

	private static final long serialVersionUID = -2137650953653684495L;

	/**
	 * The symbolic regression problem instance.
	 */
	private final SymbolicRegression problem;

	/**
	 * The container for the actual and approximated function plot.
	 */
	private JPanel container;
	
	/**
	 * The text area containing details of the result, displaying a generation
	 * counter, the objective value, and the approximated function expression
	 * tree.
	 */
	private JTextArea details;
	
	/**
	 * The button used to close the GUI.
	 */
	private JButton close;
	
	/**
	 * The current solution.
	 */
	private Solution solution;
	
	/**
	 * The current generation count.
	 */
	private int generation;
	
	/**
	 * The maximum generations being executed.
	 */
	private int maxGenerations;
	
	/**
	 * {@code true} if the GUI has been closed; {@code false} otherwise.
	 */
	private boolean isCanceled;
	
	/**
	 * Constructs a new GUI for displaying the actual and approximated
	 * functions used in a symbolic regression problem instance.
	 * 
	 * @param problem the symbolic regression problem instance
	 */
	public SymbolicRegressionGUI(SymbolicRegression problem) {
		super("Symbolic Regression Demo");
		this.problem = problem;
		
		initialize();
		layoutComponents();
		
		setSize(600, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		setIconImages(Settings.getIconImages());
	}
	
	/**
	 * Returns {@code true} if the GUI has been closed; {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if the GUI has been closed; {@code false}
	 *         otherwise
	 */
	public boolean isCanceled() {
		return isCanceled;
	}

	/**
	 * Initialize the components on the GUI.
	 */
	protected void initialize() {
		container = new JPanel(new BorderLayout());
		
		details = new JTextArea();
		details.setWrapStyleWord(true);
		details.setEditable(false);
		details.setLineWrap(true);
		
		close = new JButton(new AbstractAction("Close") {

			private static final long serialVersionUID = 2365513341407994400L;

			@Override
			public void actionPerformed(ActionEvent e) {
				isCanceled = true;
				dispose();
			}
			
		});
	}
	
	/**
	 * Layout the components on the GUI.
	 */
	protected void layoutComponents() {
		container.setMinimumSize(new Dimension(300, 300));
		
		JScrollPane detailsPane = new JScrollPane(details);
		detailsPane.setMinimumSize(new Dimension(150, 150));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				container, detailsPane);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(0.4);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPane.add(close);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}
	
	/**
	 * Updates the GUI.  This method updates a Swing GUI, and therefore must
	 * only be invoked on the event dispatch thread.
	 */
	protected void updateOnEventQueue() {
		// create local reference incase update is called again
		double[] x = problem.getX();
		double[] actualY = problem.getActualY();
		double[] approximatedY = problem.getApproximatedY(solution);

		// generate the line series
		XYSeries actualSeries = new XYSeries("Target Function", false, false);
		XYSeries approximatedSeries = new XYSeries("Estimated Function", false,
				false);

		for (int i = 0; i < x.length; i++) {
			actualSeries.add(x[i], actualY[i]);
			approximatedSeries.add(x[i], approximatedY[i]);
		}

		DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		dataset.addSeries(actualSeries);
		dataset.addSeries(approximatedSeries);

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
				solution.getVariable(0));
		
		container.removeAll();
		container.add(new ChartPanel(chart), BorderLayout.CENTER);
		container.revalidate();
		container.repaint();
		
		if (!isCanceled) {
			setVisible(true);
		}
	}
	
	/**
	 * Updates the GUI with a new intermediate solution.
	 * 
	 * @param solution the new solution
	 * @param generation the current generation count
	 * @param maxGenerations the maximum generations being run
	 */
	public void update(Solution solution, int generation, int maxGenerations) {
		this.solution = solution;
		this.generation = generation;
		this.maxGenerations = maxGenerations;
		
		if (EventQueue.isDispatchThread()) {
			updateOnEventQueue();
		} else {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					updateOnEventQueue();
				}
				
			});
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
	
	/**
	 * Runs the given symbolic regression problem instance, displaying
	 * intermediate results in a GUI.
	 * 
	 * @param problem the symbolic regression problem instance
	 */
	public static void runDemo(SymbolicRegression problem) {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// couldn't set system look and feel, continue with default
		}
		
		// setup the GUI
		SymbolicRegressionGUI gui = new SymbolicRegressionGUI(problem);

		// setup and construct the GP solver
		int generation = 0;
		int maxGenerations = 1000;
		Algorithm algorithm = null;
		Properties properties = new Properties();
		properties.setProperty("populationSize", "500");

		try {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					"GA", properties, problem);

			// run the GP solver
			while ((generation < maxGenerations) && !gui.isCanceled()) {
				algorithm.step();
				generation++;

				gui.update(algorithm.getResult().get(0), generation,
						maxGenerations);
			}
		} finally {
			if (algorithm != null) {
				algorithm.terminate();
			}
		}
	}

}
