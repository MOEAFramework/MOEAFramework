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
package org.moeaframework.core.fitness;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Indicator-based fitness using the additive &epsilon;-indicator.
 */
public class AdditiveEpsilonIndicatorFitnessEvaluator extends
		IndicatorFitnessEvaluator {

	/**
	 * Constructs an additive &epsilon;-indicator fitness evaluator.
	 * 
	 * @param problem the problem
	 */
	public AdditiveEpsilonIndicatorFitnessEvaluator(Problem problem) {
		super(problem);
	}

	/*
	 * The following method is modified from the IBEA implementation for the
	 * PISA framework, available at <a href="http://www.tik.ee.ethz.ch/pisa/">
	 * PISA Homepage</a>.
	 * 
	 * Copyright (c) 2002-2003 Swiss Federal Institute of Technology,
	 * Computer Engineering and Networks Laboratory. All rights reserved.
	 * 
	 * PISA - A Platform and Programming Language Independent Interface for
	 * Search Algorithms.
	 * 
	 * IBEA - Indicator Based Evoluationary Algorithm - A selector module
	 * for PISA
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
	@Override
	protected double calculateIndicator(Solution solution1, 
			Solution solution2) {
		double eps = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < getProblem().getNumberOfObjectives(); i++) {
			double temp_eps = solution1.getObjective(i) - 
					solution2.getObjective(i);

			if (temp_eps > eps) {
				eps = temp_eps;
			}
		}

		return eps;
	}

	@Override
	public boolean areLargerValuesPreferred() {
		return false;
	}

}
