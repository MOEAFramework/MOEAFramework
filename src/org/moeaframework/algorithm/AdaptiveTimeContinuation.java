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
package org.moeaframework.algorithm;

import java.io.NotSerializableException;
import java.io.Serializable;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Decorator for {@link EvolutionaryAlgorithm}s to add time continuation
 * (restarts). Restarts occur if either
 * <ol>
 * <li>the number of fitness function evaluations since the last restart exceeds
 * {@code maxWindowSize}; or
 * <li>the population-to-archive ratio exceeds {@code populationRatio} by more
 * than {@code 25%}.
 * </ol>
 * If a restart occurs, the population is emptied, the population size is
 * adapted to maintain the {@code populationRatio}, the the new population is
 * filled with solutions selected from {@code algorithm.getArchive()} and
 * mutated using the specified {@link Selection} and {@link Variation}
 * operators.
 * <p>
 * References:
 * <ol>
 *   <li>Goldberg, D. E.  "Sizing Populations for Serial and Parallel Genetic 
 *       Algorithms."  In 3rd International Conference on Genetic Algorithms, 
 *       pp. 70-79, 1989.
 *   <li>Srivastava, R. P.  "Time Continuation in Genetic Algorithms."
 *       Technical report, Illinois Genetic Algorithm Laboratory, 2002.
 *   <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective 
 *       Evolutionary Algorithms for Long-Term Monitoring Design."  Advances in
 *       Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class AdaptiveTimeContinuation extends PeriodicAction 
