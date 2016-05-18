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
package org.moeaframework.core.termination;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link MaxFunctionEvaluations} class.
 */
public class MaxFunctionEvaluationsTest {

	@Test
	public void test() {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxFunctionEvaluations termination = new MaxFunctionEvaluations(10000);
		
		termination.initialize(algorithm);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(termination.shouldTerminate(algorithm));
		
		algorithm.setNumberOfEvaluations(10001);
		Assert.assertTrue(termination.shouldTerminate(algorithm));
	}
	
}
