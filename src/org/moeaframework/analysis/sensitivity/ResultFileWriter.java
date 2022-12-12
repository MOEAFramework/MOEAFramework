/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.analysis.sensitivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.CommentedLineReader;
import org.moeaframework.util.io.FileUtils;

/**
 * Writes result files. A result file contains one or more entries consisting
 * of a non-dominated population and optional properties. Entries are separated
 * by one or more consecutive lines starting with the {@code #} character. Text
 * contained on these lines after the {@code #} character are ignored.
 * <p>
 * An entry contains two pieces of data: 1) properties which are defined on
 * lines starting with {@code //} in the same format as {@link TypedProperties}; and
 * 2) lines containing a sequence of floating-point numbers listing, in order,
 * the real-valued decision variables and objectives. Each decision variable is
 * separated by one or more whitespace characters. Decision variables that can 
 * not be encoded appear as {@code -}. The writer will attempt to output the 
 * data in a human-readable format, but falls back on Base64 encoded serialized
 * objects to store serializable variables.
 * <p>
 * Complete entries are always terminated by a line starting with the {@code #}
 * character. Incomplete entries, such as those with the incorrect number of
 * decision variables or objectives, are automatically removed.
 * <p>
 * This writer will append the results to the file, if a previous file exists.
 * By reading the previous file with a {@link ResultFileReader}, this writer will
 * being appending after the last valid entry. Query the
 * {@link #getNumberOfEntries()} method to determine how many valid entries are
 * contained in the file.
 * 
 * @see ResultFileReader
 */
public class ResultFileWriter implements OutputWriter {
	
    /**
     * The message displayed when an unsupported decision variable type is
     * encountered.
     */
	protected static final String ENCODING_WARNING = 
			"unsupported decision variable type, may become unstable";
	
	/**
	 * The message displayed when excluding the decision variables when saving
	 * a result file.
	 */
	protected static final String NO_VARIABLES_WARNING =
			"saving result file without variables, may become unstable";
	
	/**
	 * The message displayed when an unclean file exists from a previous run.
	 */
	protected static final String EXISTING_FILE =
			"an unclean version of the file exists from a previous run, " +
			"requires manual intervention";

	/**
	 * The stream for appending data to the file.
	 */
	private final PrintWriter writer;
	
	/**
	 * {@code true} if this writer should save the decision variables;
	 * {@code false} otherwise.
	 */
	private final boolean includeVariables;

	/**
	 * The number of lines in the file.
	 */
	private int numberOfEntries;
	
	/**
	 * {@code true} if the warning for unsupported decision variables was
	 * displayed; {@code false} otherwise.
	 */
	private boolean printedWarning;
	
	/**
	 * Equivalent to {@code ResultWriter(problem, file, true)}.
	 * 
	 * @param problem the problem
	 * @param file the file to which the results are stored
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileWriter(Problem problem, File file) throws IOException {
		this(problem, file, true);
	}

	/**
	 * Constructs an output writer for writing the decision variables and
	 * objectives of a sequence of non-dominated populations to a file. If the 
	 * file already exists, any valid entries are retained and {@code 
	 * getNumberOfEntries()} returns the number of valid entries. This allows
	 * resuming evaluation at the last valid result.
	 * <p>
	 * It is recommended to avoid setting {@code includeVariables} to {@code
	 * false}.  Any computations requiring decision variables may result in
	 * unexpected and hard to trace errors.
	 * 
	 * @param problem the problem
	 * @param file the file to which the results are stored
	 * @param includeVariables {@code true} if this writer should save the 
	 *        decision variables; {@code false} otherwise.
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileWriter(Problem problem, File file, 
			boolean includeVariables) throws IOException {
		super();
		this.includeVariables = includeVariables;
		
		if (!includeVariables) {
			System.err.println(NO_VARIABLES_WARNING);
		}

		// if the file already exists, move it to a temporary location
		File existingFile = new File(file.getParent(), "." + file.getName()
				+ ".unclean");
		
		if (existingFile.exists()) {
			if (Settings.getCleanupStrategy().equalsIgnoreCase("restore")) {
				if (file.exists()) {
					FileUtils.delete(existingFile);
				} else {
					// do nothing, the unclean file is ready for recovery
				}
			} else if (Settings.getCleanupStrategy().equalsIgnoreCase("overwrite")) {
				FileUtils.delete(existingFile);
			} else {
				throw new FrameworkException(EXISTING_FILE);
			}
		}
		
		if (file.exists()) {
			FileUtils.move(file, existingFile);
		}

		// prepare this class for writing
		numberOfEntries = 0;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), 
				true);
		
		// print header information
		writer.print("# Problem = ");
		writer.println(problem.getName());
		
		if (includeVariables) {
			writer.print("# Variables = ");
			writer.println(problem.getNumberOfVariables());
		}
		
		writer.print("# Objectives = ");
		writer.println(problem.getNumberOfObjectives());

		// if the file already existed, copy all complete entries
		if (existingFile.exists()) {
			try (ResultFileReader reader = new ResultFileReader(problem, existingFile)) {
				while (reader.hasNext()) {
					append(reader.next());
				}
			}

			FileUtils.delete(existingFile);
		}
	}

	/**
	 * Returns the number of entries written to the result file. Querying this
	 * method immediately after the constructor in which the result file already
	 * existed returns the number of valid entries contained in the result
	 * file.
	 * 
	 * @return the number of entries written to the result file
	 */
	public int getNumberOfEntries() {
		return numberOfEntries;
	}

