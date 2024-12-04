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
package org.moeaframework.analysis.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.moeaframework.analysis.diagnostics.PaintHelper;
import org.moeaframework.analysis.plot.ImageFileType;
import org.moeaframework.analysis.plot.ImageUtils;
import org.moeaframework.analysis.series.IndexType;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.viewer.RuntimeController.FitMode;
import org.moeaframework.core.Named;
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
import org.moeaframework.util.mvc.PopupAction;
import org.moeaframework.util.mvc.RunnableAction;
import org.moeaframework.util.mvc.SelectValueAction;

/**
 * Viewer showing approximation set runtime plots.
 */
public class RuntimeViewer extends JDialog implements ListSelectionListener, ControllerListener {

	private static final long serialVersionUID = -7556845366893802202L;

	/**
	 * The localization instance for producing locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(RuntimeViewer.class);
	
	/**
	 * The title of the plot.
	 */
	private final String title;
	
	/**
	 * The plot itself.
	 */
	private JFreeChart chart;
	
	/**
	 * The container of the plot.
	 */
	private JPanel chartContainer;
	
	/**
	 * The slider controlling the current NFE or index.
	 */
	private JSlider slider;
	
	/**
	 * The label shown alongside the slider.
	 */
	private JLabel sliderLabel;
	
	/**
	 * The x-axis bounds for zooming; or {@code null} if the user has not yet set zoom bounds.
	 */
	private Range zoomRangeBounds;
	
	/**
	 * The y-axis bounds for zooming; or {@code null} if the user has not yet set zoom bounds.
	 */
	private Range zoomDomainBounds;
		
	/**
	 * The control for selecting which series to display in the plot.
	 */
	private JList<PlotSeries> seriesList;
	
	/**
	 * The model containing the contents of the series list.
	 */
	private DefaultListModel<PlotSeries> seriesListModel;
	
	/**
	 * The button for selecting all series.
	 */
	private JButton selectAll;
	
	/**
	 * The button for deselecting all series.
	 */
	private JButton selectNone;
	
	/**
	 * The action to start playback.
	 */
	private RunnableAction play;
	
	/**
	 * The action to stop playback.
	 */
	private RunnableAction stop;
	
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
	
	/**
	 * The controller the manages the internal state of this viewer.
	 */
	private RuntimeController controller;
		
