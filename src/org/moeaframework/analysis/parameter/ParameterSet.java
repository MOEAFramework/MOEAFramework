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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
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
	
	/**
	 * Returns the parameter with the given name.
	 * 
	 * @param name the parameter name
	 * @return the parameter
	 * @throws NoSuchParameterException if no parameter matching the name was found
	 */
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
	
	/**
	 * Loads the parameter set.  See {@link #load(Reader) for details.
	 * 
	 * @param file the file
	 * @return the parameter set
	 * @throws IOException if an I/O error occurred while reading the parameter set
	 * @throws InvalidParameterException if any parameter was invalid
	 */
	public static SampledParameterSet load(File file) throws FileNotFoundException, IOException {
		try (FileReader reader = new FileReader(file)) {
			return load(reader);
		}
	}
	
	/**
	 * Loads the parameter set.  Parameters can be defined in the following formats:
	 * <pre>{@code
	 *   <name> <lb> <ub>                         # Legacy format
	 *   <name> <type> <lb> <ub>                  # Range
	 *   <name> const <val>                       # Constant
	 *   <name> enum <val1> <val2> ... <valN>     # Enumeration
	 * }</pre>
	 * 
	 * @param reader the reader
	 * @return the parameter set
	 * @throws IOException if an I/O error occurred while reading the parameter set
	 * @throws InvalidParameterException if any parameter was invalid
	 */
	public static SampledParameterSet load(Reader reader) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		SampledParameterSet parameterSet = new SampledParameterSet();
		
		try (LineReader lineReader = LineReader.wrap(reader).skipComments().skipBlanks()) {
			for (String line : lineReader) {
				Parameter<?> parameter = Parameter.decode(tokenizer, line);
				parameterSet.add(parameter);
			}
	
			return parameterSet;
		}
	}
	
	public void save(File file) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			save(writer);
		}
	}
	
	public void save(Writer writer) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		
		for (T parameter : this) {
			writer.write(parameter.encode(tokenizer));
			writer.write(System.lineSeparator());
		}
	}

}
