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
package org.moeaframework.analysis.io;

import java.io.File;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating a result file is empty, invalid, or not properly formatted.
 */
public class EmptyResultFileException extends FrameworkException {
	
	private static final long serialVersionUID = 6731718998866888363L;

	/**
	 * Constructs a new exception indicating a result file is empty.
	 */
	public EmptyResultFileException() {
		this(null);
	}
	
	/**
	 * Constructs a new exception indicating a result file is empty.
	 * 
	 * @param file the result file
	 */
	public EmptyResultFileException(File file) {
		super("Result file " + (file == null ? "" : "'" + file + "' ") + " is empty or invalid");
	}

}
