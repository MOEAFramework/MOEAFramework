/* Copyright 2009-2022 David Hadka
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

import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implements the &epsilon;-NSGA-II algorithm.  This algorithm extends NSGA-II with
 * an &epsilon;-dominance archive and adaptive time continuation.
 * <p>
 * References:
 * <ol>
 *   <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective 
 *       Evolutionary Algorithms for Long-Term Monitoring Design."  Advances in
 *       Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class EpsilonNSGAII extends AdaptiveTimeContinuation implements Configurable {
	
	/**
	 * Constructs a new &epsilon;-NSGA-II instance with default settings.
	 * 
	 * @param problem the problem to solve
	 */
	public EpsilonNSGAII(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new NondominatedSortingPopulation(),
				new EpsilonBoxDominanceArchive(EpsilonHelper.getEpsilon(problem)),
				new TournamentSelection(2, new ChainedComparator(new ParetoDominanceComparator(), new CrowdingComparator())),
				OperatorFactory.getInstance().getVariation(problem),
				new RandomInitialization(problem),
				100, // windowSize
				100, // maxwindowSize
				0.25, // injectionRate
				100, // minimumPopulationSize
				10000); // maximumPopulationSize
	}
	
	/**
	 * Constructs the &epsilon;-NSGA-II instance with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param archive the &epsilon;-dominance archive
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 * @param windowSize the number of iterations between invocations of {@code check}
	 * @param maxWindowSize the maximum number of iterations allowed since the last restart before forcing a restart
	 * @param injectionRate the injection rate
	 * @param minimumPopulationSize the minimum size of the population
	 * @param maximumPopulationSize the maximum size of the population
	 */
	public EpsilonNSGAII(Problem problem, int initialPopulationSize, NondominatedSortingPopulation population,
			EpsilonBoxDominanceArchive archive, Selection selection, Variation variation,
			Initialization initialization, int windowSize, int maxWindowSize, double injectionRate,
			int minimumPopulationSize, int maximumPopulationSize) {
		super(new NSGAII(problem, initialPopulationSize, population, archive, selection, variation, initialization),
				windowSize, maxWindowSize, injectionRate, minimumPopulationSize, maximumPopulationSize,
				new UniformSelection(), new UM(1.0));
	}
	
	@Override
	NSGAII getAlgorithm() {
		return (NSGAII)super.getAlgorithm();
	}

	@Override
	public NondominatedSortingPopulation getPopulation() {
		return (NondominatedSortingPopulation)super.getPopulation();
	}
	
	public Variation getVariation() {
		return getAlgorithm().getVariation();
	}
	
	@Property("operator")
	public void setVariation(Variation variation) {
		getAlgorithm().setVariation(variation);
	}
	
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		getAlgorithm().setInitialPopulationSize(initialPopulationSize);
	}
	
	public EpsilonBoxDominanceArchive getArchive() {
		return (EpsilonBoxDominanceArchive)super.getAlgorithm().getArchive();
	}
	
	public void setArchive(EpsilonBoxDominanceArchive archive) {
		getAlgorithm().setArchive(archive);
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("epsilon")) {
			setArchive(new EpsilonBoxDominanceArchive(properties.getDoubleArray("epsilon")));
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		properties.setDoubleArray("epsilon", getArchive().getComparator().getEpsilons().toArray());
		return properties;
	}

}
