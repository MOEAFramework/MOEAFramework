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

import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.RealVariable;

/**
 * Formatter for {@link Variable}s.  Primarily, this uses the registered formatter, if any, for numeric types.
 */
public class VariableFormatter implements Formatter<Variable> {
	
	private TabularData<?> data;

	/**
	 * Constructs a new variable formatter.
	 * 
	 * @param data reference back to the tabular data object this formatter is associated, so it can call the
	 *        {@link TabularData#formatValue(Object)} method on primitive types
	 */
	public VariableFormatter(TabularData<?> data) {
		super();
		this.data = data;
	}
	
	@Override
	public Class<Variable> getType() {
		return Variable.class;
	}

	@Override
	public String format(Object variable) {
		if (variable instanceof RealVariable realVariable) {
			return data.formatValue(realVariable.getValue());
		} else if (variable instanceof BinaryIntegerVariable binaryIntegerVariable) {
			return data.formatValue(binaryIntegerVariable.getValue());
		} else {
			return variable.toString();
		}
	}

}
