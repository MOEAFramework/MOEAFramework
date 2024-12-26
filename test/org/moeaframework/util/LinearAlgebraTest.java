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
package org.moeaframework.util;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class LinearAlgebraTest {

	@Test
	public void testLSolve() {
		double[][] A = new double[][] {
			{ 2, -1, 3 },
			{ 1, -3, -2 },
			{ 3, 2, -1 }};

		double[] b = new double[] { 9, 0, -1 };
		double[] x = LinearAlgebra.lsolve(A, b);

		Assert.assertArrayEquals(new double[] { 1.0, -1.0, 2.0 }, x, TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = SingularMatrixException.class)
	public void testLSolveSingular() {
		double[][] A = new double[][] {
			{ 2, -1, 3 },
			{ 1, -3, -2 },
			{ 2, -1, 3 }};

		double[] b = new double[] { 9, 0, -1 };
		LinearAlgebra.lsolve(A, b);
	}
	
	@Test(expected = DimensionMismatchException.class)
	public void testLSolveNotSquare() {
		double[][] A = new double[][] {
			{ 2, -1 },
			{ 1, -3 },
			{ 3, 2 }};

		double[] b = new double[] { 9, 0, -1 };
		LinearAlgebra.lsolve(A, b);
	}

}
