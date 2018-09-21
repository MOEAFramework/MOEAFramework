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
package org.moeaframework.util.grammar;

import java.text.MessageFormat;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating an error while parsing or processing grammars.
 */
public class GrammarException extends FrameworkException {

	private static final long serialVersionUID = 4573222148419061915L;
	
	/**
	 * Constructs an exception indicating an error while parsing or processing 
	 * grammars.
	 * 
	 * @param message the error message
	 */
	public GrammarException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception indicating an error while parsing or processing 
	 * grammars.
	 * 
	 * @param message the error message
	 * @param line the line number on which the error occurred
	 */
	public GrammarException(String message, int line) {
		super(MessageFormat.format("{0} (line {1})", message, line));
	}

}
