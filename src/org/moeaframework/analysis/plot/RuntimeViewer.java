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
package org.moeaframework.analysis.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.plot.RuntimeController.FitMode;
import org.moeaframework.analysis.runtime.Observations;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.util.Localization;
import org.moeaframework.util.mvc.ControllerEvent;
import org.moeaframework.util.mvc.ControllerListener;
import org.moeaframework.util.mvc.RunnableAction;
import org.moeaframework.util.mvc.SelectValueAction;
import org.moeaframework.util.mvc.ToggleAction;

/**
 * Window for displaying approximation set dynamics.
 */
public class RuntimeViewer extends JFrame implements ListSelectionListener, ControllerListener {

	private static final long serialVersionUID = -7556845366893802202L;

	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(RuntimeViewer.class);
	
	/**
	 * The container of the plot.
	 */
	private JPanel chartContainer;
	
	/**
	 * The slider controlling the current NFE.
	 */
	private JSlider slider;
	
	/**
	 * The x-axis bounds for zooming; or {@code null} if the user has not yet set zoom bounds.
	 */
	private Range zoomRangeBounds;
	
	/**
	 * The y-axis bounds for zooming; or {@code null} if the user has not yet set zoom bounds.
	 */
	private Range zoomDomainBounds;
		
	/**
	 * The control for selecting which seeds to display in the plot.
	 */
	private JList<RuntimeSeries> seriesList;
	
	private DefaultListModel<RuntimeSeries> seriesListModel;
	
	private JButton selectAll;
	
	private JButton selectNone;
	
	private JMenuItem play;
	
	private JMenuItem stop;
	
	/**
	 * The control for selecting which objective, constraint or decision variable will be displayed on the x-axis.
	 */
	private JComboBox<AxisSelector<Solution, Number>> xAxisSelection;
	
	/**
	 * The control for selecting which objective, constraint or decision variable will be displayed on the y-axis.
	 */
	private JComboBox<AxisSelector<Solution, Number>> yAxisSelection;
	
	/**
	 * Maintains a mapping from series key to paints displayed in the plot.
	 */
	private PaintHelper paintHelper;
	
	private RuntimeController controller;
		
