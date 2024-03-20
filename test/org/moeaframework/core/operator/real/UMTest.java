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
package org.moeaframework.core.operator.real;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.CIRunner;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.DistributionVariationTest;
import org.moeaframework.core.variable.RealVariable;

@RunWith(CIRunner.class)
public class UMTest extends DistributionVariationTest<UM> {

	@Override
	public UM createInstance() {
		return new UM(1.0);
	}

	@Test
	@Retryable
	public void testDistribution() {
		UM um = createInstance();

		Solution parent = new Solution(2, 0);
		parent.setVariable(0, new RealVariable(2.0, 0.0, 10.0));
		parent.setVariable(1, new RealVariable(-2.0, -2.0, 5.0));

		Solution[] parents = new Solution[] { parent };

		Solution[] offspring = new Solution[TestThresholds.SAMPLES];

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			offspring[i] = um.evolve(parents)[0];
		}

		checkDistribution(parents, offspring);
	}

	@Override
	protected void checkDistribution(Solution[] parents, Solution[] offspring) {
		Solution parent = parents[0];

		double[] average = average(offspring);

		for (int i = 0; i < parent.getNumberOfVariables(); i++) {
			RealVariable v = (RealVariable)parent.getVariable(i);

			Assert.assertEquals((v.getLowerBound() + v.getUpperBound()) / 2.0, average[i],
					TestThresholds.VARIATION_EPS);
		}
	}

}
