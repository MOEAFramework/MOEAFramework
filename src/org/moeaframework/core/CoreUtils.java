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
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang3.text.StrTokenizer;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.FileUtils;

/**
 * Collection of utility methods that do not fit nicely in any other location.
 */
public class CoreUtils {

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
	 * <p>
	 * This method will likely be deprecated and removed in the future.  Use
	 * {@link EncodingUtils#getReal(Solution)} instead.
	 * 
	 * @param solution the solution whose variables are all
	 *        {@code DoubleVariable}s
	 * @return the variables as an array of doubles
	 * @throws ClassCastException if any variables are not of type
	 *         {@code RealVariable}
	 */
	public static double[] castVariablesToDoubleArray(Solution solution) {
		return EncodingUtils.getReal(solution);
	}

	/**
	 * Fills the variables of a {@link Solution} whose variables are all
	 * {@link RealVariable}s with the values in the specified array of doubles.
	 * <p>
	 * This method will likely be deprecated and removed in the future.  Use
	 * {@link EncodingUtils#setReal(Solution, double[])} instead.
	 * 
	 * @param solution the solution whose variables are all
	 *        {@code RealVariable}s
	 * @param variables the array of doubles containing the values to be stored
	 *        in the solution's variables
	 * @throws IllegalArgumentException if {@code (variables.length != 
	 *         solution.getNumberOfVariables())}
	 */
	public static void fillVariablesFromDoubleArray(Solution solution,
			double[] variables) {
		EncodingUtils.setReal(solution, variables);
	}

	/**
	 * Returns an array containing the specified solution merged with the array
	 * of solutions.
	 * 
	 * @param solution the head solution
	 * @param solutions the tail solutions
	 * @return an array containing the head solution followed by the tail
	 *         solutions
	 * @deprecated Will be removed in version 2.0; replace using
	 *             ArrayUtils#add(T[], T)
	 */
	@Deprecated
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
	 * @deprecated Will be removed in version 2.0; replace using
	 *             ArrayUtils#addAll(T[], T...)
	 */
	@Deprecated
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
	
	/**
	 * Splits an executable command into its individual arguments.  Quoted text
	 * ({@code "..."}) is treated as one argument.
	 *  
	 * @param command the command represented in a single string
	 * @return the individual arguments comprising the command
	 */
	public static String[] parseCommand(String command) {
		return new StrTokenizer(command).setQuoteChar('\"').getTokenArray();
	}

	/**
	 * Moves the file from one path to another. This method can rename a file or
	 * move it to a different directory, like the Unix {@code mv} command.
	 *
	 * @param source the source file
	 * @param destination the destination file
	 * @throws IOException if an I/O error occurs
	 * @deprecated Will be removed in version 2.0; replace with
	 *             {@link FileUtils#move(File, File)}.
	 */
	@Deprecated
	public static void move(File source, File destination) throws IOException {
		FileUtils.move(source, destination);
	}

	/**
	 * Copies all the bytes from one file to another.
	 *.
	 * @param source the source file
	 * @param destination the destination file
	 * @throws IOException if an I/O error occurred
	 * @deprecated Will be removed in version 2.0; replace with
	 *             {@link FileUtils#copy(File, File)}.
	 */
	@Deprecated
	public static void copy(File source, File destination) throws IOException {
		FileUtils.copy(source, destination);
	}

	/**
	 * Deletes a file.
	 *
	 * @param file the file to delete
	 * @throws IOException if the file could not be deleted
	 * @deprecated Will be removed in version 2.0; replace with
	 *             {@link FileUtils#delete(File)}.
	 */
	@Deprecated
	public static void delete(File file) throws IOException {
		FileUtils.delete(file);
	}
	
	/**
	 * Creates the specified directory if it does not yet exist.
	 * 
	 * @param directory the directory to create
	 * @throws IOException if the directory could not be created
	 * @deprecated Will be removed in version 2.0; replace with
	 *             {@link FileUtils#mkdir(File)}.
	 */
	@Deprecated
	public static void mkdir(File directory) throws IOException {
		FileUtils.mkdir(directory);
	}

}
