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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

public class DBEATest {
	
	private Problem problem;
	private DBEA algorithm;
	
	@Before
	public void setUp() {
		problem = new MockRealProblem(2);
		algorithm = new DBEA(problem);
	}
	
	@After
	public void tearDown() {
		problem = null;
		algorithm = null;
	}
	
	@Test
	public void testDefaults() {
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.forProblem(problem);
		Assert.assertEquals(divisions, algorithm.getDivisions());
		Assert.assertEquals(divisions.getNumberOfReferencePoints(problem), algorithm.getInitialPopulationSize());
	}
	
	@Test
	public void testConfiguration() {
		NormalBoundaryDivisions divisions = new NormalBoundaryDivisions(100);
		
		algorithm.applyConfiguration(divisions.toProperties());
		
		Assert.assertEquals(divisions, algorithm.getDivisions());
		Assert.assertEquals(divisions.getNumberOfReferencePoints(problem), algorithm.getInitialPopulationSize());
	}
	
	@Test
	public void testNumberOfUniqueSolutions() {
		Population population = new Population();
		
		Assert.assertEquals(0, algorithm.numberOfUniqueSolutions(population));
		
		population.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		population.add(MockSolution.of(problem).withObjectives(1.0, 0.0));
		
		Assert.assertEquals(2, algorithm.numberOfUniqueSolutions(population));
		
		population.add(MockSolution.of(problem).withObjectives(0.0, 1.0));
		
		Assert.assertEquals(2, algorithm.numberOfUniqueSolutions(population));
	}
	
	@Test
	public void testOrderBySmallestObjective() {
		Solution solution1 = MockSolution.of(problem).withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of(problem).withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of(problem).withObjectives(1.0, 0.0);
		
		Population population = new Population(List.of(solution1, solution2, solution3));
		
		Population result = algorithm.orderBySmallestObjective(0, population);
		
		Assert.assertSame(solution2, result.get(0));
		Assert.assertSame(solution1, result.get(1));
		Assert.assertSame(solution3, result.get(2));
	}
	
	@Test
	public void testOrderBySmallestSquaredValue() {
		Solution solution1 = MockSolution.of(problem).withObjectives(0.5, 0.0);
		Solution solution2 = MockSolution.of(problem).withObjectives(0.0, 0.0);
		Solution solution3 = MockSolution.of(problem).withObjectives(1.0, 0.0);
		
		Population population = new Population(List.of(solution1, solution2, solution3));
		
		Population result = algorithm.orderBySmallestSquaredValue(1, population);
		
		Assert.assertSame(solution2, result.get(0));
		Assert.assertSame(solution1, result.get(1));
		Assert.assertSame(solution3, result.get(2));
	}
	
	@Test
	public void testLargestObjectiveValue() {
		Solution solution1 = MockSolution.of(problem).withObjectives(0.5, 0.0);
		Solution solution2 = MockSolution.of(problem).withObjectives(0.0, 0.0);
		Solution solution3 = MockSolution.of(problem).withObjectives(1.0, 0.0);
		
		Population population = new Population(List.of(solution1, solution2, solution3));
		Solution result = algorithm.largestObjectiveValue(0, population);
		
		Assert.assertSame(solution3, result);
	}
	
	@Test
	public void testGetFeasibleSolutions() {
		Solution solution1 = MockSolution.of().withConstraints(-1.0, 1.0, 0.0);
		Solution solution2 = MockSolution.of().withConstraints(0.0, 0.0, 0.0);
		
		Population population = new Population(List.of(solution1, solution2));
		Population result = algorithm.getFeasibleSolutions(population);
		
		Assert.assertEquals(1, result.size());
		Assert.assertSame(solution2, result.get(0));
	}
	
	@Test
	public void testCornerSort() {
		Solution solution1 = MockSolution.of(problem).withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of(problem).withObjectives(0.25, 0.75);
		Solution solution3 = MockSolution.of(problem).withObjectives(0.5, 0.5);
		Solution solution4 = MockSolution.of(problem).withObjectives(0.75, 0.25);
		Solution solution5 = MockSolution.of(problem).withObjectives(1.0, 0.0);
		Solution solution6 = MockSolution.of(problem).withObjectives(0.1, 1.1);
		Solution solution7 = MockSolution.of(problem).withObjectives(1.1, 0.1);
		
		Population population = new Population(List.of(solution1, solution2, solution3, solution4, solution5,
				solution6, solution7));
		
		Population result = algorithm.corner_sort(population);
		
		Assert.assertEquals(4, result.size());
		Assert.assertTrue(result.containsAll(List.of(solution1, solution5, solution6, solution7)));
	}
	
	@Test
	public void testCornerSortWithDuplicates() {
		Solution solution1 = MockSolution.of(problem).withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of(problem).withObjectives(0.25, 0.75);
		Solution solution3 = MockSolution.of(problem).withObjectives(0.5, 0.5);
		Solution solution4 = MockSolution.of(problem).withObjectives(0.75, 0.25);
		Solution solution5 = MockSolution.of(problem).withObjectives(1.0, 0.0);
		Solution solution6 = MockSolution.of(problem).withObjectives(0.0, 1.0);
		Solution solution7 = MockSolution.of(problem).withObjectives(1.0, 0.0);
		
		Population population = new Population(List.of(solution1, solution2, solution3, solution4, solution5,
				solution6, solution7));
		
		Population result = algorithm.corner_sort(population);
		
		Assert.assertEquals(4, result.size());
		Assert.assertTrue(result.containsAll(List.of(solution1, solution2, solution4, solution5)));
	}
	
	@Test
	public void testCheckDomination() {
		algorithm.getPopulation().addAll(List.of(
				MockSolution.of(problem).withObjectives(0.5, 0.5),
				MockSolution.of(problem).withObjectives(0.0, 1.0)));
		
		Assert.assertTrue(algorithm.checkDomination(MockSolution.of().withObjectives(0.75, 0.75)));
		Assert.assertFalse(algorithm.checkDomination(MockSolution.of().withObjectives(0.25, 0.25)));
		Assert.assertFalse(algorithm.checkDomination(MockSolution.of().withObjectives(0.5, 0.5)));
		Assert.assertFalse(algorithm.checkDomination(MockSolution.of().withObjectives(1.0, 0.0)));
		Assert.assertFalse(algorithm.checkDomination(MockSolution.of().withObjectives(0.25, 0.25).withConstraintViolation()));
	}

}
