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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link C1_DTLZ3} class.
 */
public class C1_DTLZ3Test {
	
	@Test
	public void test() {
		test(2);
		test(3);
		test(5);
		test(8);
		test(10);
		test(15);
	}
	
	/**
	 * All optimal solutions from the DTLZ3 problem should be feasible.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public void test(int numberOfObjectives) {
		C1_DTLZ3 problem = new C1_DTLZ3(numberOfObjectives);
		
		for (int i = 0; i <TestThresholds.SAMPLES; i++) {
			Solution solution = problem.generate();
			problem.evaluate(solution);
			Assert.assertFalse(solution.violatesConstraints());
		}
	}

}
