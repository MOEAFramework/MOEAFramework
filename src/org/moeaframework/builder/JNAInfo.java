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
package org.moeaframework.builder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.util.CommandLineUtility;

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
		
		options.addOption(Option.builder("p")
				.longOpt("problem")
				.hasArg()
				.build());
		
		OptionGroup group = new OptionGroup();
		group.setRequired(true);

		group.addOption(Option.builder("s")
				.longOpt("sysArch")
				.build());
		
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
	public void run(CommandLine commandLine) throws Exception {
		if (commandLine.hasOption("test")) {
			NativeLibrary instance = NativeLibrary.getInstance(getRequiredProblemName(commandLine));
			System.out.println("Found " + instance.getName() + " at " + instance.getFile());
		} else if (commandLine.hasOption("libName")) {
			System.out.println(System.mapLibraryName(getRequiredProblemName(commandLine)));
		} else if (commandLine.hasOption("sysArch")) {
			System.out.println(Platform.RESOURCE_PREFIX);
		} else {
			throw new IllegalArgumentException("No option selected");
		}
	}
	
	private String getRequiredProblemName(CommandLine commandLine) {
		String result = commandLine.getOptionValue("problem");
		
		if (result == null) {
			throw new IllegalArgumentException("must supply a problem name along with the given options");
		}
		
		return result;
	}

	/**
	 * Starts this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new JNAInfo().start(args);
	}

}
