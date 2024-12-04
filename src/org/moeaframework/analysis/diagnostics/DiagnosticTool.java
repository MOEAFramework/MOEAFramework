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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FilenameUtils;
import org.jfree.base.Library;
import org.jfree.ui.about.AboutDialog;
import org.jfree.ui.about.ProjectInfo;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.viewer.RuntimeViewer;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Localization;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;
import org.moeaframework.util.mvc.ControllerEvent;
import org.moeaframework.util.mvc.ControllerListener;
import org.moeaframework.util.mvc.InvertedToggleAction;
import org.moeaframework.util.mvc.RunnableAction;
import org.moeaframework.util.mvc.ToggleAction;

/**
 * The main window of the diagnostic tool.
 */
public class DiagnosticTool extends JFrame implements ListSelectionListener, ControllerListener {

	private static final long serialVersionUID = -8770087330810075627L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(DiagnosticTool.class);
	
	/**
	 * The file extension.
	 */
	private static String EXTENSION = localization.getString("file.extension");

	/**
	 * The file filter used when selecting the file to save/load.
	 */
	private static FileFilter FILTER = new FileNameExtensionFilter(
			localization.getString("file.extension.description"),
			localization.getString("file.extension"));

	/**
	 * The controller which stores the underlying data model and notifies this diagnostic tool of any changes.
	 */
	private DiagnosticToolController controller;

	/**
	 * The list of all available metrics.
	 */
	private JList<String> metricList;
	
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
	 * The table model that allows {@code resultListModel} to be displayed in a table.
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
	private JComboBox<String> algorithm;
	
	/**
	 * The control for setting the problem used by evaluation jobs.
	 */
	private JComboBox<String> problem;
	
	/**
	 * The control for setting the number of seeds used by evaluation jobs.
	 */
	private JSpinner numberOfSeeds;
	
