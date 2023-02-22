/* Copyright 2009-2023 David Hadka
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
import java.io.IOException;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

/**
 * In Example 2, we computed the hypervolume and generational distance for a single
 * run.  We can perform more extensive experiments comparing multiple algorithms
 * using multiple repetitions to statistically compare results.
 * 
 * Below, we use the Executor and Analyzer classes to help perform this analysis.
 * The Executor creates and runs each of the algorithms, possibly with multiple
 * repetitions (seeds).  The Analyzer collects the resulting Pareto fronts,
 * computes selected quality indicators, and displays the results.
 * 
 * In this example, we will compare NSGA-II, GDE3, and eMOEA on the UF1 test problem
 * using the Hypervolume indicator.
 */
public class Example4 {

	public static void main(String[] args) throws IOException {
		String problem = "UF1";
		String[] algorithms = { "NSGAII", "GDE3", "eMOEA" };

		// setup the Executor to run each test for 10,000 function evaluations
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		// setup the Analyzer to measure the hypervolume
		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeHypervolume()
				.showStatisticalSignificance();

		// run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		// display the results
		analyzer.display();
	}
	
}
