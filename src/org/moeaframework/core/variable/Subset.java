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
package org.moeaframework.core.variable;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Defined;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.validate.Validate;

/**
 * Decision variable for subsets.  Subsets can either be fixed-length, which must contain exactly {@code k} elements,
 * or variable-length, which can contain between {@code l} and {@code u} elements.  Furthermore, the values stored in
 * a subset can range from {@code 0} to {@code n}, where {@code n >= k, u}.
 */
public class Subset extends AbstractVariable {

	private static final long serialVersionUID = -4491760813656852414L;

	/**
	 * Optimization factor.  As long as {@code k} is marginally smaller than {@code n}, it is more computationally
	 * efficient to randomly generate candidate members in the subset.  An {@code OPT_FACTOR} of {@code 1.1} enables
	 * random generation as long as {@code k} is at least {@code 10%} smaller than {@code n}.
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
	 * Constructs a new decision variable for representing subsets of size {@code k} from a set of size {@code n}.
	 * 
	 * @param k the fixed size of the subset
	 * @param n the size of the original set (i.e., the number of candidate members)
	 */
	public Subset(int k, int n) {
		this(null, k, n);
	}

	/**
	 * Constructs a new decision variable for representing subsets of size {@code k} from a set of size {@code n} with
	 * the given name.
	 * 
	 * @param name the name of this decision variable
	 * @param k the fixed size of the subset
	 * @param n the size of the original set (i.e., the number of candidate members)
	 */
	public Subset(String name, int k, int n) {
		this(name, k, k, n);
	}
	
	/**
	 * Constructs a new decision variable for representing subsets whose size ranges between {@code l} (minimum size)
	 * and {@code u} (maximum size) from a set of size {@code n}.
	 * 
	 * @param l the minimum size of the subset
	 * @param u the maximum size of the subset
	 * @param n the size of the original set (i.e., the number of candidate members)
	 */
	public Subset(int l, int u, int n) {
		this(null, l, u, n);
	}

	/**
	 * Constructs a new decision variable for representing subsets whose size ranges between {@code l} (minimum size)
	 * and {@code u} (maximum size) from a set of size {@code n} with the given name.
	 * 
	 * @param name the name of this decision variable
	 * @param l the minimum size of the subset
	 * @param u the maximum size of the subset
	 * @param n the size of the original set (i.e., the number of candidate members)
	 */
	public Subset(String name, int l, int u, int n) {
		super(name);
		this.l = l;
		this.u = u;
		this.n = n;
		
		Validate.that("u", u).isLessThanOrEqualTo("n", n);
		Validate.that("l", l).isGreaterThanOrEqualTo(0);
		Validate.that("l", l).isLessThanOrEqualTo("u", u);

		members = new HashSet<>();
		
		for (int i = 0; i < l; i++) {
			members.add(i);
		}
	}
	
