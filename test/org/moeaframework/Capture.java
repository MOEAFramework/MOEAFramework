/* Copyright 2009-2025 David Hadka
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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableRunnable;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.io.RedirectStream;

/**
 * Captures content written to output, streams, or files.  One key difference is any exceptions are caught and returned
 * as part of the result, allowing this to capture partial output for debugging.
 */
public class Capture {
	
	private Capture() {
		super();
	}
	
	public static CaptureResult output(FailableRunnable<Exception> runnable) throws IOException {
		PrintStream oldOut = System.out;
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream newOut = new PrintStream(baos)) {
			System.setOut(newOut);
			
			try {
				runnable.run();
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e instanceof InvocationTargetException ite ? ite.getCause() : e);
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
	
	public static CaptureResult stream(FailableConsumer<PrintStream, Exception> consumer) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream out = new PrintStream(baos)) {
			try {
				consumer.accept(out);
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		}
	}
	
	public static CaptureResult input(InputStream stream) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try {
				stream.transferTo(baos);
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		}
	}
	
	public static CaptureResult input(Reader reader) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(baos)) {
			try {
				reader.transferTo(writer);
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		}
	}
	
	public static CaptureResult file(File file) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try {
				baos.write(Files.readAllBytes(file.toPath()));
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		}
	}
	
	public static CaptureResult file(FailableConsumer<File, Exception> consumer) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try {
				File tempFile = TempFiles.createFile();
				consumer.accept(tempFile);
				
				baos.write(Files.readAllBytes(tempFile.toPath()));
				return new CaptureResult(baos);
			} catch (Exception e) {
				return new CaptureResult(baos, e);
			}
		}
	}
	
	/**
	 * Stores the captured output and converts / saves it as required.  Note that it is safe to use the
	 * {@link ByteArrayOutputStream} in this manner, even after being closed, according to the docs.
	 */
	public static class CaptureResult {
		
		private final ByteArrayOutputStream output;
		
		private final Throwable exception;
		
		public CaptureResult(ByteArrayOutputStream output) {
			this(output, null);
		}
		
		public CaptureResult(ByteArrayOutputStream output, Throwable exception) {
			super();
			this.output = output;
			this.exception = exception;
		}
		
		public void assertSuccessful() {
			Assert.assertNull("Expected call to complete successfully, but an exception was thrown", exception);
		}
		
		public void assertThrows(Class<? extends Throwable> expectedType) {
			Assert.assertNotNull("Expected call to throw an exception", exception);
			Assert.assertEquals("Thrown exception is not the expected type", expectedType, exception.getClass());
		}
		
		public void assertEquals(String expected) {
			assertSuccessful();
			Assert.assertEquals(expected, toString());
		}
		
		public void assertEqualsNormalized(String expected) {
			assertSuccessful();
			Assert.assertEqualsNormalized(expected, toString());
		}
		
		public void assertContains(String expected) {
			assertSuccessful();
			Assert.assertStringContains(toString(), expected);
		}
		
		public void assertNotContains(String exepcted) {
			assertSuccessful();
			Assert.assertStringNotContains(toString(), exepcted);
		}
		
		public void assertThat(FailableConsumer<CaptureResult, Exception> condition) {
			assertSuccessful();
			
			try {
				condition.accept(this);
			} catch (Exception e) {
				throw new AssertionError("Caught unhandled exception while testing assertion", e);
			}
		}
		
		public boolean isSuccessful() {
			return exception == null;
		}
		
		public void rethrowException() {
			if (exception != null) {
				throw new FrameworkException("Caught exception while capturing output", exception);
			}
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
