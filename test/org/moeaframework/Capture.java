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
package org.moeaframework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.io.RedirectStream;

/**
 * Captures content written to standard output, including any partial output if an exception occurred.
 */
public class Capture {
	
	private Capture() {
		super();
	}
	
	public static CaptureResult output(Invocable invocable) throws IOException {
		PrintStream oldOut = System.out;
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream newOut = new PrintStream(baos)) {
			System.setOut(newOut);
			
			try {
				invocable.invoke();
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		} finally {
			System.setOut(oldOut);
		}
	}
	
	public static CaptureResult output(Class<? extends CommandLineUtility> tool, String... args) throws IOException {
		return output(() -> {
			Method mainMethod = tool.getMethod("main", String[].class);
			mainMethod.invoke(null, (Object)args);
		});
	}
	
	public static CaptureResult output(ProcessBuilder processBuilder) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try {
				RedirectStream.pipe(processBuilder, baos);
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		}
	}
	
	/**
	 * Function like a {@link Runnable} but can throw checked exceptions.
	 */
	@FunctionalInterface
	public static interface Invocable {
		
		public void invoke() throws Exception;
		
	}
	
	/**
	 * Stores the captured output and converts / saves it as required.  Note that it is safe to use the
	 * {@link ByteArrayOutputStream} in this manner, even after being closed, according to the docs.
	 */
	public static class CaptureResult {
		
		private final ByteArrayOutputStream output;
		
		private final Exception exception;
		
		public CaptureResult(ByteArrayOutputStream output) {
			this(output, null);
		}
		
		public CaptureResult(ByteArrayOutputStream output, Exception exception) {
			super();
			this.output = output;
			this.exception = exception;
		}
		
		public void assertSuccessful() {
			Assert.assertNull("Expected call to complete successfully, but an exception was thrown", exception);
		}
		
		public void assertThrows(Class<? extends Exception> expectedType) {
			Assert.assertNotNull("Expected call to throw an exception", exception);
			Assert.assertEquals("Thrown exception is not the expected type", expectedType, exception.getClass());
		}
		
		public boolean isSuccessful() {
			return exception == null;
		}
		
		public void rethrowException() {
			throw new FrameworkException("Caught exception while capturing output", exception);
		}
		
		public byte[] toBytes() {
			return output.toByteArray();
		}
		
		@Override
		public String toString() {
			return output.toString();
		}
		
		public File toFile() throws IOException {
			File file = TempFiles.createFile();
			toFile(file);
			return file;
		}
		
		public void toFile(File file) throws IOException {
			try (FileOutputStream output = new FileOutputStream(file)) {
				output.write(toBytes());
			}
		}
		
	}

}
