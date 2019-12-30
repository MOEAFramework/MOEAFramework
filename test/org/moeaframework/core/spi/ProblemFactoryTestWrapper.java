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

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * Problem factory which instruments {@link Problem} instances with testing
 * code.
 */
public class ProblemFactoryTestWrapper extends ProblemFactory {
	
	/**
	 * The number of times the {@code close} method has been invoked.
	 */
	private int closeCount;

	@Override
	public synchronized Problem getProblem(String name) {
		final Problem problem = super.getProblem(name);
		
		if (problem instanceof AnalyticalProblem) {
			return new AnalyticalProblem() {
				
				@Override
				public String getName() {
					return problem.getName();
				}
	
				@Override
				public int getNumberOfVariables() {
					return problem.getNumberOfVariables();
				}
	
				@Override
				public int getNumberOfObjectives() {
					return problem.getNumberOfObjectives();
				}
	
				@Override
				public int getNumberOfConstraints() {
					return problem.getNumberOfConstraints();
				}
	
				@Override
				public void evaluate(Solution solution) {
					problem.evaluate(solution);
				}
	
				@Override
				public Solution newSolution() {
					return problem.newSolution();
				}
	
				@Override
				public void close() {
					problem.close();
					closeCount++;
				}

				@Override
				public Solution generate() {
					return ((AnalyticalProblem)problem).generate();
				}
				
			};
		} else {
			return new Problem() {
	
				@Override
				public String getName() {
					return problem.getName();
				}
	
				@Override
				public int getNumberOfVariables() {
					return problem.getNumberOfVariables();
				}
	
				@Override
				public int getNumberOfObjectives() {
					return problem.getNumberOfObjectives();
				}
	
				@Override
				public int getNumberOfConstraints() {
					return problem.getNumberOfConstraints();
				}
	
				@Override
				public void evaluate(Solution solution) {
					problem.evaluate(solution);
				}
	
				@Override
				public Solution newSolution() {
					return problem.newSolution();
				}
	
				@Override
				public void close() {
					problem.close();
					closeCount++;
				}
				
			};
		}
	}

	/**
	 * Returns the number of times the {@code close} method has been invoked.
	 * 
	 * @return the number of times the {@code close} method has been invoked
	 */
	public int getCloseCount() {
		return closeCount;
	}
	
}
