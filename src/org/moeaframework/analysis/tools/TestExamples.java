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
package org.moeaframework.analysis.tools;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.Timer;

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
		Settings.PROPERTIES.setBoolean(Settings.KEY_VERBOSE, true);

		Path examplesPath = Path.of("examples");

		if (!Files.exists(examplesPath)) {
			System.out.println("No examples directory!");
			return;
		}
		
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		if (commandLine.hasOption("headless")) {
			// Must be set before instantiating Toolkit or any UI components
			System.setProperty("java.awt.headless", "true");
		}
		
		try (Stream<Path> stream = Files.walk(examplesPath)) {
			stream.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
			.sorted()
			.forEach(p -> {
				Timer timer = Timer.startNew();
				System.out.print("Compiling ");
				System.out.print(p);
				System.out.print("...");

				Path classPath = p.getParent().resolve(FilenameUtils.removeExtension(p.getFileName().toString()) + ".class");

				if (commandLine.hasOption("clean") ||
						!Files.exists(classPath) ||
						FileUtils.isFileNewer(p.toFile(), classPath.toFile())) {
					FileUtils.deleteQuietly(classPath.toFile());

					JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
					compiler.run(null, null, null, p.toAbsolutePath().toString());

					System.out.print("done!");
				} else {
					System.out.print("skipped!");
				}

				Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
				System.out.print(" (");
				System.out.print(DurationUtils.formatHighResolution(elapsedTime));
				System.out.println(")");
			});
		}

		try (Stream<Path> stream = Files.walk(examplesPath)) {
			stream.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
			.sorted()
			.map(p -> p.getName(0).equals(examplesPath) ? p.subpath(1, p.getNameCount()) : p)
			.map(p -> FilenameUtils.removeExtension(p.toString()).replaceAll("[\\\\/]", "."))
			.forEach(p -> runExample(p, examplesPath));
		}
	}
	
	private static ClassLoader createClassLoader(Path... classpath) throws MalformedURLException {
		URL[] urls = new URL[classpath.length];
		
		for (int i = 0; i < classpath.length; i++) {
			urls[i] = classpath[i].toUri().toURL();
		}
		
		return URLClassLoader.newInstance(urls);
	}
	
	private static void runExample(String example, Path... classpath) {
		Timer timer = Timer.startNew();
		
		PrintStream systemOut = System.out;
		PrintStream systemErr = System.err;
		
		try (ByteArrayOutputStream outStorage = new ByteArrayOutputStream();
				ByteArrayOutputStream errStorage = new ByteArrayOutputStream();
				PrintStream captureOut = new PrintStream(outStorage);
				PrintStream captureErr = new PrintStream(errStorage)) {
			System.setOut(captureOut);
			System.setErr(captureErr);
			
			systemOut.print("Testing ");
			systemOut.print(example);
			systemOut.print("...");
			
			try {
				Class<?> cls = Class.forName(example, true, createClassLoader(classpath));
				Method mainMethod = cls.getDeclaredMethod("main", String[].class);
				mainMethod.invoke(null, (Object)new String[0]);
				
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
			throw new FrameworkException("Failure caught while running " + example, e);
		} finally {
			System.setOut(systemOut);
			System.setErr(systemErr);
		}
	}

	/**
	 * Starts the command line utility for building and testing examples.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new TestExamples().start(args);
	}

}
