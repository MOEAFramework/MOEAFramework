/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool;
import org.moeaframework.analysis.sensitivity.Analysis;
import org.moeaframework.analysis.sensitivity.Evaluator;
import org.moeaframework.analysis.sensitivity.ExtractData;
import org.moeaframework.analysis.sensitivity.Negater;
import org.moeaframework.analysis.sensitivity.ResultFileEvaluator;
import org.moeaframework.analysis.sensitivity.ResultFileInfo;
import org.moeaframework.analysis.sensitivity.ResultFileMerger;
import org.moeaframework.analysis.sensitivity.ResultFileSeedMerger;
import org.moeaframework.analysis.sensitivity.SampleGenerator;
import org.moeaframework.analysis.sensitivity.SetContribution;
import org.moeaframework.analysis.sensitivity.SetGenerator;
import org.moeaframework.analysis.sensitivity.SetHypervolume;
import org.moeaframework.analysis.sensitivity.SimpleStatistics;
import org.moeaframework.analysis.sensitivity.SobolAnalysis;

/**
 * A command line frontend to all of the command line utilities provided by the
 * MOEA Framework.  In conjunction with a shell/terminal script, this class is
 * intended to simplify launching to utilities provided by the MOEA Framework.
 */
public class Frontend {
	
	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			Frontend.class.getPackage().getName());
	
	/**
	 * The name of the script used to launch this frontend.
	 */
	private static final String COMMAND = "moea";
	
	/**
	 * The list of available subcommands.
	 */
	private static final List<String> subcommands;
	
	static {
		subcommands = new ArrayList<String>();
		subcommands.add("Analysis");
		subcommands.add("DiagnosticTool");
		subcommands.add("Evaluator");
		subcommands.add("ExtractData");
		subcommands.add("Help");
		subcommands.add("Negater");
		subcommands.add("ReferenceSetMerger");
		subcommands.add("ResultFileEvaluator");
		subcommands.add("ResultFileInfo");
		subcommands.add("ResultFileMerger");
		subcommands.add("ResultFileSeedMerger");
		subcommands.add("SampleGenerator");
		subcommands.add("SetContribution");
		subcommands.add("SetGenerator");
		subcommands.add("SetHypervolume");
		subcommands.add("SimpleStatistics");
		subcommands.add("SobolAnalysis");
		subcommands.add("Version");
	}
	
	/**
	 * Mock command line utility for displaying the version number.
	 */
	private static class Version extends CommandLineUtility {
		
		public Version() {
			super();
		}

		@Override
		public void run(CommandLine commandLine) throws Exception {
			Properties properties = new Properties();
			properties.load(Frontend.class.getResourceAsStream(
					"/META-INF/build.properties"));
			
			System.out.println(properties.getProperty("version"));
		}

	}

	/**
	 * Starts the command line frontend.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}

		OptionCompleter completer = new OptionCompleter(subcommands);
		String command = completer.lookup(args[0]);
		
		if (command == null) {
			printUnknownCommand(args[0]);
			System.exit(-1);
		} else {
			if (command.equals("Help")) {
				if (args.length >= 2) {
					command = completer.lookup(args[1]);
					
					if (command == null) {
						printUnknownCommand(args[1]);
						System.exit(-1);
					} else if (command.equals("Help")) {
						printHelpHelp();
					} else {
						launchTool(command, new String[] { "--help" });
					}
				} else {
					printHelp();
				}
			} else {
				launchTool(command, Arrays.copyOfRange(args, 1, args.length));
			}
		}
	}
	
	private static void printUnknownCommand(String command) {
		System.err.println(localization.getString("Frontend.unknownCommand",
				command));
		System.err.println(localization.getString("Frontend.usage", COMMAND));
	}
	
	private static void printUsage() {
		System.out.println(localization.getString("Frontend.usage", COMMAND));
	}
	
	private static void printHelpHelp() {
		HelpFormatter formatter = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out, true);
		
		formatter.printUsage(writer, formatter.getWidth(),
				localization.getString("Help.command", COMMAND));
		formatter.printWrapped(writer, formatter.getWidth(),
				localization.getString("Help.description"));
		formatter.printWrapped(writer,  formatter.getWidth(), 
				localization.getString("CommandLineUtility.footer"));
	}
	
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out, true);
		
		formatter.printUsage(writer, formatter.getWidth(),
				localization.getString("Frontend.command",
						COMMAND));
		formatter.printWrapped(writer, formatter.getWidth(),
				localization.getString("Frontend.description",
						COMMAND));
		
		writer.println();
		
		formatter.printWrapped(writer, formatter.getWidth(),
				localization.getString("Frontend.available"));
		
		for (String subcommand : subcommands) {
			writer.print("    ");
			writer.println(subcommand);
		}

		formatter.printWrapped(writer,  formatter.getWidth(), 
				localization.getString("CommandLineUtility.footer"));
	}
	
	private static void launchTool(String name, String[] arguments) {
		try {
			CommandLineUtility tool = null;
			
			if (name.equals("Analysis")) {
				tool = new Analysis();
			} else if (name.equals("DiagnosticTool")) {
				tool = new LaunchDiagnosticTool();
			} else if (name.equals("Evaluator")) {
				tool = new Evaluator();
			} else if (name.equals("ExtractData")) {
				tool = new ExtractData();
			} else if (name.equals("Negater")) {
				tool = new Negater();
			} else if (name.equals("ReferenceSetMerger")) {
				tool = new ReferenceSetMerger();
			} else if (name.equals("ResultFileEvaluator")) {
				tool = new ResultFileEvaluator();
			} else if (name.equals("ResultFileInfo")) {
				tool = new ResultFileInfo();
			} else if (name.equals("ResultFileMerger")) {
				tool = new ResultFileMerger();
			} else if (name.equals("ResultFileSeedMerger")) {
				tool = new ResultFileSeedMerger();
			} else if (name.equals("SampleGenerator")) {
				tool = new SampleGenerator();
			} else if (name.equals("SetContribution")) {
				tool = new SetContribution();
			} else if (name.equals("SetGenerator")) {
				tool = new SetGenerator();
			} else if (name.equals("SetHypervolume")) {
				tool = new SetHypervolume();
			} else if (name.equals("SimpleStatistics")) {
				tool = new SimpleStatistics();
			} else if (name.equals("SobolAnalysis")) {
				tool = new SobolAnalysis();
			} else if (name.equals("Version")) {
				tool = new Version();
			} else {
				throw new IllegalStateException();
			}
			
			if (tool != null) {
				tool.setCommandString(COMMAND + " " + name);
				tool.start(arguments);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Frontend() {
		super();
	}

}
