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
package org.moeaframework.algorithm.extension;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;

/**
 * Wrapper for an algorithm.  Unless overridden, all methods call the same method on the wrapped instance.
 * 
 * @param <T> the type of the wrapped algorithm
 */
public class AlgorithmWrapper<T extends Algorithm> implements Algorithm, Configurable {

	/**
	 * The wrapped instance.
	 */
	private final T algorithm;

	/**
	 * Wraps the given algorithm.
	 * 
	 * @param algorithm the algorithm to wrap
	 */
	public AlgorithmWrapper(T algorithm) {
		super();
		this.algorithm = algorithm;
	}
	
	/**
	 * Returns the wrapped instance.
	 * 
	 * @return the wrapped instance
	 */
	public T getAlgorithm() {
		return algorithm;
	}
	
	@Override
	public String getName() {
		return algorithm.getName();
	}

	@Override
	public void evaluate(Solution solution) {
		algorithm.evaluate(solution);
	}

	@Override
	public int getNumberOfEvaluations() {
		return algorithm.getNumberOfEvaluations();
	}

	@Override
	public Problem getProblem() {
		return algorithm.getProblem();
	}

	@Override
	public void initialize() {
		algorithm.initialize();
	}

	@Override
	public boolean isInitialized() {
		return algorithm.isInitialized();
	}

	@Override
	public void step() {
		algorithm.step();
	}

	@Override
	public boolean isTerminated() {
		return algorithm.isTerminated();
	}

	@Override
	public void terminate() {
		algorithm.terminate();
	}
	
	@Override
	public NondominatedPopulation getResult() {
		return algorithm.getResult();
	}
	
	@Override
	public Extensions getExtensions() {
		return algorithm.getExtensions();
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		algorithm.saveState(stream);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		algorithm.loadState(stream);
	}

}
