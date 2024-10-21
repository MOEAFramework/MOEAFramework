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
package org.moeaframework.analysis.sample;

import java.io.BufferedReader;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.io.CommentedLineReader;

public class Samples implements Iterable<Sample> {

	private final ParameterSet<?> parameterSet;

	private final List<Sample> samples;

	public Samples(ParameterSet<?> parameterSet) {
		super();
		this.parameterSet = parameterSet;
		this.samples = Collections.synchronizedList(new ArrayList<>());
	}

	public Samples(ParameterSet<?> parameterSet, Collection<Sample> samples) {
		this(parameterSet);
		addAll(samples);
	}

	public Samples(ParameterSet<?> parameterSet, Iterable<Sample> samples) {
		this(parameterSet);
		addAll(samples);
	}

	public int size() {
		return samples.size();
	}

	public boolean isEmpty() {
		return samples.isEmpty();
	}

	void add(Sample sample) {
		this.samples.add(sample);
	}

	void addAll(Collection<Sample> samples) {
		this.samples.addAll(samples);
	}

	void addAll(Iterable<Sample> samples) {
		for (Sample sample : samples) {
			add(sample);
		}
	}
	
	public Sample get(int index) {
		return samples.get(index);
	}
	
	public <T> List<T> distinctValues(Parameter<T> parameter) {
		return samples.stream().map(x -> parameter.parse(x.getString(parameter.getName()))).distinct().toList();
	}

	@Override
	public Iterator<Sample> iterator() {
		return Collections.unmodifiableList(samples).iterator();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(samples)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		Samples rhs = (Samples)obj;
		return new EqualsBuilder()
				.append(samples, rhs.samples)
				.isEquals();
	}
	
	// TODO: This assumes values are whitespace separated, which might not be true for all parameters.  Improve
	// the encoding used.
	
	public static Samples load(File file, ParameterSet<?> parameterSet) throws FileNotFoundException, IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return load(reader, parameterSet);
		}
	}
	
	public static Samples load(Reader reader, ParameterSet<?> parameterSet) throws IOException {
		try (CommentedLineReader lineReader = CommentedLineReader.wrap(reader)) {
			Samples samples = new Samples(parameterSet);
			
			for (String line : Iterators.of(lineReader)) {
				String[] tokens = line.trim().split("\\s+");
				
				if (tokens.length != parameterSet.size()) {
					throw new IOException("invalid line: " + line);
				}
				
				Sample sample = new Sample();
				
				for (int i = 0; i < parameterSet.size(); i++) {
					Parameter<?> parameter = parameterSet.get(i);
					parameter.parse(tokens[i]);
					sample.setString(parameter.getName(), tokens[i]);
				}
				
				samples.add(sample);
			}
			
			return samples;
		}
	}

	
	public void save(File file) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			save(writer);
		}
	}
	
	public void save(Writer writer) throws IOException {
		for (Sample sample : samples) {
			for (int i = 0; i < parameterSet.size(); i++) {
				if (i > 0) {
					writer.write(' ');
				}
				
				writer.write(sample.getString(parameterSet.get(i).getName()));
			}
			
			writer.write(System.lineSeparator());
		}
	}

}