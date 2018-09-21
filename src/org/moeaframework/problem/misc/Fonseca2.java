/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.problem.misc;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * The Fonseca (2) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Concave Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Fonseca C. M. and Fleming, P. J. (1995).  "Multiobjective Genetic
 *       Algorithms Made Easy: Selection, Sharing and Mating Restriction."
 *       Genetic Algorithms and Engineering Systems: Innovations and
 *       Applications, 12-14 Sept. 1995, pp. 45-52.
 *   <li>Van Veldhuizen, D. A. (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Fonseca2 extends AbstractProblem implements AnalyticalProblem {
	
	/**
	 * Constructs the Fonseca (2) problem with {@code 3} decision variables.
	 */
	public Fonseca2() {
		this(3);
	}

	/**
	 * Constructs the Fonseca (2) problem with the specified number of decision
	 * variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Fonseca2(int numberOfVariables) {
		super(numberOfVariables, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double f1 = 0.0;
		double f2 = 0.0;
		
		for (int i=0; i<numberOfVariables; i++) {
			f1 += Math.pow(x[i] - 1.0/Math.sqrt(numberOfVariables), 2.0);
			f2 += Math.pow(x[i] + 1.0/Math.sqrt(numberOfVariables), 2.0);
		}
		
		f1 = 1.0 - Math.exp(-f1);
		f2 = 1.0 - Math.exp(-f2);
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 2);

		for (int i=0; i<numberOfVariables; i++) {
			solution.setVariable(i, EncodingUtils.newReal(-4.0, 4.0));
		}

		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		double x = PRNG.nextDouble(-1.0 / Math.sqrt(numberOfVariables),
				1.0 / Math.sqrt(numberOfVariables));
		
		for (int i = 0; i < numberOfVariables; i++) {
			EncodingUtils.setReal(solution.getVariable(i), x);
		}
		
		evaluate(solution);
		return solution;
	}

}
