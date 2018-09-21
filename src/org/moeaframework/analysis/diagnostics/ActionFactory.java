/* Copyright 2009-2018 David Hadka
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
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.base.Library;
import org.jfree.ui.about.AboutDialog;
import org.jfree.ui.about.ProjectInfo;
import org.moeaframework.Instrumenter;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;
import org.moeaframework.util.Localization;

/**
 * Collection of actions used by the diagnostic tool.
 */
public class ActionFactory implements ControllerListener {
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			ActionFactory.class);
	
	/**
	 * The file extension.
	 */
	private static String EXTENSION = 
			"." + localization.getString("file.extension").toLowerCase();

	/**
	 * The file filter used when selecting the file to save/load.
	 */
	private static FileFilter FILTER = new FileNameExtensionFilter(
			localization.getString("file.extension.description"),
			localization.getString("file.extension"));
	
	/**
	 * The {@code Controller} instance on which these actions operate.
	 */
	private final Controller controller;
	
	/**
	 * The {@code DiagnosticTool} instance on which these actions operate.
	 */
	private final DiagnosticTool frame;

	/**
	 * The action to save the results to a file.
	 */
	private Action saveAction;
	
	/**
	 * The action to load results from a file.
	 */
	private Action loadAction;
	
	/**
	 * The action to close the diagnostic tool.
	 */
	private Action exitAction;
	
	/**
	 * The action to toggle the display of the last run's trace.
	 */
	private Action showLastTraceAction;
	
	/**
	 * The action to toggle on all indicator collectors.
	 */
	private Action enableAllIndicatorsAction;
	
	/**
	 * The action to toggle off all indicator collectors.
	 */
	private Action disableAllIndicatorsAction;
	
	/**
	 * The action to toggle the inclusion of the hypervolume indicator
	 * collector.
	 */
	private Action includeHypervolumeAction;
	
	/**
	 * The action to toggle the inclusion of the generational distance
	 * indicator collector.
	 */
	private Action includeGenerationalDistanceAction;
	
	/**
	 * The action to toggle the inclusion of the inverted generational distance
	 * indicator collector.
	 */
	private Action includeInvertedGenerationalDistanceAction;
	
	/**
	 * The action to toggle the inclusion the spacing indicator collector.
	 */
	private Action includeSpacingAction;
	
	/**
	 * The action to toggle the inclusion of the additive &epsilon;-indicator
	 * collector.
	 */
	private Action includeAdditiveEpsilonIndicatorAction;
	
	/**
	 * The action to toggle the inclusion of the contribution indicator
	 * collector.
	 */
	private Action includeContributionAction;
	
	/**
	 * The action to toggle the inclusion of the R1 indicator collector.
	 */
	private Action includeR1Action;
	
	/**
	 * The action to toggle the inclusion of the R2 indicator collector.
	 */
	private Action includeR2Action;
	
	/**
	 * The action to toggle the inclusion of the R3 indicator collector.
	 */
	private Action includeR3Action;
	
	/**
	 * The action to toggle the inclusion of &epsilon;-progress restart
	 * collector.
	 */
	private Action includeEpsilonProgressAction;
	
	/**
	 * The action to toggle the inclusion of the adaptive multimethod variation
	 * collector.
	 */
	private Action includeAdaptiveMultimethodVariationAction;
	
	/**
	 * The action to toggle the inclusion of the adaptive time continuation
	 * collector.
	 */
	private Action includeAdaptiveTimeContinuationAction;
	
	/**
	 * The action to toggle the inclusion of the elapsed time collector.
	 */
	private Action includeElapsedTimeAction;
	
	/**
	 * The action to toggle the inclusion of the population size collector.
	 */
	private Action includePopulationSizeAction;
	
	/**
	 * The action to toggle the inclusion of the approximation set collector.
	 */
	private Action includeApproximationSetAction;
	
	/**
	 * The action for displaying memory usage.
	 */
	private Action memoryUsageAction;
	
	/**
	 * The action for starting the evaluation task.
	 */
	private Action runAction;
	
	/**
	 * The action for canceling a running evaluation task.
	 */
	private Action cancelAction;
	
	/**
	 * The action for clearing all results.
	 */
	private Action clearAction;
	
	/**
	 * The action for showing a statistical comparison of the results.
	 */
	private Action showStatisticsAction;
	
	/**
	 * The action for displaying the about dialog.
	 */
	private Action aboutDialogAction;
	
	/**
	 * The action for showing individual traces in the line plots.
	 */
	private Action showIndividualTracesAction;
	
	/**
	 * The action for showing 25%, 50% and 75% quantiles in the line plots.
	 */
	private Action showQuantilesAction;
	
	/**
	 * Constructs a new action factory.
	 * 
	 * @param frame the {@code DiagnosticTool} instance on which these actions
	 *        operate
	 * @param controller the {@code Controller} instance on which these actions
	 *        operate
	 */
	public ActionFactory(DiagnosticTool frame, Controller controller) {
		super();
		this.frame = frame;
		this.controller = controller;
		
		initialize();
		
		controller.addControllerListener(this);
	}
	
	/**
	 * Initializes the actions used by this action factory.
	 */
	protected void initialize() {
		saveAction = new AbstractAction() {

			private static final long serialVersionUID = -1909996187887919230L;
			
			{
				putValue(Action.NAME, localization.getString("action.save.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.save.description"));
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(FILTER);
				
				int result = fileChooser.showSaveDialog(frame);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
						
					if (!file.getName().toLowerCase().endsWith(EXTENSION)) {
						file = new File(file.getParent(), file.getName() + 
								EXTENSION);
					}
					
					try {
						controller.saveData(file);
					} catch (IOException e) {
						controller.handleException(e);
					}
				}
			}
			
		};
		
		loadAction = new AbstractAction() {

			private static final long serialVersionUID = 6667076082827906472L;
			
			{
				putValue(Action.NAME, localization.getString("action.load.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.load.description"));
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(FILTER);
				
				int result = fileChooser.showOpenDialog(frame);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadData(fileChooser.getSelectedFile());
					} catch (IOException e) {
						controller.handleException(e);
					}
				}
			}
			
		};
		
		exitAction = new AbstractAction() {

			private static final long serialVersionUID = -8388268233198826720L;
			
			{
				putValue(Action.NAME, localization.getString("action.exit.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.exit.description"));
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
			}
			
		};
		
		showLastTraceAction = new AbstractAction() {

			private static final long serialVersionUID = -6068811236087074314L;
			
			{
				putValue(Action.NAME, localization.getString("action.showLastTrace.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.showLastTrace.description"));
				putValue(Action.SELECTED_KEY, controller.getShowLastTrace());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setShowLastTrace((Boolean)getValue(Action.SELECTED_KEY));
			}
			
		};
		
		enableAllIndicatorsAction = new AbstractAction() {

			private static final long serialVersionUID = -6068811236087074314L;
			
			{
				putValue(Action.NAME, localization.getString("action.enableAllIndicators.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.enableAllIndicators.description"));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				includeHypervolumeAction.putValue(Action.SELECTED_KEY, true);
				includeGenerationalDistanceAction.putValue(Action.SELECTED_KEY, true);
				includeInvertedGenerationalDistanceAction.putValue(Action.SELECTED_KEY, true);
				includeSpacingAction.putValue(Action.SELECTED_KEY, true);
				includeAdditiveEpsilonIndicatorAction.putValue(Action.SELECTED_KEY, true);
				includeContributionAction.putValue(Action.SELECTED_KEY, true);
				includeR1Action.putValue(Action.SELECTED_KEY, true);
				includeR2Action.putValue(Action.SELECTED_KEY, true);
				includeR3Action.putValue(Action.SELECTED_KEY, true);
				
				controller.setIncludeHypervolume(true);
				controller.setIncludeGenerationalDistance(true);
				controller.setIncludeInvertedGenerationalDistance(true);
				controller.setIncludeSpacing(true);
				controller.setIncludeAdditiveEpsilonIndicator(true);
				controller.setIncludeContribution(true);
				controller.setIncludeR1(true);
				controller.setIncludeR2(true);
				controller.setIncludeR3(true);
			}
			
		};
		
		disableAllIndicatorsAction = new AbstractAction() {

			private static final long serialVersionUID = 5291581694356532809L;
			
			{
				putValue(Action.NAME, localization.getString("action.disableAllIndicators.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.disableAllIndicators.description"));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				includeHypervolumeAction.putValue(Action.SELECTED_KEY, false);
				includeGenerationalDistanceAction.putValue(Action.SELECTED_KEY, false);
				includeInvertedGenerationalDistanceAction.putValue(Action.SELECTED_KEY, false);
				includeSpacingAction.putValue(Action.SELECTED_KEY, false);
				includeAdditiveEpsilonIndicatorAction.putValue(Action.SELECTED_KEY, false);
				includeContributionAction.putValue(Action.SELECTED_KEY, false);
				includeR1Action.putValue(Action.SELECTED_KEY, false);
				includeR2Action.putValue(Action.SELECTED_KEY, false);
				includeR3Action.putValue(Action.SELECTED_KEY, false);
				
				controller.setIncludeHypervolume(false);
				controller.setIncludeGenerationalDistance(false);
				controller.setIncludeInvertedGenerationalDistance(false);
				controller.setIncludeSpacing(false);
				controller.setIncludeAdditiveEpsilonIndicator(false);
				controller.setIncludeContribution(false);
				controller.setIncludeR1(false);
				controller.setIncludeR2(false);
				controller.setIncludeR3(false);
			}
			
		};
		
		includeHypervolumeAction = new AbstractAction() {

			private static final long serialVersionUID = -8388268233198826720L;

			{
				putValue(Action.NAME, localization.getString("action.includeHypervolume.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeHypervolume.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeHypervolume());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeHypervolume((Boolean)getValue(Action.SELECTED_KEY));
			}
			
		};
		
		includeGenerationalDistanceAction = new AbstractAction() {

			private static final long serialVersionUID = 6577840439300886142L;

			{
				putValue(Action.NAME, localization.getString("action.includeGenerationalDistance.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeGenerationalDistance.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeGenerationalDistance());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeGenerationalDistance((Boolean)getValue(Action.SELECTED_KEY));
			}
			
		};
		
		includeInvertedGenerationalDistanceAction = new AbstractAction() {

			private static final long serialVersionUID = -4264252375261182056L;

			{
				putValue(Action.NAME, localization.getString("action.includeInvertedGenerationalDistance.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeInvertedGenerationalDistance.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeInvertedGenerationalDistance());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeInvertedGenerationalDistance((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeSpacingAction = new AbstractAction() {

			private static final long serialVersionUID = 3256132970071591253L;

			{
				putValue(Action.NAME, localization.getString("action.includeSpacing.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeSpacing.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeSpacing());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeSpacing((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeAdditiveEpsilonIndicatorAction = new AbstractAction() {

			private static final long serialVersionUID = -4612470190342088537L;

			{
				putValue(Action.NAME, localization.getString("action.includeAdditiveEpsilonIndicator.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeAdditiveEpsilonIndicator.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeAdditiveEpsilonIndicator());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeAdditiveEpsilonIndicator((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeContributionAction = new AbstractAction() {

			private static final long serialVersionUID = 7751303429555416136L;

			{
				putValue(Action.NAME, localization.getString("action.includeContribution.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeContribution.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeContribution());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeContribution((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeR1Action = new AbstractAction() {

			private static final long serialVersionUID = 7307447492866764644L;

			{
				putValue(Action.NAME, localization.getString("action.includeR1.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeR1.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeR1());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeR1((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeR2Action = new AbstractAction() {

			private static final long serialVersionUID = -5385083123364658233L;

			{
				putValue(Action.NAME, localization.getString("action.includeR2.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeR2.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeR2());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeR2((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeR3Action = new AbstractAction() {

			private static final long serialVersionUID = -2777143619264295330L;

			{
				putValue(Action.NAME, localization.getString("action.includeR3.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeR3.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeR3());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeR3((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeEpsilonProgressAction = new AbstractAction() {

			private static final long serialVersionUID = -2514670979923374486L;

			{
				putValue(Action.NAME, localization.getString("action.includeEpsilonProgress.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeEpsilonProgress.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeEpsilonProgress());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeEpsilonProgress((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeAdaptiveMultimethodVariationAction = new AbstractAction() {

			private static final long serialVersionUID = -2295024482426435226L;

			{
				putValue(Action.NAME, localization.getString("action.includeAdaptiveMultimethodVariation.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeAdaptiveMultimethodVariation.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeAdaptiveMultimethodVariation());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeAdaptiveMultimethodVariation((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeAdaptiveTimeContinuationAction = new AbstractAction() {

			private static final long serialVersionUID = 3178255679435336378L;

			{
				putValue(Action.NAME, localization.getString("action.includeAdaptiveTimeContinuation.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeAdaptiveTimeContinuation.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeAdaptiveTimeContinuation());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeAdaptiveTimeContinuation((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeElapsedTimeAction = new AbstractAction() {

			private static final long serialVersionUID = -664733245004881369L;

			{
				putValue(Action.NAME, localization.getString("action.includeElapsedTime.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeElapsedTime.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeElapsedTime());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeElapsedTime((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includePopulationSizeAction = new AbstractAction() {

			private static final long serialVersionUID = 567786863596776287L;

			{
				putValue(Action.NAME, localization.getString("action.includePopulationSize.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includePopulationSize.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludePopulationSize());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludePopulationSize((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		includeApproximationSetAction = new AbstractAction() {

			private static final long serialVersionUID = 567786863596776287L;

			{
				putValue(Action.NAME, localization.getString("action.includeApproximationSet.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.includeApproximationSet.description"));
				putValue(Action.SELECTED_KEY, controller.getIncludeApproximationSet());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setIncludeApproximationSet((Boolean)getValue(Action.SELECTED_KEY));
			}

		};
		
		runAction = new AbstractAction() {

			private static final long serialVersionUID = -3966834246075639069L;
			
			{
				putValue(Action.NAME, localization.getString("action.run.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.run.description"));
				setEnabled(!controller.isRunning());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.run();
			}
			
		};
		
		cancelAction = new AbstractAction() {

			private static final long serialVersionUID = 3035060554253471054L;
			
			{
				putValue(Action.NAME, localization.getString("action.cancel.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.cancel.description"));
				setEnabled(controller.isRunning());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.cancel();
			}
			
		};
		
		clearAction = new AbstractAction() {

			private static final long serialVersionUID = 3770122212031491835L;
			
			{
				putValue(Action.NAME, localization.getString("action.clear.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.clear.description"));
				setEnabled(!controller.isRunning());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.clear();
			}
			
		};
		
		showStatisticsAction = new AbstractAction() {

			private static final long serialVersionUID = 6836221261899470110L;
			
			{
				putValue(Action.NAME, localization.getString("action.showStatistics.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.showStatistics.description"));
				setEnabled(false);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showStatistics();
			}
			
		};
		
		aboutDialogAction = new AbstractAction() {

			private static final long serialVersionUID = -7768030811303579787L;
			
			{
				putValue(Action.NAME, localization.getString("action.about.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.about.description"));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Properties properties = new Properties();
					InputStream stream = null;
					
					try {
						stream = getClass().getResourceAsStream(
								"/META-INF/build.properties");
						properties.load(stream);
					} finally {
						if (stream != null) {
							stream.close();
						}
					}
					
					ProjectInfo info = new ProjectInfo(
							properties.getProperty("name"),
							properties.getProperty("version"), 
							properties.getProperty("description"),
							null,
							properties.getProperty("copyright"),
							null,
							loadLicense());
					
					info.addLibrary(new Library("Apache Commons CLI", "1.2", 
							"Apache License", null));
					info.addLibrary(new Library("Apache Commons Codec", "1.8", 
							"Apache License", null));
					info.addLibrary(new Library("Apache Commons Lang", "3.1",
							"Apache License", null));
					info.addLibrary(new Library("Apache Commons Math", "3.4.1", 
							"Apache License", null));
					info.addLibrary(new Library("JCommon", "1.0.20", "GNU LGPL",
							null));
					info.addLibrary(new Library("JFreeChart", "1.0.15", 
							"GNU LGPL", null));
					info.addLibrary(new Library("JMetal", "4.3", "GNU LGPL", 
							null));
					info.addLibrary(new Library("MOEAFramework", 
							properties.getProperty("version"), "GNU LGPL", 
							null));
						
					AboutDialog dialog = new AboutDialog(frame,
							localization.getString("title.about"),
							info);
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
				} catch (Exception ex) {
					controller.handleException(ex);
				}
			}
			
		};
		
		showIndividualTracesAction = new AbstractAction() {

			private static final long serialVersionUID = 7197923975477668385L;
			
			{
				putValue(Action.NAME, localization.getString("action.showIndividualTraces.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.showIndividualTraces.description"));
				putValue(Action.SELECTED_KEY, controller.getShowIndividualTraces());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setShowIndividualTraces(true);
			}
			
		};
		
		showQuantilesAction = new AbstractAction() {

			private static final long serialVersionUID = -7733483777432591099L;
			
			{
				putValue(Action.NAME, localization.getString("action.showQuantiles.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.showQuantiles.description"));
				putValue(Action.SELECTED_KEY, !controller.getShowIndividualTraces());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setShowIndividualTraces(false);
			}
			
		};
		
		memoryUsageAction = new AbstractAction() {

			private static final long serialVersionUID = -3966834246075639069L;
			
			{
				setEnabled(false);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				//do nothing
			}
			
		};
		
		final Timer timer = new Timer(1000, new ActionListener() {

			final double DIVISOR = 1024*1024;

			@Override
			public void actionPerformed(ActionEvent e) {
				long free = Runtime.getRuntime().freeMemory();
				long total = Runtime.getRuntime().totalMemory();
				long max = Runtime.getRuntime().maxMemory();
				double used = (total - free) / DIVISOR;
				double available = max / DIVISOR;

				memoryUsageAction.putValue(Action.NAME, 
						localization.getString("text.memory", used, available));
			}
			
		});
		
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();
		
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				timer.stop();
			}
			
		});
	}
	
	/**
	 * Loads the GNU LGPL license file and formats it for display.
	 * 
	 * @return the formatted GNU LGPL license
	 * @throws IOException if an I/O error occurred
	 */
	private String loadLicense() throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		String line = null;
		boolean isNewParagraph = false;
		
		try {
			reader = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/META-INF/LGPL-LICENSE")));
			
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				if (line.isEmpty()) {
					isNewParagraph = true;
				} else {
					if (isNewParagraph) {
						sb.append(Settings.NEW_LINE);
						sb.append(Settings.NEW_LINE);
					} else {
						sb.append(' ');
					}
					
					sb.append(line);
					isNewParagraph = false;
				}
			}
			
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Returns the action to save the results to a file.
	 * 
	 * @return the action to save the results to a file
	 */
	public Action getSaveAction() {
		return saveAction;
	}

	/**
	 * Returns the action to load results from a file.
	 * 
	 * @return the action to load results from a file
	 */
	public Action getLoadAction() {
		return loadAction;
	}

	/**
	 * Returns the action to close the diagnostic tool.
	 * 
	 * @return the action to close the diagnostic tool
	 */
	public Action getExitAction() {
		return exitAction;
	}
	
	/**
	 * Returns the action to show the last run's trace.
	 * 
	 * @return the action to show the last run's trace
	 */
	public Action getShowLastTraceAction() {
		return showLastTraceAction;
	}
	
	/**
	 * Returns the action to toggle on all indocator collectors.
	 * 
	 * @return the action to toggle on all indocator collectors
	 */
	public Action getEnableAllIndicatorsAction() {
		return enableAllIndicatorsAction;
	}
	
	/**
	 * Returns the action to toggle off all indocator collectors.
	 * 
	 * @return the action to toggle off all indocator collectors
	 */
	public Action getDisableAllIndicatorsAction() {
		return disableAllIndicatorsAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the hypervolume indicator
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the hypervolume indicator
	 *         collector
	 */
	public Action getIncludeHypervolumeAction() {
		return includeHypervolumeAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the generational distance
	 * indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the generational distance
	 *         indicator collector
	 */
	public Action getIncludeGenerationalDistanceAction() {
		return includeGenerationalDistanceAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the inverted generational
	 * distance indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the inverted generational
	 *         distance indicator collector
	 */
	public Action getIncludeInvertedGenerationalDistanceAction() {
		return includeInvertedGenerationalDistanceAction;
	}

	/**
	 * Returns the action to toggle the inclusion the spacing indicator 
	 * collector.
	 * 
	 * @return the action to toggle the inclusion the spacing indicator 
	 *         collector
	 */
	public Action getIncludeSpacingAction() {
		return includeSpacingAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the additive 
	 * &epsilon;-indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the additive 
	 *         &epsilon;-indicator collector
	 */
	public Action getIncludeAdditiveEpsilonIndicatorAction() {
		return includeAdditiveEpsilonIndicatorAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the contribution indicator
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the contribution indicator
	 *         collector
	 */
	public Action getIncludeContributionAction() {
		return includeContributionAction;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the R1 indicator
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the R1 indicator
	 *         collector
	 */
	public Action getIncludeR1Action() {
		return includeR1Action;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the R2 indicator
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the R2 indicator
	 *         collector
	 */
	public Action getIncludeR2Action() {
		return includeR2Action;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the R3 indicator
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the R3 indicator
	 *         collector
	 */
	public Action getIncludeR3Action() {
		return includeR3Action;
	}

	/**
	 * Returns the action to toggle the inclusion of &epsilon;-progress restart
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of &epsilon;-progress restart
	 *         collector
	 */
	public Action getIncludeEpsilonProgressAction() {
		return includeEpsilonProgressAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the adaptive multimethod
	 * variation collector.
	 * 
	 * @return the action to toggle the inclusion of the adaptive multimethod
	 *         variation collector
	 */
	public Action getIncludeAdaptiveMultimethodVariationAction() {
		return includeAdaptiveMultimethodVariationAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the adaptive time
	 * continuation collector.
	 * 
	 * @return the action to toggle the inclusion of the adaptive time
	 *         continuation collector
	 */
	public Action getIncludeAdaptiveTimeContinuationAction() {
		return includeAdaptiveTimeContinuationAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the elapsed time collector.
	 * 
	 * @return the action to toggle the inclusion of the elapsed time collector
	 */
	public Action getIncludeElapsedTimeAction() {
		return includeElapsedTimeAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the population size 
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the population size 
	 *         collector
	 */
	public Action getIncludePopulationSizeAction() {
		return includePopulationSizeAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the approximation set
	 * collector.
	 * 
	 * @return the action to toggle the inclusion of the approximation set
	 *         collector
	 */
	public Action getIncludeApproximationSetAction() {
		return includeApproximationSetAction;
	}

	/**
	 * Returns the action for displaying memory usage.
	 * 
	 * @return the action for displaying memory usage
	 */
	public Action getMemoryUsageAction() {
		return memoryUsageAction;
	}

	/**
	 * Returns the action for starting the evaluation task.
	 * 
	 * @return the action for starting the evaluation task
	 */
	public Action getRunAction() {
		return runAction;
	}

	/**
	 * Returns the action for canceling a running evaluation task.
	 * 
	 * @return the action for canceling a running evaluation task
	 */
	public Action getCancelAction() {
		return cancelAction;
	}

	/**
	 * Returns the action for clearing all results.
	 * 
	 * @return the action for clearing all results
	 */
	public Action getClearAction() {
		return clearAction;
	}

	/**
	 * Returns the action for showing a statistical comparison of the results.
	 * 
	 * @return the action for showing a statistical comparison of the results
	 */
	public Action getShowStatisticsAction() {
		return showStatisticsAction;
	}
	
	/**
	 * Returns the action for displaying the about dialog.
	 * 
	 * @return the action for displaying the about dialog
	 */
	public Action getAboutDialogAction() {
		return aboutDialogAction;
	}
	
	/**
	 * Returns the action for showing individual traces in the line plots.
	 * 
	 * @return the action for showing individual traces in the line plots
	 */
	public Action getShowIndividualTracesAction() {
		return showIndividualTracesAction;
	}
	
	/**
	 * Returns the action for showing quantiles in the line plots.
	 * 
	 * @return the action for showing quantiles in the line plots
	 */
	public Action getShowQuantilesAction() {
		return showQuantilesAction;
	}

	/**
	 * Returns the action to display the approximation set for the given result.
	 * 
	 * @param key the result key
	 * @return the action to display the approximation set for the given result
	 */
	public Action getShowApproximationSetAction(final ResultKey key) {
		return new AbstractAction() {

			private static final long serialVersionUID = 1680529848835103744L;
			
			{
				putValue(Action.NAME, localization.getString("action.showApproximationSet.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.showApproximationSet.description"));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				NondominatedPopulation referenceSet = null;
				
				try {
					Instrumenter instrumenter = new Instrumenter()
							.withProblem(key.getProblem());
					
					referenceSet = instrumenter.getReferenceSet();
				} catch (Exception ex) {
					//silently handle if no reference set is available
				}
				
				ApproximationSetViewer viewer = new ApproximationSetViewer(
						key.toString(),
						controller.get(key), 
						referenceSet);
				viewer.setLocationRelativeTo(frame);
				viewer.setIconImages(frame.getIconImages());
				viewer.setVisible(true);
			}
			
		};
	}
	
	/**
	 * Returns the action to select all items in the specified table.
	 * 
	 * @param table the table on which this action operates
	 * @return the action to select all items in the specified table
	 */
	public Action getSelectAllAction(final JTable table) {
		return new AbstractAction() {

			private static final long serialVersionUID = 8538384599545194314L;
			
			{
				putValue(Action.NAME, localization.getString("action.selectAll.name"));
				putValue(Action.SHORT_DESCRIPTION, localization.getString("action.selectAll.description"));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getModel().getRowCount() > 0) {
					table.getSelectionModel().setValueIsAdjusting(true);
					table.addRowSelectionInterval(0, 
							table.getModel().getRowCount()-1);
					table.getSelectionModel().setValueIsAdjusting(false);
				}
			}
			
		};
	}

	@Override
	public void controllerStateChanged(ControllerEvent event) {
		if (event.getType().equals(ControllerEvent.Type.STATE_CHANGED)) {
			getRunAction().setEnabled(!controller.isRunning());
			getCancelAction().setEnabled(controller.isRunning());
			getClearAction().setEnabled(!controller.isRunning());
		} else if (event.getType().equals(ControllerEvent.Type.VIEW_CHANGED) ||
				event.getType().equals(ControllerEvent.Type.MODEL_CHANGED)) {
			Set<String> problems = new HashSet<String>();
			Set<String> algorithms = new HashSet<String>();
			
			for (ResultKey key : frame.getSelectedResults()) {
				problems.add(key.getProblem());
				algorithms.add(key.getAlgorithm());
			}
			
			getShowStatisticsAction().setEnabled((problems.size() == 1) && 
					(algorithms.size() > 1));
		}
	}
	
}
