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
package org.moeaframework.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.io.input.CloseShieldReader;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CloseShieldWriter;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * A collection of solutions and common methods for manipulating the collection.
 */
public class Population implements Iterable<Solution>, Formattable<Solution>, Stateful {

	/**
	 * The internal data storage for solutions.
	 */
	private final List<Solution> data;

	/**
	 * Constructs an empty population.
	 */
	public Population() {
		super();

		data = new ArrayList<Solution>();
	}

	/**
	 * Constructs a population initialized with a collection of solutions.
	 * 
	 * @param iterable the collection of solutions for initializing this population
	 */
	public Population(Iterable<? extends Solution> iterable) {
		this();

		addAll(iterable);
	}

	/**
	 * Constructs a population initialized with an array of solutions.
	 * 
	 * @param solutions the array of solutions for initializing this population
	 * @param <T> the type of solutions in the array
	 */
	public <T extends Solution> Population(T[] solutions) {
		this(Arrays.asList(solutions));
	}

	/**
	 * Returns the solution at the specified index in this population.
	 * 
	 * @param index the index of the solution to be returned
	 * @return the solution at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0) || (index >= size())}
	 */
	public Solution get(int index) {
		return data.get(index);
	}

	/**
	 * Removes the solution at the specified index from this population.
	 * 
	 * @param index the index of the solution to be removed
	 * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0) || (index >= size())}
	 */
	public void remove(int index) {
		modCount++;
		data.remove(index);
	}

	/**
	 * Returns the index of the specified solution in this population.  Invocations of certain methods on this
	 * population may alter the ordering of solutions, so the index returned should be used immediately by the
	 * {@code get} or {@code remove} methods.
	 * 
	 * @param solution the solution whose index is to be returned
	 * @return the index of the specified solution
	 */
	public int indexOf(Solution solution) {
		return data.indexOf(solution);
	}

	/**
	 * Adds the specified solution to this population.
	 * 
	 * @param solution the solution to be added
	 * @return {@code true} if the population was modified as a result of this method; {@code false} otherwise.
	 */
	public boolean add(Solution solution) {
		modCount++;
		return data.add(solution);
	}

	/**
	 * Adds a collection of solutions to this population.
	 * 
	 * @param iterable the collection of solutions to be added
	 * @return {@code true} if the population was modified as a result of this method; {@code false} otherwise
	 */
	public boolean addAll(Iterable<? extends Solution> iterable) {
		boolean changed = false;

		for (Solution solution : iterable) {
			changed |= add(solution);
		}

		return changed;
	}

	/**
	 * Adds an array of solutions to this population.
	 * 
	 * @param solutions the solutions to be added
	 * @param <T> the type of solutions in the array
	 * @return {@code true} if the population was modified as a result of this method; {@code false} otherwise
	 */
	public <T extends Solution> boolean addAll(T[] solutions) {
		return addAll(Arrays.asList(solutions));
	}
	
	/**
	 * Replaces the solution at the given index.
	 * 
	 * @param index the index to replace
	 * @param solution the new solution
	 */
	public void replace(int index, Solution solution) {
		data.set(index, solution);
	}

	/**
	 * Removes all solutions from this population.
	 */
	public void clear() {
		modCount++;
		data.clear();
	}

	/**
	 * Returns {@code true} if this population contains the specified solution; {@code false} otherwise.
	 * 
	 * @param solution the solution whose presence is tested
	 * @return {@code true} if this population contains the specified solution; {@code false} otherwise
	 */
	public boolean contains(Solution solution) {
		return data.contains(solution);
	}

	/**
	 * Returns {@code true} if this population contains all the solutions in the specified collection; {@code false}
	 * otherwise.
	 * 
	 * @param iterable the collection whose presence is tested
	 * @return {@code true} if this population contains all the solutions in the specified collection; {@code false}
	 *         otherwise
	 */
	public boolean containsAll(Iterable<? extends Solution> iterable) {
		boolean missing = false;

		for (Solution solution : iterable) {
			missing |= !contains(solution);
		}

		return !missing;
	}

	/**
	 * Returns {@code true} if this population contains all the solutions in the specified array; {@code false}
	 * otherwise.
	 * 
	 * @param solutions the array whose presence is tested
	 * @param <T> the type of solutions in the array
	 * @return {@code true} if this population contains all the solutions in the specified array; {@code false}
	 *         otherwise
	 */
	public <T extends Solution> boolean containsAll(T[] solutions) {
		return containsAll(Arrays.asList(solutions));
	}

