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
package org.moeaframework.algorithm;

import java.util.Properties;

import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.EpsilonBoxConstraintComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.DifferentialEvolutionSelection;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * 
 */
public class Borg2Provider extends AlgorithmProvider {
	
	@Override
	public Algorithm getAlgorithm(String name, Properties properties,
			Problem problem) {
		TypedProperties typedProperties = new TypedProperties(properties);

		if (name.equalsIgnoreCase("Borg2")) {
			return newBorg2(typedProperties, problem);
		} else if (name.equalsIgnoreCase("Borg3")) {
			return newBorg3(typedProperties, problem);
		} else {
			return null;
		}
	}
	
	private int getSelectionSize(double selectionRatio, int populationSize) {
		return Math.max((int)(populationSize * selectionRatio), 2);
	}
	
	private Algorithm newBorg2(final TypedProperties properties, final Problem problem) {
		int initialPopulationSize = (int)properties.getDouble(
				"initialPopulationSize", 100);

		Initialization initialization = new RandomInitialization(problem,
				initialPopulationSize);

		Population population = new Population();

		DominanceComparator comparator = new ChainedComparator(
				new AggregateConstraintComparator(),
				new ParetoDominanceComparator());

		final EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
				new EpsilonBoxConstraintComparator(properties.getDoubleArray(
						"epsilon", new double[] { EpsilonHelper
								.getEpsilon(problem) })));

		final TournamentSelection tournamentSelection = new TournamentSelection(
				getSelectionSize(properties.getDouble("selectionRatio", 0.02),
						initialPopulationSize), comparator) {

							@Override
							public Solution[] select(int arity,
									Population population) {
								int half = arity/2;
	
								return CoreUtils.merge(
										super.select(half, archive),
										super.select(arity-half, population));
							}
			
		};
		
		final Selection archiveSelection = new Selection() {

			@Override
			public Solution[] select(int arity, Population population) {
				Solution[] result = new Solution[archive.size()];
				
				for (int i=0; i<archive.size(); i++) {
					result[i] = archive.get(i);
				}
				
				return result;
			}
			
		};
		
		DifferentialEvolutionSelection deSelection = new DifferentialEvolutionSelection() {
			
			@Override
			public Solution[] select(int arity, Population population) {
				setCurrentIndex(PRNG.nextInt(population.size()));
				return CoreUtils.merge(archive.get(PRNG.nextInt(archive.size())), super.select(arity-1, population));
			}
			
		};
		
		final AdaptiveMultimethod multimethod = new AdaptiveMultimethod(archive);
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"sbx+pm", properties, problem));
		multimethod.add(deSelection, OperatorFactory.getInstance().getVariation(
				"de", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"pcx", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"spx", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"undx", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"um", properties, problem));
		multimethod.add(archiveSelection, OperatorFactory.getInstance().getVariation(
				"am", properties, problem));

		EpsilonMOEA emoea = new EpsilonMOEA(problem, population, archive,
				multimethod, multimethod, initialization, comparator) {

					@Override
					public void iterate() {
						Solution[] parents = multimethod.select(multimethod.getArity(), population);
						Solution[] children = multimethod.evolve(parents);

						for (Solution child : children) {
							evaluate(child);
							addToPopulation(child);
							archive.add(child);
						}
					}
			
		};

		final EpsilonProgressContinuation algorithm = new EpsilonProgressContinuation(
				emoea, 100, 10000, 
				1.0 / properties.getDouble("injectionRate", 0.25), 
				100, 10000,
				new UniformSelection(), 
				new UM(1.0 / problem.getNumberOfVariables()));

		algorithm.addRestartListener(new RestartListener() {

			@Override
			public void restarted(RestartEvent event) {
				tournamentSelection.setSize(getSelectionSize(
						properties.getDouble("selectionRatio", 0.02), 
						algorithm.getPopulation().size()));
			}

		});

		return algorithm;
	}
	
	private Algorithm newBorg3(final TypedProperties properties, final Problem problem) {
		int initialPopulationSize = (int)properties.getDouble(
				"initialPopulationSize", 100);

		Initialization initialization = new RandomInitialization(problem,
				initialPopulationSize);

		DominanceComparator comparator = new ChainedComparator(
				new AggregateConstraintComparator(),
				new ParetoDominanceComparator());
		
		NondominatedSortingPopulation population = 
				new NondominatedSortingPopulation(comparator);
		
		final AdaptiveMultimethod2 multimethod = new AdaptiveMultimethod2(null);

		final EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
				new EpsilonBoxConstraintComparator(properties.getDoubleArray(
						"epsilon", new double[] { EpsilonHelper
								.getEpsilon(problem) }))) {

			@Override
			public boolean add(Solution newSolution) {
				//int old = getNumberOfDominatingImprovements();
				boolean result = super.add(newSolution);
				
				if (result /*getNumberOfDominatingImprovements()-old != 0*/) {
					multimethod.solutionAcceptedToArchive(newSolution);
				}
				
				return result;
			}
			
		};

		final TournamentSelection tournamentSelection = new TournamentSelection(
				getSelectionSize(properties.getDouble("selectionRatio", 0.02),
						initialPopulationSize), new ChainedComparator(
								new AggregateConstraintComparator(),
								new ParetoDominanceComparator(),
								new CrowdingComparator()));
		
		final Selection archiveSelection = new Selection() {

			@Override
			public Solution[] select(int arity, Population population) {
				Solution[] result = new Solution[archive.size()];
				
				for (int i=0; i<archive.size(); i++) {
					result[i] = archive.get(i);
				}
				
				return result;
			}
			
		};
		
		DifferentialEvolutionSelection deSelection = new DifferentialEvolutionSelection() {
			
			@Override
			public Solution[] select(int arity, Population population) {
				setCurrentIndex(PRNG.nextInt(population.size()));
				return super.select(arity, population);
			}
			
		};
		
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"sbx+pm", properties, problem));
		multimethod.add(deSelection, OperatorFactory.getInstance().getVariation(
				"de", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"pcx", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"spx", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"undx", properties, problem));
		multimethod.add(tournamentSelection, OperatorFactory.getInstance().getVariation(
				"um", properties, problem));
		multimethod.add(archiveSelection, OperatorFactory.getInstance().getVariation(
				"am", properties, problem));
		
		multimethod.update();

		NSGAII emoea = new NSGAII(problem, population, archive, multimethod, 
				multimethod, initialization) {

					@Override
					public void iterate() {
						super.iterate();
						multimethod.update();
					}
			
		};

		final EpsilonProgressContinuation algorithm = new EpsilonProgressContinuation(
				emoea, 1, 100, 
				1.0 / properties.getDouble("injectionRate", 0.25), 
				100, 10000,
				new UniformSelection(), 
				new UM(1.0 / problem.getNumberOfVariables()));

		return algorithm;
	}


}
