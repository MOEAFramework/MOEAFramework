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
package org.moeaframework.analysis.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Constructable;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.ErrorHandler;
import org.moeaframework.util.NumericStringComparator;
import org.moeaframework.util.io.LineReader;

/**
 * Writes result files.  A result file contains one or more entries consisting of a non-dominated population and
 * optional properties.  Entries are separated by one or more consecutive lines starting with the {@code #} character.
 * Text contained on these lines after the {@code "#"} character are ignored.
 * <p>
 * Each entry consists of properties, on lines starting with {@code "//"}, and solutions, on all remaining lines.
 * Properties are stored using the same format as {@link TypedProperties#save(Writer)}.  The decision variables,
 * objectives, and constraints for each solution are separated by whitespace.  Each decision variable is encoded using
 * {@link Variable#encode()}.  In the event an error occurs while encoding a decision variable, {@code "-"} is written
 * and a warning issued.  
 * <p>
 * Complete entries are always terminated by a line starting with the {@code #} character.
 * 
 * @see ResultFileReader
 */
public class ResultFileWriter extends ResultWriter {
	
	/**
	 * The message displayed when an unsupported decision variable type is encountered.
	 */
	static final String ENCODING_WARNING = 
			"unsupported decision variable type, could cause unexpected behavior or data loss";
	
	/**
	 * The stream for appending data to the file.
	 */
	private final PrintWriter writer;

	/**
	 * The number of lines in the file.
	 */
	private int numberOfEntries;
	
	/**
	 * The error handler.
	 */
	private final ErrorHandler errorHandler;
	
	/**
	 * If set, decision variables are not written.  
	 */
	private boolean excludeVariables;
	
	/**
	 * Constructs a result file writer for writing the decision variables and objectives of a sequence of
	 * non-dominated populations to a file.
	 * 
	 * @param problem the problem
	 * @param file the file to which the results are stored
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileWriter(Problem problem, File file) throws IOException {
		this(problem, new BufferedWriter(new FileWriter(file)));
	}
	
	/**
	 * Constructs a result file writer for writing the decision variables and objectives of a sequence of
	 * non-dominated populations to a writer.
	 * 
	 * @param problem the problem
	 * @param writer the output writer
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileWriter(Problem problem, Writer writer) throws IOException {
		super();
		this.writer = new PrintWriter(writer);
		
		errorHandler = new ErrorHandler();
		errorHandler.setSuppressDuplicates(true);

		numberOfEntries = 0;
		
		printHeader(problem);
	}
	
	/**
	 * If set, excludes decision variables from the output.  In general, excluding variables is not recommended as it
	 * is then impossible to recover the original solution.
	 * 
	 * @param excludeVariables {@code true} to exclude decision variables; {@code false} otherwise
	 */
	protected void setExcludeVariables(boolean excludeVariables) {
		this.excludeVariables = excludeVariables;
	}
	
	/**
	 * Writes the header section to the output.
	 * 
	 * @param problem the problem
	 * @throws IOException if an I/O error occurred
	 */
	protected void printHeader(Problem problem) throws IOException {
		// print header information
		TypedProperties header = new TypedProperties();
		header.setInt("Version", Settings.getMajorVersion());
		
		if (problem != null) {
			header.setString("Problem", problem.getName());
			header.setInt("NumberOfVariables", problem.getNumberOfVariables());
			header.setInt("NumberOfObjectives", problem.getNumberOfObjectives());
			header.setInt("NumberOfConstraints", problem.getNumberOfConstraints());
	
			Solution prototype = problem.newSolution();
	
			for (int i = 0; i < problem.getNumberOfVariables(); i++) {
				try {
					header.setString("Variable." + (i+1) + ".Definition",
							prototype.getVariable(i).getDefinition());
				} catch (UnsupportedOperationException e) {
					header.setString("Variable." + (i+1) + ".Definition",
							Constructable.createUnsupportedDefinition(Variable.class,
									prototype.getVariable(i).getClass()));
				}
			}
	
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				try {
					header.setString("Objective." + (i+1) + ".Definition",
							prototype.getObjective(i).getDefinition());
				} catch (UnsupportedOperationException e) {
					header.setString("Objective." + (i+1) + ".Definition",
							Constructable.createUnsupportedDefinition(Objective.class,
									prototype.getObjective(i).getClass()));
				}
			}
	
			for (int i = 0; i < problem.getNumberOfConstraints(); i++) {
				try {
					header.setString("Constraint." + (i+1) + ".Definition",
							prototype.getConstraint(i).getDefinition());
				} catch (UnsupportedOperationException e) {
					header.setString("Constraint." + (i+1) + ".Definition",
							Constructable.createUnsupportedDefinition(Constraint.class,
									prototype.getConstraint(i).getClass()));
				}
			}
		}

