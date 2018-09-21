/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.core.operator;

import org.junit.Test;

public class OnePointCrossoverTest extends PointCrossoverTest {

	/**
	 * Tests if the grammar crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new OnePointCrossover(1.0));
	}

	@Test
	public void test() {
		test(new OnePointCrossover(1.0), 10);
	}
	
	@Test
	public void testOneVariable() {
		test(new OnePointCrossover(1.0), 1);
	}
	
	@Test
	public void testNoVariables() {
		test(new OnePointCrossover(1.0), 0);
	}

	@Test
	public void testParentImmutability() {
		testParentImmutability(new OnePointCrossover(1.0));
	}

}
