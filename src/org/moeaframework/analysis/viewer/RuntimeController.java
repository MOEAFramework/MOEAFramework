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

import org.moeaframework.analysis.runtime.Observations;
import org.moeaframework.analysis.viewer.RuntimeSeries.IndexType;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.Localization;
import org.moeaframework.util.mvc.Controller;
import org.moeaframework.util.mvc.Setting;
import org.moeaframework.util.mvc.SettingChangedEvent;
import org.moeaframework.util.mvc.SettingChangedListener;
import org.moeaframework.util.mvc.Toggle;

public class RuntimeController extends Controller implements SettingChangedListener {
	
	private static Localization localization = Localization.getLocalization(RuntimeController.class);
	
	public enum FitMode {
		
		InitialBounds,
		ReferenceSetBounds,
		DynamicBounds,
		Zoom
		
	}
	
	private final Setting<Integer> pointSize;
	
	private final Setting<Integer> pointTransparency;
	
	private final Setting<FitMode> fitMode;
	
	private final Toggle showReferenceSet;
			
	private final List<RuntimeSeries> approximationSets;
	
	private RuntimeSeries referenceSet;
	
	private int currentIndex;
	
	private int startingIndex;
	
	private int endingIndex;
	
	private int stepSize;
	
	private IndexType indexType;
	
	private Timer playbackTimer;
	
	public RuntimeController(RuntimeViewer viewer) {
		super(viewer);
		
		approximationSets = new ArrayList<>();
		
		pointSize = new Setting<>(8);
		pointTransparency = new Setting<>(0);
		fitMode = new Setting<>(FitMode.InitialBounds);
		showReferenceSet = new Toggle(true);
		
		pointSize.addSettingChangedListener(this);
		pointTransparency.addSettingChangedListener(this);
		fitMode.addSettingChangedListener(this);
		showReferenceSet.addSettingChangedListener(this);
		
		addShutdownHook(() -> stop());
	}

	public Setting<Integer> getPointSize() {
		return pointSize;
	}
	
	public Setting<Integer> getPointTransparency() {
		return pointTransparency;
	}

	public Setting<FitMode> getFitMode() {
		return fitMode;
	}
	
	public Toggle getShowReferenceSet() {
		return showReferenceSet;
	}
	
	public void setReferenceSet(NondominatedPopulation population) {
		referenceSet = RuntimeSeries.of(localization.getString("text.referenceSet"), population);
		updateModel();
	}
	
	public RuntimeSeries getReferenceSet() {
		return referenceSet;
	}
	
	public void addSeries(String name, Observations observations) {
		addSeries(RuntimeSeries.of(name, observations));
	}
	
	public void addSeries(RuntimeSeries series) {
		approximationSets.add(series);
		updateModel();
	}
	
	public void removeSeries(int index) {
		approximationSets.remove(index);
		updateModel();
	}
	
	public void removeSeries(RuntimeSeries series) {
		approximationSets.remove(series);
		updateModel();
	}
	
	public List<RuntimeSeries> getSeries() {
		return approximationSets;
	}
	
	public int getStartingIndex() {
		return startingIndex;
	}
	
	public int getEndingIndex() {
		return endingIndex;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public void setCurrentIndex(int index) {
		if (this.currentIndex != index) {
			this.currentIndex = index;
			fireEvent("viewChanged");
		}
	}
	
	public int getStepSize() {
		return stepSize;
	}
	
	public IndexType getIndexType() {
		return indexType;
	}
	
	public void updateModel() {
		if (approximationSets.size() == 0) {
			startingIndex = 0;
			endingIndex = 0;
			stepSize = 0;
			indexType = IndexType.NFE;
		} else {
			startingIndex = approximationSets.get(0).getStartingIndex();
			endingIndex = approximationSets.get(0).getEndingIndex();
			indexType = approximationSets.get(0).getIndexType();
			stepSize = approximationSets.get(0).getStepSize();
			
			for (int i = 1; i < approximationSets.size(); i++) {
				startingIndex = Math.min(startingIndex, approximationSets.get(i).getStartingIndex());
				endingIndex = Math.max(endingIndex, approximationSets.get(i).getEndingIndex());
				stepSize = Math.min(stepSize, approximationSets.get(i).getStepSize());
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
	
	public void stop() {
		if (playbackTimer != null) {
			playbackTimer.stop();
			playbackTimer = null;
			fireEvent("stateChanged");
		}
	}
	
	public boolean isRunning() {
		return playbackTimer != null && playbackTimer.isRunning();
	}

	@Override
	public void settingChanged(SettingChangedEvent event) {
		fireEvent("viewChanged");
	}

}
