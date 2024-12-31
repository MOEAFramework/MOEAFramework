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
package org.moeaframework.core;

/**
 * Thrown when two (or more) types are not compatible or invalid in the given context.
 */
public class TypeMismatchException extends IllegalArgumentException {

	private static final long serialVersionUID = -3650352016752120009L;

	/**
	 * Creates an exception indicating the types are not compatible or invalid in the given context.
	 * 
	 * @param message the error message
	 */
	public TypeMismatchException(String message) {
		super(message);
	}
	
	/**
	 * Creates an exception indicating the two types are not comparable.
	 * 
	 * @param type1 the first type
	 * @param type2 the second type
	 * @return the exception
	 */
	public static TypeMismatchException notComparable(Class<?> type1, Class<?> type2) {
		return new TypeMismatchException("Types are not comparable (" + type1.getName() + " and " + type2.getName() +
				")");
	}

}
