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
package org.moeaframework.problem.single;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.problem.AbstractProblem;

abstract class AbstractSingleObjectiveProblem extends AbstractProblem {
	
	public AbstractSingleObjectiveProblem(int numberOfVariables) {
		this(numberOfVariables, 1, 0);
	}
	
	public AbstractSingleObjectiveProblem(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
		super(numberOfVariables, numberOfObjectives, numberOfConstraints);
		
		if (numberOfObjectives != 1) {
			throw new IllegalArgumentException("numberOfObjectives must be 1 for single objective problems");
		}
	}
	
	/**
	 * Returns the reference set, typically a single solution unless the problem has multiple local minima, for this
	 * single objective problem.
	 * 
	 * @return the reference set
	 */
	public abstract NondominatedPopulation getReferenceSet();

}
