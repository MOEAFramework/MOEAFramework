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
package org.moeaframework.analysis.sensitivity;

import static org.moeaframework.analysis.sensitivity.ResultFileWriter.ENCODING_WARNING;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Reads result files created by {@link ResultFileWriter}.  See the documentation
 * for {@code ResultWriter} for a description of the file format.
 * <p>
 * This reader is expected to gracefully recover from incomplete or improperly
 * formatted files. Unless a serious I/O error occurred, this reader will
 * attempt to load the file to the last valid entry. This requirement enables a
 * {@code ResultWriter} to resume processing at a valid state.
 * 
 * @see ResultFileWriter
 */
public class ResultFileReader implements Closeable, Iterator<ResultEntry>,
Iterable<ResultEntry> {

	/**
	 * The internal stream for reading data from the file.
	 */
	private final BufferedReader reader;

	/**
	 * The last line read from the internal stream.
	 */
	private String line;

	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * The next entry to be returned; or {@code null} if the next entry has not
	 * yet been read.
	 */
	private ResultEntry nextEntry;

	/**
	 * {@code true} if an error occurred parsing the result file; {@code false}
	 * otherwise.
	 */
	private boolean error;
	
	/**
	 * {@code true} if the warning for unsupported decision variables was
	 * displayed; {@code false} otherwise.
	 */
	private boolean printedWarning;

	/**
	 * Constructs a result file reader for reading the approximation sets from
	 * the specified result file.
	 * 
	 * @param problem the problem
	 * @param file the file containing the results
	 * @throws IOException if an I/O error occurred
	 */
	public ResultFileReader(Problem problem, File file) throws IOException {
		super();
		this.problem = problem;
		
		reader = new BufferedReader(new FileReader(file));

		// prime the reader by reading the first line
		line = reader.readLine();
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

	/**
	 * Returns the next population in the file; or {@code null} if the end of
	 * the file is reached. If the last entry in the file is incomplete,
	 * {@code null} is returned.
	 * 
	 * @return the next population in the file; or {@code null} if the end of
	 *         the file is reached
	 * @throws NumberFormatException if an error occurred parsing the objectives
	 * @throws IOException if an I/O error occurred
	 */
	private ResultEntry readNextEntry() throws NumberFormatException, 
	IOException {
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
					System.err.println("unable to parse solution, ignoring remaining entries in the file");
					return null;
				} else {
					population.add(solution);
				}
			}

			line = reader.readLine();
		}
		
		Properties properties = new Properties();
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

		if (entries.length < problem.getNumberOfObjectives()) {
			error = true;
			return null;
		}

		try {
			
			if (entries.length == (problem.getNumberOfVariables() + 
					problem.getNumberOfObjectives())) {
				solution = problem.newSolution();
				
				// read decision variables
				for (int i = 0; i < problem.getNumberOfVariables(); i++) {
					solution.setVariable(i, decode(solution.getVariable(i),
							entries[i]));
				}
			} else {
				solution = new Solution(0, problem.getNumberOfObjectives());
			}

			// read objectives
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				solution.setObjective(i, Double.parseDouble(
						entries[entries.length - 
						        problem.getNumberOfObjectives() + i]));
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
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
	 * Decodes string representations of decision variables, returning the
	 * variable with the decoded value.  Depending on the implementation and
	 * variable type, the same variable as provided in the arguments or a new
	 * variable will be returned.
	 * 
	 * @param variable the decision variable
	 * @param string the string representation of the decision variable
	 * @return the variable with the decoded value
	 * @see ResultFileWriter#encode(Variable)
	 */
	public Variable decode(Variable variable, String string) {
		if (variable instanceof RealVariable) {
			RealVariable rv = (RealVariable)variable;
			rv.setValue(Double.parseDouble(string));
			return rv;
		} else if (variable instanceof BinaryVariable) {
			BinaryVariable bv = (BinaryVariable)variable;
			
			if (bv.getNumberOfBits() != string.length()) {
				throw new FrameworkException("invalid bit string");
			}

			for (int i=0; i<bv.getNumberOfBits(); i++) {
				char c = string.charAt(i);
				
				if (c == '0') {
					bv.set(i, false);
				} else if (c == '1') {
					bv.set(i, true);
				} else {
					throw new FrameworkException("invalid bit string");
				}
			}
			
			return bv;
		} else if (variable instanceof Permutation) {
			Permutation p = (Permutation)variable;
			String[] tokens = string.split(",");
			int[] array = new int[tokens.length];
			
			for (int i=0; i<tokens.length; i++) {
				array[i] = Integer.parseInt(tokens[i]);
			}
			
			try {
				p.fromArray(array);
			} catch (IllegalArgumentException e) {
				throw new FrameworkException("invalid permutation", e);
			}
			
			return p;
		} else {
			if (string.equals("-")) {
				if (!printedWarning) {
					System.err.println(ENCODING_WARNING);
					printedWarning = true;
				}
				
				return variable;
			} else {
				try {
					return deserialize(string);
				} catch (Exception e) {
					throw new FrameworkException("deserialization failed", e);
				}
			}
		}
	}
	
	/**
	 * Returns the variable represented by the Base64 encoded string.
	 * 
	 * @param string the Base64 encoded representation of the variable
	 * @return the variable represented by the Base64 encoded string
	 * @throws IOException if the variable could not be deserialized
	 * @throws ClassNotFoundException if the class of the deserialized variable
	 *         could not be found
	 */
	private Variable deserialize(String string) throws IOException, 
	ClassNotFoundException {
		ObjectInputStream ois = null;
		
		try {
			byte[] encoding = Base64.decodeBase64(string);
			ByteArrayInputStream baos = new ByteArrayInputStream(encoding);
			ois = new ObjectInputStream(baos);
			
			return (Variable)ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}

}