	/**
	 * Appends the decision variables, objectives and optional properties to
	 * the output file.  Constraint violating solutions are not recorded.
	 * 
	 * @param entry the entry to write
	 * @throws IOException if an I/O error occurred
	 */
	public void append(ResultEntry entry) throws IOException {		
		numberOfEntries++;
		
		//generate list of all feasible solutions
		List<Solution> feasibleSolutions = new ArrayList<Solution>();
		
		for (Solution solution : entry.getPopulation()) {
			if (!solution.violatesConstraints()) {
				feasibleSolutions.add(solution);
			}
		}
		
		//ensure a non-empty entry is written
		TypedProperties properties = entry.getProperties();
		
		if (feasibleSolutions.isEmpty() && 
				((properties == null) || (properties.isEmpty()))) {
			writer.println("//");
		}

		//write entry
		if ((properties != null) && !properties.isEmpty()) {
			printProperties(properties);
		}
		
		if (!feasibleSolutions.isEmpty()) {
			for (Solution solution : feasibleSolutions) {
				printSolution(solution);
			}
		}

		writer.println('#');
	}
	
	/**
	 * Prints the solution to the result file.
	 * 
	 * @param solution the solution
	 */
	private void printSolution(Solution solution) {
		if (includeVariables) {
			// write decision variables
			for (int i = 0; i < solution.getNumberOfVariables(); i++) {
				if (i > 0) {
					writer.print(' ');
				}

				writer.print(encode(solution.getVariable(i)));
			}
		}

		// write objectives
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			if ((i > 0) || (includeVariables && 
					(solution.getNumberOfVariables() > 0))) {
				writer.print(' ');
			}

			writer.print(solution.getObjective(i));
		}

		writer.println();
	}
	
	/**
	 * Prints the properties to the result file.  This uses a roundabout way to
	 * store properties, but using Java's underlying encoding mechanism ensures
	 * Unicode and special characters are escaped correctly.
	 * 
	 * @param properties the properties
	 * @throws IOException if an I/O error occurred
	 */
	private void printProperties(TypedProperties properties) throws IOException {
		try (StringWriter stringBuffer = new StringWriter()) {
			properties.store(stringBuffer);
		
			try (CommentedLineReader reader = new CommentedLineReader(new StringReader(stringBuffer.toString()))) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					writer.print("//");
					writer.println(line);
				}
			}
		} 
	}

	@Override
	public void close() {
		writer.close();
	}
	
	/**
	 * Encodes the decision variable into a string representation that can be
	 * safely written to a result file.  The resulting strings must not contain
	 * any whitespace characters.  For decision variables that do not support
	 * a valid encoding, the string {@code "-"} will be returned and a warning
	 * message printed.
	 * 
	 * @param variable the decision variable to encode
	 * @return the string representation of the decision variable
	 */
	public String encode(Variable variable) {
		try {
			return variable.encode();
		} catch (Exception e) {				
			if (!printedWarning) {
				System.err.println(ENCODING_WARNING);
				printedWarning = true;
			}
			
			return "-";
		}
	}
	
}
