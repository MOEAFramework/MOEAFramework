/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.studies.multioperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.core.operator.AdaptiveMultimethodVariation;
import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.AdaptiveMetropolis;
import org.moeaframework.core.operator.real.DifferentialEvolution;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.SPX;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.operator.real.UNDX;
import org.moeaframework.core.variable.RealVariable;

public class TestConvergence {
	
	private static double EPSILON = 1e-2;
	
	private static int MAX_EVALUATIONS = 10000;
	
	private static int REPETITIONS = 50;
	
	private static int POPULATION = 100;
	
	private static boolean INITIALIZED = true;
	
	private static boolean SAVE = false;
	
	private static final String[] OPERATORS = new String[] { "SBX", "DE", "PCX", "SPX", "UNDX", "UM", "AM", "Borg" };
	
	private static final MultioperatorTestProblem[] PROBLEMS = new MultioperatorTestProblem[] {
		new Unimodal(),
		new RotatedTestProblem(new Unimodal()),
		new Multimodal(),
		new RotatedTestProblem(new Multimodal()),
		new Plateau(),
		new RotatedTestProblem(new Plateau()) };
	
	public static EvolutionaryAlgorithm newInstance(String operator, final MultioperatorTestProblem problem) {
		Initialization initialization = null;
		
		if (INITIALIZED) {
			initialization = new Initialization() {

				@Override
				public Solution[] initialize() {
					Solution[] solutions = new Solution[POPULATION];
					
					for (int i=0; i<POPULATION; i++) {
						double vx = PRNG.nextGaussian(0.8, 0.05);
						double vy = PRNG.nextGaussian(0.5, 0.05);
						
						if (problem instanceof RotatedTestProblem) {
							double r = -((RotatedTestProblem)problem).getR();
							
							double rx = (vx - 0.5)*Math.cos(r) - (vy - 0.5)*Math.sin(r) + 0.5;
							double ry = (vx - 0.5)*Math.sin(r) + (vy - 0.5)*Math.cos(r) + 0.5;
							
							vx = rx;
							vy = ry;
						}
						
						Solution solution = problem.newSolution();
						RealVariable x = (RealVariable)solution.getVariable(0);
						RealVariable y = (RealVariable)solution.getVariable(1);
						
						x.setValue(vx < 0.0 ? 0.0 : vx > 1.0 ? 1.0 : vx);
						y.setValue(vy < 0.0 ? 0.0 : vy > 1.0 ? 1.0 : vy);
						
						solutions[i] = solution;
					}
					
					return solutions;
				}
				
			};
		} else {
			initialization = new RandomInitialization(problem, POPULATION);
		}

		NondominatedSortingPopulation population = new NondominatedSortingPopulation();

		NondominatedSortingComparator comparator = new NondominatedSortingComparator();

		TournamentSelection selection = new TournamentSelection(2, comparator);

		Variation variation = null;
		
		if (operator.equals("SBX")) {
			variation = new SBX(1.0, 15.0);
		} else if (operator.equals("DE")) {
			variation = new DifferentialEvolution(1.0, 0.5);
		} else if (operator.equals("PCX")) {
			variation = new PCX(3, 2, 0.1, 0.1);
		} else if (operator.equals("SPX")) {
			variation = new SPX(3, 2, 2);
		} else if (operator.equals("UNDX")) {
			variation = new UNDX(3, 2, 0.5, 0.35);
		} else if (operator.equals("UM")) {
			variation = new UM(1.0 / problem.getNumberOfVariables());
		} else if (operator.equals("AM")) {
			variation = new AdaptiveMetropolis(3, 2, 2.4);
		} else if (operator.equals("Borg")) {
			SBX sbx = new SBX(1.0, 15.0);
			DifferentialEvolution de = new DifferentialEvolution(1.0, 0.5);
			PCX pcx = new PCX(3, 2, 0.1, 0.1);
			SPX spx = new SPX(3, 2, 2);
			UNDX undx = new UNDX(3, 2, 0.5, 0.35);
			UM um = new UM(1.0 / problem.getNumberOfVariables());
			PM pm = new PM(1.0 / problem.getNumberOfVariables(), 20.0);

			AdaptiveMultimethodVariation amvariation = new AdaptiveMultimethodVariation(population);
			amvariation.addOperator(new GAVariation(sbx, pm));
			amvariation.addOperator(new GAVariation(de, pm));
			amvariation.addOperator(new GAVariation(pcx, pm));
			amvariation.addOperator(new GAVariation(spx, pm));
			amvariation.addOperator(new GAVariation(undx, pm));
			amvariation.addOperator(um);
			
			variation = amvariation;
		} else {
			throw new IllegalStateException();
		}
		
		if (!operator.equals("Borg")) {
			variation = new GAVariation(variation, new PM(1.0 / problem.getNumberOfVariables(), 20.0));
		}

		return new NSGAII(problem, population, null, selection, variation, initialization);
	}
	
