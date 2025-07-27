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
package org.moeaframework.problem.WFG;

import org.junit.Ignore;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.NondominatedPopulation.DuplicateMode;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.ProblemTest;

@Ignore("Abstract test class")
public abstract class WFGTest extends ProblemTest {
	
	protected void testGenerate(String problemName) {
		WFG problem = (WFG)ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation result = new NondominatedPopulation(DuplicateMode.ALLOW_DUPLICATES);

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			result.add(problem.generate());
		}
		
		if (problem instanceof WFG2) {
			Assume.skip("WFG2 is disjoint and can generate dominated solutions");
		} else if (problem instanceof WFG4) {
			// WFG4 can occasionally produce dominated solutions, perhaps 1 in a million generated solutions.  In the
			// example shown below, note the position parameters (l) are identical up to the displayed precision:
			//
			//     Solution 1 (dominated):
			//         Variables: [0.7000017200470681, 3.9475224331698575, 2.0999999999999996, 2.8, 3.5, 4.199999999999999, 4.8999999999999995, 5.6, 6.3, 7.0, 7.699999999999999, 8.399999999999999]
			//         Objectives: [8.089901123300647E-9, 4.671290871480589E-9, 6.0]
			//
			//     Solution 2 (dominating):
			//         Variables: [0.6999996015780581, 0.4680639184448179, 2.0999999999999996, 2.8, 3.5, 4.199999999999999, 4.8999999999999995, 5.6, 6.3, 7.0, 7.699999999999999, 8.399999999999999]
			//         Objectives: [1.0662790015989127E-9, 2.2724923884704508E-9, 6.0]
			Assert.assertGreaterThanOrEqual(result.size(), TestThresholds.SAMPLES - 1);
		} else {
			Assert.assertEquals(result.size(), TestThresholds.SAMPLES);
		}
	}

}
