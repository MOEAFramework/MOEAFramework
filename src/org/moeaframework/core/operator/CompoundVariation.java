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
package org.moeaframework.core.operator;

import org.moeaframework.core.Variation;

/**
 * Construct a variation operator applying one or more variations sequentially.  See {@link AbstractCompoundVariation}
 * for usage.
 */
public class CompoundVariation extends AbstractCompoundVariation<Variation> implements Variation {

	/**
	 * Constructs a compound variation operator with the specified variation operators.
	 * 
	 * @param operators the variation operators in the order they are applied
	 */
	public CompoundVariation(Variation... operators) {
		super();

		for (Variation operator : operators) {
			appendOperator(operator);
		}
	}

}
