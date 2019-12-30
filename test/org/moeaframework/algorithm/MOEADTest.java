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

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Operator;
import jmetal.metaheuristics.moead.MOEAD;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.mutation.PolynomialMutation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TravisRunner;
import org.moeaframework.algorithm.jmetal.JMetalAlgorithmAdapter;
import org.moeaframework.algorithm.jmetal.JMetalProblemAdapter;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

/**
 * Tests the {@link MOEAD} class.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class MOEADTest extends AlgorithmTest {
	
	private static class MOEADFactory extends AlgorithmFactory {

		@Override
		public synchronized Algorithm getAlgorithm(String name,
				Properties properties, Problem problem) {
			if (name.equalsIgnoreCase("MOEAD-JMetal")) {
				TypedProperties tp = new TypedProperties(properties);
				JMetalProblemAdapter adapter = new JMetalProblemAdapter(
						problem);
				MOEAD algorithm = new MOEAD(adapter);

				// Algorithm parameters
				algorithm.setInputParameter("populationSize",
						tp.getInt("populationSize", 100));
				algorithm.setInputParameter("maxEvaluations",
						tp.getInt("maxEvaluations", 10000));

				// Directory with the files containing the weight vectors
				// used in [2].  Note that these weights are not used in
				// these tests, but the parameter must be set to avoid
				// a NullPointerException.
				algorithm.setInputParameter("dataDirectory", "Weight");

				// Crossover operator
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("CR", 0.1);
				parameters.put("F", 0.5);
				Operator crossover = new DifferentialEvolutionCrossover(parameters);

				// Mutation operator
				parameters = new HashMap<String, Object>();
				parameters.put("probability", 1.0 / problem.getNumberOfVariables());
				parameters.put("distributionIndex", 20.0);
				Operator mutation = new PolynomialMutation(parameters);

				algorithm.addOperator("crossover",crossover);
				algorithm.addOperator("mutation",mutation);

				return new JMetalAlgorithmAdapter(algorithm, adapter);
			} else {
				return super.getAlgorithm(name, properties, problem);
			}
		}
		
	}
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "MOEAD", "MOEAD-JMetal", new MOEADFactory());
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "MOEAD", "MOEAD-JMetal", new MOEADFactory());
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "MOEAD", "MOEAD-JMetal", new MOEADFactory());
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "MOEAD", "MOEAD-JMetal", new MOEADFactory());
	}
	
	@Test
	public void testSelection() {
		org.moeaframework.algorithm.MOEAD moead = null;
		
		Problem problem = new MockRealProblem();
		Properties properties = new Properties();
		
		//the default is de+pm
		moead = (org.moeaframework.algorithm.MOEAD)AlgorithmFactory.getInstance()
				.getAlgorithm("MOEA/D", properties, problem);
		
		Assert.assertTrue(moead.useDE);
		
		//test with just de
		properties.setProperty("operator", "de");
		
		moead = (org.moeaframework.algorithm.MOEAD)AlgorithmFactory.getInstance()
				.getAlgorithm("MOEA/D", properties, problem);
		
		Assert.assertTrue(moead.useDE);
		
		//test with a different operator
		properties.setProperty("operator", "sbx+pm");
		
		moead = (org.moeaframework.algorithm.MOEAD)AlgorithmFactory.getInstance()
				.getAlgorithm("MOEA/D", properties, problem);
		
		Assert.assertFalse(moead.useDE);
	}

}
