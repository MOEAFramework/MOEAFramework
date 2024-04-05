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
package org.moeaframework.problem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.RedirectStream;

/**
 * Evaluate solutions using an externally-defined problem.  Two modes of operation are supported: standard I/O and
 * sockets.
 * 
 * <h2>Standard I/O Mode</h2>
 * Standard I/O is the easiest mode to setup and run.  First, an executable program on the computer is launched by
 * invoking the constructor with the program name (and any optional arguments):
 * <pre>
 *   new ExternalProblem("./problem.exe", "arg1", "arg2") { ... }
 * </pre>
 * Then, solutions are sent to the process on its standard input (stdin) stream, and the objectives and constraints
 * are read from its standard output (stdout) stream.  Writing or reading any other content from these streams will
 * interfere with the communication.  Consider using sockets, as discussed below, if the program already uses standard
 * input / output for other purposes.
 * 
 * <h2>Socket Mode</h2>
 * Socket mode is more complicated to setup, but is more flexible and robust.  It has the ability to not only evaluate
 * the problem on the host computer, but can be spread across a computer network.  To use sockets, use either the
 * {@link #ExternalProblem(String, int)} or {@link #ExternalProblem(InetAddress, int)} constructor.
 * 
 * <h2>C/C++ Interface</h2>
 * A C/C++ interface is provided for implementing problems.  This interface supports both modes of communication,
 * depending on which initialization routine is invoked.  See the {@code moeaframework.c} and {@code moeaframework.h}
 * files in the {@code examples/} folder for details.  This interface conforms to the communication protocol described
 * below.
 * <p>
 * The communication protocol consists of sending decision variables to the external process, and the process
 * responding with the objectives and constraints.  First, the MOEA Framework writes a line containing the decision
 * variables separated by whitespace and terminated by the newline character.  The program should read this line
 * from stdin, then write the objectives and constraints, if any, to stdout.  The objectives and constraints must also
 * appear on a single line separated by whitespace.
 * <p>
 * The program must continue processing lines until the input stream is closed.  In addition, it should always flush
 * the output stream after writing each line.  Otherwise, some systems may buffer the data causing the program to
 * stall.
 * <p>
 * Whitespace is one or more spaces, tabs or any combination thereof.  The newline is either the line feed ('\n'),
 * carriage return ('\r') or a carriage return followed immediately by a line feed ("\r\n"). 
 * <p>
 * <b>It is critical that the {@link #close()} method be invoked to ensure the external process is shutdown cleanly.</b>
 * Failure to do so could leave the process running in the background.
 */
public abstract class ExternalProblem implements Problem {
	
	/**
	 * The default port used by the MOEA Framework to connect to remote evaluation processes via sockets.
	 */
	public static final int DEFAULT_PORT = 16801;

	/**
	 * Reader connected to the process' standard output.
	 */
	private final BufferedReader reader;

	/**
	 * Writer connected to the process' standard input.
	 */
	private final BufferedWriter writer;
	
	/**
	 * Writer for debugging messages.
	 */
	private BufferedWriter debug;
	
	/**
	 * The process, or {@code null} if no process was started.
	 */
	private Process process;

	/**
	 * Constructs an external problem using {@code new ProcessBuilder(command).start()}.  If the command contains
	 * arguments, the arguments should be passed in as separate strings, such as
	 * <pre>
	 *   new ExternalProblem("command", "arg1", "arg2");
	 * </pre>
	 * 
	 * @param command a specified system command
	 * @throws IOException if an I/O error occured
	 */
	public ExternalProblem(String... command) throws IOException {
		this(new ProcessBuilder(command).start());
	}
	
	/**
	 * Constructs an external problem that connects to a remote process via sockets.  The remote process should be
	 * instantiated and already listening to the designated port number prior to invoking this constructor.
	 * 
	 * @param host the host name of the remote system; or {@code null} to use the local host
	 * @param port the port number
	 * @throws UnknownHostException if the IP address of the specified host could not be determined
	 * @throws IOException if an I/O error occurred
	 */
	public ExternalProblem(String host, int port) throws IOException, UnknownHostException {
		this(new Socket(host, port));
	}
	
	/**
	 * Constructs an external problem that connects to a remote process via sockets.  The remote process should be
	 * instantiated and already listening to the designated port number prior to invoking this constructor.
	 * 
	 * @param address the IP address of the remote system
	 * @param port the port number
	 * @throws IOException if an I/O error occurred
	 */
	public ExternalProblem(InetAddress address, int port) throws IOException {
		this(new Socket(address, port));
	}
	
