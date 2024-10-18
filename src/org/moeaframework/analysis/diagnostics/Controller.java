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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.DefaultEpsilons;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.progress.ProgressEvent;
import org.moeaframework.util.progress.ProgressListener;
import org.moeaframework.util.validate.Validate;

/**
 * The controller manages the underlying data model, performs the evaluation of jobs, and notifies any listeners when
 * its state changes.
 */
public class Controller {

	/**
	 * The collection of listeners which are notified when the controller state changes.
	 */
	private final EventListenerSupport<ControllerListener> listeners;
	
	/**
	 * The collection of all results.
	 */
	private final Map<ResultKey, List<Observations>> results;
	
	/**
	 * The last observation to be generated; or {@code null} if there is none or has been cleared.
	 */
	private Observations lastObservation;
	
	/**
	 * The setting for displaying the last run's trace.
	 */
	private final Toggle showLastTrace;
	
	/**
	 * The setting for collecting the hypervolume indicator.
	 */
	private final Toggle includeHypervolume;
	
	/**
	 * The setting for collecting the generational distance (GD) indicator.
	 */
	private final Toggle includeGenerationalDistance;
	
	/**
	 * The setting for collecting the generational distance plus (GD+) indicator.
	 */
	private final Toggle includeGenerationalDistancePlus;
	
	/**
	 * The setting for collecting the inverted generational distance plus (IGD) indicator.
	 */
	private final Toggle includeInvertedGenerationalDistance;
	
	/**
	 * The setting for collecting the inverted generational distance plus (IGD+) indicator.
	 */
	private final Toggle includeInvertedGenerationalDistancePlus;
	
	/**
	 * The setting for collecting the spacing indicator.
	 */
	private final Toggle includeSpacing;
	
	/**
	 * The setting for collecting the additive epsilon indicator.
	 */
	private final Toggle includeAdditiveEpsilonIndicator;
	
	/**
	 * The setting for collecting the contribution indicator.
	 */
	private final Toggle includeContribution;
	
	/**
	 * The setting for collecting the R1 indicator.
	 */
	private final Toggle includeR1;
	
	/**
	 * The setting for collecting the R2 indicator.
	 */
	private final Toggle includeR2;
	
	/**
	 * The setting for collecting the R3 indicator.
	 */
	private final Toggle includeR3;
	
	/**
	 * The setting for collecting epsilon progress metrics.
	 */
	private final Toggle includeEpsilonProgress;
	
	/**
	 * The setting for collecting adaptive multimethod variation probabilities.
	 */
	private final Toggle includeAdaptiveMultimethodVariation;
	
	/**
	 * The setting for collecting adaptive time continuation metrics.
	 */
	private final Toggle includeAdaptiveTimeContinuation;
	
	/**
	 * The setting for collecting the elapsed runtime.
	 */
	private final Toggle includeElapsedTime;
	
	/**
	 * The setting for collecting the approximation set.
	 */
	private final Toggle includeApproximationSet;
	
	/**
	 * The setting for collecting the population / archive sizes.
	 */
	private final Toggle includePopulationSize;
	
	/**
	 * Toggles between showing individual trace lines when {@code true} and quantiles when {@code false}.
	 */
	private final Toggle showIndividualTraces;
	
	/**
	 * The run progress of the current job being evaluated.
	 */
	private volatile int runProgress;
	
	/**
	 * The overall progress of the current job being evaluated.
	 */
	private volatile int overallProgress;
	
	/**
	 * The thread running the current job; or {@code null} if no job is running.
	 */
	private volatile Thread thread;
	
	/**
	 * The {@code DiagnosticTool} instance using this controller.
	 */
	private final DiagnosticTool frame;
	
	/**
	 * The executor for the current run.
	 */
	private Executor executor;
	
