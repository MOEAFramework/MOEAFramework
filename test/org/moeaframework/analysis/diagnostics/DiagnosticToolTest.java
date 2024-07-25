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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.swing.Action;

import org.jfree.ui.about.AboutDialog;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.TempFiles;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class DiagnosticToolTest {
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	private AtomicInteger settingsChangedCount = new AtomicInteger();
	
	private AtomicInteger stateChangedCount = new AtomicInteger();
	
	private AtomicInteger viewChangedCount = new AtomicInteger();
	
	private AtomicInteger modelChangedCount = new AtomicInteger();
	
	private AtomicInteger progressChangedCount = new AtomicInteger();
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@Test
	public void testWithDisplay() throws InterruptedException {
		// Render the UI and display plots to check for any errors
		DiagnosticTool tool = runTest();
		tool.setVisible(true);
		tool.selectAllMetrics();
		tool.selectAllResults();
		tool.getController().setShowLastTrace(true);
		tool.getController().setShowIndividualTraces(true);
		
		StatisticalResultsViewer viewer = tool.getController().showStatistics();
		viewer.dispose();
		
		AboutDialog dialog = tool.showAbout();
		dialog.dispose();
		
		tool.dispose();
	}
	
	@Test
	public void testWithoutDisplay() throws InterruptedException {
		runTest();
	}
	
	@Test
	public void testSaveLoadData() throws IOException, InterruptedException {
		DiagnosticTool tool = runTest();
		
		File tempFile = TempFiles.createFile();
		tool.getController().saveData(tempFile);
		tool.getController().loadData(tempFile);
	}
	
	public DiagnosticTool runTest() throws InterruptedException {
		DiagnosticTool tool = new DiagnosticTool();
		Controller controller = tool.getController();
		
		controller.addControllerListener(new ControllerListener() {

			@Override
			public void controllerStateChanged(ControllerEvent event) {
				switch (event.getType()) {
					case STATE_CHANGED -> {
						stateChangedCount.incrementAndGet();

						// State changes should only occur when a run starts or finishes
						Assert.assertNotEquals(isRunning.get(), event.getSource().isRunning());
						isRunning.set(event.getSource().isRunning());
					}
					case MODEL_CHANGED -> modelChangedCount.incrementAndGet();
					case VIEW_CHANGED -> viewChangedCount.incrementAndGet();
					case PROGRESS_CHANGED -> progressChangedCount.incrementAndGet();
					case SETTINGS_CHANGED -> settingsChangedCount.incrementAndGet();
					default -> Assert.fail("Unexpected controller event type " + event.getType());
				}
			}
			
		});
		
		tool.setAlgorithm("NSGAII");
		tool.setProblem("DTLZ2_2");
		tool.setNumberOfEvaluations(10000);
		tool.setNumberOfSeeds(5);
		
		Assert.assertEquals(0, controller.getOverallProgress());
		Assert.assertEquals(0, controller.getRunProgress());
		Assert.assertNull(controller.getLastObservation());
		
		controller.run();
		controller.join();
		
		// Block until the events signal the run finishes, as events can be sent after the thread completes
		while (isRunning.get()) {
			Thread.yield();
		}
		
		Assert.assertEquals(0, settingsChangedCount.get());
		Assert.assertEquals(2, stateChangedCount.get());
		Assert.assertEquals(5, modelChangedCount.get());
		Assert.assertGreaterThanOrEqual(viewChangedCount.get(), 5);
		Assert.assertEquals(506, progressChangedCount.get()); // 101 per seed * 5 seeds + 1 final update
		
		Assert.assertEquals(100, controller.getOverallProgress());
		Assert.assertEquals(0, controller.getRunProgress());
		
		ResultKey key = new ResultKey("NSGAII", "DTLZ2_2");
		Assert.assertNotNull(controller.get(key));
		Assert.assertSize(5, controller.get(key));
		
		for (int i = 0; i < 5; i++) {
			Assert.assertTrue(controller.get(key).get(i).keys().contains("Hypervolume"));
			Assert.assertTrue(controller.get(key).get(i).keys().contains("Population Size"));
			Assert.assertTrue(controller.get(key).get(i).keys().contains("Approximation Set"));
		}
		
		Assert.assertContains(tool.getSelectedResults(), key);
		Assert.assertNotNull(controller.getLastObservation());
		
		return tool;
	}
	
	@Test
	public void testToggleActions() {
		DiagnosticTool tool = new DiagnosticTool();
		Controller controller = tool.getController();
		ActionFactory actionFactory = tool.getActionFactory();
		
		testToggleAction(controller, actionFactory.getIncludeHypervolumeAction(), c -> c.getIncludeHypervolume());
		testToggleAction(controller, actionFactory.getIncludeGenerationalDistanceAction(), c -> c.getIncludeGenerationalDistance());
		testToggleAction(controller, actionFactory.getIncludeGenerationalDistancePlusAction(), c -> c.getIncludeGenerationalDistancePlus());
		testToggleAction(controller, actionFactory.getIncludeInvertedGenerationalDistanceAction(), c -> c.getIncludeInvertedGenerationalDistance());
		testToggleAction(controller, actionFactory.getIncludeInvertedGenerationalDistancePlusAction(), c -> c.getIncludeInvertedGenerationalDistancePlus());
		testToggleAction(controller, actionFactory.getIncludeContributionAction(), c -> c.getIncludeContribution());
		testToggleAction(controller, actionFactory.getIncludeSpacingAction(), c -> c.getIncludeSpacing());
		testToggleAction(controller, actionFactory.getIncludeAdditiveEpsilonIndicatorAction(), c -> c.getIncludeAdditiveEpsilonIndicator());
		testToggleAction(controller, actionFactory.getIncludeR1Action(), c -> c.getIncludeR1());
		testToggleAction(controller, actionFactory.getIncludeR2Action(), c -> c.getIncludeR2());
		testToggleAction(controller, actionFactory.getIncludeR3Action(), c -> c.getIncludeR3());
		testToggleAction(controller, actionFactory.getIncludeEpsilonProgressAction(), c -> c.getIncludeEpsilonProgress());
		testToggleAction(controller, actionFactory.getIncludeAdaptiveMultimethodVariationAction(), c -> c.getIncludeAdaptiveMultimethodVariation());
		testToggleAction(controller, actionFactory.getIncludeAdaptiveTimeContinuationAction(), c -> c.getIncludeAdaptiveTimeContinuation());
		testToggleAction(controller, actionFactory.getIncludeElapsedTimeAction(), c -> c.getIncludeElapsedTime());
		testToggleAction(controller, actionFactory.getIncludeApproximationSetAction(), c -> c.getIncludeApproximationSet());
		testToggleAction(controller, actionFactory.getIncludePopulationSizeAction(), c -> c.getIncludePopulationSize());
		testToggleAction(controller, actionFactory.getShowIndividualTracesAction(), c -> c.getShowIndividualTraces());
		testToggleAction(controller, actionFactory.getShowLastTraceAction(), c -> c.getShowLastTrace());
	}
	
	@Test
	public void testLocalization() {
		Assert.assertLocalized(new DiagnosticTool(), Assert::isLocalized);
	}

	private void testToggleAction(Controller controller, Action action, Function<Controller, Boolean> reader) {
		Boolean originalValue = reader.apply(controller);
		Assert.assertEquals(originalValue, action.getValue(Action.SELECTED_KEY));
		
		action.putValue(Action.SELECTED_KEY, !originalValue.booleanValue());
		action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, "click"));
		Assert.assertEquals(!originalValue, reader.apply(controller));
	}

}
