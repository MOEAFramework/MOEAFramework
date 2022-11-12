package org.moeaframework.parallel.island.executor;

import java.io.Closeable;

import org.moeaframework.core.NondominatedPopulation;

/**
 * Executes an island model strategy.  This class is responsible for taking the
 * conceptual design of the island model, as described in {@see IslandModel} and
 * executing it on physical hardware, whether that is a single core, multiple cores,
 * or multiple machines.
 */
public interface IslandExecutor extends Closeable {

	/**
	 * Executes this island model for the given number of function evaluations.
	 * 
	 * @param maxEvaluations the maximum number of evaluations across all islands
	 * @return the resulting non-dominated population aggregated across all islands
	 */
	public NondominatedPopulation run(int maxEvaluations);

}