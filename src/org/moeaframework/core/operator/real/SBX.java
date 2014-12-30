/* Copyright 2009-2015 David Hadka
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
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Simulated binary crossover (SBX) operator.  SBX attempts to simulate the 
 * offspring distribution of binary-encoded single-point crossover on 
 * real-valued decision variables.  An example of this distribution,
 * which favors offspring nearer to the two parents, is shown below.
 * <p>
 * <img src="doc-files/SBX-1.png" alt="Example SBX operator distribution" />
 * <p>
 * The distribution index controls the shape of the offspring distribution. 
 * Larger values for the distribution index generates offspring closer to the 
 * parents.
 * <p>
 * This operator is type-safe.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Agrawal, R. B.  "Simulated Binary Crossover for Continuous
 *       Search Space."  Indian Institute of Technology, Kanpur, India.  
 *       Technical Report No. IITK/ME/SMD-94027, 1994.
 * </ol>
 */
public class SBX implements Variation {

	/**
	 * The probability of applying this SBX operator to each variable.
	 */
	private final double probability;

	/**
	 * The distribution index of this SBX operator.
	 */
	private final double distributionIndex;

	/**
	 * Constructs a SBX operator with the specified probability and
	 * distribution index.
	 * 
	 * @param probability the probability of applying this SBX operator to each
	 *        variable
	 * @param distributionIndex the distribution index of this SBX operator
	 */
	public SBX(double probability, double distributionIndex) {
		this.probability = probability;
		this.distributionIndex = distributionIndex;
	}

	/**
	 * Returns the probability of applying this SBX operator to each variable.
	 * 
	 * @return the probability of applying this SBX operator to each variable
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Returns the distribution index of this SBX operator.
	 * 
	 * @return the distribution index of this SBX operator
	 */
	public double getDistributionIndex() {
		return distributionIndex;
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		if (PRNG.nextDouble() <= probability) {
			for (int i = 0; i < result1.getNumberOfVariables(); i++) {
				Variable variable1 = result1.getVariable(i);
				Variable variable2 = result2.getVariable(i);

				if (PRNG.nextBoolean() && (variable1 instanceof RealVariable)
						&& (variable2 instanceof RealVariable)) {
					evolve((RealVariable)variable1, (RealVariable)variable2,
							distributionIndex);
				}
			}
		}

		return new Solution[] { result1, result2 };
	}

	/*
	 * The following source code is modified from the DTLZ variator module for
	 * PISA. This implementation was chosen over Kalyanmoy Deb's original SBX
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
	 * Evolves the specified variables using the SBX operator.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @param distributionIndex the distribution index of this SBX operator
	 */
	public static void evolve(RealVariable v1, RealVariable v2,
			double distributionIndex) {
		double x0 = v1.getValue();
		double x1 = v2.getValue();

		double dx = Math.abs(x1 - x0);

		if (dx > Settings.EPS) {
			double lb = v1.getLowerBound();
			double ub = v1.getUpperBound();
			double bl;
			double bu;
			
			if (x0 < x1) {
				bl = 1 + 2 * (x0 - lb) / dx;
				bu = 1 + 2 * (ub - x1) / dx;
			} else {
				bl = 1 + 2 * (x1 - lb) / dx;
				bu = 1 + 2 * (ub - x0) / dx;
			}

			//use symmetric distributions
			if (bl < bu) {
				bu = bl;
			} else {
				bl = bu;
			}

			double p_bl = 1 - 1 / (2 * Math.pow(bl, distributionIndex + 1));
			double p_bu = 1 - 1 / (2 * Math.pow(bu, distributionIndex + 1));
			double u = PRNG.nextDouble();
			
			//prevent out-of-bounds values if PRNG draws the value 1.0
			if (u == 1.0) {
				u = Math.nextAfter(u, -1.0);
			}
			
			double u0 = u * p_bl;
			double u1 = u * p_bu;
			double b0;
			double b1;

			if (u0 <= 0.5) {
				b0 = Math.pow(2 * u0, 1 / (distributionIndex + 1));
			} else {
				b0 = Math.pow(0.5 / (1 - u0), 1 / (distributionIndex + 1));
			}

			if (u1 <= 0.5) {
				b1 = Math.pow(2 * u1, 1 / (distributionIndex + 1));
			} else {
				b1 = Math.pow(0.5 / (1 - u1), 1 / (distributionIndex + 1));
			}

			if (x0 < x1) {
				v1.setValue(0.5 * (x0 + x1 + b0 * (x0 - x1)));
				v2.setValue(0.5 * (x0 + x1 + b1 * (x1 - x0)));
			} else {
				v1.setValue(0.5 * (x0 + x1 + b1 * (x0 - x1)));
				v2.setValue(0.5 * (x0 + x1 + b0 * (x1 - x0)));
			}

			//this makes PISA's SBX compatible with other implementations
			//which swap the values
			if (PRNG.nextBoolean()) {
				double temp = v1.getValue();
				v1.setValue(v2.getValue());
				v2.setValue(temp);
			}
			
			//guard against out-of-bounds values
			if (v1.getValue() < lb) {
				v1.setValue(lb);
			} else if (v1.getValue() > ub) {
				v1.setValue(ub);
			}
			
			if (v2.getValue() < lb) {
				v2.setValue(lb);
			} else if (v2.getValue() > ub) {
				v2.setValue(ub);
			}
		}
	}

}
