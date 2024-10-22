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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Tokenizer;

public abstract class ParameterSet<T extends Parameter<?>> implements Iterable<T> {
		
	protected final List<T> parameters;
	
	public ParameterSet() {
		super();
		this.parameters = new ArrayList<T>();
	}
	
	@SafeVarargs
	public ParameterSet(T... parameters) {
		this(List.of(parameters));
	}
	
	public ParameterSet(Collection<T> parameters) {
		this();
		
		for (T parameter : parameters) {
			add(parameter);
		}
	}
	
	public int size() {
		return parameters.size();
	}
	
	public void add(T parameter) {
		parameters.add(parameter);
	}
	
	public T get(int index) {
		return parameters.get(index);
	}
	
	public T get(String name) {
		for (T parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase(name)) {
				return parameter;
			}
		}
		
		throw new NoSuchParameterException(name);
	}

	@Override
	public Iterator<T> iterator() {
		return parameters.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (T parameter : parameters) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			
			sb.append(parameter.toString());
		}
		
		return sb.toString();
	}
	
	public static SampledParameterSet load(File file) throws FileNotFoundException, IOException {
		try (FileReader reader = new FileReader(file)) {
			return load(reader);
		}
	}
	
	public static SampledParameterSet load(Reader reader) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		SampledParameterSet parameterSet = new SampledParameterSet();
		
		try (LineReader lineReader = LineReader.wrap(reader).skipComments()) {
			for (String line : lineReader) {
				String[] tokens = tokenizer.decodeToArray(line);
				Parameter<?> parameter = null;
				
				if (tokens.length == 3) {
					// Constant format: <name> <type> <value>
					if (tokens[1].equalsIgnoreCase("int") || tokens[1].equalsIgnoreCase("integer")) {
						parameter = new Constant<Integer>(tokens[0], Integer.parseInt(tokens[2]));
					} else if (tokens[1].equalsIgnoreCase("long")) {
						parameter = new Constant<Long>(tokens[0], Long.parseLong(tokens[2]));
					} else if (tokens[1].equalsIgnoreCase("double") || tokens[1].equalsIgnoreCase("decimal")) {
						parameter = new Constant<Double>(tokens[0], Double.parseDouble(tokens[2]));
					} else if (tokens[1].equalsIgnoreCase("const") || tokens[1].equalsIgnoreCase("constant")) {
						parameter = new Constant<String>(tokens[0], tokens[2]);
					} else if (tokens[1].equalsIgnoreCase("enum") || tokens[1].equalsIgnoreCase("enumeration")) {
						parameter = new Enumeration<String>(tokens[0], tokens[2]);
					} else {
						// Legacy format: <name> <lowerBound> <upperBound>
						parameter = new DecimalRange(tokens[0], Double.parseDouble(tokens[1]),
								Double.parseDouble(tokens[2]));
					}
				}
				
				if (parameter == null && tokens.length == 4) {
					// Range format: <name> <type> <lowerBound> <upperBound>
					if (tokens[1].equalsIgnoreCase("int") || tokens[1].equalsIgnoreCase("integer")) {
						parameter = new IntegerRange(tokens[0], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
					} else if (tokens[1].equalsIgnoreCase("long")) {
						parameter = new LongRange(tokens[0], Long.parseLong(tokens[2]), Long.parseLong(tokens[3]));
					} else if (tokens[1].equalsIgnoreCase("double") || tokens[1].equalsIgnoreCase("decimal")) {
						parameter = new DecimalRange(tokens[0], Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
					}
				}
				
				if (parameter == null && tokens.length > 3) {
					// Enumeration format: <name> enum <value1> [... <valueN>]
					if (tokens[1].equalsIgnoreCase("enum") || tokens[1].equalsIgnoreCase("enumeration")) {
						parameter = new Enumeration<String>(tokens[0], Arrays.copyOfRange(tokens, 2, tokens.length));
					}
				}
				
				if (parameter == null) {
					throw new IOException("invalid line: " + line);
				}

				parameterSet.add(parameter);
			}
	
			return parameterSet;
		}
	}

}
