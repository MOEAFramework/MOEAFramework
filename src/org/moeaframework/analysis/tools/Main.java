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
package org.moeaframework.analysis.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool;
import org.moeaframework.builder.BuildProblem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.Localization;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.cli.GeneratePlots;
import org.moeaframework.util.cli.TestExamples;
import org.moeaframework.util.format.TabularData;

/**
 * Entry point to access all command line tools.
 */
public class Main extends CommandLineUtility {

	private static final Map<String, Class<? extends CommandLineUtility>> TOOLS;
	
	private static final Map<String, Class<? extends CommandLineUtility>> INTERNAL_TOOLS;

	private final TypedProperties buildProperties;

	static {
		TOOLS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		INTERNAL_TOOLS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		registerTool(CalculateIndicator.class);
		registerTool(DataStoreTool.class);
		registerTool(EndOfRunEvaluator.class);
		registerTool(Initialize.class);
		registerTool(MetricsAnalysis.class);
		registerTool(MetricsEvaluator.class);
		registerTool(MetricsValidator.class);
		registerTool(ReferenceSetGenerator.class);
		registerTool(ResultFileConverter.class);
		registerTool(ResultFileMerger.class);
		registerTool(ResultFileMetadata.class);
		registerTool(ResultFileSeedMerger.class);
		registerTool(ResultFileValidator.class);
		registerTool(ResultFileViewer.class);
		registerTool(RuntimeEvaluator.class);
		registerTool(SampleGenerator.class);
		registerTool(SobolAnalysis.class);
		registerTool(Solve.class);
		registerTool(WeightGenerator.class);

		registerTool(BuildProblem.class);
		registerTool(LaunchDiagnosticTool.class);
		
		registerTool(GeneratePlots.class.getSimpleName(), GeneratePlots.class, true);
		registerTool(TestExamples.class.getSimpleName(), TestExamples.class, true);
	}

	/**
	 * Registers a tool to be included in the CLI commands.
	 * 
	 * @param tool the class implementing the tool
	 */
	public static void registerTool(Class<? extends CommandLineUtility> tool) {
		registerTool(tool.getSimpleName(), tool, false);
	}

	/**
	 * Registers a tool to be included in the CLI commands.
	 * 
	 * @param name the command name
	 * @param tool the class implementing the tool
	 * @param internal if {@code true}, the tool is hidden from the list of available commands
	 */
	public static void registerTool(String name, Class<? extends CommandLineUtility> tool, boolean internal) {
		if (internal) {
			INTERNAL_TOOLS.put(name, tool);
		} else {
			TOOLS.put(name, tool);
		}
	}

	private Main() throws IOException {
		super();
		setCommandString("java -classpath \"lib/*\" " + getClass().getName() + " [command]");
		buildProperties = TypedProperties.loadBuildProperties();
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String[] args = commandLine.getArgs();
		
		if (commandLine.hasOption("verbose")) {
			Settings.PROPERTIES.setBoolean(Settings.KEY_VERBOSE, true);
		}

		if (commandLine.hasOption("version")) {
			showVersion();
		} else if (commandLine.hasOption("info")) {
			showInfo();
		} else if (args.length < 1) {
			showHelp();
		} else {
			OptionCompleter completer = new OptionCompleter(TOOLS.keySet());
			completer.addAll(INTERNAL_TOOLS.keySet());
			
			String command = completer.lookup(args[0]);

			if (command == null) {
				throw new UnrecognizedOptionException("'" + args[0] +
						"' is not a valid command, use --help to see available options");
			}

			Class<? extends CommandLineUtility> toolClass = INTERNAL_TOOLS.containsKey(command) ?
					INTERNAL_TOOLS.get(command) : TOOLS.get(command);
			String[] toolArgs = Arrays.copyOfRange(args, 1, args.length);
			
			Constructor<? extends CommandLineUtility> constructor = toolClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			
			CommandLineUtility toolInstance = constructor.newInstance();
			toolInstance.setHideUsage(true);
			toolInstance.start(toolArgs);
		}
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionGroup group = new OptionGroup();

		group.addOption(Option.builder("v")
				.longOpt("version")
				.build());
		group.addOption(Option.builder()
				.longOpt("info")
				.build());
		
		options.addOptionGroup(group);
		options.addOption(Option.builder()
				.longOpt("verbose")
				.build());

		return options;
	}

	/**
	 * Displays the version of this library.
	 */
	protected void showVersion() {
		System.out.println(buildProperties.getString("version"));
	}
	
	/**
	 * Displays information about the system for debugging purposes.
	 */
	protected void showInfo() {
		TabularData.of(List.of(
				Pair.of("Java Version", System.getProperty("java.version")),
				Pair.of("Java Vendor", System.getProperty("java.vendor")),
				Pair.of("OS Name", System.getProperty("os.name")),
				Pair.of("OS Architecture", System.getProperty("os.arch")),
				Pair.of("OS Version", System.getProperty("os.version")),
				Pair.of("MOEAFramework Version", buildProperties.getString("version")))).display();
	}

	@Override
	protected void showHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		int width = getConsoleWidth();
		
		Options commands = new Options();
		
		for (String tool : TOOLS.keySet()) {
			commands.addOption(tool, Localization.getString(TOOLS.get(tool), "description"));
		}
		
		try (PrintWriter writer = createOutputWriter()) {
			helpFormatter.printWrapped(writer, width, buildProperties.getString("name") + ": " +
					buildProperties.getString("description"));
			writer.println();
			helpFormatter.printWrapped(writer, width, Localization.getString(getClass(), "description"));
			writer.println();
			helpFormatter.setOptPrefix("");
			helpFormatter.printOptions(writer, width, commands, helpFormatter.getLeftPadding(),
					helpFormatter.getDescPadding());
			writer.println();
			helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "header"));
			writer.println();
			helpFormatter.setOptPrefix("-");
			helpFormatter.printOptions(writer, width, getLocalizedOptions(), helpFormatter.getLeftPadding(),
					helpFormatter.getDescPadding());
			helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "footer"));
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Main().start(args);
	}

}
