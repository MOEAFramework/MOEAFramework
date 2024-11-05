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
package org.moeaframework.algorithm;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.algorithm.extension.CheckpointExtension;
import org.moeaframework.core.initialization.Initialization;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;

public class AbstractEvolutionaryAlgorithmTest {

	private AbstractEvolutionaryAlgorithm newInstance() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Population population = new Population();
		NondominatedPopulation archive = new NondominatedPopulation();
		Initialization initialization = new RandomInitialization(problem);
		Variation variation = OperatorFactory.getInstance().getVariation(problem);

		return new AbstractEvolutionaryAlgorithm(problem, 50, population, archive,
				initialization, variation) {
			
			@Override
			public String getName() {
				return "TestAbstractEvolutionaryAlgorithm";
			}

			@Override
			protected void iterate() {
				population.clear();
				population.addAll(initialization.initialize(50));
				evaluateAll(population);
				archive.addAll(population);
			}

		};
	}

	@Test
	public void testResumable() throws IOException {
		File file = TempFiles.createFile();
		NondominatedPopulation lastResult = new NondominatedPopulation();
		NondominatedPopulation lastArchive = new NondominatedPopulation();
		Population lastPopulation = new Population();
		int lastNFE = 0;

		for (int i = 0; i < 10; i++) {
			AbstractEvolutionaryAlgorithm algorithm = newInstance();
			algorithm.addExtension(new CheckpointExtension(file, 0));

			Assert.assertEquals(lastNFE, algorithm.getNumberOfEvaluations());
			Assert.assertEquals(lastResult, algorithm.getResult());
			Assert.assertEquals(lastArchive, algorithm.getArchive());
			Assert.assertEquals(lastPopulation, algorithm.getPopulation());

			algorithm.step();

			lastNFE = algorithm.getNumberOfEvaluations();
			lastResult = algorithm.getResult();
			lastArchive = algorithm.getArchive();
			lastPopulation = algorithm.getPopulation();
		}
	}

}
