/* Copyright 2009-2018 David Hadka
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
 * <p>
 * Upon calling {@link #start(String[])}, this class registers an uncaught
 * exception handler on the calling thread.  The purpose of this handler is to
 * catch any exceptions and display a formatted error message on the command
 * line.
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
 * Note that the main method always throws an {@link Exception}.  This ensures
 * the error is propagated to the uncaught exception handler.
 */
public abstract class CommandLineUtility {
	
	/**
	 * Exception handler for formatting and printing the error message to the
	 * command line.
	 */
	private class CommandLineUncaughtExceptionHandler implements
	UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			if (e instanceof ParseException) {
				// error when parsing command line options
				System.err.println(e.getMessage());
				showHelp();
			} else {
				e.printStackTrace();
			}
		}

	}
	
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

		options.addOption(OptionBuilder
				.withLongOpt("help")
				.create('h'));

		return options;
	}

	/**
	 * Runs this command line utility with the specified command line arguments.
	 * 
	 * @param commandLine the command line arguments
	 */
	public abstract void run(CommandLine commandLine) throws Exception;

	/**
	 * Starts this command line utility with the command line arguments
	 * provided by the {@code main} method. The command line arguments are
	 * parsed into a {@link CommandLine} object and the {@code run} method is
	 * invoked.
	 * <p>
	 * At the start of this method, a specialized uncaught exception handler
	 * is registered with the calling thread.  This exception handler is
	 * responsible for formatting and displaying any errors on the command
	 * line.  Note that this exception handler is not removed at the end of
	 * this method; its removal is not necessary when this utility is invoked
	 * on the command line.
	 * 
	 * @param args the command line arguments
	 */
	public void start(String[] args) throws Exception {
		Thread.currentThread().setUncaughtExceptionHandler(
				new CommandLineUncaughtExceptionHandler());
		
		// trim last argument because of an error with Windows newline
		// characters
		if (args.length > 0) {
			args[args.length - 1] = args[args.length - 1].trim();
		}

		Options options = getOptions();
		CommandLineParser commandLineParser = new GnuParser();
		CommandLine commandLine = commandLineParser.parse(options, args);
			
		if (commandLine.hasOption("help")) {
			showHelp();
		} else {
			run(commandLine);
		}
	}
	
	/**
	 * Format and display the help information that details the available
	 * command line options.  This method performs any available localization
	 * using {@link Localization}.
	 * 
	 * @param options the available command line options
	 */
	private void showHelp() {
		Options options = getOptions();
		
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
