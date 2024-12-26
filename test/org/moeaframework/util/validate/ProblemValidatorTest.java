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
package org.moeaframework.util.validate;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockConstraintProblem;
import org.moeaframework.mock.MockMixedBinaryProblem;
import org.moeaframework.mock.MockMultiTypeProblem;
import org.moeaframework.mock.MockRealProblem;

public class ProblemValidatorTest {
	
	@Test
	public void testValidatorType() {
		Assert.assertInstanceOf(ProblemValidator.class, Validate.that("problem", new MockRealProblem()));
	}

	@Test
	public void testUnconstrained() {
		Validate.that("problem", new MockRealProblem()).isUnconstrained();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("problem", new MockConstraintProblem()).isUnconstrained());
	}
	
	@Test
	public void testProblemType() {
		Validate.that("problem", new MockRealProblem()).isType(RealVariable.class);
		Validate.that("problem", new MockMixedBinaryProblem()).isType(BinaryVariable.class);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("problem", new MockRealProblem()).isType(BinaryVariable.class));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("problem", new MockMultiTypeProblem()).isType(BinaryVariable.class));
	}
	
}
