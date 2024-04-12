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
package org.moeaframework.analysis;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.DTLZ.DTLZ2;

public class DefaultEpsilonsTest {
	
	@Test
	public void testDefault() {
		Assert.assertGreaterThan(DefaultEpsilons.getInstance().getEpsilons(new DTLZ2(3)).get(0), DefaultEpsilons.DEFAULT);
	}
	
	@Test
	public void testUndefinedProblem() {
		Assert.assertEquals(DefaultEpsilons.getInstance().getEpsilons(new MockRealProblem()).get(0), DefaultEpsilons.DEFAULT);
	}
	
	@Test
	public void testOverride() {
		Problem problem = new MockRealProblem();
		
		DefaultEpsilons.getInstance().override(problem, Epsilons.of(0.5));
		Assert.assertEquals(DefaultEpsilons.getInstance().getEpsilons(problem).get(0), 0.5);
		
		DefaultEpsilons.getInstance().clearOverrides();
		Assert.assertEquals(DefaultEpsilons.getInstance().getEpsilons(problem).get(0), DefaultEpsilons.DEFAULT);
	}
	
}
