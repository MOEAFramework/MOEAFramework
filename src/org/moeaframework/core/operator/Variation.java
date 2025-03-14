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
package org.moeaframework.core.operator;

import org.moeaframework.core.Named;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;

/**
 * Interface for variation operators.  Variation operators manipulate one or more existing solutions, called
 * <em>parents</em>, to produce one or more new solutions, called <em>children</em> or <em>offspring</em>.
 * <p>
 * Variation operators should include the {@link TypeSafe} annotation when appropriate.
 */
public interface Variation extends Configurable, Named {
	
	/**
	 * Returns the name of this variation operator.  This name should also be used as the prefix for any parameters.
	 * As such, the name should only contain alphanumeric characters, avoid using whitespace and other symbols.
	 * 
	 * @return the name of this variation operator
	 */
	@Override
	public String getName();

	/**
	 * Returns the number of solutions that must be supplied to the {@code evolve} method.
	 * 
	 * @return the number of solutions that must be supplied to the {@code evolve} method
	 */
	public int getArity();

	/**
	 * Evolves one or more parent solutions (specified by {@code getArity}) and produces one or more child solutions.
	 * By contract, the parents must not be modified.  The copy constructor should be used to create copies of the
	 * parents with these copies subsequently modified.
	 * 
	 * @param parents the array of parent solutions
	 * @return an array of child solutions
	 * @throws IllegalArgumentException if an incorrect number of parents was supplied
	 *         {@code (parents.length != getArity())}
	 */
	public Solution[] evolve(Solution[] parents);

}
