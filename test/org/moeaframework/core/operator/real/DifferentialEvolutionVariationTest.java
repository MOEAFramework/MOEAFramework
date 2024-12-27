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
package org.moeaframework.core.operator.real;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.MeanCentricVariationTest;
import org.moeaframework.core.variable.RealVariable;

@RunWith(CIRunner.class)
public class DifferentialEvolutionVariationTest extends MeanCentricVariationTest<DifferentialEvolutionVariation> {
	
	@Override
	public DifferentialEvolutionVariation createInstance() {
		return new DifferentialEvolutionVariation(1.0, 1.0);
	}
	
	@Override
	public boolean isTypeSafe() {
		return false;
	}
	
	@Test
	public void testOffspring() {
		DifferentialEvolutionVariation de = createInstance();

		Solution s1 = new Solution(2, 0);
		s1.setVariable(0, new RealVariable(-10.0, 10.0).withValue(0.0));
		s1.setVariable(1, new RealVariable(-10.0, 10.0).withValue(0.0));

		Solution s2 = new Solution(2, 0);
		s2.setVariable(0, new RealVariable(-10.0, 10.0).withValue(1.0));
		s2.setVariable(1, new RealVariable(-10.0, 10.0).withValue(0.0));

		Solution s3 = new Solution(2, 0);
		s3.setVariable(0, new RealVariable(-10.0, 10.0).withValue(0.0));
		s3.setVariable(1, new RealVariable(-10.0, 10.0).withValue(0.0));

		Solution s4 = new Solution(2, 0);
		s4.setVariable(0, new RealVariable(-10.0, 10.0).withValue(5.0));
		s4.setVariable(1, new RealVariable(-10.0, 10.0).withValue(5.0));

		Solution[] parents = new Solution[] { s1, s2, s3, s4 };
		
		Solution offspring = de.evolve(parents)[0];
		
		// the offspring will be centered at s4 and offset in the direction (s2 - s3), resulting in the point (6, 5)
		Assert.assertEquals(6.0, RealVariable.getReal(offspring.getVariable(0)), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(5.0, RealVariable.getReal(offspring.getVariable(1)), TestThresholds.LOW_PRECISION);
	}

}
