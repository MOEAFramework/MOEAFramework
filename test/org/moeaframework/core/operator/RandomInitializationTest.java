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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.analysis.sensitivity.ProblemStub;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.tree.Rules;

public class RandomInitializationTest {

	@Test
	public void testMixedType() {
		Problem problem = new ProblemStub(6) {
			
			@Override
			public Solution newSolution() {
				Rules rules = new Rules();
				rules.populateWithDefaults();
				rules.setReturnType(Number.class);
				
				Solution solution = new Solution(6, 0);
				solution.setVariable(0, new RealVariable(0, 1));
				solution.setVariable(1, new BinaryVariable(10));
				solution.setVariable(2, new Permutation(4));
				solution.setVariable(3, new Grammar(4));
				solution.setVariable(4, new Program(rules));
				solution.setVariable(5, new Variable() {

					private static final long serialVersionUID = -5453570189207466169L;

					@Override
					public Variable copy() {
						throw new UnsupportedOperationException();
					}

					@Override
					public void randomize() {
						// do nothing
					}

				});
				return solution;
			}

		};

		Initialization initialization = new RandomInitialization(problem, 100);
		Assert.assertEquals(100, initialization.initialize().length);
	}

}
