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

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.Normalizer;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.CEC2009.UF1;

/**
 * Demonstrates how to customize or disable normalization when computing any of the
 * quality / performance indicators.
 */
public class NormalizationExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new UF1();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		NondominatedPopulation approximationSet = algorithm.getResult();

		// Default - Derives bounds from reference set
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/UF1.dat");
		
		Hypervolume defaultHypervolume = new Hypervolume(problem, referenceSet);
		System.out.println("Normalized by reference set (default): " + defaultHypervolume.evaluate(approximationSet.copy()));
		
		// Explicit bounds
		Hypervolume explicitHypervolume = new Hypervolume(problem, new double[] { 0.0, 0.0 }, new double[] { 2.0, 2.0 });
		System.out.println("Normalized with explicit bounds: " + explicitHypervolume.evaluate(approximationSet));
		
		// Disabled normalization
		Hypervolume disabledHypervolume = new Hypervolume(problem, Normalizer.none());
		System.out.println("Disabled normalization: " + disabledHypervolume.evaluate(approximationSet));
	}

}
