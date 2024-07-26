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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.moeaframework.Instrumenter;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.util.Localization;

/**
 * Collection of actions used by the diagnostic tool.
 */
public class ActionFactory {
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(ActionFactory.class);
	
	/**
	 * The file extension.
	 */
	private static String EXTENSION = localization.getString("file.extension");

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
	 * The action to toggle the inclusion of the hypervolume indicator collector.
	 */
	private Action includeHypervolumeAction;
	
	/**
	 * The action to toggle the inclusion of the generational distance indicator collector.
	 */
	private Action includeGenerationalDistanceAction;
	
	/**
	 * The action to toggle the inclusion of the generational distance plus indicator collector.
	 */
	private Action includeGenerationalDistancePlusAction;
	
	/**
	 * The action to toggle the inclusion of the inverted generational distance indicator collector.
	 */
	private Action includeInvertedGenerationalDistanceAction;
	
	/**
	 * The action to toggle the inclusion of the inverted generational distance plus indicator collector.
	 */
	private Action includeInvertedGenerationalDistancePlusAction;
	
	/**
	 * The action to toggle the inclusion the spacing indicator collector.
	 */
	private Action includeSpacingAction;
	
	/**
	 * The action to toggle the inclusion of the additive &epsilon;-indicator collector.
	 */
	private Action includeAdditiveEpsilonIndicatorAction;
	
	/**
	 * The action to toggle the inclusion of the contribution indicator collector.
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
	 * The action to toggle the inclusion of &epsilon;-progress restart collector.
	 */
	private Action includeEpsilonProgressAction;
	
	/**
	 * The action to toggle the inclusion of the adaptive multimethod variation collector.
	 */
	private Action includeAdaptiveMultimethodVariationAction;
	
	/**
	 * The action to toggle the inclusion of the adaptive time continuation collector.
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
	 * The action to select all results.
	 */
	private Action selectAllResultsAction;
	
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
	 * @param frame the {@code DiagnosticTool} instance on which these actions operate
	 * @param controller the {@code Controller} instance on which these actions operate
	 */
	public ActionFactory(DiagnosticTool frame, Controller controller) {
		super();
		this.frame = frame;
		this.controller = controller;
		
		initialize();
	}
	
