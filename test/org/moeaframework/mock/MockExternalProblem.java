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
package org.moeaframework.mock;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.problem.ExternalProblem;
import org.moeaframework.util.io.LineReader;

public class MockExternalProblem extends ExternalProblem {
	
	private final Pattern pattern = Pattern.compile(
			"^[0-9]+\\.[0-9]+\\s+[0-9]+\\s+[0-1]{5}\\s+\\[[0-9,]+\\]\\s+\\{[0-9,]+\\}$");
		
	private volatile Exception exception;
	
	private Thread thread;
	
	private PipedOutputStream pipedWriter;
	
	private PipedInputStream pipedReader;
	
	public MockExternalProblem() throws IOException {
		this(input -> "0.2 0.8 0.5");
	}
	
	public MockExternalProblem(final Function<String, String> callback) throws IOException {
		this(new PipedInputStream(), new PipedOutputStream(), callback);
	}
	
	MockExternalProblem(final PipedInputStream input, final PipedOutputStream output,
			final Function<String, String> callback) throws IOException {
		super(new Builder().withIOStreams(input, output));
		
		pipedWriter = new PipedOutputStream(input);
		pipedReader = new PipedInputStream(output);

		thread = new Thread() {
			@Override
			public void run() {
				try (LineReader reader = LineReader.wrap(new InputStreamReader(pipedReader));
						PrintStream writer = new PrintStream(pipedWriter)) {
					for (String line : reader) {
						if (thread.isInterrupted()) {
							break;
						}
						
						Assert.assertStringMatches(line, pattern);
	
						writer.println(callback.apply(line));
						writer.flush();
					}
				} catch (Exception e) {
					exception = e;
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public String getName() {
		return MockExternalProblem.class.getSimpleName();
	}

	@Override
	public int getNumberOfVariables() {
		return 5;
	}

	@Override
	public int getNumberOfObjectives() {
		return 2;
	}

	@Override
	public int getNumberOfConstraints() {
		return 1;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(5, 2, 1);
		solution.setVariable(0, new RealVariable(0.0, 1.0).withValue(0.5));
		solution.setVariable(1, new BinaryIntegerVariable(0, 10).withValue(5));
		solution.setVariable(2, new BinaryVariable(5));
		solution.setVariable(3, new Permutation(3));
		solution.setVariable(4, new Subset(1, 3));
		return solution;
	}
	
	@Override
	public synchronized void close() {
		super.close();
		
		thread.interrupt();
		
		IOUtils.closeQuietly(pipedReader);
		IOUtils.closeQuietly(pipedWriter);
		
		if (exception != null) {
			throw new AssertionError("Caught exception in " + getName(), exception);
		}
	}

}
