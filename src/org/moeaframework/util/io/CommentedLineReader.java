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
package org.moeaframework.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Read text from a character-input stream, ignoring lines starting with the #
 * character. Lines are only ignored when using the {@link #readLine} method.
 */
public class CommentedLineReader extends BufferedReader {

	/**
	 * Constructs a buffered reader that ignores lines starting with the # 
	 * character with the specified input buffer size.
	 * 
	 * @param in a reader
	 * @param sz the input buffer size
	 */
	public CommentedLineReader(Reader in, int sz) {
		super(in, sz);
	}

	/**
	 * Constructs a buffered reader that ignores lines starting with the # 
	 * character.
	 * 
	 * @param in a reader
	 */
	public CommentedLineReader(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {
		String line = super.readLine();

		// skip over comments
		while ((line != null) && line.startsWith("#")) {
			line = super.readLine();
		}

		return line;
	}

}
