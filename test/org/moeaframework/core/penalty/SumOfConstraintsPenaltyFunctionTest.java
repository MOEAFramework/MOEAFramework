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
package org.moeaframework.core.penalty;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Penalty;
import org.moeaframework.mock.MockSolution;

public class SumOfConstraintsPenaltyFunctionTest {
	
	private SumOfConstraintsPenaltyFunction penaltyFunction;
	
	@Before
	public void setUp() {
		penaltyFunction = new SumOfConstraintsPenaltyFunction();
	}
	
	@After
	public void tearDown() {
		penaltyFunction = null;
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidOffset() {
		penaltyFunction.setOffset(-1.0);
	}
	
	@Test
	public void testNoConstraints() {
		Solution solution = MockSolution.of();
		
		Assert.assertEquals(0.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
		
		penaltyFunction.setOffset(100.0);
		
		Assert.assertEquals(0.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testFeasible() {
		Solution solution = MockSolution.of().withConstraints(0.0);
		
		Assert.assertEquals(0.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
		
		penaltyFunction.setOffset(100.0);
		
		Assert.assertEquals(0.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testInfeasible() {
		Solution solution = MockSolution.of().withConstraints(1.0);
		
		Assert.assertEquals(1.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
		
		solution = MockSolution.of().withConstraints(1.0, 0.0, -2.0);
		
		Assert.assertEquals(3.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(3.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
		
		penaltyFunction.setOffset(100.0);
		
		Assert.assertEquals(103.0, penaltyFunction.calculate(solution), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(103.0, Penalty.getAttribute(solution), TestThresholds.HIGH_PRECISION);
	}

}
