/* Copyright 2009-2023 David Hadka
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
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * It's also very easy to add your own test or real-world problems for use in the MOEA
 * Framework.  Here we recreate the Srinivas test problem and solve it using NSGA-II. 
 */
public class Example6 {

	public static class Srinivas extends AbstractProblem {

		/**
		 * Creates the problem with two decision variables, two objectives, and two constraints.
		 */
		public Srinivas() {
			super(2, 2, 2);
		}

		/**
		 * Function to evaluate each solution.
		 */
		@Override
		public void evaluate(Solution solution) {
			double[] x = EncodingUtils.getReal(solution);
			double f1 = Math.pow(x[0] - 2.0, 2.0) + Math.pow(x[1] - 1.0, 2.0) + 2.0;
			double f2 = 9.0*x[0] - Math.pow(x[1] - 1.0, 2.0);
			double c1 = Math.pow(x[0], 2.0) + Math.pow(x[1], 2.0) - 225.0;
			double c2 = x[0] - 3.0*x[1] + 10.0;
			
			// set the objective values - these are being minimized
			solution.setObjective(0, f1);
			solution.setObjective(1, f2);
			
			// set the constraint values - we treat any non-zero value as a constraint violation!
			solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
			solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
		}

		/**
		 * Function to create a new solution.  Here is where we define the types and
		 * bounds of each decision variables.  In this example, we have two real-valued
		 * variables ranging from -20 to 20.
		 */
		@Override
		public Solution newSolution() {
			Solution solution = new Solution(2, 2, 2);
			
			solution.setVariable(0, new RealVariable(-20.0, 20.0));
			solution.setVariable(1, new RealVariable(-20.0, 20.0));
			
			return solution;
		}
		
	}
	
	public static void main(String[] args) {
		Problem problem = new Srinivas();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		algorithm.getResult().display();
	}
	
}