	/**
	 * Displays the approximation sets contained in the result series.  In addition to displaying the viewer, this
	 * method also configures the system look and feel.  Therefore, prefer this static method when creating a
	 * standalone version of this window.
	 * 
	 * @param title the name or title for the data
	 * @param referenceSet the reference set for the problem
	 * @param series the series containing approximation set data to display
	 */
	public static void show(String title, NondominatedPopulation referenceSet, ResultSeries... series) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				//silently handle
			}
				
			RuntimeViewer viewer = new RuntimeViewer(null, title);
			
			viewer.getController().setReferenceSet(referenceSet);
			
			for (int i = 0; i < series.length; i++) {
				viewer.getController().addSeries(localization.getString("text.series") + " " + (i + 1), series[i]);
			}
			
			viewer.setLocationRelativeTo(null);
			viewer.setVisible(true);
		});
	}
	
	/**
	 * Constructs an empty runtime viewer.
	 * 
	 * @param title the title of the plot
	 */
	public RuntimeViewer(String title) {
		this(null, title);
	}
	
	/**
	 * Constructs an empty runtime viewer with the given owner.
	 * 
	 * @param owner the owner frame
	 * @param title the title of the plot
	 */
	public RuntimeViewer(Frame owner, String title) {
		super(owner);
		this.title = title;
		
		initialize();
		layoutComponents();
		
		setTitle(localization.getString("title.runtimeViewer"));
		setSize(800, 600);
		setMinimumSize(new Dimension(400, 300));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImages(Settings.getIcon().getResolutionVariants());
		
		controller.fireEvent("stateChanged");
	}
	
	private void initialize() {
		controller = new RuntimeController(this);
		controller.addControllerListener(this);
		
		seriesListModel = new DefaultListModel<>();
		
		slider = new JSlider();
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setCurrentIndex(slider.getValue());
			}
			
		});
		
		sliderLabel = new JLabel("", JLabel.CENTER);
		
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
		
		seriesList = new JList<>(seriesListModel);
		seriesList.addListSelectionListener(this);
		seriesList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = seriesList.locationToIndex(e.getPoint());
					boolean isHovering = index >= 0 && seriesList.getCellBounds(index, index).contains(e.getPoint());
					
					RunnableAction removeSeries = new RunnableAction("removeSeries", localization, () -> {
						controller.removeSeries(index);
					});
					
					if (!isHovering) {
						removeSeries.setEnabled(false);
					}
					
					JPopupMenu seriesMenu = new JPopupMenu();
					seriesMenu.add(removeSeries);
					seriesMenu.show(seriesList, e.getX(), e.getY());
				}
			}
			
		});
		
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
	
	private void layoutComponents() {
		setLayout(new BorderLayout());
		
		JPanel controlPane = new JPanel(new BorderLayout());
		controlPane.add(slider, BorderLayout.CENTER);
		controlPane.add(sliderLabel, BorderLayout.SOUTH);
		
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(chartContainer, BorderLayout.CENTER);
		rightPane.add(controlPane, BorderLayout.SOUTH);
		
		JPanel selectionPane = new JPanel(new GridLayout(1, 2));
		selectionPane.add(selectAll);
		selectionPane.add(selectNone);
		
		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.setBorder(BorderFactory.createTitledBorder(localization.getString("text.series")));
		leftPane.add(new JScrollPane(seriesList), BorderLayout.CENTER);
		leftPane.add(selectionPane, BorderLayout.SOUTH);
		leftPane.setMinimumSize(new Dimension(150, 150));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        
        RunnableAction savePlot = new RunnableAction("savePlot", localization, () -> {
        	JFileChooser fileChooser = new JFileChooser();
        	
        	for (ImageFileType fileType : ImageUtils.getSupportedImageFormats()) {
        		fileChooser.addChoosableFileFilter(fileType.getFilter());
        	}
        	
        	fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);

    		int result = fileChooser.showSaveDialog(this);

    		if (result == JFileChooser.APPROVE_OPTION) {
    			try {
					ImageUtils.save(chart, fileChooser.getSelectedFile());
				} catch (Exception e) {
					controller.handleException(e);
				}
    		}
        });
        
        RunnableAction addSeries = new RunnableAction("addSeries", localization, () -> {
        	JFileChooser fileChooser = new JFileChooser();

    		int result = fileChooser.showOpenDialog(this);

    		if (result == JFileChooser.APPROVE_OPTION) {
    			try {
    				controller.addSeries(fileChooser.getSelectedFile().getName(),
    						ResultSeries.of(fileChooser.getSelectedFile()));
    			} catch (Exception e) {
    				controller.handleException(e);
    			}
    		}
        });
        
		play = new RunnableAction("play", localization, controller::play);
		stop = new RunnableAction("stop", localization, controller::stop);
		
		RunnableAction start = new RunnableAction("start", localization,
				() -> controller.setCurrentIndex(controller.getStartingIndex()));
		RunnableAction end = new RunnableAction("end", localization,
				() -> controller.setCurrentIndex(controller.getEndingIndex()));
		
		PopupAction pointSize = new PopupAction("pointSizeMenu", localization, () -> {
			JPopupMenu menu = new JPopupMenu();
			
			for (int value : new int[] { 8, 10, 12, 14, 16 }) {
				menu.add(new SelectValueAction<>("pointSize", localization, controller.getPointSize(), value).toMenuItem());
			}

			return menu;
		});
		
		PopupAction transparency = new PopupAction("pointTransparencyMenu", localization, () -> {
			JPopupMenu menu = new JPopupMenu();
			
			for (int value : new int[] { 0, 25, 50, 75, 100 }) {
				menu.add(new SelectValueAction<>("pointTransparency", localization, controller.getPointTransparency(), value).toMenuItem());
			}
			
			return menu;
		});
		
		PopupAction fitMode = new PopupAction("fitMenu", localization, () -> {
			JPopupMenu menu = new JPopupMenu();
			
			for (FitMode value : FitMode.values()) {
				menu.add(new SelectValueAction<>("fit", localization, controller.getFitMode(), value).toMenuItem());
			}
			
			return menu;
		});
		
		JPanel objectiveSelectionPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		objectiveSelectionPane.add(new JLabel(localization.getString("text.xAxis")));
		objectiveSelectionPane.add(xAxisSelection);
		objectiveSelectionPane.add(new JLabel(localization.getString("text.yAxis")));
		objectiveSelectionPane.add(yAxisSelection);
		
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        toolbar.add(savePlot);
        toolbar.add(addSeries);
        toolbar.addSeparator();
        toolbar.add(start);
		toolbar.add(play);
		toolbar.add(stop);
		toolbar.add(end);
		toolbar.addSeparator();
		toolbar.add(fitMode);
		toolbar.addSeparator();
		toolbar.add(pointSize);
		toolbar.add(transparency);
		toolbar.addSeparator();
		toolbar.add(objectiveSelectionPane);

		add(toolbar, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the controller for this runtime viewer.
	 * 
	 * @return the controller
	 */
	public RuntimeController getController() {
		return controller;
	}
	
	private Shape getPointShape() {
		double shapeSize = controller.getPointSize().get();
		return new Ellipse2D.Double(-shapeSize/2.0, -shapeSize/2.0, shapeSize, shapeSize);
	}
	
	private void updateModel() {
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
			prototypeSolution = controller.getReferenceSet().getPrototypeSolution();
			numberOfVariables = Math.min(numberOfVariables, prototypeSolution.getNumberOfVariables());
			numberOfObjectives = Math.min(numberOfObjectives, prototypeSolution.getNumberOfObjectives());
			numberOfConstraints = Math.min(numberOfConstraints, prototypeSolution.getNumberOfConstraints());
		}
		
		for (PlotSeries series : controller.getSeries()) {
			prototypeSolution = series.getPrototypeSolution();
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
		
		// update list, keeping existing selection and adding any new items
		int seriesCount = seriesListModel.size();
		int[] selectedIndices = seriesList.getSelectionModel().getSelectedIndices();
		
		seriesListModel.clear();
		seriesListModel.addAll(controller.getSeries());

		seriesList.setSelectedIndices(selectedIndices);
		
		if (seriesListModel.size() > seriesCount) {
			seriesList.addSelectionInterval(seriesCount, seriesListModel.size() - 1);
		}
		
		slider.setMinimum(controller.getStartingIndex());
		slider.setMaximum(controller.getEndingIndex());
		slider.setValue(controller.getCurrentIndex());
		
		if (controller.getIndexType() == IndexType.NFE) {
			slider.setMinorTickSpacing(Math.max(controller.getStepSize(), 10));
			slider.setMajorTickSpacing(controller.getEndingIndex() / 10);
			sliderLabel.setText(localization.getString("text.NFE"));
		} else {
			slider.setMinorTickSpacing(1);
			slider.setMajorTickSpacing(-1);
			sliderLabel.setText(localization.getString("text.Index"));
		}
	}
	
	/**
	 * Updates the display.  This method must only be invoked on the event dispatch thread.
	 */
	private void updateView() {
		if (chartContainer == null) {
			return;
		}
		
		AxisSelector<Solution, Number> xAxis = xAxisSelection.getItemAt(xAxisSelection.getSelectedIndex());
		AxisSelector<Solution, Number> yAxis = yAxisSelection.getItemAt(yAxisSelection.getSelectedIndex());

		int currentIndex = controller.getCurrentIndex();
		slider.setValue(currentIndex);
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		//generate approximation set
		for (PlotSeries series : getSelectedSeries()) {
			dataset.addSeries(series.toXYSeries(currentIndex, xAxis, yAxis));
		}
		
		//generate reference set
		PlotSeries referenceSet = controller.getReferenceSet();
		
		if (referenceSet != null && controller.getShowReferenceSet().get()) {
			dataset.addSeries(referenceSet.toXYSeries(currentIndex, xAxis, yAxis));
		}
		
		chart = ChartFactory.createScatterPlot(
				(title == null ? "" : title + " @ ") + (controller.getIndexType() == IndexType.NFE ?
						localization.getString("text.NFE") : localization.getString("text.Index")) +
						" " + slider.getValue(),
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
				if (e.getChart() != null) {
					zoomRangeBounds = e.getChart().getXYPlot().getRangeAxis().getRange();
					zoomDomainBounds = e.getChart().getXYPlot().getDomainAxis().getRange();
					controller.getFitMode().set(FitMode.Zoom);
				}
			}
			
		});
		
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPopupMenu(null); // disable default pop-up
		
		chartContainer.removeAll();
		chartContainer.add(chartPanel, BorderLayout.CENTER);
		chartContainer.revalidate();
		chartContainer.repaint();
	}
	
	private List<PlotSeries> getSelectedSeries() {
		List<PlotSeries> result = new ArrayList<>();
		
		for (int selectedIndex : seriesList.getSelectedIndices()) {
			result.add(seriesListModel.get(selectedIndex));
		}
		
		return result;
	}
	
	private void updateBounds(XYPlot plot) {
		switch (controller.getFitMode().get()) {
			case InitialBounds -> {
				Pair<Range, Range> bounds = getBoundsAt(controller.getStartingIndex());
				
				plot.getDomainAxis().setRange(bounds.getLeft());
				plot.getRangeAxis().setRange(bounds.getRight());
			}
			case Zoom -> {
				if ((zoomRangeBounds == null) || (zoomDomainBounds == null)) {
					Pair<Range, Range> bounds = getBoundsAt(controller.getStartingIndex());
					
					zoomDomainBounds = bounds.getLeft();
					zoomRangeBounds = bounds.getRight();
				}
				
				plot.getDomainAxis().setRange(zoomDomainBounds);
				plot.getRangeAxis().setRange(zoomRangeBounds);
			}
			case ReferenceSetBounds -> {
				AxisSelector<Solution, Number> xAxis = xAxisSelection.getItemAt(xAxisSelection.getSelectedIndex());
				AxisSelector<Solution, Number> yAxis = yAxisSelection.getItemAt(yAxisSelection.getSelectedIndex());
				
				Pair<Range, Range> bounds = controller.getReferenceSet().getBoundsAt(0, xAxis, yAxis);
				
				plot.getDomainAxis().setRange(bounds.getLeft());
				plot.getRangeAxis().setRange(bounds.getRight());
			}
			default -> {
				// JFreeChart dynamically scales bounds
			}
		}
	}
	
	private Pair<Range, Range> getBoundsAt(int index) {
		Range domain = null;
		Range range = null;
		PlotSeries referenceSet = controller.getReferenceSet();
		AxisSelector<Solution, Number> xAxis = xAxisSelection.getItemAt(xAxisSelection.getSelectedIndex());
		AxisSelector<Solution, Number> yAxis = yAxisSelection.getItemAt(yAxisSelection.getSelectedIndex());
		
		if (referenceSet != null) {
			Pair<Range, Range> seriesBounds = referenceSet.getBoundsAt(index, xAxis, yAxis);
			
			if (seriesBounds != null) {
				domain = Range.combine(domain, seriesBounds.getLeft());
				range = Range.combine(range, seriesBounds.getRight());
			}
		}
		
		for (PlotSeries series : getSelectedSeries()) {
			Pair<Range, Range> seriesBounds = series.getBoundsAt(index, xAxis, yAxis);
			
			if (seriesBounds != null) {
				domain = Range.combine(domain, seriesBounds.getLeft());
				range = Range.combine(range, seriesBounds.getRight());
			}
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
	
	static class PlotSeries implements Named {
		
		private final String name;
		
		private final ResultSeries series;
		
		public PlotSeries(String name, ResultSeries series) {
			super();
			this.name = name;
			this.series = series;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		public ResultSeries getSeries() {
			return series;
		}

		@Override
		public String toString() {
			return name;
		}
		
		public Pair<Range, Range> getBoundsAt(int index, AxisSelector<Solution, Number> xAxis,
				AxisSelector<Solution, Number> yAxis) {
			IndexedResult entry = series.at(index);
			
			if (entry == null) {
				return null;
			}
			
			double domainMin = Double.POSITIVE_INFINITY;
			double domainMax = Double.NEGATIVE_INFINITY;
			double rangeMin = Double.POSITIVE_INFINITY;
			double rangeMax = Double.NEGATIVE_INFINITY;
			
			for (Solution solution : entry.getPopulation()) {
				double xValue = xAxis.apply(solution).doubleValue();
				double yValue = yAxis.apply(solution).doubleValue();
					
				domainMin = Math.min(domainMin, xValue);
				domainMax = Math.max(domainMax, xValue);
				rangeMin = Math.min(rangeMin, yValue);
				rangeMax = Math.max(rangeMax, yValue);
			}
			
			double domainDelta = 0.1 * (domainMax - domainMin);
			double rangeDelta = 0.1 * (rangeMax - rangeMin);

			return Pair.of(
					new Range(domainMin - domainDelta, domainMax + domainDelta),
					new Range(rangeMin - rangeDelta, rangeMax + rangeDelta));
		}
		
		public XYSeries toXYSeries(int index, AxisSelector<Solution, Number> xAxis,
				AxisSelector<Solution, Number> yAxis) {
			IndexedResult result = series.at(index);
			XYSeries xySeries = new XYSeries(name, false, true);
			
			if (result != null) {
				for (Solution solution : result.getPopulation()) {
					xySeries.add(xAxis.apply(solution), yAxis.apply(solution));
				}
			}
			
			return xySeries;
		}
		
		public int getStepSize() {
			return switch (series.getIndexType()) {
				case NFE -> (series.getEndingIndex() - series.getStartingIndex()) / series.size();
				case Index, Singleton -> 1;
			};
		}
		
		public Solution getPrototypeSolution() {
			return series.first().getPopulation().get(0);
		}
		
	}
	
	static class AxisSelector<T, R> implements Function<T, R> {
		
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
