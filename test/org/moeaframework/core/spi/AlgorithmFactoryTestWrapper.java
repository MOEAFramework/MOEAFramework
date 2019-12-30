/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.core.spi;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Properties;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Algorithm factory that instruments {@link Algorithm} instances with testing
 * code.
 */
public class AlgorithmFactoryTestWrapper extends AlgorithmFactory {
	
	/**
	 * The number of times the {@code terminate} method has been invoked.
	 */
	private int terminateCount;

	@Override
	public synchronized Algorithm getAlgorithm(String name,
			Properties properties, Problem problem) {
		final Algorithm algorithm = super.getAlgorithm(name, properties, 
				problem);
		
		return new Algorithm() {
			
			@Override
			public Problem getProblem() {
				return algorithm.getProblem();
			}

			@Override
			public NondominatedPopulation getResult() {
				return algorithm.getResult();
			}

			@Override
			public void step() {
				algorithm.step();
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
			public boolean isTerminated() {
				return algorithm.isTerminated();
			}

			@Override
			public void terminate() {
				algorithm.terminate();
				terminateCount++;
			}

			@Override
			public Serializable getState() throws NotSerializableException {
				return algorithm.getState();
			}

			@Override
			public void setState(Object state) throws NotSerializableException {
				algorithm.setState(state);
			}
			
		};
	}

	/**
	 * Returns the number of times the {@code terminate} method has been
	 * invoked.
	 * 
	 * @return the number of times the {@code terminate} method has been
	 *         invoked
	 */
	public int getTerminateCount() {
		return terminateCount;
	}
	
}