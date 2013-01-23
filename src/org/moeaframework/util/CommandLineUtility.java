/* Copyright 2009-2013 David Hadka
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Abstract class for providing command line utilities. This class is provided
 * to ensure a standard interface for command line utilities as well as handling
 * the quirks of different operating systems.
 */
public abstract class CommandLineUtility {
	
	/**
	 * The command string used to invoke this command line utility.  If
	 * {@code null}, this displays as {@code java full.class.Name}.
	 */
	private String commandString = null;

	/**
	 * Constructs a command line utility. The constructor for subclasses should
	 * provide a private constructor unless programmatic access is also desired.
	 */
	public CommandLineUtility() {
		super();
	}

	/**
	 * Returns the options made available by this command line utility. The
	 * base implementation automatically provides the {@code -h,--help} option.
	 * Implementations overriding this method and begin with a call to
	 * {@code super.getOptions()}.
	 * 
	 * @return the options made available by this command line utility
	 */
	@SuppressWarnings("static-access")
	public Options getOptions() {
		Options options = new Options();

		options.addOption(OptionBuilder.withLongOpt("help").withDescription(
				"Display help information").create('h'));

		return options;
	}

	/**
	 * Runs this command line utility with the specified command line arguments.
	 * 
	 * @param commandLine the command line arguments
	 */
	public abstract void run(CommandLine commandLine) throws Exception;

	/**
	 * Starts this command- line utility with the command line arguments
	 * provided by the {@code main} method. The command line arguments are
	 * parsed into a {@link CommandLine} object and the {@code run} method is
	 * invoked.
	 * 
	 * @param args the command line arguments
	 */
	public void start(String[] args) {
		// trim last argument because of an error with Windows newline
		// characters
		if (args.length > 0) {
			args[args.length - 1] = args[args.length - 1].trim();
		}

		Options options = getOptions();
		CommandLineParser commandLineParser = new GnuParser();
		CommandLine commandLine = null;

		try {
			commandLine = commandLineParser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

		if ((commandLine == null) || commandLine.hasOption("help")) {
			//update the options with their descriptions
			for (Object object : options.getOptions()) {
				Option option = (Option)object;
				String key = "option." + option.getLongOpt();
				Class<?> type = getClass();
				
				while (CommandLineUtility.class.isAssignableFrom(type)) {
					if (Localization.containsKey(type, key)) {
						option.setDescription(
								Localization.getString(type, key));
						break;
					} else {
						type = type.getSuperclass();
					}
				}
			}
			
			//format and display the help message
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp(
					getCommandString(),
					Localization.getString(getClass(), "description"),
					options, 
					Localization.getString(CommandLineUtility.class, "footer"),
					true);
			System.exit(-1);
		}

		try {
			run(commandLine);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Returns the command string used to invoke this command line utility.
	 * 
	 * @return the command string used to invoke this command line utility
	 */
	public String getCommandString() {
		if (commandString == null) {
			return "java " + getClass().getName();
		} else {
			return commandString;
		}
	}

	/**
	 * Sets the command string used to invoke this command line utility.
	 * 
	 * @param commandString the command string used to invoke this command line
	 *        utility; or {@code null} to use the default Java command line
	 *        string
	 */
	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}

}
