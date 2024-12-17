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
import java.io.StringReader;
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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.Timer;
import org.moeaframework.util.io.LineReader;

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
			getLogger().warning("No examples directory!");
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
			stream
				.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
				.sorted()
				.forEach(p -> compileExample(p, commandLine.hasOption("clean")));
		}

		try (Stream<Path> stream = Files.walk(examplesPath)) {
			stream
				.filter(p -> FilenameUtils.getExtension(p.getFileName().toString()).equalsIgnoreCase("java"))
				.sorted()
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
	
	private void compileExample(Path example, boolean clean) {
		getLogger().info("Compiling " + example + "...");

		Path classPath = example.getParent().resolve(
				FilenameUtils.removeExtension(example.getFileName().toString()) + ".class");

		if (clean || !Files.exists(classPath) || FileUtils.isFileNewer(example.toFile(), classPath.toFile())) {
			Timer timer = Timer.startNew();
			FileUtils.deleteQuietly(classPath.toFile());

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			int exitCode = compiler.run(null, null, null, example.toAbsolutePath().toString());
			
			if (exitCode != 0) {
				getLogger().severe("Failed with non-zero exit code (" + exitCode + ")");
				throw new FrameworkException("Failed while compiling " + example);
			} else {
				Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
				getLogger().info("Succeeded! (" + DurationUtils.formatHighResolution(elapsedTime) + ")");
			}
		} else {
			getLogger().info("Skipped (no changes to source)");
		}
	}
	
	private void runExample(Path example, Path examplesPath) {
		if (example.startsWith(examplesPath)) {
			example = examplesPath.relativize(example);
		}
		
		String className = FilenameUtils.removeExtension(example.toString()).replaceAll("[\\\\/]", ".");
		
		PrintStream systemOut = System.out;
		PrintStream systemErr = System.err;
		
		try (ByteArrayOutputStream outStorage = new ByteArrayOutputStream();
				ByteArrayOutputStream errStorage = new ByteArrayOutputStream();
				PrintStream captureOut = new PrintStream(outStorage);
				PrintStream captureErr = new PrintStream(errStorage)) {
			System.setOut(captureOut);
			System.setErr(captureErr);
			
			getLogger().info("Testing " + className + "...");

			try {
				Timer timer = Timer.startNew();

				Class<?> cls = Class.forName(className, true, createClassLoader(examplesPath));
				Method mainMethod = cls.getDeclaredMethod("main", String[].class);
				mainMethod.invoke(null, (Object)new String[0]);
				
				Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
				getLogger().info("Succeeded! (" + DurationUtils.formatHighResolution(elapsedTime) + ")");
			} catch (NoSuchMethodException e) {
				getLogger().info("Skipped (no main method)");
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof HeadlessException) {
					getLogger().info("Skipped (requires graphical display)");
				} else {
					throw e;
				}
			}
			
			captureOut.close();
			captureErr.close();
			
			if (outStorage.size() > 0) {
				getLogger().info("================================ Begin Output ================================");
				try (LineReader reader = LineReader.wrap(new StringReader(outStorage.toString()))) {
					reader.lines().forEach(s -> getLogger().info(s));
				}
				getLogger().info("================================= End Output =================================");
			}
			
			if (errStorage.size() > 0) {
				getLogger().warning("================================ Begin Error =================================");
				try (LineReader reader = LineReader.wrap(new StringReader(errStorage.toString()))) {
					reader.lines().forEach(s -> getLogger().info(s));
				}
				getLogger().warning("================================= End Error ==================================");
			}
		} catch (Exception e) {
			getLogger().severe("Failed!");
			getLogger().severe(ExceptionUtils.getStackTrace(e));
			throw new FrameworkException("Failed while testing " + className);
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