	/**
	 * Returns {@code true} if this population contains no solutions; {@code false} otherwise.
	 * 
	 * @return {@code true} if this population contains no solutions; {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Returns an iterator for accessing the solutions in this population.
	 */
	@Override
	public Iterator<Solution> iterator() {
		return new PopulationIterator();
	}

	/**
	 * Removes the specified solution from this population, if present.
	 * 
	 * @param solution the solution to be removed
	 * @return {@code true} if this population was modified as a result of this method; {@code false} otherwise
	 */
	public boolean remove(Solution solution) {
		modCount++;
		return data.remove(solution);
	}

	/**
	 * Removes all solutions in the specified collection from this population.
	 * 
	 * @param iterable the collection of solutions to be removed
	 * @return {@code true} if this population was modified as a result of this method; {@code false} otherwise
	 */
	public boolean removeAll(Iterable<? extends Solution> iterable) {
		boolean changed = false;

		for (Solution solution : iterable) {
			changed |= remove(solution);
		}

		return changed;
	}

	/**
	 * Removes all solutions in the specified array from this population.
	 * 
	 * @param solutions the array of solutions to be removed
	 * @param <T> the type of solutions in the array
	 * @return {@code true} if this population was modified as a result of this method; {@code false} otherwise
	 */
	public <T extends Solution> boolean removeAll(T[] solutions) {
		return removeAll(Arrays.asList(solutions));
	}

	/**
	 * Returns the number of solutions in this population.
	 * 
	 * @return the number of solutions in this population
	 */
	public int size() {
		return data.size();
	}

	/**
	 * Sorts the solutions in this population using the specified comparator.  Invocations of certain methods on this
	 * population may alter the ordering of solutions, so the {@code get}, {@code remove} and iteration methods
	 * should be called immediately after invoking this method.
	 * 
	 * @param comparator the comparator to be used for sorting
	 */
	public void sort(Comparator<? super Solution> comparator) {
		modCount++;
		Collections.sort(data, comparator);
	}
	
	/**
	 * Applies a filter to this population, removing any solutions that do not match the predicate.
	 * 
	 * @param predicate the filter that returns {@code true} on solutions to keep
	 */
	public void filter(Predicate<Solution> predicate) {
		Iterator<Solution> iterator = iterator();
		
		while (iterator.hasNext()) {
			Solution solution = iterator.next();
			
			if (!predicate.test(solution)) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Computes the lower bounds of this population.  Since all objectives are minimized, this is equivalent to the
	 * ideal point.  Also note that all solutions, including those violating constraints, are included.
	 * 
	 * @return the lower bounds
	 */
	public double[] getLowerBounds() {
		double[] lower = null;
		
		for (Solution solution : this) {
			if (lower == null) {
				lower = solution.getObjectives(); // the returned array is a copy
			} else {
				for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
					lower[i] = Math.min(lower[i], solution.getObjective(i));
				}
			}
		}
		
		if (lower == null) {
			throw new UnsupportedOperationException("population must contain at least one solution to compute bounds");
		}
		
		return lower;
	}
	
	/**
	 * Computes the upper bounds of this population.  Since all objectives are minimized, this is equivalent to the
	 * Nadir point.  Also note that all solutions, including those violating constraints, are included.
	 * 
	 * @return the upper bounds
	 */
	public double[] getUpperBounds() {
		double[] upper = null;
		
		for (Solution solution : this) {
			if (upper == null) {
				upper = solution.getObjectives(); // the returned array is a copy
			} else {
				for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
					upper[i] = Math.max(upper[i], solution.getObjective(i));
				}
			}
		}
		
		if (upper == null) {
			throw new UnsupportedOperationException("population must contain at least one solution to compute bounds");
		}
		
		return upper;
	}

	/**
	 * Sorts this population using the specified comparator and removes the last (maximum) solutions until this
	 * population's size is within the specified size.
	 * 
	 * @param size the target population size after truncation
	 * @param comparator the comparator to be used for truncation
	 */
	public void truncate(int size, Comparator<? super Solution> comparator) {
		sort(comparator);

		while (data.size() > size) {
			data.remove(data.size() - 1);
		}
	}
	
