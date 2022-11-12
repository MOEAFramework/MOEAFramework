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
	
	private PrintStream writer;
	
	public OutputLogger() {
		this((PrintStream)null);
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
		writer.print(b);
	}

	public void println(char c) {
		writer.print(c);
	}
	
	public void println(char[] cs) {
		writer.print(cs);
	}
	
	public void println(double d) {
		writer.print(d);
	}
	
	public void println(float f) {
		writer.print(f);
	}
	
	public void println(int i) {
		writer.print(i);
	}
	
	public void println(long l) {
		writer.print(l);
	}
	
	public void println(Object o) {
		writer.print(o);
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
