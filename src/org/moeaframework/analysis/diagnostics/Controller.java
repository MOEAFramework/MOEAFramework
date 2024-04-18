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
import org.moeaframework.analysis.DefaultEpsilons;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.progress.ProgressEvent;
import org.moeaframework.util.progress.ProgressListener;

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
	 * {@code true} if the last run's trace should be drawn separately; {@code false} otherwise.
	 */
	private boolean showLastTrace = false;
	
	/**
	 * {@code true} if the hypervolume indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeHypervolume = true;
	
	/**
	 * {@code true} if the generational distance indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeGenerationalDistance = true;
	
	/**
	 * {@code true} if the inverted generational distance indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeInvertedGenerationalDistance = true;
	
	/**
	 * {@code true} if the spacing indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeSpacing = true;
	
	/**
	 * {@code true} if the additive &epsilon;-indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeAdditiveEpsilonIndicator = true;
	
	/**
	 * {@code true} if the contribution indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeContribution = true;
	
	/**
	 * {@code true} if the R1 indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeR1 = false;
	
	/**
	 * {@code true} if the R2 indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeR2 = true;
	
	/**
	 * {@code true} if the R3 indicator collector is included; {@code false} otherwise.
	 */
	private boolean includeR3 = false;
	
	/**
	 * {@code true} if the &epsilon;-progress collector is included; {@code false} otherwise.
	 */
	private boolean includeEpsilonProgress = true;
	
	/**
	 * {@code true} if the adaptive multimethod variation collector is included; {@code false} otherwise.
	 */
	private boolean includeAdaptiveMultimethodVariation = true;
	
	/**
	 * {@code true} if the adaptive time continuation collector is included; {@code false} otherwise.
	 */
	private boolean includeAdaptiveTimeContinuation = true;
	
	/**
	 * {@code true} if the elapsed time collector is included; {@code false} otherwise.
	 */
	private boolean includeElapsedTime = true;
	
	/**
	 * {@code true} if the approximation set collector is included; {@code false} otherwise.
	 */
	private boolean includeApproximationSet = true;
	
	/**
	 * {@code true} if the population size collector is included; {@code false} otherwise.
	 */
	private boolean includePopulationSize = true;
	
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
	 * Toggles between showing individual trace lines when {@code true} and quantiles when {@code false}.
	 */
	private boolean showIndividualTraces;
	
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
	 * Fires a {@code MODEL_CHANGED} controller event.
	 */
	protected void fireModelChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.MODEL_CHANGED));
	}
	
	/**
	 * Fires a {@code STATE_CHANGED} controller event.
	 */
	protected void fireStateChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.STATE_CHANGED));
	}
	
	/**
	 * Fires a {@code PROGRESS_CHANGED} controller event.
	 */
	protected void fireProgressChangedEvent() {
		fireEvent(new ControllerEvent(this, ControllerEvent.Type.PROGRESS_CHANGED));
	}
	
	/**
	 * Fires a {@code VIEW_CHANGED} controller event.
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
	 * existing results.  A {@code MODEL_CHANGED} event is fired.
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
	 * Clears all results from this collector.  A {@code MODEL_CHANGED} event is fired.
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
	 * Loads all results stored in the specified file.  A {@code MODEL_CHANGED} event is fired.
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
	 * Updates the progress of this controller.  A {@code PROGRESS_CHANGED} event is fired.
	 * 
	 * @param currentEvaluation the current evaluation number
	 * @param currentSeed the current seed number
	 * @param totalEvaluations the total number of evaluations
	 * @param totalSeeds the total number of seeds
	 */
	protected void updateProgress(int currentEvaluation, int currentSeed, int totalEvaluations, int totalSeeds) {
		runProgress = (int)(100*currentEvaluation/(double)totalEvaluations);
		overallProgress = (int)(100*(currentSeed > 0 ? currentSeed - 1 : 0)/(double)totalSeeds);
		
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
			
			if (getIncludeHypervolume()) {
				analyzer.includeHypervolume();
			}
			
			if (getIncludeGenerationalDistance()) {
				analyzer.includeGenerationalDistance();
			}
			
			if (getIncludeInvertedGenerationalDistance()) {
				analyzer.includeInvertedGenerationalDistance();
			}
			
			if (getIncludeSpacing()) {
				analyzer.includeSpacing();
			}
			
			if (getIncludeAdditiveEpsilonIndicator()) {
				analyzer.includeAdditiveEpsilonIndicator();
			}
			
			if (getIncludeContribution()) {
				analyzer.includeContribution();
			}
			
			if (getIncludeR1()) {
				analyzer.includeR1();
			}
			
			if (getIncludeR2()) {
				analyzer.includeR2();
			}
			
			if (getIncludeR3()) {
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
					
					if (getIncludeHypervolume()) {
						instrumenter.attachHypervolumeCollector();
					}
					
					if (getIncludeGenerationalDistance()) {
						instrumenter.attachGenerationalDistanceCollector();
					}
					
					if (getIncludeInvertedGenerationalDistance()) {
						instrumenter.attachInvertedGenerationalDistanceCollector();
					}
					
					if (getIncludeSpacing()) {
						instrumenter.attachSpacingCollector();
					}
					
					if (getIncludeAdditiveEpsilonIndicator()) {
						instrumenter.attachAdditiveEpsilonIndicatorCollector();
					}
					
					if (getIncludeContribution()) {
						instrumenter.attachContributionCollector();
					}
					
					if (getIncludeR1()) {
						instrumenter.attachR1Collector();
					}
					
					if (getIncludeR2()) {
						instrumenter.attachR2Collector();
					}
					
					if (getIncludeR3()) {
						instrumenter.attachR3Collector();
					}
					
					if (getIncludeEpsilonProgress()) {
						instrumenter.attachEpsilonProgressCollector();
					}
					
					if (getIncludeAdaptiveMultimethodVariation()) {
						instrumenter.attachAdaptiveMultimethodVariationCollector();
					}
					
					if (getIncludeAdaptiveTimeContinuation()) {
						instrumenter.attachAdaptiveTimeContinuationCollector();
					}
					
					if (getIncludeElapsedTime()) {
						instrumenter.attachElapsedTimeCollector();
					}
					
					if (getIncludeApproximationSet()) {
						instrumenter.attachApproximationSetCollector();
					}
					
					if (getIncludePopulationSize()) {
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
	 * Returns {@code true} if the last run's trace is displayed; {@code false} otherwise.
	 * 
	 * @return {@code true} if the last run's trace is displayed; {@code false} otherwise
	 */
	public boolean getShowLastTrace() {
		return showLastTrace;
	}

	/**
	 * Sets the display of the last run's trace.
	 * 
	 * @param showLastTrace {@code true} if the last run's trace is displayed; {@code false} otherwise
	 */
	public void setShowLastTrace(boolean showLastTrace) {
		this.showLastTrace = showLastTrace;
		
		fireViewChangedEvent();
	}

	/**
	 * Returns {@code true} if the hypervolume indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the hypervolume indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeHypervolume() {
		return includeHypervolume;
	}

	/**
	 * Sets the inclusion of the hypervolume indicator collector.
	 * 
	 * @param includeHypervolume {@code true} if the hypervolume collector is included; {@code false} otherwise
	 */
	public void setIncludeHypervolume(boolean includeHypervolume) {
		this.includeHypervolume = includeHypervolume;
	}

	/**
	 * Returns {@code true} if the generational distance indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the generational distance indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeGenerationalDistance() {
		return includeGenerationalDistance;
	}

	/**
	 * Sets the inclusion of the generational distance indicator collector.
	 * 
	 * @param includeGenerationalDistance {@code true} if the generational distance indicator collector is included;
	 *        {@code false} otherwise
	 */
	public void setIncludeGenerationalDistance(boolean includeGenerationalDistance) {
		this.includeGenerationalDistance = includeGenerationalDistance;
	}

	/**
	 * Returns {@code true} if the inverted generational distance indicator collector is included; {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if the inverted generational distance indicator collector is included; {@code false}
	 *         otherwise
	 */
	public boolean getIncludeInvertedGenerationalDistance() {
		return includeInvertedGenerationalDistance;
	}

	/**
	 * Sets the inclusion of the inverted generational distance indicator collector.
	 * 
	 * @param includeInvertedGenerationalDistance {@code true} if the inverted generational distance indicator
	 *        collector is included; {@code false} otherwise
	 */
	public void setIncludeInvertedGenerationalDistance(boolean includeInvertedGenerationalDistance) {
		this.includeInvertedGenerationalDistance = includeInvertedGenerationalDistance;
	}

	/**
	 * Returns {@code true} if the spacing indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the spacing indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeSpacing() {
		return includeSpacing;
	}

	/**
	 * Sets the inclusion of the spacing indicator collector.
	 * 
	 * @param includeSpacing {@code true} if the spacing indicator collector is included; {@code false} otherwise
	 */
	public void setIncludeSpacing(boolean includeSpacing) {
		this.includeSpacing = includeSpacing;
	}

	/**
	 * Returns {@code true} if the additive &epsilon;-indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the additive &epsilon;-indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeAdditiveEpsilonIndicator() {
		return includeAdditiveEpsilonIndicator;
	}

	/**
	 * Sets the inclusion of the additive &epsilon;-indicator collector.
	 * 
	 * @param includeAdditiveEpsilonIndicator {@code true} if the additive  &epsilon;-indicator collector is included;
	 *        {@code false} otherwise
	 */
	public void setIncludeAdditiveEpsilonIndicator(boolean includeAdditiveEpsilonIndicator) {
		this.includeAdditiveEpsilonIndicator = includeAdditiveEpsilonIndicator;
	}

	/**
	 * Returns {@code true} if the contribution indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the contribution indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeContribution() {
		return includeContribution;
	}

	/**
	 * Sets the inclusion of the contribution indicator collector.
	 * 
	 * @param includeContribution {@code true} if the contribution indicator collector is included; {@code false}
	 *        otherwise
	 */
	public void setIncludeContribution(boolean includeContribution) {
		this.includeContribution = includeContribution;
	}
	
	/**
	 * Returns {@code true} if the R1 indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the R1 indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeR1() {
		return includeR1;
	}
	
	/**
	 * Sets the inclusion of the R1 indicator collector.
	 * 
	 * @param includeR1 {@code true} if the R1 indicator collector is included; {@code false} otherwise
	 */
	public void setIncludeR1(boolean includeR1) {
		this.includeR1 = includeR1;
	}
	
	/**
	 * Returns {@code true} if the R2 indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the R2 indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeR2() {
		return includeR2;
	}
	
	/**
	 * Sets the inclusion of the R2 indicator collector.
	 * 
	 * @param includeR2 {@code true} if the R2 indicator collector is included; {@code false} otherwise
	 */
	public void setIncludeR2(boolean includeR2) {
		this.includeR2 = includeR2;
	}
	
	/**
	 * Returns {@code true} if the R3 indicator collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the R3 indicator collector is included; {@code false} otherwise
	 */
	public boolean getIncludeR3() {
		return includeR3;
	}
	
	/**
	 * Sets the inclusion of the R3 indicator collector.
	 * 
	 * @param includeR3 {@code true} if the R3 indicator collector is included; {@code false} otherwise
	 */
	public void setIncludeR3(boolean includeR3) {
		this.includeR3 = includeR3;
	}

	/**
	 * Returns {@code true} if the &epsilon;-progress collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the &epsilon;-progress collector is included; {@code false} otherwise
	 */
	public boolean getIncludeEpsilonProgress() {
		return includeEpsilonProgress;
	}

	/**
	 * Sets the inclusion of the &epsilon;-progress collector.
	 * 
	 * @param includeEpsilonProgress {@code true} if the &epsilon;-progress collector is included; {@code false}
	 *        otherwise
	 */
	public void setIncludeEpsilonProgress(boolean includeEpsilonProgress) {
		this.includeEpsilonProgress = includeEpsilonProgress;
	}

	/**
	 * Returns {@code true} if the adaptive multimethod variation collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the adaptive multimethod variation collector is included; {@code false} otherwise
	 */
	public boolean getIncludeAdaptiveMultimethodVariation() {
		return includeAdaptiveMultimethodVariation;
	}

	/**
	 * Sets the inclusion of the adaptive multimethod variation collector.
	 * 
	 * @param includeAdaptiveMultimethodVariation {@code true} if the adaptive multimethod variation collector is
	 *        included; {@code false} otherwise
	 */
	public void setIncludeAdaptiveMultimethodVariation(boolean includeAdaptiveMultimethodVariation) {
		this.includeAdaptiveMultimethodVariation = includeAdaptiveMultimethodVariation;
	}

	/**
	 * Returns {@code true} if the adaptive time continuation collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the adaptive time continuation collector is included; {@code false} otherwise
	 */
	public boolean getIncludeAdaptiveTimeContinuation() {
		return includeAdaptiveTimeContinuation;
	}

	/**
	 * Sets the inclusion of the adaptive time continuation collector.
	 * 
	 * @param includeAdaptiveTimeContinuation {@code true} if the adaptive time continuation collector is included;
	 *        {@code false} otherwise
	 */
	public void setIncludeAdaptiveTimeContinuation(boolean includeAdaptiveTimeContinuation) {
		this.includeAdaptiveTimeContinuation = includeAdaptiveTimeContinuation;
	}

	/**
	 * Returns {@code true} if the elapsed time collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the elapsed time collector is included; {@code false} otherwise
	 */
	public boolean getIncludeElapsedTime() {
		return includeElapsedTime;
	}

	/**
	 * Sets the inclusion of the elapsed time collector.
	 * 
	 * @param includeElapsedTime {@code true} if the elapsed time collector is included; {@code false} otherwise
	 */
	public void setIncludeElapsedTime(boolean includeElapsedTime) {
		this.includeElapsedTime = includeElapsedTime;
	}

	/**
	 * Returns {@code true} if the approximation set collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the approximation set collector is included; {@code false} otherwise
	 */
	public boolean getIncludeApproximationSet() {
		return includeApproximationSet;
	}

	/**
	 * Sets the inclusion of the approximation set collector.
	 * 
	 * @param includeApproximationSet {@code true} if the approximation set collector is included; {@code false}
	 *        otherwise
	 */
	public void setIncludeApproximationSet(boolean includeApproximationSet) {
		this.includeApproximationSet = includeApproximationSet;
	}

	/**
	 * Returns {@code true} if the population size collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the population size collector is included; {@code false} otherwise
	 */
	public boolean getIncludePopulationSize() {
		return includePopulationSize;
	}

	/**
	 * Sets the inclusion of the population size collector.
	 * 
	 * @param includePopulationSize {@code true} if the population size collector is included; {@code false} otherwise
	 */
	public void setIncludePopulationSize(boolean includePopulationSize) {
		this.includePopulationSize = includePopulationSize;
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
	 * Returns {@code true} if individual traces are shown; {@code false} if quantiles are shown.
	 * 
	 * @return {@code true} if individual traces are shown; {@code false} if quantiles are shown
	 */
	public boolean getShowIndividualTraces() {
		return showIndividualTraces;
	}

	/**
	 * Set to {@code true} to show individual traces; {@code false} to show quantiles.
	 * 
	 * @param showIndividualTraces {@code true} to show individual traces; {@code false} to show quantiles
	 */
	public void setShowIndividualTraces(boolean showIndividualTraces) {
		if (this.showIndividualTraces != showIndividualTraces) {
			this.showIndividualTraces = showIndividualTraces;
			
			fireViewChangedEvent();
		}
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

}
