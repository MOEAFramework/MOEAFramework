/* Copyright 2009-2012 David Hadka
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
import java.io.IOException;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

/**
 * Demonstrates how problems can be defined externally to the MOEA Framework,
 * possibly written in a different programming language.
 */
public class Example4 {

	/**
	 * The ExternalProblem opens a communication channel with the external
	 * process.  Some Java methods are required to correctly setup the problem
	 * definition.
	 */
	public static class Rosenbrock extends ExternalProblem {

		public Rosenbrock() throws IOException {
			super("./auxiliary/c/rosenbrock.exe");
		}

		/**
		 * Constructs a new solution and defines the bounds of the decision
		 * variables.
		 */
		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), 1);

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new RealVariable(-10.0, 10.0));
			}

			return solution;
		}

		@Override
		public String getName() {
			return "Rosenbrock";
		}

		@Override
		public int getNumberOfVariables() {
			return 2;
		}

		@Override
		public int getNumberOfObjectives() {
			return 1;
		}

		@Override
		public int getNumberOfConstraints() {
			return 0;
		}

	}
	
	public static void main(String[] args) {
		//configure and run the Rosenbrock function
		NondominatedPopulation result = new Executor()
				.withProblemClass(Rosenbrock.class)
				.withAlgorithm("GDE3")
				.withMaxEvaluations(100000)
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
