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
package org.moeaframework.analysis.diagnostics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartColor;
import org.jfree.chart.PaintMap;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.Settings;

/**
 * The main window of the diagnostic tool.
 */
public class DiagnosticTool extends JFrame implements ListSelectionListener, ControllerListener {

	private static final long serialVersionUID = -8770087330810075627L;

	private final Controller controller;

	private JList metricList;
	
	private SortedListModel<ResultKey> resultListModel;
	
	private SortedListModel<String> metricListModel;
	
	private JPanel chartContainer;
	
	private PaintMap paintMap;
	
	private int nextPaintIndex;
	
	private JTable resultTable;
	
	private AbstractTableModel resultTableModel;
	
	private JButton selectAll;
	
	private JButton showStatistics;
	
	public static final String[] algorithms = new String[] { "NSGAII", "GDE3", 
		"eMOEA", "Borg", "eNSGAII", "MOEAD", "Random" };
	
	public static final String[] problems = new String[] { 
		"DTLZ1_2", "DTLZ2_2", "DTLZ3_2", "DTLZ4_2", "DTLZ7_2", 
		"ROT_DTLZ1_2", "ROT_DTLZ2_2", "ROT_DTLZ3_2", "ROT_DTLZ4_2", "ROT_DTLZ7_2", 
		"UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12", "UF13",
		"CF1", "CF2", "CF3", "CF4", "CF5", "CF6", "CF7", "CF8", "CF9", "CF10",
		"LZ1", "LZ2", "LZ3", "LZ4", "LZ5", "LZ6", "LZ7", "LZ8", "LZ9",
		"WFG1_2", "WFG2_2", "WFG3_2", "WFG4_2", "WFG5_2", "WFG6_2", "WFG7_2", "WFG8_2", "WFG9_2",
		"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT5", "ZDT6" };
	
	private JComboBox algorithm;
	
	private JComboBox problem;
	
	private JSpinner numberOfSeeds;
	
	private JSpinner numberOfEvaluations;
	
	private JButton run;
	
	private JButton cancel;
	
	private JButton clear;
	
	private JProgressBar runProgress;
	
	private JProgressBar seedProgress;
	
	private final ActionFactory actionFactory;
	
	private static final Paint[] COLORS = ChartColor.createDefaultPaintArray();
	
	public DiagnosticTool() {
		super("MOEA Diagnostic Tool");
		
		controller = new Controller(this);
		controller.addControllerListener(this);
		
		actionFactory = new ActionFactory(this, controller);

		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		initialize();
		setupMenu();
		layoutComponents();
	}
	
