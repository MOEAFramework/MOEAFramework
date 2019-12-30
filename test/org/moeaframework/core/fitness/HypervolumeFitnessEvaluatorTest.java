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
package org.moeaframework.core.fitness;

import java.util.Arrays;

import jmetal.core.SolutionSet;
import jmetal.metaheuristics.ibea.IBEA;

import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.algorithm.jmetal.JMetalProblemAdapter;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link HypervolumeFitnessEvaluator} class.
 */
public class HypervolumeFitnessEvaluatorTest {
	
	@Test
	public void test() {
		Population population = new Population();
		population.add(TestUtils.newSolution(0.0, 0.0));
		population.add(TestUtils.newSolution(0.0, 1.0));
		population.add(TestUtils.newSolution(1.0, 0.0));
		population.add(TestUtils.newSolution(0.5, 0.5));
		
		FitnessEvaluator fitnessEvaluator = new HypervolumeFitnessEvaluator(
				new MockRealProblem());
		fitnessEvaluator.evaluate(population);
		
		for (Solution solution : population) {
			System.out.println(Arrays.toString(solution.getObjectives()) + " " + solution.getAttribute("fitness"));
		}
	}

	@Test
	public void testDTLZ2_2D() {
		test("DTLZ2_2");
	}
	
	@Test
	public void testDTLZ2_4D() {
		test("DTLZ2_4");
	}
	
	@Test
	public void testDTLZ2_6D() {
		test("DTLZ2_6");
	}
	
	@Test
	public void testDTLZ2_8D() {
		test("DTLZ2_8");
	}
	
	private void test(String problemName) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		Population myPopulation = generatePopulation(problem, 100);
		
		//translate population to JMetal solution set
		SolutionSet theirPopulation = new SolutionSet(myPopulation.size());
		
		for (Solution mySolution : myPopulation) {
			jmetal.core.Solution theirSolution = new jmetal.core.Solution(
					mySolution.getNumberOfObjectives());
			
			for (int i=0; i<mySolution.getNumberOfObjectives(); i++) {
				theirSolution.setObjective(i, mySolution.getObjective(i));
			}
			
			theirPopulation.add(theirSolution);
		}
		
		//compute JMetal fitnesses using IBEA
		IBEA ibea = new IBEA(new JMetalProblemAdapter(problem));
		ibea.calculateFitness(theirPopulation);
		
		//compute our fitnesses
		FitnessEvaluator fitnessEvaluator = new HypervolumeFitnessEvaluator(
				problem);
		fitnessEvaluator.evaluate(myPopulation);
		
		//compare indicator values
		for (int i=0; i<myPopulation.size(); i++) {
			TestUtils.assertEquals(theirPopulation.get(i).getFitness(),
					(Double)myPopulation.get(i).getAttribute(
							FitnessEvaluator.FITNESS_ATTRIBUTE));
		}
	}
	
	private Population generatePopulation(Problem problem, int N) {
		Initialization initialization = new RandomInitialization(problem, N);
		Solution[] solutions = initialization.initialize();

		for (Solution solution : solutions) {
			problem.evaluate(solution);
		}

		return new Population(solutions);
	}
	
}
