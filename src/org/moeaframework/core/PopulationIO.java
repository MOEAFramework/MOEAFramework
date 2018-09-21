/* Copyright 2009-2018 David Hadka
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.util.io.CommentedLineReader;

/**
 * Collection of static methods for reading and writing populations to files.
 * The {@link #write} and {@link #read} methods should be used when storing and
 * retrieving solutions to be used by this framework. The
 * {@link #writeObjectives} and {@link #readObjectives} should be used when the
 * data is accessed by external programs, as these two methods store the data in
 * a human-readable form.
 */
public class PopulationIO {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private PopulationIO() {
		super();
	}
	
	/**
	 * Parses the objective vectors contained in the specified reader, returning
	 * the resulting population.  This method does not close the reader.
	 * 
	 * @param reader the reader containing the objective vectors
	 * @return a population containing all objective vectors read
	 * @throws IOException if an I/O error occurred
	 */
	public static Population readObjectives(BufferedReader reader) throws
	IOException {
		Population population = new Population();
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.trim().split("\\s+");
			double[] values = new double[tokens.length];

			for (int i = 0; i < tokens.length; i++) {
				values[i] = Double.parseDouble(tokens[i]);
			}

			population.add(new Solution(values));
		}
		
		return population;
	}

	/**
	 * Reads a set of objective vectors from the specified file. Files read
	 * using this method should only have been created using the
	 * {@code writeObjectives} method.
	 * 
	 * @param file the file containing the objective vectors
	 * @return a population containing all objective vectors in the specified
	 *         file
	 * @throws IOException if an I/O exception occurred
	 */
	public static Population readObjectives(File file) throws IOException {
		BufferedReader reader = null;

		try {
			reader = new CommentedLineReader(new FileReader(file));

			return readObjectives(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Writes the objective vectors of all solutions to the specified file. 
	 * Files created using this method should only be loaded using the 
	 * {@code loadObjectives} method.
	 * 
	 * @param file the file to which the objective vectors are written
	 * @param solutions the solutions whose objective vectors are written to
	 *        the specified file
	 * @throws IOException if an I/O exception occurred
	 */
	public static void writeObjectives(File file, Iterable<Solution> solutions)
			throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));

			for (Solution solution : solutions) {
				writer.write(Double.toString(solution.getObjective(0)));

				for (int i = 1; i < solution.getNumberOfObjectives(); i++) {
					writer.write(" ");
					writer.write(Double.toString(solution.getObjective(i)));
				}

				writer.newLine();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Writes a collection of solutions to the specified file. Files written 
	 * using this method should only be read using the {@link #read} method. 
	 * This method relies on serialization.
	 * 
	 * @param file the file to which the solutions are written
	 * @param solutions the solutions to be written in the specified file
	 * @throws IOException if an I/O exception occurred
	 */
	public static void write(File file, Iterable<Solution> solutions)
			throws IOException {
		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(file)));
			List<Solution> list = new ArrayList<Solution>();

			for (Solution solution : solutions) {
				list.add(solution);
			}

			oos.writeObject(list);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
	}

	/**
	 * Reads a population from the specified file. Files read using this method
	 * should only have been created using the {@link #write} method. This
	 * method relies on serialization.
	 * 
	 * @param file the file containing the population
	 * @return a population containing all solutions in the specified file
	 * @throws IOException if an I/O exception occurred
	 */
	public static Population read(File file) throws IOException {
		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(file)));
			Population population = new Population();

			for (Object solution : (List<?>)ois.readObject()) {
				population.add((Solution)solution);
			}

			return population;
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}

}
