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
package org.moeaframework.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Utility for auto-completion, finding the closest matching unique option from a set of options.  For instance,
 * <pre>{@code
 *   OptionCompleter completer = new OptionCompleter();
 *   completer.add("subset");
 *   completer.add("superset");
 *   completer.lookup("sub");   // returns "subset"
 *   completer.lookup("sup");   // returns "superset"
 *   completer.lookup("s");     // returns null, since both subset and superset match
 *   completer.lookup("k");     // returns null, no matches
 * }</pre>
 */
public class OptionCompleter {

	/**
	 * Collection of the unique options stored in a case-insensitive manner.
	 */
	private final TreeSet<String> options;

	/**
	 * Constructs a new, empty option auto-completer.
	 */
	public OptionCompleter() {
		super();

		options = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	}

	/**
	 * Constructs a new option auto-completer initialized to recognize the specified options.
	 * 
	 * @param options the initial options
	 */
	public OptionCompleter(String... options) {
		this(List.of(options));
	}

	/**
	 * Constructs a new option auto-completer initialized to recognize the specified options.
	 * 
	 * @param options the initial options
	 */
	public OptionCompleter(Collection<String> options) {
		this();
		
		addAll(options);
	}
	
	/**
	 * Constructs a new option auto-completer initialized to recognize the enumerated values.  Note that the enum
	 * constants are converted into their string representations, and likewise the returned value of
	 * {@link #lookup(String)} will be the string representation.  Use {@link Enum#valueOf(Class, String)} to convert
	 * back into the enumeration, if required.
	 * 
	 * @param <T> the enum type
	 * @param options the enum class
	 */
	public <T extends Enum<?>> OptionCompleter(Class<T> options) {
		this();
		
		for (T option : options.getEnumConstants()) {
			add(option.name());
		}
	}

	/**
	 * Adds an option to this {@code OptionCompleter}.  Duplicate options are ignored.
	 * 
	 * @param option the option
	 */
	public void add(String option) {
		options.add(option);
	}
	
	/**
	 * Adds the given options to this {@code OptionCompleter}.  Duplicate options are ignored.
	 * 
	 * @param options the options
	 */
	public void addAll(Collection<String> options) {
		this.options.addAll(options);
	}
	
	/**
	 * Returns the supported options.
	 * 
	 * @return the supported options
	 */
	public String[] getOptions() {
		return options.toArray(String[]::new);
	}

	/**
	 * Returns the closest matching unique option from the set of options stored in this {@code OptionCompleter}.
	 * Returns {@code null} if no options matched or more than one option matched.
	 * 
	 * @param partial the partial/complete option
	 * @return the closest matching option, or {@code null} if no options matched or more than one option matched
	 */
	public String lookup(String partial) {
		String result = null;
		Comparator<? super String> comparator = options.comparator();
		
		for (String option : options) {
			String optionPrefix = option.substring(0, Math.min(option.length(), partial.length()));
			
			if (comparator.compare(optionPrefix, partial) == 0) {
				if (optionPrefix.length() == option.length()) {
					// exact matches are immediately returned
					return option;
				} else if (result == null) {
					// partial matches are tracked in case there are duplicates
					result = option;
				} else {
					// multiple partial matches found
					return null;
				}
			}
		}

		return result;
	}

}
