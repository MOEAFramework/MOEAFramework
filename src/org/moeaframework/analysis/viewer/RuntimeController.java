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

import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.moeaframework.analysis.series.IndexType;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.analysis.viewer.RuntimeViewer.PlotSeries;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.Localization;
import org.moeaframework.util.mvc.Controller;
import org.moeaframework.util.mvc.Setting;
import org.moeaframework.util.mvc.SettingChangedEvent;
import org.moeaframework.util.mvc.SettingChangedListener;
import org.moeaframework.util.mvc.Toggle;

/**
 * Controller for the {@link RuntimeViewer}.
 */
public class RuntimeController extends Controller implements SettingChangedListener {
	
	private static Localization localization = Localization.getLocalization(RuntimeController.class);
	
	/**
	 * Enumeration of plot fit options.
	 */
	public enum FitMode {
		
		/**
		 * Fit the plot to the initial bounds of the series.
		 */
		InitialBounds,
		
		/**
		 * Fit the plot to the bounds of the reference set.
		 */
		ReferenceSetBounds,
		
		/**
		 * Fit the plot using the bounds of the data currently displayed in the plot.
		 */
		DynamicBounds,
		
		/**
		 * Fit the plot based on the zoom, which is set when the user selects a region using the mouse cursor.
		 */
		Zoom
		
	}
	
	private final Setting<Integer> pointSize;
	
	private final Setting<Integer> pointTransparency;
	
	private final Setting<FitMode> fitMode;
	
	private final Toggle showReferenceSet;
			
	private final List<PlotSeries> data;
	
	private PlotSeries referenceSet;
	
	private int currentIndex;
	
	private int startingIndex;
	
	private int endingIndex;
	
	private int stepSize;
	
	private IndexType indexType;
	
	private Timer playbackTimer;
	
	/**
	 * Constructs a new controller with no data.
	 * 
	 * @param viewer the viewer
	 */
	public RuntimeController(RuntimeViewer viewer) {
		super(viewer);
		
		data = new ArrayList<>();
		
		pointSize = new Setting<>(8);
		pointTransparency = new Setting<>(0);
		fitMode = new Setting<>(FitMode.InitialBounds);
		showReferenceSet = new Toggle(true);
		
		pointSize.addSettingChangedListener(this);
		pointTransparency.addSettingChangedListener(this);
		fitMode.addSettingChangedListener(this);
		showReferenceSet.addSettingChangedListener(this);
		
		addShutdownHook(this::stop);
	}

	/**
	 * Returns the setting for the chart's point size, a value greater than {@code 0}.
	 * 
	 * @return the point size setting
	 */
	public Setting<Integer> getPointSize() {
		return pointSize;
	}
	
	/**
	 * Returns the setting for the chart's transparency, a value between {@code 0} and {@code 100}.
	 * 
	 * @return the point transparency setting
	 */
	public Setting<Integer> getPointTransparency() {
		return pointTransparency;
	}

	/**
	 * Returns the setting for the chart's fit or scaling mode.
	 * 
	 * @return the fit mode setting
	 */
	public Setting<FitMode> getFitMode() {
		return fitMode;
	}
	
	/**
	 * Returns the toggle for optionally displaying the reference set.
	 * 
	 * @return the reference set toggle
	 */
	public Toggle getShowReferenceSet() {
		return showReferenceSet;
	}
	
	/**
	 * Sets or updates the reference set displayed in the plot.
	 * 
	 * @param population the reference set population
	 */
	public void setReferenceSet(NondominatedPopulation population) {
		referenceSet = new PlotSeries(localization.getString("text.referenceSet"), ResultSeries.of(population));
		updateModel();
	}
	
	/**
	 * Returns the reference set.
	 * 
	 * @return the reference set
	 */
	public PlotSeries getReferenceSet() {
		return referenceSet;
	}
	
	/**
	 * Adds a series to the plot.
	 * 
	 * @param name the name of the series
	 * @param series the series itself
	 */
	public void addSeries(String name, ResultSeries series) {
		data.add(new PlotSeries(name, series));
		updateModel();
	}
	
