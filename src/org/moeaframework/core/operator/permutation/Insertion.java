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
package org.moeaframework.core.operator.permutation;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.operator.TypeSafeMutation;
import org.moeaframework.core.variable.Permutation;

/**
 * Insertion mutation operator. Randomly selects an entry in the permutation and inserts it at some other position
 * in the permutation.
 * <p>
 * This operator is type-safe.
 */
@Prefix("insertion")
public class Insertion extends TypeSafeMutation<Permutation> {

	/**
	 * Constructs an insertion mutation operator with the default settings.
	 */
	public Insertion() {
		this(0.3);
	}

	/**
	 * Constructs an insertion mutation operator with the specified probability of mutating a variable.
	 * 
	 * @param probability the probability of mutating a variable
	 */
	public Insertion(double probability) {
		super(Permutation.class, probability);
	}
	
	@Override
	public String getName() {
		return "insertion";
	}

	/**
	 * Evolves the specified permutation using the insertion mutation operator.
	 * 
	 * @param permutation the permutation to be mutated
	 */
	public void mutate(Permutation permutation) {
		int i = PRNG.nextInt(permutation.size());
		int j = PRNG.nextInt(permutation.size() - 1);

		if (i == j) {
			j = permutation.size() - 1;
		}

		permutation.insert(i, j);
	}

}
