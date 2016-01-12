/* Copyright 2009-2016 David Hadka
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.util.io.CommentedLineReader;

/**
 * Loads the parameters in a parameter file. A parameter file contains on each
 * line the name, lower bound, and upper bound of a parameter separated by
 * whitespace.
 */
public class ParameterFile {

	/**
	 * The parameters.
	 */
	private final List<Parameter> parameters;

	/**
	 * Constructs a parameter file with the parameters contained in the
	 * specified parameter description file.
	 * 
	 * @param file the parameter file
	 * @throws IOException if an I/O error occurred
	 */
	public ParameterFile(File file) throws IOException {
		this(new FileReader(file));
	}

	/**
	 * Constructs a parameter file with the parameters read from the underlying
	 * reader.  The reader is closed when the parameters are finished loading.
	 * 
	 * @param reader the reader of the parameters
	 * @throws IOException if an I/O error occurred
	 */
	public ParameterFile(Reader reader) throws IOException {
		super();

		if (reader instanceof CommentedLineReader) {
			parameters = load((CommentedLineReader)reader);
		} else {
			parameters = load(new CommentedLineReader(reader));
		}
	}

	/**
	 * Returns the parameters read from the specified reader. The reader is
	 * closed by this method.
	 * 
	 * @param reader the reader of the parameters
	 * @return the parameters read from the specified reader
	 * @throws IOException if an I/O error occurred
	 */
	private List<Parameter> load(CommentedLineReader reader) throws IOException {
		List<Parameter> parameters = new ArrayList<Parameter>();
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\\s+");

				if (tokens.length != 3) {
					throw new IOException("expected only three items per line");
				}

				parameters
						.add(new Parameter(tokens[0], Double
								.parseDouble(tokens[1]), Double
								.parseDouble(tokens[2])));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return parameters;
	}

	/**
	 * Returns the number of parameters in this parameter file.
	 * 
	 * @return the number of parameters in this parameter file
	 */
	public int size() {
		return parameters.size();
	}

	/**
	 * Returns the parameter at the specified index.
	 * 
	 * @param index the index of the parameter to be returned
	 * @return the parameter at the specified index
	 */
	public Parameter get(int index) {
		return parameters.get(index);
	}

}
