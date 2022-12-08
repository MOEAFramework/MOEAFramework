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

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Internal class used by several tools in this package for writing either to
 * {@see System.out} or a file.  In particular, this will not allow callers
 * to close {@code System.out}, such as when it appears in a try-with-resources
 * block.
 */
class OutputLogger implements Closeable {
	
	private final PrintStream writer;
	
	public OutputLogger() {
		this((PrintStream)null);
	}
	
	public OutputLogger(String filename) throws FileNotFoundException {
		this(new File(filename));
	}
	
	public OutputLogger(File file) throws FileNotFoundException {
		this(file == null ? null : new PrintStream(file));
	}
	
	public OutputLogger(PrintStream writer) {
		super();
		this.writer = writer == null ? System.out : writer;
	}
	
	public void print(boolean b) {
		writer.print(b);
	}

	public void print(char c) {
		writer.print(c);
	}
	
	public void print(char[] cs) {
		writer.print(cs);
	}
	
	public void print(double d) {
		writer.print(d);
	}
	
	public void print(float f) {
		writer.print(f);
	}
	
	public void print(int i) {
		writer.print(i);
	}
	
	public void print(long l) {
		writer.print(l);
	}
	
	public void print(Object o) {
		writer.print(o);
	}
	
	public void print(String s) {
		writer.print(s);
	}
	
	public void println() {
		writer.println();
	}
	
	public void println(boolean b) {
		writer.println(b);
	}

	public void println(char c) {
		writer.println(c);
	}
	
	public void println(char[] cs) {
		writer.println(cs);
	}
	
	public void println(double d) {
		writer.println(d);
	}
	
	public void println(float f) {
		writer.println(f);
	}
	
	public void println(int i) {
		writer.println(i);
	}
	
	public void println(long l) {
		writer.println(l);
	}
	
	public void println(Object o) {
		writer.println(o);
	}
	
	public void println(String s) {
		writer.println(s);
	}

	@Override
	public void close() throws IOException {
		if (writer != null && writer != System.out) {
			writer.close();
		}
	}

}
