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
package org.moeaframework.examples.misc;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;

/**
 * While we recommend in most scenarios to create algorithms and problems directly by calling their constructor, the
 * factory classes allow dynamically constructing each by name.
 * 
 * This is especially useful when:
 *   1. Running an experiment over a variety of algorithms or problems,
 *   2. Creating an algorithm or problem provided by our JMetal-Plugin, PISA-Plugin, or other third-party library, and
 *   3. As a replacement for the Executor, which was deprecated and removed in v5.
 */
public class FactoryExample {

	public static void main(String[] args) {
		String[] problemNames = new String[] { "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "DTLZ5", "DTLZ6", "DTLZ7" };
		String[] algorithmNames = new String[] { "NSGA-II", "NSGA-III", "MOEA/D", "GDE3" };
		
		for (String problemName : problemNames) {
			Problem problem = ProblemFactory.getInstance().getProblem(problemName);
			NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
			
			Indicators indicators = Indicators.of(problem, referenceSet);
			indicators.includeHypervolume();
			
			System.out.println(problemName + ":");
			
			for (String algorithmName : algorithmNames) {
				Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, problem);
				algorithm.run(10000);
				
				IndicatorValues indicatorValues = indicators.apply(algorithm.getResult());
				
				System.out.println("  " + algorithmName + ": " + indicatorValues.getHypervolume());
			}
		}
	}

}
