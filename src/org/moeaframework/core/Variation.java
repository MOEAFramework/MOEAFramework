/* Copyright 2009-2018 David Hadka
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
 * Interface for variation operators. Variation operators manipulate one or more
 * existing solutions, called <em>parents</em>, to produce one or more new
 * solutions, called <em>children</em> or <em>offspring</em>.
 * <p>
 * A variation operator is <em>type-safe</em> if it checks variable types at
 * runtime and operates only on those variables it supports. Unsupported
 * variables must be left unmodified. A type-safe variation operator must ensure
 * casts are valid and never throw a {@code ClassCastException}. A type-safe
 * variation class should indicate this fact by stating
 * "This variation operator is type-safe" in the class comments.
 * <p>
 * Mixed-type encodings are supported by using type-safe variation operators.
 * Variation operators for each type in the encoding are applied sequentially,
 * each operating on only those variables with the correct type.
 */
public interface Variation {

	/**
	 * Returns the number of solutions that must be supplied to the
	 * {@code evolve} method.
	 * 
	 * @return the number of solutions that must be supplied to the
	 *         {@code evolve} method
	 */
	public int getArity();

	/**
	 * Evolves one or more parent solutions (specified by {@code getArity}) and
	 * produces one or more child solutions. By contract, the parents must not
	 * be modified. The copy constructor should be used to create copies of the
	 * parents with these copies subsequently modified.
	 * 
	 * @param parents the array of parent solutions
	 * @return an array of child solutions
	 * @throws IllegalArgumentException if an incorrect number of parents was
	 *         supplied {@code (parents.length != getArity())}
	 */
	public Solution[] evolve(Solution[] parents);

}
