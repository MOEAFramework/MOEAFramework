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

import org.moeaframework.core.constraint.Constraint;

/**
 * Formatter for {@link Constraint}s.  Primarily, this uses the registered formatter, if any, for numeric types.
 */
public class ConstraintFormatter implements Formatter<Constraint> {
	
	private TabularData<?> data;

	/**
	 * Constructs a new constraint formatter.
	 * 
	 * @param data reference back to the tabular data object this formatter is associated, so it can call the
	 *        {@link TabularData#formatValue(Object)} method on primitive types
	 */
	public ConstraintFormatter(TabularData<?> data) {
		super();
		this.data = data;
	}
	
	@Override
	public Class<Constraint> getType() {
		return Constraint.class;
	}

	@Override
	public String format(Object value) {
		if (value instanceof Constraint constraint) {
			return data.formatValue(constraint.getValue());
		} else {
			return value.toString();
		}
	}

}
