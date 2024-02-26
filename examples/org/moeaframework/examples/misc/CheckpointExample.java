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
package org.moeaframework.examples.misc;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.Checkpoints;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.problem.misc.Srinivas;

/**
 * Demonstrates using checkpoints to allow saving and resuming the state of an algorithm.
 * This is useful for long-running experiments that need to tolerate interruptions.
 * 
 * Try running this example a few times.  Observe how the starting NFE picks up where the
 * prior run finished.
 */
public class CheckpointExample {
	
	public static void main(String[] args) throws IOException {
		File checkpointFile = new File("checkpoint.dat");

		NSGAII algorithm = new NSGAII(new Srinivas());
		Checkpoints checkpoints = new Checkpoints(algorithm, checkpointFile, 1000);

		if (checkpointFile.exists()) {
		    System.out.println("Checkpoint file exists, resuming previous run at " +
		        checkpoints.getNumberOfEvaluations() + " evaluations!");
		} else {
		    System.out.println("No checkpoint file, starting new run!");
		}

		checkpoints.run(1000000);
	}

}
