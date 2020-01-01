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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.moeaframework.TestUtils;
import org.moeaframework.algorithm.jmetal.JMetalFactory;
import org.moeaframework.algorithm.jmetal.JMetalUtils;
import org.moeaframework.algorithm.jmetal.ProblemAdapter;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void test(String problemName) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		Population myPopulation = generatePopulation(problem, 100);
		
		//translate population to JMetal solution set
		ProblemAdapter<? extends org.uma.jmetal.solution.Solution<?>> problemAdapter = JMetalUtils.createProblemAdapter(problem);
		List<org.uma.jmetal.solution.Solution<?>> theirPopulation = new ArrayList<org.uma.jmetal.solution.Solution<?>>();

		for (Solution mySolution : myPopulation) {
			org.uma.jmetal.solution.Solution<?> theirSolution = problemAdapter.createSolution();
			JMetalUtils.copyObjectivesAndConstraints(mySolution, theirSolution);
			theirPopulation.add(theirSolution);
		}
		
		//compute JMetal fitnesses using IBEA
		TypedProperties properties = new TypedProperties();
		CrossoverOperator<?> crossover = JMetalFactory.getInstance().createCrossoverOperator(problemAdapter, properties);
		MutationOperator<?> mutation = JMetalFactory.getInstance().createMutationOperator(problemAdapter, properties);
	    SelectionOperator selection = new BinaryTournamentSelection();
	    
		IBEA ibea = new IBEA(problemAdapter, 100, 100, 25000, selection, crossover, mutation);
		ibea.calculateFitness(theirPopulation);
		
		//compute our fitnesses
		FitnessEvaluator fitnessEvaluator = new HypervolumeFitnessEvaluator(
				problem);
		fitnessEvaluator.evaluate(myPopulation);
		
		//compare indicator values
		for (int i=0; i<myPopulation.size(); i++) {
			TestUtils.assertEquals(
					(Double)new Fitness().getAttribute(
							theirPopulation.get(i)),
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
