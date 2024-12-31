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
package org.moeaframework.core.variable;

import org.junit.Assert;
import org.junit.Test;

public class VariableTest {
	
	@Test
	public void testGetNameOrDefault() {
		Assert.assertEquals("Var1", Variable.getNameOrDefault(new RealVariable(0.0, 1.0), 0));
		Assert.assertEquals("Var2", Variable.getNameOrDefault(new BinaryIntegerVariable(0, 1), 1));
		
		Assert.assertEquals("foo", Variable.getNameOrDefault(new RealVariable("foo", 0.0, 1.0), 0));
	}

}
