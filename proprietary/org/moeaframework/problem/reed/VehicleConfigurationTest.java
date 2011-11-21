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
import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link VehicleConfiguration} class.
 */
public class VehicleConfigurationTest {
	
	@Test
	public void test() {
		VehicleConfiguration problem = new VehicleConfiguration();
		Solution solution = problem.newSolution();
		
		CoreUtils.fillVariablesFromDoubleArray(solution, new double[] {
				0.0028779285, 0.97251445, 0.86261487, 0.97573525, 0.15289536,
				0.77597964, 0.8411738, 6, 0.04072624, 0.24211216, 0.54441357 });
		
		problem.evaluate(solution);
		
		Assert.assertArrayEquals(new double[] { 0.5918615866923137, 
				-0.906229872798847, -0.9400817610088182, -1.0289089483281288, 
				-1.0256818592181514 }, solution.getObjectives(), Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 351.6212170420549 }, 
				solution.getConstraints(), Settings.EPS);
		
		Assert.assertEquals(0.8409522156570686, 
				(Double)solution.getAttribute("mass"), Settings.EPS);
	}

}
