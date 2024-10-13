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
package org.moeaframework.problem.WFG;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;

public class WFG1Test extends WFGTest {
	
	@Test
	public void test() {
		Problem problem = new WFG1(2);
		
		Assert.assertArrayEquals(new double[] { 1.0, 5.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.0, 1.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	@Ignore("JMetal produces incorrect solutions for some results, see notes")
	public void testJMetal2D() {
		testAgainstJMetal("WFG1_2");
	}
	
	@Test
	@Ignore("JMetal produces incorrect solutions for some results, see notes")
	public void testJMetal3D() {
		testAgainstJMetal("WFG1_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("WFG1", 2);
		assertProblemDefined("WFG1_2", 2);
		assertProblemDefined("WFG1_3", 3);
	}
	
	@Test
	public void testGenerate() {
		testGenerate("WFG1_2");
		testGenerate("WFG1_3");
	}
	
	/**
	 * WFG1 randomly experienced CI failures when compared against JMetal.  The original WFG website is no longer
	 * available, but I found two versions of the original codes, which are now archived at:
	 * 
	 *     https://github.com/MOEAFramework/Archive/blob/main/WFG_v2006.03.28.tar.gz
	 *     https://github.com/MOEAFramework/PISA/blob/main/variators/wfg_c_source.rar
	 * 
	 * Below, we are comparing one such solution that produced different results, using the result from the original
	 * WFG codes for validation.
	 */
	@Test
	public void testKnownGoodSolution() {
		Problem problem = ProblemFactory.getInstance().getProblem("WFG1_2");
		Solution solution = MockSolution.of(problem).at(1.8389005583162747, 3.6362558539762473, 0.12275000703991479, 1.3456684852354126, 3.4999999596260922, 2.1963726044829377, 6.692140537740539, 1.5934349970960149, 3.0573993595302524, 17.46985254359542, 19.95946267739649);
		
		problem.evaluate(solution);
		
		Assert.assertArrayEquals(new double[] { 2.96206, 0.967335 }, solution.getObjectiveValues(), 0.00001);
	}
	
	@Test(expected = AssertionError.class)
	public void testJMetalOnKnownGoodSolution() {
		assumeJMetalExists();
		
		Problem problem = ProblemFactory.getInstance().getProblem("WFG1_2-JMetal");
		Solution solution = MockSolution.of(problem).at(1.8389005583162747, 3.6362558539762473, 0.12275000703991479, 1.3456684852354126, 3.4999999596260922, 2.1963726044829377, 6.692140537740539, 1.5934349970960149, 3.0573993595302524, 17.46985254359542, 19.95946267739649);
		
		problem.evaluate(solution);
		
		Assert.assertArrayEquals(new double[] { 2.96206, 0.967335 }, solution.getObjectiveValues(), 0.00001);
	}

}
