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
package org.moeaframework.algorithm;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;

/**
 * Random search implementation.  An {@link Initialization} instance is used
 * to generate random solutions, which are evaluated and all non-dominated
 * solutions retained.  The result is the set of all non-dominated solutions.
 */
public class RandomSearch extends AbstractAlgorithm {
	
	/**
	 * The initialization routine used to generate random solutions.
	 */
	private final Initialization generator;
	
	/**
	 * The archive of non-dominated solutions.
	 */
	private final NondominatedPopulation archive;

	/**
	 * Constructs a new random search procedure for the given problem.
	 * 
	 * @param problem the problem being solved
	 * @param generator the initialization routine used to generate random
	 *        solutions
	 * @param archive the archive of non-dominated solutions
	 */
	public RandomSearch(Problem problem, Initialization generator,
			NondominatedPopulation archive) {
		super(problem);
		this.generator = generator;
		this.archive = archive;
	}

	@Override
	public NondominatedPopulation getResult() {
		return archive;
	}

	@Override
	protected void initialize() {
		super.initialize();
		iterate();
	}

	@Override
	protected void iterate() {
		Population solutions = new Population(generator.initialize());
		evaluateAll(solutions);
		archive.addAll(solutions);
	}

}