	/**
	 * Constructs a new controller for the specified {@code DiagnosticTool} instance.
	 * 
	 * @param frame the {@code DiagnosticTool} instance using this controller
	 */
	public Controller(DiagnosticTool frame) {
		super();
		this.frame = frame;
		
		listeners = EventListenerSupport.create(ControllerListener.class);
		results = new HashMap<ResultKey, List<Observations>>();
		
		showLastTrace = new Toggle(false, ControllerEvent.Type.SETTINGS_CHANGED, ControllerEvent.Type.VIEW_CHANGED);
		showIndividualTraces = new Toggle(false, ControllerEvent.Type.SETTINGS_CHANGED, ControllerEvent.Type.VIEW_CHANGED);
		includeHypervolume = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeGenerationalDistance = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeGenerationalDistancePlus = new Toggle(false, ControllerEvent.Type.SETTINGS_CHANGED);
		includeInvertedGenerationalDistance = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeInvertedGenerationalDistancePlus = new Toggle(false, ControllerEvent.Type.SETTINGS_CHANGED);
		includeSpacing = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeAdditiveEpsilonIndicator = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeContribution = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeR1 = new Toggle(false, ControllerEvent.Type.SETTINGS_CHANGED);
		includeR2 = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeR3 = new Toggle(false, ControllerEvent.Type.SETTINGS_CHANGED);
		includeEpsilonProgress = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeAdaptiveMultimethodVariation = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeAdaptiveTimeContinuation = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeElapsedTime = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includeApproximationSet = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
		includePopulationSize = new Toggle(true, ControllerEvent.Type.SETTINGS_CHANGED);
	}
	
	/**
	 * Adds the specified listener to receive all subsequent controller events.
	 * 
	 * @param listener the listener to receive controller events
	 */
	public void addControllerListener(ControllerListener listener) {
		listeners.addListener(listener);
	}
	
	/**
	 * Removes the specified listener so it no longer receives controller events.
	 * 
	 * @param listener the listener to no longer receive controller events
	 */
	public void removeControllerListener(ControllerListener listener) {
		listeners.removeListener(listener);
	}
	