		printProperties(header, "# ");
	}
	
	/**
	 * Returns the error handler used by this reader.
	 * 
	 * @return the error handler
	 */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * Returns the number of entries written to the result file.  Querying this method immediately after the
	 * constructor in which the result file already existed returns the number of valid entries that were
	 * recovered.
	 * 
	 * @return the number of entries written to the result file thus far
	 */
	@Override
	public int getNumberOfEntries() {
		return numberOfEntries;
	}

	/**
	 * Writes the decision variables, objectives and optional properties to the output file.  Constraint violating
	 * solutions are not recorded.
	 * 
	 * @param entry the entry to write
	 * @throws IOException if an I/O error occurred
	 */
	@Override
	public void write(ResultEntry entry) throws IOException {		
		numberOfEntries++;

		//ensure a non-empty entry is written
		Population population = entry.getPopulation();
		TypedProperties properties = entry.getProperties();
		
		if (population.isEmpty() && ((properties == null) || (properties.isEmpty()))) {
			writer.println("//");
		}

		//write entry
		if ((properties != null) && !properties.isEmpty()) {
			printProperties(properties, "//");
		}
		
		if (!population.isEmpty()) {
			for (Solution solution : population) {
				printSolution(solution);
			}
		}

		writer.println('#');
		writer.flush();
	}
	
	/**
	 * Prints the solution to the result file.
	 * 
	 * @param solution the solution
	 */
	private void printSolution(Solution solution) {
		boolean writeSeparator = false;
		
		if (!excludeVariables) {
			// write decision variables
			for (int i = 0; i < solution.getNumberOfVariables(); i++) {
				if (writeSeparator) {
					writer.print(' ');
				}

				writer.print(encode(solution.getVariable(i)));
				writeSeparator = true;
			}
		}

		// write objectives
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			if (writeSeparator) {
				writer.print(' ');
			}

			writer.print(solution.getObjective(i).getValue());
			writeSeparator = true;
		}

		// write constraints
		for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
			if (writeSeparator) {
				writer.print(' ');
			}

			writer.print(solution.getConstraint(i).getValue());
			writeSeparator = true;
		}
		
		writer.println();
	}
	
	/**
	 * Prints the properties to the result file.
	 * 
	 * @param properties the properties
	 * @throws IOException if an I/O error occurred
	 */
	private void printProperties(TypedProperties properties, String prefix) throws IOException {
		try (StringWriter buffer = new StringWriter()) {
			properties.save(buffer, new NumericStringComparator());
		
			try (LineReader lineReader = LineReader.wrap(new StringReader(buffer.toString()))) {
				for (String line : lineReader) {
					writer.print(prefix);
					writer.println(line);
				}
			}
			
			writer.flush();
		} 
	}

	@Override
	public void close() {
		writer.close();
	}
	
	/**
	 * Encodes the decision variable into a string representation that can be safely written to a result file.
	 * The resulting strings must not contain any whitespace characters.  For decision variables that do not support
	 * a valid encoding, the string {@code "-"} will be returned and a warning message printed.
	 * 
	 * @param variable the decision variable to encode
	 * @return the string representation of the decision variable
	 */
	public String encode(Variable variable) {
		try {
			return variable.encode();
		} catch (Exception e) {
			errorHandler.warn(ENCODING_WARNING);
			return "-";
		}
	}

	/**
	 * Opens the result file for writing.  Any existing file will be overwritten.
	 * 
	 * @param problem the problem
	 * @param file the file
	 * @return the result file writer
	 * @throws IOException if an I/O error occurred
	 */
	public static ResultFileWriter open(Problem problem, File file) throws IOException {
		return new ResultFileWriter(problem, file);
	}

	/**
	 * Opens the result file in append mode.  If the file already exists, any invalid entries will be removed by
	 * calling {@link #repair(Problem, File)}.  Check {@link #getNumberOfEntries()} to determine the number of valid
	 * entries in the file.
	 * 
	 * @param problem the problem
	 * @param file the file
	 * @return the result file writer
	 * @throws IOException if an I/O error occurred
	 */
	public static ResultFileWriter append(Problem problem, File file) throws IOException {
		if (!file.exists()) {
			return open(problem, file);
		}
		
		int numberOfEntries = repair(problem, file);
		
		ResultFileWriter writer = new ResultFileWriter(problem, new BufferedWriter(new FileWriter(file, true))) {

			protected void printHeader(Problem problem) throws IOException {
				// skip header when appending
			}
			
		};
		
		writer.numberOfEntries = numberOfEntries;
		return writer;
	}
	
	/**
	 * Repairs the contents of the result file, removing any incomplete or invalid entries from the file.
	 * 
	 * @param file the file
	 * @return the number of valid entries in the file
	 * @throws IOException if an I/O error occurred
	 */
	public static int repair(File file) throws IOException {
		return repair(null, file);
	}
	
	/**
	 * Repairs the contents of the result file, removing any incomplete or invalid entries from the file.
	 * 
	 * @param problem the problem, or {@code null} to derive the problem from the result file
	 * @param file the file
	 * @return the number of valid entries in the file
	 * @throws IOException if an I/O error occurred
	 */
	public static int repair(Problem problem, File file) throws IOException {
		if (!file.exists()) {
			return 0;
		}
		
		File tempFile = File.createTempFile("temp", null);
		int numberOfEntries = 0;
		
		try (ResultFileReader reader = ResultFileReader.open(problem, file);
				ResultFileWriter writer = ResultFileWriter.open(reader.getProblem(), tempFile)) {
			while (reader.hasNext()) {
				writer.write(reader.next());
				numberOfEntries += 1;
			}
		}

		// replace the original only if any changes were made, leaving the timestamp unchanged
		replace(tempFile, file);
		
		return numberOfEntries;
	}
	
}
