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
import org.moeaframework.core.Settings;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.util.PropertyScope;

public class DefaultEpsilonsTest {
	
	@Test
	public void testKnownProblemOverridesDefault() {
		Assert.assertNotSame(DefaultEpsilons.getInstance().getEpsilons(new DTLZ2(3)), DefaultEpsilons.DEFAULT);
	}
	
	@Test
	public void testUndefinedProblemReturnsDefault() {
		Assert.assertSame(DefaultEpsilons.getInstance().getEpsilons(new MockRealProblem()), DefaultEpsilons.DEFAULT);
	}
	
	@Test
	public void testOverride() {
		Problem problem = new MockRealProblem();
		
		DefaultEpsilons.getInstance().override(problem, Epsilons.of(0.5));
		Assert.assertEquals(DefaultEpsilons.getInstance().getEpsilons(problem), Epsilons.of(0.5));
		
		DefaultEpsilons.getInstance().clearOverrides();
		Assert.assertSame(DefaultEpsilons.getInstance().getEpsilons(problem), DefaultEpsilons.DEFAULT);
	}
	
	@Test
	public void testOverrideFromProperties() {
		Problem problem = new MockRealProblem();
		
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.createKey("org", "moeaframework", "problem", problem.getName(), "epsilons"), 0.75)) {
			Assert.assertEquals(DefaultEpsilons.getInstance().getEpsilons(problem), Epsilons.of(0.75));
		}
	}
	
}
