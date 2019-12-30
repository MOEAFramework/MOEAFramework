/* Copyright 2009-2019 David Hadka
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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link AbstractEvolutionaryAlgorithm} class.
 */
public class AbstractEvolutionaryAlgorithmTest {

	/**
	 * Returns a new instance of a dummy abstract evolutionary algorithm for
	 * testing.
	 * 
	 * @return a new instance of a dummy abstract evolutionary algorithm for
	 *         testing
	 */
	private AbstractEvolutionaryAlgorithm newInstance() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Population population = new Population();
		NondominatedPopulation archive = new NondominatedPopulation();
		Initialization initialization = new RandomInitialization(problem, 50);

		return new AbstractEvolutionaryAlgorithm(problem, population, archive,
				initialization) {

			@Override
			protected void iterate() {
				population.clear();
				population.addAll(initialization.initialize());
				evaluateAll(population);
				archive.addAll(population);
			}

		};
	}

	/**
	 * Tests if abstract evolutionary algorithms are resumable.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testResumable() throws IOException {
		File file = TestUtils.createTempFile();
		NondominatedPopulation lastResult = new NondominatedPopulation();
		NondominatedPopulation lastArchive = new NondominatedPopulation();
		Population lastPopulation = new Population();
		int lastNFE = 0;
		AbstractEvolutionaryAlgorithm algorithm = null;
		Checkpoints checkpoints = null;

		for (int i = 0; i < 10; i++) {
			algorithm = newInstance();
			checkpoints = new Checkpoints(algorithm, file, 0);

			Assert.assertEquals(lastNFE, checkpoints.getNumberOfEvaluations());
			TestUtils.assertEquals(lastResult, checkpoints.getResult());
			TestUtils.assertEquals(lastArchive, algorithm.getArchive());
			TestUtils.assertEquals(lastPopulation, algorithm.getPopulation());

			checkpoints.step();

			lastNFE = checkpoints.getNumberOfEvaluations();
			lastResult = checkpoints.getResult();
			lastArchive = algorithm.getArchive();
			lastPopulation = algorithm.getPopulation();
		}
	}

}
