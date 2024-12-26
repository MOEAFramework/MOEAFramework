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
package org.moeaframework.core.spi;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.extension.AlgorithmWrapper;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.problem.Problem;

/**
 * Algorithm factory that instruments {@link Algorithm} instances with testing code.
 */
public class AlgorithmFactoryTestWrapper extends AlgorithmFactory {
	
	/**
	 * The number of times the {@code terminate} method has been invoked.
	 */
	private int terminateCount;

	@Override
	public synchronized Algorithm getAlgorithm(String name, TypedProperties properties, Problem problem) {
		final Algorithm algorithm = super.getAlgorithm(name, properties, problem);
		
		return new AlgorithmWrapper<>(algorithm) {

			@Override
			public void terminate() {
				super.terminate();
				terminateCount++;
			}
			
		};
	}

	/**
	 * Returns the number of times the {@code terminate} method has been invoked.
	 * 
	 * @return the number of times the {@code terminate} method has been invoked
	 */
	public int getTerminateCount() {
		return terminateCount;
	}
	
}