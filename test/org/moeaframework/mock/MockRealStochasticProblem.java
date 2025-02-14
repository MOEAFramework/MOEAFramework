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
package org.moeaframework.mock;

import org.apache.commons.math3.random.MersenneTwister;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.parallel.FutureSolution;

public class MockRealStochasticProblem extends MockProblem {
	
	long evaluationID = 0;
	
	public MockRealStochasticProblem() {
		super(1, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		long randSeed;
		if (solution instanceof FutureSolution futureSolution) {
			// if it's doing distributed evaluation, we'll get the unique ID that was assigned
			randSeed = futureSolution.getDistributedEvaluationID();
		} else {
			// otherwise it's single-threaded, and we just use our own 0-based counter
			randSeed = evaluationID++;
		}
		
		MersenneTwister rng = new MersenneTwister(randSeed);
		
		double x = RealVariable.getReal(solution.getVariable(0));
		solution.setObjectiveValue(0, x + rng.nextDouble());
	}

	@Override
	public Solution newSolution() {
		Solution solution = super.newSolution();
		solution.setVariable(0, new RealVariable(0, 10));
		return solution;
	}

}
