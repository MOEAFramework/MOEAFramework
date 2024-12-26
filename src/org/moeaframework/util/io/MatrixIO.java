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
package org.moeaframework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Static methods for reading and writing matrices.  Each row in the matrix maps to a row in the file, and each row
 * is expected to contain the same number of values.
 */
public class MatrixIO {
	
	private MatrixIO() {
		super();
	}

	/**
	 * Loads the content of the reader into a matrix.
	 * 
	 * @param reader the reader containing the matrix
	 * @return the matrix values
	 * @throws IOException if an I/O error occurred while reading the file
	 */
	public static double[][] load(Reader reader) throws IOException {
		try (MatrixReader in = new MatrixReader(reader)) {
			List<double[]> data = new ArrayList<>();
			
			while (in.hasNext()) {
				data.add(in.next());
			}
				
			return data.toArray(double[][]::new);
		}
	}
	
	/**
	 * Loads the content of the file into a matrix.
	 * 
	 * @param file the file containing the matrix
	 * @return the matrix values
	 * @throws FileNotFoundException if the file was not found
	 * @throws IOException if an I/O error occurred while reading the file
	 */
	public static double[][] load(File file) throws FileNotFoundException, IOException {
		try (FileReader reader = new FileReader(file)) {
			return load(reader);
		}
	}
	
	/**
	 * Loads a single column from the reader into a matrix.
	 * 
	 * @param reader the reader containing the matrix
	 * @param index the index of the column
	 * @return the column values
	 * @throws IOException if an I/O error occurred while reading the file
	 * @throws IndexOutOfBoundsException if the index is not a valid column
	 */
	public static double[] loadColumn(Reader reader, int index) throws IOException {
		try (MatrixReader in = new MatrixReader(reader)) {
			List<Double> data = new ArrayList<>();
			
			while (in.hasNext()) {
				data.add(in.next()[index]);
			}
			
			return data.stream().mapToDouble(Double::doubleValue).toArray();
		}
	}
	
	/**
	 * Loads a single column from the file into a matrix.
	 * 
	 * @param file the file containing the matrix
	 * @param index the index of the column
	 * @return the column values
	 * @throws FileNotFoundException if the file was not found
	 * @throws IOException if an I/O error occurred while reading the file
	 * @throws IndexOutOfBoundsException if the index is not a valid column
	 */
	public static double[] loadColumn(File file, int index) throws FileNotFoundException, IOException {
		try (FileReader reader = new FileReader(file)) {
			return loadColumn(reader, index);
		}
	}
	
	/**
	 * Saves a matrix to the writer.
	 * 
	 * @param writer the writer
	 * @param matrix a 2D array containing the matrix
	 */
	public static void save(Writer writer, double[][] matrix) {
		save(writer, Arrays.asList(matrix));
	}
	
	/**
	 * Saves a matrix to a file.
	 * 
	 * @param file the file
	 * @param matrix a 2D array containing the matrix
	 * @throws IOException if an I/O error occurred while writing the file
	 */
	public static void save(File file, double[][] matrix) throws IOException {
		save(file, Arrays.asList(matrix));
	}
	
	/**
	 * Saves a matrix to the writer.
	 * 
	 * @param writer the writer
	 * @param matrix an iterator over the rows in the matrix
	 */
	public static void save(Writer writer, Iterable<double[]> matrix) {
		try (PrintWriter out = new PrintWriter(writer)) {
			Tokenizer tokenizer = new Tokenizer();
			
			for (double[] row : matrix) {
				out.println(tokenizer.encode(DoubleStream.of(row).mapToObj(Double::toString)));
			}
		}
	}
	
	/**
	 * Saves a matrix to a file.
	 * 
	 * @param file the file
	 * @param matrix an iterator over the rows in the matrix
	 * @throws IOException if an I/O error occurred while writing the file
	 */
	public static void save(File file, Iterable<double[]> matrix) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			save(writer, matrix);
		}
	}

}
