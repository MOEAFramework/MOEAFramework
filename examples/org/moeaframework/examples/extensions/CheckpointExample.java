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
package org.moeaframework.examples.extensions;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.CheckpointExtension;
import org.moeaframework.algorithm.extension.LoggingExtension;
import org.moeaframework.problem.misc.Srinivas;

/**
 * Demonstrates using checkpoints to allow saving and resuming the state of an algorithm.
 * This is useful for long-running experiments that need to tolerate interruptions.
 */
public class CheckpointExample {
	
	public static void main(String[] args) throws IOException {
		File checkpointFile = new File("checkpoint.dat");
		checkpointFile.delete();

		NSGAII algorithm = new NSGAII(new Srinivas());
		algorithm.addExtension(new LoggingExtension());
		algorithm.addExtension(new CheckpointExtension(checkpointFile, 1000));
		algorithm.run(500000);
		
		System.out.println("========== End of first run ==========");
		
		algorithm = new NSGAII(new Srinivas());
		algorithm.addExtension(new LoggingExtension());
		algorithm.addExtension(new CheckpointExtension(checkpointFile, 1000));
		algorithm.run(1000000 - algorithm.getNumberOfEvaluations());
	}

}