	/**
	 * Initializes the actions used by this action factory.
	 */
	protected void initialize() {
		saveAction = new RunnableAction("save", () -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(FILTER);

			int result = fileChooser.showSaveDialog(frame);

			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase(EXTENSION)) {
					file = new File(file.getParent(), file.getName() + "." + EXTENSION);
				}

				try {
					controller.saveData(file);
				} catch (IOException e) {
					controller.handleException(e);
				}
			}
		});
		
		loadAction = new RunnableAction("load", () -> {
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
		});
		
		exitAction = new RunnableAction("exit", frame::dispose);
		selectAllResultsAction = new RunnableAction("selectAll", frame::selectAllResults);
		aboutDialogAction = new RunnableAction("about", frame::showAbout);
		
		enableAllIndicatorsAction = new RunnableAction("enableAllIndicators", () -> {
			controller.setIncludeHypervolume(true);
			controller.setIncludeGenerationalDistance(true);
			controller.setIncludeGenerationalDistancePlus(true);
			controller.setIncludeInvertedGenerationalDistance(true);
			controller.setIncludeInvertedGenerationalDistancePlus(true);
			controller.setIncludeSpacing(true);
			controller.setIncludeAdditiveEpsilonIndicator(true);
			controller.setIncludeContribution(true);
			controller.setIncludeR1(true);
			controller.setIncludeR2(true);
			controller.setIncludeR3(true);
		});
		
		disableAllIndicatorsAction = new RunnableAction("disableAllIndicators", () -> {
			controller.setIncludeHypervolume(false);
			controller.setIncludeGenerationalDistance(false);
			controller.setIncludeGenerationalDistancePlus(false);
			controller.setIncludeInvertedGenerationalDistance(false);
			controller.setIncludeInvertedGenerationalDistancePlus(false);
			controller.setIncludeSpacing(false);
			controller.setIncludeAdditiveEpsilonIndicator(false);
			controller.setIncludeContribution(false);
			controller.setIncludeR1(false);
			controller.setIncludeR2(false);
			controller.setIncludeR3(false);
		});
		
		includeHypervolumeAction = new ToggleAction("includeHypervolume",
				controller::getIncludeHypervolume,
				controller::setIncludeHypervolume);
		includeGenerationalDistanceAction = new ToggleAction("includeGenerationalDistance",
				controller::getIncludeGenerationalDistance,
				controller::setIncludeGenerationalDistance);		
		includeGenerationalDistancePlusAction = new ToggleAction("includeGenerationalDistancePlus",
				controller::getIncludeGenerationalDistancePlus,
				controller::setIncludeGenerationalDistancePlus);
		includeInvertedGenerationalDistanceAction = new ToggleAction("includeInvertedGenerationalDistance",
				controller::getIncludeInvertedGenerationalDistance,
				controller::setIncludeInvertedGenerationalDistance);
		includeInvertedGenerationalDistancePlusAction = new ToggleAction("includeInvertedGenerationalDistancePlus",
				controller::getIncludeInvertedGenerationalDistancePlus,
				controller::setIncludeInvertedGenerationalDistancePlus);
		includeSpacingAction = new ToggleAction("includeSpacing",
				controller::getIncludeSpacing,
				controller::setIncludeSpacing);
		includeAdditiveEpsilonIndicatorAction = new ToggleAction("includeAdditiveEpsilonIndicator",
				controller::getIncludeAdditiveEpsilonIndicator,
				controller::setIncludeAdditiveEpsilonIndicator);
		includeContributionAction = new ToggleAction("includeContribution",
				controller::getIncludeContribution,
				controller::setIncludeContribution);
		includeR1Action = new ToggleAction("includeR1",
				controller::getIncludeR1,
				controller::setIncludeR1);
		includeR2Action = new ToggleAction("includeR2",
				controller::getIncludeR2,
				controller::setIncludeR2);
		includeR3Action = new ToggleAction("includeR3",
				controller::getIncludeR3,
				controller::setIncludeR3);
		includeEpsilonProgressAction = new ToggleAction("includeEpsilonProgress",
				controller::getIncludeEpsilonProgress,
				controller::setIncludeEpsilonProgress);
		includeAdaptiveMultimethodVariationAction = new ToggleAction("includeAdaptiveMultimethodVariation",
				controller::getIncludeAdaptiveMultimethodVariation,
				controller::setIncludeAdaptiveMultimethodVariation);
		includeAdaptiveTimeContinuationAction = new ToggleAction("includeAdaptiveTimeContinuation",
				controller::getIncludeAdaptiveTimeContinuation,
				controller::setIncludeAdaptiveTimeContinuation);
		includeElapsedTimeAction = new ToggleAction("includeElapsedTime",
				controller::getIncludeElapsedTime,
				controller::setIncludeElapsedTime);
		includePopulationSizeAction = new ToggleAction("includePopulationSize",
				controller::getIncludePopulationSize,
				controller::setIncludePopulationSize);		
		includeApproximationSetAction = new ToggleAction("includeApproximationSet",
				controller::getIncludeApproximationSet,
				controller::setIncludeApproximationSet);
		
		runAction = new RunnableAction("run", controller::run) {

			private static final long serialVersionUID = -3966834246075639069L;
			
			@Override
			public void controllerStateChanged(ControllerEvent event) {
				setEnabled(!controller.isRunning());
			}
			
		};
		
		cancelAction = new RunnableAction("cancel", controller::cancel) {
			
			private static final long serialVersionUID = 1490880962056609890L;

			@Override
			public void controllerStateChanged(ControllerEvent event) {
				setEnabled(controller.isRunning());
			}
			
		};
		
		clearAction = new RunnableAction("clear", controller::clear) {

			private static final long serialVersionUID = 3770122212031491835L;
			
			@Override
			public void controllerStateChanged(ControllerEvent event) {
				setEnabled(!controller.isRunning());
			}
			
		};
		
		showStatisticsAction = new RunnableAction("showStatistics", controller::showStatistics) {
			
			private static final long serialVersionUID = 6836221261899470110L;
			
			@Override
			public void controllerStateChanged(ControllerEvent event) {
				if (event.getType().equals(ControllerEvent.Type.VIEW_CHANGED) ||
						event.getType().equals(ControllerEvent.Type.MODEL_CHANGED)) {
					Set<String> problems = new HashSet<String>();
					Set<String> algorithms = new HashSet<String>();
					
					for (ResultKey key : frame.getSelectedResults()) {
						problems.add(key.getProblem());
						algorithms.add(key.getAlgorithm());
					}
										
					setEnabled((problems.size() == 1) && (algorithms.size() > 1));
				}
			}
			
		};
		
		showLastTraceAction = new ToggleAction("showLastTrace",
				controller::getShowLastTrace,
				controller::setShowLastTrace);
		showIndividualTracesAction = new ToggleAction("showIndividualTraces",
				controller::getShowIndividualTraces,
				controller::setShowIndividualTraces);
		showQuantilesAction = new ToggleAction("showQuantiles",
				() -> !controller.getShowIndividualTraces(),
				(b) -> controller.setShowIndividualTraces(!b));
		
		memoryUsageAction = new AbstractAction() {

			private static final long serialVersionUID = -3966834246075639069L;
			
			{
				setEnabled(false);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				final double divisor = 1024*1024;
				long free = Runtime.getRuntime().freeMemory();
				long total = Runtime.getRuntime().totalMemory();
				long max = Runtime.getRuntime().maxMemory();
				double used = (total - free) / divisor;
				double available = max / divisor;

				memoryUsageAction.putValue(Action.NAME, localization.getString("text.memory", used, available));
			}
			
		};
		
		final Timer timer = new Timer(1000, memoryUsageAction);
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
	 * Returns the action to toggle the inclusion of the hypervolume indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the hypervolume indicator collector
	 */
	public Action getIncludeHypervolumeAction() {
		return includeHypervolumeAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the generational distance indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the generational distance indicator collector
	 */
	public Action getIncludeGenerationalDistanceAction() {
		return includeGenerationalDistanceAction;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the generational distance plus indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the generational distance plus indicator collector
	 */
	public Action getIncludeGenerationalDistancePlusAction() {
		return includeGenerationalDistancePlusAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the inverted generational distance indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the inverted generational distance indicator collector
	 */
	public Action getIncludeInvertedGenerationalDistanceAction() {
		return includeInvertedGenerationalDistanceAction;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the inverted generational distance plus indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the inverted generational distance plus indicator collector
	 */
	public Action getIncludeInvertedGenerationalDistancePlusAction() {
		return includeInvertedGenerationalDistancePlusAction;
	}

	/**
	 * Returns the action to toggle the inclusion the spacing indicator collector.
	 * 
	 * @return the action to toggle the inclusion the spacing indicator collector
	 */
	public Action getIncludeSpacingAction() {
		return includeSpacingAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the additive &epsilon;-indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the additive &epsilon;-indicator collector
	 */
	public Action getIncludeAdditiveEpsilonIndicatorAction() {
		return includeAdditiveEpsilonIndicatorAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the contribution indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the contribution indicator collector
	 */
	public Action getIncludeContributionAction() {
		return includeContributionAction;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the R1 indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the R1 indicator collector
	 */
	public Action getIncludeR1Action() {
		return includeR1Action;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the R2 indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the R2 indicator collector
	 */
	public Action getIncludeR2Action() {
		return includeR2Action;
	}
	
	/**
	 * Returns the action to toggle the inclusion of the R3 indicator collector.
	 * 
	 * @return the action to toggle the inclusion of the R3 indicator collector
	 */
	public Action getIncludeR3Action() {
		return includeR3Action;
	}

	/**
	 * Returns the action to toggle the inclusion of &epsilon;-progress restart collector.
	 * 
	 * @return the action to toggle the inclusion of &epsilon;-progress restart collector
	 */
	public Action getIncludeEpsilonProgressAction() {
		return includeEpsilonProgressAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the adaptive multimethod variation collector.
	 * 
	 * @return the action to toggle the inclusion of the adaptive multimethod variation collector
	 */
	public Action getIncludeAdaptiveMultimethodVariationAction() {
		return includeAdaptiveMultimethodVariationAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the adaptive time continuation collector.
	 * 
	 * @return the action to toggle the inclusion of the adaptive time continuation collector
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
	 * Returns the action to toggle the inclusion of the population size collector.
	 * 
	 * @return the action to toggle the inclusion of the population size collector
	 */
	public Action getIncludePopulationSizeAction() {
		return includePopulationSizeAction;
	}

	/**
	 * Returns the action to toggle the inclusion of the approximation set collector.
	 * 
	 * @return the action to toggle the inclusion of the approximation set collector
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
		return new RunnableAction("showApproximationSet", () -> {
			NondominatedPopulation referenceSet = null;

			try {
				Instrumenter instrumenter = new Instrumenter().withProblem(key.getProblem());

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
		});
	}
	
	/**
	 * Returns the action to select all results.
	 * 
	 * @return the action to select all items in the specified table
	 */
	public Action getSelectAllResultsAction() {
		return selectAllResultsAction;
	}
	
	private abstract class LocalizedAction extends AbstractAction implements ControllerListener {

		private static final long serialVersionUID = 4030882078395416151L;
		
		protected final String id;
		
		public LocalizedAction(String id) {
			super();
			this.id = id;
			
			putValue(Action.NAME, localization.getString("action." + id + ".name"));
			putValue(Action.SHORT_DESCRIPTION, localization.getString("action." + id + ".description"));
			
			controller.addControllerListener(this);
		}
		
	}
	
	private class RunnableAction extends LocalizedAction {
		
		private static final long serialVersionUID = 3633238192124429111L;
		
		private final Runnable runnable;
		
		public RunnableAction(String id, Runnable runnable) {
			super(id);
			this.runnable = runnable;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			runnable.run();
		}
		
		@Override
		public void controllerStateChanged(ControllerEvent event) {
			// do nothing
		}
		
	}
	
	private class ToggleAction extends LocalizedAction {
		
		private static final long serialVersionUID = -992336279525967638L;

		private final Supplier<Boolean> getter;
		
		private final Consumer<Boolean> setter;

		public ToggleAction(String id, Supplier<Boolean> getter, Consumer<Boolean> setter) {
			super(id);
			this.getter = getter;
			this.setter = setter;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setter.accept((Boolean)getValue(Action.SELECTED_KEY));
		}
		
		@Override
		public void controllerStateChanged(ControllerEvent event) {
			if (event.getType().equals(ControllerEvent.Type.SETTINGS_CHANGED)) {
				putValue(Action.SELECTED_KEY, getter.get());
			}
		}
		
	}
	
}
