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
package org.moeaframework.problem;

import java.io.File;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class PymooProblem extends ExternalProblem {
	
	public static final String getScriptPath() {
		return new File("src/test/resources").exists() ?
				new File("src/test/resources/org/moeaframework/problem/pymoo.py").getPath() :
				new File("test/org/moeaframework/problem/pymoo.py").getPath();
	}
	
	private final String pymooProblemName;
	
	private final Problem moeaProblemInstance;
	
	public PymooProblem(String pymooProblemName, Problem moeaProblemInstance) {
		super(new ExternalProblem.Builder().withCommand(
				"python",
				getScriptPath(),
				pymooProblemName,
				Integer.toString(moeaProblemInstance.getNumberOfVariables()),
				Integer.toString(moeaProblemInstance.getNumberOfObjectives()),
				Integer.toString(moeaProblemInstance.getNumberOfConstraints())));
		
		this.pymooProblemName = pymooProblemName;
		this.moeaProblemInstance = moeaProblemInstance;
	}

	@Override
	public String getName() {
		return pymooProblemName;
	}

	@Override
	public int getNumberOfVariables() {
		return moeaProblemInstance.getNumberOfVariables();
	}

	@Override
	public int getNumberOfObjectives() {
		return moeaProblemInstance.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfConstraints() {
		return moeaProblemInstance.getNumberOfConstraints();
	}

	@Override
	public Solution newSolution() {
		return moeaProblemInstance.newSolution();
	}


}
