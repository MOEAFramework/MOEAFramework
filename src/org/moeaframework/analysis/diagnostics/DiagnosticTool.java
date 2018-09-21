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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.Settings;
import org.moeaframework.util.Localization;

/**
 * The main window of the diagnostic tool.
 */
public class DiagnosticTool extends JFrame implements ListSelectionListener, 
ControllerListener {

	private static final long serialVersionUID = -8770087330810075627L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			DiagnosticTool.class);

	/**
	 * The controller which stores the underlying data model and notifies this
	 * diagnostic tool of any changes.
	 */
	private Controller controller;

	/**
	 * The list of all available metrics.
	 */
	private JList metricList;
	
	/**
	 * The underlying data model storing all available results.
	 */
	private SortedListModel<ResultKey> resultListModel;
	
	/**
	 * The underlying data model storing all available metrics.
	 */
	private SortedListModel<String> metricListModel;
	
	/**
	 * The container of all plots.
	 */
	private JPanel chartContainer;
	
	/**
	 * The table for displaying all available results.
	 */
	private JTable resultTable;
	
	/**
	 * The table model that allows {@code resultListModel} to be displayed in a
	 * table.
	 */
	private AbstractTableModel resultTableModel;
	
	/**
	 * The button for selecting all results.
	 */
	private JButton selectAll;
	
	/**
	 * The button for displaying a statistical comparison of selected results.
	 */
	private JButton showStatistics;
	
	/**
	 * The control for setting the algorithm used by evaluation jobs.
	 */
	private JComboBox algorithm;
	
	/**
	 * The control for setting the problem used by evaluation jobs.
	 */
	private JComboBox problem;
	
	/**
	 * The control for setting the number of seeds used by evaluation jobs.
	 */
	private JSpinner numberOfSeeds;
	
	/**
	 * The control for setting the number of evaluations used by evaluation
	 * jobs.
	 */
	private JSpinner numberOfEvaluations;
	
	/**
	 * The button for starting a new evaluation job.
	 */
	private JButton run;
	
	/**
	 * The button for canceling the current evaluation job.
	 */
	private JButton cancel;
	
	/**
	 * The button for clearing all results contained in this diagnostic tool.
	 */
	private JButton clear;
	
	/**
	 * The progress bar displaying the individual run progress.
	 */
	private JProgressBar runProgress;
	
	/**
	 * The progress bar displaying the overall progress.
	 */
	private JProgressBar overallProgress;
	
	/**
	 * The factory for the actions supported in this diagnostic tool window.
	 */
	private ActionFactory actionFactory;

	/**
	 * Maintains a mapping from series key to paints displayed in the plot.
	 */
	private PaintHelper paintHelper;

	/**
	 * Constructs a new diagnostic tool window.
	 */
	public DiagnosticTool() {
		super(localization.getString("title.diagnosticTool"));

		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		initialize();
		layoutMenu();
		layoutComponents();
	}
	
	/**
	 * Initializes this window.  This method is invoked by the constructor, and
	 * should not be invoked again.
	 */
	protected void initialize() {
		controller = new Controller(this);
		controller.addControllerListener(this);
		
		actionFactory = new ActionFactory(this, controller);
		resultListModel = new SortedListModel<ResultKey>();
		metricListModel = new SortedListModel<String>();
		metricList = new JList(metricListModel);
		paintHelper = new PaintHelper();
		chartContainer = new JPanel();
		
		metricList.addListSelectionListener(this);
		
		//initialize the table containing all available results
		resultTableModel = new AbstractTableModel() {

			private static final long serialVersionUID = -4148463449906184742L;

			@Override
			public String getColumnName(int column) {
				switch (column) {
				case 0: 
					return localization.getString("text.algorithm");
				case 1: 
					return localization.getString("text.problem");
				case 2: 
					return localization.getString("text.numberOfSeeds");
				default: 
					throw new IllegalStateException();
				}
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public int getRowCount() {
				return resultListModel.getSize();
			}

			@Override
			public Object getValueAt(int row, int column) {
				ResultKey key = resultListModel.getElementAt(row);
				
				switch (column) {
				case 0: 
					return key.getAlgorithm();
				case 1: 
					return key.getProblem();
				case 2: 
					return controller.get(key).size();
				default: 
					throw new IllegalStateException();
				}
			}
			
		};
		
		resultTable = new JTable(resultTableModel);
		
		resultTable.getSelectionModel().addListSelectionListener(this);

		resultTable.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = resultTable.rowAtPoint(e.getPoint());
					boolean containsSet = false;
					
					if (index == -1) {
						return;
					}
					
					ResultKey key = resultListModel.getElementAt(index);
					
					//verify that at least one accumulator contains data
					for (Accumulator accumulator : controller.get(key)) {
						if (accumulator.keySet().contains(
								"Approximation Set")) {
							containsSet = true;
						}
					}
					
					if (!containsSet) {
						return;
					}
					
					JPopupMenu popupMenu = new JPopupMenu();
					
					popupMenu.add(new JMenuItem(
							actionFactory.getShowApproximationSetAction(
									resultListModel.getElementAt(index))));
					
					popupMenu.show(resultTable, e.getX(), e.getY());
				}
			}
			
		});
		
		selectAll = new JButton(actionFactory.getSelectAllAction(resultTable));
		showStatistics = new JButton(actionFactory.getShowStatisticsAction());
		
		//initialize the sorted list of algorithms
		Set<String> algorithmNames = new HashSet<String>();
		
		for (String algorithm : Settings.getDiagnosticToolAlgorithms()) {
			algorithmNames.add(algorithm);
		}
		
		for (String algorithm : Settings.getPISAAlgorithms()) {
			algorithmNames.add(algorithm);
		}
		
		List<String> sortedAlgorithmNames = new ArrayList<String>(
				algorithmNames);
		Collections.sort(sortedAlgorithmNames);
		
		algorithm = new JComboBox(sortedAlgorithmNames.toArray());
		
		//initialize the sorted list of problems
		Set<String> problemNames = new HashSet<String>();
		
		for (String problem : Settings.getDiagnosticToolProblems()) {
			problemNames.add(problem);
		}
		
		for (String problem : Settings.getProblems()) {
			problemNames.add(problem);
		}
		
		List<String> sortedProblemNames = new ArrayList<String>(problemNames);
		Collections.sort(sortedProblemNames);
		
		problem = new JComboBox(sortedProblemNames.toArray());
		
		//initialize miscellaneous components
		numberOfSeeds = new JSpinner(new SpinnerNumberModel(10, 1, 
				Integer.MAX_VALUE, 10));
		numberOfEvaluations = new JSpinner(new SpinnerNumberModel(10000, 500, 
				Integer.MAX_VALUE, 1000));
		run = new JButton(actionFactory.getRunAction());
		cancel = new JButton(actionFactory.getCancelAction());
		clear = new JButton(actionFactory.getClearAction());
		
		runProgress = new JProgressBar();
		overallProgress = new JProgressBar();
		
		algorithm.setEditable(true);
		problem.setEditable(true);
	}
	
	/**
	 * Lays out the menu on this window.  This method is invoked by the
	 * constructor, and should not be invoked again.
	 */
	protected void layoutMenu() {
		JMenu file = new JMenu(localization.getString("menu.file"));
		file.add(new JMenuItem(actionFactory.getSaveAction()));
		file.add(new JMenuItem(actionFactory.getLoadAction()));
		file.addSeparator();
		file.add(new JMenuItem(actionFactory.getExitAction()));
		
		JMenu view = new JMenu(localization.getString("menu.view"));
		JMenuItem individualTraces = new JRadioButtonMenuItem(
				actionFactory.getShowIndividualTracesAction());
		JMenuItem quantiles = new JRadioButtonMenuItem(
				actionFactory.getShowQuantilesAction());
		ButtonGroup traceGroup = new ButtonGroup();
		traceGroup.add(individualTraces);
		traceGroup.add(quantiles);
		view.add(individualTraces);
		view.add(quantiles);
		view.addSeparator();
		view.add(new JCheckBoxMenuItem(
				actionFactory.getShowLastTraceAction()));
		
		JMenu metrics = new JMenu(localization.getString("menu.collect"));
		metrics.add(new JMenuItem(
				actionFactory.getEnableAllIndicatorsAction()));
		metrics.add(new JMenuItem(
				actionFactory.getDisableAllIndicatorsAction()));
		metrics.addSeparator();
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeHypervolumeAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeGenerationalDistanceAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeInvertedGenerationalDistanceAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeSpacingAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeAdditiveEpsilonIndicatorAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeContributionAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeR1Action()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeR2Action()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeR3Action()));
		metrics.addSeparator();
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeEpsilonProgressAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeAdaptiveMultimethodVariationAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeAdaptiveTimeContinuationAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeElapsedTimeAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludePopulationSizeAction()));
		metrics.add(new JCheckBoxMenuItem(
				actionFactory.getIncludeApproximationSetAction()));
		
		JMenu help = new JMenu(localization.getString("menu.help"));
		help.add(new JMenuItem(actionFactory.getAboutDialogAction()));
		
		JMenu usage = new JMenu(actionFactory.getMemoryUsageAction());
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(view);
		menuBar.add(metrics);
		menuBar.add(help);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(usage);
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Lays out the components on this window.  This method is invoked by the
	 * constructor, and should not be invoked again.
	 */
	protected void layoutComponents() {
		GridBagConstraints label = new GridBagConstraints();
		label.gridx = 0;
		label.gridy = GridBagConstraints.RELATIVE;
		label.anchor = GridBagConstraints.EAST;
		label.insets = new Insets(0, 5, 5, 25);
		
		GridBagConstraints field = new GridBagConstraints();
		field.gridx = 1;
		field.gridy = GridBagConstraints.RELATIVE;
		field.fill = GridBagConstraints.HORIZONTAL;
		field.weightx = 1.0;
		field.insets = new Insets(0, 0, 5, 5);
		
		GridBagConstraints button = new GridBagConstraints();
		button.gridx = 0;
		button.gridwidth = 2;
		button.fill = GridBagConstraints.HORIZONTAL;
		button.insets = new Insets(0, 0, 5, 0);
		
		JPanel analysisPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		analysisPane.add(selectAll);
		analysisPane.add(showStatistics);
		
		JPanel resultPane = new JPanel(new BorderLayout());
		resultPane.setBorder(BorderFactory.createTitledBorder(
				localization.getString("text.displayedResults")));
		resultPane.add(new JScrollPane(resultTable), BorderLayout.CENTER);
		resultPane.add(analysisPane, BorderLayout.SOUTH);
		resultPane.setMinimumSize(new Dimension(100, 100));
		
		JPanel metricPane = new JPanel(new BorderLayout());
		metricPane.setBorder(BorderFactory.createTitledBorder(
				localization.getString("text.displayedMetrics")));
		metricPane.add(new JScrollPane(metricList), BorderLayout.CENTER);
		metricPane.setMinimumSize(new Dimension(100, 100));
		
		JPanel selectionPane = new JPanel(new GridLayout(2, 1));
		selectionPane.add(resultPane);
		selectionPane.add(metricPane);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPane.add(run);
		buttonPane.add(cancel);
		buttonPane.add(clear);
		
		JPanel controlPane = new JPanel(new GridBagLayout());
		controlPane.setBorder(BorderFactory.createTitledBorder(
				localization.getString("text.controls")));
		controlPane.add(new JLabel(
				localization.getString("text.algorithm") + ":"), label);
		controlPane.add(algorithm, field);
		controlPane.add(new JLabel(
				localization.getString("text.problem") + ":"), label);
		controlPane.add(problem, field);
		controlPane.add(new JLabel(
				localization.getString("text.numberOfSeeds") + ":"), label);
		controlPane.add(numberOfSeeds, field);
		controlPane.add(new JLabel(
				localization.getString("text.numberOfEvaluations") + ":"),
				label);
		controlPane.add(numberOfEvaluations, field);
		controlPane.add(buttonPane, button);
		controlPane.add(new JPanel(), button);
		controlPane.add(new JLabel(
				localization.getString("text.runProgress") + ":"), label);
		controlPane.add(runProgress, field);
		controlPane.add(new JLabel(
				localization.getString("text.overallProgress") + ":"), label);
		controlPane.add(overallProgress, field);
		
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(controlPane);
		controls.add(selectionPane);
		controls.setMinimumSize(controlPane.getPreferredSize());
		controls.setPreferredSize(controlPane.getPreferredSize());
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				controls, chartContainer);
		splitPane.setDividerLocation(-1);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}
	
	/**
	 * Updates the models underlying the GUI components as a result of model
	 * changes.  This method must only be invoked on the event dispatch thread.
	 */
	protected void updateModel() {
		//determine selection mode
		List<ResultKey> selectedResults = getSelectedResults();
		List<String> selectedMetrics = getSelectedMetrics();
		boolean selectAllResults = false;
		boolean selectFirstMetric = false;
		
		if (selectedResults.size() == resultListModel.getSize()) {
			selectAllResults = true;
		}
		
		if ((selectedMetrics.size() == 0) && (metricListModel.getSize() == 0)) {
			selectFirstMetric = true;
		}
		
		//update metric list and result table contents
		resultListModel.addAll(controller.getKeys());
		
		for (ResultKey key : controller.getKeys()) {
			for (Accumulator accumulator : controller.get(key)) {
				metricListModel.addAll(accumulator.keySet());
			}
		}
		
		//update metric list selection
		metricList.getSelectionModel().removeListSelectionListener(this);
		metricList.clearSelection();
		
		if (selectFirstMetric) {
			metricList.setSelectedIndex(0);
		} else {
			for (String metric : selectedMetrics) {
				int index = metricListModel.getIndexOf(metric);
				metricList.getSelectionModel().addSelectionInterval(index, 
						index);
			}
		}
		
		metricList.getSelectionModel().addListSelectionListener(this);
		
		//update result table selection
		resultTable.getSelectionModel().removeListSelectionListener(this);
		resultTableModel.fireTableDataChanged();
		
		if (selectAllResults && (selectedResults.size() < 
				resultListModel.getSize())) {
			resultTable.getSelectionModel().addSelectionInterval(0, 
					resultListModel.getSize()-1);
		} else {
			for (ResultKey key : selectedResults) {
				int index = resultListModel.getIndexOf(key);
				resultTable.getSelectionModel().addSelectionInterval(index, 
						index);
			}
		}

		resultTable.getSelectionModel().addListSelectionListener(this);
	}
	
	/**
	 * Returns the controller used by this diagnostic tool instance.  This
	 * controller provides access to the underlying data model displayed in
	 * this window.
	 * 
	 * @return the controller used by this diagnostic tool instance
	 */
	public Controller getController() {
		return controller;
	}
	
	/**
	 * Returns the paint helper used by this diagnostic tool instance.  This
	 * paint helper contains the mapping from series to paints displayed in this
	 * window.
	 * 
	 * @return the paint helper used by this diagnostic tool instance
	 */
	public PaintHelper getPaintHelper() {
		return paintHelper;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		
		controller.fireViewChangedEvent();
	}
	
	/**
	 * Invoked when the underlying data model is cleared, resulting in the GUI
	 * removing and resetting all components.  This method must only be invoked
	 * on the event dispatch thread.
	 */
	protected void clear() {
		resultListModel.clear();
		resultTable.getSelectionModel().clearSelection();
		resultTableModel.fireTableDataChanged();
		metricListModel.clear();
		metricList.getSelectionModel().clearSelection();
		paintHelper.clear();
		
		chartContainer.removeAll();
		chartContainer.revalidate();
		chartContainer.repaint();
	}
	
	/**
	 * Updates the chart layout when the user changes which metrics to plot.
	 * This method must only be invoked on the event dispatch thread.
	 */
	protected void updateChartLayout() {
		chartContainer.removeAll();
		
		List<String> selectedMetrics = getSelectedMetrics();
		
		if (selectedMetrics.size() > 0) {
			if (selectedMetrics.size() <= 1) {
				chartContainer.setLayout(new GridLayout(1, 1));
			} else if (selectedMetrics.size() <= 2) {
				chartContainer.setLayout(new GridLayout(2, 1));
			} else if (selectedMetrics.size() <= 4) {
				chartContainer.setLayout(new GridLayout(2, 2));
			} else if (selectedMetrics.size() <= 6) {
				chartContainer.setLayout(new GridLayout(3, 2));
			} else {
				chartContainer.setLayout(new GridLayout(
						(int)Math.ceil(selectedMetrics.size()/3.0), 3));
			}
			
			GridLayout layout = (GridLayout)chartContainer.getLayout();
			int spaces = layout.getRows()*layout.getColumns();
			
			for (int i=0; i<Math.max(spaces, selectedMetrics.size()); i++) {
				if (i < selectedMetrics.size()) {
					chartContainer.add(createChart(selectedMetrics.get(i)));
				} else {
					chartContainer.add(new EmptyPlot(this));
				}
			}
		}
		
		chartContainer.revalidate();
	}

	/**
	 * Returns a list of the selected metrics.
	 * 
	 * @return a list of the selected metrics
	 */
	protected List<String> getSelectedMetrics() {
		List<String> selectedMetrics = new ArrayList<String>();
		
		for (int index : metricList.getSelectedIndices()) {
			selectedMetrics.add(metricListModel.getElementAt(index));
		}
		
		return selectedMetrics;
	}
	
	/**
	 * Returns a list of the selected results.
	 * 
	 * @return a list of the selected results
	 */
	protected List<ResultKey> getSelectedResults() {
		List<ResultKey> selectedResults = new ArrayList<ResultKey>();
		
		for (int index : resultTable.getSelectedRows()) {
			selectedResults.add(resultListModel.getElementAt(index));
		}
		
		return selectedResults;
	}
	
	/**
	 * Returns the algorithm selected in the run control pane.
	 * 
	 * @return the algorithm selected for the next evaluation job
	 */
	protected String getAlgorithm() {
		return (String)algorithm.getSelectedItem();
	}
	
	/**
	 * Returns the problem selected in the run control pane.
	 * 
	 * @return the problem selected in the run control pane
	 */
	protected String getProblem() {
		return (String)problem.getSelectedItem();
	}
	
	/**
	 * Returns the number of evaluations set in the run control pane.
	 * 
	 * @return the number of evaluations set in the run control pane
	 */
	protected int getNumberOfEvaluations() {
		return (Integer)numberOfEvaluations.getValue();
	}
	
	/**
	 * Returns the number of seeds set in the run control pane.
	 * 
	 * @return the number of seeds set in the run control pane
	 */
	protected int getNumberOfSeeds() {
		return (Integer)numberOfSeeds.getValue();
	}
	
	/**
	 * Creates and returns the GUI component for plotting the specified metric.
	 * 
	 * @param metric the metric to plot
	 * @return the GUI component for plotting the specified metric
	 */
	protected ResultPlot createChart(String metric) {
		if (metric.equals("Approximation Set")) {
			return new ApproximationSetPlot(this, metric);
		} else {
			return new LinePlot(this, metric);
		}
	}

	@Override
	public void controllerStateChanged(ControllerEvent event) {
		if (event.getType().equals(ControllerEvent.Type.MODEL_CHANGED)) {
			if (controller.getKeys().isEmpty()) {
				clear();
			} else {
				updateModel();
			}
		} else if (event.getType().equals(
				ControllerEvent.Type.PROGRESS_CHANGED)) {
			runProgress.setValue(controller.getRunProgress());
			overallProgress.setValue(controller.getOverallProgress());
		} else if (event.getType().equals(ControllerEvent.Type.VIEW_CHANGED)) {
			updateChartLayout();
		}
	}

	@Override
	public void dispose() {
		controller.cancel();
		super.dispose();
	}

}
