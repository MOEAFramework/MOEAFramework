/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public class CocoProblemWrapper extends AbstractProblem {
	
	private final Problem problem;
	
	public CocoProblemWrapper(Problem problem) {
		super(problem.getDimension(), problem.getNumberOfObjectives(), problem.getNumberOfConstraints());
		this.problem = problem;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		solution.setObjectives(problem.evaluateFunction(x));
		
		if (numberOfConstraints > 0) {
			solution.setConstraints(problem.evaluateConstraint(x));
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(
					problem.getSmallestValueOfInterest(i), problem.getLargestValueOfInterest(i)));
		}
		
		return solution;
	}
	
	public static void printProblemNames() {
		CocoJNI.cocoSetLogLevel("error");
		
		try {

			final String observer_options = 
					  "result_folder: Foobar_on_bbob-biobj " 
					+ "algorithm_name: Foobar "
					+ "algorithm_info: \"MOEA Framework implementation of Foobar\"";

			Suite suite = new Suite("bbob-biobj", "year: 2016", "dimensions: 2,3,5,10,20,40");
			Observer observer = new Observer("bbob-biobj", observer_options);
			Benchmark benchmark = new Benchmark(suite, observer);
			Problem problem = null;
			
			while ((problem = benchmark.getNextProblem()) != null) {
				System.out.println(problem.getName());
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	public static CocoProblemWrapper findProblem(String algorithm, String problemName) {
		CocoJNI.cocoSetLogLevel("error");
		
		try {

			final String observer_options = 
					  "result_folder: " + algorithm + "_on_bbob-biobj " 
					+ "algorithm_name: " + algorithm + " "
					+ "algorithm_info: \"MOEA Framework implementation of " + algorithm + "\"";

			Suite suite = new Suite("bbob-biobj", "year: 2016", "dimensions: 2,3,5,10,20,40");
			Observer observer = new Observer("bbob-biobj", observer_options);
			Benchmark benchmark = new Benchmark(suite, observer);
			Problem problem = null;
			
			while ((problem = benchmark.getNextProblem()) != null) {
				if (problem.getName().equals(problemName)) {
					return new CocoProblemWrapper(problem);
				}
			}

			return null;
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

}