	/**
	 * Returns a copy of this population.  This can be thought of as a "deep copy", which creates a copy of both the
	 * population itself and copies of the individual solutions in the population.  Consequently, the returned
	 * copy is completely independent, such that any modifications to the contents or order will not impact the
	 * original.
	 * <p>
	 * Since creating such a "deep copy" can be expensive, prefer using the constructor {@link #Population(Iterable)}
	 * or {@link #addAll(Iterable)} whenever possible.  These alternatives are useful when filtering or reordering the
	 * solutions, but the solutions themselves are left unchanged.
	 * 
	 * @return the copy of this population
	 */
	public Population copy() {
		Population result = new Population();
		
		for (Solution solution : this) {
			result.add(solution.copy());
		}
		
		return result;
	}
	
	/**
	 * Returns the contents of this population as a list.  Consider using {@link #copy()} first if planning to modify
	 * the solutions in any way.
	 * 
	 * @return the contents of this population as a list
	 */
	public List<Solution> asList() {
		List<Solution> result = new ArrayList<Solution>(size());
		
		for (Solution solution : this) {
			result.add(solution);
		}
		
		return result;
	}
	
	@Override
	public TabularData<Solution> asTabularData() {
		TabularData<Solution> data = new TabularData<Solution>(this);
		
		if (!isEmpty()) {	
			Solution solution = get(0);
			
			for (int i = 0; i < solution.getNumberOfVariables(); i++) {
				final int index = i;
				data.addColumn(new Column<Solution, Variable>("Var" + (index+1), s -> s.getVariable(index)));
			}
			
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				final int index = i;
				data.addColumn(new Column<Solution, Double>("Obj" + (index+1), s -> s.getObjective(index)));
			}
			
			for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
				final int index = i;
				data.addColumn(new Column<Solution, Double>("Constr" + (index+1), s -> s.getConstraint(index)));
			}
		}
		
		return data;
	}
	
	/**
	 * Saves the objective vectors of all solutions to the specified file.  Files created using this method should
	 * only be loaded using the {@link #loadObjectives(File)} method.
	 * 
	 * @param file the file to which the objective vectors are written
	 * @throws IOException if an I/O exception occurred
	 */
	public void saveObjectives(File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			saveObjectives(writer);
		}
	}
	
	/**
	 * Saves the objective vectors of all solutions to the writer.  Files created using this method should only be
	 * loaded using the {@link #loadObjectives(File)} method.  <b>This method does not close the writer!</b>
	 * 
	 * @param writer the writer to which the objective vectors are written
	 * @throws IOException if an I/O exception occurred
	 */
	public void saveObjectives(Writer writer) throws IOException {
		try (BufferedWriter output = new BufferedWriter(CloseShieldWriter.wrap(writer))) {
			for (Solution solution : this) {
				for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
					if (i > 0) {
						output.write(" ");
					}
					
					output.write(Double.toString(solution.getObjective(i)));
				}

				output.newLine();
			}
		}
	}
	
	/**
	 * Loads a set of objective vectors from the specified file.  Files read using this method should only have been
	 * created using the {@link #saveObjectives(File)} method.
	 * 
	 * @param file the file containing the objective vectors
	 * @return a population containing all objective vectors in the specified file
	 * @throws IOException if an I/O exception occurred
	 */
	public static Population loadObjectives(File file) throws IOException {
		try (FileReader reader = new FileReader(file)) {
			return loadObjectives(reader);
		}
	}
	
	/**
	 * Loads the objective vectors contained in the specified reader, returning the resulting population.  <b>This
	 * method does not close the reader!</b>
	 * 
	 * @param reader the reader containing the objective vectors
	 * @return a population containing all objective vectors read
	 * @throws IOException if an I/O error occurred
	 */
	public static Population loadObjectives(Reader reader) throws IOException {
		try (CommentedLineReader input = CommentedLineReader.wrap(CloseShieldReader.wrap(reader))) {
			Population population = new Population();
			String line = null;
			
			while ((line = input.readLine()) != null) {
				String[] tokens = line.trim().split("\\s+");
				Solution solution = new Solution(0, tokens.length);
	
				for (int i = 0; i < tokens.length; i++) {
					solution.setObjective(i, Double.parseDouble(tokens[i]));
				}
	
				population.add(solution);
			}
			
			return population;
		}
	}
	
	/**
	 * Saves this population to a file using a binary format.  Unlike the other save and load methods, the binary
	 * format stores a complete copy of the solutions, including all attributes.  Files written using this method can
	 * only be read using the {@link #loadBinary} method.
	 * 
	 * @param file the file to which the solutions are written
	 * @throws IOException if an I/O exception occurred
	 */
	public void saveBinary(File file) throws IOException {
		try (FileOutputStream output = new FileOutputStream(file)) {
			saveBinary(output);
		}
	}
	
	/**
	 * Saves this population to an output stream using a binary format.  Unlike the other save and load methods, the
	 * binary format stores a complete copy of the solutions, including all attributes.  Files written using this
	 * method can only be read using the {@link #loadBinary} method.  <b>This method does not close the output
	 * stream!</b>
	 * 
	 * @param outputStream the outputStream to which the solutions are written
	 * @throws IOException if an I/O exception occurred
	 */
	public void saveBinary(OutputStream outputStream) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				CloseShieldOutputStream.wrap(outputStream)))) {
			saveState(oos);
		}
	}
	
	/**
	 * Loads a population from the specified file.  Files read using this method should only have been created using
	 * the {@link #saveBinary} method.
	 * 
	 * @param file the file containing the population
	 * @return a population containing all solutions in the specified file
	 * @throws IOException if an I/O exception occurred
	 */
	public static Population loadBinary(File file) throws IOException {
		try (FileInputStream input = new FileInputStream(file)) {
			return loadBinary(input);
		}
	}
	
	/**
	 * Loads a population from the specified input stream.  Files read using this method should only have been created
	 * using the {@link #saveBinary} method.  <b>This method does not close the input stream!</b>
	 * 
	 * @param inputStream the input stream containing the population
	 * @return a population containing all solutions in the specified file
	 * @throws IOException if an I/O exception occurred
	 */
	public static Population loadBinary(InputStream inputStream) throws IOException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				CloseShieldInputStream.wrap(inputStream)))) {
			Population population = new Population();
			population.loadState(ois);
			return population;
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	/*
	 * The following code is based on the Apache Commons Collections library.  This is to provide a similar iterator
	 * behavior to other collection classes without requiring the Population to implement all collection methods.  The
	 * license terms are provided below.
	 * 
	 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the
	 * NOTICE file distributed with this work for additional information regarding copyright ownership.  The ASF
	 * licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file
	 * except in compliance with the License.  You may obtain a copy of the License at
	 *
	 *     http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
	 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License
	 * for the specific language governing permissions and limitations under the License.
	 */
	
	/**
	 * The modification count.
	 */
	int modCount;

	/**
	 * An iterator over the solutions in a population.
	 */
	private class PopulationIterator implements Iterator<Solution> {

		/**
		 * The index of the next node to be returned.
		 */
		private int nextIndex;

		/**
		 * The index of the last node that was returned.  Set to {@code -1} if the iterator is not positioned at a
		 * valid node (i.e., at initialization or after an element is removed).
		 */
		private int currentIndex;

		/**
         * The modification count that the list is expected to have. If the list doesn't have this count, then a
         * {@link java.util.ConcurrentModificationException} may be thrown by the operations.
         */
		private int expectedModCount;
		
		/**
		 * Constructs a population iterator.
		 */
		public PopulationIterator() {
			super();
			
			nextIndex = 0;
			currentIndex = -1;
			expectedModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			return nextIndex != size();
		}

		@Override
		public Solution next() {
			checkModCount();
			
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			
			try {
				Solution value = get(nextIndex);
				currentIndex = nextIndex++;
				return value;
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void remove() {
			checkModCount();
			
			if (currentIndex == -1) {
				throw new IllegalStateException();
			}

			try {
				Population.this.remove(currentIndex);
				nextIndex--;
				currentIndex = -1;
				expectedModCount++;
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		/**
         * Checks the modification count of the list is the value that this object expects.
         * 
         * @throws ConcurrentModificationException if the list's modification count is not the value that was expected
         */
		private void checkModCount() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		stream.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		data.clear();
		data.addAll((List<Solution>)stream.readObject());
	}

}
