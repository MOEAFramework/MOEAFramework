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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Defined;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.validate.Validate;

/**
 * Decision variable for grammars.  This class represents the grammar as a variable-length integer codon which is
 * subsequently converted into a grammar using {@link ContextFreeGrammar#build}.
 * 
 * @see ContextFreeGrammar
 */
public class Grammar extends AbstractVariable {

	private static final long serialVersionUID = 1701058698946283174L;

	/**
	 * The integer codon of this grammar.
	 */
	private int[] codon;

	/**
	 * The number of values that each codon can represent.  Each index in the codon array can be assigned a value in
	 * the range {@code [0, maximumValue-1]}.
	 */
	private int maximumValue = 256;

	/**
	 * Constructs a grammar variable with the specified initial size.
	 * 
	 * @param size the initial size of this grammar
	 */
	public Grammar(int size) {
		this(null, size);
	}

	/**
	 * Constructs a grammar variable with the specified initial size and name.
	 * 
	 * @param name the name of this decision variable
	 * @param size the initial size of this grammar
	 */
	public Grammar(String name, int size) {
		super(name);

		fromArray(new int[size]);
	}

	/**
	 * Returns the number of values that each codon can represent.  Each index in the codon array can be assigned a
	 * value in the range {@code [0, maximumValue-1]}
	 * 
	 * @return the number of values that each codon can represent
	 */
	public int getMaximumValue() {
		return maximumValue;
	}

