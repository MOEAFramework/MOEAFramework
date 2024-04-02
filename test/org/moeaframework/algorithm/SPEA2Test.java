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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.TypedProperties;

@RunWith(CIRunner.class)
@Retryable
public class SPEA2Test extends JMetalAlgorithmTest {
	
	public SPEA2Test() {
		super("SPEA2", true);
	}
	
	@Test
	public void testComputeDistances() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(2));
		
		Population population = new Population();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		population.add(MockSolution.of().withObjectives(0.5, 0.5));
		
		double[][] distances = spea2.computeDistanceMatrix(population);
		
		Assert.assertEquals(0.0, distances[0][0]);
		Assert.assertEquals(0.0, distances[1][1]);
		Assert.assertEquals(0.0, distances[2][2]);
		Assert.assertEquals(Math.sqrt(2), distances[0][1]);
		Assert.assertEquals(Math.sqrt(2), distances[1][0]);
		Assert.assertEquals(Math.sqrt(0.5), distances[0][2]);
		Assert.assertEquals(Math.sqrt(0.5), distances[2][0]);
		Assert.assertEquals(Math.sqrt(0.5), distances[1][2]);
		Assert.assertEquals(Math.sqrt(0.5), distances[2][1]);
	}
	
	@Test
	public void testTruncate1() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(2));
		
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.5, 0.5);
		
		Population population = new Population();
		population.addAll(List.of(solution1, solution2, solution3));
		
		spea2.getFitnessEvaluator().evaluate(population);
		Population result = spea2.truncate(population, 2);
		
		Assert.assertSize(2, result);
		Assert.assertContains(result, solution1);
		Assert.assertContains(result, solution2);
		Assert.assertNotContains(result, solution3);
	}
	
	@Test
	public void testTruncate2() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(2));
		
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.5, 0.5);
		
		Population population = new Population();
		population.addAll(List.of(solution1, solution2, solution3));
		
		spea2.getFitnessEvaluator().evaluate(population);
		Population result = spea2.truncate(population, 1);
		
		Assert.assertSize(1, result);
		Assert.any(() -> Assert.assertContains(result, solution1), () -> Assert.assertContains(result, solution2));
		Assert.assertNotContains(result, solution3);
	}
	
	@Test
	public void testFitnessNondominated() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(2));
		
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(0.5, 0.5);
		
		Population population = new Population();
		population.addAll(List.of(solution1, solution2, solution3));
		
		spea2.getFitnessEvaluator().evaluate(population);
		
		assertFitnessInRange(solution1, 0, 0.9999);
		assertFitnessInRange(solution2, 0, 0.9999);
		assertFitnessInRange(solution3, 0, 0.9999);
	}
	
	@Test
	public void testFitnessDominated() {
		SPEA2 spea2 = new SPEA2(new MockRealProblem(2));
		
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(2.0, 2.0);
		
		Population population = new Population();
		population.addAll(List.of(solution1, solution2, solution3));
		
		spea2.getFitnessEvaluator().evaluate(population);
		
		assertFitnessInRange(solution1, 0, 0.9999);
		assertFitnessInRange(solution2, 2, 2.9999); // ~= S(1) = 2
		assertFitnessInRange(solution3, 3, 3.9999); // ~= S(1) + S(2) = 3
	}
	
	private void assertFitnessInRange(Solution solution, double min, double max) {
		double fitness = FitnessEvaluator.getFitness(solution);
		
		if ((fitness < min) || (fitness > max)) {
			Assert.fail("fitness " + fitness + " not within bounds [" + min + ", " + max + "]");
		}
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testLargeK() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		SPEA2 spea2 = new SPEA2(problem,
				100,
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation(null, new TypedProperties(), problem),
				100,
				10000);
		
		for (int i = 0; i < 10; i++) {
			spea2.step();
		}
	}

}
