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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.util.io.RedirectStream;
import org.moeaframework.util.io.Tokenizer;

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
			System.err.println("ERROR: " + e.getLocalizedMessage());
			
			if (Settings.isVerbose()) {
				System.err.println("Full stack trace:");
				e.printStackTrace(System.err);
			}
		}

	}
	
	/**
	 * The command string used to invoke this command line utility.  If {@code null}, this displays as
	 * {@code java full.class.Name}.
	 */
	private String commandString;
	
	/**
	 * When {@code true}, the usage line is not displayed.
	 */
	private boolean hideUsage;

	/**
	 * Constructs a command line utility.  The constructor for subclasses should provide a private constructor unless
	 * programmatic access is also desired.
	 */
	public CommandLineUtility() {
		super();
	}
	
	/**
	 * Returns the console width by:
	 * <ol>
	 *   <li>Using the value stored in {@value Settings#KEY_HELP_WIDTH}
	 *   <li>Using {@code stty size}
	 *   <li>Defaulting to {@value HelpFormatter#DEFAULT_WIDTH}
	 * </ol>
	 * 
	 * @return the console width
	 */
	protected int getConsoleWidth() {
		int width = Settings.PROPERTIES.getInt(Settings.KEY_HELP_WIDTH, -1);
		
		if (width <= 0) {
			if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
				try {
					ProcessBuilder processBuilder = new ProcessBuilder("stty", "size");
					processBuilder.redirectInput(Redirect.INHERIT);
					
					String output = RedirectStream.capture(processBuilder);
					Tokenizer tokenizer = new Tokenizer();
					width = Integer.parseInt(tokenizer.decodeToArray(output.trim())[1]);
				} catch (Exception e) {
					System.err.println("Unable to detect console width: " + e.getMessage());
				}
			}
		}
		
		if (width <= 0) {
			width = HelpFormatter.DEFAULT_WIDTH;
		}
		
		return width;
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
			CommandLine commandLine = commandLineParser.parse(options, args, true);
			
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
		HelpFormatter helpFormatter = new HelpFormatter();
		int width = getConsoleWidth();

		try (PrintWriter writer = createOutputWriter()) {
			if (!hideUsage) {
				helpFormatter.printUsage(writer, width, getCommandString(), options);
				writer.println();
			}
			
			helpFormatter.printWrapped(writer, width, Localization.getString(getClass(), "description"));
			writer.println();
			helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "header"));
			writer.println();
			helpFormatter.printOptions(writer, width, getLocalizedOptions(), helpFormatter.getLeftPadding(), helpFormatter.getDescPadding());
			helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "footer"));
		}
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
	
	/**
	 * Sets the flag to hide the usage line from the help message.
	 * 
	 * @param hideUsage if {@code true}, hides the usage line
	 */
	public void setHideUsage(boolean hideUsage) {
		this.hideUsage = hideUsage;
	}
	
	/**
	 * Exits this command line utility with an error.
	 * 
	 * @param message the error message
	 */
	public void fail(String... message) {
		throw new FrameworkException(String.join(System.lineSeparator(), message));
	}
	
	/**
	 * Creates and returns a writer for printing output from this command line utility.  The caller is responsible for
	 * closing this writer.
	 * 
	 * @return the writer
	 */
	protected PrintWriter createOutputWriter() {
		return new PrintWriter(System.out);
		
	}
	
	/**
	 * Creates and returns a writer for printing output from this command line utility.  The caller is responsible for
	 * closing this writer.
	 * 
	 * @param filename the filename, or {@code null}
	 * @return the writer
	 * @throws FileNotFoundException if opening the file for writing failed
	 */
	protected PrintWriter createOutputWriter(String filename) throws FileNotFoundException {
		return createOutputWriter(filename == null ? null : new File(filename));
	}
	
	/**
	 * Creates and returns a writer for printing output from this command line utility.  The caller is responsible for
	 * closing this writer.
	 * 
	 * @param file the file, or {@code null}
	 * @return the writer
	 * @throws FileNotFoundException if opening the file for writing failed
	 */
	protected PrintWriter createOutputWriter(File file) throws FileNotFoundException {
		return file == null ? new PrintWriter(System.out) : new PrintWriter(file);
	}

}