	/**
	 * Sets the number of values that each codon can represent.  Each index in the codon array can be assigned a value
	 * in the range {@code [0, maximumValue-1]}.
	 * 
	 * @param maximumValue the number of values that each codon can represent
	 */
	public void setMaximumValue(int maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * Returns the integer codon representation of this grammar.  The returned object is a clone of the internal
	 * storage, and thus can be modified independently of this instance.
	 * 
	 * @return the integer codon representation of this grammar
	 */
	public int[] toArray() {
		return codon.clone();
	}

	/**
	 * Sets the integer codon representation of this grammar.  The stored object is a clone of the argument, and thus
	 * can be modified independently of this instance.
	 * 
	 * @param codon the new integer codon representation for this grammar
	 * @throws IllegalArgumentException if any codon value is out of range
	 *         ({@code (value < 0) || (value >= getMaximumValue())})
	 */
	public void fromArray(int[] codon) {
		for (int i = 0; i < codon.length; i++) {
			Validate.that("codon[i]", codon[i]).isBetween(0, maximumValue - 1);
		}

		this.codon = codon.clone();
	}

	/**
	 * Returns the length of the integer codon representation of this grammar.
	 * 
	 * @return the length of the integer codon representation of this grammar
	 */
	public int size() {
		return codon.length;
	}

	/**
	 * Sets the specified index in the integer codon representation of this grammar to the specified value.
	 * 
	 * @param index the index of the codon to be assigned
	 * @param value the new value for the specified index
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range ({@code (index < 0) || (index >= size())})
	 * @throws IllegalArgumentException if the value is out of range
	 *         ({@code (value < 0) || (value >= getMaximumValue())})
	 */
	public void set(int index, int value) {
		Validate.that("value", value).isBetween(0, maximumValue - 1);
		codon[index] = value;
	}

	/**
	 * Returns the value at the specified index in the integer codon representation of this grammar.
	 * 
	 * @param index the index of the codon value to be returned
	 * @return the value at the specified index in the integer codon representation of this grammar
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 *         ({@code (index < 0) || (index >= size())})
	 */
	public int get(int index) {
		return codon[index];
	}
	
	/**
	 * Returns the string representation of this grammar produced by evaluating the context-free grammar with the
	 * codon array.
	 * 
	 * @param cfg the context-free grammar
	 * @return the string representation of the grammar, or {@code null} if no valid grammar was produced
	 * @see ContextFreeGrammar#build(int[])
	 */
	public String build(ContextFreeGrammar cfg) {
		return cfg.build(codon);
	}

	@Override
	public Grammar copy() {
		Grammar copy = new Grammar(name, codon.length);
		copy.fromArray(codon);
		return copy;
	}

	/**
	 * Removes the indices in the range {@code [start, end]} from the integer codon representation, returning array of
	 * the values removed by this cut operation.  For example,
	 * <pre>
	 *   Grammar grammar = new Grammar(new int[] { 0, 1, 2, 3, 4, 5 });
	 *   int[] removed = grammar.cut(2, 4);
	 * </pre>
	 * results in grammar representing the array {@code [2, 3, 4]} and removed containing {@code [0, 1, 5]}.
	 * 
	 * @param start the start index of the cut operation
	 * @param end the end index of the cut operation
	 * @return the array of values removed by this cut operation
	 */
	public int[] cut(int start, int end) {
		Validate.that("start", start).isLessThanOrEqualTo("end", end);

		int[] newCodon = new int[codon.length - (end - start + 1)];
		int[] result = new int[end - start + 1];
		int index = 0;

		for (int i = 0; i < start; i++) {
			newCodon[index++] = codon[i];
		}

		for (int i = start; i <= end; i++) {
			result[i - start] = codon[i];
		}

		for (int i = end + 1; i < codon.length; i++) {
			newCodon[index++] = codon[i];
		}

		fromArray(newCodon);
		return result;
	}

	/**
	 * Inserts the specified array into this grammar's integer codon representation at the specified insert index.
	 * For example,
	 * <pre>
	 *   Grammar grammar = new Grammar(new int[] { 0, 1, 2, 3, 4, 5 });
	 *   grammar.insert(2, new int[] { 6, 7 });
	 * </pre>
	 * results in grammar representing the array {@code [0, 1, 6, 7, 2, 3, 4, 5]}.
	 * 
	 * @param insertIndex the index where the specified array is to be inserted
	 * @param array the array of integer codons to be inserted
	 */
	public void insert(int insertIndex, int[] array) {
		int[] newCodon = new int[codon.length + array.length];
		int index = 0;

		for (int i = 0; i < insertIndex; i++) {
			newCodon[index++] = codon[i];
		}

		for (int i = 0; i < array.length; i++) {
			newCodon[index++] = array[i];
		}

		for (int i = insertIndex; i < codon.length; i++) {
			newCodon[index++] = codon[i];
		}

		fromArray(newCodon);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(codon)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Grammar rhs = (Grammar)obj;
			
			return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(codon, rhs.codon)
					.isEquals();
		}
	}

	@Override
	public void randomize() {
		for (int i = 0; i < codon.length; i++) {
			codon[i] = PRNG.nextInt(getMaximumValue());
		}
	}
	
	@Override
	public String getDefinition() {
		if (name == null) {
			return Defined.createDefinition(Variable.class, Grammar.class, size());
		} else {
			return Defined.createDefinition(Variable.class, Grammar.class, name, size());
		}
	}
	
	@Override
	public String toString() {
		return encode();
	}
	
	@Override
	public String encode() {
		StringBuilder sb = new StringBuilder();
		sb.append("Grammar(");
		
		int[] array = toArray();
		
		for (int i=0; i<array.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			
			sb.append(array[i]);
		}
		
		sb.append(")");
		return sb.toString();
	}
	
	@Override
	public void decode(String value) {
		if (!value.startsWith("Grammar(") || !value.endsWith(")")) {
			throw new VariableEncodingException("Failed to decode grammar, missing 'Grammar(' ... ')'");
		}
		
		String[] tokens = value.substring(8, value.length()-1).split(",");
		int[] array = new int[tokens.length];
		
		for (int i=0; i<tokens.length; i++) {
			array[i] = Integer.parseInt(tokens[i]);
		}
		
		fromArray(array);
	}

}
