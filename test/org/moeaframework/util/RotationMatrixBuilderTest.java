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

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;

public class RotationMatrixBuilderTest {
	
	/**
	 * The maximum number of dimensions to test.
	 */
	private static final int N = 10;
	
	public void assertRotationMatrix(RealMatrix rm) {
		LUDecomposition lu = new LUDecomposition(rm);
		
		Assert.assertEquals(1.0, lu.getDeterminant(), TestEnvironment.HIGH_PRECISION);
		Assert.assertEquals(rm.transpose(), lu.getSolver().getInverse(), TestEnvironment.LOW_PRECISION);
	}
	
	public void assertIdentityMatrix(RealMatrix rm) {
		for (int i=0; i<rm.getRowDimension(); i++) {
			for (int j=0; j<rm.getColumnDimension(); j++) {
				if (i == j) {
					Assert.assertEquals(1.0, rm.getEntry(i, j), TestEnvironment.HIGH_PRECISION);
				} else {
					Assert.assertEquals(0.0, rm.getEntry(i, j), TestEnvironment.HIGH_PRECISION);
				}
			}
		}
	}
	
	/**
	 * Tests if sequential calls to {@code rotatePlane} and {@code withTheta} are applied sequentially and eventually
	 * result in a full rotation.
	 */
	@Test
	public void testRotatePlane() {
		for (int n=2; n<N; n++) {
			RotationMatrixBuilder builder = new RotationMatrixBuilder(n);
			
			assertIdentityMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			assertRotationMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			assertRotationMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			assertRotationMatrix(builder.create());
			builder.rotatePlane(0, n-1).withTheta(Math.PI/2);
			assertIdentityMatrix(builder.create());
		}
	}
	
	@Test
	public void testRotateK() {
		for (int n=2; n<N; n++) {
			for (int k=0; k<=CombinatoricsUtils.binomialCoefficient(n, 2); k++) {
				RotationMatrixBuilder builder = new RotationMatrixBuilder(n);
				builder.rotateK(k);
				
				for (int i=0; i<100; i++) {
					assertRotationMatrix(builder.withRandomThetas().create());
				}
			}
		}
	}
	
	@Test
	public void testRotateAll() {
		for (int n=2; n<N; n++) {
			for (int k=0; k<=CombinatoricsUtils.binomialCoefficient(n, 2); k++) {
				RotationMatrixBuilder builder = new RotationMatrixBuilder(n);
				builder.rotateAll();
				
				for (int i=0; i<100; i++) {
					assertRotationMatrix(builder.withThetas(Math.PI/4).create());
				}
			}
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRotatePlaneWithSameAxesException() {
		new RotationMatrixBuilder(3).rotatePlane(1, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRotateKExceedsNumberOfRotationPlanesException() {
		new RotationMatrixBuilder(3).rotateK(4);
	}

}
