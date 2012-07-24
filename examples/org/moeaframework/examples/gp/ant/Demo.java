/* Copyright 2009-2012 David Hadka
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 * Example running the ant trail problem.  NSGA-II isn't really designed for
 * single-objective functions, but this serves as a demonstration of genetic
 * programming.
 */
public class Demo {
	
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
		File file = new File("src/org/moeaframework/util/tree/ant/santafe.trail");
		
		NondominatedPopulation results = new Executor()
				.withProblemClass(AntProblem.class, file, maxMoves)
				.withAlgorithm("NSGAII")
				.withProperty("populationSize", 500)
				.withMaxEvaluations(500000)
				.run();
		
		for (Solution solution : results) {
			AntProblem problem = new AntProblem(file, maxMoves);
			problem.evaluate(solution);
			problem.displayLastEvaluation();
		}
	}

}
