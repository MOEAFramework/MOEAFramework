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
package org.moeaframework.analysis.sample;

import java.io.BufferedWriter;
import java.io.File;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.stream.DataStream;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Tokenizer;

/**
 * A collection of samples, typically associated with a parameter set that generated the samples.
 * <p>
 * Samples can be stored in a file using the {@link #save(File)} and {@link #load(File, ParameterSet)} methods.
 * The file begins with a header line listing the parameter names.  Then, each sample is written on its own line,
 * with the parameter values separated by whitespace.  The ordering of parameters must match the provided
 * {@link ParameterSet}.
 */
public class Samples implements Iterable<Sample>, Formattable<Sample>, DataStream<Sample> {

	private final ParameterSet parameterSet;

	private final List<Sample> samples;

	/**
	 * Constructs an empty collection of samples.
	 * 
	 * @param parameterSet the parameter set defining the parameters included in each sample
	 */
	public Samples(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
		this.samples = Collections.synchronizedList(new ArrayList<>());
	}

	/**
	 * Constructs an empty collection of samples.
	 * 
	 * @param parameterSet the parameter set defining the parameters included in each sample
	 * @param samples the collection of samples
	 */
	public Samples(ParameterSet parameterSet, Collection<Sample> samples) {
		this(parameterSet);
		addAll(samples);
	}

	/**
	 * Constructs an empty collection of samples.
	 * 
	 * @param parameterSet the parameter set defining the parameters included in each sample
	 * @param samples the collection of samples
	 */
	public Samples(ParameterSet parameterSet, Iterable<Sample> samples) {
		this(parameterSet);
		addAll(samples);
	}
	
	/**
	 * That parameter set defining the parameters included in these samples.
	 * 
	 * @return the parameter set
	 */
	public ParameterSet getParameterSet() {
		return parameterSet;
	}

	/**
	 * Returns the number of samples.
	 * 
	 * @return the number of samples
	 */
	@Override
	public int size() {
		return samples.size();
	}

	/**
	 * Adds the given sample to this collection.
	 * 
	 * @param sample the sample to add
	 */
	void add(Sample sample) {
		this.samples.add(sample);
	}

	/**
	 * Adds all samples to this collection.
	 * 
	 * @param samples the samples to add
	 */
	void addAll(Collection<Sample> samples) {
		this.samples.addAll(samples);
	}

	/**
	 * Adds all samples to this collection.
	 * 
	 * @param samples the samples to add
	 */
	void addAll(Iterable<Sample> samples) {
		for (Sample sample : samples) {
			add(sample);
		}
	}
	
	/**
	 * Returns the sample at the given index.
	 * 
	 * @param index the index of the sample
	 * @return the sample at the given index
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public Sample get(int index) {
		return samples.get(index);
	}
	
	/**
	 * Evaluates each sample, collecting the results in a {@link SampledResults}.
	 * 
	 * @param <T> the return type of the function
	 * @param function the function used to evaluate each sample
	 * @return the results
	 */
	public <T> SampledResults<T> evaluateAll(Function<Sample, T> function) {
		SampledResults<T> results = new SampledResults<>(parameterSet);
		
		for (Sample sample : samples) {
			results.add(sample, function.apply(sample));
		}
		
		return results;
	}
	
	/**
	 * Evaluates each sample.
	 * 
	 * @param consumer the consumer used to evaluate each sample
	 */
	public void evaluateAll(Consumer<Sample> consumer) {
		evaluateAll((sample) -> {
			consumer.accept(sample);
			return null;
		});
	}
	