	/**
	 * Displays the approximation sets contained in the observations.  In addition to displaying the viewer, this
	 * method also configures the system look and feel.  Therefore, prefer this static method when creating a 
	 * standalone version of this window.
	 * 
	 * @param title the name or title for the data
	 * @param referenceSet the reference set for the problem
	 * @param observations the observations containing approximation set data
	 */
	public static void show(String title, NondominatedPopulation referenceSet, Observations... observations) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				//silently handle
			}
				
			RuntimeViewer viewer = new RuntimeViewer(title, referenceSet, observations);
			viewer.setLocationRelativeTo(null);
			viewer.setVisible(true);	
		});
	}
	
	public RuntimeViewer(String title) {
		super();
		
		initialize();
		layoutMenu();
		layoutComponents();
		
		setTitle(title != null ? title : localization.getString("title.runtimeViewer"));
		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImages(Settings.getIcon().getResolutionVariants());
		
		controller.fireEvent("stateChanged");
	}
	
	/**
	 * Constructs a new window for displaying approximation set dynamics.  This constructor must only be invoked on the
	 * event dispatch thread.
	 * 
	 * @param name the name or title for the data
	 * @param referenceSet the reference set for the problem
	 */
	public RuntimeViewer(String title, NondominatedPopulation referenceSet, Observations... observations) {
		this(title);
		
		getController().setReferenceSet(referenceSet);
		
		for (int i = 0; i < observations.length; i++) {
			getController().addSeries("Seed " + (i + 1), observations[i]);
		}
	}
	
	/**
	 * Initializes this window.  This method is invoked in the constructor, and should not be invoked again.
	 */
	protected void initialize() {
		controller = new RuntimeController(this);
		controller.addControllerListener(this);
		
		seriesListModel = new DefaultListModel<>();
		
		slider = new JSlider();
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing(100);
		slider.setMajorTickSpacing(1000);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setCurrentNFE(slider.getValue());
			}
			
		});
		
		xAxisSelection = new JComboBox<>();
		xAxisSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateView();
			}
			
		});
		
		yAxisSelection = new JComboBox<>();
		yAxisSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateView();
			}
			
		});
		
		//initialize the seed list		
		seriesList = new JList<>(seriesListModel);
		seriesList.addListSelectionListener(this);
		
		selectAll = new RunnableAction("selectAll", localization, () -> {
				seriesList.getSelectionModel().setSelectionInterval(0, seriesList.getModel().getSize()-1);
			}).toButton();
		
		selectNone = new RunnableAction("selectNone", localization, () -> {
				seriesList.getSelectionModel().clearSelection();
			}).toButton();
		
		paintHelper = new PaintHelper();
		paintHelper.set(localization.getString("text.referenceSet"), Color.BLACK);
		
		chartContainer = new JPanel(new BorderLayout());
	}
	
	private void layoutMenu() {
		JMenu fileMenu = new JMenu(localization.getString("menu.file"));
		
		fileMenu.add(new RunnableAction("loadResultFile", localization, () -> {
				JFileChooser fileChooser = new JFileChooser();

				int result = fileChooser.showOpenDialog(RuntimeViewer.this);

				if (result == JFileChooser.APPROVE_OPTION) {
					try (ResultFileReader reader = ResultFileReader.openLegacy(null, fileChooser.getSelectedFile())) {
						seriesListModel.addElement(RuntimeSeries.of(fileChooser.getSelectedFile().getName(), reader));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}).toMenuItem());
		
		fileMenu.add(new RunnableAction("loadReferenceSet", localization, () -> {
				JFileChooser fileChooser = new JFileChooser();

				int result = fileChooser.showOpenDialog(RuntimeViewer.this);

				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						controller.setReferenceSet(NondominatedPopulation.load(fileChooser.getSelectedFile()));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}).toMenuItem());
		
		fileMenu.addSeparator();
		fileMenu.add(new RunnableAction("exit", localization, this::dispose).toMenuItem());
		
		JMenu sizeMenu = new JMenu(localization.getString("menu.pointSize"));
		
		for (int size : new int[] { 8, 10, 12, 14, 16 }) {
			sizeMenu.add(new SelectValueAction<>("pointSize", localization, controller.getPointSize(), size).toMenuItem());
		}
		
		JMenu transparencyMenu = new JMenu(localization.getString("menu.pointTransparency"));
		
		for (int transparency : new int[] { 0, 25, 50, 75, 100 }) {
			transparencyMenu.add(new SelectValueAction<>("pointTransparency", localization, controller.getPointTransparency(), transparency).toMenuItem());
		}

		JMenu fitMenu = new JMenu(localization.getString("menu.fit"));
		
		for (FitMode fitMode : FitMode.values()) {
			fitMenu.add(new SelectValueAction<>("fit", localization, controller.getFitMode(), fitMode).toMenuItem());
		}
		
		JMenu viewMenu = new JMenu(localization.getString("menu.view"));
		viewMenu.add(new ToggleAction("showReferenceSet", localization, controller.getShowReferenceSet()).toMenuItem());
		viewMenu.addSeparator();
		viewMenu.add(fitMenu);
		viewMenu.addSeparator();
		viewMenu.add(sizeMenu);
		viewMenu.add(transparencyMenu);
		
		play = new RunnableAction("play", localization, controller::play).toMenuItem();
		stop = new RunnableAction("stop", localization, controller::stop).toMenuItem();

		JMenu playbackMenu = new JMenu("Playback");
		playbackMenu.add(play);
		playbackMenu.add(stop);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(playbackMenu);
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Lays out the components on this window.  This method is invoked by the constructor, and should not be invoked
	 * again.
	 */
	protected void layoutComponents() {
		setLayout(new BorderLayout());
		
		JPanel objectivePane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		objectivePane.add(new JLabel(localization.getString("text.xAxis")));
		objectivePane.add(xAxisSelection);
		objectivePane.add(new JLabel(localization.getString("text.yAxis")));
		objectivePane.add(yAxisSelection);
		
		JPanel controlPane = new JPanel(new GridLayout(2, 1));
		controlPane.add(slider);
		controlPane.add(objectivePane);
		
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(chartContainer, BorderLayout.CENTER);
		rightPane.add(controlPane, BorderLayout.SOUTH);
		
		JPanel seriesSelectionPane = new JPanel(new GridLayout(1, 2));
		seriesSelectionPane.add(selectAll);
		seriesSelectionPane.add(selectNone);
		
		JPanel seriesPane = new JPanel(new BorderLayout());
		seriesPane.setBorder(BorderFactory.createTitledBorder(localization.getString("text.series")));
		seriesPane.add(new JScrollPane(seriesList), BorderLayout.CENTER);
		seriesPane.add(seriesSelectionPane, BorderLayout.SOUTH);
		seriesPane.setMinimumSize(new Dimension(150, 150));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, seriesPane, rightPane);
		splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);

		add(splitPane, BorderLayout.CENTER);
	}
	
	public RuntimeController getController() {
		return controller;
	}
	
	protected Shape getPointShape() {
		double shapeSize = controller.getPointSize().get();
		return new Ellipse2D.Double(-shapeSize/2.0, -shapeSize/2.0, shapeSize, shapeSize);
	}
	
	protected void updateModel() {
		int xAxisIndex = xAxisSelection.getSelectedIndex();
		int yAxisIndex = yAxisSelection.getSelectedIndex();
		
		if (xAxisIndex < 0) {
			xAxisIndex = 0;
		}
		
		if (yAxisIndex < 0) {
			yAxisIndex = 1;
		}

		Vector<AxisSelector<Solution, Number>> axes = new Vector<>();
		Solution prototypeSolution = null;
		int numberOfVariables = Integer.MAX_VALUE;
		int numberOfObjectives = Integer.MAX_VALUE;
		int numberOfConstraints = Integer.MAX_VALUE;
		
		if (controller.getReferenceSet() != null) {
			prototypeSolution = controller.getReferenceSet().first().getValue().get(0);
			numberOfVariables = Math.min(numberOfVariables, prototypeSolution.getNumberOfVariables());
			numberOfObjectives = Math.min(numberOfObjectives, prototypeSolution.getNumberOfObjectives());
			numberOfConstraints = Math.min(numberOfConstraints, prototypeSolution.getNumberOfConstraints());
		}
		
		for (RuntimeSeries series : controller.getSeries()) {
			prototypeSolution = series.first().getValue().get(0);
			numberOfVariables = Math.min(numberOfVariables, prototypeSolution.getNumberOfVariables());
			numberOfObjectives = Math.min(numberOfObjectives, prototypeSolution.getNumberOfObjectives());
			numberOfConstraints = Math.min(numberOfConstraints, prototypeSolution.getNumberOfConstraints());
		}

		if (prototypeSolution != null) {
			for (int i = 0; i < numberOfObjectives; i++) {
				final int index = i;
				axes.add(new AxisSelector<Solution, Number>(
						Objective.getNameOrDefault(prototypeSolution.getObjective(index), index),
						s -> s.getObjective(index).getValue()));
			}
			
			for (int i = 0; i < numberOfConstraints; i++) {
				final int index = i;
				axes.add(new AxisSelector<Solution, Number>(
						Constraint.getNameOrDefault(prototypeSolution.getConstraint(index), index),
						s -> s.getConstraint(index).getValue()));
			}
			
			for (int i = 0; i < numberOfVariables; i++) {
				final int index = i;
				axes.add(new AxisSelector<Solution, Number>(
						Variable.getNameOrDefault(prototypeSolution.getVariable(index), index),
						s -> s.getVariable(index) instanceof RealVariable rv ? rv.getValue() : Double.NaN));
			}
		}
		
		xAxisSelection.setModel(new DefaultComboBoxModel<>(axes));
		yAxisSelection.setModel(new DefaultComboBoxModel<>(axes));
		xAxisSelection.setSelectedIndex(xAxisIndex);
		yAxisSelection.setSelectedIndex(yAxisIndex);
		
		seriesListModel.clear();
		seriesListModel.addAll(controller.getSeries());
		
		slider.setMinimum(0);
		slider.setMaximum(controller.getMaximumNFE());
		slider.setValue(controller.getCurrentNFE());
	}
	
	/**
	 * Updates the display.  This method must only be invoked on the event dispatch thread.
	 */
	protected void updateView() {
		if (chartContainer == null) {
			return;
		}
		
		AxisSelector<Solution, Number> xAxis = xAxisSelection.getItemAt(xAxisSelection.getSelectedIndex());
		AxisSelector<Solution, Number> yAxis = yAxisSelection.getItemAt(yAxisSelection.getSelectedIndex());

		int currentNFE = controller.getCurrentNFE();
		slider.setValue(currentNFE);
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		//generate approximation set
		for (RuntimeSeries series : getSelectedSeriesAt(currentNFE)) {
			dataset.addSeries(series.toXYSeries(currentNFE, xAxis, yAxis));
		}
		
		//generate reference set
		RuntimeSeries referenceSet = controller.getReferenceSet();
		
		if (referenceSet != null && controller.getShowReferenceSet().get()) {
			dataset.addSeries(referenceSet.toXYSeries(0, xAxis, yAxis));
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(
				getTitle() + " @ " + slider.getValue() + " NFE", 
				xAxisSelection.getSelectedItem().toString(),
				yAxisSelection.getSelectedItem().toString(), 
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true,
				false);
		
		//set the renderer to only display shapes
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
		
		for (int i=0; i<dataset.getSeriesCount(); i++) {
			Paint paint = paintHelper.get(dataset.getSeriesKey(i));
			
			if (paint instanceof Color color) {
				int transparency = controller.getPointTransparency().get();
				
				if (transparency > 0) {
					paint = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(255 * (100 - transparency) / 100.0f));
				}
			}
			
			renderer.setSeriesPaint(i, paint);
			renderer.setSeriesShape(i, getPointShape());
		}
		
		plot.setRenderer(renderer);
		
		updateBounds(plot);
		
		//register with the chart to receive zoom events
		chart.addChangeListener(new ChartChangeListener() {
			
			@Override
			public void chartChanged(ChartChangeEvent e) {
				zoomRangeBounds = e.getChart().getXYPlot().getRangeAxis().getRange();
				zoomDomainBounds = e.getChart().getXYPlot().getDomainAxis().getRange();
				controller.getFitMode().set(FitMode.Zoom);
			}
			
		});
		
		chartContainer.removeAll();
		chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
		chartContainer.revalidate();
		chartContainer.repaint();
	}
	
	private List<RuntimeSeries> getSelectedSeriesAt(int NFE) {
		List<RuntimeSeries> result = new ArrayList<>();
		
		for (int selectedIndex : seriesList.getSelectedIndices()) {
			result.add(seriesListModel.get(selectedIndex));
		}
		
		return result;
	}
	
	private void updateBounds(XYPlot plot) {
		switch (controller.getFitMode().get()) {
			case InitialBounds -> {
				Pair<Range, Range> bounds = getBoundsAt(0);
				
				plot.getDomainAxis().setRange(bounds.getLeft());
				plot.getRangeAxis().setRange(bounds.getRight());
			}
			case Zoom -> {
				if ((zoomRangeBounds == null) || (zoomDomainBounds == null)) {
					Pair<Range, Range> bounds = getBoundsAt(0);
					
					zoomDomainBounds = bounds.getLeft();
					zoomRangeBounds = bounds.getRight();
				}
				
				plot.getDomainAxis().setRange(zoomDomainBounds);
				plot.getRangeAxis().setRange(zoomRangeBounds);
			}
			case ReferenceSetBounds -> {
				Pair<Range, Range> bounds = getBoundsAt(controller.getReferenceSet(), 0);
				
				plot.getDomainAxis().setRange(bounds.getLeft());
				plot.getRangeAxis().setRange(bounds.getRight());
			}
			default -> {
				// JFreeChart dynamically scales bounds
			}
		}
	}
	
	private Pair<Range, Range> getBoundsAt(RuntimeSeries series, int NFE) {
		AxisSelector<Solution, Number> xAxis = xAxisSelection.getItemAt(xAxisSelection.getSelectedIndex());
		AxisSelector<Solution, Number> yAxis = yAxisSelection.getItemAt(yAxisSelection.getSelectedIndex());
		return series.bounds(NFE, xAxis, yAxis);
	}
	
	private Pair<Range, Range> getBoundsAt(int NFE) {
		Range domain = null;
		Range range = null;
		RuntimeSeries referenceSet = controller.getReferenceSet();
		
		if (referenceSet != null) {
			Pair<Range, Range> seriesBounds = getBoundsAt(referenceSet, NFE);
			domain = Range.combine(domain, seriesBounds.getLeft());
			range = Range.combine(range, seriesBounds.getRight());
		}
		
		for (RuntimeSeries series : getSelectedSeriesAt(NFE)) {
			Pair<Range, Range> seriesBounds = getBoundsAt(series, NFE);
			domain = Range.combine(domain, seriesBounds.getLeft());
			range = Range.combine(range, seriesBounds.getRight());
		}
		
		return Pair.of(domain, range);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		
		updateView();
	}
	
	private class AxisSelector<T, R> implements Function<T, R> {
		
		private final String name;
		
		private final Function<T, R> getter;
		
		public AxisSelector(String name, Function<T, R> getter) {
			super();
			this.name = name;
			this.getter = getter;
		}

		@Override
		public R apply(T value) {
			return getter.apply(value);
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}

	@Override
	public void controllerStateChanged(ControllerEvent event) {
		if (event.getEventType().equals("modelChanged")) {
			updateModel();
		}
		
		if (event.getEventType().equals("viewChanged")) {
			updateView();
		}
		
		if (event.getEventType().equals("stateChanged")) {
			play.setEnabled(!controller.isRunning());
			stop.setEnabled(controller.isRunning());
		}
	}

}
