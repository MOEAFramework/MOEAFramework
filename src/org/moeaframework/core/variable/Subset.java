/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.variable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

/**
 * Decision variable for fixed-size subsets.  Use a {@code BinaryVariable} for
 * variable-size subsets.
 */
public class Subset implements Variable {

	private static final long serialVersionUID = -4491760813656852414L;

	/**
	 * Optimization factor.  As long as {@code k} is marginally smaller than
	 * {@code n}, it is more computationally efficient to randomly generate
	 * candidate members in the subset.  An {@code OPT_FACTOR} of {@code 1.1}
	 * enables random generation as long as {@code k} is at least {@code 10%}
	 * smaller than {@code n}.
	 */
	private static final double OPT_FACTOR = 1.1;

	/**
	 * Enables validation checks to ensure the subset is valid.  Due to the
	 * time complexity of validating subsets, this is disabled by default.
	 */
	private static final boolean VALIDATE = false;

	/**
	 * The number of candidate members.
	 */
	private int n;

	/**
	 * The ordered members in this subset.
	 */
	private int[] members;

	/**
	 * Set of members in this subset for faster querying.
	 */
	private Set<Integer> memberSet;

	/**
	 * Constructs a new decision variable for representing subsets of size
	 * {@code k} from a set of size {@code n}.
	 * 
	 * @param k the fixed size of the subset
	 * @param n the size of the original set (i.e., the number of candidate
	 *          members)
	 */
	public Subset(int k, int n) {
		super();
		this.n = n;
		
		if (k > n) {
			throw new IllegalArgumentException("k must be <= n");
		}

		members = new int[k];
		memberSet = new HashSet<Integer>();
		
		for (int i = 0; i < k; i++) {
			members[i] = i;
			memberSet.add(i);
		}
	}

	/**
	 * Returns the fixed size of this subset.
	 * 
	 * @return the fixed size of this subset
	 */
	public int getK() {
		return members.length;
	}

	/**
	 * The size of the original set.
	 * 
	 * @return the size of the original set
	 */
	public int getN() {
		return n;
	}

	/**
	 * Gets the member of this subset at the given index.
	 * 
	 * @param index the index
	 * @return the member of this subset at the given index
	 */
	public int get(int index) {
		return members[index];
	}

	/**
	 * Checks if this subset is valid, throwing an exception if not.
	 * 
	 * @throws FrameworkException if this subset is not valid
	 */
	public void validate() {
		if (VALIDATE) {
			Set<Integer> values = new HashSet<Integer>();

			for (int i = 0; i < members.length; i++) {
				values.add(members[i]);
			}

			if (values.size() != members.length) {
				throw new FrameworkException("not a valid subset");
			}
		}
	}

	/**
	 * Assigns the member of this subset at the given index.
	 * 
	 * @param index the index
	 * @param value the new member
	 */
	public void set(int index, int value) {
		memberSet.remove(members[index]);
		members[index] = value;
		memberSet.add(value);
		validate();
	}

	/**
	 * Returns the membership in this subset as an unmodifiable set.
	 * 
	 * @return the membership in this subset.
	 */
	public Set<Integer> getSet() {
		return Collections.unmodifiableSet(memberSet);
	}

	/**
	 * Returns the membership in this subset as an array.
	 * 
	 * @return the membership in this subset
	 */
	public int[] toArray() {
		return members.clone();
	}

	/**
	 * Populates this subset from an array.
	 * 
	 * @param members the array containing the subset members
	 */
	public void fromArray(int[] members) {
		if (this.members.length != members.length) {
			throw new IllegalArgumentException("invalid subset length");
		}

		memberSet.clear();

		for (int i = 0; i < members.length; i++) {
			this.members[i] = members[i];
			memberSet.add(members[i]);
		}

		validate();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(n)
				.append(members)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Subset rhs = (Subset)obj;

			return new EqualsBuilder()
					.append(n, rhs.n)
					.append(members, rhs.members)
					.isEquals();
		}
	}

	@Override
	public Subset copy() {
		Subset copy = new Subset(members.length, n);
		copy.fromArray(members);
		return copy;
	}

	@Override
	public void randomize() {
		if (members.length < n / OPT_FACTOR) {
			Set<Integer> generated = new HashSet<Integer>();

			for (int i = 0; i < members.length; i++) {
				while (true) {
					int value = PRNG.nextInt(n);

					if (!generated.contains(value)) {
						members[i] = value;
						generated.add(value);
						break;
					}
				}
			}
		} else {
			List<Integer> pool = new LinkedList<Integer>();

			for (int i = 0; i < n; i++) {
				pool.add(i);
			}

			for (int i = 0; i < members.length; i++) {
				members[i] = pool.remove(PRNG.nextInt(pool.size()));
			}
		}
	}

	/**
	 * Randomly pick a value that is not contained in this subset.
	 */
	public int randomNonmember() {
		if (members.length == n) {
			throw new FrameworkException("no non-member exists (k == n)");
		} else if (members.length < n / OPT_FACTOR) {
			while (true) {
				int value = PRNG.nextInt(n);

				if (!memberSet.contains(value)) {
					return value;
				}
			}
		} else {
			int result = -1;
			int count = 0;

			for (int i = 0; i < n; i++) {
				if (!memberSet.contains(i)) {
					count++;

					if (PRNG.nextInt(count) == 0) {
						result = i;
					}
				}
			}

			return result;
		}
	}

}
