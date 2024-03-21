/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.util.format;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.RealVariable;

public class VariableFormatterTest {
	
	@Test
	public void testDefaultSettings() {
		TabularData<Variable> data = new TabularData<>(new ArrayList<Variable>());
		VariableFormatter formatter = new VariableFormatter(data);
		
		Assert.assertEquals("0.500000", formatter.format(new RealVariable(0.5, 0.0, 1.0)));
		Assert.assertEquals("5", formatter.format(new BinaryIntegerVariable(5, 0, 10)));
		Assert.assertEquals("0000000000", formatter.format(new BinaryVariable(10)));	
	}
	
	@Test
	public void testArray() {
		TabularData<Variable> data = new TabularData<>(new ArrayList<Variable>());
		VariableFormatter formatter = new VariableFormatter(data);
		
		Assert.assertEquals("[0.500000, 5, 0000000000]", formatter.format(List.of(
				new RealVariable(0.5, 0.0, 1.0), new BinaryIntegerVariable(5, 0, 10), new BinaryVariable(10))));	
	}

}
