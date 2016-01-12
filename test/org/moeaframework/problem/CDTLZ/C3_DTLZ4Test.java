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
package org.moeaframework.problem.CDTLZ;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link C3_DTLZ4} class.
 */
public class C3_DTLZ4Test {
	
	/**
	 * Visual test of the Pareto front.  Copy the output and generate a plot,
	 * such as with R, and compare against the figures in Jain and Deb (2014):
	 * <pre>
	 *     library(rgl)
	 *     x = matrix(c(<paste text>), ncol=3, byrow=T)
	 *     plot3d(x)
	 * </pre>
	 */
	@Test
	@Ignore("skip visual tests")
	public void visualTest() {
		NondominatedPopulation result = new Executor()
				.withProblemClass(C3_DTLZ4.class, 3)
				.withAlgorithm("NSGAIII")
				.withMaxEvaluations(100000)
				.run();

		for (Solution solution : result) {
			if (!solution.violatesConstraints()) {
				System.out.format("%.4f, %.4f, %.4f,%n",
						solution.getObjective(0),
						solution.getObjective(1),
						solution.getObjective(2));
			}
		}
	}

}