	/**
	 * Fires a {@link ControllerEvent.Type#SETTINGS_CHANGED} controller event.
	 */
	protected void fireSettingsChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.SETTINGS_CHANGED));
	}
	
	/**
	 * Fires a {@link ControllerEvent.Type#MODEL_CHANGED} controller event.
	 */
	protected void fireModelChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.MODEL_CHANGED));
	}
	
	/**
	 * Fires a {@link ControllerEvent.Type#STATE_CHANGED} controller event.
	 */
	protected void fireStateChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.STATE_CHANGED));
	}
	
	/**
	 * Fires a {@link ControllerEvent.Type#PROGRESS_CHANGED} controller event.
	 */
	protected void fireProgressChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.PROGRESS_CHANGED));
	}
	
	/**
	 * Fires a {@link ControllerEvent.Type#VIEW_CHANGED} controller event.
	 */
	protected void fireViewChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.VIEW_CHANGED));
	}
	
	/**
	 * Fires the specified controller event.  All listeners will receive this event on the event dispatch thread.
	 * 
	 * @param event the controller event to fire
	 */
	protected synchronized void fireEvent(final ControllerEvent event) {
		SwingUtilities.invokeLater(() -> listeners.fire().controllerStateChanged(event));
	}
	
	/**
	 * Adds a new result to this controller.  If the specified key already exists, the observation is appended to the
	 * existing results.  A {@link ControllerEvent.Type#MODEL_CHANGED} event is fired.
	 * 
	 * @param key the result key identifying the algorithm and problem associated with these results
	 * @param observation the observation storing the results
	 */
	public void add(ResultKey key, Observations observation) {
		synchronized (results) {
			if (!results.containsKey(key)) {
				results.put(key, new CopyOnWriteArrayList<Observations>());
			}
			
			results.get(key).add(observation);
			lastObservation = observation;
		}
		
		fireModelChangedEvent();
	}
	
	/**
	 * Adds a new result to this controller.  This method invokes {@link #add(ResultKey, Observations)}.
	 * 
	 * @param algorithm the algorithm associated with these results
	 * @param problem the problem associated with these results
	 * @param observation the observation storing the results
	 */
	public void add(String algorithm, String problem, Observations observation) {
		add(new ResultKey(algorithm, problem), observation);
	}
	
	/**
	 * Clears all results from this collector.  A {@link ControllerEvent.Type#MODEL_CHANGED} event is fired.
	 */
	public void clear() {
		if (results.isEmpty()) {
			return;
		}
		
		synchronized (results) {
			results.clear();
			frame.getPaintHelper().clear();
			lastObservation = null;
		}
		
		fireModelChangedEvent();
	}
	
	/**
	 * Returns an unmodifiable collection containing the results associated with the specified key.
	 * 
	 * @param key the result key
	 * @return an unmodifiable collection containing the results associated with the specified key
	 */
	public List<Observations> get(ResultKey key) {
		synchronized (results) {
			return Collections.unmodifiableList(results.get(key));
		}
	}
	
	/**
	 * Returns an unmodifiable set of result keys contained in this controller.
	 * 
	 * @return an unmodifiable set of result keys contained in this controller
	 */
	public Set<ResultKey> getKeys() {
		synchronized (results) {
			return Collections.unmodifiableSet(results.keySet());
		}
	}
	
	/**
	 * Returns the last observation to be generated; or {@code null} if there is none or has been cleared
	 * 
	 * @return the last observation to be generated; or {@code null}
	 */
	public Observations getLastObservation() {
		synchronized (results) {
			return lastObservation;
		}
	}
	
	/**
	 * Clears the last observation.  Subsequent invocations of {@link #getLastObservation()} will return {@code null}
	 * until a new observation is generated.
	 */
	public void clearLastObservation() {
		synchronized (results) {
			lastObservation = null;
		}
	}
	
	/**
	 * Saves all results stored in this controller to the specified file.
	 * 
	 * @param file the file to which the results are saved
	 * @throws IOException if an I/O error occurred
	 */
	public void saveData(File file) throws IOException {
		synchronized (results) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
				oos.writeObject(results);
			}
		}
	}
	
	/**
	 * Loads all results stored in the specified file.  A {@link ControllerEvent.Type#MODEL_CHANGED} event is fired.
	 * 
	 * @param file the file containing the results to load
	 * @throws IOException if an I/O error occurred
	 */
	public void loadData(File file) throws IOException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			Map<?, ?> data = (Map<?, ?>)ois.readObject();
			
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				ResultKey key = (ResultKey)entry.getKey();
				List<?> list = (List<?>)entry.getValue();
				
				for (Object element : list) {
					add(key, (Observations)element);
				}
			}
		} catch (StreamCorruptedException e) {
			throw new IOException("This file does not appear to be a data file generated by the diagnostic tool.", e);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Updates the progress of this controller.  A {@link ControllerEvent.Type#PROGRESS_CHANGED} event is fired.
	 * 
	 * @param currentEvaluation the current evaluation number
	 * @param currentSeed the current seed number
	 * @param totalEvaluations the total number of evaluations
	 * @param totalSeeds the total number of seeds
	 */
	protected void updateProgress(int currentEvaluation, int currentSeed, int totalEvaluations, int totalSeeds) {
		double evalPercent = currentEvaluation/(double)totalEvaluations;
		double seedPercent = (currentSeed > 0 ? currentSeed - 1 : 0)/(double)totalSeeds;
		
		runProgress = (int)(100*evalPercent);
		overallProgress = (int)(100*(seedPercent + evalPercent/(double)totalSeeds));
				
		fireProgressChangedEvent();
	}
	
	/**
	 * Creates and displays a dialog containing a statistical comparison of the selected results.
	 * 
	 * @return the dialog, or {@code null} if unable to display
	 */
	public StatisticalResultsViewer showStatistics() {
		List<ResultKey> selectedResults = frame.getSelectedResults();
		String problemName = selectedResults.get(0).getProblem();
		
		try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
			Epsilons epsilons = DefaultEpsilons.getInstance().getEpsilons(problem);
			
			Analyzer analyzer = new Analyzer()
					.withProblem(problemName)
					.withEpsilons(epsilons)
					.showAggregate()
					.showStatisticalSignificance();
						
			if (includeHypervolume().get()) {
				analyzer.includeHypervolume();
			}
			
			if (includeGenerationalDistance().get()) {
				analyzer.includeGenerationalDistance();
			}
			
			if (includeGenerationalDistancePlus().get()) {
				analyzer.includeGenerationalDistancePlus();
			}
			
			if (includeInvertedGenerationalDistance().get()) {
				analyzer.includeInvertedGenerationalDistance();
			}
			
			if (includeInvertedGenerationalDistancePlus().get()) {
				analyzer.includeInvertedGenerationalDistancePlus();
			}
			
			if (includeSpacing().get()) {
				analyzer.includeSpacing();
			}
			
			if (includeAdditiveEpsilonIndicator().get()) {
				analyzer.includeAdditiveEpsilonIndicator();
			}
			
			if (includeContribution().get()) {
				analyzer.includeContribution();
			}
			
			if (includeR1().get()) {
				analyzer.includeR1();
			}
			
			if (includeR2().get()) {
				analyzer.includeR2();
			}
			
			if (includeR3().get()) {
				analyzer.includeR3();
			}
			
			for (ResultKey key : selectedResults) {
				for (Observations observations : get(key)) {
					if (!observations.keys().contains("Approximation Set")) {
						continue;
					}
					
					NondominatedPopulation population = new EpsilonBoxDominanceArchive(epsilons);
					List<?> list = (List<?>)observations.last().get("Approximation Set");
					
					for (Object object : list) {
						population.add((Solution)object);
					}
					
					analyzer.add(key.getAlgorithm(), population);
				}
			}
			
			try (ByteArrayOutputStream output = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(output)) {
				analyzer.printAnalysis(ps);
				
				StatisticalResultsViewer viewer = new StatisticalResultsViewer(this, output.toString());
				viewer.setLocationRelativeTo(frame);
				viewer.setVisible(true);
				return viewer;
			} catch (IOException e) {
				handleException(e);
				return null;
			}
		}
	}
	
	/**
	 * Launches a thread to run the current evaluation job.
	 */
	public void run() {
		if (thread != null) {
			System.err.println("job already running");
			return;
		}
		
		final String problemName = frame.getProblem();
		final String algorithmName = frame.getAlgorithm();
		final int numberOfEvaluations = frame.getNumberOfEvaluations();
		final int numberOfSeeds = frame.getNumberOfSeeds();
		
		thread = new Thread() {
			
			public void run() {
				try {
					updateProgress(0, 0, numberOfEvaluations, numberOfSeeds);

					// setup the instrumenter to collect the necessary info
					Instrumenter instrumenter = new Instrumenter()
							.withFrequency(100)
							.withProblem(problemName);
					
					if (includeHypervolume().get()) {
						instrumenter.attachHypervolumeCollector();
					}
					
					if (includeGenerationalDistance().get()) {
						instrumenter.attachGenerationalDistanceCollector();
					}
					
					if (includeGenerationalDistancePlus().get()) {
						instrumenter.attachGenerationalDistancePlusCollector();
					}
					
					if (includeInvertedGenerationalDistance().get()) {
						instrumenter.attachInvertedGenerationalDistanceCollector();
					}
					
					if (includeInvertedGenerationalDistancePlus().get()) {
						instrumenter.attachInvertedGenerationalDistancePlusCollector();
					}
					
					if (includeSpacing().get()) {
						instrumenter.attachSpacingCollector();
					}
					
					if (includeAdditiveEpsilonIndicator().get()) {
						instrumenter.attachAdditiveEpsilonIndicatorCollector();
					}
					
					if (includeContribution().get()) {
						instrumenter.attachContributionCollector();
					}
					
					if (includeR1().get()) {
						instrumenter.attachR1Collector();
					}
					
					if (includeR2().get()) {
						instrumenter.attachR2Collector();
					}
					
					if (includeR3().get()) {
						instrumenter.attachR3Collector();
					}
					
					if (includeEpsilonProgress().get()) {
						instrumenter.attachEpsilonProgressCollector();
					}
					
					if (includeAdaptiveMultimethodVariation().get()) {
						instrumenter.attachAdaptiveMultimethodVariationCollector();
					}
					
					if (includeAdaptiveTimeContinuation().get()) {
						instrumenter.attachAdaptiveTimeContinuationCollector();
					}
					
					if (includeElapsedTime().get()) {
						instrumenter.attachElapsedTimeCollector();
					}
					
					if (includeApproximationSet().get()) {
						instrumenter.attachApproximationSetCollector();
					}
					
					if (includePopulationSize().get()) {
						instrumenter.attachPopulationSizeCollector();
					}
					
					// lookup predefined epsilons for this problem
					try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
						instrumenter.withEpsilons(DefaultEpsilons.getInstance().getEpsilons(problem));
					}
					
					// setup the progress listener to receive updates
					ProgressListener listener = new ProgressListener() {
						
						@Override
						public void progressUpdate(ProgressEvent event) {
							updateProgress(
									event.getCurrentNFE(),
									event.getCurrentSeed(),
									event.getMaxNFE(),
									event.getTotalSeeds());
							
							if (event.isSeedFinished()) {
								Executor executor = event.getExecutor();
								Instrumenter instrumenter = executor.getInstrumenter();
								
								add(algorithmName, problemName, instrumenter.getObservations());
							}
						}
						
					};
					
					// setup the executor to run for the desired time
					executor = new Executor()
							.withSameProblemAs(instrumenter)
							.withInstrumenter(instrumenter)
							.withAlgorithm(algorithmName)
							.withMaxEvaluations(numberOfEvaluations)
							.withProgressListener(listener);
					
					// run the executor using the listener to collect results
					executor.runSeeds(numberOfSeeds);
				} catch (Exception e) {
					handleException(e);
				} finally {
					thread = null;
					fireStateChangedEvent();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		fireStateChangedEvent();
	}
	
	/**
	 * Notifies the controller that it should cancel the current evaluation job.
	 */
	public void cancel() {
		if (executor != null) {
			executor.cancel();
		}
	}
	
	/**
	 * Returns {@code true} if this controller is currently processing an evaluation job; {@code false} otherwise.
	 * 
	 * @return {@code true} if this controller is currently processing an evaluation job; {@code false} otherwise
	 */
	public boolean isRunning() {
		return thread != null;
	}
	
	/**
	 * Waits for the currently running thread to finish.  If there is no such thread, this method returns immediately.
	 * 
	 * @throws InterruptedException if the thread was interrupted
	 */
	public void join() throws InterruptedException {
		if (thread != null) {
			thread.join();
		}
	}

	/**
	 * Returns the setting for displaying the last trace.
	 * 
	 * @return the setting object
	 */
	public Toggle showLastTrace() {
		return showLastTrace;
	}

	/**
	 * Returns the setting for collecting the hypervolume indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeHypervolume() {
		return includeHypervolume;
	}

	/**
	 * Returns the setting for collecting the generational distance (GD) indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeGenerationalDistance() {
		return includeGenerationalDistance;
	}
	
	/**
	 * Returns the setting for collecting the generational distance plus (GD+) indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeGenerationalDistancePlus() {
		return includeGenerationalDistancePlus;
	}

	/**
	 * Returns the setting for collecting the inverted generational distance (IGD) indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeInvertedGenerationalDistance() {
		return includeInvertedGenerationalDistance;
	}
	
	/**
	 * Returns the setting for collecting the inverted generational distance plus (IGD+) indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeInvertedGenerationalDistancePlus() {
		return includeInvertedGenerationalDistancePlus;
	}

	/**
	 * Returns the setting for collecting the spacing indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeSpacing() {
		return includeSpacing;
	}

	/**
	 * Returns the setting for collecting the additive epsilon indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeAdditiveEpsilonIndicator() {
		return includeAdditiveEpsilonIndicator;
	}

	/**
	 * Returns the setting for collecting the contribution indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeContribution() {
		return includeContribution;
	}
	
	/**
	 * Returns the setting for collecting the R1 indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeR1() {
		return includeR1;
	}
	
	/**
	 * Returns the setting for collecting the R2 indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeR2() {
		return includeR2;
	}
	
	/**
	 * Returns the setting for collecting the R3 indicator.
	 * 
	 * @return the setting object
	 */
	public Toggle includeR3() {
		return includeR3;
	}

	/**
	 * Returns the setting for collecting epsilon progress metrics.
	 * 
	 * @return the setting object
	 */
	public Toggle includeEpsilonProgress() {
		return includeEpsilonProgress;
	}

	/**
	 * Returns the setting for collecting adaptive multimethod variation probabilities.
	 * 
	 * @return the setting object
	 */
	public Toggle includeAdaptiveMultimethodVariation() {
		return includeAdaptiveMultimethodVariation;
	}

	/**
	 * Returns the setting for collecting adaptive time continuation metrics.
	 * 
	 * @return the setting object
	 */
	public Toggle includeAdaptiveTimeContinuation() {
		return includeAdaptiveTimeContinuation;
	}

	/**
	 * Returns the setting for collecting the elapsed runtime.
	 * 
	 * @return the setting object
	 */
	public Toggle includeElapsedTime() {
		return includeElapsedTime;
	}

	/**
	 * Returns the setting for collecting the approximation set.
	 * 
	 * @return the setting object
	 */
	public Toggle includeApproximationSet() {
		return includeApproximationSet;
	}

	/**
	 * Returns the setting for collecting the population / archive size.
	 * 
	 * @return the setting object
	 */
	public Toggle includePopulationSize() {
		return includePopulationSize;
	}
	
	/**
	 * Returns the setting for displaying individual traces versus quantiles.
	 * 
	 * @return the setting object
	 */
	public Toggle showIndividualTraces() {
		return showIndividualTraces;
	}
	
	/**
	 * Returns the run progress of the current job being evaluated.  The run progress measures the number of
	 * evaluations completed thus far.
	 * 
	 * @return the run progress of the current job being evaluated
	 */
	public int getRunProgress() {
		return runProgress;
	}

	/**
	 * Returns the overall progress of the current job being evaluated.  The overall progress measures the number of
	 * seeds completed thus far.
	 * 
	 * @return the overall progress of the current job being evaluated
	 */
	public int getOverallProgress() {
		return overallProgress;
	}

	/**
	 * Handles an exception, possibly displaying a dialog box containing details of the exception.
	 * 
	 * @param e the exception
	 */
	protected void handleException(Exception e) {
		e.printStackTrace();
		
		String message = e.getMessage() == null ? e.toString() : e.getMessage();
		
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			message += " - " + e.getCause().getMessage();
		}
		
		JOptionPane.showMessageDialog(
				frame, 
				message, 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Represents a setting that can be configured programmatically or through user interaction in the UI.
	 * 
	 * @param <T> the underlying type of the setting
	 */
	public interface Setting<T> {
		
		/**
		 * Sets the value of the setting.
		 * 
		 * @param newValue the new value
		 */
		void set(T newValue);
		
		/**
		 * Gets the value of the setting.
		 * 
		 * @return the current value
		 */
		T get();
		
	}
	
	/**
	 * Abstract setting that can optionally fire events when the value changes.
	 *  
	 * @param <T> the underlying type of the setting
	 */
	abstract class AbstractSetting<T> implements Setting<T> {
		
		private T value;
		
		private List<ControllerEvent.Type> eventTypes;
		
		public AbstractSetting(T value, ControllerEvent.Type... eventTypes) {
			this(value, List.of(eventTypes));
		}
		
		public AbstractSetting(T value, List<ControllerEvent.Type> eventTypes) {
			super();
			this.value = value;
			this.eventTypes = eventTypes;
			
			Validate.that("value", value).isNotNull();
		}
		
		@Override
		public void set(T newValue) {
			if (!value.equals(newValue)) {
				value = newValue;
				
				for (ControllerEvent.Type eventType : eventTypes) {
					fireEvent(new ControllerEvent(Controller.this, eventType));
				}
			}
		}
		
		@Override
		public T get() {
			return value;
		}
		
	}
	
	/**
	 * A togglable setting representing a binary state (on/off, true/false, enabled/disabled).
	 */
	class Toggle extends AbstractSetting<Boolean> {
		
		public Toggle(boolean value, ControllerEvent.Type... eventTypes) {
			super(value, eventTypes);
		}
		
		public void flip() {
			set(!get());
		}
		
		public Toggle invert() {
			return new InvertedToggle(this);
		}
		
	}
	
	/**
	 * An inverted version of a toggle.  For example, if the underlying value of a {@link Toggle} is {@code false}, then
	 * the inverse is {@code true}.  This class delegates to the original toggle for storing values and firing events.
	 */
	class InvertedToggle extends Toggle {
		
		private final Toggle original;

		public InvertedToggle(Toggle original) {
			super(false);
			this.original = original;
		}
		
		@Override
		public void set(Boolean newValue) {
			original.set(!newValue);
		}
		
		@Override
		public Boolean get() {
			return !original.get();
		}
		
		@Override
		public Toggle invert() {
			return original;
		}
		
	}

}
