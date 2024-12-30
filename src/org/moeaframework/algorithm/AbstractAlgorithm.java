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
package org.moeaframework.algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.algorithm.extension.Extensions;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Stateful;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.validate.Validate;

/**
 * Abstract class providing default implementations for several {@link Algorithm} methods.
 * <p>
 * When creating a new subclass, one should:
 * <ol>
 *   <li>Use the {@link #evaluate} or {@link #evaluateAll} methods provided by this class.  Do not call
 *       {@link Problem#evaluate} directly as that will not count the number of function evaluations correctly.
 *   <li>When possible, prefer evaluating all solutions at once by calling {@link #evaluateAll}.  Doing so allows
 *       function evaluations to run in parallel when enabled.
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
	 * The extensions registered with this algorithm.
	 */
	private final Extensions extensions;

	/**
	 * Constructs an abstract algorithm for solving the specified problem.
	 * 
	 * @param problem the problem being solved
	 */
	public AbstractAlgorithm(Problem problem) {
		super();
		
		Validate.that("problem", problem).isNotNull();
		this.problem = problem;
		this.extensions = new Extensions(this);
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

	@Override
	public void initialize() {
		assertNotInitialized();
		initialized = true;
		extensions.onInitialize();
	}

	@Override
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
	 * {@inheritDoc}
	 * <p>
	 * Avoid overriding this method in subclasses.  Instead, prefer overriding {@link #initialize()} and
	 * {@link #iterate()} with any algorithm-specific details.
	 */
	@Override
	public void step() {
		terminated = false;
		
		if (!isInitialized()) {
			initialize();
		} else {
			iterate();
		}
		
		extensions.onStep();
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

	@Override
	public void terminate() {
		if (terminated) {
			throw new AlgorithmTerminationException(this, "algorithm already terminated");
		}

		terminated = true;
		extensions.onTerminate();
	}
	
	@Override
	public Extensions getExtensions() {
		return extensions;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, "algorithm not initialized");
		}
		
		Stateful.writeTypeSafety(stream, this);
		stream.writeInt(numberOfEvaluations);
		
		extensions.saveState(stream);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		assertNotInitialized();
		initialized = true;
		
		Stateful.checkTypeSafety(stream, this);
		numberOfEvaluations = stream.readInt();
		
		extensions.loadState(stream);
	}

}
