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
package org.moeaframework.core.constraint;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.TypeMismatchException;

public class ConstraintTest {
	
	@Test(expected = TypeMismatchException.class)
	public void testCompareTo() {
		Equal.to(5.0).compareTo(NotEqual.to(5.0));
	}
	
	@Test
	public void testIsViolation() {
		Equal equal = Equal.to(5.0);
		
		equal.setValue(5.0);
		Assert.assertFalse(equal.isViolation());
		
		equal.setValue(4.9);
		Assert.assertTrue(equal.isViolation());
		
		equal.setValue(5.1);
		Assert.assertTrue(equal.isViolation());
	}
	
	@Test
	public void testGetNameOrDefault() {
		Assert.assertEquals("Constr1", Constraint.getNameOrDefault(Equal.to(5.0), 0));
		Assert.assertEquals("Constr2", Constraint.getNameOrDefault(Equal.to(5.0), 1));
		
		Assert.assertEquals("foo", Constraint.getNameOrDefault(Equal.to("foo", 5.0), 0));
	}

}
