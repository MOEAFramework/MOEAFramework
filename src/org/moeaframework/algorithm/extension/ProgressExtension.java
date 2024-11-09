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
package org.moeaframework.algorithm.extension;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.EventListener;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.termination.TerminationCondition;
import org.moeaframework.util.DurationUtils;

/**
 * Extends an algorithm to track and report progress.
 */
public class ProgressExtension implements Extension {
	
	/**
	 * The listeners to receive progress reports.
	 */
	private final EventListenerSupport<ProgressListener> listeners;
	
	/**
	 * Calculates the moving average processing speed for estimating remaining time.  The value is the measured
	 * milliseconds to complete one percent progress.
	 */
	private final DescriptiveStatistics statistics;

	/**
	 * The elapsed time.
	 */
	private final StopWatch timer;
	
	/**
	 * The termination condition for the current run.
	 */
	private TerminationCondition terminationCondition;
	
	/**
	 * The last recorded percent complete.
	 */
	private double lastPercentComplete;

	/**
	 * Constructs a new extension for tracking progress.
	 */
	public ProgressExtension() {
		super();
		listeners = EventListenerSupport.create(ProgressListener.class);
		statistics = new DescriptiveStatistics(25);
		timer = new StopWatch();
	}
	
	@Override
	public void onRun(Algorithm algorithm, TerminationCondition terminationCondition) {
		if (!timer.isStarted()) {
			timer.start();
		}
		
		this.terminationCondition = terminationCondition;
		updateProgress(algorithm);
	}

	@Override
	public void onStep(Algorithm algorithm) {
		updateProgress(algorithm);
	}

	@Override
	public void onInitialize(Algorithm algorithm) {
		updateProgress(algorithm);
	}

	@Override
	public void onTerminate(Algorithm algorithm) {
		updateProgress(algorithm);
		timer.stop();
	}
	
	/**
	 * Adds the given listener to receive progress reports.
	 * 
	 * @param listener the listener to receive progress reports
	 * @return a reference to this extension
	 */
	public ProgressExtension withListener(ProgressListener listener) {
		addListener(listener);
		return this;
	}
	
	/**
	 * Adds the given listener to receive progress reports.
	 * 
	 * @param listener the listener to receive progress reports
	 */
	public void addListener(ProgressListener listener) {
		listeners.addListener(listener);
	}
	
	/**
	 * Removes the given listener so it no longer receives progress reports.
	 * 
	 * @param listener the listener to no longer receive progress reports
	 */
	public void removeListener(ProgressListener listener) {
		listeners.removeListener(listener);
	}
	
	/**
	 * Updates the moving average statistics used for estimating the remaining time and fires a progress event, if
	 * required.
	 */
	private void updateProgress(Algorithm algorithm) {
		if (terminationCondition == null) {
			return;
		}
		
		double percentComplete = terminationCondition.getPercentComplete(algorithm);
		
		if (Double.isNaN(percentComplete)) {
			return;
		}
		
		double percentChange = percentComplete - lastPercentComplete;
			
		// only update if a change in percent complete was detected
		if (percentChange >= 0.0001) {
			timer.split();
				
			statistics.addValue(timer.getSplitTime() / percentChange);
			lastPercentComplete = percentChange;
			
			percentComplete = Math.min(Math.max(percentComplete, 0.0), 100.0);

			listeners.fire().progressUpdate(new ProgressEvent(
					algorithm,
					percentComplete,
					Duration.ofMillis(timer.getTime()),
					Duration.ofMillis(Math.round(statistics.getMean() * (100.0 - percentComplete)))));
		}
	}
	
	/**
	 * Interface used to listen for progress reports provided by this extension.
	 */
	public static interface ProgressListener extends EventListener {
		
		/**
		 * Called when a new progress report is generated.
		 * 
		 * @param event the progress report
		 */
		public void progressUpdate(ProgressEvent event);

	}
	
	/**
	 * A progress event, including the percent complete, elapsed time, and estimated remaining time.
	 */
	public static class ProgressEvent {

		/**
		 * The algorithm being run. 
		 */
		private final Algorithm algorithm;
		
		/**
		 * The percent complete as a value between {@code 0.0} and {@code 100.0}.
		 */
		private final double percentComplete;
		
		/**
		 * The elapsed time in seconds.
		 */
		private final Duration elapsedTime;
		
		/**
		 * The estimated remaining time in seconds.
		 */
		private final Duration remainingTime;

		ProgressEvent(Algorithm algorithm, double percentComplete, Duration elapsedTime, Duration remainingTime) {
			super();
			this.algorithm = algorithm;
			this.percentComplete = percentComplete;
			this.elapsedTime = elapsedTime;
			this.remainingTime = remainingTime;
		}
		
		/**
		 * Returns the algorithm that is currently running.
		 * 
		 * @return the algorithm currently running
		 */
		public Algorithm getAlgorithm() {
			return algorithm;
		}

		/**
		 * Returns the percent complete as a value between {@code 0.0} and {@code 100.0}.
		 * 
		 * @return the percent complete as a fraction between {@code 0.0} and {@code 100.0}
		 */
		public double getPercentComplete() {
			return percentComplete;
		}

		/**
		 * Returns the elapsed time.
		 * 
		 * @return the elapsed time
		 */
		public Duration getElapsedTime() {
			return elapsedTime;
		}

		/**
		 * Returns the estimated remaining time.
		 * 
		 * @return the estimated remaining time
		 */
		public Duration getRemainingTime() {
			return remainingTime;
		}

	}
	
	/**
	 * Default progress listener that displays information to standard output.
	 */
	public static class DefaultProgressListener implements ProgressListener {
		
		private final int PROGRESS_WIDTH = 40;
		
		/**
		 * Constructs the default progress listener.
		 */
		public DefaultProgressListener() {
			super();
		}
		
		@Override
		public void progressUpdate(ProgressEvent event) {
			System.out.print("E: ");
			System.out.print(DurationUtils.format(event.getElapsedTime()));
			System.out.print(", R: ");
			System.out.print(DurationUtils.format(event.getRemainingTime()));
			System.out.print(" [");
			
			double percentComplete = Math.floor(event.getPercentComplete()) / 100.0;
			
			if (percentComplete <= 0.0) {
				System.out.print(" ".repeat(PROGRESS_WIDTH));
			} else if (percentComplete >= 1.0) {
				System.out.print("=".repeat(PROGRESS_WIDTH));
			} else {
				int n = Math.max(0, (int)Math.round(percentComplete * PROGRESS_WIDTH) - 1);
				
				System.out.print("=".repeat(n));
				System.out.print(">");
				System.out.print(" ".repeat(PROGRESS_WIDTH - n - 1));
			}
			
			System.out.print("] ");
			System.out.print(NumberFormat.getPercentInstance().format(percentComplete));
			System.out.print("\r");
			System.out.flush();
		}

	}
	
}
