/* Copyright 2009-2019 David Hadka
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

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.AbsoluteError;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link RotationMatrixBuilder} class.
 */
public class RotationMatrixBuilderTest {
	
	/**
	 * The maximum number of dimensions to test.
	 */
	private static final int N = 10;
	
	/**
	 * Asserts that the matrix is a rotation matrix; that is, the matrix is
	 * orthogonal and has a determinant of {@code 1}.
	 * 
	 * @param rm the matrix to test
	 */
	public void testRotationMatrix(RealMatrix rm) {
		LUDecomposition lu = new LUDecomposition(rm);
		
		Assert.assertEquals(1.0, lu.getDeterminant(), Settings.EPS);
		TestUtils.assertEquals(rm.transpose(), lu.getSolver().getInverse(), 
				new AbsoluteError(0.05));
	}
	
	/**
	 * Tests if the matrix is the identity matrix.
	 * 
	 * @param rm the matrix to test
	 */
	public void testIdentityMatrix(RealMatrix rm) {
		for (int i=0; i<rm.getRowDimension(); i++) {
			for (int j=0; j<rm.getColumnDimension(); j++) {
				if (i == j) {
					Assert.assertEquals(1.0, rm.getEntry(i, j), Settings.EPS);
				} else {
					Assert.assertEquals(0.0, rm.getEntry(i, j), Settings.EPS);
				}
			}
		}
	}
	
	/**
	 * Tests if sequential calls to {@code rotatePlane} and {@code withTheta}
	 * are applied sequentially and eventually result in a full rotation.
	 */
	@Test
	public void testRotatePlane() {
		for (int n=2; n<N; n++) {
			RotationMatrixBuilder builder = new RotationMatrixBuilder(n);
			
			testIdentityMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			testRotationMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			testRotationMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			testRotationMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			testIdentityMatrix(builder.create());
		}
	}
	
	/**
	 * Tests if the {@code rotateK} method produces valid rotation matrices.
	 */
	@Test
	public void testRotateK() {
		for (int n=2; n<N; n++) {
			for (int k=0; k<=ArithmeticUtils.binomialCoefficient(n, 2); k++) {
				RotationMatrixBuilder builder = new RotationMatrixBuilder(n);
				builder.rotateK(k);
				
				for (int i=0; i<100; i++) {
					testRotationMatrix(builder.withRandomThetas().create());
				}
			}
		}
	}
	
	/**
	 * Tests if the {@code rotateK} method produces valid rotation matrices.
	 */
	@Test
	public void testRotateAll() {
		for (int n=2; n<N; n++) {
			for (int k=0; k<=ArithmeticUtils.binomialCoefficient(n, 2); k++) {
				RotationMatrixBuilder builder = new RotationMatrixBuilder(n);
				builder.rotateAll();
				
				for (int i=0; i<100; i++) {
					testRotationMatrix(builder.withThetas(Math.PI/4).create());
				}
			}
		}
	}
	
	/**
	 * Tests if an exception is thrown if the same axes are given for the plane
	 * of rotation.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRotatePlaneException() {
		new RotationMatrixBuilder(3).rotatePlane(1, 1);
	}
	
	/**
	 * Tests if an exception is thrown if the number of rotation planes exceeds
	 * the valid number of rotation planes.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRotateKException() {
		new RotationMatrixBuilder(3).rotateK(4);
	}

}
