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
package org.moeaframework.util.io;

import java.io.File;
import java.text.MessageFormat;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating a file could not be validated.
 */
public class ValidationException extends FrameworkException {

	private static final long serialVersionUID = -7383134236871115442L;

	/**
	 * The file that could not be validated.
	 */
	private final File file;

	/**
	 * Constructs an exception indicating the specified file could not be
	 * validated.
	 * 
	 * @param file the file that could not be validated
	 * @param message optional message providing additional details, set to
	 *        {@code null} if omitted
	 */
	public ValidationException(File file, String message) {
		super(MessageFormat.format("{0} ({1})", (message == null ? 
				"file validation failed" : message), file));
		this.file = file;
	}

	/**
	 * Returns the file that could not be validated.
	 * 
	 * @return the file that could not be validated
	 */
	public File getFile() {
		return file;
	}

}
