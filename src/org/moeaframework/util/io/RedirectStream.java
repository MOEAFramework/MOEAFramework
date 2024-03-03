/* Copyright 2009-2024 David Hadka
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

import java.io.ByteArrayOutputStream;
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
	public static void redirect(InputStream inputStream, OutputStream outputStream) {
		new RedirectStream(inputStream, outputStream).start();
	}
	
	/**
	 * Invokes the process and captures the output.  Any error messages are sent to {@link System#err}.
	 * 
	 * @param processBuilder the process builder
	 * @return the captured output
	 * @throws IOException if an I/O error occurred while running the process
	 * @throws InterruptedException if this thread was interrupted while waiting for the process to terminate
	 */
	public static String capture(ProcessBuilder processBuilder) throws IOException, InterruptedException {
		try (OutputStream out = new ByteArrayOutputStream()) {
			pipe(processBuilder, out);
			return out.toString();
		}
	}
	
	/**
	 * Invokes the process and pipes output to the given stream.  Any error messages are sent to {@link System#err}.
	 * 
	 * @param processBuilder the process builder
	 * @param out the stream where output is piped
	 * @throws IOException if an I/O error occurred while running the process
	 * @throws InterruptedException if this thread was interrupted while waiting for the process to terminate
	 */
	public static void pipe(ProcessBuilder processBuilder, OutputStream out) throws IOException,
	InterruptedException {
		pipe(processBuilder, out, System.err);
	}
	
	/**
	 * Invokes the process and pipes output and error messages to the given streams.
	 * 
	 * @param processBuilder the process builder
	 * @param out the stream where output is piped
	 * @param err the stream where error messages are piped
	 * @throws IOException if an I/O error occurred while running the process
	 * @throws InterruptedException if this thread was interrupted while waiting for the process to terminate
	 */
	public static void pipe(ProcessBuilder processBuilder, OutputStream out, OutputStream err) throws IOException,
	InterruptedException {
		Process process = processBuilder.start();
		
		RedirectStream.redirect(process.getInputStream(), out);
		RedirectStream.redirect(process.getErrorStream(), err);
		
		if (process.waitFor() != 0) {
			throw new IOException("Process exited with non-zero status (" + process.exitValue() + ")");
		}
	}
	
	/**
	 * Invokes the process and pipes output and error messages to {@link System#out} and {@link System#err},
	 * respectively.
	 * 
	 * @param processBuilder the process builder
	 * @throws IOException if an I/O error occurred while running the process
	 * @throws InterruptedException if this thread was interrupted while waiting for the process to terminate
	 */
	public static void invoke(ProcessBuilder processBuilder) throws IOException, InterruptedException {
		pipe(processBuilder, System.out, System.err);
	}

}
