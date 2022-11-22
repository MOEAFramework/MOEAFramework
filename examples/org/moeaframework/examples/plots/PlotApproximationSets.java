/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.examples.plots;

import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;

/**
 * Displays a plot comparing the final Pareto approximation sets found by
 * NSGAII and eMOEA on the 2D DTLZ2 problem.
 */
public class PlotApproximationSets {

	public static void main(String[] args) {
		NondominatedPopulation result1 = new Executor()
				.withProblem("DTLZ2_2")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(10000)
				.run();
		
		NondominatedPopulation result2 = new Executor()
				.withProblem("DTLZ2_2")
				.withAlgorithm("eMOEA")
				.withMaxEvaluations(10000)
				.run();
		
		Plot plot = new Plot();
		plot.add("NSGAII", result1);
		plot.add("eMOEA", result2);
		plot.show();
	}

}
