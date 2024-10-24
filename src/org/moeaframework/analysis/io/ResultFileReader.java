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

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.moeaframework.core.Constructable;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemStub;
import org.moeaframework.util.ErrorHandler;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Tokenizer;

import static org.moeaframework.analysis.io.ResultFileWriter.ENCODING_WARNING;

/**
 * Reads result files created by {@link ResultFileWriter}.
 * <p>
 * By default, this will attempt to gracefully recover from incomplete or improperly formatted files.  Unless a serious
 * I/O error occurred, this reader will attempt to load the file to the last valid entry.  This requirement enables a
 * {@code ResultWriter} to resume processing at a valid state.  {@link #setErrorsAreFatal(boolean)} and
 * {@link #setWarningsAreFatal(boolean)} can be used to change this default behavior.
 * 
 * @see ResultFileWriter
 */
public class ResultFileReader implements Closeable, Iterator<ResultEntry>, Iterable<ResultEntry> {
	
	/**
	 * The internal stream for reading data from the file.
	 */
	private final LineReader reader;
	
	/**
	 * The tokenizer for parsing lines.
	 */
	private final Tokenizer tokenizer;
	
	/**
	 * The problem.
	 */
	private Problem problem;

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
	 * When set, indicates the file is using the legacy, objective-only file format produced by
	 * {@link org.moeaframework.core.population.Population#saveObjectives(File)}.
	 */
	private boolean legacyFormat;

	/**
	 * The error handler.
	 */
	private final ErrorHandler errorHandler;
	
	// TODO: Add reader constructor
	
	/**
	 * Constructs a result file reader for reading the approximation sets from the specified result file.
	 * 
	 * @param file the file containing the results
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileReader(File file) throws IOException {
		this(null, file);
	}
	
	/**
	 * Constructs a result file reader for reading the approximation sets from the specified result file.
	 * 
	 * @param problem the problem, if {@code null} a problem "stub" will be generated
	 * @param file the file containing the results
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileReader(Problem problem, File file) throws IOException {
		this(problem, file, false);
	}

	/**
	 * Constructs a result file reader for reading the approximation sets from the specified result file.
	 * 
	 * @param problem the problem, if {@code null} a problem "stub" will be generated
	 * @param file the file containing the results
	 * @param allowLegacyFormat allows reading legacy file formats for backwards compatibility
	 * @throws IOException if an I/O error occurred
	 */
	protected ResultFileReader(Problem problem, File file, boolean allowLegacyFormat) throws IOException {
		super();
		this.problem = problem;
		
		errorHandler = new ErrorHandler();
		errorHandler.setSuppressDuplicates(true);
		
		reader = LineReader.wrap(new FileReader(file));
		readHeader(allowLegacyFormat);
		
		tokenizer = new Tokenizer();
	}
	
	/**
	 * Returns the problem instance used by this reader.  This is either the problem instance passed into the
	 * constructor or a {@link ProblemStub} if the problem was reconstructed from the result file.
	 * 
	 * @return the problem instance
	 */
	public Problem getProblem() {
		return problem;
	}
	
	/**
	 * Returns the error handler used by this reader.
	 * 
	 * @return the error handler
	 */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
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
	
	private void readHeader(boolean allowLegacyFormat) throws IOException {		
		if (line == null) {
			line = reader.readLine();
		}

		// Read the header content
		StringWriter stringBuffer = new StringWriter();
		
		while ((line != null) && line.startsWith("#")) {
			stringBuffer.write(line.substring(1).trim());
			stringBuffer.write('\n');

			line = reader.readLine();
		}

		TypedProperties header = TypedProperties.newInstance();
		header.load(new StringReader(stringBuffer.toString()));
		
		// Validate the header content
		version = header.getInt("Version", 0);
		
		if (version > Settings.getMajorVersion()) {
			errorHandler.warn("Result file created with newer version (file: {0}, software: {1})",
					version, Settings.getMajorVersion());
		}
		
		String problemName = header.getString("Problem", "");
		int numberOfVariables = header.getInt("NumberOfVariables", header.getInt("Variables", 0));
		int numberOfObjectives = header.getInt("NumberOfObjectives", header.getInt("Objectives", 0));
		int numberOfConstraints = header.getInt("NumberOfConstraints", header.getInt("Constraints", 0));
		
		if (allowLegacyFormat && version == 0 && numberOfVariables == 0 && numberOfObjectives == 0 && numberOfConstraints == 0) {
			legacyFormat = true;
		}
		
		if (problem == null) {
			ProblemStub stub = new ProblemStub(problemName, numberOfVariables, numberOfObjectives, numberOfConstraints);
			
			for (int i = 0; i < numberOfVariables; i++) {
				if (header.contains("Variable." + (i+1) + ".Definition")) {
					stub.setVariableDefinition(i, Constructable.createInstance(Variable.class,
							header.getString("Variable." + (i+1) + ".Definition")));
				}
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				if (header.contains("Objective." + (i+1) + ".Definition")) {
					stub.setObjectiveDefinition(i, Constructable.createInstance(Objective.class,
							header.getString("Objective." + (i+1) + ".Definition")));
				}
			}
			
			for (int i = 0; i < numberOfConstraints; i++) {
				if (header.contains("Constraint." + (i+1) + ".Definition")) {
					stub.setConstraintDefinition(i, Constructable.createInstance(Constraint.class,
							header.getString("Constraint." + (i+1) + ".Definition")));
				}
			}
			
			problem = stub;
		} else {
			if (problemName != null && problem.getName() != null && 
					!problemName.isEmpty() && !problem.getName().isEmpty() &&
					!problemName.equalsIgnoreCase(problem.getName())) {
				errorHandler.warn("Problem defined in result file does not match problem (given: '{0}', expected: '{1}')",
						problemName, problem.getName());
			}
			
			if (numberOfVariables > 0 && numberOfVariables != problem.getNumberOfVariables()) {
				errorHandler.warn("Number of variables in result file does not match problem (given: {0}, expected: {1})",
						numberOfVariables, problem.getNumberOfVariables());
			}
			
			if (numberOfObjectives > 0 && numberOfObjectives != problem.getNumberOfObjectives()) {
				errorHandler.warn("Number of objectives in result file does not match problem (given: {0}, expected: {1})",
						numberOfObjectives, problem.getNumberOfObjectives());
			}
			
			if (numberOfConstraints > 0 && numberOfConstraints != problem.getNumberOfConstraints()) {
				errorHandler.warn("Number of constraints in result file does not match problem (given: {0}, expected: {1})",
						numberOfConstraints, problem.getNumberOfConstraints());
			}
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
					errorHandler.error("Unable to parse solution, ignoring remaining entries in the file");
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
			if (legacyFormat && population.size() > 0) {
				return new ResultEntry(population, properties);
			}
			
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
		String[] entries = tokenizer.decodeToArray(line);
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
			} else if (!legacyFormat && entries.length != problem.getNumberOfObjectives()) {
				errorHandler.error("Unable to parse solution, number of entries ({0}) does not match problem definition", entries.length);
				return null;
			}
			
			Solution prototype = problem.newSolution();
			
			solution = new Solution(
					includesVariables ? problem.getNumberOfVariables() : 0,
					legacyFormat ? entries.length : problem.getNumberOfObjectives(),
					problem.getNumberOfConstraints());
			
			for (int i = 0; i < solution.getNumberOfVariables(); i++) {
				solution.setVariable(i, decode(prototype.getVariable(i), entries[index++]));
			}

			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				Objective objective = legacyFormat ? Objective.createDefault() : prototype.getObjective(i);
				objective.setValue(Double.parseDouble(entries[index++]));
				solution.setObjective(i, objective);
			}
			
			for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
				Constraint constraint = prototype.getConstraint(i);
				
				if (includesConstraints) {
					constraint.setValue(Double.parseDouble(entries[index++]));
				}
				
				solution.setConstraint(i, constraint);
			}			
		} catch (Exception e) {
			errorHandler.error(e);
			return null;
		}

		return solution;
	}

	@Override
	public boolean hasNext() {
		try {
			if (errorHandler.isError()) {
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
			errorHandler.warn(ENCODING_WARNING);
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
	
	/**
	 * Opens the result file for reading, supporting older legacy file formats.
	 * 
	 * @param problem the problem
	 * @param file the file containing the results
	 * @return the reader
	 * @throws IOException if an I/O error occurred
	 */
	public static ResultFileReader openLegacy(Problem problem, File file) throws IOException {
		return new ResultFileReader(problem, file, true);
	}

}
