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
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.CommentedLineReader;
import org.moeaframework.util.io.FileUtils;

/**
 * Writes result files. A result file contains one or more entries consisting of a non-dominated population and
 * optional properties. Entries are separated by one or more consecutive lines starting with the {@code #} character.
 * Text contained on these lines after the {@code #} character are ignored.
 * <p>
 * An entry contains two pieces of data: 1) properties which are defined on lines starting with {@code //} in the
 * same format as {@link TypedProperties}; and 2) lines containing a sequence of floating-point numbers listing,
 * in order, the real-valued decision variables and objectives. Each decision variable is separated by one or more
 * whitespace characters. Decision variables that can not be encoded appear as {@code -}. The writer will attempt to
 * output the data in a human-readable format, but falls back on Base64 encoded serialized objects to store
 * serializable variables.
 * <p>
 * Complete entries are always terminated by a line starting with the {@code #} character. Incomplete entries, such
 * as those with the incorrect number of decision variables or objectives, are automatically removed.
 * <p>
 * When appending is enabled, this will attempt to recover any valid records from the previous file. Query the
 * {@link #getNumberOfEntries()} method to determine how many valid entries were recovered.
 * 
 * @see ResultFileReader
 */
public class ResultFileWriter implements OutputWriter {
	
    /**
     * The message displayed when an unsupported decision variable type is encountered.
     */
	static final String ENCODING_WARNING = 
			"unsupported decision variable type, could cause unexpected behavior or data loss";
	
	/**
	 * The message displayed when excluding the decision variables when saving a result file.
	 */
	static final String NO_VARIABLES_WARNING =
			"saving result file without variables, could cause unexpected behavior or data loss";
	
	/**
	 * The message displayed when an unclean file exists from a previous run.
	 */
	static final String EXISTING_FILE =
			"an unclean version of the file exists from a previous run, requires manual intervention";

	/**
	 * The stream for appending data to the file.
	 */
	private final PrintWriter writer;
	
	/**
	 * Settings for this result file.
	 */
	private final ResultFileWriterSettings settings;

	/**
	 * The number of lines in the file.
	 */
	private int numberOfEntries;
	
	/**
	 * {@code true} if the warning for unsupported decision variables was displayed; {@code false} otherwise.
	 */
	private boolean printedWarning;
	
	/**
	 * Constructs a result file writer with default settings.
	 * 
	 * @param problem the problem
	 * @param file the file to which the results are stored
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileWriter(Problem problem, File file) throws IOException {
		this(problem, file, ResultFileWriterSettings.getDefault());
	}
	
	/**
	 * Constructs a result file writer for writing the decision variables and objectives of a sequence of
	 * non-dominated populations to a file.
	 * 
	 * @param problem the problem
	 * @param file the file to which the results are stored
	 * @param settings the settings to use when writing the result file
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileWriter(Problem problem, File file, ResultFileWriterSettings settings) throws IOException {
		super();
		this.settings = settings;
		
		if (!settings.isIncludeVariables()) {
			System.err.println(NO_VARIABLES_WARNING);
		}
		
		if (settings.isAppend()) {
			// when appending, first move the file to a temporary location
			File existingFile = settings.getUncleanFile(file);
			
			if (existingFile.exists()) {
				if (settings.getCleanupStrategy().equals(CleanupStrategy.RESTORE)) {
					if (file.exists()) {
						FileUtils.delete(existingFile);
					} else {
						// do nothing, the unclean file is ready for recovery
					}
				} else if (settings.getCleanupStrategy().equals(CleanupStrategy.OVERWRITE)) {
					FileUtils.delete(existingFile);
				} else {
					throw new FrameworkException(EXISTING_FILE);
				}
			}
			
			if (file.exists()) {
				FileUtils.move(file, existingFile);
			}
		}

		// prepare this class for writing
		numberOfEntries = 0;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		// print header information
		writer.print("# Problem = ");
		writer.println(problem.getName());
		
		if (settings.isIncludeVariables()) {
			writer.print("# Variables = ");
			writer.println(problem.getNumberOfVariables());
		}
		
		writer.print("# Objectives = ");
		writer.println(problem.getNumberOfObjectives());

		if (settings.isAppend()) {
			// when appending, copy valid entries out of temporary file
			File existingFile = settings.getUncleanFile(file);

			if (existingFile.exists()) {
				try (ResultFileReader reader = new ResultFileReader(problem, existingFile)) {
					while (reader.hasNext()) {
						append(reader.next());
					}
				}
	
				FileUtils.delete(existingFile);
			}
		}
	}

	/**
	 * Returns the number of entries written to the result file. Querying this method immediately after the
	 * constructor in which the result file already existed returns the number of valid entries that were
	 * recovered.
	 * 
	 * @return the number of entries written to the result file thus far
	 */
	public int getNumberOfEntries() {
		return numberOfEntries;
	}