	/**
	 * Removes a series from the plot.
	 * 
	 * @param index the index of the series to remove
	 */
	public void removeSeries(int index) {
		data.remove(index);
		updateModel();
	}
	
	/**
	 * Returns all series included in the plot.
	 * 
	 * @return all series
	 */
	public List<PlotSeries> getSeries() {
		return data;
	}
	
	/**
	 * Returns the starting or minimum index value.  This value is derived from the individual series and can change
	 * when the model is updated.
	 * 
	 * @return the starting index
	 */
	public int getStartingIndex() {
		return startingIndex;
	}
	
	/**
	 * Returns the ending or maximum index value.  This value is derived from the individual series and can change
	 * when the model is updated.
	 * 
	 * @return the starting index
	 */
	public int getEndingIndex() {
		return endingIndex;
	}
	
	/**
	 * Returns the step size for iterating over indices.  This value is derived from the individual series and can
	 * change when the model is updated.
	 * 
	 * @return the step size
	 */
	public int getStepSize() {
		return stepSize;
	}
	
	/**
	 * Returns the index type.  This value is derived from the individual series and can change when the model is
	 * updated.
	 * 
	 * @return the index type
	 */
	public IndexType getIndexType() {
		return indexType;
	}
	
	/**
	 * Returns the current index being displayed.
	 * 
	 * @return the current index
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	/**
	 * Sets the current index to display.
	 * 
	 * @param index the new index
	 */
	public void setCurrentIndex(int index) {
		if (this.currentIndex != index) {
			this.currentIndex = index;
			fireEvent("viewChanged");
		}
	}
	
	/**
	 * Updates the derived values and triggers a {@code "modelChanged"} event.
	 */
	public void updateModel() {
		if (data.size() == 0) {
			startingIndex = 0;
			endingIndex = 0;
			stepSize = 0;
			indexType = IndexType.NFE;
		} else {
			startingIndex = data.get(0).getSeries().getStartingIndex();
			endingIndex = data.get(0).getSeries().getEndingIndex();
			stepSize = data.get(0).getStepSize();
			indexType = data.get(0).getSeries().getIndexType();
			
			for (int i = 1; i < data.size(); i++) {
				startingIndex = Math.min(startingIndex, data.get(i).getSeries().getStartingIndex());
				endingIndex = Math.max(endingIndex, data.get(i).getSeries().getEndingIndex());
				stepSize = Math.min(stepSize, data.get(i).getStepSize());
				
				if (!data.get(i).getSeries().getIndexType().equals(indexType)) {
					indexType = IndexType.Index;
				}
			}
		}
		
		if (currentIndex < startingIndex) {
			currentIndex = startingIndex;
		}
		
		if (currentIndex > endingIndex) {
			currentIndex = endingIndex;
		}
		
		fireEvent("modelChanged");
	}
	
	/**
	 * Starts the playback timer, which iterates through the indices displayed in the plot.  Has no effect if playback
	 * is already started.
	 */
	public void play() {
		if (playbackTimer != null) {
			return;
		}
		
		playbackTimer = new Timer(100, e -> {
			int nextIndex = getCurrentIndex() + getStepSize();
			
			if (nextIndex > endingIndex) {
				nextIndex = startingIndex;
			}
			
			setCurrentIndex(nextIndex);
		});
		
		playbackTimer.start();
		fireEvent("stateChanged");
	}
	
	/**
	 * Stops the playback timer.
	 */
	public void stop() {
		if (playbackTimer != null) {
			playbackTimer.stop();
			playbackTimer = null;
			fireEvent("stateChanged");
		}
	}
	
	/**
	 * Returns {@code true} if playback is running; {@code false} otherwise.
	 * 
	 * @return {@code true} if playback is running; {@code false} otherwise
	 */
	public boolean isRunning() {
		return playbackTimer != null && playbackTimer.isRunning();
	}

	@Override
	public void settingChanged(SettingChangedEvent event) {
		fireEvent("viewChanged");
	}

}
