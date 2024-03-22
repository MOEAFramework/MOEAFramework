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
package org.moeaframework.algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.initialization.RandomInitialization;

/**
 * Random search implementation.  An {@link Initialization} instance is used to generate random solutions, which are
 * evaluated and all non-dominated solutions retained.  The result is the set of all non-dominated solutions.
 */
public class RandomSearch extends AbstractAlgorithm implements Configurable {
	
	/**
	 * The number of solutions sampled each iteration.
	 */
	private int sampleSize;
	
	/**
	 * The initialization routine used to generate random solutions.
	 */
	private final Initialization generator;
	
	/**
	 * The archive of non-dominated solutions.
	 */
	private final NondominatedPopulation archive;
	
	/**
	 * Constructs a new random search procedure with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public RandomSearch(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new RandomInitialization(problem),
				new NondominatedPopulation());
	}

	/**
	 * Constructs a new random search procedure for the given problem.
	 * 
	 * @param problem the problem being solved
	 * @param sampleSize the number of solutions sampled each iteration
	 * @param generator the initialization routine used to generate random solutions
	 * @param archive the archive of non-dominated solutions
	 */
	public RandomSearch(Problem problem, int sampleSize, Initialization generator, NondominatedPopulation archive) {
		super(problem);
		setSampleSize(sampleSize);
		
		Validate.notNull("generator", generator);
		Validate.notNull("archive", archive);
		
		this.generator = generator;
		this.archive = archive;
	}

	/**
	 * Returns the number of solutions sampled each iteration.
	 * 
	 * @return the sample size
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * Sets the number of solutions sampled each iteration.  The main reason to set the sample size is when
	 * distributed solution evaluations, as the sample size at least the number of threads.  The default value
	 * is 100.
	 * 
	 * @param sampleSize the sample size
	 */
	@Property(alias="populationSize")
	public void setSampleSize(int sampleSize) {
		Validate.greaterThanZero("sampleSize", sampleSize);
		this.sampleSize = sampleSize;
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
		Population solutions = new Population(generator.initialize(sampleSize));
		evaluateAll(solutions);
		archive.addAll(solutions);
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		archive.saveState(stream);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		archive.loadState(stream);
	}

}
