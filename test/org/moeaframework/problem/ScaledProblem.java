/* Copyright 2009-2015 David Hadka
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
package org.moeaframework.problem;

import java.io.File;
import java.io.IOException;

import org.moeaframework.core.Population;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class ScaledProblem implements Problem {
	
	private final Problem problem;
	
	private final double[] factors;
	
	public ScaledProblem(Problem problem, double base) {
		super();
		this.problem = problem;
		
		factors = new double[problem.getNumberOfObjectives()];
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			factors[i] = Math.pow(base, i);
		}
	}

	@Override
	public String getName() {
		return "Scaled " + problem.getName();
	}

	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints();
	}

	@Override
	public void evaluate(Solution solution) {
		problem.evaluate(solution);
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			solution.setObjective(i, solution.getObjective(i) * factors[i]);
		}
	}

	@Override
	public Solution newSolution() {
		return problem.newSolution();
	}

	@Override
	public void close() {
		problem.close();
	}
	
	public void scaleReferenceSet(File file, File scaledFile) throws IOException {
		Population population = PopulationIO.readObjectives(file);
		
		for (Solution solution : population) {
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				solution.setObjective(i, solution.getObjective(i) * factors[i]);
			}
		}
		
		PopulationIO.writeObjectives(scaledFile, population);
	}

}
