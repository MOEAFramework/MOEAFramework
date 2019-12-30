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
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.util.io.FileUtils;

/**
 * Tests the {@link PISAAlgorithms} class using the old, static configuration
 * files.
 */
public class PISAAlgorithmsStaticConfigurationTest {
	
	/**
	 * The real encoded test problem.
	 */
	private Problem realProblem;
	
	/**
	 * The properties for controlling the test problems.
	 */
	private Properties properties;
	
	/**
	 * Creates the shared problem.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Before
	public void setUp() throws IOException {
		realProblem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		properties = new Properties();
		
		properties.setProperty("populationSize", "100");
		properties.setProperty("maxEvaluations", "1000");
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		realProblem = null;
		properties = null;
	}
	
	private void run(String name, String directory, String command, 
			String configuration, Problem problem) {
		TestUtils.assumeFileExists(new File(directory));

		Settings.PROPERTIES.setString(
				"org.moeaframework.algorithm.pisa.algorithms", name);
		Settings.PROPERTIES.setString("org.moeaframework.algorithm.pisa." +
				name + ".command", command);
		Settings.PROPERTIES.setString("org.moeaframework.algorithm.pisa." +
				name + ".configuration", configuration);
		
		test(AlgorithmFactory.getInstance().getAlgorithm(name, properties, 
				problem));
		
		Settings.PROPERTIES.remove(
				"org.moeaframework.algorithm.pisa.algorithms");
	}
	
	private void run(String algorithm, String os, Problem problem) {
		String name = algorithm + "_" + os;
		String directory = "./pisa/" + name;
		
		run(name, directory, directory + "/" + algorithm + ".exe", 
				directory + "/" + algorithm + "_param.txt", problem);
	}
	
	private void run(String algorithm, String os) {
		run(algorithm, os, realProblem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testConstraints() {
		TestUtils.assumeFileExists(new File("./pisa/hype_win"));

		run("hype", "win", ProblemFactory.getInstance().getProblem("CF1"));
	}
	
	@Test
	public void testECEA() {
		properties.setProperty("maxIterations", "100");
		run("ecea", "win");
	}
	
	@Test
	@Ignore("possible memory leak, favor built-in implementation")
	public void testEpsilonMOEA() {
		properties.setProperty("mu", "2");
		properties.setProperty("lambda", "2");
		run("epsmoea", "win");
	}
	
	@Test
	public void testFEMO() {
		run("femo", "win");
	}
	
	@Test
	public void testHypE() {
		run("hype", "win");
	}
	
	@Test
	public void testIBEA() {
		run("ibea", "win");
	}
	
	@Test
	@Ignore("need to make design file an argument, otherwise can't parallelize")
	public void testMSOPS() throws IOException {
		FileUtils.copy(
				new File("./pisa/msops_win/msops_weights/" + 
						properties.getProperty("populationSize") + 
						"/space-filling-" +
						realProblem.getNumberOfObjectives() + "dim.des"), 
				new File("space-filling-" +
						realProblem.getNumberOfObjectives() + "dim.des"));
		run("msops", "win");
	}

	@Test
	public void testNSGAII() {
		run("nsga2", "win");
	}
	
	@Test
	@Ignore("crashes with fp != NULL assertion on line 135 in semo_io.c, but recompiling from source fixes this bug")
	public void testSEMO() {
		properties.setProperty("populationSize", "1");
		properties.setProperty("operator", "PM");
		run("semo", "win");
	}
	
	@Test
	public void testSEMO2() {
		run("semo2", "win");
	}
	
	@Test
	public void testSHV() {
		run("shv", "win");
	}
	
	@Test
	public void testSIBEA() {
		String name = "sibea_win";
		String directory = "./pisa/" + name;
		
		TestUtils.assumeFileExists(new File(directory));

		Settings.PROPERTIES.setString(
				"org.moeaframework.algorithm.pisa.algorithms", name);
		Settings.PROPERTIES.setString("org.moeaframework.algorithm.pisa." +
				name + ".command", "java -jar " + directory + "/sibea.jar");
		Settings.PROPERTIES.setString("org.moeaframework.algorithm.pisa." +
				name + ".configuration", directory + "/sibea_param.txt");
		
		test(AlgorithmFactory.getInstance().getAlgorithm(name, properties, 
				realProblem));
		
		Settings.PROPERTIES.remove(
				"org.moeaframework.algorithm.pisa.algorithms");
	}
	
	@Test
	@Ignore("this works, but is very time consuming")
	public void testSPAM() {
		run("spam", "win");
	}
	
	@Test
	public void testSPEA2() {
		run("spea2", "win");
	}
	
	@Test
	public void testUnaryOperators() {
		properties.setProperty("operator", "PM");
		run("semo2", "win");
	}
	
	@Test
	public void testMultiparentOperators() {
		properties.setProperty("mu", "500");
		properties.setProperty("operator", "PCX");
		run("semo2", "win");
	}
	
	@Test
	public void testCaseSensitivity() {
		String name = "hype_win";
		
		Settings.PROPERTIES.setString(
				"org.moeaframework.algorithm.pisa.algorithms", name);
		Settings.PROPERTIES.setString("org.moeaframework.algorithm.pisa." +
				name + ".command", "foo");
		
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				name.toUpperCase(), new Properties(), realProblem);
		
		Assert.assertNotNull(algorithm);
		
		algorithm.terminate();
		
		Settings.PROPERTIES.remove(
				"org.moeaframework.algorithm.pisa.algorithms");
	}
	
	/**
	 * Tests if the given algorithm operates correctly.
	 * 
	 * @param algorithm the algorithm
	 */
	private void test(Algorithm algorithm) {
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
	}
	
}
