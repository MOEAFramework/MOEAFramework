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
	
	public void print(String s) {
		writer.print(s);
	}

	public void print(double d) {
		writer.print(d);
	}
	
	public void println() {
		writer.println();
	}
	
	public void println(String s) {
		writer.println(s);
	}
	
	public void println(double d) {
		writer.println(d);
	}

	@Override
	public void close() throws IOException {
		if (writer != null && writer != System.out) {
			writer.close();
		}
	}

}
