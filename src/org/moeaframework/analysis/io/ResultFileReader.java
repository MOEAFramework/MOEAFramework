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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.problem.ProblemStub;
import org.moeaframework.util.TypedProperties;

import static org.moeaframework.analysis.io.ResultFileWriter.ENCODING_WARNING;

/**
 * Reads result files created by {@link ResultFileWriter}.
 * <p>
 * This reader is expected to gracefully recover from incomplete or improperly formatted files. Unless a serious I/O
 * error occurred, this reader will attempt to load the file to the last valid entry. This requirement enables a
 * {@code ResultWriter} to resume processing at a valid state.
 * 
 * @see ResultFileWriter
 */
public class ResultFileReader implements Closeable, Iterator<ResultEntry>, Iterable<ResultEntry> {

	/**
	 * If {@code false}, any errors are suppressed and remaining entries are ignored.  This is the default when
	 * attempting to recover a corrupted or incomplete result file.  If {@code true}, an exception is thrown and
	 * processing terminates.
	 */
	private final boolean errorsAreFatal;
	
	/**
	 * If {@code false}, any validation warnings are logged but ignored.  If {@code true}, an exception is thrown and
	 * processing terminates.
	 */
	private final boolean warningsAreFatal;
	
	/**
	 * The internal stream for reading data from the file.
	 */
	private final BufferedReader reader;
	
	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * The last line read from the internal stream.
	 */
	private String line;

	/**
	 * The next entry to be returned; or {@code null} if the next entry has not yet been read.
	 */
	private ResultEntry nextEntry;
	
	/**
	 * The version of the file.  This value is written starting in version 5, and will default to {@code 0} otherwise.
	 */
	private int version;

	/**
	 * {@code true} if an error occurred parsing the result file; {@code false} otherwise.
	 */
	private boolean error;
	
	/**
	 * {@code true} if the warning for unsupported decision variables was displayed; {@code false} otherwise.
	 */
	private boolean printedWarning;

	/**
	 * Constructs a result file reader for reading the approximation sets from the specified result file.
	 * 
	 * @param problem the problem
	 * @param file the file containing the results
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileReader(Problem problem, File file) throws IOException {
		super();
		this.problem = problem;
		this.errorsAreFatal = false;
		this.warningsAreFatal = false;
		
		reader = new BufferedReader(new FileReader(file));
		readHeader();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public ResultEntry next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		ResultEntry result = nextEntry;
		nextEntry = null;
		return result;
	}

	@Override
	public Iterator<ResultEntry> iterator() {
		return this;
	}
	
	private void warn(String message) {
		if (warningsAreFatal) {
			throw new FrameworkException(message);
		}
		
		System.err.println(message);
	}
	
	private void error(String message) {
		if (errorsAreFatal) {
			throw new FrameworkException(message);
		}
		
		System.err.println(message);
		error = true;
	}
	
	private void readHeader() throws IOException {
		Pattern pattern = Pattern.compile("^#\\s*([a-z0-9_\\-]+)\\s*=\\s*(.*)$", Pattern.CASE_INSENSITIVE);
		
		if (line == null) {
			line = reader.readLine();
		}
		
		while ((line != null) && line.startsWith("#")) {
			Matcher matcher = pattern.matcher(line);
			
			if (matcher.matches()) {
				String key = matcher.group(1);
				String value = matcher.group(2);
				
				if (key.equalsIgnoreCase("Version")) {
					version = Integer.parseInt(value);
					
					if (version > Settings.getMajorVersion()) {
						warn("Result file created with newer version (" + version + ")");
					}
				} else if (problem instanceof ProblemStub) {
					// skip validations if using the ProblemStub
				} else if (key.equalsIgnoreCase("Problem")) {
					if (!value.isBlank() && !value.equalsIgnoreCase(problem.getName())) {
						warn("Problem defined in result file does not match problem (given: " +
								value + ", expected: " + problem.getName());
					}
				} else if (key.equalsIgnoreCase("Variables")) {
					int numberOfVariables = Integer.parseInt(value);
					
					if (numberOfVariables != problem.getNumberOfVariables()) {
						warn("Number of variables in result file does not match problem (given: " +
								numberOfVariables + ", expected: " + problem.getNumberOfVariables());
					}
				} else if (key.equalsIgnoreCase("Objectives")) {
					int numberOfObjectives = Integer.parseInt(value);
					
					if (numberOfObjectives != problem.getNumberOfObjectives()) {
						warn("Number of objectives in result file does not match problem (given: " +
								numberOfObjectives + ", expected: " + problem.getNumberOfObjectives());
					}
				} else if (key.equalsIgnoreCase("Constraints")) {
					int numberOfConstraints= Integer.parseInt(value);
					
					if (numberOfConstraints != problem.getNumberOfConstraints()) {
						warn("Number of constraints in result file does not match problem (given: " +
								numberOfConstraints + ", expected: " + problem.getNumberOfConstraints());
					}
				}
			} else {
				warn("Malformed header line, ignoring: " + line);
			}
			
			line = reader.readLine();
		}
	}

	/**
	 * Returns the next population in the file; or {@code null} if the end of the file is reached. If the last
	 * entry in the file is incomplete, {@code null} is returned.
	 * 
	 * @return the next population in the file; or {@code null} if the end of the file is reached
	 * @throws NumberFormatException if an error occurred parsing the objectives
	 * @throws IOException if an I/O error occurred
	 */
	private ResultEntry readNextEntry() throws NumberFormatException, IOException {
		NondominatedPopulation population = new NondominatedPopulation();
		StringWriter stringBuffer = new StringWriter();

		// ignore any comment lines separating entries
		while ((line != null) && line.startsWith("#")) {
			line = reader.readLine();
		}

		// read next entry, terminated by #
		while ((line != null) && !line.startsWith("#")) {
			if (line.startsWith("//")) {
				stringBuffer.write(line.substring(2));
				stringBuffer.write('\n');
			} else {
				Solution solution = parseSolution(line);
				
				if (solution == null) {
					error("unable to parse solution, ignoring remaining entries in the file");
					return null;
				} else {
					population.add(solution);
				}
			}

			line = reader.readLine();
		}
		
		TypedProperties properties = new TypedProperties();
		properties.load(new StringReader(stringBuffer.toString()));

		// return population only if non-empty and terminated by a #
		if ((line == null) || !line.startsWith("#")) {
			return null;
		} else {
			return new ResultEntry(population, properties);
		}
	}
	
