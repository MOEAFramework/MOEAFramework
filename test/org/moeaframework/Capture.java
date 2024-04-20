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
import java.nio.charset.StandardCharsets;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;

public class Capture {
	
	private Capture() {
		super();
	}
	
	public static CaptureResult output(Runnable runnable) throws IOException {
		PrintStream oldOut = System.out;
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream newOut = new PrintStream(baos)) {
			System.setOut(newOut);
			runnable.run();
			return new CaptureResult(baos);
		} finally {
			System.setOut(oldOut);
		}
	}
	
	public static CaptureResult output(Class<? extends CommandLineUtility> tool, String... args) throws IOException {
		return output(() -> {
			try {
				Method mainMethod = tool.getMethod("main", String[].class);
				mainMethod.invoke(null, (Object)args);
			} catch (Exception e) {
				throw new FrameworkException("Caught exception calling the main method of " + tool.getSimpleName(), e);
			}
		});
	}
	
	public static class CaptureResult {
		
		private final ByteArrayOutputStream output;
		
		public CaptureResult(ByteArrayOutputStream output) {
			super();
			this.output = output;
		}
		
		public byte[] toBytes() {
			return output.toByteArray();
		}
		
		@Override
		public String toString() {
			return output.toString(StandardCharsets.UTF_8);
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
