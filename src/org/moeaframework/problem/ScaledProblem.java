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
import java.io.IOException;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Decorator to create scaled variants of problems.  The {@code i}-th objective is scaled by a factor of
 * {@code base^i}.  This can be used to determine if an algorithm is able to handle objectives of varying magnitudes.
 * <p>
 * Use {@link #loadScaledReferenceSet(File)} or {@link #createScaledReferenceSet(File, File)} to convert the original
 * Pareto front file into the scaled version.
 */
public class ScaledProblem extends ProblemWrapper {
	
	/**
	 * The scaling factor for each objective.
	 */
	private final double[] factors;
	
	/**
	 * Constructs a new scaled problem.
	 * 
	 * @param problem the original, unscaled problem
	 * @param base the base for scaling
	 */
	public ScaledProblem(Problem problem, double base) {
		super(problem);
		
		factors = new double[problem.getNumberOfObjectives()];
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			factors[i] = Math.pow(base, i);
		}
	}

	@Override
	public String getName() {
		return "Scaled " + super.getName();
	}

	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		scale(solution);
	}
	
	/**
	 * Loads the reference set file and scales the solutions.
	 * 
	 * @param file the reference set file
	 * @return the scaled reference set
	 * @throws IOException if an I/O error occurred
	 */
	public NondominatedPopulation loadScaledReferenceSet(File file) throws IOException {
		NondominatedPopulation result = new NondominatedPopulation();
		
		for (Solution solution : NondominatedPopulation.loadReferenceSet(file)) {
			scale(solution);
			result.add(solution);
		}
		
		return result;
	}
	
	/**
	 * Converts a reference set into its scaled version.
	 * 
	 * @param file the original, unscaled reference set file
	 * @param scaledFile the scaled reference set file
	 * @throws IOException if an I/O error occurred
	 */
	public void createScaledReferenceSet(File file, File scaledFile) throws IOException {
		loadScaledReferenceSet(file).saveObjectives(scaledFile);
	}
	
	/**
	 * Scales the objectives of this solution in place.
	 * 
	 * @param solution the solution to scale
	 */
	private void scale(Solution solution) {
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			solution.setObjective(i, solution.getObjective(i) * factors[i]);
		}
	}

}