	/**
	 * Appends the decision variables, objectives and optional properties to the output file.  Constraint violating
	 * solutions are not recorded.
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
		
		if (feasibleSolutions.isEmpty() && ((properties == null) || (properties.isEmpty()))) {
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
		if (settings.isIncludeVariables()) {
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
			if ((i > 0) || (settings.isIncludeVariables() && (solution.getNumberOfVariables() > 0))) {
				writer.print(' ');
			}

			writer.print(solution.getObjective(i));
		}

		writer.println();
	}
	
	/**
	 * Prints the properties to the result file.
	 * 
	 * @param properties the properties
	 * @throws IOException if an I/O error occurred
	 */
	private void printProperties(TypedProperties properties) throws IOException {
		// using Properties#store is a roundabout way, but this ensures special characters are stored safely
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
			if (!printedWarning) {
				System.err.println(ENCODING_WARNING);
				printedWarning = true;
			}
			
			return "-";
		}
	}
	
	/**
	 * The settings used when writing result files.
	 */
	public static class ResultFileWriterSettings extends OutputWriterSettings {
		
		/**
		 * {@code true} to enable writing all decision variables; {@code false} otherwise.
		 */
		protected final boolean includeVariables;
		
		/**
		 * Constructs the default settings object.
		 */
		public ResultFileWriterSettings() {
			this(Optional.empty(), Optional.empty(), Optional.empty());
		}
		
		/**
		 * Constructs a new result file settings object.
		 * 
		 * @param append {@code true} to enable append mode, {@code false} otherwise
		 * @param includeVariables {@code true} to enable writing all decision variables; {@code false} otherwise
		 * @param cleanupStrategy the cleanup strategy
		 */
		public ResultFileWriterSettings(Optional<Boolean> append, Optional<Boolean> includeVariables,
				Optional<CleanupStrategy> cleanupStrategy) {
			super(append, cleanupStrategy);
			this.includeVariables = includeVariables != null && includeVariables.isPresent() ?
					includeVariables.get() : true;
		}
		
		/**
		 * Returns {@code true} if writing all decision variables; {@code false} otherwise.
		 * 
		 * @return {@code true} if writing all decision variables; {@code false} otherwise
		 */
		public boolean isIncludeVariables() {
			return includeVariables;
		}
		
		/**
		 * Returns the default settings for writing result files.
		 * 
		 * @return the default settings for writing result files
		 */
		public static ResultFileWriterSettings getDefault() {
			return new ResultFileWriterSettings();
		}
		
		/**
		 * Returns the settings with append mode disabled.
		 * 
		 * @return the settings with append mode disabled
		 */
		public static ResultFileWriterSettings noAppend() {
			return new ResultFileWriterSettings(Optional.of(false), Optional.empty(), Optional.empty());
		}
		
		/**
		 * Returns the settings produced from the given command line options.
		 * 
		 * @param commandLine the given command line options
		 * @return the settings
		 */
		public static ResultFileWriterSettings from(CommandLine commandLine) {
			return new ResultFileWriterSettings(Optional.empty(),
					Optional.of(!commandLine.hasOption("novariables")),
					Optional.empty());
		}
		
	}
	
}
