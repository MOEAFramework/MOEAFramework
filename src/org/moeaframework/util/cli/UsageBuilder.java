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
 * <p>
 * This is based on the Apache Commons CLI {@link HelpFormatter#printUsage(java.io.PrintWriter, int, String, Options)}
 * implementation, extended to allow listing subcommands and positional arguments.
 */
class UsageBuilder {

	private String executable;

	private Class<? extends CommandLineUtility> utility;
	
	private List<String> commands;

	private Options options;

	private List<String> positionalArgs;
	
	public UsageBuilder() {
		super();
		executable = "java -classpath \"lib/*\"";
		commands = new ArrayList<>();
		positionalArgs = new ArrayList<>();
	}
	
	public UsageBuilder withExecutable(String executable) {
		this.executable = executable;
		return this;
	}
	
	public UsageBuilder withUtility(Class<? extends CommandLineUtility> utility) {
		this.utility = utility;
		return this;
	}
	
	public UsageBuilder withCommand(String command) {
		commands.add(command);
		return this;
	}
	
	public UsageBuilder withOptions(Options options) {
		this.options = options;
		return this;
	}
	
	public UsageBuilder withPositionalArgs(String... args) {
		positionalArgs.clear();
		positionalArgs.addAll(List.of(args));
		return this;
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		Set<OptionGroup> processedGroups = new HashSet<>();
		
		sb.append(executable != null ? executable.trim() : "");
		
		if (utility != null) {
			sb.append(" ");
			sb.append(utility.getName());
		}
		
		for (String command : commands) {
			sb.append(" ");
			sb.append(command.trim());
		}
		
		List<Option> sortedOptions = new ArrayList<>(options.getOptions());
		sortedOptions.sort(new HelpFormatter().getOptionComparator());

		for (Option option : sortedOptions) {			
			OptionGroup group = options.getOptionGroup(option);
			
			if (group == null) {
				sb.append(" ");
				sb.append(formatOption(option, option.isRequired()));
			} else if (!processedGroups.contains(group)) {
				processedGroups.add(group);
				sb.append(" ");
				sb.append(formatOptionGroup(group));
			}
		}
		
		for (String arg : positionalArgs) {
			sb.append(" <");
			sb.append(arg.trim());
			sb.append(">");
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
