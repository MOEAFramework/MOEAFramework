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
package org.moeaframework.examples.statistics;

import java.io.IOException;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.util.statistics.MannWhitneyUTest;

/**
 * Demonstrates using the non-parametric Mann-Whitney U test to determine if two algorithms produce results with
 * different medians.  A number of statistical tests are provided in the {@code org.moeaframework.util.statistics}
 * package, with varying assumptions about the underlying data.
 */
public class StatisticalComparisonExample {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(3);
		MannWhitneyUTest test = new MannWhitneyUTest();
		
		// Set up the Hypervolume indicator
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/DTLZ2.3D.pf");
		Indicators indicators = Indicators.of(problem, referenceSet).includeHypervolume();
		
		// Collect the results from NSGA-II, storing as group 0.
		for (int i = 0; i < 25; i++) {
			NSGAII algorithm = new NSGAII(problem);
			algorithm.run(10000);
			
			IndicatorValues values = indicators.apply(algorithm.getResult());
			test.add(values.getHypervolume(), 0);
		}
		
		// Collect the results from MOEA/D, storing as group 1.
		for (int i = 0; i < 25; i++) {
			MOEAD algorithm = new MOEAD(problem);
			algorithm.run(10000);
					
			IndicatorValues values = indicators.apply(algorithm.getResult());
			test.add(values.getHypervolume(), 1);
		}
		
		System.out.println("NSGA-II median: " + test.getStatistics(0).getPercentile(50));
		System.out.println("MOEA/D median:  " + test.getStatistics(1).getPercentile(50));
		System.out.println("Are medians different (5% significance level)? " + Boolean.toString(test.test(0.05)));
	}

}
