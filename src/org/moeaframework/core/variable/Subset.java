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

import java.util.Arrays;
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
 * Decision variable for subsets.
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
	 * The minimum number of members in the subset.
	 */
	private int l;
	
	/**
	 * The maximum number of members in the subset.
	 */
	private int u;

	/**
	 * The number of candidate members.
	 */
	private int n;

	/**
	 * Set of members in this subset for faster querying.
	 */
	private Set<Integer> members;

	/**
	 * Constructs a new decision variable for representing subsets of size
	 * {@code k} from a set of size {@code n}.
	 * 
	 * @param k the fixed size of the subset
	 * @param n the size of the original set (i.e., the number of candidate
	 *          members)
	 */	
	public Subset(int k, int n) {
		this(k, k, n);
	}

	/**
	 * Constructs a new decision variable for representing subsets whose size
	 * ranges between {@code l} (minimum size) and {@code u} (maximum size)
	 * from a set of size {@code n}
	 * 
	 * @param l the minimum size of the subset
	 * @param u the maximum size of the subset
	 * @param n the size of the original set (i.e., the number of candidate
	 *          members)
	 */
	public Subset(int l, int u, int n) {
		super();
		this.l = l;
		this.u = u;
		this.n = n;
		
		if (u > n) {
			throw new IllegalArgumentException("k must be <= n");
		}
		
		if (l < 0) {
			throw new IllegalArgumentException("l must be >= 0");
		}
		
		if (l > u) {
			throw new IllegalArgumentException("l must be <= u");
		}

		members = new HashSet<Integer>();
		
		for (int i = 0; i < l; i++) {
			members.add(i);
		}
	}
	
	/**
	 * Checks if this subset is valid, throwing an exception if it is not.
	 * 
	 * @throws FrameworkException if this subset is not valid
	 */
	public void validate() {
		if ((members.size() < l) || (members.size() > u)) {
			throw new FrameworkException("subset not valid (invalid size)");
		}
		
		for (int value : members) {
			if ((value < 0) || (value >= n)) {
				throw new FrameworkException("subset not valid (contains invalid member)");
			}
		}
	}

	/**
	 * Returns the minimum number of members in this subset.
	 * 
	 * @return the minimum number of members in this subset
	 */
	public int getL() {
		return l;
	}
	
	/**
	 * Returns the maximum number of members in this subset.
	 * 
	 * @return the maximum number of members in this subset
	 */
	public int getU() {
		return u;
	}

	/**
	 * Returns the size of the original set.
	 * 
	 * @return the size of the original set
	 */
	public int getN() {
		return n;
	}
	
	/**
	 * Returns the current size of this subset.
	 * 
	 * @return the current size of this subset
	 */
	public int size() {
		return members.size();
	}
	
	/**
	 * Replaces a member of this subset with another member not in this subset.
	 * 
	 * @param oldValue the old member
	 * @param newValue the new member
	 */
	public void replace(int oldValue, int newValue) {
		members.remove(oldValue);
		members.add(newValue);
	}
	
	/**
	 * Adds a new member to this subset, increasing the subset size by 1.
	 * 
	 * @param value the new member
	 */
	public void add(int value) {
		members.add(value);
	}
	
	/**
	 * Removes a member from this subset, decreasing the subset size by 1.
	 * 
	 * @param value the member to remove
	 */
	public void remove(int value) {
		members.remove(value);
	}
	
	/**
	 * Returns {@code true} if the subset contains the given member; {@code false} otherwise.
	 * 
	 * @param value the member
	 * @return {@code true} if the subset contains the given member; {@code false} otherwise
	 */
	public boolean contains(int value) {
		return members.contains(value);
	}

	/**
	 * Returns the membership in this subset.
	 * 
	 * @return the membership in this subset.
	 */
	public Set<Integer> getSet() {
		return new HashSet<Integer>(members);
	}

	/**
	 * Returns the membership in this subset as an array.  The ordering is non-deterministic and may change
	 * between calls.
	 * 
	 * @return the membership in this subset as an array
	 */
	public int[] toArray() {
		int[] result = new int[members.size()];
		int index = 0;
		
		for (Integer value : members) {
			result[index++] = value;
		}
		
		return result;
	}

	/**
	 * Populates this subset from an array.  Any duplicate values in the array will be ignored.
	 * 
	 * @param members the array containing the subset members
	 */
	public void fromArray(int[] array) {
		if (array.length < l || array.length > u) {
			throw new IllegalArgumentException("invalid subset length");
		}
		
		members.clear();

		for (int i = 0; i < array.length; i++) {
			if ((array[i] < 0) || (array[i] >= n)) {
				throw new IllegalArgumentException("invalid value in subset");
			}
			
			members.add(array[i]);
		}
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
		Subset copy = new Subset(l, u, n);
		copy.members = getSet();
		return copy;
	}

	@Override
	public void randomize() {
		int s = PRNG.nextInt(l, u);
		
		members.clear();
		
		if (s < n / OPT_FACTOR) {
			Set<Integer> generated = new HashSet<Integer>();

			for (int i = 0; i < s; i++) {
				while (true) {
					int value = PRNG.nextInt(n);

					if (!generated.contains(value)) {
						members.add(value);
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

			for (int i = 0; i < s; i++) {
				members.add(pool.remove(PRNG.nextInt(pool.size())));
			}
		}
	}
	
	/**
	 * Randomly pick a value that is contained in this subset.
	 * 
	 * @return the randomly-selected member
	 */
	public int randomMember() {
		if (members.size() == 0) {
			throw new FrameworkException("no member exists (set is empty)");
		} else {
			return PRNG.nextItem(members);
		}
	}

	/**
	 * Randomly pick a value that is not contained in this subset.
	 * 
	 * @returns the randomly-selected non-member
	 */
	public int randomNonmember() {
		if (members.size() == n) {
			throw new FrameworkException("no non-member exists (set contains all values)");
		} else if (members.size() < n / OPT_FACTOR) {
			while (true) {
				int value = PRNG.nextInt(n);

				if (!members.contains(value)) {
					return value;
				}
			}
		} else {
			int result = -1;
			int count = 0;

			for (int i = 0; i < n; i++) {
				if (!members.contains(i)) {
					count++;

					if (PRNG.nextInt(count) == 0) {
						result = i;
					}
				}
			}

			return result;
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

}
