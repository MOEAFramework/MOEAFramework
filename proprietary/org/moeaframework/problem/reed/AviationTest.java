/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.problem.reed;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;

/**
 * Tests the {@link Aviation} class.
 */
public class AviationTest {
	
	@Test
	public void testEvaluate() {
		OldAviation oldAviation = new OldAviation();
		Aviation newAviation = new Aviation();
		Initialization initialization = new RandomInitialization(oldAviation, 
				TestThresholds.SAMPLES);
		
		for (Solution oldSolution : initialization.initialize()) {
			Solution newSolution = oldSolution.copy();
			
			oldAviation.evaluate(oldSolution);
			newAviation.evaluate(newSolution);
			
			TestUtils.assertEquals(oldSolution, newSolution);
			
			//more precise comparison of objectives and constraints
			Assert.assertArrayEquals(oldSolution.getObjectives(), 
					newSolution.getObjectives(), Settings.EPS);
			Assert.assertArrayEquals(oldSolution.getConstraints(), 
					newSolution.getConstraints(), Settings.EPS);
			
			//test for NaN in PFPF
			Assert.assertFalse(Double.isNaN(newSolution.getObjective(9)));
		}
	}
	
	@Test
	public void testNewSolution() {
		OldAviation oldAviation = new OldAviation();
		Aviation newAviation = new Aviation();
		
		TestUtils.assertEquals(oldAviation.newSolution(), 
				newAviation.newSolution());
	}

}
