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
package org.moeaframework.core.penalty;

import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Validate;

/**
 * Penalty function based on a fixed offset plus the sum of constraint violations.
 */
public class SumOfConstraintsPenaltyFunction implements PenaltyFunction {
	
	private double offset;
	
	/**
	 * Constructs a new penalty function based on the sum of constraint violations.
	 */
	public SumOfConstraintsPenaltyFunction() {
		this(0.0);
	}
	
	/**
	 * Constructs a new penalty function based on the sum of constraint violations.
	 * 
	 * @param offset the initial offset applied to all penalties
	 */
	public SumOfConstraintsPenaltyFunction(double offset) {
		super();
		setOffset(offset);
	}
	
	/**
	 * Sets the base penalty applied to all infeasible solutions.
	 * 
	 * @param offset the constant offset applied to all penalties
	 */
	public void setOffset(double offset) {
		Validate.greaterThanOrEqualToZero("offset", offset);
		this.offset = offset;
	}
	
	/**
	 * Returns the base penalty applied to all infeasible solutions.
	 * 
	 * @return the constant offset applied to all penalties
	 */
	public double getOffset() {
		return offset;
	}

	@Override
	public double calculate(Solution solution) {
		double penalty = solution.isFeasible() ? 0.0 : offset + solution.getSumOfConstraintViolations();
		PenaltyFunction.setPenalty(solution, penalty);
		return penalty;
	}

}
