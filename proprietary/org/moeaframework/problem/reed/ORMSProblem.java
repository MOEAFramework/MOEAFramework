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
package org.moeaframework.problem.reed;

import java.io.IOException;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ExternalProblem;

public class ORMSProblem extends ExternalProblem {

	public ORMSProblem() throws IOException {
		super("./evaluator");
	}

	@Override
	public String getName() {
		return "ORMS";
	}

	@Override
	public int getNumberOfObjectives() {
		return 4;
	}

	@Override
	public int getNumberOfVariables() {
		return 14;
	}

	@Override
	public int getNumberOfConstraints() {
		return 0;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(14, 4);
		solution.setVariable(0, new RealVariable(25, 125));
		solution.setVariable(1, new RealVariable(10, 75));
		solution.setVariable(2, new RealVariable(0.2, 0.5));
		solution.setVariable(3, new RealVariable(0, 0.05));
		solution.setVariable(4, new RealVariable(0, 0.2));
		solution.setVariable(5, new RealVariable(0, 0.2));
		solution.setVariable(6, new RealVariable(20, 300));
		solution.setVariable(7, new RealVariable(1.4, 3.5));
		solution.setVariable(8, new RealVariable(75, 300));
		solution.setVariable(9, new RealVariable(15, 300));
		solution.setVariable(10, new RealVariable(40, 600));
		solution.setVariable(11, new RealVariable(0.03, 0.2));
		solution.setVariable(12, new RealVariable(0.001, 0.015));
		solution.setVariable(13, new RealVariable(0, 0.5));
		return solution;
	}

}
