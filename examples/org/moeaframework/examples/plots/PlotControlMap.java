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
package org.moeaframework.examples.plots;

import java.util.stream.IntStream;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

// TODO: Can this be updated to use samples?

/**
 * Generates a control map plot showing the effects of Max Evaluations and Population Size parameters on the
 * performance of NSGA-II when solving the 2-objective DTLZ2 problem.
 */
public class PlotControlMap {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		Hypervolume hypervolume = new Hypervolume(problem, NondominatedPopulation.loadReferenceSet("./pf/DTLZ2.2D.pf"));
		
		double[] x = IntStream.range(0, 50).mapToDouble(i -> 100 * (i+1)).toArray(); // maxEvaluations from 100 to 5000
		double[] y = IntStream.range(0, 50).mapToDouble(i -> 4 * (i+1)).toArray();   // populationSize from 2 to 100
		double[][] z = new double[x.length][y.length];

		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				System.out.println("Evaluating run " + (i * y.length + j + 1) + " of " + (x.length * y.length));
				
				NSGAII algorithm = new NSGAII(problem);
				algorithm.setInitialPopulationSize((int)y[j]);
				algorithm.run((int)x[i]);
				
				z[i][j] = hypervolume.evaluate(algorithm.getResult());
			}
		}
		
		new Plot()
			.heatMap("Hypervolume", x, y, z)
			.setXLabel("Max Evaluations")
			.setYLabel("Population Size")
			.show();
	}

}
