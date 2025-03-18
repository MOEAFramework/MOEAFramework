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
package org.moeaframework.util.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.util.Localization;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.io.RedirectStream;
import org.moeaframework.util.io.Tokenizer;

/**
 * Abstract class for providing command line utilities.  This class is provided to ensure a standard interface for
 * command line utilities as well as handling the quirks of different operating systems.
 * <p>
 * By default, Unix-style CLIs are supported where options are passed in using either the single character short form
 * (e.g., {@code -h}) or the long form (e.g., {@code --help}.  Alternatively, if any commands are specified, the CLI
 * behaves more like the Git CLI, where the first positional argument specifies the command to execute.  Each command
 * simply invokes its underlying implementation, which is itself another command line utility.
 * <p>
 * Implementations should provide a main method that invokes {@link #start(String[])}.  Any unhandled exception should
 * be propagated out of the main method, allowing the CLI to exit with a non-zero status code.
 */
public abstract class CommandLineUtility {
	
	/**
	 * Exception handler for formatting and printing the error message to the command line.
	 */
	private static class CommandLineUncaughtExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			System.err.println("ERROR: " + e.toString());
			
			if (Settings.isVerbose()) {
				System.err.println("Full stack trace:");
				e.printStackTrace(System.err);
			}
		}

	}
	
	/**
	 * Builder used to construct the usage message.
	 */
	private UsageBuilder usageBuilder;
	
	/**
	 * Formatter for help messages.
	 */
	private final HelpFormatter helpFormatter;
	
	/**
	 * When {@code true}, prompts to confirm an operation are skipped.
	 */
	private boolean acceptConfirmations;

	/**
	 * Constructs a command line utility.  The constructor for subclasses should provide a private constructor unless
	 * programmatic access is also desired.
	 */
	public CommandLineUtility() {
		super();
		usageBuilder = new UsageBuilder();
		
		helpFormatter = new HelpFormatter();
		helpFormatter.setLeftPadding(2);
	}
	
	/**
	 * Returns the console width by:
	 * <ol>
	 *   <li>The value stored in {@link Settings#KEY_HELP_WIDTH}
	 *   <li>Invoking {@code stty size} on supported platforms
	 *   <li>Defaulting to {@value HelpFormatter#DEFAULT_WIDTH}
	 * </ol>
	 * 
	 * @return the console width
	 */
	private int getConsoleWidth() {
		int width = Settings.PROPERTIES.getInt(Settings.KEY_HELP_WIDTH, -1);
		
		if (width <= 0) {
			if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
				try {
					ProcessBuilder processBuilder = new ProcessBuilder("stty", "size");
					processBuilder.redirectInput(Redirect.INHERIT);
					processBuilder.redirectError(Redirect.DISCARD);

					String output = RedirectStream.capture(processBuilder);
					Tokenizer tokenizer = new Tokenizer();
					width = Integer.parseInt(tokenizer.decodeToArray(output.trim())[1]);
				} catch (Exception e) {
					if (Settings.isVerbose()) {
						System.err.println("Unable to detect console width!");
						e.printStackTrace();
					}
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
	 * Return the commands made available by this command line utility.  The base implementation returns an empty list,
	 * indicating no commands are configured.
	 * 
	 * @return the commands
	 */
	public List<Command> getCommands() {
		return new ArrayList<>();
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
		
		if (System.getProperty("cli.executable") != null) {
			usageBuilder.withExecutable(System.getProperty("cli.executable"));
		} else {
			usageBuilder.withUtility(getClass());
		}
		
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
	 * Finds the matching command and invokes its {@link #start(String[])} method.  Implementations are expected to
	 * call this from their {@link #run(CommandLine)} method after processing any options.
	 * 
	 * @param commandLine the parsed command line
	 * @throws Exception if any exception occurred while running this command
	 */
	protected void runCommand(CommandLine commandLine) throws Exception {
		String[] args = commandLine.getArgs();
		List<Command> commands = getCommands();
		
		if (args.length < 1) {
			throw new ParseException("No command specified, use --help to see available options");
		}
		
		OptionCompleter completer = new OptionCompleter(commands.stream().map(Command::getName).toList());
		String match = completer.lookup(args[0]);
				
		if (match == null) {
			throw new ParseException("'" + args[0] + "' is not a valid command, use --help to see available options");
		}
		
		Command command = commands.stream().filter(x -> x.getName().equals(match)).findFirst().get();
		String[] commandArgs = Arrays.copyOfRange(commandLine.getArgs(), 1, commandLine.getArgs().length);
		
		Constructor<? extends CommandLineUtility> constructor = command.getImplementation().getDeclaredConstructor();
		constructor.setAccessible(true);
			
		CommandLineUtility commandInstance = constructor.newInstance();
		commandInstance.setUsageBuilder(usageBuilder.withCommand(command.getName()));
		commandInstance.start(commandArgs);
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
		int width = getConsoleWidth();
		Options options = getLocalizedOptions();
		List<Command> commands = getCommands();
		
		try (PrintWriter writer = createOutputWriter()) {
			helpFormatter.printWrapped(writer, width, Localization.getString(getClass(), "title"));
			writer.println();
			
			String usagePrefix = Localization.getString(CommandLineUtility.class, "usage");
			helpFormatter.printWrapped(writer, width, usagePrefix.length() + 1, usagePrefix + " " + getUsageString());
			writer.println();

			if (Localization.containsKey(getClass(), "description")) {
				helpFormatter.printWrapped(writer, width, Localization.getString(getClass(), "description"));
				writer.println();
			}
			
			if (!commands.isEmpty()) {
				helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "commands"));
				writer.println();
				
				Options commandOptions = new Options();

				for (Command command : commands) {
					if (command.isVisible()) {
						commandOptions.addOption(command.getName(),
								Localization.getString(command.getImplementation(), "title"));
					}
				}
				
				helpFormatter.setOptPrefix("");
				helpFormatter.printOptions(writer, width, commandOptions, helpFormatter.getLeftPadding(),
						helpFormatter.getDescPadding());
				helpFormatter.setOptPrefix(HelpFormatter.DEFAULT_OPT_PREFIX);

				writer.println();
			}
			
			helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "header"));
			writer.println();
			
			helpFormatter.printOptions(writer, width, options, helpFormatter.getLeftPadding(), helpFormatter.getDescPadding());
			writer.println();
			
			helpFormatter.printWrapped(writer, width, Localization.getString(CommandLineUtility.class, "footer"));
		}
	}
	
	/**
	 * Sets the usage builder used to create and format the usage string.
	 * 
	 * @param usageBuilder the usage builder
	 */
	protected void setUsageBuilder(UsageBuilder usageBuilder) {
		this.usageBuilder = usageBuilder;
	}
	
	/**
	 * Returns the formatted usage string.
	 * 
	 * @return the formatted usage string
	 */
	protected String getUsageString() {
		if (Localization.containsKey(getClass(), "args")) {
			usageBuilder.withPositionalArgs(Localization.getString(getClass(), "args").split(","));
		}
		
		usageBuilder.withOptions(getOptions());
		
		return usageBuilder.build();
	}
	
	/**
	 * Sets the flag to hide and automatically respond to confirmation (yes/no) prompts.
	 * 
	 * @param acceptConfirmations if {@code true}, accept all confirmation prompts
	 */
	public void setAcceptConfirmations(boolean acceptConfirmations) {
		this.acceptConfirmations = acceptConfirmations;
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
	 * Display a confirmation (yes/no) prompt to the user and wait for user input.
	 * 
	 * @param prompt the prompt message
	 * @return {@code true} if the prompt was approved; {@code false} otherwise
	 */
	protected boolean prompt(String prompt) {
		if (acceptConfirmations) {
			return true;
		}
		
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print(prompt + " [y/N] ");
			String response = scanner.nextLine();
			return response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes");
		}
	}
	
	/**
	 * Creates and returns a writer for printing output from this command line utility.  The caller is responsible for
	 * closing this writer.
	 * 
	 * @return the writer
	 */
	protected PrintWriter createOutputWriter() {
		return new PrintWriter(CloseShieldOutputStream.wrap(System.out), true);
		
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
		return file == null ? createOutputWriter() : new PrintWriter(file);
	}

}
