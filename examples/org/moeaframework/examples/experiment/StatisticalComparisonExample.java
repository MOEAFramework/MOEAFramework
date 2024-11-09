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
package org.moeaframework.examples.experiment;

import java.io.IOException;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates using IndicatorStatistics to compute descriptive statistics and determine if results are statistically
 * similar / different.
 */
public class StatisticalComparisonExample {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(3);

		// Set up the Hypervolume indicator
		NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/DTLZ2.3D.pf");
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		
		// Collect the results and compute statistics
		IndicatorStatistics statistics = new IndicatorStatistics(hypervolume);
		
		// Collect the results from NSGA-II
		for (int i = 0; i < 25; i++) {
			NSGAII algorithm = new NSGAII(problem);
			algorithm.run(10000);
			statistics.add("NSGA-II", algorithm.getResult());
		}
		
		// Collect the results from MOEA/D, storing as group 1.
		for (int i = 0; i < 25; i++) {
			MOEAD algorithm = new MOEAD(problem);
			algorithm.run(10000);
			statistics.add("MOEA/D", algorithm.getResult());
		}
		
		statistics.display();
	}

}
