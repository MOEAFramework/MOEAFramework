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
package org.moeaframework.algorithm.extension;

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

import org.moeaframework.algorithm.Algorithm;

/**
 * Adds checkpoint functionality to an algorithm where the state is periodically recorded in a state file.  If the
 * state file exists at the start of the run, the algorithm is restored and resumes from that point.
 */
public class CheckpointExtension extends PeriodicExtension {

	/**
	 * The file containing the checkpoint states.
	 */
	private final File stateFile;
	
	/**
	 * Creates a new checkpoint extension.
	 * 
	 * @param stateFile the file containing the checkpoint states
	 * @param frequency the number of objective function evaluations between checkpoints
	 */
	public CheckpointExtension(File stateFile, int frequency) {
		this(stateFile, frequency, FrequencyType.EVALUATIONS);
	}

	/**
	 * Creates a new checkpoint extension.
	 * 
	 * @param stateFile the file containing the checkpoint states
	 * @param frequency the frequency of checkpoints
	 * @param frequencyType the type of frequency
	 */
	public CheckpointExtension(File stateFile, int frequency, FrequencyType frequencyType) {
		super(frequency, frequencyType);
		this.stateFile = stateFile;
	}

	/**
	 * Saves the state.
	 * 
	 * @param algorithm the algorithm to save to the state file
	 * @throws IOException if an I/O error occurred
	 */
	private void saveToStateFile(Algorithm algorithm) throws IOException {
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
	 * @param algorithm the algorithm to restore from the state file
	 * @throws IOException if an I/O error occurred
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void loadFromStateFile(Algorithm algorithm) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(stateFile)))) {
			algorithm.loadState(ois);
		}
	}
	
	@Override
	public void doAction(Algorithm algorithm) {
		try {
			saveToStateFile(algorithm);
		} catch (IOException e) {
			System.err.println("WARNING: Failed to write state file, continuing without checkpoints!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRegister(Algorithm algorithm) {
		super.onRegister(algorithm);
		
		if (stateFile.exists() && (stateFile.length() != 0L)) {
			try {
				loadFromStateFile(algorithm);
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("WARNING Failed to read state file, continuing without checkpoints!");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onTerminate(Algorithm algorithm) {
		doAction(algorithm);
		super.onTerminate(algorithm);
	}

}
