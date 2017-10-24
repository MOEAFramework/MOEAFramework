/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem;

import org.apache.commons.math3.random.MersenneTwister;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.distributed.FutureSolution;

/**
 * A mock stochastic problem with one real variable. 
 * In this case, the f(x) = x + random.nextDouble();   
 */
public class MockRealStochasticProblem extends AbstractProblem {
	
	long evaluationID = 0;
	
	public MockRealStochasticProblem() {
		super(1, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		long randSeed;
		if (solution instanceof FutureSolution) {
			// if it's doing distributed evaluation, we'll get the unique ID that was assigned
			randSeed = ((FutureSolution) solution).getDistributedEvaluationID(); 
		} else {
			// otherwise it's single-threaded), and we just use our own 0-based counter
			randSeed = evaluationID++;
		}
		MersenneTwister rng = new MersenneTwister(randSeed);
		
		double x = EncodingUtils.getReal(solution.getVariable(0));
		solution.setObjective(0, x + rng.nextDouble());
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new RealVariable(0, 10));
		return solution;
	}

}
