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
package org.moeaframework.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.validate.Validate;

/**
 * Read lines from a character-input stream, with options to ignore blank and commented lines.
 */
public class LineReader extends BufferedReader implements Iterable<String>, Iterator<String> {
	
	/**
	 * When set, skip any lines starting with the comment prefix.
	 */
	private boolean skipComments;
	
	/**
	 * When set, skip any blank lines that contain only whitespace.
	 */
	private boolean skipBlanks;
	
	/**
	 * When set, trim any leading and trailing whitespace from each line.
	 */
	private boolean trim;
	
	/**
	 * The prefix string used to identify comment lines.
	 */
	private String commentPrefix;
	
	/**
	 * Stores the next line to return when {@link #readLine()} is called.
	 */
	private String nextLine;

	/**
	 * Constructs a new line reader.
	 * 
	 * @param in a reader
	 */
	public LineReader(Reader in) {
		super(in);
		this.commentPrefix = "#";
	}
	
	/**
	 * Wraps the given reader in a line reader.
	 * 
	 * @param reader the reader
	 * @return the reader wrapped in a line reader
	 */
	public static LineReader wrap(Reader reader) {
		if (reader instanceof LineReader commentedLineReader) {
			return commentedLineReader;
		} else {
			return new LineReader(reader);
		}
	}
	
	public LineReader skipComments() {
		this.skipComments = true;
		return this;
	}
	
	public LineReader skipBlanks() {
		this.skipBlanks = true;
		return this;
	}
	
	public LineReader trim() {
		this.trim = true;
		return this;
	}
	
	public LineReader commentPrefix(String commentPrefix) {
		Validate.that("commentPrefix", commentPrefix).isNotEmpty();
		
		this.commentPrefix = commentPrefix;
		return this;
	}
	
	private boolean skipLine(String line) {
		if (skipBlanks && line.isBlank()) {
			return true;
		}
		
		if (skipComments && line.startsWith(commentPrefix)) {
			return true;
		}
		
		return false;
	}
	
	private String readLineUncheckedException() {
		try {
			return readLine();
		} catch (IOException e) {
			throw new FrameworkException("caught exception while itereating over line reader", e);
		}
	}

	@Override
	public String readLine() throws IOException {
		String line = null;
		
		if (nextLine != null) {
			line = nextLine;
			nextLine = null;
		} else {
			line = super.readLine();

			// loop until we find the next line
			while ((line != null) && skipLine(line)) {
				line = super.readLine();
			}
			
			if (line != null && trim) {
				line = line.trim();
			}

			return line;
		}
		
		return line;
	}
	
	@Override
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public boolean hasNext() {
		if (nextLine == null) {
			nextLine = readLineUncheckedException();
		}
		
		return nextLine != null;
	}

	@Override
	public String next() {
		String result = readLineUncheckedException();
			
		if (result == null) {
			throw new NoSuchElementException();
		}
			
		return result;
	}
	
	@Override
	public long transferTo(Writer out) throws IOException {
		String newline = System.lineSeparator();
		long size = 0;

		for (String line : this) {
			out.write(line);
			out.write(newline);

			size += line.length() + newline.length();
		}

		return size;
	}
	
	/**
	 * Returns the last line in the reader, skipping any intermediate lines.
	 * 
	 * @return the last line
	 */
	public String last() {
		String result = next();
		
		while (hasNext()) {
			result = next();
		}
		
		return result;
	}

}