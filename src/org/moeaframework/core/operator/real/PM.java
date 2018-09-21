/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.core.operator.real;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Polynomial mutation (PM) operator.  PM attempts to simulate the offspring 
 * distribution of binary-encoded bit-flip mutation on real-valued decision 
 * variables.  Similar to SBX, PM favors offspring nearer to the parent.
 * <p>
 * The distribution index controls the shape of the offspring distribution. 
 * Larger values for the distribution index generates offspring closer to the 
 * parents.
 * <p>
 * It is recommended each decision variable is mutated with a probability of
 * {@code 1 / L}, where {@code L} is the number of decision variables.  This
 * results in one mutation per offspring on average.
 * <p>
 * This operator is type-safe.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Goyal, M.  "A combined genetic adaptive search (GeneAS) 
 *       for engineering design."  Computer Science and Informatics, 
 *       26(4):30-45, 1996.
 * </ol>
 */
public class PM implements Variation {

	/**
	 * The probability this operator is applied to each decision variable.
	 */
	private final double probability;

	/**
	 * The distribution index controlling the shape of the polynomial mutation.
	 */
	private final double distributionIndex;

	/**
	 * Constructs a polynomial mutation operator with the specified probability
	 * and distribution index.
	 * 
	 * @param probability the probability this operator is applied to each
	 *        decision variable
	 * @param distributionIndex the distribution index controlling the shape of
	 *        the polynomial mutation.
	 */
	public PM(double probability, double distributionIndex) {
		super();
		this.probability = probability;
		this.distributionIndex = distributionIndex;
	}

	/**
	 * Returns the probability this operator is applied to each decision
	 * variable.
	 * 
	 * @return the probability this operator is applied to each decision
	 *         variable
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Returns the distribution index controlling the shape of the polynomial
	 * mutation.
	 * 
	 * @return the distribution index controlling the shape of the polynomial
	 *         mutation
	 */
	public double getDistributionIndex() {
		return distributionIndex;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();

		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);

			if ((PRNG.nextDouble() <= probability)
					&& (variable instanceof RealVariable)) {
				evolve((RealVariable)variable, distributionIndex);
			}
		}

		return new Solution[] { result };
	}

	/*
	 * The following source code is modified from the DTLZ variator module for
	 * PISA. This implementation was chosen over Kalyanmoy Deb's original PM
	 * implementation due to license incompatibilities with the LGPL. The DTLZ
	 * variator module license is provided below.
	 * 
	 * Copyright (c) 2002-2003 Swiss Federal Institute of Technology,
	 * Computer Engineering and Networks Laboratory. All rights reserved.
	 * 
	 * PISA - A Platform and Programming Language Independent Interface for
	 * Search Algorithms.
	 * 
	 * DTLZ - Scalable Test Functions for MOEAs - A variator module for PISA
	 * 
	 * Permission to use, copy, modify, and distribute this software and its
	 * documentation for any purpose, without fee, and without written
	 * agreement is hereby granted, provided that the above copyright notice
	 * and the following two paragraphs appear in all copies of this
	 * software.
	 * 
	 * IN NO EVENT SHALL THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER
	 * ENGINEERING AND NETWORKS LABORATORY BE LIABLE TO ANY PARTY FOR DIRECT,
	 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF
	 * THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE SWISS
	 * FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS
	 * LABORATORY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 * 
	 * THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND
	 * NETWORKS LABORATORY, SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING,
	 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
	 * FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS
	 * ON AN "AS IS" BASIS, AND THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY,
	 * COMPUTER ENGINEERING AND NETWORKS LABORATORY HAS NO OBLIGATION TO
	 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
	 */
	/**
	 * Mutates the specified variable using polynomial mutation.
	 * 
	 * @param v the variable to be mutated
	 * @param distributionIndex the distribution index controlling the shape of
	 *        the polynomial mutation
	 */
	public static void evolve(RealVariable v, double distributionIndex) {
		double u = PRNG.nextDouble();
		double x = v.getValue();
		double lb = v.getLowerBound();
		double ub = v.getUpperBound();
		double dx = ub - lb;
		double delta;

		if (u < 0.5) {
			double bl = (x - lb) / dx;
			double b = 2 * u + (1 - 2 * u)
					* (Math.pow(1 - bl, (distributionIndex + 1)));
			delta = Math.pow(b, (1.0 / (distributionIndex + 1))) - 1.0;
		} else {
			double bu = (ub - x) / dx;
			double b = 2 * (1 - u) + 2 * (u - 0.5)
					* (Math.pow(1 - bu, (distributionIndex + 1)));
			delta = 1.0 - Math.pow(b, (1.0 / (distributionIndex + 1)));
		}

		x = x + delta * dx;

		if (x < lb) {
			x = lb;
		} else if (x > ub) {
			x = ub;
		}

		v.setValue(x);
	}

	@Override
	public int getArity() {
		return 1;
	}

}
