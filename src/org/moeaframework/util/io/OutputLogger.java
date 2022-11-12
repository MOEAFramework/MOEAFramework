package org.moeaframework.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Convenience class used by several tools when needing to write to either
 * {@code System.out} or a user-defined file.
 */
public class OutputLogger implements Closeable {
	
	private PrintStream writer;
	
	/**
	 * Creates a new output logger.
	 */
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
