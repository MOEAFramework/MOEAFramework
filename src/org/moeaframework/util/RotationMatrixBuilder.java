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
package org.moeaframework.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.moeaframework.core.PRNG;

/**
 * Builds rotation matrices of any dimension constructively from one or more
 * 2D planar rotations.  Three types of construction mechanisms are provided:
 * 
 * <ol>
 *   <li>Specific rotation planes and thetas
 *     <pre>  new RotationMatrixBuilder(3).rotatePlane(0,1).withTheta(Math.PI/4)
 *       .rotatePlane(1,2).withTheta(Math.PI/8).create()</pre>
 *   <li>Specific rotation planes with random thetas
 *     <pre>  new RotationMatrixBuilder(3).rotatePlane(0,1).rotatePlane(1,2)
 *       .withRandomThetas().create()</pre>
 *   <li>Random subplanes with random thetas
 *     <pre>  new RotationMatrixBuilder(3).rotateK(2).create()</pre>
 * </ol>
 * 
 * <p>
 * References:
 * <ol>
 *   <li>Aguilera, A. and P&eacute;rez-Aguila, R.  "General n-Dimensional 
 *       Rotations."  WSCG 2004, pp. 1-8, 2004.
 * </ol>
 */
public class RotationMatrixBuilder {
	
	/**
	 * The 2D rotation planes.
	 */
	private static class Plane {
		
		/**
		 * The first axis defining the rotation plane.
		 */
		private final int firstAxis;
		
		/**
		 * The second axis defining the rotation plane.
		 */
		private final int secondAxis;
		
		/**
		 * The rotation angle in radians; or {@code NaN} if no rotation is 
		 * assigned.
		 */
		private double theta;
		
		/**
		 * Constructs a 2D rotation plane around the specified axes.
		 * 
		 * @param firstAxis the first axis
		 * @param secondAxis the second axis
		 */
		public Plane(int firstAxis, int secondAxis) {
			super();
			this.firstAxis = firstAxis;
			this.secondAxis = secondAxis;
			
			theta = Double.NaN;
		}

		/**
		 * Returns the first axis defining the rotation plane.
		 *
		 * @return the first axis defining the rotation plane
		 */
		public int getFirstAxis() {
			return firstAxis;
		}

		/**
		 * Returns the second axis defining the rotation plane.
		 *
		 * @return the second axis defining the rotation plane
		 */
		public int getSecondAxis() {
			return secondAxis;
		}

		/**
		 * Returns the rotation angle in radians; or {@code NaN} if no rotation
		 * is assigned.
		 * 
		 * @return the rotation angle in radians; or {@code NaN} if no rotation
		 *         is assigned
		 */
		public double getTheta() {
			return theta;
		}

		/**
		 * Sets the rotation angle in radians.
		 * 
		 * @param theta the rotation angle in radians
		 */
		public void setTheta(double theta) {
			this.theta = theta;
		}
		
	}
	
	/**
	 * The dimension of rotation matrices produced by this builder.
	 */
	private final int dimension;
	
	/**
	 * The 2D rotation planes currently used by this builder.
	 */
	private final List<Plane> planes;
	
	/**
	 * Constructs a rotation matrix builder for the given dimension.
	 * 
	 * @param dimension the dimension of rotation matrices produced by
	 *        this builder
	 */
	public RotationMatrixBuilder(int dimension) {
		super();
		this.dimension = dimension;
		
		planes = new ArrayList<Plane>();
	}
	
	/**
	 * Adds a rotation around the 2D rotation plane defined by the two axes.
	 * The plane is initially unrotated, but can be assigned a specific rotation
	 * angle if followed by {@link #withTheta(double)}.
	 * 
	 * @param i the first axis
	 * @param j the second axis
	 * @return a reference to this rotation matrix builder
	 */
	public RotationMatrixBuilder rotatePlane(int i, int j) {
		if ((i < 0) || (i >= dimension) || (j < 0) || 
				(j >= dimension) || (i == j)) {
			throw new IllegalArgumentException("invalid plane");
		}
		
		planes.add(new Plane(i, j));
		
		return this;
	}
	
	/**
	 * Sets the rotation angle, in radians, of the last 2D rotation plane
	 * added to this builder.  This method should always only follow 
	 * invocations of {@link #rotatePlane(int, int)}.
	 * 
	 * @param theta the rotation angle in radians
	 * @return a reference to this rotation matrix builder
	 */
	public RotationMatrixBuilder withTheta(double theta) {
		planes.get(planes.size()-1).setTheta(theta);
		
		return this;
	}
	