	/**
	 * Constructs an external problem using the specified socket.
	 * 
	 * @param socket the socket used to send solutions to be evaluated
	 * @throws IOException if an I/O error occurred
	 */
	protected ExternalProblem(Socket socket) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream());
	}

	/**
	 * Constructs an external problem using the specified process.
	 * 
	 * @param process the process used to evaluate solutions
	 */
	protected ExternalProblem(Process process) {
		this(process.getInputStream(), process.getOutputStream());
		RedirectStream.redirect(process.getErrorStream(), System.err);
		
		this.process = process;
	}
	
	/**
	 * Constructs an external problem using the specified input and output streams.
	 * 
	 * @param input the input stream
	 * @param output the output stream
	 */
	protected ExternalProblem(InputStream input, OutputStream output) {
		super();
		reader = new BufferedReader(new InputStreamReader(input));
		writer = new BufferedWriter(new OutputStreamWriter(output));
		
		if (Settings.isExternalProblemDebuggingEnabled()) {
			setDebugStream(System.out);
		}
	}
	
	/**
	 * Sets the output stream used to write debugging information.  If {@code null}, disables debugging.  The debug
	 * stream is not closed by this class and must be managed by the caller.
	 * 
	 * @param stream the output stream
	 */
	public void setDebugStream(OutputStream stream) {
		if (stream == null) {
			debug = null;
		} else {
			debug = new BufferedWriter(new OutputStreamWriter(stream));
		}
	}

	/**
	 * Closes the connection to the process.  No further invocations of {@code evaluate} are permitted.
	 */
	@Override
	public synchronized void close() {
		try {
			writer.close();
		} catch (IOException e) {
			throw new ProblemException(this, e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new ProblemException(this, e);
			}
		}
	}

	/**
	 * Evaluates the specified solution using the process defined by this class' constructor.
	 * 
	 * @param solution the solution to evaluate
	 */
	@Override
	public synchronized void evaluate(Solution solution) throws ProblemException {
		BufferedWriter debug = this.debug;
		
		// send variables to external process
		try {
			StringBuilder sb = new StringBuilder();
			
			sb.append(encode(solution.getVariable(0)));
			
			for (int i = 1; i < solution.getNumberOfVariables(); i++) {
				sb.append(" ");
				sb.append(encode(solution.getVariable(i)));
			}
			
			sb.append(System.lineSeparator());
			
			if (debug != null) {
				debug.write("<< ");
				debug.write(sb.toString());
				debug.flush();
			}
			
			writer.write(sb.toString());
			writer.flush();
		} catch (IOException e) {
			throw new ProblemException(this, "error sending variables to external process", e);
		}

		// receive objectives from external process
		try {
			String line = reader.readLine();

			if (line == null) {
				if (debug != null) {
					debug.write("Reached end of stream");
					debug.newLine();
					
					if (process != null) {
						try {
							int exitCode = process.exitValue();
							debug.write("Process exited with code " + exitCode);
						} catch (IllegalThreadStateException e) {
							debug.write("Process is still alive");
						}
					}
					
					debug.flush();
				}
				
				throw new ProblemException(this, "end of stream reached when response expected");
			}

			if (debug != null) {
				debug.write(">> ");
				debug.write(line);
				debug.newLine();
				debug.flush();
			}
			
			String[] tokens = line.split("\\s+");
			
			if (tokens.length != (solution.getNumberOfObjectives() + solution.getNumberOfConstraints())) {
				throw new ProblemException(this, "response contained fewer tokens than expected");
			}
			
			int index = 0;

			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				solution.setObjective(i, Double.parseDouble(tokens[index]));
				index++;
			}
			
			for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
				solution.setConstraint(i, Double.parseDouble(tokens[index]));
				index++;
			}
		} catch (IOException e) {
			throw new ProblemException(this, "error receiving variables from external process", e);
		} catch (NumberFormatException e) {
			throw new ProblemException(this, "error receiving variables from external process", e);
		}
	}

	/**
	 * Encodes the variable in a string format sent to the program.
	 * 
	 * @param variable the variable
	 * @return the string representation of the variable
	 * @throws IOException if an error occurs during serialization
	 */
	private String encode(Variable variable) throws IOException {
		if (!(variable instanceof RealVariable ||
				variable instanceof BinaryVariable ||
				variable instanceof Permutation)) {
			throw new IOException(ExternalProblem.class.getSimpleName() + " does not support encoding type " +
					variable.getClass().getSimpleName());
		}
		
		// use toString() instead of encode() as we want to send the value
		return variable.toString();
	}

}
