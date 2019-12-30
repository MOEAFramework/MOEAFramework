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
package org.moeaframework.core.operator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.Vector;

public abstract class DistributionVariationTest {

	protected abstract void check(Solution[] parents, Solution[] offspring);

	protected Solution newSolution(double... variables) {
		Solution solution = new Solution(variables.length, 0);

		for (int i = 0; i < variables.length; i++) {
			solution.setVariable(i, new RealVariable(variables[i],
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
		}

		return solution;
	}

	protected double[] average(Solution[] solutions) {
		double[] average = new double[solutions[0].getNumberOfVariables()];

		for (Solution solution : solutions) {
			average = Vector.add(average, EncodingUtils.getReal(solution));
		}

		return Vector.divide(average, solutions.length);
	}

}
