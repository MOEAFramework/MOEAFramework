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
package org.moeaframework.core.termination;

import java.time.Duration;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Wait;
import org.moeaframework.mock.MockAlgorithm;

public class CompoundTerminationConditionTest {
	
	@Test
	public void testNFE() {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxElapsedTime timeTermination = new MaxElapsedTime(Duration.ofSeconds(1));
		MaxFunctionEvaluations nfeTermination = new MaxFunctionEvaluations(10000);
		CompoundTerminationCondition compound = new CompoundTerminationCondition(timeTermination, nfeTermination);
		
		compound.initialize(algorithm);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Wait.spinFor(Duration.ofMillis(250));
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Wait.spinFor(Duration.ofMillis(250));
		algorithm.setNumberOfEvaluations(10001);
		Assert.assertTrue(compound.shouldTerminate(algorithm));
	}
	
	@Test
	public void testTime() {
		MockAlgorithm algorithm = new MockAlgorithm();
		MaxElapsedTime timeTermination = new MaxElapsedTime(Duration.ofSeconds(1));
		MaxFunctionEvaluations nfeTermination = new MaxFunctionEvaluations(10000);
		CompoundTerminationCondition compound = new CompoundTerminationCondition(timeTermination, nfeTermination);
		
		compound.initialize(algorithm);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Wait.spinFor(Duration.ofMillis(550));
		algorithm.setNumberOfEvaluations(1000);
		Assert.assertFalse(compound.shouldTerminate(algorithm));
		
		Wait.spinFor(Duration.ofMillis(550));
		algorithm.setNumberOfEvaluations(2000);
		Assert.assertTrue(compound.shouldTerminate(algorithm));
	}

}
