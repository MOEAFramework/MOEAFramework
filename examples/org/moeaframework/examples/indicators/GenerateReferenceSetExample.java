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
package org.moeaframework.examples.indicators;

import java.io.File;
import java.io.IOException;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Generates a reference set for the 3-objective DTLZ2 problem using &epsilon; values of {@code 0.01}.  Any problem
 * that implements the {@link AnalyticalProblem} interface has a known Pareto front.
 */
public class GenerateReferenceSetExample {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		AnalyticalProblem problem = new DTLZ2(3);
		NondominatedPopulation referenceSet = new EpsilonBoxDominanceArchive(Epsilons.of(0.01));
			
		for (int i = 0; i < 1000; i++) {
			referenceSet.add(problem.generate());
		}
			
		referenceSet.save(new File("Custom_DTLZ2.3D.pf"));
	}

}
