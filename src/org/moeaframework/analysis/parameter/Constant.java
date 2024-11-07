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
package org.moeaframework.analysis.parameter;

import java.util.List;

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.io.Tokenizer;

/**
 * A parameter assigned a constant value.
 * 
 * @param <T> the type of this parameter
 */
public class Constant<T> extends AbstractParameter<T> {
	
	private final T value;
	
	/**
	 * Constructs a new constant parameter.
	 * 
	 * @param name the name of this parameter
	 * @param value the constant value
	 */
	public Constant(String name, T value) {
		super(name);
		this.value = value;
	}
	
	/**
	 * Returns the constant value.
	 * 
	 * @return the constant value
	 */
	public T getValue() {
		return value;
	}
	
	@Override
	public T parse(String str) {
		if (!value.toString().equals(str)) {
			throw new InvalidParameterException(getName(), "constant expected to have value '" + value +
					"', but was '" + str + "'");
		}
		
		return value;
	}
	
	/**
	 * Applies this constant to the given sample.
	 * 
	 * @param sample thes ample
	 */
	public void apply(Sample sample) {
		assignValue(sample, value);
	}
	
	/**
	 * Applies this constant to the given samples.
	 * 
	 * @param samples the samples
	 */
	public void apply(Iterable<Sample> samples) {
		for (Sample sample : samples) {
			apply(sample);
		}
	}
	
	@Override
	public String encode(Tokenizer tokenizer) {
		return tokenizer.encode(List.of(getName(), "const", getValue().toString()));
	}
	
	/**
	 * Decodes the string representation of this parameter.
	 * 
	 * @param tokenizer the tokenizer
	 * @param line the string representation
	 * @return the decoded parameter
	 * @throws InvalidParameterException if the string representation is not a valid parameter
	 */
	public static Constant<String> decode(Tokenizer tokenizer, String line) {
		String[] tokens = tokenizer.decodeToArray(line);
		
		if (tokens.length < 2) {
			throw new InvalidParameterException(tokens[0], "missing type");
		}
		
		if (!tokens[1].equalsIgnoreCase("const")) {
			throw new InvalidParameterException(tokens[0], "type does not match 'const'");
		}
		
		if (tokens.length != 3) {
			throw new InvalidParameterException(tokens[0], "constants require exactly one value");
		}
		
		return new Constant<String>(tokens[0], tokens[2]);
	}

}
