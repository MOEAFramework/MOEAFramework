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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.DTLZ.DTLZ2;
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
		
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(1.0, 0.0));
		
		Assert.assertEquals(2, algorithm.numberOfUniqueSolutions(population));
		
		population.add(TestUtils.newSolution(0.0, 1.0));
		
		Assert.assertEquals(2, algorithm.numberOfUniqueSolutions(population));
	}
	
	@Test
	public void testOrderBySmallestObjective() {
		Solution solution1 = TestUtils.newSolution(0.5, 0.5);
		Solution solution2 = TestUtils.newSolution(0.0, 1.0);
		Solution solution3 = TestUtils.newSolution(1.0, 0.0);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		Population result = algorithm.orderBySmallestObjective(0, population);
		
		Assert.assertSame(solution2, result.get(0));
		Assert.assertSame(solution1, result.get(1));
		Assert.assertSame(solution3, result.get(2));
	}
	
	@Test
	public void testOrderBySmallestSquaredValue() {
		Solution solution1 = TestUtils.newSolution(0.5, 0.0);
		Solution solution2 = TestUtils.newSolution(0.0, 0.0);
		Solution solution3 = TestUtils.newSolution(1.0, 0.0);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		Population result = algorithm.orderBySmallestSquaredValue(1, population);
		
		Assert.assertSame(solution2, result.get(0));
		Assert.assertSame(solution1, result.get(1));
		Assert.assertSame(solution3, result.get(2));
	}
	
	@Test
	public void testLargestObjectiveValue() {
		Solution solution1 = TestUtils.newSolution(0.5, 0.0);
		Solution solution2 = TestUtils.newSolution(0.0, 0.0);
		Solution solution3 = TestUtils.newSolution(1.0, 0.0);
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		
		Solution result = algorithm.largestObjectiveValue(0, population);
		
		Assert.assertSame(solution3, result);
	}
	
	@Test
	public void testSumOfConstraintValues() {
		Solution solution = new Solution(0, 0, 3);
		solution.setConstraints(new double[] { -1.0, 1.0, 0.0 });
		
		Assert.assertEquals(2.0, algorithm.sumOfConstraintViolations(solution), Settings.EPS);
	}
	
	@Test
	public void testGetFeasibleSolutions() {
		Solution solution1 = new Solution(0, 0, 3);
		solution1.setConstraints(new double[] { -1.0, 1.0, 0.0 });
		
		Solution solution2 = new Solution(0, 0, 3);
		solution2.setConstraints(new double[] { 0.0, 0.0, 0.0 });
		
		Population population = new Population();
		population.add(solution1);
		population.add(solution2);
		
		Population result = algorithm.getFeasibleSolutions(population);
		
		Assert.assertEquals(1, result.size());
		Assert.assertSame(solution2, result.get(0));
	}
	
	/**
	 * Compares the intermediate values between the Matlab/Octave version of DBEA and the Java version.  The
	 * Matlab/Octave version can be downloaded from the experimental repository.
	 */
	@Test
	@Ignore("Must download DBEA Matlab/Octave code to use this test")
	public void test() throws IOException, InterruptedException {
		File directory = new File("Matlab-DBEA");
		Problem problem = new DTLZ2(15);
		
		DBEA.TESTING_MODE = true;
		DBEA dbea = new DBEA(problem, new NormalBoundaryDivisions(3, 0));
		dbea.generateWeights();
		
		String line = null;
		
		// get the initial population from Matlab/Octave
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "init_pop.txt")))) {
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				
				Solution solution = problem.newSolution();
				String[] tokens = line.trim().split("\\s+");
				
				for (int i = 0; i < problem.getNumberOfVariables(); i++) {
					EncodingUtils.setReal(solution.getVariable(i), Double.parseDouble(tokens[i]));
				}
				
				dbea.getPopulation().add(solution);
			}
		} 
		
		// check that the objectives are identical
		dbea.evaluateAll(dbea.getPopulation());
		
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "init_obj.txt")))) {
			int index = 0;
			
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				
				String[] tokens = line.trim().split("\\s+");
				
				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					double expected = Double.parseDouble(tokens[i]);
					double actual = dbea.getPopulation().get(index).getObjective(i);
					
					if (Math.abs(expected - actual) > 0.0001) {
						Assert.fail("Objective values do not match");
					}
				}
				
				index++;
			}
		}
		
		// check the initial corner calculation
		dbea.preserveCorner();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "init_corner.txt")))) {
			int index = 0;
			
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				
				String[] tokens = line.trim().split("\\s+");
				
				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					double expected = Double.parseDouble(tokens[i]);
					double actual = dbea.corner.get(index).getObjective(i);
					
					if (Math.abs(expected - actual) > 0.0001) {
						Assert.fail("Corners do not match");
					}
				}
				
				index++;
			}
		}
		
		// check that the initial ideal point and intercept calculation is correct
		dbea.initializeIdealPointAndIntercepts();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "idealpoint.txt")))) {
			line = reader.readLine();
			String[] tokens = line.trim().split("\\s+");
			
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				double expected = Double.parseDouble(tokens[i]);
				double actual = dbea.idealPoint[i];
					
				if (Math.abs(expected - actual) > 0.0001) {
					Assert.fail("Ideal points do not match");
				}
			}
		}
		
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "intercept.txt")))) {
			line = reader.readLine();
			String[] tokens = line.trim().split("\\s+");
			
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				double expected = Double.parseDouble(tokens[i]);
				double actual = dbea.intercepts[i];
					
				if (Math.abs(expected - actual) > 0.0001) {
					Assert.fail("Intercepts do not match");
				}
			}
		}
		
		// loop until finished
		int count = 0;
		
		while (count < 1000) {
			System.out.println(count++);
			
			while (!new File(directory, "child.txt").exists()) {
				Thread.sleep(500);
			}
			
			// read the child solution generated by Matlab/Octave
			Solution child = null;
			
			try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "child.txt")))) {
				line = reader.readLine();
				String[] tokens = line.trim().split("\\s+");
				
				child = problem.newSolution();
				
				for (int i = 0; i < problem.getNumberOfVariables(); i++) {
					EncodingUtils.setReal(child.getVariable(i), Double.parseDouble(tokens[i]));
				}
			}
			
			// ensure the check domination method is valid
			dbea.evaluate(child);
			boolean dominated = dbea.checkDomination(child);
			
			try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "dom_flag.txt")))) {
				line = reader.readLine();
				int value = (int)Double.parseDouble(line.trim());
				
				if ((value == 1 && !dominated) || (value == 0 && dominated)) {
					// ok
				} else {
					Assert.fail("Check domination does not match");
				}
			}
			
			if (!dominated) {
				// ensure the ideal point and intercept update correctly
				dbea.updateIdealPointAndIntercepts(child);
			
				try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "updated_idealpoint.txt")))) {
					line = reader.readLine();
					String[] tokens = line.trim().split("\\s+");
					
					for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
						double expected = Double.parseDouble(tokens[i]);
						double actual = dbea.idealPoint[i];
							
						if (Math.abs(expected - actual) > 0.0001) {
							Assert.fail("Updated ideal points do not match");
						}
					}
				}
				
				try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "updated_intercept.txt")))) {
					line = reader.readLine();
					String[] tokens = line.trim().split("\\s+");
					
					for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
						double expected = Double.parseDouble(tokens[i]);
						double actual = dbea.intercepts[i];
							
						if (Math.abs(expected - actual) > 0.0001) {
							Assert.fail("Updated intercepts do not match");
						}
					}
				}
				
				// check if the update population method is correct
				dbea.updatePopulation(child);
				
				try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "updated_pop.txt")))) {
					int index = 0;
					
					while ((line = reader.readLine()) != null) {
						if (line.isEmpty()) {
							continue;
						}
						
						String[] tokens = line.trim().split("\\s+");
						
						for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
							double expected = Double.parseDouble(tokens[i]);
							double actual = dbea.getPopulation().get(index).getObjective(i);
							
							if (Math.abs(expected - actual) > 0.0001) {
								Assert.fail("Updated populations do not match");
							}
						}
						
						index++;
					}
				}
				
				try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, "updated_corner.txt")))) {
					int index = 0;
					
					while ((line = reader.readLine()) != null) {
						if (line.isEmpty()) {
							continue;
						}
						
						String[] tokens = line.trim().split("\\s+");

						for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
							double expected = Double.parseDouble(tokens[i]);
							double actual = dbea.corner.get(index).getObjective(i);
							
							if (Math.abs(expected - actual) > 0.0001) {
								Assert.fail("Updated corners do not match");
							}
						}
						
						index++;
					}
				}
			} else {
				System.out.println("Child is dominated");
			}
			
			// let Matlab/Octave know that it can proceed to the next child
			new File(directory, "child.txt").delete();
		}
	}

}
