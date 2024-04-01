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
package org.moeaframework.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.core.Settings;

/**
 * Abstract class for providing command line utilities. This class is provided to ensure a standard interface for
 * command line utilities as well as handling the quirks of different operating systems.
 * <p>
 * Upon calling {@link #start(String[])}, this class registers an uncaught exception handler on the calling thread.
 * The purpose of this handler is to catch any exceptions and display a formatted error message on the command line.
 * <p>
 * As such, subclasses should include a main method similar to the following:
 * <pre>
 *     public class MyCustomUtility extends CommandLineUtility {
 *     
 *         // implement the run and getOptions methods
 *         
 *         public static void main(String[] args) throws Exception {
 *             new MyCustomUtility().start(args);
 *         }
 *         
 *     }
 * </pre>
 * Note that the main method always throws an {@link Exception}.  This ensures the error is propagated to the uncaught
 * exception handler.
 */
public abstract class CommandLineUtility {
	
	/**
	 * Exception handler for formatting and printing the error message to the command line.
	 */
	private class CommandLineUncaughtExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * The command string used to invoke this command line utility.  If {@code null}, this displays as
	 * {@code java full.class.Name}.
	 */
	private String commandString = null;

	/**
	 * Constructs a command line utility.  The constructor for subclasses should provide a private constructor unless
	 * programmatic access is also desired.
	 */
	public CommandLineUtility() {
		super();
	}

	/**
	 * Returns the options made available by this command line utility.  The base implementation automatically provides
	 * the {@code -h,--help} option.  Implementations overriding this method and begin with a call to
	 * {@code super.getOptions()}.
	 * 
	 * @return the options made available by this command line utility
	 */
	public Options getOptions() {
		Options options = new Options();

		options.addOption(Option.builder("h")
				.longOpt("help")
				.build());

		return options;
	}

	/**
	 * Runs this command line utility with the specified command line arguments.
	 * 
	 * @param commandLine the command line arguments
	 * @throws Exception if any exception occurred while running this command
	 */
	public abstract void run(CommandLine commandLine) throws Exception;

	/**
	 * Starts this command line utility with the command line arguments provided by the {@code main} method.  The
	 * command line arguments are parsed into a {@link CommandLine} object and the {@code run} method is invoked.
	 * <p>
	 * At the start of this method, a specialized uncaught exception handler is registered with the calling thread.
	 * This exception handler is responsible for formatting and displaying any errors on the command line.  Note that
	 * this exception handler is not removed at the end of this method; its removal is not necessary when this utility
	 * is invoked on the command line.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if any exception occurred while running this command
	 */
	public void start(String[] args) throws Exception {
		Thread.currentThread().setUncaughtExceptionHandler(new CommandLineUncaughtExceptionHandler());
		
		// trim last argument because of an error with Windows newline characters
		if (args.length > 0) {
			args[args.length - 1] = args[args.length - 1].trim();
		}

		Options options = getOptions();
		CommandLineParser commandLineParser = new DefaultParser();
		
		try {
			CommandLine commandLine = commandLineParser.parse(options, args);
			
			if (commandLine.hasOption("help")) {
				showHelp();
			} else {
				run(commandLine);
			}
		} catch (ParseException e) {
			if (args.length > 0 && (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help"))) {
				showHelp();
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Returns the options with their descriptions loaded using {@link Localization}.
	 * 
	 * @return the available command line options
	 */
	protected Options getLocalizedOptions() {
		Options options = getOptions();
		
		//update the options with their descriptions
		for (Option option : options.getOptions()) {
			String key = "option." + option.getLongOpt();
			Class<?> type = getClass();
			
			while (CommandLineUtility.class.isAssignableFrom(type)) {
				if (Localization.containsKey(type, key)) {
					option.setDescription(Localization.getString(type, key));
					break;
				} else {
					type = type.getSuperclass();
				}
			}
		}
		
		return options;
	}
	
	/**
	 * Format and display the help information that details the available command line options.
	 */
	protected void showHelp() {
		Options options = getLocalizedOptions();
		
		StringBuilder description = new StringBuilder();
		description.append(System.lineSeparator());
		description.append(Localization.getString(getClass(), "description"));
		description.append("  ");
		description.append(Localization.getString(CommandLineUtility.class, "description"));
		description.append(System.lineSeparator());
		description.append(System.lineSeparator());
		
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(Settings.PROPERTIES.getInt(Settings.KEY_HELP_WIDTH, HelpFormatter.DEFAULT_WIDTH));
		helpFormatter.printHelp(
				getCommandString(),
				description.toString(),
				options, 
				Localization.getString(CommandLineUtility.class, "footer"),
				true);
	}

	/**
	 * Returns the command string used to invoke this command line utility.
	 * 
	 * @return the command string used to invoke this command line utility
	 */
	protected String getCommandString() {
		if (commandString == null) {
			return "java -classpath \"lib/*\" " + getClass().getName();
		} else {
			return commandString;
		}
	}

	/**
	 * Sets the command string used to invoke this command line utility.
	 * 
	 * @param commandString the command string used to invoke this command line utility; or {@code null} to use the
	 *        default Java command line string
	 */
	protected void setCommandString(String commandString) {
		this.commandString = commandString;
	}

}
