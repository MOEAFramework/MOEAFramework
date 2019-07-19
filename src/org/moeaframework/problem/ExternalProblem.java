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
 * Evaluate solutions using an externally-defined problem.  Two modes of
 * operation are supported: standard I/O and sockets.
 * 
 * <h4>Standard I/O Mode</h4>
 * Standard I/O is the easiest mode to setup and run.  First, an executable
 * program on the computer is launched by invoking the constructor with the
 * program name (and any optional arguments):
 * <pre>
 *   new ExternalProblem("./problem.exe", "arg1", "arg2") { ... }
 * </pre>
 * Then, solutions are sent to the process on its standard input (stdin) stream,
 * and the objectives and constraints are read from its standard output (stdout)
 * stream.
 * <p>
 * The program can not use the standard I/O for any other purpose.  Programs
 * which read from or write to the standard I/O streams should instead use
 * sockets, as discussed below.
 * 
 * <h4>Socket Mode</h4>
 * Socket mode is more complicated to setup, but is more flexible and robust.
 * It has the ability to not only evaluate the problem on the host computer,
 * but can be spread across a computer network.  To use sockets, use either the
 * {@link #ExternalProblem(String, int)} or
 * {@link #ExternalProblem(InetAddress, int)} constructor.
 * 
 * <h4>C/C++ Interface</h4>
 * A C/C++ interface is provided for implementing problems.  This interface
 * supports both modes of communication, depending on which initialization
 * routine is invoked.  See the {@code moeaframework.c} and
 * {@code moeaframework.h} files in the {@code examples/} folder for details.
 * This interface conforms to the communication protocol described below.
 * <p>
 * The communication protocol consists of sending decision variables to the
 * external process, and the process responding with the objectives and
 * constraints.  The decision variables line consists of one or more variables
 * separated by whitespace and terminated by a newline. The process evaluates
 * the problem for the given variables and outputs the objectives separated by
 * whitespace and terminated by a newline. If the problem also has constraints,
 * each constraint is returned after the objectives on the same line.  The
 * process must only terminate when the end of stream is reached. In addition,
 * the process should flush the output stream to ensure the output is processed
 * immediately.
 * <p>
 * Whitespace is one or more spaces, tabs or any combination thereof. The
 * newline is either the line feed ('\n'), carriage return ('\r') or a carriage
 * return followed immediately by a line feed ("\r\n"). 
 * <p>
 * <b>It is critical that the {@link #close()} method be invoked to ensure the
 * external process is shutdown cleanly.</b>
 */
public abstract class ExternalProblem implements Problem {
	
	/**
	 * The default port used by the MOEA Framework to connect to remote
	 * evaluation processes via sockets.
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
	 * Constructs an external problem using {@code new
	 * ProcessBuilder(command).start()}.  If the command contains arguments,
	 * the arguments should be passed in as separate strings, such as
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
	 * Constructs an external problem that connects to a remote process via
	 * sockets.  The remote process should be instantiated and already
	 * listening to the designated port number prior to invoking this 
	 * constructor.
	 * 
	 * @param host the host name of the remote system; or {@code null} to use
	 *        the local host
	 * @param port the port number
	 * @throws UnknownHostException if the IP address of the specified host
	 *         could not be determined
	 * @throws IOException if an I/O error occurred
	 */
	public ExternalProblem(String host, int port) throws IOException, 
	UnknownHostException {
		this(new Socket(host, port));
	}
	
	/**
	 * Constructs an external problem that connects to a remote process via
	 * sockets.  The remote process should be instantiated and already
	 * listening to the designated port number prior to invoking this
	 * constructor.
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
	ExternalProblem(Socket socket) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream());
	}

	/**
	 * Constructs an external problem using the specified process.
	 * 
	 * @param process the process used to evaluate solutions
	 */
	ExternalProblem(Process process) {
		this(process.getInputStream(), process.getOutputStream());
		RedirectStream.redirect(process.getErrorStream(), System.err);
		
		this.process = process;
	}
	
	/**
	 * Constructs an external problem using the specified input and output 
	 * streams.
	 * 
	 * @param input the input stream
	 * @param output the output stream
	 */
	ExternalProblem(InputStream input, OutputStream output) {
		super();
		reader = new BufferedReader(new InputStreamReader(input));
		writer = new BufferedWriter(new OutputStreamWriter(output));
		
		if (Settings.getExternalProblemDebuggingEnabled()) {
			setDebugStream(System.out);
		}
	}
	
	/**
	 * Sets the output stream used to write debugging information.  If
	 * {@code null}, disables debugging.  The debug stream is not closed
	 * by this class and must be managed by the caller.
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
	 * Closes the connection to the process. No further invocations of
	 * {@code evaluate} are permitted.
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
	 * Evaluates the specified solution using the process defined by this class'
	 * constructor.
	 * 
	 * @param solution the solution to evaluate
	 */
	@Override
	public synchronized void evaluate(Solution solution) 
	throws ProblemException {
		BufferedWriter debug = this.debug;
		
		// send variables to external process
		try {
			StringBuilder sb = new StringBuilder();
			
			sb.append(encode(solution.getVariable(0)));
			
			for (int i = 1; i < solution.getNumberOfVariables(); i++) {
				sb.append(" ");
				sb.append(encode(solution.getVariable(i)));
			}
			
			sb.append(Settings.NEW_LINE);
			
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
			
			if (tokens.length != (solution.getNumberOfObjectives() + 
					solution.getNumberOfConstraints())) {
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
	 * Serializes a variable to a string form.
	 * 
	 * @param variable the variable whose value is serialized
	 * @return the serialized version of the variable
	 * @throws IOException if an error occurs during serialization
	 */
	private String encode(Variable variable) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		if (variable instanceof RealVariable) {
			RealVariable rv = (RealVariable)variable;
			sb.append(rv.getValue());
		} else if (variable instanceof BinaryVariable) {
			BinaryVariable bv = (BinaryVariable)variable;
			
			for (int i=0; i<bv.getNumberOfBits(); i++) {
				sb.append(bv.get(i) ? "1" : "0");
			}
		} else if (variable instanceof Permutation) {
			Permutation p = (Permutation)variable;

			for (int i=0; i<p.size(); i++) {
				if (i > 0) {
					sb.append(',');
				}
				
				sb.append(p.get(i));
			}
		} else {
			throw new IOException("unable to serialize variable");
		}
		
		return sb.toString();
	}

}
