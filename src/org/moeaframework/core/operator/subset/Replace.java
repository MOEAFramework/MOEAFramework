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
package org.moeaframework.core.operator.subset;

import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.operator.TypeSafe;
import org.moeaframework.core.operator.TypeSafeMutation;
import org.moeaframework.core.variable.Subset;

/**
 * Replacement mutation operator.  Randomly replaces one of the members in the subset with a non-member.
 * <p>
 * This operator is type-safe.
 */
@TypeSafe
@Prefix("replace")
public class Replace extends TypeSafeMutation<Subset> {
	
	/**
	 * Constructs a replacement mutation operator with default settings.
	 */
	public Replace() {
		this(0.3);
	}

	/**
	 * Constructs a replacement mutation operator with the specified probability of mutating a variable.
	 * 
	 * @param probability the probability of mutating a variable
	 */
	public Replace(double probability) {
		super(Subset.class, probability);
	}
	
	@Override
	public String getName() {
		return "replace";
	}

	/**
	 * Mutates the specified subset using the replacement mutation operator.
	 * 
	 * @param subset the subset to be mutated
	 */
	@Override
	public void mutate(Subset subset) {
		if ((subset.size() < subset.getN()) && (subset.size() > 0)) {
			subset.replace(subset.randomMember(), subset.randomNonmember());
		}
	}

}