	/**
	 * Returns {@code true} if this subset satisfies the size requirements and all members are valid.
	 * 
	 * @return {@code true} if this subset is valid; {@code false} otherwise
	 */
	public boolean isValid() {
		if ((members.size() < l) || (members.size() > u)) {
			return false;
		}
		
		for (int value : members) {
			if ((value < 0) || (value >= n)) {
				return false;
			}
		}
		
		return true;
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
	 * @throws IllegalArgumentException if either value is not valid
	 */
	public void replace(int oldValue, int newValue) {
		remove(oldValue);
		add(newValue);
	}
	
	/**
	 * Adds a new member to this subset, increasing the subset size by 1.
	 * <p>
	 * This operation can result in a subset that violates the subset size requirements.  Use {@link #isValid()} after
	 * making modifications to verify the resulting subset is valid.
	 * 
	 * @param value the new member
	 * @throws IllegalArgumentException if the value is not a valid member
	 */
	public void add(int value) {
		Validate.that("value", value).isBetween(0, n-1);
		members.add(value);
	}
	
	/**
	 * Removes a member from this subset, decreasing the subset size by 1.  This has no effect if the value is not
	 * already a member of the subset.  
	 * <p>
	 * This operation can result in a subset that violates the subset size requirements.  Use {@link #isValid()} after
	 * making modifications to verify the resulting subset is valid.
	 * 
	 * @param value the member to remove
	 * @throws IllegalArgumentException if the value is not a valid member
	 */
	public void remove(int value) {
		Validate.that("value", value).isBetween(0, n-1);
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
		return new HashSet<>(members);
	}

	/**
	 * Returns the membership in this subset as an array.  The ordering is non-deterministic and may change between
	 * calls.
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
	 * @param array the array containing the subset members
	 * @throws IllegalArgumentException if the array is not a valid subset
	 */
	public void fromArray(int[] array) {
		Validate.that("array.length", array.length).isBetween(l, u);
		members.clear();

		for (int i = 0; i < array.length; i++) {
			Validate.that("array[" + i + "]", array[i]).isBetween(0, n-1);
			members.add(array[i]);
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
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
					.appendSuper(super.equals(obj))
					.append(n, rhs.n)
					.append(members, rhs.members)
					.isEquals();
		}
	}

	@Override
	public Subset copy() {
		Subset copy = new Subset(name, l, u, n);
		copy.fromArray(toArray());
		return copy;
	}

	@Override
	public void randomize() {
		int s = PRNG.nextInt(l, u);
		
		members.clear();
		
		if (s < n / OPT_FACTOR) {
			Set<Integer> generated = new HashSet<>();

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
			List<Integer> pool = new LinkedList<>();

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
			throw new FrameworkException("Subset is empty");
		} else {
			return PRNG.nextItem(members);
		}
	}

	/**
	 * Randomly pick a value that is not contained in this subset.
	 * 
	 * @return the randomly-selected non-member
	 */
	public int randomNonmember() {
		if (members.size() == n) {
			throw new FrameworkException("Subset contains all possible values");
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
	public String getDefinition() {
		if (name == null) {
			return Defined.createDefinition(Variable.class, Subset.class, l, u, n);
		} else {
			return Defined.createDefinition(Variable.class, Subset.class, name, l, u, n);
		}
	}
	
	@Override
	public String toString() {
		return encode();
	}
	
	@Override
	public String encode() {
		StringBuilder sb = new StringBuilder();
		int[] array = toArray();
		
		sb.append('{');

		for (int i=0; i<array.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			
			sb.append(array[i]);
		}
		
		sb.append('}');
		return sb.toString();
	}
	
	@Override
	public void decode(String value) {
		if (value.startsWith("{") && value.endsWith("}")) {
			value = value.substring(1, value.length()-1);
		}
		
		String[] tokens = value.split(",");
		int[] array = new int[tokens.length];
		
		for (int i=0; i<tokens.length; i++) {
			array[i] = Integer.parseInt(tokens[i]);
		}
		
		fromArray(array);
	}
	
	/**
	 * Returns the value stored in a subset decision variable.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a subset decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Subset}
	 */
	public static int[] getSubset(Variable variable) {
		Subset subset = Validate.that("variable", variable).isA(Subset.class);
		return subset.toArray();
	}
	
	/**
	 * Returns the value stored in a subset decision variable with the items sorted.
	 * 
	 * @param variable the decision variable
	 * @return the value stored in a subset decision variable with the items sorted
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Subset}
	 */
	public static int[] getOrderedSubset(Variable variable) {
		int[] subset = getSubset(variable);
		Arrays.sort(subset);
		return subset;
	}
	
	/**
	 * Returns the subset as a binary string, where 1 indicates the index is included in the set.
	 * 
	 * @param variable the decision variable
	 * @return a binary string representation of the subset
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Subset}
	 */
	public static boolean[] getSubsetAsBinary(Variable variable) {
		Subset subset = Validate.that("variable", variable).isA(Subset.class);
		boolean[] result = new boolean[subset.getN()];
			
		for (int i = 0; i < subset.getN(); i++) {
			result[i] = subset.contains(i);
		}
			
		return result;
	}
	
	/**
	 * Returns the subset as a BitSet, where a set bit indicates the index is included in the set.
	 * 
	 * @param variable the decision variable
	 * @return a BitSet representation of the subset
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Subset}
	 */
	public static BitSet getSubsetAsBitSet(Variable variable) {
		Subset subset = Validate.that("variable", variable).isA(Subset.class);
		BitSet bitSet = new BitSet(subset.getN());
			
		for (int i = 0; i < subset.getN(); i++) {
			bitSet.set(i, subset.contains(i));
		}
			
		return bitSet;
	}
	
	/**
	 * Sets the value of a subset decision variable.
	 * 
	 * @param variable the decision variable
	 * @param values the subset to assign the subset decision variable
	 * @throws IllegalArgumentException if the decision variable is not of type {@link Subset}
	 * @throws IllegalArgumentException if {@code values} is not a valid subset
	 */
	public static void setSubset(Variable variable, int[] values) {
		Subset subset = Validate.that("variable", variable).isA(Subset.class);
		subset.fromArray(values);
	}
	
	/**
	 * Sets the value of a subset decision variable using a binary string representation.  Indices set to {@code true}
	 * are included in the subset.
	 * 
	 * @param variable the decision variable
	 * @param values the binary string representation of a subset
	 * @throws IllegalArgumentException if the binary string representation is not a valid subset
	 */
	public static void setSubset(Variable variable, boolean[] values) {
		BitSet bitSet = new BitSet(values.length);
			
		for (int i = 0; i < values.length; i++) {
			bitSet.set(i, values[i]);
		}
			
		setSubset(variable, bitSet);
	}
	
	/**
	 * Sets the value of a subset decision variable using a BitSet representation.  Indices set to {@code true}
	 * are included in the subset.
	 * 
	 * @param variable the decision variable
	 * @param bitSet the BitSet representation of a subset
	 * @throws IllegalArgumentException if the BitSet representation is not a valid subset
	 */
	public static void setSubset(Variable variable, BitSet bitSet) {
		Subset subset = Validate.that("variable", variable).isA(Subset.class);
		int[] values = new int[bitSet.cardinality()];
		int count = 0;
			
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) {
			values[count++] = i;
		}
			
		setSubset(subset, values);
	}

}
