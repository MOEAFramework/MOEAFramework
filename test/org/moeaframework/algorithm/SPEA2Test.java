package org.moeaframework.algorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import jmetal.core.SolutionSet;
import jmetal.util.Spea2Fitness;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link SPEA2} class.
 */
public class SPEA2Test extends AlgorithmTest {
	
	@Test
	public void test() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		Population population = new Population();
		SolutionSet solutionSet = new SolutionSet(20);
		Initialization initialization = new RandomInitialization(problem, 20);
		
		for (Solution solution : initialization.initialize()) {
			problem.evaluate(solution);
			population.add(solution);
			
			jmetal.core.Solution jmetalSolution = new jmetal.core.Solution(2);
			jmetalSolution.setObjective(0, solution.getObjective(0));
			jmetalSolution.setObjective(1, solution.getObjective(1));
			solutionSet.add(jmetalSolution);
		}
		
		SPEA2 spea2 = new SPEA2(problem, null, null, 0, 1);
		spea2.fitnessEvaluator.evaluate(population);
		Population result1 = spea2.truncate(population, 3);
		
		Spea2Fitness fitness = new Spea2Fitness(solutionSet);
		fitness.fitnessAssign();
		SolutionSet result2 = fitness.environmentalSelection(3);
		
		for (int i = 0; i < population.size(); i++) {
			System.out.println(Arrays.toString(population.get(i).getObjectives()) + " " + population.get(i).getAttribute("fitness"));
		}
		System.out.println("=");
		for (int i = 0; i < solutionSet.size(); i++) {
			System.out.println(solutionSet.get(i).getObjective(0) + " " + solutionSet.get(i).getObjective(1) + " " + solutionSet.get(i).getFitness());
		}
		
		System.out.println();
		System.out.println();
		
		for (int i = 0; i < result1.size(); i++) {
			System.out.println(result1.get(i).getAttribute("fitness"));
		}
		System.out.println("=");
		for (int i = 0; i < result2.size(); i++) {
			System.out.println(result2.get(i).getFitness());
		}
	}
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "SPEA2", "SPEA2-JMetal");
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "SPEA2", "SPEA2-JMetal");
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "SPEA2", "SPEA2-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "SPEA2", "SPEA2-JMetal");
	}
	
	@Test
	public void testComputeDistances() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(), null, null, 0, 1);
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Solution solution3 = TestUtils.newSolution(0.5, 0.5);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		double[][] distances = spea2.computeDistanceMatrix(population);
		
		TestUtils.assertEquals(0.0, distances[0][0]);
		TestUtils.assertEquals(0.0, distances[1][1]);
		TestUtils.assertEquals(0.0, distances[2][2]);
		TestUtils.assertEquals(Math.sqrt(2), distances[0][1]);
		TestUtils.assertEquals(Math.sqrt(2), distances[1][0]);
		TestUtils.assertEquals(Math.sqrt(0.5), distances[0][2]);
		TestUtils.assertEquals(Math.sqrt(0.5), distances[2][0]);
		TestUtils.assertEquals(Math.sqrt(0.5), distances[1][2]);
		TestUtils.assertEquals(Math.sqrt(0.5), distances[2][1]);
	}
	
	@Test
	public void testTruncate1() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(), null, null, 0, 1);
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Solution solution3 = TestUtils.newSolution(0.5, 0.5);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		spea2.fitnessEvaluator.evaluate(population);
		Population result = spea2.truncate(population, 2);
		
		Assert.assertEquals(2, result.size());
		Assert.assertTrue(result.contains(solution1));
		Assert.assertTrue(result.contains(solution2));
		Assert.assertFalse(result.contains(solution3));
	}
	
	@Test
	public void testTruncate2() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(), null, null, 0, 1);
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Solution solution3 = TestUtils.newSolution(0.5, 0.5);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		spea2.fitnessEvaluator.evaluate(population);
		Population result = spea2.truncate(population, 1);
		
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.contains(solution1) || result.contains(solution2));
		Assert.assertFalse(result.contains(solution3));
	}
	
	@Test
	public void testFitnessNondominated() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(), null, null, 0, 1);
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Solution solution3 = TestUtils.newSolution(0.5, 0.5);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		spea2.fitnessEvaluator.evaluate(population);
		
		assertFitnessInRange(solution1, 0, 0.9999);
		assertFitnessInRange(solution2, 0, 0.9999);
		assertFitnessInRange(solution3, 0, 0.9999);
	}
	
	@Test
	public void testFitnessDominated() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(), null, null, 0, 1);
		
		Solution solution1 = TestUtils.newSolution(0.0, 0.0);
		Solution solution2 = TestUtils.newSolution(1.0, 1.0);
		Solution solution3 = TestUtils.newSolution(2.0, 2.0);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		spea2.fitnessEvaluator.evaluate(population);
		
		assertFitnessInRange(solution1, 0, 0.9999);
		assertFitnessInRange(solution2, 2, 2.9999); // ~= S(1) = 2
		assertFitnessInRange(solution3, 3, 3.9999); // ~= S(1) + S(2) = 3
	}
	
	private void assertFitnessInRange(Solution solution, double min, double max) {
		double fitness = (Double)solution.getAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE);
		
		if ((fitness < min) || (fitness > max)) {
			Assert.fail("fitness " + fitness + " not within bounds [" + min + ", " + max + "]");
		}
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testLargeK() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		SPEA2 spea2 = new SPEA2(problem,
				new RandomInitialization(problem, 100),
				OperatorFactory.getInstance().getVariation(null, new Properties(), problem),
				0,
				10000);
		
		for (int i = 0; i < 10; i++) {
			spea2.step();
		}
	}

}
