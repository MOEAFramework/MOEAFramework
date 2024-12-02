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
import org.junit.After;
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
	
	private DiagnosticTool tool;
	
	private DiagnosticToolController controller;
	
	private AtomicBoolean isRunning;
	
	private Counter<String> controllerStateCounter;
	
	@Before
	public void setUp() {
		isRunning = new AtomicBoolean(false);
		controllerStateCounter = new Counter<String>();
	}
	
	@After
	public void tearDown() {
		tool = null;
		controller = null;
	}
	
	@Test
	public void testDisplay() throws InterruptedException, InvocationTargetException {
		Assume.assumeHasDisplay();
		
		// Render the UI and display plots to check for any errors
		runTest("NSGAII", "DTLZ2_2", 1000, 5);
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
	public void test() throws InterruptedException, InvocationTargetException {
		runTest("NSGAII", "DTLZ2_2", 1000, 5);
		assertEvents(5);
		assertResult("NSGAII", "DTLZ2_2", 5);
	}
	
	@Test
	public void testJMetal() throws InterruptedException, InvocationTargetException {
		Assume.assumeJMetalExists();
		runTest("NSGAII-JMetal", "DTLZ2_2", 1000, 5);
		assertResult("NSGAII-JMetal", "DTLZ2_2", 5);
	}
	
	@Test
	public void testSaveLoadData() throws IOException, InterruptedException, InvocationTargetException {
		runTest("NSGAII", "DTLZ2_2", 1000, 5);
		
		File tempFile = TempFiles.createFile();
		tool.getController().saveData(tempFile);
		tool.getController().loadData(tempFile);
	}
	
	public void runTest(String algorithmName, String problemName, int numberOfEvaluations, int numberOfSeeds)
			throws InterruptedException, InvocationTargetException {
		tool = new DiagnosticTool();
		controller = tool.getController();
		
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
		
		tool.setAlgorithm(algorithmName);
		tool.setProblem(problemName);
		tool.setNumberOfEvaluations(numberOfEvaluations);
		tool.setNumberOfSeeds(numberOfSeeds);
		
		Assert.assertEquals(0, controller.getOverallProgress());
		Assert.assertEquals(0, controller.getRunProgress());
		Assert.assertNull(controller.getLastObservation());
		
		controller.run();
		controller.join();
		
		// Drain the event queue and check if finished
		SwingUtilities.invokeAndWait(() -> {});
		Assert.assertFalse(controller.isRunning());
		
		Assert.assertEquals(100, controller.getOverallProgress());
		Assert.assertEquals(100, controller.getRunProgress());
		Assert.assertNotNull(controller.getLastObservation());
	}
	
	private void assertEvents(int expectedSeeds) {
		Assert.assertEquals(0, controllerStateCounter.get("settingsChanged"));
		Assert.assertEquals(2, controllerStateCounter.get("stateChanged"));
		Assert.assertGreaterThanOrEqual(controllerStateCounter.get("modelChanged"), expectedSeeds);
		Assert.assertGreaterThanOrEqual(controllerStateCounter.get("viewChanged"), expectedSeeds);
		Assert.assertEquals(11 * expectedSeeds + 1, controllerStateCounter.get("progressChanged"));
	}
	
	private void assertResult(String algorithmName, String problemName, int expectedSeeds) {
		ResultKey key = new ResultKey(algorithmName, problemName);
		Assert.assertNotNull(controller.get(key));
		Assert.assertSize(expectedSeeds, controller.get(key));

		for (int i = 0; i < expectedSeeds; i++) {
			Assert.assertTrue(controller.get(key).get(i).keys().contains("Hypervolume"));
			Assert.assertTrue(controller.get(key).get(i).keys().contains("Approximation Set"));
		}

		Assert.assertContains(tool.getSelectedResults(), key);
	}
	
	@Test
	public void testLocalization() {
		Assert.assertLocalized(new DiagnosticTool(), Assert::isLocalized);
	}

}