	public static void saveReference(MultioperatorTestProblem problem) throws FileNotFoundException {
		if (SAVE) {
			PrintStream ps = new PrintStream(new FileOutputStream(new File(problem.getName().replaceAll(" ", "_")+ ".ref")));
			
			for (double x = 0.0; x <= 1.0; x += 0.025) {
				for (double y = 0.0; y <= 1.0; y += 0.025) {
					Solution solution = problem.newSolution();
					CoreUtils.fillVariablesFromDoubleArray(solution, new double[] { x, y });
					problem.evaluate(solution);
					
					ps.print(x);
					ps.print(' ');
					ps.print(y);
					ps.print(' ');
					ps.print(solution.getObjective(0));
					ps.println();
				}
			}
			
			ps.close();
		}
	}
	
	public static int savePopulation(MultioperatorTestProblem problem, EvolutionaryAlgorithm algorithm, String operator, int repetition) throws FileNotFoundException {
		PrintStream ps = null;
		
		if (SAVE) {
			ps = new PrintStream(new FileOutputStream(new File(problem.getName().replaceAll(" ", "_") + "_" + operator + "_" + repetition + ".pop")));
		}

		if (ps != null) {
			for (Solution solution : algorithm.getPopulation()) {
				ps.print(((RealVariable)solution.getVariable(0)).getValue());
				ps.print(' ');
				ps.print(((RealVariable)solution.getVariable(1)).getValue());
				ps.print(' ');
				ps.print(solution.getObjective(0));				
				ps.println();
			}
		}
		
		while (!problem.isAtOptimum(algorithm.getPopulation(), EPSILON) && 
				!algorithm.isTerminated() &&
				(algorithm.getNumberOfEvaluations() < MAX_EVALUATIONS)) {
			algorithm.step();
			
			if (ps != null) {
				for (Solution solution : algorithm.getPopulation()) {
					ps.print(((RealVariable)solution.getVariable(0)).getValue());
					ps.print(' ');
					ps.print(((RealVariable)solution.getVariable(1)).getValue());
					ps.print(' ');
					ps.print(solution.getObjective(0));
					ps.println();
				}
			}
		}
		
		if (ps != null) {
			ps.close();
		}
		
		return algorithm.getNumberOfEvaluations();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		for (MultioperatorTestProblem problem : PROBLEMS) {
			System.out.println(problem.getName() + " " + EPSILON);
			saveReference(problem);
			
			for (String operator : OPERATORS) {
				int sum = 0;
				int successful = 0;
				
				for (int i=0; i<REPETITIONS; i++) {
					EvolutionaryAlgorithm algorithm = newInstance(operator, problem);
					int evaluations = savePopulation(problem, algorithm, operator, i);
					
					if (evaluations < MAX_EVALUATIONS) {
						sum += evaluations;
						successful++;
					}
				}
				
				System.out.println(operator);
				System.out.println("  Successful: " + successful + " / " + REPETITIONS);
				System.out.println("  Average # of Evaluations: " + (sum / (double)successful));
			}
		}
	}

}
