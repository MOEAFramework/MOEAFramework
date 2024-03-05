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

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;

/**
 * Since MOEAs are stochastic algorithms (i.e., they include randomness), each run will typically produce
 * different results.  Collecting and measuring performance using multiple random-number seeds is a good practice.
 */
public class MultipleSeedsExample {
	
	public static void main(String[] args) throws Exception {
		Executor executor = new Executor()
		        .withProblem("UF1")
		        .withAlgorithm("NSGAII")
		        .withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
		        .withSameProblemAs(executor)
		        .includeHypervolume()
		        .includeGenerationalDistance();
				
		analyzer.addAll("NSGAII", executor.runSeeds(50));
		analyzer.display();
	}

}
