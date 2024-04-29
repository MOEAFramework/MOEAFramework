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
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.io.RedirectStream;

/**
 * Evaluate solutions using an externally-defined problem.  Two modes of operation are supported: standard I/O and
 * sockets.
 * 
 * <h2>Communication Protocol</h2>
 * Each solution is evaluated serially by writing the decision variables to the process or socket, waiting for a
 * response, and reading the objectives and constraints.  Values are formatted using the {@link Variable#toString()}
 * method and separated by whitespace, typically a single space character, and sent on a single line terminated by the
 * new line character (which depending on the platform can be {@code "\n"}, {@code "\r"}, or {@code "\r\n"}).  This
 * process repeats in a loop until all solutions are evaluated, at which point the stream is closed.  We strongly
 * recommend flushing the output stream after writing each line to prevent buffering.
 * 
 * <h2>Standard I/O</h2>
 * When using Standard I/O, a process is started and the data is transmitted over the standard input/output streams.
 * One limitation of this approach is the process can not use standard input/output for any other purpose, as that will
 * interfere with the communication.  Consider using sockets instead.
 * 
 * <h2>Sockets</h2>
 * When using Sockets, data is transmitted over the network.  Typically, this talks to a local process using a
 * specific port.  However, this can also connect to a remote process over a local-area network or the Internet.
 * 
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
	 * Builder for defining the process or connection to the external problem.
	 */
	public static class Builder {
		
		private String[] command;
		
		private File workingDirectory;
				
		private InetSocketAddress socketAddress;
		
		private InputStream inputStream;
		
		private OutputStream outputStream;
		
		private OutputStream errorStream;
		
		private PrintStream debug;
				
		private Duration connectionDelay = Duration.ofSeconds(1);
		
		private Duration shutdownTimeout = Duration.ofSeconds(10);
		
		/**
		 * Constructs a new builder.
		 */
		public Builder() {
			super();
			
			if (Settings.isExternalProblemDebuggingEnabled()) {
				withDebugging();
			} else {
				withDebugging(OutputStream.nullOutputStream());
			}
		}
		
		/**
		 * Configures this builder to start a process using the given command and optional arguments.
		 * 
		 * @param command the command and arguments
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withCommand(String... command) {
			this.command = command;
			return this;
		}
		
		/**
		 * Sets the working directory where the process is started.
		 * 
		 * @param directory the working directory
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withWorkingDirectory(File directory) {
			this.workingDirectory = directory;
			return this;
		}
		
		/**
		 * Sets the working directory where the process is started.
		 * 
		 * @param path the working directory
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withWorkingDirectory(Path path) {
			return withWorkingDirectory(path.toFile());
		}
		
		/**
		 * Configures this builder to communicate with a local process using the given port.
		 * 
		 * @param port the port
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withSocket(int port) {
			socketAddress = new InetSocketAddress(port);
			return this;
		}
		
		/**
		 * Configures this builder to communicate with the specified address and port.
		 * 
		 * @param address the address
		 * @param port the port
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withSocket(InetAddress address, int port) {
			socketAddress = new InetSocketAddress(address, port);
			return this;
		}
		
		/**
		 * Configures this builder to communicate with the specified hostname and port.
		 * 
		 * @param hostname the host name
		 * @param port the port
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withSocket(String hostname, int port) {
			socketAddress = new InetSocketAddress(hostname, port);
			return this;
		}
		
		/**
		 * Configures this builder to communicate using the given input and output streams.  This is primarily intended
		 * for internal use.
		 * 
		 * @param inputStream the input stream
		 * @param outputStream the output stream
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withIOStreams(InputStream inputStream, OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
			return this;
		}
		
		/**
		 * Enables writing debugging info to standard output.
		 * 
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withDebugging() {
			return withDebugging(CloseShieldOutputStream.wrap(System.out));
		}
		
		/**
		 * Enables writing debugging info to the given output stream.
		 * 
		 * @param debug the output stream
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withDebugging(OutputStream debug) {
			this.debug = new PrintStream(debug);
			return this;
		}
		
		/**
		 * Redirects the process' standard error to the given stream.
		 * 
		 * @param errorStream the stream where error messages are written
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder redirectErrorTo(OutputStream errorStream) {
			this.errorStream = errorStream;
			return this;
		}
		
		/**
		 * Overrides the delay between starting the process and attempting to connect with sockets.
		 * 
		 * @param connectionDelay the connection delay
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withConnectionDelay(Duration connectionDelay) {
			this.connectionDelay = connectionDelay;
			return this;
		}
		
		/**
		 * Overrides the timeout given to allow the process to cleanly terminate before sending a kill signal.
		 * 
		 * @param shutdownTimeout the shutdown timeout
		 * @return a reference to this builder for chaining together calls
		 */
		public Builder withShutdownTimeout(Duration shutdownTimeout) {
			this.shutdownTimeout = shutdownTimeout;
			return this;
		}
		
		/**
		 * Returns the constructed instance.
		 * 
		 * @return the external problem instance
		 */
		private Instance build() {
			return new Instance(this);
		}
		
	}
	
	/**
	 * Instance of the external problem.  This manages the lifecycle of the process and connections.
	 */
	protected static class Instance implements Closeable {
		
		private final ProcessBuilder processBuilder;
		
		private final InetSocketAddress socketAddress;
		
		private final Duration connectionDelay;
		
		private final Duration shutdownTimeout;
		
		private Process process;
		
		private Socket socket;
		
		private BufferedReader reader;
		
		private BufferedWriter writer;
		
		private OutputStream errorStream;
		
		private PrintStream debug;
		
		/**
		 * Constructs an instance of an external problem.
		 * 
		 * @param builder the builder
		 */
		public Instance(Builder builder) {
			super();
			this.socketAddress = builder.socketAddress;
			this.connectionDelay = builder.connectionDelay;
			this.shutdownTimeout = builder.shutdownTimeout;
			this.errorStream = builder.errorStream;
			this.debug = builder.debug;
			
			if (builder.command != null && builder.command.length > 0) {
				String[] command = builder.command.clone();

				// If the command is relative and it exists in the working directory, correct the relative path to
				// work on different platforms.
				if (!new File(command[0]).isAbsolute() && new File(builder.workingDirectory, command[0]).exists()) {
					File relativePath = SystemUtils.IS_OS_WINDOWS ? builder.workingDirectory : new File(".");
					command[0] = new File(relativePath, command[0]).getPath();
				}
				
				this.processBuilder = new ProcessBuilder(command).directory(builder.workingDirectory);
			} else {
				this.processBuilder = null;
			}
			
			// Passing in streams directly is primarily intended for testing and internal use
			if (builder.inputStream != null && builder.outputStream != null) {
				reader = new BufferedReader(new InputStreamReader(builder.inputStream));
				writer = new BufferedWriter(new OutputStreamWriter(builder.outputStream));
			}
		}
		
		/**
		 * Returns {@code true} if the underlying process or connections are established; {@code false} otherwise.
		 * 
		 * @return {@code true} if the underlying process or connections are established; {@code false} otherwise
		 */
		public boolean isStarted() {
			return reader != null || writer != null;
		}
		
		/**
		 * Starts the underlying process and establishes any connections.
		 * 
		 * @throws IOException if an I/O error occurred
		 */
		public void start() throws IOException {
			if (isStarted()) {
				return;
			}
			
			if (processBuilder != null) {
				process = processBuilder.start();
				RedirectStream.redirect(process.getErrorStream(), errorStream != null ? errorStream : System.err);
				
				try {
					debug.println("Sleeping for " + connectionDelay);
					Thread.sleep(DurationUtils.toMilliseconds(connectionDelay));
				} catch (InterruptedException e) {
					// ignore if interrupted
				}
			}
			
			if (socketAddress != null) {
				socket = new Socket();
				socket.connect(socketAddress);
			}
			
			if (socket != null) {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} else if (process != null) {
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			} else {
				throw new IllegalArgumentException("Must configure a program or socket address");
			}
		}
		
		/**
		 * Returns the reader used to read content from the external problem.
		 * 
		 * @return the reader
		 */
		public BufferedReader getReader() {
			if (!isStarted()) {
				throw new IllegalStateException("must call start() before using problem instance");
			}
			
			return reader;
		}
		
		/**
		 * Returns the writer used to write content to the external problem.
		 * 
		 * @return the writer
		 */
		public BufferedWriter getWriter() {
			if (!isStarted()) {
				throw new IllegalStateException("must call start() before using problem instance");
			}
			
			return writer;
		}
		
		/**
		 * Returns the stream where debugging logs are written.
		 * 
		 * @return the debug stream
		 */
		public PrintStream getDebug() {
			return debug;
		}

		@Override
		public void close() throws IOException {
			if (writer != null) {
				writer.close();
			}
			
			if (reader != null) {
				reader.close();
			}
			
			if (debug != null) {
				debug.close();
			}
			
			if (socket != null) {
				socket.close();
			}
			
			if (process != null) {
				try {
					if (process.waitFor(DurationUtils.toMilliseconds(shutdownTimeout), TimeUnit.MILLISECONDS)) {
						int exitCode = process.exitValue();
						debug.println("Process exited with code " + exitCode);
					} else {
						debug.println("Process still alive after timeout, sending kill signal");
						process.destroy();
					}
				} catch (InterruptedException | IllegalThreadStateException e) {
					debug.println("Caught exception while waiting on process: " + e.getMessage());
				}
			}
		}
		
	}
	
	/**
	 * The instance backing this external problem, which manages the underlying resources including the process,
	 * socket, and streams.
	 */
	private final Instance instance;

	/**
	 * Constructs an external problem using {@code new ProcessBuilder(command).start()}.  If the command contains
	 * arguments, the arguments should be passed in as separate strings, such as
	 * <pre>
	 *   new ExternalProblem("command", "arg1", "arg2");
	 * </pre>
	 * 
	 * @param command a specified system command
	 * @throws IOException if an I/O error occured
	 * @deprecated Use the {@link #ExternalProblem(Builder)} constructor
	 */
	@Deprecated
	public ExternalProblem(String... command) throws IOException {
		this(new Builder().withCommand(command));
	}
	
	/**
	 * Constructs an external problem that connects to a remote process via sockets.  The remote process should be
	 * instantiated and already listening to the designated port number prior to invoking this constructor.
	 * 
	 * @param host the host name of the remote system; or {@code null} to use the local host
	 * @param port the port number
	 * @throws UnknownHostException if the IP address of the specified host could not be determined
	 * @throws IOException if an I/O error occurred
	 * @deprecated Use the {@link #ExternalProblem(Builder)} constructor
	 */
	@Deprecated
	public ExternalProblem(String host, int port) throws IOException, UnknownHostException {
		this(new Builder().withSocket(host, port));
	}
	
	/**
	 * Constructs an external problem that connects to a remote process via sockets.  The remote process should be
	 * instantiated and already listening to the designated port number prior to invoking this constructor.
	 * 
	 * @param address the IP address of the remote system
	 * @param port the port number
	 * @throws IOException if an I/O error occurred
	 * @deprecated Use the {@link #ExternalProblem(Builder)} constructor
	 */
	@Deprecated
	public ExternalProblem(InetAddress address, int port) throws IOException {
		this(new Builder().withSocket(address, port));
	}

	/**
	 * Constructs an external problem using the specified builder.
	 * 
	 * @param builder the builder that defines the process and/or socket address
	 */
	public ExternalProblem(Builder builder) {
		super();
		instance = builder.build();
	}
	
	/**
	 * Constructs an external problem using the specified process.
	 * 
	 * @param process the process used to evaluate solutions
	 * @deprecated Use the {@link #ExternalProblem(Builder)} constructor
	 */
	@Deprecated
	protected ExternalProblem(Process process) {
		this(process.getInputStream(), process.getOutputStream());
		RedirectStream.redirect(process.getErrorStream(), System.err);
	}
	

	/**
	 * Constructs an external problem using the specified input and output streams.
	 * 
	 * @param input the input stream
	 * @param output the output stream
	 * @deprecated Use the {@link #ExternalProblem(Builder)} constructor
	 */
	@Deprecated
	protected ExternalProblem(InputStream input, OutputStream output) {
		this(new Builder().withIOStreams(input, output));
	}
	
	/**
	 * Sets the output stream used to write debugging information.  If {@code null}, disables debugging.  The debug
	 * stream is not closed by this class and must be managed by the caller.
	 * 
	 * @param stream the output stream
	 * @deprecated Enable debugging by calling {@link Builder#withDebugging()}
	 */
	@Deprecated
	public void setDebugStream(OutputStream stream) {		
		if (stream == null) {
			instance.debug = null;
		} else {
			instance.debug = new PrintStream(stream);
		}
	}

	/**
	 * Closes the connection to the process.  No further invocations of {@code evaluate} are permitted.
	 */
	@Override
	public synchronized void close() {
		try {
			instance.close();
		} catch (IOException e) {
			throw new ProblemException(this, e);
		}
	}

	/**
	 * Evaluates the specified solution using the process defined by this class' constructor.
	 * 
	 * @param solution the solution to evaluate
	 */
	@Override
	public synchronized void evaluate(Solution solution) throws ProblemException {
		if (!instance.isStarted()) {
			try {
				instance.start();
			} catch (IOException e) {
				throw new ProblemException(this, "error while starting external problem", e);
			}
		}
		
		BufferedReader reader = instance.getReader();
		BufferedWriter writer = instance.getWriter();
		PrintStream debug = instance.getDebug();
		
		// send variables to external process
		try {
			StringBuilder sb = new StringBuilder();
			
			sb.append(encode(solution.getVariable(0)));
			
			for (int i = 1; i < solution.getNumberOfVariables(); i++) {
				sb.append(" ");
				sb.append(encode(solution.getVariable(i)));
			}
			
			sb.append(System.lineSeparator());
			
			debug.print("<< ");
			debug.println(sb.toString());
			
			writer.write(sb.toString());
			writer.flush();
		} catch (IOException e) {
			throw new ProblemException(this, "error sending variables to external problem", e);
		}

		// receive objectives from external process
		try {
			String line = reader.readLine();

			if (line == null) {
				if (debug != null) {
					debug.println("Reached end of stream");
					instance.close();
				}
				
				throw new ProblemException(this, "end of stream reached when response expected");
			}

			debug.print(">> ");
			debug.println(line);
			
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
		} catch (IOException | NumberFormatException e) {
			throw new ProblemException(this, "error receiving variables from external problem", e);
		}
	}

	/**
	 * Encodes the variable in a string format sent to the program.
	 * 
	 * @param variable the variable
	 * @return the string representation of the variable
	 */
	private String encode(Variable variable) {
		if (!(variable instanceof RealVariable ||
				variable instanceof BinaryVariable ||
				variable instanceof Permutation)) {
			throw new ProblemException(this, "encoding " + variable.getClass().getSimpleName() + " not supported");
		}
		
		// use toString() instead of encode() as we want to send the value
		return variable.toString();
	}

}