	/**
	 * Sets the rotation angle, in radians, of all 2D rotation planes added to
	 * this builder.
	 * 
	 * @param theta the rotation angle in radians
	 * @return a reference to this rotation matrix builder
	 */
	public RotationMatrixBuilder withThetas(double theta) {
		for (Plane plane : planes) {
			plane.setTheta(theta);
		}
		
		return this;
	}
	
	/**
	 * Assigns random rotation angles to all 2D rotation planes added to this 
	 * builder.
	 * 
	 * @return a reference to this rotation matrix builder
	 */
	public RotationMatrixBuilder withRandomThetas() {
		for (Plane plane : planes) {
			plane.setTheta(PRNG.nextDouble(0.0, 2.0*Math.PI));
		}
		
		return this;
	}
	
	/**
	 * Rotates all 2D rotation planes and assigns each a random rotation angle.
	 * 
	 * @return a reference to this rotation matrix builder
	 */
	public RotationMatrixBuilder rotateAll() {
		for (int i = 0; i < dimension - 1; i++) {
			for (int j = i+1; j < dimension; j++) {
				Plane plane = new Plane(i, j);
				plane.setTheta(PRNG.nextDouble(0.0, 2.0*Math.PI));
				planes.add(plane);
			}
		}
		
		return this;
	}
	
	/**
	 * Rotates {@code k} randomly-selected but unique 2D rotation planes and 
	 * assigns each a random rotation angle.  For an {@code N}-dimension 
	 * rotation matrix, there exist {@code (N choose 2)} 2D rotation planes.
	 * 
	 * @param k the number of randomly-selected 2D rotation planes
	 * @return a reference to this rotation matrix builder
	 */
	public RotationMatrixBuilder rotateK(int k) {
		List<Plane> tempPlanes = new ArrayList<Plane>();
		
		//generate the list of all available rotation planes
		for (int i = 0; i < dimension - 1; i++) {
			for (int j = i+1; j < dimension; j++) {
				tempPlanes.add(new Plane(i, j));
			}
		}
		
		if ((k < 0) || (k > tempPlanes.size())) {
			throw new IllegalArgumentException("invalid number of planes");
		}
		
		//shuffle to randomize which planes are rotated
		PRNG.shuffle(tempPlanes);
		
		//clear any existing planes
		planes.clear();
		
		//assign the first k planes
		for (int i = 0; i < k; i++) {
			Plane plane = tempPlanes.get(i);
			plane.setTheta(PRNG.nextDouble(0.0, 2.0*Math.PI));
			planes.add(plane);
		}
		
		return this;
	}
	
	/**
	 * Returns the rotation matrix resulting from applying all 2D rotation 
	 * planes and angles added to this builder.
	 * 
	 * @return the rotation matrix resulting from applying all 2D rotation 
	 *         planes and angles added to this builder
	 */
	public RealMatrix create() {
		RealMatrix rotation = newIdentityMatrix();
		
		for (Plane plane : planes) {
			double theta = plane.getTheta();
			
			if (Double.isNaN(theta)) {
				continue;
			}

			rotation = rotation.multiply(newRotationMatrix(
					plane.getFirstAxis(), plane.getSecondAxis(), theta));
		}
		
		return rotation;
	}
	
	/**
	 * Returns an identity matrix.
	 * 
	 * @return an identity matrix
	 */
	private RealMatrix newIdentityMatrix() {
		RealMatrix identity = MatrixUtils.createRealMatrix(dimension, 
				dimension); 
		
		for (int i = 0; i < dimension; i++) {
			identity.setEntry(i, i, 1.0);
		}
		
		return identity;
	}
	
	/**
	 * Returns a rotation matrix for rotating about the 2D plane defined by
	 * the specified axes.
	 * 
	 * @param i the first axis
	 * @param j the second axis
	 * @param theta the rotation angle in radians
	 * @return a rotation matrix for rotating about the 2D plane defined by
	 *         the specified axes
	 */
	private RealMatrix newRotationMatrix(int i, int j, double theta) {
		RealMatrix rotation = newIdentityMatrix();
		
		rotation.setEntry(i, i, Math.cos(theta));
		rotation.setEntry(i, j, -Math.sin(theta));
		rotation.setEntry(j, i, Math.sin(theta));
		rotation.setEntry(j, j, Math.cos(theta));
		
		return rotation;
	}

}
