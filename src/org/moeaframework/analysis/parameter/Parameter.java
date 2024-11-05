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

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.core.Named;
import org.moeaframework.util.io.Tokenizer;

/**
 * Represents a typed parameter.
 * <p>
 * To support saving and loading parameters from a file, all parameters must implement the {@link #encode(Tokenizer)}
 * method along with a static {@link #decode(Tokenizer, String)} method.
 * 
 * @param <T> the type of the parameter
 * @see Sample
 */
public interface Parameter<T> extends Named {

	/**
	 * Parses this parameter value from the given string.
	 * 
	 * @param str the string
	 * @return the parameter value
	 * @throws InvalidParameterException if the given string is not a valid parameter value
	 */
	public T parse(String str);
	
	/**
	 * Encodes this parameter definition in a format suitable for storing in a file.
	 * 
	 * @param tokenizer the tokenizer
	 * @return the string representation
	 */
	public String encode(Tokenizer tokenizer);
	
	/**
	 * Reads the parameter value from the given sample.
	 * 
	 * @param sample the sample
	 * @return the parameter value
	 */
	public default T readValue(Sample sample) {
		return parse(sample.getString(getName()));
	}
	
	/**
	 * Assigns the parameter value in the given sample.
	 * 
	 * @param sample the sample
	 * @param value the value to assign
	 */
	public default void assignValue(Sample sample, T value) {
		sample.setString(getName(), value.toString());
	}
	
	/**
	 * Entry point to using the parameter builder.  This starts by specifying the name, with additional options being
	 * configured on the returned builder.
	 * 
	 * @param name the parameter name
	 * @return the parameter builder
	 */
	public static ParameterBuilder named(String name) {
		return new ParameterBuilder(name);
	}
	
	/**
	 * Decodes the string representation of a parameter.
	 * 
	 * @param tokenizer the tokenizer
	 * @param line the string representation
	 * @return the decoded parameter
	 * @throws InvalidParameterException if the string representation is not a valid parameter
	 */
	public static Parameter<?> decode(Tokenizer tokenizer, String line) {
		String[] tokens = tokenizer.decodeToArray(line);
		
		if (tokens.length < 2) {
			throw new InvalidParameterException(tokens[0], "missing type");
		}
		
		Map<String, BiFunction<Tokenizer, String, Parameter<?>>> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		types.put("const", Constant::decode);
		types.put("enum", Enumeration::decode);
		types.put("int", IntegerRange::decode);
		types.put("long", LongRange::decode);
		types.put("decimal", DecimalRange::decode);
		
		BiFunction<Tokenizer, String, Parameter<?>> decoder = types.get(tokens[1]);
		
		if (decoder == null) {
			if (tokens.length == 3) {
				// legacy format
				try {
					double lowerBound = Double.parseDouble(tokens[1]);
					double upperBound = Double.parseDouble(tokens[2]);
					return new DecimalRange(tokens[0], lowerBound, upperBound);
				} catch (NumberFormatException e) {
					// fall through
				}
			}
			
			throw new InvalidParameterException(tokens[0], "invalid type '" + tokens[1] + "', valid values: " +
					String.join(", ", types.keySet().toArray(String[]::new)));
		}
		
		return decoder.apply(tokenizer, line);
	}
	
}
