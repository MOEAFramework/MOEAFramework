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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

import org.moeaframework.analysis.runtime.Observations;
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
	
	private int currentNFE;
	
	private int maximumNFE;
	
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
		referenceSet = new RuntimeSeries(localization.getString("text.referenceSet"));
		referenceSet.add(0, population);
		updateModel();
	}
	
	public RuntimeSeries getReferenceSet() {
		return referenceSet;
	}
	
	public void addSeries(String name, Observations observations) {
		addSeries(RuntimeSeries.of(name, observations));
	}
	
	public void addSeries(RuntimeSeries approximationSet) {
		approximationSets.add(approximationSet);
		updateModel();
	}
	
	public List<RuntimeSeries> getSeries() {
		return approximationSets;
	}
	
	public int getMaximumNFE() {
		return maximumNFE;
	}
	
	public int getCurrentNFE() {
		return currentNFE;
	}
	
	public void setCurrentNFE(int currentNFE) {
		this.currentNFE = currentNFE;
		fireEvent("viewChanged");
	}
	
	public void updateModel() {
		maximumNFE = 0;
		
		for (RuntimeSeries approximationSet : approximationSets) {
			maximumNFE = Math.max(maximumNFE, approximationSet.last().getKey());
		}
		
		fireEvent("modelChanged");
	}
	
	public void play() {
		if (playbackTimer != null) {
			return;
		}
		
		playbackTimer = new Timer(100, e -> {
			int nextNFE = getCurrentNFE() + 100;
			
			if (nextNFE > maximumNFE) {
				nextNFE = 0;
			}
			
			setCurrentNFE(nextNFE);
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
