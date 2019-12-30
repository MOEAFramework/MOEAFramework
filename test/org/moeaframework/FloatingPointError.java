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
package org.moeaframework;

/**
 * Interface for comparing floating-point numbers while accounting for the 
 * inaccuracies of the floating-point representation.
 */
public interface FloatingPointError {
	
	/**
	 * Asserts that the two floating-point values are equal, throwing an
	 * {@code AssertionError} if they are not considered equal.
	 * 
	 * @param d1 the first floating-point value
	 * @param d2 the second floating-point value
	 */
	public void assertEquals(double d1, double d2);

}
