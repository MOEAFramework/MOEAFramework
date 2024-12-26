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
package org.moeaframework.util.format;

import org.moeaframework.core.objective.Objective;

/**
 * Formatter for {@link Objective}s.  Primarily, this uses the registered formatter, if any, for numeric types.
 */
public class ObjectiveFormatter implements Formatter<Objective> {
	
	private TabularData<?> data;

	/**
	 * Constructs a new objective formatter.
	 * 
	 * @param data reference back to the tabular data object this formatter is associated, so it can call the
	 *        {@link TabularData#formatValue(Object)} method on primitive types
	 */
	public ObjectiveFormatter(TabularData<?> data) {
		super();
		this.data = data;
	}
	
	@Override
	public Class<Objective> getType() {
		return Objective.class;
	}

	@Override
	public String format(Object value) {
		if (value instanceof Objective objective) {
			return data.formatValue(objective.getValue());
		} else {
			return value.toString();
		}
	}

}
