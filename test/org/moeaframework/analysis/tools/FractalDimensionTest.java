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
package org.moeaframework.analysis.tools;

import java.util.function.IntFunction;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.core.PRNG;

@RunWith(CIRunner.class)
public class FractalDimensionTest extends AbstractToolTest {
	
	private static final int N = 1000;
	
	private static final double EPS = 0.1;
	
	@Test
	public void testPoint() throws Exception {
		runTest((i) -> new double[] { 0.0, 0.0 }, 0.0);
	}
	
	@Test
	public void testHorizontalLine() throws Exception {
		runTest((i) -> new double[] { (double)i / N, 0.0 }, 1.0);
	}
	
	@Test
	public void testVerticalLine() throws Exception {
		runTest((i) -> new double[] { 0.0, (double)i / N }, 1.0);
	}
	
	@Test
	public void testDiagonalLine() throws Exception {
		runTest((i) -> new double[] { (double)i / N, (double)i / N }, 1.0);
	}
	
	@Test
	@Retryable
	public void testFull() throws Exception {
		runTest((i) -> new double[] { PRNG.nextDouble(), PRNG.nextDouble() }, 2.0);
	}
	
	private void runTest(IntFunction<double[]> generator, double expected) {
		double[][] data = IntStream.range(0, N).mapToObj(generator).toArray(double[][]::new);
		Assert.assertEquals(expected, FractalDimension.computeDimension(data), EPS);
	}

}