	/**
	 * Parses the solution encoded in the specified line from the result file.
	 * 
	 * @param line the line containing the encoded solution
	 * @return the solution
	 */
	private Solution parseSolution(String line) {
		String[] entries = line.trim().split("\\s+");
		Solution solution = null;
		
		try {
			int index = 0;
			boolean includesVariables = false;
			boolean includesConstraints = false;
			
			if (version >= 5 && entries.length == problem.getNumberOfVariables() + problem.getNumberOfObjectives() + problem.getNumberOfConstraints()) {
				includesVariables = true;
				includesConstraints = true;
			} else if (version >= 5 && entries.length == problem.getNumberOfObjectives() + problem.getNumberOfConstraints()) {
				includesConstraints = true;
			} else if (version < 5 && entries.length == problem.getNumberOfVariables() + problem.getNumberOfObjectives()) {
				includesVariables = true;
			} else if (problem instanceof ProblemStub && entries.length >= problem.getNumberOfObjectives()) {
				// TODO: How to handle this case?????  ProblemStub only specifies objectives.  In the past, we pulled off
				// the last N entries, but with constraints being written we don't necessarily know where to start.
				// Can we get rid of ProblemSub and instead use the header info from the file?
				index = entries.length - problem.getNumberOfObjectives();
			} else if (entries.length != problem.getNumberOfObjectives()) {
				return null;
			}
			
			if (includesVariables) {
				solution = problem.newSolution();
			} else {
				solution = new Solution(0, problem.getNumberOfObjectives(), problem.getNumberOfConstraints());
			}
			
			if (includesVariables) {
				for (int i = 0; i < problem.getNumberOfVariables(); i++) {
					solution.setVariable(i, decode(solution.getVariable(i), entries[index++]));
				}
			}

			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				solution.setObjectiveValue(i, Double.parseDouble(entries[index++]));
			}
			
			if (includesConstraints) {
				for (int i = 0; i < problem.getNumberOfConstraints(); i++) {
					solution.setConstraintValue(i, Double.parseDouble(entries[index++]));
				}
			}
		} catch (Exception e) {
			error(e.getMessage());
			return null;
		}

		return solution;
	}

	@Override
	public boolean hasNext() {
		try {
			if (error) {
				return false;
			}

			if (nextEntry == null) {
				nextEntry = readNextEntry();
			}

			return nextEntry != null;
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Decodes string representations of decision variables, returning the variable with the decoded value.  Depending
	 * on the implementation and variable type, the same variable as provided in the arguments or a new variable will
	 * be returned.
	 * 
	 * @param variable the decision variable
	 * @param string the string representation of the decision variable
	 * @return the variable with the decoded value
	 * @see ResultFileWriter#encode(Variable)
	 */
	public Variable decode(Variable variable, String string) {
		if (string.equals("-")) {
			if (!printedWarning) {
				warn(ENCODING_WARNING);
				printedWarning = true;
			}
		} else {
			variable.decode(string);
		}
		
		return variable;
	}
	
	/**
	 * Opens the result file for reading.
	 * 
	 * @param problem the problem
	 * @param file the file containing the results
	 * @return the reader
	 * @throws IOException if an I/O error occurred
	 */
	public static ResultFileReader open(Problem problem, File file) throws IOException {
		return new ResultFileReader(problem, file);
	}

}
