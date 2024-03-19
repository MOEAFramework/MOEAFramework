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

import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Validate;

/**
 * Abstract class providing default implementations for several {@link EvolutionaryAlgorithm} methods. Primarily,
 * the {@link #initialize()} method generates and evaluates the initial population, adding the solutions to the archive
 * if available. The {@link #getResult()} method returns the non-dominated solutions from the population and, if
 * available, the archive.  The majority of evolutionary algorithms should only need to override the {@link #iterate()}
 * method.
 */
public abstract class AbstractEvolutionaryAlgorithm extends AbstractAlgorithm implements EvolutionaryAlgorithm,
Configurable {
	
	/**
	 * The initial population size.
	 */
	private int initialPopulationSize;

	/**
	 * The current population.
	 */
	private Population population;

	/**
	 * The archive storing the non-dominated solutions.
	 */
	private NondominatedPopulation archive;

	/**
	 * The initialization operator.
	 */
	private Initialization initialization;
	
	/**
	 * The variation operator.
	 */
	private Variation variation;

	/**
	 * Constructs an abstract evolutionary algorithm.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population
	 * @param archive the archive storing the non-dominated solutions
	 * @param initialization the initialization operator
	 * @param variation the variation operator
	 */
	public AbstractEvolutionaryAlgorithm(Problem problem, int initialPopulationSize, Population population,
			NondominatedPopulation archive, Initialization initialization, Variation variation) {
		super(problem);
		setInitialPopulationSize(initialPopulationSize);
		setPopulation(population);
		setArchive(archive);
		setInitialization(initialization);
		
		if (variation != null) {
			setVariation(variation);
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		Population population = getPopulation();
		NondominatedPopulation archive = getArchive();
		NondominatedPopulation result = new NondominatedPopulation();

		result.addAll(population);

		if (archive != null) {
			result.addAll(archive);
		}

		return result;
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		if (variation == null) {
			throw new AlgorithmInitializationException(this,
					"no variation operator set, must set one by calling setVariation(...)");
		}

		Population population = getPopulation();
		NondominatedPopulation archive = getArchive();
		Solution[] initialSolutions = initialization.initialize(initialPopulationSize);
		
		evaluateAll(initialSolutions);
		population.addAll(initialSolutions);

		if (archive != null) {
			archive.addAll(population);
		}
	}

	@Override
	public NondominatedPopulation getArchive() {
		return archive;
	}
	
	/**
	 * Sets the archive used by this algorithm.  This value can not be set after initialization.
	 * 
	 * @param archive the archive
	 */
	protected void setArchive(NondominatedPopulation archive) {
		assertNotInitialized();
		this.archive = archive;
	}
	
	/**
	 * Returns the initial population size.
	 * 
	 * @return the initial population size
	 */
	public int getInitialPopulationSize() {
		return initialPopulationSize;
	}
	
	/**
	 * Sets the initial population size.  This value can not be set after initialization.
	 * 
	 * @param initialPopulationSize the initial population size
	 */
	protected void setInitialPopulationSize(int initialPopulationSize) {
		assertNotInitialized();
		Validate.greaterThanZero("initialPopulationSize", initialPopulationSize);
		this.initialPopulationSize = initialPopulationSize;
	}

	@Override
	public Population getPopulation() {
		return population;
	}
	
	/**
	 * Sets the population used by this algorithm.  This value can not be set after initialization.
	 * 
	 * @param population the population
	 */
	protected void setPopulation(Population population) {
		assertNotInitialized();
		Validate.notNull("population", population);
		this.population = population;
	}
	
	/**
	 * Returns the variation operator currently in use by this algorithm.
	 * 
	 * @return the variation operator
	 */
	public Variation getVariation() {
		return variation;
	}
	
	/**
	 * Replaces the variation operator to be used by this algorithm.
	 * 
	 * @param variation the variation operator
	 */
	protected void setVariation(Variation variation) {
		Validate.notNull("variation", variation);
		this.variation = variation;
	}

	/**
	 * Returns the initialization method for generating solutions in the initial population.
	 * 
	 * @return the initialization method
	 */
	public Initialization getInitialization() {
		return initialization;
	}

	/**
	 * Sets the initialization method for generating solutions in the initial population.  This can only
	 * be set before initializing the algorithm.
	 * 
	 * @param initialization the initialization method
	 */
	public void setInitialization(Initialization initialization) {
		assertNotInitialized();
		Validate.notNull("initialization", initialization);
		this.initialization = initialization;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		population.saveState(stream);
		
		if (archive != null) {
			archive.saveState(stream);
		}
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		population.loadState(stream);
		
		if (archive != null) {
			archive.loadState(stream);
		}
	}

}
