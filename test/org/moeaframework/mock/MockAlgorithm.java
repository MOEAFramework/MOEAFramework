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
package org.moeaframework.mock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.algorithm.extension.Extensions;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation;

public class MockAlgorithm implements Algorithm {
	
	private int numberOfSteps;
	
	private int numberOfEvaluations;
	
	public MockAlgorithm() {
		super();
	}

	@Override
	public Problem getProblem() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NondominatedPopulation getResult() {
		throw new UnsupportedOperationException();
	}
	
	public int getNumberOfSteps() {
		return numberOfSteps;
	}
	
	public int getNumberOfEvaluationsPerStep() {
		return 10;
	}

	@Override
	public void step() {
		numberOfSteps++;
		numberOfEvaluations += getNumberOfEvaluationsPerStep();
	}

	@Override
	public void evaluate(Solution solution) {
		// do nothing
	}
	
	public void setNumberOfEvaluations(int numberOfEvaluations) {
		this.numberOfEvaluations = numberOfEvaluations;
	}

	@Override
	public int getNumberOfEvaluations() {
		return numberOfEvaluations;
	}
	
	@Override
	public boolean isInitialized() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void initialize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTerminated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void terminate() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Extensions getExtensions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		throw new UnsupportedOperationException();
	}
	
}
