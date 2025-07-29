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
package org.moeaframework.tools;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.JavaBuilder;
import org.moeaframework.util.ReflectionUtils;
import org.moeaframework.util.Timer;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility that scans the {@code examples/} folder for Java files, compiles them, and runs class
 * containing a {@code main} method.
 */
public class TestExamples extends CommandLineUtility {

	private TestExamples() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("c")
				.longOpt("clean")
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder()
				.longOpt("headless")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		TestEnvironment.setVerbose(true);

		File buildDirectory = new File("build/");
		File examplesDirectory = new File("examples/");

		if (!examplesDirectory.exists()) {
			System.out.println("No examples directory!");
			return;
		}
		
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		if (commandLine.hasOption("headless")) {
			// Must be set before instantiating Toolkit or any UI components
			TestEnvironment.setHeadless(true);
		}
		
		JavaBuilder builder = new JavaBuilder();
		builder.buildPath(buildDirectory);
		builder.sourcePath(examplesDirectory);
		
		try (Stream<Path> stream = Files.walk(examplesDirectory.toPath())) {
			stream.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
				.sorted()
				.forEach(p -> compile(p, builder));
		}

		try (Stream<Path> stream = Files.walk(examplesDirectory.toPath())) {
			stream.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
				.sorted()
				.forEach(p -> test(p, builder));
		}
	}
	
	private void compile(Path path, JavaBuilder builder) {
		try {
			Timer timer = Timer.startNew();
			System.out.print("Compiling ");
			System.out.print(path);
			System.out.print("...");
			
			if (!builder.compile(path.toFile())) {
				throw new FrameworkException("Failed to compile " + path);
			}
	
			System.out.print("done!");
	
			Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
			System.out.print(" (");
			System.out.print(DurationUtils.formatHighResolution(elapsedTime));
			System.out.println(")");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private void test(Path path, JavaBuilder builder) {
		Timer timer = Timer.startNew();
		
		PrintStream systemOut = System.out;
		PrintStream systemErr = System.err;
		
		try (ByteArrayOutputStream outStorage = new ByteArrayOutputStream();
				ByteArrayOutputStream errStorage = new ByteArrayOutputStream();
				PrintStream captureOut = new PrintStream(outStorage);
				PrintStream captureErr = new PrintStream(errStorage)) {
			System.setOut(captureOut);
			System.setErr(captureErr);
			
			String className = builder.getFullyQualifiedClassName(path.toFile());
						
			systemOut.print("Testing ");
			systemOut.print(className);
			systemOut.print("...");
			
			try {
				Class<?> cls = Class.forName(className, true, builder.getClassLoader());
				ReflectionUtils.invokeStaticMethod(cls, "main", (Object)new String[0]);

				systemOut.print("done!");
			} catch (NoSuchMethodException e) {
				systemOut.print("skipped (no main method)");
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof HeadlessException) {
					systemOut.print("skipped (requires graphical display)");
				} else {
					throw e;
				}
			}
			
			captureOut.close();
			captureErr.close();
			
			Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
			systemOut.print(" (");
			systemOut.print(DurationUtils.formatHighResolution(elapsedTime));
			systemOut.println(")");
			
			if (outStorage.size() > 0) {
				systemOut.println();
				systemOut.println("================================ Begin Output ================================");
				systemOut.print(outStorage.toString());
				systemOut.println("================================= End Output =================================");
			}
			
			if (errStorage.size() > 0) {
				systemOut.println();
				systemOut.println("================================ Begin Error =================================");
				systemOut.print(errStorage.toString());
				systemOut.println("================================= End Error ==================================");
			}
			
			systemOut.println();
		} catch (Exception e) {
			throw new FrameworkException("Failed during test of " + path, e);
		} finally {
			System.setOut(systemOut);
			System.setErr(systemErr);
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new TestExamples().start(args);
	}

}
