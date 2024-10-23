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

public class Constant<T> extends AbstractParameter<T> implements EnumeratedParameter<T> {
	
	private final T value;
	
	public Constant(String name, T value) {
		super(name);
		this.value = value;
	}
	
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
	
	@Override
	public List<Sample> enumerate(List<Sample> samples) {
		for (Sample sample : samples) {
			sample.setString(getName(), value.toString());
		}
		
		return samples;
	}
	
	@Override
	public List<T> values() {
		return List.of(value);
	}
	
	@Override
	public String toString() {
		return getName() + ": " + getValue();
	}
	
	@Override
	public String encode(Tokenizer tokenizer) {
		return tokenizer.encode(List.of(getName(), "const", getValue().toString()));
	}
	
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
