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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import org.jfree.ui.about.AboutDialog;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Counter;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.viewer.TextViewer;
import org.moeaframework.util.mvc.ControllerEvent;
import org.moeaframework.util.mvc.ControllerListener;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class DiagnosticToolTest {
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	private Counter<String> controllerStateCounter = new Counter<String>();
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@Test
	public void testWithDisplay() throws InterruptedException, InvocationTargetException {
		// Render the UI and display plots to check for any errors
		DiagnosticTool tool = runTest();
		tool.setVisible(true);
		tool.selectAllMetrics();
		tool.selectAllResults();
		tool.getController().showLastTrace().set(true);
		tool.getController().showIndividualTraces().set(true);
		
		TextViewer viewer = tool.getController().showStatistics();
		viewer.dispose();
		
		AboutDialog dialog = tool.showAbout();
		dialog.dispose();
		
		tool.dispose();
	}
	
	@Test
	public void testWithoutDisplay() throws InterruptedException, InvocationTargetException {
		runTest();
	}
	
	@Test
	public void testSaveLoadData() throws IOException, InterruptedException, InvocationTargetException {
		DiagnosticTool tool = runTest();
		
		File tempFile = TempFiles.createFile();
		tool.getController().saveData(tempFile);
		tool.getController().loadData(tempFile);
	}
	
	public DiagnosticTool runTest() throws InterruptedException, InvocationTargetException {
		DiagnosticTool tool = new DiagnosticTool();
		DiagnosticToolController controller = tool.getController();
		
		// Wait for event queue to clear before tracking events
		SwingUtilities.invokeAndWait(() -> {});
		
		controller.addControllerListener(new ControllerListener() {

			@Override
			public void controllerStateChanged(ControllerEvent event) {
				controllerStateCounter.incrementAndGet(event.getEventType());
				
				if (event.getEventType().equals("stateChanged")) {
					// State changes should only occur when a run starts or finishes
					Assert.assertNotEquals(isRunning.get(), controller.isRunning());
					isRunning.set(controller.isRunning());
				}
			}
			
		});
		
		tool.setAlgorithm("NSGAII");
		tool.setProblem("DTLZ2_2");
		tool.setNumberOfEvaluations(1000);
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
		
		Assert.assertEquals(0, controllerStateCounter.get("settingsChanged"));
		Assert.assertEquals(2, controllerStateCounter.get("stateChanged"));
		Assert.assertGreaterThanOrEqual(controllerStateCounter.get("modelChanged"), 5);
		Assert.assertGreaterThanOrEqual(controllerStateCounter.get("viewChanged"), 5);
		Assert.assertEquals(56, controllerStateCounter.get("progressChanged")); // 11 per seed * 5 seeds + 1 final update

		Assert.assertEquals(100, controller.getOverallProgress());
		Assert.assertEquals(100, controller.getRunProgress());

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
	public void testLocalization() {
		Assert.assertLocalized(new DiagnosticTool(), Assert::isLocalized);
	}

}
