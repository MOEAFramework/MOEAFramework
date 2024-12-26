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

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemWrapper;

/**
 * Problem factory which instruments {@link Problem} instances with testing code.
 */
public class ProblemFactoryTestWrapper extends ProblemFactory {
	
	private int closeCount;

	@Override
	public synchronized Problem getProblem(String name) {
		return new CloseWrapper(super.getProblem(name));
	}
	
	/**
	 * Returns the number of times the {@code close} method has been invoked.
	 * 
	 * @return the number of times the {@code close} method has been invoked
	 */
	public int getCloseCount() {
		return closeCount;
	}
	
	private class CloseWrapper extends ProblemWrapper implements AnalyticalProblem {

		protected CloseWrapper(Problem problem) {
			super(problem);
		}

		@Override
		public Solution generate() {
			if (problem instanceof AnalyticalProblem analyticalProblem) {
				return analyticalProblem.generate();
			} else {
				throw new FrameworkException(problem.getClass() + " does not implement " + AnalyticalProblem.class);
			}
		}
		
		@Override
		public void close() {
			super.close();
			closeCount++;
		}
		
	}
	
}
