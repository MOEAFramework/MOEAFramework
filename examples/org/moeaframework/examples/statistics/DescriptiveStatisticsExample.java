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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates collecting descriptive statistics, such as the min, mean, max, median, and inter-quartile range (IQR),
 * from end-of-run results.
 */
public class DescriptiveStatisticsExample {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(3);
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		
		// Set up the Hypervolume indicator
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/DTLZ2.3D.pf");
		Indicators indicators = Indicators.of(problem, referenceSet).includeHypervolume();
		
		// Solve the problem with NSGA-II, collecting the Hypervolume value from each run
		for (int i = 0; i < 25; i++) {
			NSGAII algorithm = new NSGAII(problem);
			algorithm.run(10000);
			
			IndicatorValues values = indicators.apply(algorithm.getResult());
			statistics.addValue(values.getHypervolume());
		}
		
		System.out.println("Min:    " + statistics.getMin());
		System.out.println("Mean:   " + statistics.getMean());
		System.out.println("Max:    " + statistics.getMax());
		System.out.println("Median: " + statistics.getPercentile(50));
		System.out.println("IQR:    " + (statistics.getPercentile(75) - statistics.getPercentile(25)));
	}

}
