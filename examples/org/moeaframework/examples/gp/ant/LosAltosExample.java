/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.gp.ant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;

/**
 * Example running the ant trail problem with the Los Altos trail.
 */
public class LosAltosExample {
	
	/**
	 * Starts the example running the ant trail problem.
	 * 
	 * @param args the command line arguments
	 * @throws FileNotFoundException if the ant trail file could not be found
	 * @throws IOException if an I/O error occurred
	 */
	public static void main(String[] args) throws FileNotFoundException,
	IOException {
		int maxMoves = 500;
		
		// solve the ant trail instance
		NondominatedPopulation results = new Executor()
				.withProblemClass(AntProblem.class, openDataFile(), maxMoves)
				.withAlgorithm("GA")
				.withProperty("populationSize", 500)
				.withMaxEvaluations(500000)
				.run();
		
		// display the result
		AntProblem problem = new AntProblem(openDataFile(), maxMoves);
		problem.evaluate(results.get(0));
		problem.displayLastEvaluation();
	}
	
	/**
	 * Returns an input stream that contains the Los Altos ant trail data file.
	 * 
	 * @return an input stream that contains the Los Altos ant trail data file
	 */
	public static InputStream openDataFile() {
		InputStream stream = LosAltosExample.class.getResourceAsStream(
				"losaltos.trail");
		
		if (stream == null) {
			System.err.println("Unable to find the file losaltos.trail.");
			System.exit(-1);
		}
		
		return stream;
	}

}
