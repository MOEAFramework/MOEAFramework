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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

/**
 * An enumeration of a fixed set of possible values.  This parameter can either be used to enumerate all possible
 * values or sample according to a sequence.
 * 
 * @param <T> the type of each value
 */
public class Enumeration<T> extends AbstractParameter<T> implements EnumeratedParameter<T>, SampledParameter<T> {
	
	private List<T> values;
	
	/**
	 * Constructs an enumeration with a fixed set of possible values.
	 * 
	 * @param name the parameter name
	 * @param values the values
	 */
	@SafeVarargs
	public Enumeration(String name, T... values) {
		this(name, List.of(values));
	}

	/**
	 * Constructs an enumeration with a fixed set of possible values.
	 * 
	 * @param name the parameter name
	 * @param values the values
	 */
	public Enumeration(String name, List<T> values) {
		super(name);
		this.values = values;
	}
	
	/**
	 * Returns the number of values defined by this enumeration.
	 * 
	 * @return the number of values
	 */
	public int size() {
		return values.size();
	}
	
	@Override
	public List<T> values() {
		return values;
	}
	
	@Override
	public T parse(String str) {
		for (T value : values) {
			if (value.toString().equals(str)) {
				return value;
			}
		}
		
		throw new InvalidParameterException(getName(), "invalid value '" + str + "', expected one of: " +
				values.stream().map(Object::toString).collect(Collectors.joining(", ")));
	}

	@Override
	public List<Sample> enumerate(List<Sample> samples) {
		List<Sample> result = new ArrayList<Sample>();
		
		for (Sample sample : samples) {
			for (T value : values) {
				Sample newSample = new Sample();
				newSample.addAll(sample);
				assignValue(newSample, value);
				result.add(newSample);
			}
		}
		
		return result;
	}
	
	@Override
	public void sample(Sample sample, double scale) {
		Validate.that("scale", scale).isBetween(0.0, 1.0);
		int index = (int)(scale * Math.nextAfter(values.size(), Double.NEGATIVE_INFINITY));
		assignValue(sample, values.get(index));
	}

	@Override
	public String encode(Tokenizer tokenizer) {
		return tokenizer.encode(Stream.concat(Stream.of(getName(), "enum"), values().stream().map(Object::toString)));
	}
	
	/**
	 * Decodes the string representation of this parameter.
	 * 
	 * @param tokenizer the tokenizer
	 * @param line the string representation
	 * @return the decoded parameter
	 * @throws InvalidParameterException if the string representation is not a valid parameter
	 */
	public static Enumeration<String> decode(Tokenizer tokenizer, String line) {
		String[] tokens = tokenizer.decodeToArray(line);
		
		if (tokens.length < 2) {
			throw new InvalidParameterException(tokens[0], "missing type");
		}
		
		if (!tokens[1].equalsIgnoreCase("enum")) {
			throw new InvalidParameterException(tokens[0], "type does not match 'enum'");
		}
		
		if (tokens.length < 3) {
			throw new InvalidParameterException(tokens[0], "enumerations require at least one value");
		}
		
		return new Enumeration<String>(tokens[0], Arrays.copyOfRange(tokens, 2, tokens.length));
	}

}
