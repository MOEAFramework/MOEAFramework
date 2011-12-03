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
import org.moeaframework.Executor;
import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Demonstrates how a new problem is defined and used within the MOEA
 * Framework.
 */
public class Example3 {

	/**
	 * Implementation of the Rosenbrock function.  The optimum is at (1, 1)
	 * with an objective value of 0.
	 */
	public static class Rosenbrock extends AbstractProblem {

		/**
		 * Constructs a new instance of the Rosenbrock function, defining it
		 * to include 2 decision variables and 1 objective.
		 */
		public Rosenbrock() {
			super(2, 1);
		}

		/**
		 * Extracts the decision variables from the solution, evaluates the
		 * Rosenbrock function, and saves the resulting objective value back to
		 * the solution. 
		 */
		@Override
		public void evaluate(Solution solution) {
			double sum = 0.0;
			double[] x = CoreUtils.castVariablesToDoubleArray(solution);

			for (int j=0; j<100; j++) {
			for (int i = 0; i < numberOfVariables - 1; i++) {
				sum += Math.pow(1.0 - x[i], 2.0) + 100.0
						* Math.pow(x[i + 1] - Math.pow(x[i], 2.0), 2.0);
			}
			}

			solution.setObjective(0, sum);
		}

		/**
		 * Constructs a new solution and defines the bounds of the decision
		 * variables.
		 */
		@Override
		public Solution newSolution() {
			Solution solution = new Solution(numberOfVariables, 1);

			for (int i = 0; i < numberOfVariables; i++) {
				solution.setVariable(i, new RealVariable(-10.0, 10.0));
			}

			return solution;
		}

	}
	
	public static void main(String[] args) {
		//configure and run the Rosenbrock function
		NondominatedPopulation result = new Executor()
				.withProblemClass(Rosenbrock.class)
				.withAlgorithm("GDE3")
				.withMaxEvaluations(100000)
				.distributeOnAllCores()
				.run();
				
		//display the results
		for (Solution solution : result) {
			System.out.print(solution.getVariable(0));
			System.out.print(" ");
			System.out.print(solution.getVariable(1));
			System.out.print(" => ");
			System.out.println(solution.getObjective(0));
		}
	}
	
}
