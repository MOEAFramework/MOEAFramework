/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.core.spi;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

public class IndirectAlgorithmProviderTest {
	
	@Test
	public void test() {
		Assert.assertNotNull(new IndirectAlgorithmProvider("org.moeaframework.core.spi.TestAlgorithmProvider")
				.getAlgorithm("testAlgorithm", new TypedProperties(), new MockRealProblem()));
	}
	
	@Test
	public void testMissingClassName() {
		Assert.assertNull(new IndirectAlgorithmProvider("foo.bar").getAlgorithm(
				"foo", new TypedProperties(), new MockRealProblem()));
	}

}