	/**
	 * Evaluates each sample in parallel, distributing across all available processors, and collecting the results in
	 * a {@link SampledResults}.
	 * 
	 * @param <T> the return type of the function
	 * @param function the function used to evaluate each sample
	 * @return the results
	 * @throws InterruptedException if the current thread was interrupted while waiting for evaluations to complete
	 * @throws ExecutionException if the function threw an exception
	 */
	public <T> SampledResults<T> distributeAll(Function<Sample, T> function) throws InterruptedException,
	ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		try {
			return evaluateAll(function, executor);
		} finally {
			executor.shutdown();
		}
	}
	
	/**
	 * Evaluates each sample in parallel, distributing across all available processors.
	 * 
	 * @param consumer the consumer used to evaluate each sample
	 * @throws InterruptedException if the current thread was interrupted while waiting for evaluations to complete
	 * @throws ExecutionException if the consumer threw an exception
	 */
	public void distributeAll(Consumer<Sample> consumer) throws InterruptedException, ExecutionException {
		distributeAll((sample) -> {
			consumer.accept(sample);
			return null;
		});
	}
	
	/**
	 * Evaluates each sample using the provided executor service, potentially distributing the evaluations in parallel.
	 * As such, the function should not have any side-effects.  The caller is expected to manage the lifecycle of the
	 * executor.
	 * 
	 * @param <T> the return type of the function
	 * @param function the function used to evaluate each sample
	 * @param executor the executor for distributing the evaluations
	 * @return the results
	 * @throws InterruptedException if the current thread was interrupted while waiting for evaluations to complete
	 * @throws ExecutionException if the function threw an exception
	 */
	public <T> SampledResults<T> evaluateAll(Function<Sample, T> function, ExecutorService executor)
			throws InterruptedException, ExecutionException {
		SampledResults<T> results = new SampledResults<>(parameterSet);
		List<Future<Pair<Sample, T>>> futures = new ArrayList<>();
		
		for (Sample sample : samples) {
			futures.add(executor.submit(() -> Pair.of(sample, function.apply(sample))));
		}
		
		for (Future<Pair<Sample, T>> future : futures) {
			Pair<Sample, T> result = future.get();
			results.add(result.getKey(), result.getValue());
		}
		
		return results;
	}

	@Override
	public Iterator<Sample> iterator() {
		return Collections.unmodifiableList(samples).iterator();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
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
	
	@Override
	public TabularData<Sample> asTabularData() {
		TabularData<Sample> table = new TabularData<>(samples);
		
		for (Parameter<?> parameter : parameterSet) {
			table.addColumn(new Column<>(parameter.getName(), x -> parameter.readValue(x)));
		}
				
		return table;
	}
	

	@Override
	public Stream<Sample> stream() {
		return samples.stream();
	}
	
	@Override
	public void forEach(Consumer<? super Sample> consumer) {
		DataStream.super.forEach(consumer);
	}

	/**
	 * Loads the samples from a file.
	 * 
	 * @param file the source file
	 * @param parameterSet the parameter set
	 * @return the loaded samples
	 * @throws IOException if an I/O error occurred
	 */
	public static Samples load(File file, ParameterSet parameterSet) throws IOException {
		try (FileReader reader = new FileReader(file)) {
			return load(reader, parameterSet);
		}
	}
	
	/**
	 * Loads the samples from a file.
	 * 
	 * @param reader the reader
	 * @param parameterSet the parameter set
	 * @return the loaded samples
	 * @throws IOException if an I/O error occurred
	 */
	public static Samples load(Reader reader, ParameterSet parameterSet) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		Samples samples = new Samples(parameterSet);
		
		try (LineReader lineReader = LineReader.wrap(reader).skipComments().skipBlanks()) {
			for (String line : lineReader) {
				String[] tokens = tokenizer.decodeToArray(line);
				
				if (tokens.length != parameterSet.size()) {
					throw new IOException("Invalid line, expected " + parameterSet.size() + " entries: " + line);
				}
				
				Sample sample = new Sample();
				
				for (int i = 0; i < parameterSet.size(); i++) {
					Parameter<?> parameter = parameterSet.get(i);
					parameter.parse(tokens[i]);
					sample.setString(parameter.getName(), tokens[i]);
				}
				
				samples.add(sample);
			}
		}
		
		return samples;
	}

	/**
	 * Saves the samples to a file.
	 * 
	 * @param file the destination file
	 * @throws IOException if an I/O error occurred
	 */
	public void save(File file) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			save(writer);
		}
	}
	
	/**
	 * Saves the samples to a file.
	 * 
	 * @param writer the writer
	 * @throws IOException if an I/O error occurred
	 */
	public void save(Writer writer) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		
		// write a header line
		writer.write("# ");
		
		for (int i = 0; i < parameterSet.size(); i++) {
			if (i > 0) {
				writer.write(tokenizer.getDelimiter());
			}
			
			writer.write(tokenizer.escape(parameterSet.get(i).getName()));
		}
		
		writer.write(System.lineSeparator());
		
		// write the content
		for (Sample sample : samples) {
			for (int i = 0; i < parameterSet.size(); i++) {
				if (i > 0) {
					writer.write(tokenizer.getDelimiter());
				}
				
				writer.write(tokenizer.escape(sample.getString(parameterSet.get(i).getName())));
			}
			
			writer.write(System.lineSeparator());
		}
	}

}