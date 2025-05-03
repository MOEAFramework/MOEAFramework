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
package org.moeaframework.builder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;

/**
 * Command line tool used to lookup Java Native Access (JNA) information.
 */
public class JNAInfo extends CommandLineUtility {
	
	/**
	 * Constructs a new instance of this command line utility.
	 */
	public JNAInfo() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionGroup group = new OptionGroup();
		group.setRequired(true);

		group.addOption(Option.builder("s")
				.longOpt("sysArch")
				.build());
		
		group.addOption(Option.builder("l")
				.longOpt("libName")
				.hasArg()
				.argName("name")
				.build());
		
		group.addOption(Option.builder("f")
				.longOpt("findLib")
				.hasArg()
				.argName("name")
				.build());

		group.addOption(Option.builder("t")
				.longOpt("testProblem")
				.hasArg()
				.argName("name")
				.build());
		
		options.addOptionGroup(group);
		
		return options;
	}
	
	private Options getLegacyOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("p")
				.longOpt("problem")
				.hasArg()
				.required()
				.build());
		
		OptionGroup group = new OptionGroup();
		group.setRequired(true);
		
		group.addOption(Option.builder("l")
				.longOpt("libName")
				.build());

		group.addOption(Option.builder("t")
				.longOpt("test")
				.build());
		
		options.addOptionGroup(group);
		
		return options;
	}

	@Override
	public void start(String[] args) throws Exception {
		Options options = getLegacyOptions();
		CommandLineParser commandLineParser = new DefaultParser();
		
		try {
			CommandLine commandLine = commandLineParser.parse(options, args, true);
			
			if (commandLine.hasOption("problem")) {
				if (commandLine.hasOption("test")) {
					super.start(new String[] { "--testProblem", commandLine.getOptionValue("problem") });
				} else if (commandLine.hasOption("libName")) {
					super.start(new String[] { "--libName", commandLine.getOptionValue("problem") });
				} else {
					super.start(args);
				}
			} else {
				super.start(args);
			}
		} catch (ParseException e) {
			super.start(args);
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		if (commandLine.hasOption("findLib")) {
			String name = commandLine.getOptionValue("findLib");

			
			try {
				NativeLibrary.getInstance(name);
				pass("Located native library");
			} catch (UnsatisfiedLinkError e) {
				fail("Failed to locate native library");
			}
		}
		
		if (commandLine.hasOption("testProblem")) {
			String problemName = commandLine.getOptionValue("testProblem");
			Problem problem = null;

			try {
				NativeLibrary.getInstance(problemName);
				pass("Located native library");
			} catch (UnsatisfiedLinkError e) {
				fail("Failed to locate native library");
			}
			
			try {
				problem = ProblemFactory.getInstance().getProblem(problemName);
				pass("Problem registered with SPI");
			} catch (ProviderNotFoundException e) {
				fail("Problem not registered correctly with SPI");
			}
			
			if (problem != null) {
				Solution solution = problem.newSolution();
				
				for (int i = 0; i < solution.getNumberOfVariables(); i++) {
					solution.getVariable(i).randomize();
				}
				
				try {
					problem.evaluate(solution);
				} catch (Exception e) {
					fail("Problem evaluation failed: " + e.getMessage());
				}
				
				for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
					if (Double.isNaN(solution.getObjective(i).getValue())) {
						fail("Objective at index " + i + " was unset (NaN)");
					}
				}
				
				for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
					if (Double.isNaN(solution.getConstraint(i).getValue())) {
						fail("Constraint at index " + i + " was unset (NaN)");
					}
				}
				
				pass("Problem evaluated successfully");
			}
		}
		
		if (commandLine.hasOption("libName")) {
			System.out.println(System.mapLibraryName(commandLine.getOptionValue("libName")));
		}
		
		if (commandLine.hasOption("sysArch")) {
			System.out.println(Platform.RESOURCE_PREFIX);
		}
	}
	
	private void pass(String message) {
		System.out.println("\u2705 " + message);
	}
	
	private void fail(String message) {
		System.out.println("\u274c " + message);
		throw new FrameworkException("Test failed!");
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new JNAInfo().start(args);
	}

}
