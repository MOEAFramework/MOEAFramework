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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Stateful;
import org.moeaframework.core.configuration.Validate;

/**
 * Abstract class providing default implementations for several {@link Algorithm} methods.
 * <p>
 * When creating a new subclass, one should:
 * <ol>
 *   <li>Use the {@link #evaluate} or {@link #evaluateAll} methods provided by this class. Do not call
 *       {@link Problem#evaluate} directly as that will not count the number of function evaluations correctly.
 *   <li>When possible, prefer evaluating all solutions at once by calling {@link #evaluateAll}. Doing so allows
 *       function evaluations to run in parallel when enabled (see {@code Executor#distributeOnAllCores()}).
 *   <li>Implement the algorithm by overriding the {@link #initialize()} and {@link #iterate()} methods.
 * </ol>
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
	 * {@code true} if the {@link #initialize()} method has been invoked; {@code false} otherwise.
	 */
	protected boolean initialized;

	/**
	 * {@code true} if the {@link #terminate()} method has been invoked; {@code false} otherwise.
	 */
	protected boolean terminated;

	/**
	 * Constructs an abstract algorithm for solving the specified problem.
	 * 
	 * @param problem the problem being solved
	 */
	public AbstractAlgorithm(Problem problem) {
		super();
		
		Validate.notNull("problem", problem);
		this.problem = problem;
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
	 * Performs any initialization that is required by this algorithm.  This method is called automatically on the
	 * first invocation of {@link #step()}.  Implementations should always invoke {@code super.initialize()} to ensure
	 * the algorithm is initialized correctly.
	 * 
	 * @throws AlgorithmInitializationException if the algorithm has already been initialized
	 */
	protected void initialize() {
		assertNotInitialized();
		initialized = true;
	}

	/**
	 * Returns {@code true} if the {@link #initialize()} method has been invoked; {@code false} otherwise.
	 * 
	 * @return {@code true} if the {@link #initialize()} method has been invoked; {@code false} otherwise
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * Throws an exception if the algorithm is initialized.  Use this anywhere to check and fail if the algorithm is
	 * already initialized.
	 * 
	 * @throws AlgorithmInitializationException if the algorithm is initialized
	 */
	public void assertNotInitialized() {
		if (initialized) {
			throw new AlgorithmInitializationException(this, "algorithm already initialized");
		}
	}

	/**
	 * This method first checks if the algorithm is initialized.  If not, the {@link #initialize()} method is invoked.
	 * Once initialized, all subsequent calls to {@code step} invoke {@link #iterate()}.  Implementations should
	 * override the {@code initialize} and {@code iterate} methods in preference to modifying this method.
	 * 
	 * @throws AlgorithmTerminationException if the algorithm has already terminated
	 */
	@Override
	public void step() {
		if (isTerminated()) {
			throw new AlgorithmTerminationException(this, "algorithm already terminated");
		} else if (!isInitialized()) {
			initialize();
		} else {
			iterate();
		}
	}

	/**
	 * Performs one iteration of the algorithm.  This method should be overridden by implementations to perform each
	 * logical iteration of the algorithm.
	 */
	protected abstract void iterate();

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * Implementations should always invoke {@code super.terminate()} to ensure the hierarchy is terminated correctly.
	 * 
	 * @throws AlgorithmTerminationException if the algorithm has already terminated
	 */
	@Override
	public void terminate() {
		if (terminated) {
			throw new AlgorithmTerminationException(this, "algorithm already terminated");
		}

		terminated = true;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, "algorithm not initialized");
		}
		
		Stateful.writeTypeSafety(stream, this);
		stream.writeInt(numberOfEvaluations);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		assertNotInitialized();
		initialized = true;
		
		Stateful.checkTypeSafety(stream, this);
		numberOfEvaluations = stream.readInt();
	}

}
