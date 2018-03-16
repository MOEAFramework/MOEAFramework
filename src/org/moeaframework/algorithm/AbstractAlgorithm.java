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
import java.util.Arrays;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Abstract class providing default implementations for several
 * {@link Algorithm} methods. All method extending this class must use the
 * {@link #evaluate} method to evaluate a solution. This is mandatory to ensure
 * the {@link #getNumberOfEvaluations()} method returns the correct result.
 * <p>
 * Subclasses should avoid overriding the {@link #step()} method and instead
 * override the {@link #initialize()} and {@link #iterate()} methods
 * individually.
 */
public abstract class AbstractAlgorithm implements Algorithm {

	/**
	 * The problem being solved.
	 */
	protected final Problem problem;

	/**
	 * The number of times the {@link #evaluate} method was invoked.
	 */
	protected int numberOfEvaluations;

	/**
	 * {@code true} if the {@link #initialize()} method has been invoked;
	 * {@code false} otherwise.
	 */
	protected boolean initialized;

	/**
	 * {@code true} if the {@link #terminate()} method has been invoked;
	 * {@code false} otherwise.
	 */
	protected boolean terminated;

	/**
	 * Constructs an abstract algorithm for solving the specified problem.
	 * 
	 * @param problem the problem being solved
	 */
	public AbstractAlgorithm(Problem problem) {
		super();
		this.problem = problem;
	}

	/**
	 * Evaluates the specified solutions. This method calls
	 * {@link #evaluate(Solution)} on each of the solutions. Subclasses should
	 * prefer calling this method over {@code evaluate} whenever possible,
	 * as this ensures the solutions can be evaluated in parallel.
	 * 
	 * @param solutions the solutions to evaluate
	 */
	public void evaluateAll(Iterable<Solution> solutions) {
		for (Solution solution : solutions) {
			evaluate(solution);
		}
	}
	
	/**
	 * Evaluates the specified solutions.  This method is equivalent to
	 * {@code evaluateAll(Arrays.asList(solutions))}.
	 * 
	 * @param solutions the solutions to evaluate
	 */
	public void evaluateAll(Solution[] solutions) {
		evaluateAll(Arrays.asList(solutions));
	}

	@Override
	public void evaluate(Solution solution) {
		problem.evaluate(solution);
		numberOfEvaluations++;
	}

	@Override
	public int getNumberOfEvaluations() {
		return numberOfEvaluations;
	}

	@Override
	public Problem getProblem() {
		return problem;
	}

	/**
	 * Performs any initialization that is required by this algorithm. This
	 * method is called automatically by the first invocation of
	 * {@link #step()}, but may also be called manually prior to any invocations
	 * of {@code step}. Implementations should always invoke
	 * {@code super.initialize()} to ensure the hierarchy is initialized
	 * correctly.
	 * 
	 * @throws AlgorithmInitializationException if the algorithm has already
	 *         been initialized
	 */
	protected void initialize() {
		if (initialized) {
			throw new AlgorithmInitializationException(this, 
					"algorithm already initialized");
		}

		initialized = true;
	}

	/**
	 * Returns {@code true} if the {@link #initialize()} method has been
	 * invoked; {@code false} otherwise.
	 * 
	 * @return {@code true} if the {@link #initialize()} method has been
	 *         invoked; {@code false} otherwise
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * This method first checks if the algorithm is initialized. If not, the
	 * {@link #initialize()} method is invoked. If initialized, all calls to
	 * {@code step} invoke {@link #iterate()}. Implementations should override
	 * the {@code initialize} and {@code iterate} methods in preference to
	 * modifying this method.
	 * 
	 * @throws AlgorithmTerminationException if the algorithm has already 
	 *         terminated
	 */
	@Override
	public void step() {
		if (isTerminated()) {
			throw new AlgorithmTerminationException(this, 
					"algorithm already terminated");
		} else if (!isInitialized()) {
			initialize();
		} else {
			iterate();
		}
	}

	/**
	 * Performs one iteration of the algorithm. This method should be
	 * overridden by implementations to perform each logical iteration of the
	 * algorithm.
	 */
	protected abstract void iterate();

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * Implementations should always invoke {@code super.terminate()} to ensure
	 * the hierarchy is terminated correctly. This method is automatically
	 * invoked during finalization, and need only be called directly if
	 * non-Java resources are in use.
	 * 
	 * @throws AlgorithmTerminationException if the algorithm has already 
	 *         terminated
	 */
	@Override
	public void terminate() {
		if (terminated) {
			throw new AlgorithmTerminationException(this, 
					"algorithm already terminated");
		}

		terminated = true;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (!isTerminated()) {
				terminate();
			}
		} finally {
			super.finalize();
		}
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		throw new NotSerializableException(getClass().getName());
	}

	@Override
	public void setState(Object state) throws NotSerializableException {
		throw new NotSerializableException(getClass().getName());
	}

}
