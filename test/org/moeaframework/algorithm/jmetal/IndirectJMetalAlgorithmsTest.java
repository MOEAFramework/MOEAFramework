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
package org.moeaframework.algorithm.jmetal;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

/**
 * Tests the {@link IndirectJMetalAlgorithms} to ensure the weak class reference is valid.
 */
public class IndirectJMetalAlgorithmsTest {

	@Test
	public void test() {
		IndirectJMetalAlgorithms provider = new IndirectJMetalAlgorithms();
		Assert.assertNotNull(provider.getAlgorithm("NSGAII-JMetal", new TypedProperties(), new MockRealProblem()));
	}

}
