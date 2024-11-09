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
package org.moeaframework.algorithm;

import java.util.Arrays;

import org.moeaframework.algorithm.extension.Extensible;
import org.moeaframework.core.Named;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Stateful;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.termination.MaxFunctionEvaluations;
import org.moeaframework.core.termination.TerminationCondition;
import org.moeaframework.problem.Problem;

/**
 * Interface for an optimization algorithm. An optimization algorithm operates by performing a series of optimization
 * steps, though the amount of work performed by each step depends on the algorithm. For example, an algorithm
 * may completely solve a problem in one step or may require hundreds of thousands of steps.
 */
public interface Algorithm extends Stateful, Extensible, Named {
	
	/**
	 * Returns the name of this algorithm.  Whenever possible, this name should match the name recognized by 
	 * {@link org.moeaframework.core.spi.AlgorithmFactory}.
	 * 
	 * @return the name of this algorithm
	 */
	@Override
	@Property(value="algorithm", readOnly=true)
	public String getName();

	/**
	 * Returns the problem being solved by this algorithm.
	 * 
	 * @return the problem being solved by this algorithm
	 */
	@Property(value="problem", readOnly=true)
	public Problem getProblem();

	/**
	 * Returns the current best-known result.
	 * 
	 * @return the current best-known result
	 */
	public NondominatedPopulation getResult();
	
	/**
	 * Returns {@code true} if this algorithm has been initialized; {@code false} otherwise.
	 * 
	 * @return {@code true} if this algorithm has been initialized; {@code false} otherwise
	 * @see #initialize()
	 */
	public boolean isInitialized();
	
	/**
	 * Performs any initialization that is required by this algorithm.  This method should only be called once, though
	 * the specific implementation may choose to no-op or throw {@link AlgorithmInitializationException} if called
	 * multiple times.
	 * <p>
	 * Implementations should always call {@code super.initialize()} to ensure the algorithm is initialized correctly.
	 * 
	 * @throws AlgorithmInitializationException if the algorithm has already been initialized
	 */
	public void initialize();

	/**
	 * Performs one logical step of this algorithm.  The amount of work performed depends on the implementation.  One
	 * invocation of this method may produce one or many trial solutions.
	 * <p>
	 * In general, calling this method after {@link #terminate()} is permitted.  When this happens,
	 * {@link #isTerminated()} is reset.  We recommend checking {@link #isTerminated()} after each step to detect when
	 * termination conditions are reached.  However, if the implementation is unable to continue, this method should
	 * throw {@link AlgorithmTerminationException}.
	 */
	public void step();
	
	/**
	 * Executes this algorithm, terminating when it reaches a target number of function evaluations.  Please note that
	 * algorithms may have additional termination conditions that could cause this method to return before reaching
	 * the target number of evaluations.  Use {@link #getNumberOfEvaluations()} to verify the actual number of
	 * evaluations.
	 * 
	 * @param evaluations the number of function evaluations
	 */
	public default void run(int evaluations) {
		run(new MaxFunctionEvaluations(evaluations));
	}
	
	/**
	 * Executes this algorithm until the terminal condition signals it to stop.
	 * 
	 * @param terminationCondition the termination condition
	 */
	public void run(TerminationCondition terminationCondition);
	
	/**
	 * Evaluates the specified solutions. This method calls {@link #evaluate(Solution)} on each of the solutions.
	 * Subclasses should prefer calling this method over {@code evaluate} whenever possible, as this ensures the
	 * solutions can be evaluated in parallel.
	 * 
	 * @param solutions the solutions to evaluate
	 */
	public default void evaluateAll(Iterable<Solution> solutions) {
		for (Solution solution : solutions) {
			evaluate(solution);
		}
	}
	
	/**
	 * Evaluates the specified solutions.  This method is equivalent to {@code evaluateAll(Arrays.asList(solutions))}.
	 * 
	 * @param solutions the solutions to evaluate
	 */
	public default void evaluateAll(Solution[] solutions) {
		evaluateAll(Arrays.asList(solutions));
	}

	/**
	 * Evaluates the specified solution for the problem being solved by this algorithm.
	 * 
	 * @param solution the solution to be evaluated
	 * @see Problem#evaluate(Solution)
	 */
	public void evaluate(Solution solution);

	/**
	 * Returns the number of times the {@code evaluate} method was invoked. This is the primary measure of runtime
	 * for optimization algorithms.
	 * 
	 * @return the number of times the {@code evaluate} method was invoked
	 */
	public int getNumberOfEvaluations();

	/**
	 * Returns {@code true} if this algorithm has reached its termination condition; {@code false} otherwise.
	 * 
	 * @return {@code true} if this algorithm has reached its termination condition; {@code false} otherwise
	 * @see #terminate()
	 */
	public boolean isTerminated();

	/**
	 * Called when the termination condition is reached and the run is complete.  This method is automatically called
	 * when using {@link #run(TerminationCondition)}, but can also be invoked directly if executing the algorithm
	 * step-by-step with {@link #step()}.
	 * <p>
	 * Implementations should always call {@code super.terminate()} to ensure the algorithm is terminated correctly.
	 */
	public void terminate();
	
}
