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

import org.junit.Assert;

/**
 * Compares floating-point numbers allowing a small absolute error when
 * determining equality.
 */
public class AbsoluteError implements FloatingPointError {
	
	/**
	 * The maximum absolute error permitted when considering if two floating-
	 * point numbers are equal.
	 */
	private final double epsilon;
	
	/**
	 * Creates a comparator of floating point values allowing a small absolute
	 * error when determining equality.
	 * 
	 * @param epsilon the maximum absolute error permitted when considering if
	 *        two floating-point numbers are equal
	 */
	public AbsoluteError(double epsilon) {
		super();
		this.epsilon = epsilon;
	}

	@Override
	public void assertEquals(double d1, double d2) {
		Assert.assertEquals(d1, d2, epsilon);
	}

}
