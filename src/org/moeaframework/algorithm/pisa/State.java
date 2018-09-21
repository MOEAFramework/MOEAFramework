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
package org.moeaframework.algorithm.pisa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.moeaframework.core.Settings;

/**
 * Reads and writes PISA state files.
 */
public class State {

	/**
	 * The state file.
	 */
	private final File file;

	/**
	 * The delay in milliseconds between successive reads of the state file.
	 */
	private static final long pollRate = Settings.getPISAPollRate();
	
	/**
	 * The number of times this class will attempt to write to the state file
	 * until propagating the error.  Failures primarily result from PISA 
	 * selectors locking the state file.
	 */
	private static final int numberOfRetries = 5;

	/**
	 * Constructs a state indicator backed by the specified file.
	 * 
	 * @param file the state file
	 */
	public State(File file) {
		super();
		this.file = file;
	}

	/**
	 * Reads the state.
	 * 
	 * @return the state
	 * @throws IOException if an I/O error occurred
	 */
	public int get() throws IOException {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			if (line == null) {
				return -1;
			} else {
				return Integer.parseInt(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Sets the state.
	 * 
	 * @param state the state
	 * @throws IOException if an I/O error occurred
	 * @throws InterruptedException if {@link Thread#sleep(long)} was
	 *         interrupted
	 */
	public void set(int state) throws IOException, InterruptedException {
		PrintWriter writer = null;
		int retriesRemaining = numberOfRetries;

		while (true) {
			try {
				writer = new PrintWriter(new FileWriter(file));
				writer.print(state);
				break;
			} catch (IOException e) {
				retriesRemaining--;
				
				if (retriesRemaining <= 0) {
					throw e;
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
			
			Thread.sleep(pollRate);
		}
	}

	/**
	 * Blocks until the state becomes the specified value.
	 * 
	 * @param state the state to wait for
	 * @throws IOException if an I/O error occurred
	 * @throws InterruptedException if {@link Thread#sleep(long)} was
	 *         interrupted
	 */
	public void waitFor(int state) throws IOException, InterruptedException {
		while (!file.exists() || (get() != state)) {
			Thread.sleep(pollRate);
		}
	}

	/**
	 * Blocks while the state remains at the specified value, returning the
	 * new state value when it changes.
	 * 
	 * @param state the state to wait on
	 * @return the new state
	 * @throws IOException if an I/O error occurred
	 * @throws InterruptedException if {@link Thread#sleep(long)} was
	 *         interrupted
	 */
	public int waitWhile(int state) throws IOException, InterruptedException {
		int current;

		do {
			Thread.sleep(pollRate);
		} while (!file.exists() || ((current = get()) == state));

		return current;
	}

}
