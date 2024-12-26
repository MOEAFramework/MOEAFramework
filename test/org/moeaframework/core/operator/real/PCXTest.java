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
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentCentricVariationTest;

@RunWith(CIRunner.class)
public class PCXTest extends ParentCentricVariationTest<PCX> {
	
	@Override
	public PCX createInstance() {
		return new PCX(3, TestThresholds.SAMPLES);
	}
	
	@Override
	public boolean isTypeSafe() {
		return false;
	}

	@Test
	@Retryable
	public void testFullDistribution() {
		PCX pcx = createInstance();

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0), newSolution(0.0, 1.0), newSolution(1.0, 0.0) };
		Solution[] offspring = pcx.evolve(parents);

		checkDistribution(parents, offspring);
	}

	@Test
	@Retryable
	public void testPartialDistribution() {
		PCX pcx = createInstance();

		Solution[] parents = new Solution[] { newSolution(0.0, 0.0), newSolution(0.0, 1.0), newSolution(0.0, 2.0) };
		Solution[] offspring = pcx.evolve(parents);

		checkDistribution(parents, offspring);
	}

}