	protected void setupMenu() {
		JMenu file = new JMenu("File");
		file.add(new JMenuItem(actionFactory.getSaveAction()));
		file.add(new JMenuItem(actionFactory.getLoadAction()));
		file.addSeparator();
		file.add(new JMenuItem(actionFactory.getExitAction()));
		
		JMenu metrics = new JMenu("Collect");
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeHypervolumeAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeGenerationalDistanceAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeInvertedGenerationalDistanceAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeSpacingAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeAdditiveEpsilonIndicatorAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeContributionAction()));
		metrics.addSeparator();
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeEpsilonProgressAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeAdaptiveMultimethodVariationAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeAdaptiveTimeContinuationAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeElapsedTimeAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludePopulationSizeAction()));
		metrics.add(new JCheckBoxMenuItem(actionFactory.getIncludeApproximationSetAction()));
		
		JMenu help = new JMenu("Help");
		help.add(new JMenuItem(actionFactory.getAboutDialogAction()));
		
		JMenu usage = new JMenu(actionFactory.getMemoryUsageAction());
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(metrics);
		menuBar.add(help);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(usage);
		
		setJMenuBar(menuBar);
	}
	
	protected void initialize() {
		resultListModel = new SortedListModel<ResultKey>();
		metricListModel = new SortedListModel<String>();
		metricList = new JList(metricListModel);
		paintMap = new PaintMap();
		chartContainer = new JPanel();
		
		metricList.addListSelectionListener(this);
		
		resultTableModel = new AbstractTableModel() {

			private static final long serialVersionUID = -4148463449906184742L;

			@Override
			public String getColumnName(int column) {
				switch (column) {
				case 0: 
					return "Algorithm";
				case 1: 
					return "Problem";
				case 2: 
					return "Seeds";
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
				ResultKey key = (ResultKey)resultListModel.getElementAt(row);
				
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
					final int index = resultTable.rowAtPoint(e.getPoint());
					
					if (index == -1) {
						return;
					}
					
					JPopupMenu popupMenu = new JPopupMenu();
					
					popupMenu.add(new JMenuItem(
							actionFactory.getShowApproximationSetAction(
									resultListModel.getElementAt(index))));
					
					Point point = resultTable.getPopupLocation(e);
					popupMenu.show(resultTable, point.x, point.y);
				}
			}
			
		});
		
		selectAll = new JButton(actionFactory.getSelectAllAction(resultTable));
		showStatistics = new JButton(actionFactory.getShowStatisticsAction());
		
		Set<String> algorithmNames = new HashSet<String>();
		
		for (String algorithm : algorithms) {
			algorithmNames.add(algorithm);
		}
		
		for (String algorithm : Settings.getPISAAlgorithms()) {
			algorithmNames.add(algorithm);
		}
		
		List<String> sortedAlgorithmNames = new ArrayList<String>(algorithmNames);
		Collections.sort(sortedAlgorithmNames);
		
		algorithm = new JComboBox(sortedAlgorithmNames.toArray());
		problem = new JComboBox(problems);
		numberOfSeeds = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 10));
		numberOfEvaluations = new JSpinner(new SpinnerNumberModel(10000, 500, Integer.MAX_VALUE, 1000));
		run = new JButton(actionFactory.getRunAction());
		cancel = new JButton(actionFactory.getCancelAction());
		clear = new JButton(actionFactory.getClearAction());
		
		runProgress = new JProgressBar();
		seedProgress = new JProgressBar();
		
		algorithm.setEditable(true);
		problem.setEditable(true);
	}
	
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
		resultPane.setBorder(BorderFactory.createTitledBorder("Displayed Results"));
		resultPane.add(new JScrollPane(resultTable), BorderLayout.CENTER);
		resultPane.add(analysisPane, BorderLayout.SOUTH);
		resultPane.setMinimumSize(new Dimension(100, 100));
		
		JPanel metricPane = new JPanel(new BorderLayout());
		metricPane.setBorder(BorderFactory.createTitledBorder("Displayed Metrics"));
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
		controlPane.setBorder(BorderFactory.createTitledBorder("Controls"));
		controlPane.add(new JLabel("Algorithm:"), label);
		controlPane.add(algorithm, field);
		controlPane.add(new JLabel("Problem:"), label);
		controlPane.add(problem, field);
		controlPane.add(new JLabel("Seeds:"), label);
		controlPane.add(numberOfSeeds, field);
		controlPane.add(new JLabel("Max NFE:"), label);
		controlPane.add(numberOfEvaluations, field);
		controlPane.add(buttonPane, button);
		controlPane.add(new JPanel(), button);
		controlPane.add(new JLabel("Run Progress:"), label);
		controlPane.add(runProgress, field);
		controlPane.add(new JLabel("Overall Progress:"), label);
		controlPane.add(seedProgress, field);
		
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
	
	protected void updateResultList() {
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
	
	public Controller getController() {
		return controller;
	}
	
	protected Paint getPaint(Comparable<?> key) {
		if (paintMap.containsKey(key)) {
			return paintMap.getPaint(key);
		} else {
			Paint paint = COLORS[(nextPaintIndex++) % COLORS.length];
			paintMap.put(key, paint);
			return paint;
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		
		controller.fireViewChangedEvent();
	}
	
	protected void clear() {
		resultListModel.clear();
		resultTable.getSelectionModel().clearSelection();
		resultTableModel.fireTableDataChanged();
		metricListModel.clear();
		metricList.getSelectionModel().clearSelection();
		paintMap.clear();
		nextPaintIndex = 0;
		
		chartContainer.removeAll();
		chartContainer.revalidate();
		chartContainer.repaint();
	}
	
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

	protected List<String> getSelectedMetrics() {
		List<String> selectedMetrics = new ArrayList<String>();
		
		for (int index : metricList.getSelectedIndices()) {
			selectedMetrics.add(metricListModel.getElementAt(index));
		}
		
		return selectedMetrics;
	}
	
	protected String getAlgorithm() {
		return (String)algorithm.getSelectedItem();
	}
	
	protected String getProblem() {
		return (String)problem.getSelectedItem();
	}
	
	protected int getNumberOfEvaluations() {
		return (Integer)numberOfEvaluations.getValue();
	}
	
	protected int getNumberOfSeeds() {
		return (Integer)numberOfSeeds.getValue();
	}
	
	protected List<ResultKey> getSelectedResults() {
		List<ResultKey> selectedResults = new ArrayList<ResultKey>();
		
		for (int index : resultTable.getSelectedRows()) {
			selectedResults.add(resultListModel.getElementAt(index));
		}
		
		return selectedResults;
	}
	
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
				updateResultList();
			}
		} else if (event.getType().equals(ControllerEvent.Type.PROGRESS_CHANGED)) {
			runProgress.setValue(controller.getRunProgress());
			seedProgress.setValue(controller.getOverallProgress());
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
