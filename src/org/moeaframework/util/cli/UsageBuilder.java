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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.Iterators.IndexedValue;

/**
 * Builds and formats usage strings as shown in help messages.
 */
class UsageBuilder {

	private String executable;

	private Class<?> mainClass;

	private List<String> commands;

	private Options options;

	private List<String> positionalArgs;

	private OptionStyle optionStyle;

	public UsageBuilder() {
		super();
		commands = new ArrayList<>();
		positionalArgs = new ArrayList<>();
		optionStyle = OptionStyle.NONE;
	}

	/**
	 * Sets the executable used to start Java.  By default, this is a generic {@code java} command, but can be
	 * overridden if a different command is used.
	 * 
	 * @param executable the executable string
	 * @return this usage builder
	 */
	public UsageBuilder executable(String executable) {
		this.executable = executable;
		return this;
	}

	/**
	 * Sets the Java class defining the main method, typically extending from {@link CommandLineUtility}, that is being
	 * invoked.
	 * 
	 * @param mainClass the class defining the main method being invoked
	 * @return this usage builder
	 */
	public UsageBuilder mainClass(Class<?> mainClass) {
		this.mainClass = mainClass;
		return this;
	}

	/**
	 * Sets the commands, if supported, that were called.  Each command is appended to any previously-added commands.
	 * 
	 * @param command the command name
	 * @return this usage builder
	 */
	public UsageBuilder command(String command) {
		commands.add(command);
		return this;
	}
	
	/**
	 * Sets the commands, if supported, that were called.  Each command is appended to any previously-added commands.
	 * 
	 * @param command the command name
	 * @return this usage builder
	 */
	public UsageBuilder command(Command command) {
		return command(command.getName());
	}

	/**
	 * Sets the supported options.
	 * 
	 * @param options the options
	 * @return this usage builder
	 */
	public UsageBuilder options(Options options) {
		this.options = options;
		return this;
	}
	
	/**
	 * Sets the style for formatting options.
	 * 
	 * @param optionStyle the style for formatting options
	 * @return this usage builder
	 */
	public UsageBuilder optionStyle(OptionStyle optionStyle) {
		this.optionStyle = optionStyle;
		return this;
	}

	/**
	 * Sets the positional arguments.  Each argument is wrapped in "<" and ">".  However, if the string starts and
	 * ends with double quotes, then the argument is displayed as-is without any additional formatting.
	 * 
	 * @param args the positional argument names
	 * @return this usage builder
	 */
	public UsageBuilder positionalArgs(String... args) {
		positionalArgs.clear();
		positionalArgs.addAll(List.of(args));
		return this;
	}

	/**
	 * Constructs the usage string.  This implementation is derived from Apache Commons CLI's {@link HelpFormatter}.
	 * 
	 * @return the usage string
	 */
	public String build() {
		StringBuilder sb = new StringBuilder();
		Set<OptionGroup> processedGroups = new HashSet<>();

		sb.append(executable != null ? executable.trim() : "");

		if (mainClass != null) {
			sb.append(" ");
			sb.append(mainClass.getName());
		}

		for (String command : commands) {
			sb.append(" ");
			sb.append(command.trim());
		}

		if (options != null) {
			List<Option> sortedOptions = new ArrayList<>(options.getOptions());
			sortedOptions.sort(new HelpFormatter().getOptionComparator());

			if (optionStyle.equals(OptionStyle.NONE)) {
				if (!sortedOptions.isEmpty()) {
					sb.append(" [options]");
				}
			} else {
				for (Option option : sortedOptions) {			
					OptionGroup group = options.getOptionGroup(option);
					
					if (optionStyle.equals(OptionStyle.REQUIRED_ONLY) &&
							(!option.isRequired() || (group != null && !group.isRequired()))) {
						continue;
					}

					if (group == null) {
						sb.append(" ");
						sb.append(formatOption(option, option.isRequired()));
					} else if (!processedGroups.contains(group)) {
						processedGroups.add(group);
						sb.append(" ");
						sb.append(formatOptionGroup(group));
					}
				}
			}
		}

		for (String arg : positionalArgs) {
			if (arg.startsWith("\"") && arg.endsWith("\"")) {
				sb.append(" ");
				sb.append(arg.substring(1, arg.length()-1));
			} else {
				sb.append(" <");
				sb.append(arg.trim());
				sb.append(">");
			}
		}

		return sb.toString();
	}

	private String formatOption(Option option, boolean required) {
		StringBuilder sb = new StringBuilder();

		if (!required) {
			sb.append("[");
		}
		if (option.getOpt() != null) {
			sb.append("-");
			sb.append(option.getOpt());
		} else {
			sb.append("--");
			sb.append(option.getLongOpt());
		}

		if (option.hasArg()) {
			sb.append(" ");
			sb.append("<");
			sb.append(option.getArgName() != null && !option.getArgName().isBlank() ? option.getArgName() : "arg");
			sb.append(">");
		}

		if (!required) {
			sb.append("]");
		}

		return sb.toString();
	}

	private String formatOptionGroup(OptionGroup group) {
		StringBuilder sb = new StringBuilder();

		if (!group.isRequired()) {
			sb.append("[");
		}

		for (IndexedValue<Option> option : Iterators.enumerate(group.getOptions())) {
			if (option.getIndex() > 0) {
				sb.append(" | ");
			}

			sb.append(formatOption(option.getValue(), true));
		}

		if (!group.isRequired()) {
			sb.append("]");
		}

		return sb.toString();
	}

}
