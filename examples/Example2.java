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
 * The Executor and Analyzer are used to drive experiments where we compare the
 * performance of algorithms.  The Executor creates and executes the named algorithm,
 * typically repeating for multiple seeds.  The Analyzer collects the Pareto front
 * results, computes various performance indicators, and displays the results.
 * 
 * In the example below, we compare NSGA-II, GDE3, and eMOEA on the UF1 test problem
 * using the Hypervolume indicator.
 */
public class Example2 {

	public static void main(String[] args) throws IOException {
		String problem = "UF1";
		String[] algorithms = { "NSGAII", "GDE3", "eMOEA" };

		//setup the experiment
		Executor executor = new Executor()
				.withProblem(problem)
				.withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeHypervolume()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			analyzer.addAll(algorithm, 
					executor.withAlgorithm(algorithm).runSeeds(50));
		}

		//print the results
		analyzer.display();
	}
	
}
