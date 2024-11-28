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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.validate.Validate;

/**
 * A collection of parameters along with methods to enumerate or generate samples.
 */
public class ParameterSet implements Iterable<Parameter<?>> {
	
	private final List<Parameter<?>> parameters;
	
	/**
	 * Constructs a new, empty parameter set.
	 */
	public ParameterSet() {
		super();
		this.parameters = new ArrayList<>();
	}
	
	/**
	 * Constructs a parameter set with the given parameters.
	 * 
	 * @param parameters the parameters
	 */
	@SafeVarargs
	public ParameterSet(Parameter<?>... parameters) {
		this(List.of(parameters));
	}
	
	/**
	 * Constructs a parameter set with the given parameters.
	 * 
	 * @param parameters the parameters
	 */
	public ParameterSet(Collection<? extends Parameter<?>> parameters) {
		this();
		
		for (Parameter<?> parameter : parameters) {
			add(parameter);
		}
	}
	
	/**
	 * Returns the number of parameters.
	 * 
	 * @return the number of parameters
	 */
	public int size() {
		return parameters.size();
	}
	
	/**
	 * Adds a parameter to this parameter set.
	 * 
	 * @param parameter the new parameter
	 */
	void add(Parameter<?> parameter) {
		parameters.add(parameter);
	}
	
	/**
	 * Returns the parameter at the given index.
	 * 
	 * @param index the index
	 * @return the parameter
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public Parameter<?> get(int index) {
		return parameters.get(index);
	}
	
	/**
	 * Returns the parameter with the given name.
	 * 
	 * @param name the parameter name
	 * @return the parameter
	 * @throws NoSuchParameterException if no parameter matching the name was found
	 */
	public Parameter<?> get(String name) {
		for (Parameter<?> parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase(name)) {
				return parameter;
			}
		}
		
		throw new NoSuchParameterException(name);
	}
	
	/**
	 * Returns the index of the given parameter.
	 * 
	 * @param parameter the parameter
	 * @return the index of the parameter
	 */
	public int indexOf(Parameter<?> parameter) {
		return parameters.indexOf(parameter);
	}
	
	/**
	 * Returns the index of the parameter with the given name.
	 * 
	 * @param name the parameter name
	 * @return the index of the parameter
	 */
	public int indexOf(String name) {
		int index = 0;
		
		for (Parameter<?> parameter : parameters) {
			if (parameter.getName().equalsIgnoreCase(name)) {
				return index;
			}
			
			index += 1;
		}
		
		throw new NoSuchParameterException(name);
	}
	
	/**
	 * Returns {@code true} if this parameter set supports enumeration; {@code false} otherwise.  All parameters must
	 * be {@link EnumeratedParameter} or {@link Constant}.
	 * 
	 * @return {@code true} if this parameter set supports enumeration; {@code false} otherwise.
	 * @see #enumerate()
	 */
	public boolean isEnumerable() {
		for (Parameter<?> parameter : parameters) {
			if (!(parameter instanceof EnumeratedParameter<?>) && !(parameter instanceof Constant<?>)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Enumerates samples.  This is effectively a "cross join" of each enumerated parameter, meaning every possible
	 * combination of values is generated.  If {@code k} is the number of parameters and {@code N} is the number of
	 * values per enumeration, the result will contain {@code N^k} samples.
	 * 
	 * @return the enumerated samples
	 * @throws FrameworkException if any parameter is not enumerable or a constant
	 */
	public Samples enumerate() {
		List<Sample> result = new ArrayList<>();
		result.add(new Sample());
		
		for (Parameter<?> parameter : parameters) {
			if (parameter instanceof EnumeratedParameter<?> enumeratedParameter) {
				result = enumeratedParameter.enumerate(result);
			} else if (parameter instanceof Constant<?> constantParameter) {
				constantParameter.apply(result);
			} else {
				Validate.that("parameter", parameter).fails("Unsupported parameter " + parameter.getName() +
						" of type" + parameter.getClass().getName() + ", must be enumerated");
			}
		}

		return new Samples(this, result);
	}

	/**
	 * Generates samples according so some sequence.  The sequence is used to generate numbers between {@code 0.0} and
	 * {@code 1.0}, which are then converted to the parameter value by calling
	 * {@link SampledParameter#sample(Sample, double)}.
	 * 
	 * @param numberOfSamples the number of samples to generate
	 * @param sequence the sequence generator
	 * @return the generated samples
	 * @throws FrameworkException if any parameter is not sampled or a constant
	 */
	public Samples sample(int numberOfSamples, Sequence sequence) {
		// Identify which parameters are sampled vs constants.
		List<SampledParameter<?>> sampledParameters = new ArrayList<>();
		List<Constant<?>> constantParameters = new ArrayList<>();
		
		for (Parameter<?> parameter : parameters) {
			if (parameter instanceof SampledParameter sampledParameter) {
				sampledParameters.add(sampledParameter);
			} else if (parameter instanceof Constant<?> constantParameter) {
				constantParameters.add(constantParameter);
			} else {
				Validate.that("parameter", parameter).fails("Unsupported parameter " + parameter.getName() +
						" of type " + parameter.getClass().getName() + ", must be sampled or constant");
			}
		}
		
		List<Sample> result = new ArrayList<>();
		
		// Process the sampled parameters using the provided sequence generator.
		if (sampledParameters.size() > 0) {
			double[][] sequences = sequence.generate(numberOfSamples, sampledParameters.size());
			
			for (double[] seq : sequences) {
				Sample sample = new Sample();
				
				for (int i = 0; i < sampledParameters.size(); i++) {
					sampledParameters.get(i).sample(sample, seq[i]);
				}
				
				result.add(sample);
			}
		} else {
			result.add(new Sample());
		}
		
		// Process the constants.
		for (int i = 0; i < constantParameters.size(); i++) {
			for (Sample sample : result) {
				constantParameters.get(i).apply(sample);
			}
		}

		return new Samples(this, result);
	}

	@Override
	public Iterator<Parameter<?>> iterator() {
		return parameters.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Parameter<?> parameter : parameters) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			
			sb.append(parameter.toString());
		}
		
		return sb.toString();
	}
	
	/**
	 * Loads the parameter set.  See {@link #load(Reader)} for details.
	 * 
	 * @param file the file
	 * @return the parameter set
	 * @throws IOException if an I/O error occurred
	 * @throws InvalidParameterException if any parameter was invalid
	 */
	public static ParameterSet load(File file) throws IOException {
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
	 * @throws IOException if an I/O error occurred
	 * @throws InvalidParameterException if any parameter was invalid
	 * @see Parameter#decode(Tokenizer, String)
	 */
	public static ParameterSet load(Reader reader) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		ParameterSet parameterSet = new ParameterSet();
		
		try (LineReader lineReader = LineReader.wrap(reader).skipComments().skipBlanks()) {
			for (String line : lineReader) {
				Parameter<?> parameter = Parameter.decode(tokenizer, line);
				parameterSet.add(parameter);
			}
	
			return parameterSet;
		}
	}
	
	/**
	 * Saves the parameter set to a file.
	 * 
	 * @param file the file
	 * @throws IOException if an I/O error occurred
	 */
	public void save(File file) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			save(writer);
		}
	}
	
	/**
	 * Writes the parameter set.
	 * 
	 * @param writer the writer
	 * @throws IOException if an I/O error occurred
	 */
	public void save(Writer writer) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		
		for (Parameter<?> parameter : this) {
			writer.write(parameter.encode(tokenizer));
			writer.write(System.lineSeparator());
		}
	}

}
