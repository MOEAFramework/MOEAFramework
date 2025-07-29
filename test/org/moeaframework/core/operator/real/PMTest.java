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
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.MeanCentricVariationTest;
import org.moeaframework.core.variable.RealVariable;

@RunWith(CIRunner.class)
public class PMTest extends MeanCentricVariationTest<PM> {
	
	@Override
	public PM createInstance() {
		return new PM(1.0, 20.0);
	}

	@Test
	@Retryable
	public void testDistribution() {
		PM pm = createInstance();

		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(-10.0, 10.0).withValue(1.0));
		solution.setVariable(1, new RealVariable(-10.0, 10.0).withValue(-1.0));

		Solution[] parents = new Solution[] { solution };

		Solution[] offspring = new Solution[TestEnvironment.SAMPLES];

		for (int i = 0; i < TestEnvironment.SAMPLES; i++) {
			offspring[i] = pm.evolve(parents)[0];
		}

		checkDistribution(parents, offspring);
	}

}