	/**
	 * The control for setting the number of evaluations used by evaluation jobs.
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
		setIconImages(Settings.getIcon().getResolutionVariants());
		
		initialize();
		layoutMenu();
		layoutComponents();
	}
	
	/**
	 * Initializes this window.  This method is invoked by the constructor, and should not be invoked again.
	 */
	private void initialize() {
		controller = new DiagnosticToolController(this);
		controller.addControllerListener(this);
		
		resultListModel = new SortedListModel<>();
		metricListModel = new SortedListModel<>();
		metricList = new JList<>(metricListModel);
		paintHelper = new PaintHelper();
		chartContainer = new JPanel();
		
		metricList.addListSelectionListener(this);
		
		//initialize the table containing all available results
		resultTableModel = new AbstractTableModel() {

			private static final long serialVersionUID = -4148463449906184742L;

			@Override
			public String getColumnName(int column) {
				return switch (column) {
					case 0 -> localization.getString("text.algorithm");
					case 1 -> localization.getString("text.problem");
					case 2 -> localization.getString("text.numberOfSeeds");
					default -> throw new IllegalStateException();
				};
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
				
				return switch (column) {
					case 0 -> key.getAlgorithm();
					case 1 -> key.getProblem();
					case 2 -> controller.get(key).size();
					default -> throw new IllegalStateException();
				};
			}
			
		};
		
		resultTable = new JTable(resultTableModel);
		resultTable.getSelectionModel().addListSelectionListener(this);
		resultTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = resultTable.rowAtPoint(e.getPoint());
					
					if (index == -1) {
						return;
					}
					
					ResultKey key = resultListModel.getElementAt(index);
					
					JPopupMenu popupMenu = new JPopupMenu();
					popupMenu.add(new RunnableAction("showApproximationSet", localization, () -> {
						RuntimeViewer viewer = new RuntimeViewer(DiagnosticTool.this, key.toString());
						List<ResultSeries> data = controller.get(key);
							
						for (int i = 0; i < data.size(); i++) {
							String name = localization.getString("text.seed", i + 1);
							viewer.getController().addSeries(name, data.get(i));
						}
							
						viewer.getController().setReferenceSet(
								ProblemFactory.getInstance().getReferenceSet(key.getProblem()));
							
						viewer.setLocationRelativeTo(DiagnosticTool.this);
						viewer.setVisible(true);
					}).toMenuItem());
					
					popupMenu.show(resultTable, e.getX(), e.getY());
				}
			}
			
		});
		
		selectAll = new RunnableAction("selectAll", localization, this::selectAllResults).toButton();
		showStatistics = new RunnableAction("showStatistics", localization, controller::showStatistics).toButton();
		
		//initialize the sorted list of algorithms
		Vector<String> sortedAlgorithmNames = new Vector<>(AlgorithmFactory.getInstance().getAllDiagnosticToolAlgorithms());
		Collections.sort(sortedAlgorithmNames, String.CASE_INSENSITIVE_ORDER);
		
		algorithm = new JComboBox<>(sortedAlgorithmNames);
		
		//initialize the sorted list of problems
		Vector<String> sortedProblemNames = new Vector<>(ProblemFactory.getInstance().getAllDiagnosticToolProblems());
		Collections.sort(sortedProblemNames, String.CASE_INSENSITIVE_ORDER);
		
		problem = new JComboBox<>(sortedProblemNames);
		
		//initialize miscellaneous components
		numberOfSeeds = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 10));
		numberOfEvaluations = new JSpinner(new SpinnerNumberModel(10000, 500, Integer.MAX_VALUE, 1000));
		run = new RunnableAction("run", localization, controller::run).toButton();
		cancel = new RunnableAction("cancel", localization, controller::cancel).toButton();
		clear = new RunnableAction("clear", localization, controller::clear).toButton();
		
		runProgress = new JProgressBar();
		overallProgress = new JProgressBar();
		
		algorithm.setEditable(true);
		problem.setEditable(true);
		
		controller.fireEvent("modelChanged");
		controller.fireEvent("viewChanged");
	}
	
	/**
	 * Lays out the menu on this window.  This method is invoked by the constructor, and should not be invoked again.
	 */
	private void layoutMenu() {
		JMenu fileMenu = new JMenu(localization.getString("menu.file"));
		fileMenu.add(new RunnableAction("save", localization, () -> {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(FILTER);
	
				int result = fileChooser.showSaveDialog(this);
	
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
	
					if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase(EXTENSION)) {
						file = new File(file.getParent(), file.getName() + "." + EXTENSION);
					}
	
					try {
						controller.saveData(file);
					} catch (IOException e) {
						controller.handleException(e);
					}
				}
			}).toMenuItem());
		fileMenu.add(new RunnableAction("load", localization, () -> {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(FILTER);
	
				int result = fileChooser.showOpenDialog(this);
	
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadData(fileChooser.getSelectedFile());
					} catch (IOException e) {
						controller.handleException(e);
					}
				}
			}).toMenuItem());
		fileMenu.addSeparator();
		fileMenu.add(new RunnableAction("exit", localization, this::dispose).toMenuItem());
		
		JMenu viewMenu = new JMenu(localization.getString("menu.view"));
		viewMenu.add(new JRadioButtonMenuItem(new ToggleAction("showIndividualTraces", localization, controller.showIndividualTraces())));
		viewMenu.add(new JRadioButtonMenuItem(new InvertedToggleAction("showQuantiles", localization, controller.showIndividualTraces())));
		viewMenu.addSeparator();
		viewMenu.add(new ToggleAction("showLastTrace", localization, controller.showLastTrace()).toMenuItem());
		
		JMenu metricsMenu = new JMenu(localization.getString("menu.collect"));
		metricsMenu.add(new RunnableAction("enableAllIndicators", localization, () -> {
				controller.includeHypervolume().set(true);
				controller.includeGenerationalDistance().set(true);
				controller.includeGenerationalDistancePlus().set(true);
				controller.includeInvertedGenerationalDistance().set(true);
				controller.includeInvertedGenerationalDistancePlus().set(true);
				controller.includeSpacing().set(true);
				controller.includeAdditiveEpsilonIndicator().set(true);
				controller.includeContribution().set(true);
				controller.includeR1().set(true);
				controller.includeR2().set(true);
				controller.includeR3().set(true);
			}).toMenuItem());
		metricsMenu.add(new RunnableAction("disableAllIndicators", localization, () -> {
				controller.includeHypervolume().set(false);
				controller.includeGenerationalDistance().set(false);
				controller.includeGenerationalDistancePlus().set(false);
				controller.includeInvertedGenerationalDistance().set(false);
				controller.includeInvertedGenerationalDistancePlus().set(false);
				controller.includeSpacing().set(false);
				controller.includeAdditiveEpsilonIndicator().set(false);
				controller.includeContribution().set(false);
				controller.includeR1().set(false);
				controller.includeR2().set(false);
				controller.includeR3().set(false);
			}).toMenuItem());
		metricsMenu.addSeparator();
		metricsMenu.add(new ToggleAction("includeHypervolume", localization, controller.includeHypervolume()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeGenerationalDistance", localization, controller.includeGenerationalDistance()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeInvertedGenerationalDistance", localization, controller.includeInvertedGenerationalDistance()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeSpacing", localization, controller.includeSpacing()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeAdditiveEpsilonIndicator", localization, controller.includeAdditiveEpsilonIndicator()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeContribution", localization, controller.includeContribution()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeR1", localization, controller.includeR1()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeR2", localization, controller.includeR2()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeR3", localization, controller.includeR3()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeGenerationalDistancePlus", localization, controller.includeGenerationalDistancePlus()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeInvertedGenerationalDistancePlus", localization, controller.includeInvertedGenerationalDistancePlus()).toMenuItem());
		metricsMenu.addSeparator();
		metricsMenu.add(new ToggleAction("includeEpsilonProgress", localization, controller.includeEpsilonProgress()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeAdaptiveMultimethodVariation", localization, controller.includeAdaptiveMultimethodVariation()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeAdaptiveTimeContinuation", localization, controller.includeAdaptiveTimeContinuation()).toMenuItem());
		metricsMenu.add(new ToggleAction("includeElapsedTime", localization, controller.includeElapsedTime()).toMenuItem());
		metricsMenu.add(new ToggleAction("includePopulationSize", localization, controller.includePopulationSize()).toMenuItem());
		
		JMenu helpMenu = new JMenu(localization.getString("menu.help"));
		helpMenu.add(new RunnableAction("about", localization, this::showAbout).toMenuItem());
		
		JMenu usageMenu = new JMenu();
		usageMenu.setEnabled(false);
		
		Timer timer = new Timer(1000, e -> {
			final double divisor = 1024*1024;
			long free = Runtime.getRuntime().freeMemory();
			long total = Runtime.getRuntime().totalMemory();
			long max = Runtime.getRuntime().maxMemory();
			double used = (total - free) / divisor;
			double available = max / divisor;
			
			usageMenu.setText(localization.getString("text.memory", used, available));
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();
		controller.addShutdownHook(() -> timer.stop());
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(metricsMenu);
		menuBar.add(helpMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(usageMenu);
				
		setJMenuBar(menuBar);
	}
	
	/**
	 * Lays out the components on this window.  This method is invoked by the constructor, and should not be invoked
	 * again.
	 */
	private void layoutComponents() {
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
		resultPane.setBorder(BorderFactory.createTitledBorder(localization.getString("text.displayedResults")));
		resultPane.add(new JScrollPane(resultTable), BorderLayout.CENTER);
		resultPane.add(analysisPane, BorderLayout.SOUTH);
		resultPane.setMinimumSize(new Dimension(100, 100));
		
		JPanel metricPane = new JPanel(new BorderLayout());
		metricPane.setBorder(BorderFactory.createTitledBorder(localization.getString("text.displayedMetrics")));
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
		controlPane.setBorder(BorderFactory.createTitledBorder(localization.getString("text.controls")));
		controlPane.add(new JLabel(localization.getString("text.algorithm") + ":"), label);
		controlPane.add(algorithm, field);
		controlPane.add(new JLabel(localization.getString("text.problem") + ":"), label);
		controlPane.add(problem, field);
		controlPane.add(new JLabel(localization.getString("text.numberOfSeeds") + ":"), label);
		controlPane.add(numberOfSeeds, field);
		controlPane.add(new JLabel(localization.getString("text.numberOfEvaluations") + ":"), label);
		controlPane.add(numberOfEvaluations, field);
		controlPane.add(buttonPane, button);
		controlPane.add(new JPanel(), button);
		controlPane.add(new JLabel(localization.getString("text.runProgress") + ":"), label);
		controlPane.add(runProgress, field);
		controlPane.add(new JLabel(localization.getString("text.overallProgress") + ":"), label);
		controlPane.add(overallProgress, field);
		
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(controlPane);
		controls.add(selectionPane);
		controls.setMinimumSize(controlPane.getPreferredSize());
		controls.setPreferredSize(controlPane.getPreferredSize());
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controls, chartContainer);
		splitPane.setDividerLocation(-1);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}
	
	/**
	 * Updates the models underlying the GUI components as a result of model changes.  This method must only be invoked
	 * on the event dispatch thread.
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
			for (ResultSeries series : controller.get(key)) {
				metricListModel.add("Approximation Set");
				metricListModel.addAll(series.getDefinedProperties());
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
				metricList.getSelectionModel().addSelectionInterval(index, index);
			}
		}
		
		metricList.getSelectionModel().addListSelectionListener(this);
		
		//update result table selection
		resultTable.getSelectionModel().removeListSelectionListener(this);
		resultTableModel.fireTableDataChanged();
		
		if (selectAllResults && (selectedResults.size() < resultListModel.getSize())) {
			resultTable.getSelectionModel().addSelectionInterval(0, resultListModel.getSize()-1);
		} else {
			for (ResultKey key : selectedResults) {
				int index = resultListModel.getIndexOf(key);
				resultTable.getSelectionModel().addSelectionInterval(index, index);
			}
		}

		resultTable.getSelectionModel().addListSelectionListener(this);
	}
	
	/**
	 * Returns the controller used by this diagnostic tool instance.  This controller provides access to the underlying
	 * data model displayed in this window.
	 * 
	 * @return the controller used by this diagnostic tool instance
	 */
	public DiagnosticToolController getController() {
		return controller;
	}
	
	/**
	 * Returns the paint helper used by this diagnostic tool instance.  This paint helper contains the mapping from
	 * series to paints displayed in this window.
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
		
		controller.fireEvent("viewChanged");
	}
	
	/**
	 * Invoked when the underlying data model is cleared, resulting in the GUI removing and resetting all components.
	 * This method must only be invoked on the event dispatch thread.
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
	 * Updates the chart layout when the user changes which metrics to plot.  This method must only be invoked on the
	 * event dispatch thread.
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
				chartContainer.setLayout(new GridLayout((int)Math.ceil(selectedMetrics.size()/3.0), 3));
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
		List<String> selectedMetrics = new ArrayList<>();
		
		for (int index : metricList.getSelectedIndices()) {
			selectedMetrics.add(metricListModel.getElementAt(index));
		}
		
		return selectedMetrics;
	}
	
	/**
	 * Selects all available metrics for display.
	 */
	public void selectAllMetrics() {
		metricList.getSelectionModel().setSelectionInterval(0, metricList.getModel().getSize()-1);
	}
	
	/**
	 * Returns a list of the selected results.
	 * 
	 * @return a list of the selected results
	 */
	protected List<ResultKey> getSelectedResults() {
		List<ResultKey> selectedResults = new ArrayList<>();
		
		for (int index : resultTable.getSelectedRows()) {
			selectedResults.add(resultListModel.getElementAt(index));
		}
		
		return selectedResults;
	}
	
	/**
	 * Selects all available results for display.
	 */
	public void selectAllResults() {
		resultTable.getSelectionModel().setSelectionInterval(0, resultTable.getModel().getRowCount()-1);
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
	 * Sets the algorithm selected in the run control pane.
	 * 
	 * @param algorithm the algorithm selected in the run control pane
	 */
	protected void setAlgorithm(String algorithm) {
		this.algorithm.setSelectedItem(algorithm);
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
	 * Sets the problem selected in the run control pane.
	 * 
	 * @param problem the problem selected in the run control pane
	 */
	protected void setProblem(String problem) {
		this.problem.setSelectedItem(problem);
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
	 * Sets the number of evaluations in the run control pane.
	 * 
	 * @param numberOfEvaluations the number of function evaluations
	 */
	protected void setNumberOfEvaluations(int numberOfEvaluations) {
		this.numberOfEvaluations.setValue(numberOfEvaluations);
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
	 * Sets the number of seeds in the run control pane.
	 * 
	 * @param numberOfSeeds the number of seeds
	 */
	protected void setNumberOfSeeds(int numberOfSeeds) {
		this.numberOfSeeds.setValue(numberOfSeeds);
	}
	
	/**
	 * Creates and returns the GUI component for plotting the specified metric.
	 * 
	 * @param metric the metric to plot
	 * @return the GUI component for plotting the specified metric
	 */
	protected ResultPlot createChart(String metric) {
		if (metric.equals("Approximation Set")) {
			return new ApproximationSetPlot(this);
		} else {
			return new LinePlot(this, metric);
		}
	}
	
	/**
	 * Creates and displays a dialog containing about / license information.
	 * 
	 * @return the dialog, or {@code null} if unable to display
	 */
	protected AboutDialog showAbout() {
		try {
			TypedProperties properties = TypedProperties.loadBuildProperties();

			ProjectInfo info = new ProjectInfo(
					properties.getString("name"),
					properties.getString("version"),
					properties.getString("description"),
					null,
					properties.getString("copyright"),
					null,
					loadLicense());
			
			for (String dependency : properties.getStringArray("runtime.dependencies", new String[0])) {
				info.addLibrary(new Library(
						dependency,
						properties.getString(dependency + ".version", "???"),
						properties.getString(dependency + ".license", "???"),
						null));
			}
			
			info.addLibrary(new Library(
					"Flaticon",
					"-",
					"www.flaticon.com/terms-of-use",
					null));
			
			AboutDialog dialog = new AboutDialog(this, localization.getString("title.about"), info);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
			return dialog;
		} catch (Exception ex) {
			controller.handleException(ex);
			return null;
		}
	}
	
	/**
	 * Loads the GNU LGPL license file and formats it for display.
	 * 
	 * @return the formatted GNU LGPL license
	 * @throws IOException if an I/O error occurred
	 */
	private String loadLicense() throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean isNewParagraph = false;
		
		try (LineReader lineReader = Resources.asLineReader(getClass(), "/META-INF/LGPL-LICENSE",
				ResourceOption.REQUIRED).skipComments().trim()) {
			for (String line : lineReader) {
				if (line.isEmpty()) {
					isNewParagraph = true;
				} else {
					if (isNewParagraph) {
						sb.append(System.lineSeparator());
						sb.append(System.lineSeparator());
					} else {
						sb.append(' ');
					}
					
					sb.append(line);
					isNewParagraph = false;
				}
			}
			
			return sb.toString();
		}
	}

	@Override
	public void controllerStateChanged(ControllerEvent event) {
		if (event.getEventType().equals("modelChanged")) {
			if (controller.getKeys().isEmpty()) {
				clear();
			} else {
				updateModel();
			}
		}
		
		if (event.getEventType().equals("progressChanged")) {
			runProgress.setValue(controller.getRunProgress());
			overallProgress.setValue(controller.getOverallProgress());
		}
		
		if (event.getEventType().equals("viewChanged")) {
			updateChartLayout();
		}
		
		if (event.getEventType().equals("stateChanged")) {
			run.setEnabled(!controller.isRunning());
			cancel.setEnabled(controller.isRunning());
			clear.setEnabled(!controller.isRunning());
		}
		
		if (event.getEventType().equals("viewChanged") || event.getEventType().equals("modelChanged")) {
			Set<String> problems = new HashSet<>();
			Set<String> algorithms = new HashSet<>();
			
			for (ResultKey key : getSelectedResults()) {
				problems.add(key.getProblem());
				algorithms.add(key.getAlgorithm());
			}
								
			showStatistics.setEnabled((problems.size() == 1) && (algorithms.size() >= 1));
		}
	}

}
