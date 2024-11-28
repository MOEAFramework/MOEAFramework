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
package org.moeaframework.examples.misc;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates solving a problem with mixed types.  In this example, we recreate the Srinivas problem using a binary
 * integer and real-valued decision variables.
 * 
 * Default operators will be provided for mixed types, by combining the operators for each type, but we can also supply
 * our own operators as demonstrated in this example.
 */
public class MixedTypesExample {
	
	public static class MixedTypesSrinivasProblem extends AbstractProblem {

		public MixedTypesSrinivasProblem() {
			super(2, 2, 2);
		}

		@Override
		public void evaluate(Solution solution) {
			int x = EncodingUtils.getInt(solution.getVariable(0));
			double y = EncodingUtils.getReal(solution.getVariable(1));
			double f1 = Math.pow(x - 2.0, 2.0) + Math.pow(y - 1.0, 2.0) + 2.0;
			double f2 = 9.0*x - Math.pow(y - 1.0, 2.0);
			double c1 = Math.pow(x, 2.0) + Math.pow(y, 2.0) - 225.0;
			double c2 = x - 3.0*y + 10.0;
			
			solution.setObjectiveValue(0, f1);
			solution.setObjectiveValue(1, f2);
			solution.setConstraintValue(0, c1);
			solution.setConstraintValue(1, c2);
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(2, 2, 2);
			
			solution.setVariable(0, EncodingUtils.newBinaryInt(-20, 20));
			solution.setVariable(1, EncodingUtils.newReal(-20.0, 20.0));
			
			solution.setConstraint(0, LessThanOrEqual.to(0.0));
			solution.setConstraint(1, LessThanOrEqual.to(0.0));
			
			return solution;
		}

	}
	
	public static void main(String[] args) {
		Problem problem = new MixedTypesSrinivasProblem();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.setVariation(new CompoundVariation(new SBX(), new HUX(), new PM(), new BitFlip()));
		algorithm.run(10000);
		
		algorithm.getResult().display();
	}

}
