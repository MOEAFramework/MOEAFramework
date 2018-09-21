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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Abstract class providing default implementations for several
 * {@link EvolutionaryAlgorithm} methods. Primarily, the {@link #initialize()}
 * method generates and evaluates the initial population, adding the solutions
 * to the archive if available. The {@link #getResult()} method returns the
 * non-dominated solutions from the population and, if available, the archive.
 * The majority of evolutionary algorithms should only need to override the
 * {@link #iterate()} method.
 */
public abstract class AbstractEvolutionaryAlgorithm extends AbstractAlgorithm
		implements EvolutionaryAlgorithm {

	/**
	 * The current population.
	 */
	protected final Population population;

	/**
	 * The archive storing the non-dominated solutions.
	 */
	protected final NondominatedPopulation archive;

	/**
	 * The initialization operator.
	 */
	protected final Initialization initialization;

	/**
	 * Constructs an abstract evolutionary algorithm.
	 * 
	 * @param problem the problem being solved
	 * @param population the population
	 * @param archive the archive storing the non-dominated solutions
	 * @param initialization the initialization operator
	 */
	public AbstractEvolutionaryAlgorithm(Problem problem,
			Population population, NondominatedPopulation archive,
			Initialization initialization) {
		super(problem);
		this.population = population;
		this.archive = archive;
		this.initialization = initialization;
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

		Population population = getPopulation();
		NondominatedPopulation archive = getArchive();
		Solution[] initialSolutions = initialization.initialize();
		
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

	@Override
	public Population getPopulation() {
		return population;
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, 
					"algorithm not initialized");
		}

		List<Solution> populationList = new ArrayList<Solution>();
		List<Solution> archiveList = new ArrayList<Solution>();

		for (Solution solution : population) {
			populationList.add(solution);
		}

		if (archive != null) {
			for (Solution solution : archive) {
				archiveList.add(solution);
			}
		}

		return new EvolutionaryAlgorithmState(getNumberOfEvaluations(),
				populationList, archiveList);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		super.initialize();

		EvolutionaryAlgorithmState state = (EvolutionaryAlgorithmState)objState;

		numberOfEvaluations = state.getNumberOfEvaluations();
		population.addAll(state.getPopulation());

		if (archive != null) {
			archive.addAll(state.getArchive());
		}
	}

	/**
	 * Proxy for serializing and deserializing the state of an
	 * {@code AbstractEvolutionaryAlgorithm}. This proxy supports saving
	 * the {@code numberOfEvaluations}, {@code population} and {@code archive}.
	 */
	private static class EvolutionaryAlgorithmState implements Serializable {

		private static final long serialVersionUID = -6186688380313335557L;

		/**
		 * The number of objective function evaluations.
		 */
		private final int numberOfEvaluations;

		/**
		 * The population stored in a serializable list.
		 */
		private final List<Solution> population;

		/**
		 * The archive stored in a serializable list.
		 */
		private final List<Solution> archive;

		/**
		 * Constructs a proxy to serialize and deserialize the state of an 
		 * {@code AbstractEvolutionaryAlgorithm}.
		 * 
		 * @param numberOfEvaluations the number of objective function
		 *        evaluations
		 * @param population the population stored in a serializable list
		 * @param archive the archive stored in a serializable list
		 */
		public EvolutionaryAlgorithmState(int numberOfEvaluations,
				List<Solution> population, List<Solution> archive) {
			super();
			this.numberOfEvaluations = numberOfEvaluations;
			this.population = population;
			this.archive = archive;
		}

		/**
		 * Returns the number of objective function evaluations.
		 * 
		 * @return the number of objective function evaluations
		 */
		public int getNumberOfEvaluations() {
			return numberOfEvaluations;
		}

		/**
		 * Returns the population stored in a serializable list.
		 * 
		 * @return the population stored in a serializable list
		 */
		public List<Solution> getPopulation() {
			return population;
		}

		/**
		 * Returns the archive stored in a serializable list.
		 * 
		 * @return the archive stored in a serializable list
		 */
		public List<Solution> getArchive() {
			return archive;
		}

	}

}
