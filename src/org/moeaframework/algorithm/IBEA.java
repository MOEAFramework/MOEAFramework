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

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeFitnessEvaluator;
import org.moeaframework.core.fitness.IndicatorFitnessEvaluator;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of the Indicator-Based Evolutionary Algorithm (IBEA).  Instead of using Pareto dominance to evaluate
 * the quality of solutions, IBEA uses an indicator function (typically hypervolume but other indicator functions
 * can be specified). 
 * <p>
 * References:
 * <ol>
 *   <li>Zitzler, E. and S. Kunzli (2004).  Indicator-Based Selection in Multiobjective Search.  Parallel Problem
 *       Solving from Nature (PPSN VIII), pp. 832-842.
 * </ol>
 */
public class IBEA extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The indicator fitness evaluator to use (e.g., hypervolume or additive-epsilon indicator).
	 */
	private IndicatorFitnessEvaluator fitnessEvaluator;
	
	/**
	 * The fitness comparator for comparing solutions based on their fitness.
	 */
	private FitnessComparator fitnessComparator;
	
	/**
	 * The selection operator.
	 */
	private Selection selection;
	
	/**
	 * Constructs a new IBEA instance with default settings.
	 * 
	 * @param problem the problem
	 */
	public IBEA(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				null,
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation(problem),
				new HypervolumeFitnessEvaluator(problem));
	}

	/**
	 * Constructs a new IBEA instance.
	 * 
	 * @param problem the problem
	 * @param initialPopulationSize the initial population size
	 * @param archive the external archive; or {@code null} if no external archive is used
	 * @param initialization the initialization operator
	 * @param variation the variation operator
	 * @param fitnessEvaluator the indicator fitness evaluator to use (e.g., hypervolume additive-epsilon indicator)
	 */
	public IBEA(Problem problem, int initialPopulationSize, NondominatedPopulation archive,
			Initialization initialization, Variation variation, IndicatorFitnessEvaluator fitnessEvaluator) {
		super(problem, initialPopulationSize, new Population(), archive, initialization, variation);
		setFitnessEvaluator(fitnessEvaluator);
		
		Validate.problemHasNoConstraints(problem);
	}
	
	/**
	 * Returns the indicator-based fitness evaluator.
	 * 
	 * @return the indicator-based fitness evaluator
	 */
	public IndicatorFitnessEvaluator getFitnessEvaluator() {
		return fitnessEvaluator;
	}
	
	/**
	 * Sets the indicator-based fitness evaluator.  This value can not be {@code null}.
	 * 
	 * @param fitnessEvaluator the indicator-based fitness evaluator
	 */
	public void setFitnessEvaluator(IndicatorFitnessEvaluator fitnessEvaluator) {
		Validate.notNull("fitnessEvaluator", fitnessEvaluator);
		
		this.fitnessEvaluator = fitnessEvaluator;
		fitnessComparator = new FitnessComparator(fitnessEvaluator.areLargerValuesPreferred());
		selection = new TournamentSelection(fitnessComparator);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		fitnessEvaluator.evaluate(getPopulation());
	}

	@Override
	protected void iterate() {
		Population population = getPopulation();
		Variation variation = getVariation();
		Population offspring = new Population();
		int populationSize = population.size();
		
		while (offspring.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(), population);
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}
		
		evaluateAll(offspring);
		population.addAll(offspring);
		fitnessEvaluator.evaluate(population);
		
		while (population.size() > populationSize) {
			int worstIndex = findWorstIndex();
			fitnessEvaluator.removeAndUpdate(population, worstIndex);
		}
	}
	
	/**
	 * Returns the index of the solution with the worst fitness value.
	 * 
	 * @return the index of the solution with the worst fitness value
	 */
	private int findWorstIndex() {
		Population population = getPopulation();
		int worstIndex = 0;
		
		for (int i = 1; i < population.size(); i++) {
			if (fitnessComparator.compare(population.get(worstIndex), population.get(i)) == -1) {
				worstIndex = i;
			}
		}
		
		return worstIndex;
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}
	
	@Override
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("indicator")) {
			String indicator = properties.getString("indicator");
			
			if ("hypervolume".equalsIgnoreCase(indicator)) {
				setFitnessEvaluator(new HypervolumeFitnessEvaluator(problem));
			} else if ("epsilon".equalsIgnoreCase(indicator)) {
				setFitnessEvaluator(new AdditiveEpsilonIndicatorFitnessEvaluator(problem));
			} else {
				throw new ConfigurationException("invalid indicator: " + indicator);
			}
		}
		
		super.applyConfiguration(properties);
		
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		
		if (fitnessEvaluator instanceof HypervolumeFitnessEvaluator) {
			properties.setString("indicator", "hypervolume");
		} else if (fitnessEvaluator instanceof AdditiveEpsilonIndicatorFitnessEvaluator) {
			properties.setString("indicator", "epsilon");
		}
		
		return properties;
	}

}