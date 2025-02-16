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
package org.moeaframework.examples.ant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.Solution;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * Example running the ant trail problem with the Los Altos trail.
 */
public class LosAltosExample {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int maxMoves = 500;
		
		InputStream input = Resources.asStream(LosAltosExample.class, "losaltos.trail", ResourceOption.REQUIRED);
		
		try (AntProblem problem = new AntProblem(input, maxMoves)) {
			// solve the ant trail instance
			GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
			algorithm.setInitialPopulationSize(500);
			algorithm.run(500000);
				
			// display the result
			Solution solution = algorithm.getResult().get(0);
			problem.evaluate(solution);
			problem.displayLastEvaluation();
		}
	}

}
