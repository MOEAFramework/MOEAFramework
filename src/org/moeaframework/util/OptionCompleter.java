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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for auto-completion, finding the closest matching unique option
 * from a set of options. For instance,
 * 
 * <pre>
 * 	OptionCompleter completer = new OptionCompleter();
 * 	completer.add(&quot;subset&quot;);
 * 	completer.add(&quot;superset&quot;);
 * 	completer.lookup(&quot;sub&quot;); // returns &quot;subset&quot;
 * 	completer.lookup(&quot;sup&quot;); // returns &quot;superset&quot;
 * 	completer.lookup(&quot;s&quot;); // returns null, since both subset and superset match
 * 	completer.lookup(&quot;k&quot;); // returns null, no matches
 * </pre>
 */
public class OptionCompleter {

	/**
	 * Collection of options. The map serves to ensure each option is unique
	 * and caches the value of the option in lowercase.
	 */
	private final Map<String, String> options;

	/**
	 * Constructs a new, empty option auto-completer.
	 */
	public OptionCompleter() {
		super();

		options = new HashMap<String, String>();
	}

	/**
	 * Constructs a new option auto-completer initialized to recognize the 
	 * specified options.
	 * 
	 * @param options the initial options
	 */
	public OptionCompleter(String... options) {
		this();

		for (String option : options) {
			add(option);
		}
	}

	/**
	 * Constructs a new option auto-completer initialized to recognize the 
	 * specified options.
	 * 
	 * @param options the initial options
	 */
	public OptionCompleter(Collection<String> options) {
		this();

		for (String option : options) {
			add(option);
		}
	}

	/**
	 * Adds an option to this {@code OptionCompleter}. Duplicate options are
	 * ignored.
	 * 
	 * @param option the option
	 */
	public void add(String option) {
		options.put(option, option.toLowerCase());
	}

	/**
	 * Returns the closest matching unique option from the set of options stored
	 * in this {@code OptionCompleter}. Returns {@code null} if no options
	 * matched or more than one option matched.
	 * 
	 * @param partial the partial/complete option
	 * @return the closest matching option, or {@code null} if no options
	 *         matched or more than one option matched
	 */
	public String lookup(String partial) {
		String result = null;

		// cast to lower case for case-insensitive matching
		partial = partial.toLowerCase();

		for (Map.Entry<String, String> option : options.entrySet()) {
			if (option.getValue().startsWith(partial)) {
				if (result == null) {
					result = option.getKey();
				} else {
					// more than one potential options matched
					return null;
				}
			}
		}

		return result;
	}

}
