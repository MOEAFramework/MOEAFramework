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
package org.moeaframework.examples.external;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

/**
 * Demonstrates writing a external problem (2-objective DTLZ2) in Python.
 * 
 * A few things to note:
 * 
 *   1. setDebugStream is useful for debugging the external program if you notice it stuck or
 *      not behaving correctly.  This will display the variables sent to the program and the
 *      objectives/constraints read back.
 *      
 *   2. If the program appears to be stuck, ensure you are flushing the output after writing
 *      each line.  Often, the output is buffered and must be flushed to write the output
 *      immediately.
 */
public class ExternalProblemWithPython {

	public static class MyDTLZ2 extends ExternalProblem {

		public MyDTLZ2() throws IOException {
			super("python", "examples/dtlz2.py");
		}

		@Override
		public String getName() {
			return "DTLZ2";
		}

		@Override
		public int getNumberOfVariables() {
			return 11;
		}

		@Override
		public int getNumberOfObjectives() {
			return 2;
		}

		@Override
		public int getNumberOfConstraints() {
			return 0;
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());

			for (int i = 0; i < getNumberOfVariables(); i++) {
				solution.setVariable(i, new RealVariable(0.0, 1.0));
			}

			return solution;
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		try (ExternalProblem problem = new MyDTLZ2()) {
			// Uncomment the following to show debugging output
			//problem.setDebugStream(System.out);
			
			Algorithm algorithm = new NSGAII(problem);
			algorithm.run(10000);
			algorithm.getResult().display();
		}
	}
	
}
