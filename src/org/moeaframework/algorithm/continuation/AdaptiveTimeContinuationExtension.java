/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.algorithm.continuation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.EvolutionaryAlgorithm;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.algorithm.extension.LoggingExtension;
import org.moeaframework.algorithm.extension.PeriodicExtension;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.selection.Selection;
import org.moeaframework.core.selection.UniformSelection;
import org.moeaframework.util.validate.Validate;

/**
 * Decorator for {@link EvolutionaryAlgorithm}s to add time continuation (restarts).  Restarts occur if either
 * <ol>
 *   <li>the number of fitness function evaluations since the last restart exceeds {@code maxWindowSize}; or
 *   <li>the population-to-archive ratio exceeds {@code populationRatio} by more than {@code 25%}.
 * </ol>
 * If a restart occurs, the population is emptied, the population size is adapted to maintain the
 * {@code populationRatio}, the the new population is filled with solutions selected from
 * {@code algorithm.getArchive()} and mutated using the specified {@link Selection} and {@link Variation} operators.
 * <p>
 * References:
 * <ol>
 *   <li>Goldberg, D. E.  "Sizing Populations for Serial and Parallel Genetic Algorithms."  In 3rd International
 *       Conference on Genetic Algorithms, pp. 70-79, 1989.
 *   <li>Srivastava, R. P.  "Time Continuation in Genetic Algorithms." Technical report, Illinois Genetic Algorithm
 *       Laboratory, 2002.
 *   <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective Evolutionary Algorithms for Long-Term
 *       Monitoring Design."  Advances in Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class AdaptiveTimeContinuationExtension extends PeriodicExtension implements Configurable {

	/**
	 * The maximum number of iterations allowed since the last restart before forcing a restart.
	 */
	private int maxWindowSize;

	/**
	 * The percentage of the population that, during a restart, are introduced into the new population.
	 */
	private double injectionRate;

	/**
	 * The minimum size of the population.
	 */
	private int minimumPopulationSize;

	/**
	 * The maximum size of the population.
	 */
	private int maximumPopulationSize;

	/**
	 * The selection operator for selecting solutions from the archive during a restart.
	 */
	private final Selection restartSelection;

	/**
	 * The variation operator for mutating solutions selected from the archive during a restart.
	 */
	private final Variation restartVariation;

	/**
	 * The number of iterations at the last invocation of {@code restart}.
	 */
	private int iterationAtLastRestart;

	/**
	 * The collection of listeners notified when a restart occurs.
	 */
	private final EventListenerSupport<RestartListener> listeners;
	
	/**
	 * Creates the adaptive time continuation extension with default settings.
	 */
	public AdaptiveTimeContinuationExtension() {
		this(100, 100, 0.25, 100, 10000, new UniformSelection(), new UM(1.0));
	}

	/**
	 * Creates the adaptive time continuation extension.
	 * 
	 * @param windowSize the number of iterations between invocations of {@code check}
	 * @param maxWindowSize the maximum number of iterations allowed since the last restart before forcing a restart
	 * @param injectionRate the injection rate percentage
	 * @param minimumPopulationSize the minimum size of the population
	 * @param maximumPopulationSize the maximum size of the population
	 * @param restartSelection the selection operator for selecting solutions from the archive during a restart
	 * @param restartVariation the variation operator for mutating solutions selected from the archive during a restart
	 */
	public AdaptiveTimeContinuationExtension(int windowSize, int maxWindowSize, double injectionRate,
			int minimumPopulationSize, int maximumPopulationSize, Selection restartSelection,
			Variation restartVariation) {
		super(Frequency.ofIterations(windowSize));
		setMaxWindowSize(maxWindowSize);
		setInjectionRate(injectionRate);
		setMinimumPopulationSize(minimumPopulationSize);
		setMaximumPopulationSize(maximumPopulationSize);
		
		Validate.that("restartSelection", restartSelection).isNotNull();
		Validate.that("restartVariation", restartVariation).isNotNull();
		
		this.restartSelection = restartSelection;
		this.restartVariation = restartVariation;
		
		listeners = EventListenerSupport.create(RestartListener.class);
	}
	
	/**
	 * Returns the number of iterations between invocations of {@code check}.
	 * 
	 * @return the window size, in iterations
	 */
	public int getWindowSize() {
		return frequency.getValue();
	}
	
	/**
	 * Sets the number of iterations between invocations of {@code check}.
	 * 
	 * @param windowSize the window size, in iterations
	 */
	@Property
	public void setWindowSize(int windowSize) {
		Validate.that("windowSize", windowSize).isGreaterThan(0);
		this.frequency = Frequency.ofIterations(windowSize);
	}

	/**
	 * Returns the maximum number of iterations allowed since the last restart before forcing a restart.
	 * 
	 * @return the maximum window size, in iterations
	 */
	public int getMaxWindowSize() {
		return maxWindowSize;
	}

	/**
	 * Sets the maximum number of iterations allowed since the last restart before forcing a restart.
	 * 
	 * @param maxWindowSize the maximum window size, in iterations
	 */
	@Property
	public void setMaxWindowSize(int maxWindowSize) {
		Validate.that("maxWindowSize", maxWindowSize).isGreaterThan(0);
		this.maxWindowSize = maxWindowSize;
	}

	/**
	 * Returns the percentage of the population that, during a restart, are introduced into the new population.
	 * 
	 * @return the injection rate
	 */
	public double getInjectionRate() {
		return injectionRate;
	}

	/**
	 * Sets the percentage of the population that, during a restart, are introduced into the new population.  The
	 * population will be resized to hold {@code archive.size() / injectionRate} solutions.
	 * 
	 * @param injectionRate the injection rate
	 */
	@Property
	public void setInjectionRate(double injectionRate) {
		Validate.that("injectionRate", injectionRate).isGreaterThan(0.0);
		this.injectionRate = injectionRate;
	}

	/**
	 * Returns the minimum size of the population.
	 * 
	 * @return the minimum size of the population
	 */
	public int getMinimumPopulationSize() {
		return minimumPopulationSize;
	}

	/**
	 * Sets the minimum size of the population.
	 * 
	 * @param minimumPopulationSize the minimum size of the population
	 */
	@Property
	public void setMinimumPopulationSize(int minimumPopulationSize) {
		Validate.that("minimumPopulationSize", minimumPopulationSize).isGreaterThan(0);
		this.minimumPopulationSize = minimumPopulationSize;
	}

	/**
	 * Returns the maximum size of the population.
	 * 
	 * @return the maximum size of the population
	 */
	public int getMaximumPopulationSize() {
		return maximumPopulationSize;
	}

	/**
	 * Sets the maximum size of the population.
	 * 
	 * @param maximumPopulationSize the maximum size of the population
	 */
	@Property
	public void setMaximumPopulationSize(int maximumPopulationSize) {
		Validate.that("maximumPopulationSize", maximumPopulationSize).isGreaterThan(0);
		this.maximumPopulationSize = maximumPopulationSize;
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
	
	@Override
	public void onRegister(Algorithm algorithm) {
		super.onRegister(algorithm);
		
		EvolutionaryAlgorithm ea = Validate.that("algorithm", algorithm).isA(EvolutionaryAlgorithm.class);
		Validate.that("archive", ea.getArchive()).isNotNull();
	}

	/**
	 * Performs a check to determine if a restart should occur.  Returns {@code RestartType.NONE} if no restart should
	 * occur; or {@code RestartType.HARD} if the population-to-archive ratio exceeds {@code populationRatio} by more
	 * than {@code 25%} or the number of fitness evaluations since the last restart exceeds {@code maxWindowSize}.
	 * 
	 * @param algorithm the evolutionary algorithm
	 * @return {@code RestartType.NONE} if no restart should occur; or {@code RestartType.HARD} if the
	 *         population-to-archive ratio exceeds {@code populationRatio} by more than {@code 25%} or
	 *         if the number of fitness evaluations since the last restart exceeds {@code maxWindowSize}
	 */
	protected RestartType check(Algorithm algorithm) {
		EvolutionaryAlgorithm ea = (EvolutionaryAlgorithm)algorithm;
		
		int populationSize = ea.getPopulation().size();
		double targetSize = ea.getArchive().size() / injectionRate;

		if (iteration - iterationAtLastRestart >= maxWindowSize) {
			return RestartType.HARD;
		} else if ((targetSize >= minimumPopulationSize) && (targetSize <= maximumPopulationSize) &&
				(Math.abs(populationSize - targetSize) > (0.25 * targetSize))) {
			return RestartType.HARD;
		} else {
			return RestartType.NONE;
		}
	}

	/**
	 * Performs a restart.  If the type is {@code RestartType.HARD}, the population is emptied, resized and filled with
	 * solutions selected and mutated from the archive.  If the type is {@code RestartType.SOFT}, the population is not
	 * emptied; new solutions are only added to fill any empty slots.
	 * 
	 * @param algorithm the evolutionary algorithm
	 * @param type the type of restart
	 */
	protected void restart(Algorithm algorithm, RestartType type) {
		EvolutionaryAlgorithm ea = (EvolutionaryAlgorithm)algorithm;
		
		Population population = ea.getPopulation();
		NondominatedPopulation archive = ea.getArchive();

		if (type.equals(RestartType.HARD)) {
			population.clear();
			population.addAll(archive);
		}

		int newPopulationSize = (int)(archive.size() / injectionRate);

		if (newPopulationSize < minimumPopulationSize) {
			newPopulationSize = minimumPopulationSize;
		} else if (newPopulationSize > maximumPopulationSize) {
			newPopulationSize = maximumPopulationSize;
		}
		
		LoggingExtension.info(algorithm, "{0} restarting; Type: {1}, New Population Size: {2}",
				algorithm.getClass().getSimpleName(), type, newPopulationSize);

		while (population.size() < newPopulationSize) {
			Solution[] parents = restartSelection.select(restartVariation.getArity(), archive);
			Solution[] children = restartVariation.evolve(parents);

			for (Solution child : children) {
				algorithm.evaluate(child);
				population.add(child);
				archive.add(child);
			}
		}

		if (type.equals(RestartType.HARD)) {
			iterationAtLastRestart = iteration;
		}

		listeners.fire().restarted(new RestartEvent(algorithm, type));
	}

	@Override
	public void doAction(Algorithm algorithm) {
		RestartType type = check(algorithm);

		if ((type != null) && !type.equals(RestartType.NONE)) {
			restart(algorithm, type);
		}
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeInt(iterationAtLastRestart);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		iterationAtLastRestart = stream.readInt();
	}

}
