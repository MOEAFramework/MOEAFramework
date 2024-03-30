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
package org.moeaframework.problem;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.DTLZ.DTLZ2;

public class ScaledProblemTest {
	
	@Test
	public void testReferenceSetStaysNondominated() throws IOException {
		NondominatedPopulation original = NondominatedPopulation.loadReferenceSet(new File("./pf/DTLZ2.2D.pf"));
		
		ScaledProblem scaledProblem = new ScaledProblem(new DTLZ2(2), 2.0);
		NondominatedPopulation scaled = scaledProblem.loadScaledReferenceSet(new File("./pf/DTLZ2.2D.pf"));
		
		Assert.assertEquals(original.size(), scaled.size());
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testBase1LeavesSolutionUnchanged() {
		Problem problem = new DTLZ2(2);
		ScaledProblem scaledProblem = new ScaledProblem(problem, 1.0);
		
		Solution solution = problem.newSolution();
		Solution scaledSolution = solution.copy();
		
		problem.evaluate(solution);
		scaledProblem.evaluate(scaledSolution);
		
		Assert.assertArrayEquals(solution.getObjectives(), scaledSolution.getObjectives(), Settings.EPS);
	}

}