implements EvolutionaryAlgorithm {

	/**
	 * The maximum number of iterations allowed since the last restart before 
	 * forcing a restart.
	 */
	private final int maxWindowSize;

	/**
	 * The population-to-archive ratio.
	 */
	private final double populationRatio;

	/**
	 * The minimum size of the population.
	 */
	private final int minimumPopulationSize;

	/**
	 * The maximum size of the population.
	 */
	private final int maximumPopulationSize;

	/**
	 * The selection operator for selecting solutions from the archive during a
	 * restart.
	 */
	private final Selection selection;

	/**
	 * The variation operator for mutating solutions selected from the archive
	 * during a restart.
	 */
	private final Variation variation;

	/**
	 * The number of iterations at the last invocation of {@code restart}.
	 */
	private int iterationAtLastRestart;

	/**
	 * The collection of listeners notified when a restart occurs.
	 */
	private final EventListenerSupport<RestartListener> listeners;

	/**
	 * Decorates the specified algorithm with adaptive time continuation.
	 * 
	 * @param algorithm the algorithm being decorated
	 * @param windowSize the number of iterations between invocations of
	 *        {@code check}
	 * @param maxWindowSize the maximum number of iterations allowed since the
	 *        last restart before forcing a restart
	 * @param populationRatio the population-to-archive ratio
	 * @param minimumPopulationSize the minimum size of the population
	 * @param maximumPopulationSize the maximum size of the population
	 * @param selection the selection operator for selecting solutions from the
	 *        archive during a restart
	 * @param variation the variation operator for mutating solutions selected
	 *        from the archive during a restart
	 */
	public AdaptiveTimeContinuation(EvolutionaryAlgorithm algorithm,
			int windowSize, int maxWindowSize, double populationRatio,
			int minimumPopulationSize, int maximumPopulationSize,
			Selection selection, Variation variation) {
		super(algorithm, windowSize, FrequencyType.STEPS);
		this.maxWindowSize = maxWindowSize;
		this.populationRatio = populationRatio;
		this.minimumPopulationSize = minimumPopulationSize;
		this.maximumPopulationSize = maximumPopulationSize;
		this.selection = selection;
		this.variation = variation;

		listeners = EventListenerSupport.create(RestartListener.class);
	}

	/**
	 * Adds a listener to be notified whenever a restart occurs.
	 * 
	 * @param listener the listener to be notified whenever a restart occurs
	 */
	public void addRestartListener(RestartListener listener) {
		listeners.addListener(listener);
	}

	/**
	 * Removes the specified listener so it no longer receives notifications
	 * whenever a restart occurs.
	 * 
	 * @param listener the listener to be removed
	 */
	public void removeRestartListener(RestartListener listener) {
		listeners.removeListener(listener);
	}

	/**
	 * Performs a check to determine if a restart should occur. Returns
	 * {@code RestartType.NONE} if no restart should occur; or
	 * {@code RestartType.HARD} if the population-to-archive ratio exceeds 
	 * {@code populationRatio} by more than {@code 25%} or the number of fitness
	 * evaluations since the last restart exceeds {@code maxWindowSize}.
	 * 
	 * @return {@code RestartType.NONE} if no restart should occur; or
	 *         {@code RestartType.HARD} if the population-to-archive ratio
	 *         exceeds {@code populationRatio} by more than {@code 25%} or
	 *         if the number of fitness evaluations since the last restart 
	 *         exceeds {@code maxWindowSize}
	 */
	protected RestartType check() {
		int populationSize = getPopulation().size();
		double targetSize = populationRatio * getArchive().size();

		if (iteration - iterationAtLastRestart >= maxWindowSize) {
			return RestartType.HARD;
		} else if ((targetSize >= minimumPopulationSize) &&
				(targetSize <= maximumPopulationSize) && 
				(Math.abs(populationSize - targetSize) > (0.25 * targetSize))) {
			return RestartType.HARD;
		} else {
			return RestartType.NONE;
		}
	}

	/**
	 * Performs a restart. If the type is {@code RestartType.HARD}, the
	 * population is emptied, resized and filled with solutions selected and
	 * mutated from the archive. If the type is {@code RestartType.SOFT}, the
	 * population is not emptied; new solutions are only added to fill any empty
	 * slots.
	 * 
	 * @param type the type of restart
	 */
	protected void restart(RestartType type) {
		Population population = getPopulation();
		NondominatedPopulation archive = getArchive();

		if (type.equals(RestartType.HARD)) {
			population.clear();
			population.addAll(archive);
		}

		int newPopulationSize = (int)(populationRatio * archive.size());

		if (newPopulationSize < minimumPopulationSize) {
			newPopulationSize = minimumPopulationSize;
		} else if (newPopulationSize > maximumPopulationSize) {
			newPopulationSize = maximumPopulationSize;
		}

		while (population.size() < newPopulationSize) {
			Solution[] parents = selection.select(variation.getArity(), 
					archive);
			Solution[] children = variation.evolve(parents);

			for (Solution child : children) {
				algorithm.evaluate(child);
				population.add(child);
				archive.add(child);
			}
		}

		if (type.equals(RestartType.HARD)) {
			iterationAtLastRestart = iteration;
		}

		listeners.fire().restarted(new RestartEvent(this, type));
	}

	@Override
	public void doAction() {
		RestartType type = check();

		if ((type != null) && !type.equals(RestartType.NONE)) {
			restart(type);
		}
	}

	@Override
	public Population getPopulation() {
		return ((EvolutionaryAlgorithm)algorithm).getPopulation();
	}

	@Override
	public NondominatedPopulation getArchive() {
		return ((EvolutionaryAlgorithm)algorithm).getArchive();
	}
	
	/**
	 * Proxy for serializing and deserializing the state of an
	 * {@code AdaptiveTimeContinuation} instance. This proxy supports saving
	 * the underlying algorithm state and {@code iterationAtLastRestart}.
	 */
	private static class AdaptiveTimeContinuationState implements Serializable {

		private static final long serialVersionUID = -4773227519517581809L;

		/**
		 * The state of the underlying algorithm.
		 */
		private final Serializable algorithmState;
		
		/**
		 * The {@code iterationAtLastRestart} value of the
		 * {@code AdaptiveTimeContinuation} instance.
		 */
		private final int iterationAtLastRestart;

		/**
		 * Constructs a proxy for storing the state of an
		 * {@code AdaptiveTimeContinuation} instance.
		 * 
		 * @param algorithmState the state of the underlying algorithm
		 * @param iterationAtLastRestart the {@code iterationAtLastRestart}
		 *        value of the {@code AdaptiveTimeContinuation} instance
		 */
		public AdaptiveTimeContinuationState(Serializable algorithmState,
				int iterationAtLastRestart) {
			super();
			this.algorithmState = algorithmState;
			this.iterationAtLastRestart = iterationAtLastRestart;
		}

		/**
		 * Returns the underlying algorithm state.
		 * 
		 * @return the underlying algorithm state
		 */
		public Serializable getAlgorithmState() {
			return algorithmState;
		}

		/**
		 * Returns the {@code iterationAtLastRestart} value of the
		 * {@code AdaptiveTimeContinuation} instance.
		 * 
		 * @return the {@code iterationAtLastRestart} value of the
		 *         {@code AdaptiveTimeContinuation} instance
		 */
		public int getIterationAtLastRestart() {
			return iterationAtLastRestart;
		}
		
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		return new AdaptiveTimeContinuationState(super.getState(),
				iterationAtLastRestart);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		AdaptiveTimeContinuationState state =
				(AdaptiveTimeContinuationState)objState;
		
		super.setState(state.getAlgorithmState());
		iterationAtLastRestart = state.getIterationAtLastRestart();
	}

}
