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
package org.moeaframework.examples.generalizedDecomposition;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.util.weights.FixedWeights;

/**
 * Compares MOEA/D with generalized decomposition (GD-MOEA/D) to MOEA/D with regular weights.
 */
public class GeneralizedDecompositionExample {
	
	private static final File NBI_WEIGHTS = new File("nbi_weights.txt");
	private static final File GD_WEIGHTS = new File("gd_weights.txt");

	public static void main(String[] args) throws IOException {
		if (!NBI_WEIGHTS.exists() || !GD_WEIGHTS.exists()) {
			System.err.println("Please run ./generateWeights.sh to generate the weight input files");
			return;
		}
		
		Problem problem = new DTLZ2(3);
		Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.load("pf/DTLZ2.3D.pf"));
		IndicatorStatistics analysis = new IndicatorStatistics(hypervolume);
		
		// run with Normal Boundary Intersection (NBI) weights
		for (int i = 0; i < 50; i++) {
			FixedWeights weights = FixedWeights.load(NBI_WEIGHTS);
			
			MOEAD algorithm = new MOEAD(problem);
			algorithm.setWeightGenerator(weights);
			algorithm.setInitialPopulationSize(weights.size());
			algorithm.run(10000);
			analysis.add("MOEA/D (NBI)", algorithm.getResult());
		}
		
		// run with Generalized Decomposition (GD) weights (derived from the NBI weights)
		for (int i = 0; i < 50; i++) {
			FixedWeights weights = FixedWeights.load(GD_WEIGHTS);
			
			MOEAD algorithm = new MOEAD(problem);
			algorithm.setWeightGenerator(weights);
			algorithm.setInitialPopulationSize(weights.size());
			algorithm.run(10000);
			analysis.add("MOEA/D (GD)", algorithm.getResult());
		}
		
		analysis.display();
	}

}