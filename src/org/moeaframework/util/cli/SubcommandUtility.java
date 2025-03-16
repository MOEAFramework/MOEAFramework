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

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.util.Localization;
import org.moeaframework.util.OptionCompleter;

/**
 * Command line utility based on subcommands, where the first non-option argument is the subcommand to invoke.  All
 * arguments to the right of the command are passed on to the subcommand.
 */
public abstract class SubcommandUtility extends CommandLineUtility {

	private final Map<String, Subcommand> subcommands;

	public SubcommandUtility() {
		super();
		subcommands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		setCommandString("java -classpath \"lib/*\" " + getClass().getName() + " [command]");
	}
	
	/**
	 * Adds a subcommand to this command line utility.
	 * 
	 * @param command the subcommand
	 */
	public void add(Subcommand command) {
		subcommands.put(command.getName(), command);
	}

	Subcommand getSubcommand(CommandLine commandLine) throws ParseException {
		String[] args = commandLine.getArgs();
		
		if (args.length < 1) {
			throw new ParseException("No command specified, use --help to see available options");
		}
		
		OptionCompleter completer = new OptionCompleter(subcommands.keySet());
		String subcommand = completer.lookup(args[0]);
		
		if (subcommand == null) {
			throw new ParseException("'" + args[0] + "' is not a valid command, use --help to see available options");
		}
		
		return subcommands.get(subcommand);
	}
	
	@Override
	public void run(CommandLine commandLine) throws Exception {
		Subcommand command = getSubcommand(commandLine);
		String[] commandArgs = Arrays.copyOfRange(commandLine.getArgs(), 1, commandLine.getArgs().length);
			
		Constructor<? extends CommandLineUtility> constructor = command.getImplementation().getDeclaredConstructor();
		constructor.setAccessible(true);
			
		CommandLineUtility commandInstance = constructor.newInstance();
		commandInstance.setHideUsage(true);
		commandInstance.start(commandArgs);
	}

	@Override
	protected void showHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		int width = getConsoleWidth();
		
		Options options = new Options();
				
		for (Subcommand subcommand : subcommands.values()) {
			if (subcommand.isVisible()) {
				options.addOption(subcommand.getName(), Localization.getString(subcommand.getImplementation(), "description"));
			}
		}
		
		try (PrintWriter writer = createOutputWriter()) {
			helpFormatter.printWrapped(writer, width, Localization.getString(getClass(), "description"));
			writer.println();
			helpFormatter.printWrapped(writer, width, Localization.getString(SubcommandUtility.class, "commands"));
			writer.println();
			helpFormatter.setOptPrefix("");
			helpFormatter.printOptions(writer, width, options, helpFormatter.getLeftPadding(),
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
	 * Defines a subcommand for use by this command line utility.
	 */
	public static class Subcommand {
		
		private final String name;
		
		private final Class<? extends CommandLineUtility> implementation;
		
		private final boolean isVisible;
		
		private Subcommand(String name, Class<? extends CommandLineUtility> implementation, boolean isVisible) {
			super();
			this.name = name;
			this.implementation = implementation;
			this.isVisible = isVisible;
		}

		/**
		 * Returns the display name for this subcommand.
		 * 
		 * @return the display name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the class implementing this subcommand.
		 * 
		 * @return the class
		 */
		public Class<? extends CommandLineUtility> getImplementation() {
			return implementation;
		}
		
		/**
		 * Returns {@code true} if this subcommand is visible to users; {@code false} otherwise.
		 * 
		 * @return {@code true} if this subcommand is visible to users; {@code false} otherwise
		 */
		public boolean isVisible() {
			return isVisible;
		}
		
		public static Subcommand of(Class<? extends CommandLineUtility> implementation) {
			return of(implementation.getSimpleName(), implementation);
		}
		
		public static Subcommand of(String name, Class<? extends CommandLineUtility> implementation) {
			return new Subcommand(name, implementation, true);
		}
		
		public static Subcommand hidden(Class<? extends CommandLineUtility> implementation) {
			return hidden(implementation.getSimpleName(), implementation);
		}
		
		public static Subcommand hidden(String name, Class<? extends CommandLineUtility> implementation) {
			return new Subcommand(name, implementation, false);
		}
		
	}

}
