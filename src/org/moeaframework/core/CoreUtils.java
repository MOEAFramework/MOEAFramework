/* Copyright 2009-2012 David Hadka
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.moeaframework.core.variable.RealVariable;

/**
 * Collection of utility methods that do not fit nicely in any other location.
 */
public class CoreUtils {
	
	/**
	 * Error message when unable to delete a file.
	 */
	private static final String UNABLE_TO_DELETE = "unable to delete {0}";
	
	/**
	 * Error message when unable to create a directory.
	 */
	private static final String UNABLE_TO_MKDIR = "unable to mkdir {0}";

	/**
	 * Private constructor to prevent instantiation.
	 */
	private CoreUtils() {
		super();
	}
	
	/**
	 * Throws an {@link IllegalArgumentException} if the specified argument is
	 * {@code null}.
	 * 
	 * @param name the argument name
	 * @param argument the object which should not be {@code null}
	 * @throws IllegalArgumentException if the argument is {@code null}
	 * @deprecated Will be removed in version 2.0; use Validate class instead
	 */
	@Deprecated
	public static void assertNotNull(String name, Object argument) {
		if (argument == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"{0} is null", name));
		}
	}

	/**
	 * Returns the variables as an array of doubles of a {@link Solution} whose
	 * variables are all {@link RealVariable}s.
	 * 
	 * @param solution the solution whose variables are all
	 *        {@code DoubleVariable}s
	 * @return the variables as an array of doubles
	 * @throws ClassCastException if any variables are not of type
	 *         {@code RealVariable}
	 */
	public static double[] castVariablesToDoubleArray(Solution solution) {
		double[] variables = new double[solution.getNumberOfVariables()];

		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			variables[i] = ((RealVariable)solution.getVariable(i)).getValue();
		}

		return variables;
	}

	/**
	 * Fills the variables of a {@link Solution} whose variables are all
	 * {@link RealVariable}s with the values in the specified array of doubles.
	 * 
	 * @param solution the solution whose variables are all
	 *        {@code RealVariable}s
	 * @param variables the array of doubles containing the values to be stored
	 *        in the solution's variables
	 */
	public static void fillVariablesFromDoubleArray(Solution solution,
			double[] variables) {
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			((RealVariable)solution.getVariable(i)).setValue(variables[i]);
		}
	}

	/**
	 * Returns an array containing the specified solution merged with the array
	 * of solutions.
	 * 
	 * @param solution the head solution
	 * @param solutions the tail solutions
	 * @return an array containing the head solution followed by the tail
	 *         solutions
	 */
	public static Solution[] merge(Solution solution, Solution[] solutions) {
		Solution[] result = new Solution[solutions.length + 1];

		result[0] = solution;

		for (int i = 0; i < solutions.length; i++) {
			result[i + 1] = solutions[i];
		}

		return result;
	}

	/**
	 * Returns an array containing the merger of the two specified arrays.
	 * 
	 * @param s1 the head solutions
	 * @param s2 the tail solutions
	 * @return an array containing the merger of the two specified arrays
	 */
	public static Solution[] merge(Solution[] s1, Solution[] s2) {
		Solution[] result = new Solution[s1.length + s2.length];

		for (int i = 0; i < s1.length; i++) {
			result[i] = s1[i];
		}

		for (int i = 0; i < s2.length; i++) {
			result[i + s1.length] = s2[i];
		}

		return result;
	}
	
	/*
	 * The following code is based on the Files and ByteStream classes by
	 * Chris Nokleberg.  The code is replicated here since both classes are 
	 * tagged with the @Beta annotation, indicating its API is subject to 
	 * incompatible changes in future releases.
	 * 
	 * Copyright (C) 2007 The Guava Authors
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */

	/**
	 * Moves the file from one path to another. This method can rename a file or
	 * move it to a different directory, like the Unix {@code mv} command.
	 *
	 * @param from the source file
	 * @param to the destination file
	 * @throws IOException if an I/O error occurs
	 */
	public static void move(File from, File to) throws IOException {
		if (from == null) {
			throw new IllegalArgumentException("source is null");
		}
		
		if (to == null) {
			throw new IllegalArgumentException("destination is null");
		}
		
		if (from.equals(to)) {
			return;
		}

		if (!from.renameTo(to)) {
			copy(from, to);
			
			if (!from.delete()) {
				if (!to.delete()) {
					throw new IOException(MessageFormat.format(UNABLE_TO_DELETE,
							to));
				}
				
				throw new IOException(MessageFormat.format(UNABLE_TO_DELETE, 
						from));
			}
		}
	}

	/**
	 * Copies all the bytes from one file to another.
	 *.
	 * @param from the source file
	 * @param to the destination file
	 * @throws IOException if an I/O error occurred
	 */
	public static void copy(File from, File to) throws IOException {
		if (from == null) {
			throw new IllegalArgumentException("source is null");
		}
		
		if (to == null) {
			throw new IllegalArgumentException("destination is null");
		}
		
		if (from.equals(to)) {
			return;
		}
		
		InputStream input = null;
		OutputStream output = null;

		try {
			input = new FileInputStream(from);

			try {
				output = new FileOutputStream(to);
				
				copy(input, output);
			} finally {
				if (output != null) {
					output.close();
				}
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	/**
	 * Copies all bytes from the input stream to the output stream.
	 * Does not close or flush either stream.
	 *
	 * @param from the input stream to read from
	 * @param to the output stream to write to
	 * @return the number of bytes copied
	 * @throws IOException if an I/O error occurred
	 */
	private static long copy(InputStream from, OutputStream to)
	throws IOException {
		byte[] buf = new byte[Settings.BUFFER_SIZE];
		long total = 0;

		while (true) {
			int r = from.read(buf);

			if (r == -1) {
				break;
			}

			to.write(buf, 0, r);
			total += r;
		}

		return total;
	}

	/**
	 * Deletes a file.
	 *
	 * @param file the file to delete
	 * @throws IOException if the file could not be deleted
	 */
	public static void delete(File file) throws IOException {
		if (file.exists()) {
			if (!file.delete()) {
				throw new IOException(MessageFormat.format(UNABLE_TO_DELETE, 
						file));
			}
		}
	}
	
	/**
	 * Creates the specified directory if it does not yet exist.
	 * 
	 * @param directory the directory to create
	 * @throws IOException if the directory could not be created
	 */
	public static void mkdir(File directory) throws IOException {
		if (directory.exists()) {
			if (!directory.isDirectory()) {
				throw new IOException(MessageFormat.format(UNABLE_TO_MKDIR,
						directory));
			}
		} else {
			if (!directory.mkdirs()) {
				throw new IOException(MessageFormat.format(UNABLE_TO_MKDIR,
						directory));
			}
		}
	}

}
