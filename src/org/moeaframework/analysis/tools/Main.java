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
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool;
import org.moeaframework.builder.BuildProblem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.cli.GeneratePlots;
import org.moeaframework.util.cli.SubcommandUtility;
import org.moeaframework.util.cli.TestExamples;
import org.moeaframework.util.format.TabularData;

/**
 * Entry point to access all command line tools.
 */
public class Main extends SubcommandUtility {

	private Main() {
		super();
		add(Subcommand.of(BuildProblem.class));
		add(Subcommand.of(CalculateIndicator.class));
		add(Subcommand.of(DataStoreTool.class));
		add(Subcommand.of(EndOfRunEvaluator.class));
		add(Subcommand.of(Initialize.class));
		add(Subcommand.of(LaunchDiagnosticTool.class));
		add(Subcommand.of(MetricsAnalysis.class));
		add(Subcommand.of(MetricsEvaluator.class));
		add(Subcommand.of(MetricsValidator.class));
		add(Subcommand.of(ReferenceSetGenerator.class));
		add(Subcommand.of(ResultFileConverter.class));
		add(Subcommand.of(ResultFileMerger.class));
		add(Subcommand.of(ResultFileMetadata.class));
		add(Subcommand.of(ResultFileSeedMerger.class));
		add(Subcommand.of(ResultFileValidator.class));
		add(Subcommand.of(ResultFileViewer.class));
		add(Subcommand.of(RuntimeEvaluator.class));
		add(Subcommand.of(SampleGenerator.class));
		add(Subcommand.of(SobolAnalysis.class));
		add(Subcommand.of(Solve.class));
		add(Subcommand.of(WeightGenerator.class));
		
		add(Subcommand.hidden(GeneratePlots.class));
		add(Subcommand.hidden(TestExamples.class));
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		if (commandLine.hasOption("verbose")) {
			Settings.PROPERTIES.setBoolean(Settings.KEY_VERBOSE, true);
		}

		if (commandLine.hasOption("version")) {
			showVersion();
		} else if (commandLine.hasOption("info")) {
			showInfo();
		} else if (commandLine.getArgs().length < 1) {
			showHelp();
		} else {
			super.run(commandLine);
		}
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionGroup group = new OptionGroup();

		group.addOption(Option.builder()
				.longOpt("version")
				.build());
		group.addOption(Option.builder()
				.longOpt("info")
				.build());
		
		options.addOptionGroup(group);
		options.addOption(Option.builder("v")
				.longOpt("verbose")
				.build());

		return options;
	}

	/**
	 * Displays the version of this library.
	 * 
	 * @throws IOException if an error occurred loading the build properties
	 */
	protected void showVersion() throws IOException {
		TypedProperties buildProperties = TypedProperties.loadBuildProperties();
		System.out.println(buildProperties.getString("version"));
	}
	
	/**
	 * Displays information about the system for debugging purposes.
	 * 
	 * @throws IOException if an error occurred loading the build properties
	 */
	protected void showInfo() throws IOException {
		TypedProperties buildProperties = TypedProperties.loadBuildProperties();
		
		TabularData.of(List.of(
				Pair.of("Java Version", System.getProperty("java.version")),
				Pair.of("Java Vendor", System.getProperty("java.vendor")),
				Pair.of("OS Name", System.getProperty("os.name")),
				Pair.of("OS Architecture", System.getProperty("os.arch")),
				Pair.of("OS Version", System.getProperty("os.version")),
				Pair.of("MOEAFramework Version", buildProperties.getString("version")))).display();
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
