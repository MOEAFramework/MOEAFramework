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
package org.moeaframework.algorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.PeriodicAction;

/**
 * Decorates an {@link Algorithm} to periodically save checkpoint files from which the algorithm can resume itself if
 * unexpectedly terminated.
 * <pre>
 *   File stateFile = new File("last.state");
 * 
 *   Algorithm algorithm = new Checkpoints(new NSGAII(...), stateFile, 100);
 * 
 *   while (!algorithm.isTerminated()) {
 *     algorithm.step();
 *   }
 * </pre>
 */
public class Checkpoints extends PeriodicAction {

	/**
	 * The file containing the checkpoint states.
	 */
	private final File stateFile;

	/**
	 * Decorates an algorithm to periodically save checkpoints from which the algorithm can resume itself if
	 * unexpectedly terminated.
	 * 
	 * @param algorithm the algorithm
	 * @param stateFile the file containing the checkpoint states
	 * @param checkpointFrequency the number of objective function evaluations between checkpoints
	 */
	public Checkpoints(Algorithm algorithm, File stateFile, int checkpointFrequency) {
		super(algorithm, checkpointFrequency, FrequencyType.EVALUATIONS);
		this.stateFile = stateFile;

		if (stateFile.exists() && (stateFile.length() != 0L)) {
			try {
				loadFromStateFile();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("an error occurred while reading the state file");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves the state.
	 * 
	 * @param state the state
	 * @throws IOException if an I/O error occurred
	 */
	private void saveToStateFile() throws IOException {
		File tempFile = File.createTempFile("checkpoint", "state");
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(tempFile)))) {
			algorithm.saveState(oos);
		}
		
		Files.move(tempFile.toPath(), stateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Loads the state.
	 * 
	 * @return the state
	 * @throws IOException if an I/O error occurred
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void loadFromStateFile() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(stateFile)))) {
			algorithm.loadState(ois);
		}
	}
	
	@Override
	public void doAction() {
		try {
			saveToStateFile();
		} catch (IOException e) {
			System.err.println("an error occurred while writing the state file");
		}
	}

	@Override
	public void terminate() {
		doAction();
		super.terminate();
	}

}
