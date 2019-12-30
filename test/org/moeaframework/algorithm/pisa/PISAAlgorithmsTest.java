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
package org.moeaframework.algorithm.pisa;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link PISAAlgorithms} class ussing the new, parameter-based
 * configurations.
 */
public class PISAAlgorithmsTest {
	
	/**
	 * The test problem.
	 */
	private Problem problem;
	
	/**
	 * The properties for controlling the test problems.
	 */
	private Properties properties;
	
	/**
	 * The quality indicator for comparing solutions to the test problem.
	 */
	private QualityIndicator qualityIndicator;
	
	/**
	 * Creates the shared problem.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Before
	public void setUp() throws IOException {
		problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		properties = new Properties();
		qualityIndicator = new QualityIndicator(problem,
				ProblemFactory.getInstance().getReferenceSet("DTLZ2_2"));
		
		properties.setProperty("populationSize", "100");
		properties.setProperty("maxEvaluations", "1000");
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		problem = null;
		properties = null;
		qualityIndicator = null;
	}
	
	/**
	 * Tests if running NSGA2 with the same or different parameter settings
	 * produces different/similar results, thus checking if the parameters
	 * are correctly set.
	 */
	@Test
	public void testNSGA2() {
		String name = "nsga2_parameter_test";
		String directory = "./pisa/nsga2_win";
		
		TestUtils.assumeFileExists(new File(directory));

		Settings.PROPERTIES.setString(
				"org.moeaframework.algorithm.pisa.algorithms", name);
		Settings.PROPERTIES.setString("org.moeaframework.algorithm.pisa." +
				name + ".command", directory + "/" + "nsga2.exe");
		Settings.PROPERTIES.setStringArray("org.moeaframework.algorithm.pisa." +
				name + ".parameters", new String[] { "seed", "tournament" });
		Settings.PROPERTIES.setInt("org.moeaframework.algorithm.pisa." + name +
				".parameter.tournament", 2);
		
		long seed1 = PRNG.getRandom().nextLong();
		long seed2 = PRNG.getRandom().nextLong();
		
		while (seed1 == seed2) {
			seed2 = PRNG.getRandom().nextLong();
		}

		double result1 = test(AlgorithmFactory.getInstance().getAlgorithm(name,
				properties, problem), seed1);
		
		properties.setProperty("seed", Integer.toString(PRNG.nextInt()));
		
		double result2 = test(AlgorithmFactory.getInstance().getAlgorithm(name,
				properties, problem), seed2);
		
		properties.setProperty("tournament", "3");
		
		double result3 = test(AlgorithmFactory.getInstance().getAlgorithm(name,
				properties, problem), seed1);
		
		double result4 = test(AlgorithmFactory.getInstance().getAlgorithm(name,
				properties, problem), seed1);

		Assert.assertTrue(result1 != result3);
		Assert.assertTrue(result2 != result3);
		Assert.assertTrue(result3 == result4);
		
		Settings.PROPERTIES.remove(
				"org.moeaframework.algorithm.pisa.algorithms");
	}
	
	/**
	 * Tests if the given algorithm operates correctly.
	 * 
	 * @param algorithm the algorithm
	 */
	protected double test(Algorithm algorithm, long seed) {
		PRNG.setSeed(seed);
		
		Assert.assertTrue(algorithm instanceof PISAAlgorithm);
		Assert.assertEquals(0, algorithm.getNumberOfEvaluations());
		Assert.assertEquals(0, algorithm.getResult().size());
		Assert.assertFalse(algorithm.isTerminated());
		
		while (algorithm.getNumberOfEvaluations() < 1000) {
			algorithm.step();
		}
		
		algorithm.terminate();
		
		Assert.assertEquals(1000, algorithm.getNumberOfEvaluations());
		Assert.assertTrue(algorithm.getResult().size() > 0);
		Assert.assertTrue(algorithm.isTerminated());
		
		qualityIndicator.calculate(algorithm.getResult());
		return qualityIndicator.getHypervolume();
	}
	
}
