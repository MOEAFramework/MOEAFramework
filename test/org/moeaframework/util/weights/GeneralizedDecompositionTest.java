/* Copyright 2009-2015 David Hadka
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

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the {@link GeneralizedDecomposition} class.
 */
public class GeneralizedDecompositionTest extends WeightGeneratorTest {
	
	@Test
	@Ignore("plot this output in R and compare against paper")
	public void testPaper() {
		for (double[] weight : new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(3, 30, 0).generate()).generate()) {
			System.out.println(weight[0] + ", " + weight[1] + ", " + weight[2]);
		}
	}
	
	@Test
	public void test() {
		test(new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(2, 30)), 2);
		test(new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(3, 12)), 3);
		test(new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(5, 4)), 5);
	}

}
