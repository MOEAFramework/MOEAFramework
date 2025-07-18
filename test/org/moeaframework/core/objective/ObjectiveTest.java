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
package org.moeaframework.core.objective;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.TypeMismatchException;

public class ObjectiveTest {
	
	@Test(expected = TypeMismatchException.class)
	public void testCompareToWrongType() {
		Minimize.value().compareTo(Maximize.value());
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsWrongType() {
		Assert.assertFalse(Minimize.value().equals(Maximize.value()));
	}
	
	@Test
	public void testGetNameOrDefault() {
		Assert.assertEquals("Obj1", Objective.getNameOrDefault(Minimize.value(), 0));
		Assert.assertEquals("Obj2", Objective.getNameOrDefault(Maximize.value(), 1));
		
		Assert.assertEquals("foo", Objective.getNameOrDefault(Minimize.value("foo"), 0));
	}

}
