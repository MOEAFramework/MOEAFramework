/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.util.weights;

import org.junit.Test;

/**
 * Tests the {@link NormalBoundaryIntersectionGenerator} class.
 */
public class NormalBoundaryIntersectionGeneratorTest extends
WeightGeneratorTest {

	@Test
	public void testSingleLayer() {
		test(new NormalBoundaryIntersectionGenerator(2, 30), 2);
		test(new NormalBoundaryIntersectionGenerator(3, 12), 3);
		test(new NormalBoundaryIntersectionGenerator(5, 4), 5);
	}
	
	@Test
	public void testTwoLayer() {
		test(new NormalBoundaryIntersectionGenerator(8, 4, 3), 8);
		test(new NormalBoundaryIntersectionGenerator(10, 3, 2), 10);
		test(new NormalBoundaryIntersectionGenerator(15, 3, 2), 15);
	}
	
}
