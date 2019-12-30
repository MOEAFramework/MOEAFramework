/* Copyright 2009-2019 David Hadka
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.Localization;

/**
 * Window for displaying approximation set dynamics.
 */
public class ApproximationSetViewer extends JFrame implements ChangeListener,
ActionListener, ChartChangeListener, ListSelectionListener {

	private static final long serialVersionUID = -7556845366893802202L;
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			ApproximationSetViewer.class);

	/**
	 * The accumulators which contain {@code "Approximation Set"} entries.
	 */
	private List<Accumulator> accumulators;
	
	/**
	 * The container of the plot.
	 */
	private JPanel chartContainer;
	
	/**
	 * The slider controlling the current NFE.
	 */
	private JSlider slider;
	
	/**
	 * The x-axis bounds of the initial approximation set(s).
	 */
	private Range initialRangeBounds;
	
	/**
	 * The y-axis bounds of the initial approximation set(s).
	 */
	private Range initialDomainBounds;
	
	/**
	 * The x-axis bounds for zooming; or {@code null} if the user has not yet
	 * set zoom bounds.
	 */
	private Range zoomRangeBounds;
	
	/**
	 * The y-axis bounds for zooming; or {@code null} if the user has not yet
	 * set zoom bounds.
	 */
	private Range zoomDomainBounds;
	
	/**
	 * The x-axis bounds of the reference set.
	 */
	private Range referenceRangeBounds;
	
	/**
	 * The y-axis bounds of the reference set.
	 */
	private Range referenceDomainBounds;
	
	/**
	 * The control for choosing to scale the plot using the initial
	 * approximation set bounds.
	 */
	private JRadioButton useInitialBounds;
	
	/**
	 * The control for choosing to scale the plot using the reference set
	 * bounds.
	 */
	private JRadioButton useReferenceSetBounds;
	
	/**
	 * The control for choosing to scale the plot dynamically at each NFE.
	 */
	private JRadioButton useDynamicBounds;
	
	/**
	 * The control for choosing to scale the plot using the user-specified zoom.
	 */
	private JRadioButton useZoomBounds;
	
	/**
	 * The reference set.
	 */
	private NondominatedPopulation referenceSet;
	
	/**
	 * The control for selecting which seeds to display in the plot.
	 */
	private JList seedList;
	
	/**
	 * The control for selecting all seeds.
	 */
	private JButton selectAll;
	
	/**
	 * The control for selecting which objective, constraint or decision
	 * variable will be displayed on the x-axis.
	 */
	private JComboBox xAxisSelection;
	
	/**
	 * The control for selecting which objective, constraint or decision
	 * variable will be displayed on the y-axis.
	 */
	private JComboBox yAxisSelection;
	
	/**
	 * Maintains a mapping from series key to paints displayed in the plot.
	 */
	private PaintHelper paintHelper;
	
	/**
	 * Constructs a new window for displaying approximation set dynamics.  This
	 * constructor must only be invoked on the event dispatch thread.
	 * 
	 * @param name the name or title for the data
	 * @param accumulators the accumulators containing approximation set data
	 * @param referenceSet the reference set for the problem
	 */
	public ApproximationSetViewer(String name, List<Accumulator> accumulators, 
			NondominatedPopulation referenceSet) {
		super(localization.getString("title.approximationSetViewer", name));
		this.accumulators = accumulators;
		this.referenceSet = referenceSet;
		
		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		initialize();
		layoutComponents();
		
		//this results in a call to update()
		selectAll.doClick();
	}
	
	/**
	 * Initializes the reference set bounds.
	 */
	protected void initializeReferenceSetBounds() {
		if (referenceSet == null) {
			return;
		}

		double domainMin = Double.POSITIVE_INFINITY;
		double domainMax = Double.NEGATIVE_INFINITY;
		double rangeMin = Double.POSITIVE_INFINITY;
		double rangeMax = Double.NEGATIVE_INFINITY;
		
		for (Solution solution : referenceSet) {
			domainMin = Math.min(domainMin, getValue(solution, 0));
			domainMax = Math.max(domainMax, getValue(solution, 0));
			rangeMin = Math.min(rangeMin, getValue(solution, 1));
			rangeMax = Math.max(rangeMax, getValue(solution, 1));
		}
		
		domainMax += (domainMax-domainMin);
		rangeMax += (rangeMax-rangeMin);
		
		referenceDomainBounds = new Range(domainMin, domainMax);
		referenceRangeBounds = new Range(rangeMin, rangeMax);
	}
	
	/**
	 * Initializes this window.  This method is invoked in the constructor, and
	 * should not be invoked again.
	 */
	protected void initialize() {
		//initialize the NFE slider
		int minimumNFE = Integer.MAX_VALUE;
		int maximumNFE = Integer.MIN_VALUE;
		
		for (Accumulator accumulator : accumulators) {
			minimumNFE = Math.min(minimumNFE, 
					(Integer)accumulator.get("NFE", 0));
			maximumNFE = Math.max(maximumNFE, 
					(Integer)accumulator.get("NFE", accumulator.size("NFE")-1));
		}
		
		slider = new JSlider(minimumNFE, maximumNFE, minimumNFE);
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing(100);
		slider.setMajorTickSpacing(1000);
		slider.addChangeListener(this);
		
		//initializes the options available for axis plotting
		Solution solution = (Solution)((List<?>)accumulators.get(0).get(
				"Approximation Set", 0)).get(0);
		Vector<String> objectives = new Vector<String>();
		
		for (int i=0; i<solution.getNumberOfObjectives(); i++) {
			objectives.add(localization.getString("text.objective", i+1));
		}
		
		for (int i=0; i<solution.getNumberOfConstraints(); i++) {
			objectives.add(localization.getString("text.constraint", i+1));
		}
		
		for (int i=0; i<solution.getNumberOfVariables(); i++) {
			objectives.add(localization.getString("text.variable", i+1));
		}
		
		xAxisSelection = new JComboBox(objectives);
		yAxisSelection = new JComboBox(objectives);
		
		xAxisSelection.setSelectedIndex(0);
		yAxisSelection.setSelectedIndex(1);
		
		xAxisSelection.addActionListener(this);
		yAxisSelection.addActionListener(this);
		
		//initialize the reference set bounds
		initializeReferenceSetBounds();
		
		//initialize plotting controls
		useInitialBounds = new JRadioButton(
				localization.getString("action.useInitialBounds.name"));
		useReferenceSetBounds = new JRadioButton(
				localization.getString("action.useReferenceSetBounds.name"));
		useDynamicBounds = new JRadioButton(
				localization.getString("action.useDynamicBounds.name"));
		useZoomBounds = new JRadioButton(
				localization.getString("action.useZoom.name"));
		
		useInitialBounds.setToolTipText(
				localization.getString("action.useInitialBounds.description"));
		useReferenceSetBounds.setToolTipText(
				localization.getString("action.useReferenceSetBounds.description"));
		useDynamicBounds.setToolTipText(
				localization.getString("action.useDynamicBounds.description"));
		useZoomBounds.setToolTipText(
				localization.getString("action.useZoom.description"));
		
		ButtonGroup rangeButtonGroup = new ButtonGroup();
		rangeButtonGroup.add(useInitialBounds);
		rangeButtonGroup.add(useReferenceSetBounds);
		rangeButtonGroup.add(useDynamicBounds);
		rangeButtonGroup.add(useZoomBounds);
		
		if (referenceSet == null) {
			useReferenceSetBounds.setEnabled(false);
		}
		
		useInitialBounds.setSelected(true);
		useInitialBounds.addActionListener(this);
		useReferenceSetBounds.addActionListener(this);
		useDynamicBounds.addActionListener(this);
		useZoomBounds.addActionListener(this);
		
		//initialize the seed list
		String[] seeds = new String[accumulators.size()];
		
		for (int i=0; i<accumulators.size(); i++) {
			seeds[i] = localization.getString("text.seed", i+1);
		}
		
		seedList = new JList(seeds);
		seedList.addListSelectionListener(this);
		
		selectAll = new JButton(new AbstractAction() {

			private static final long serialVersionUID = -3709557130361259485L;
			
			{
				putValue(Action.NAME,
						localization.getString("action.selectAll.name"));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				seedList.getSelectionModel().setSelectionInterval(0, 
						seedList.getModel().getSize()-1);
			}
			
		});
		
		//initialize miscellaneous components
		paintHelper = new PaintHelper();
		paintHelper.set(localization.getString("text.referenceSet"),
				Color.BLACK);
		
		chartContainer = new JPanel(new BorderLayout());
	}
	
	/**
	 * Lays out the components on this window.  This method is invoked by the
	 * constructor, and should not be invoked again.
	 */
	protected void layoutComponents() {
		setLayout(new BorderLayout());
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPane.add(useInitialBounds);
		buttonPane.add(useReferenceSetBounds);
		buttonPane.add(useDynamicBounds);
		buttonPane.add(useZoomBounds);
		
		JPanel objectivePane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		objectivePane.add(new JLabel(localization.getString("text.xAxis")));
		objectivePane.add(xAxisSelection);
		objectivePane.add(new JLabel(localization.getString("text.yAxis")));
		objectivePane.add(yAxisSelection);
		
		JPanel controlPane = new JPanel(new GridLayout(3, 1));
		controlPane.add(slider);
		controlPane.add(buttonPane);
		controlPane.add(objectivePane);
		
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(chartContainer, BorderLayout.CENTER);
		rightPane.add(controlPane, BorderLayout.SOUTH);
		
		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.setBorder(BorderFactory.createTitledBorder(
				localization.getString("text.seeds")));
		leftPane.add(new JScrollPane(seedList), BorderLayout.CENTER);
		leftPane.add(selectAll, BorderLayout.SOUTH);
		leftPane.setMinimumSize(new Dimension(100, 100));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				leftPane, rightPane);

		add(splitPane, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the x- or y-axis value for the specified solution.  The returned
	 * value changes based on the user's preferences, and may return the value
	 * stored in an objective, constraint or variable.  Returns {@code 0.0} if
	 * the value could not be parsed.
	 * 
	 * @param solution the solution whose x- or y-axis value is returned
	 * @param axis {@code 0} for x-axis, {@code 1} for y-axis
	 * @return the x- or y-axis value for the specified solution
	 */
	protected double getValue(Solution solution, int axis) {
		int selection = axis == 0 ? xAxisSelection.getSelectedIndex() : 
				yAxisSelection.getSelectedIndex();
		
		if (selection < solution.getNumberOfObjectives()) {
			return solution.getObjective(selection);
		} else {
			selection -= solution.getNumberOfObjectives();
		}
		
		if (selection < solution.getNumberOfConstraints()) {
			return solution.getConstraint(selection);
		} else {
			selection -= solution.getNumberOfConstraints();
		}
		
		if (selection < solution.getNumberOfVariables()) {
			if (solution.getVariable(selection) instanceof RealVariable) {
				return ((RealVariable)solution.getVariable(selection))
						.getValue();
			} else {
				return 0.0;
			}
		} else {
			return 0.0;
		}
	}
	
	/**
	 * Updates the display.  This method must only be invoked on the event
	 * dispatch thread.
	 */
	protected void update() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		//generate approximation set
		for (int seedIndex : seedList.getSelectedIndices()) {
			Accumulator accumulator = accumulators.get(seedIndex);
			int index = 0;
			
			if (!accumulator.keySet().contains("Approximation Set")) {
				continue;
			}
				
			while ((index < accumulator.size("NFE")-1) && 
					((Integer)accumulator.get("NFE", index) < slider.getValue())) {
				index++;
			}
				
			List<?> list = (List<?>)accumulator.get("Approximation Set", index);
			XYSeries series = new XYSeries(
					localization.getString("text.seed", seedIndex+1),
					false, true);
				
			for (Object object : list) {
				Solution solution = (Solution)object;
				series.add(getValue(solution, 0), getValue(solution, 1));
			}
			
			dataset.addSeries(series);
		}
		
		//generate reference set
		if (referenceSet != null) {
			XYSeries series = new XYSeries(
					localization.getString("text.referenceSet"),
					false, true);
				
			for (Solution solution : referenceSet) {
				series.add(getValue(solution, 0), getValue(solution, 1));
			}
			
			dataset.addSeries(series);
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(
				getTitle() + " @ " + slider.getValue() + " NFE", 
				(String)xAxisSelection.getSelectedItem(),
				(String)yAxisSelection.getSelectedItem(), 
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true,
				false);
		
		//set the renderer to only display shapes
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, 
				true);
		
		for (int i=0; i<dataset.getSeriesCount(); i++) {
			Paint paint = paintHelper.get(dataset.getSeriesKey(i));
			renderer.setSeriesPaint(i, paint);
		}
		
		plot.setRenderer(renderer);
		
		//set the zoom based on the user's preferences
		if ((initialRangeBounds == null) || (initialDomainBounds == null)) {
			initialRangeBounds = plot.getRangeAxis().getRange();
			initialDomainBounds = plot.getDomainAxis().getRange();
		}
		
		if (useInitialBounds.isSelected()) {
			plot.getRangeAxis().setRange(initialRangeBounds);
			plot.getDomainAxis().setRange(initialDomainBounds);
		} else if (useZoomBounds.isSelected()) {
			if ((zoomRangeBounds == null) || (zoomDomainBounds == null)) {
				zoomRangeBounds = initialRangeBounds;
				zoomDomainBounds = initialDomainBounds;
			}
			
			plot.getRangeAxis().setRange(zoomRangeBounds);
			plot.getDomainAxis().setRange(zoomDomainBounds);
		} else if (useReferenceSetBounds.isSelected()) {
			if (referenceRangeBounds.getLength() > 0.0) {
				plot.getRangeAxis().setRange(referenceRangeBounds);
			}
			
			if (referenceDomainBounds.getLength() > 0.0) {
				plot.getDomainAxis().setRange(referenceDomainBounds);
			}
		}
		
		//register with the chart to receive zoom events
		chart.addChangeListener(this);
		
		chartContainer.removeAll();
		chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
		chartContainer.revalidate();
		chartContainer.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ((e.getSource() == xAxisSelection) || 
				(e.getSource() == yAxisSelection)) {
			initialRangeBounds = null;
			initialDomainBounds = null;
			initializeReferenceSetBounds();
		}
		
		update();
	}

	@Override
	public void chartChanged(ChartChangeEvent e) {
		zoomRangeBounds = e.getChart().getXYPlot().getRangeAxis().getRange();
		zoomDomainBounds = e.getChart().getXYPlot().getDomainAxis().getRange();
		useZoomBounds.setSelected(true);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		
		update();
	}

}
