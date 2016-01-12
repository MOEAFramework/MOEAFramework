/* Copyright 2009-2016 David Hadka
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;

/**
 * Redirects all content received by an {@link InputStream} to an
 * {@link OutputStream}. This thread terminates when the input stream is closed
 * or the end of file is reached. The output stream is never closed.
 */
public class RedirectStream extends Thread {

	/**
	 * The input stream whose content is redirected to the output stream.
	 */
	private final InputStream inputStream;

	/**
	 * The output stream to which the content is written.
	 */
	private final OutputStream outputStream;

	/**
	 * Constructs a thread for reading the contents out of the specified input
	 * stream. The contents are deleted and are not redirected anywhere.
	 * 
	 * @param inputStream the input stream from which content is read
	 */
	private RedirectStream(InputStream inputStream) {
		this(inputStream, null);
	}

	/**
	 * Constructs a thread for redirecting the contents of the specified input
	 * stream to the specified output stream.
	 * 
	 * @param inputStream the input stream from which content is read
	 * @param outputStream the output stream to which the content is redirected
	 */
	private RedirectStream(InputStream inputStream, OutputStream outputStream) {
		super();
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	@Override
	public void run() {
		try {
			try {
				byte[] buffer = new byte[Settings.BUFFER_SIZE];
				int len;

				while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
					if (outputStream != null) {
						outputStream.write(buffer, 0, len);
					}
				}
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	/**
	 * Reads all the contents of the specified input stream. The contents are
	 * immediately deleted and are not redirected anywhere.
	 * 
	 * @param inputStream the input stream from which content is read
	 */
	public static void redirect(InputStream inputStream) {
		new RedirectStream(inputStream).start();
	}

	/**
	 * Redirects all the contents from the specified input stream to the
	 * specified output stream.
	 * 
	 * @param inputStream the input stream from which content is read
	 * @param outputStream the output stream to which the content is redirected
	 */
	public static void redirect(InputStream inputStream,
			OutputStream outputStream) {
		new RedirectStream(inputStream, outputStream).start();
	}

}
