/* Copyright 2009-2022 David Hadka
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

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Identifies mutation operators that evolve exactly one parent.
 */
public interface Mutation extends Variation {
	
	/**
	 * Mutates the given parent to produce an offspring.
	 * 
	 * @param parent the parent solution
	 * @return the offspring
	 */
	public Solution mutate(Solution parent);
	
	@Override
	public default int getArity() {
		return 1;
	}

	@Override
	public default Solution[] evolve(Solution[] parents) {
		return new Solution[] { mutate(parents[0]) };
	}

}
