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
package org.moeaframework.studies.borg;

import java.util.Properties;

import org.moeaframework.algorithm.AdaptiveTimeContinuation;
import org.moeaframework.algorithm.EpsilonProgressContinuation;
import org.moeaframework.algorithm.RestartEvent;
import org.moeaframework.algorithm.RestartListener;
import org.moeaframework.algorithm.RestartType;
import org.moeaframework.algorithm.EpsilonMOEA;
import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.EpsilonBoxConstraintComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.AdaptiveMultimethodVariation;
import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.DifferentialEvolution;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.SPX;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.operator.real.UNDX;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.util.TypedProperties;

public class BorgVariants extends AlgorithmProvider {

	@Override
	public Algorithm getAlgorithm(String name, Properties properties,
			Problem problem) {
		TypedProperties typedProperties = new TypedProperties(properties);

		if (name.equalsIgnoreCase("BorgA")) {
			return newBorg(typedProperties, problem, false, false, false);
		} else if (name.equalsIgnoreCase("BorgB")) {
			return newBorg(typedProperties, problem, true, false, false);
		} else if (name.equalsIgnoreCase("BorgC")) {
			return newBorg(typedProperties, problem, false, true, false);
		} else if (name.equalsIgnoreCase("BorgD")) {
			return newBorg(typedProperties, problem, false, false, true);
		} else if (name.equalsIgnoreCase("BorgE")) {
			return newBorg(typedProperties, problem, true, true, false);
		} else if (name.equalsIgnoreCase("BorgF")) {
			return newBorg(typedProperties, problem, true, false, true);
		} else if (name.equalsIgnoreCase("BorgG")) {
			return newBorg(typedProperties, problem, false, true, true);
		} else if (name.equalsIgnoreCase("BorgH")) {
			return newBorg(typedProperties, problem, true, true, true);
		} else {
			return null;
		}
	}

	private int getSelectionSize(double selectionRatio, int populationSize) {
		return Math.max((int)(populationSize * selectionRatio), 2);
	}

	/**
	 * Returns a variant of the Borg MOEA with the specified components enabled.
	 * 
	 * @param properties the parameters
	 * @param problem the problem
	 * @param withAdaptivePopulationSizing {@code true} if adaptive population
	 *        sizing is enabled; {@code false} otherwise
	 * @param withEpsilonProgress {@code true} if epsilon-progress triggered
	 *        restarts are enabled; {@code false} otherwise
	 * @param withMultioperators {@code true} if the auto-adaptive
	 *        multi-operator recombination is enabled; {@code false} otherwise
	 * @return a variant of the Borg MOEA with the specified components enabled
	 */
	private Algorithm newBorg(final TypedProperties properties, Problem problem,
			boolean withAdaptivePopulationSizing, boolean withEpsilonProgress,
			boolean withMultioperators) {
		int initialPopulationSize = (int)properties.getDouble(
				"initialPopulationSize", 100);

		Initialization initialization = new RandomInitialization(problem,
				initialPopulationSize);

		Population population = new Population();

		DominanceComparator comparator = new ChainedComparator(
				new AggregateConstraintComparator(),
				new ParetoDominanceComparator());

		EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
				new EpsilonBoxConstraintComparator(properties.getDoubleArray(
						"epsilon", new double[] { EpsilonHelper
								.getEpsilon(problem) })));

		final TournamentSelection selection = new TournamentSelection(
				withAdaptivePopulationSizing ? getSelectionSize(properties
						.getDouble("selectionRatio", 0.02),
						initialPopulationSize) : 2, comparator);
		
		SBX sbx = new SBX(properties.getDouble("sbx.rate", 1.0), properties
				.getDouble("sbx.distributionIndex", 15.0));

		PM pm = new PM(properties
				.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()),
				properties.getDouble("pm.distributionIndex", 20.0));
		
		Variation variation = null;
		
		if (withMultioperators) {
			DifferentialEvolution de = new DifferentialEvolution(properties
					.getDouble("de.crossoverRate", 1.0), properties.getDouble(
					"de.stepSize", 0.5));

			PCX pcx = new PCX((int)properties.getDouble("pcx.parents", 3),
					(int)properties.getDouble("pcx.offspring", 2), properties
							.getDouble("pcx.eta", 0.1), properties.getDouble(
							"pcx.zeta", 0.1));

			SPX spx = new SPX((int)properties.getDouble("spx.parents", 3),
					(int)properties.getDouble("spx.offspring", 2), properties
							.getDouble("spx.epsilon", 2));

			UNDX undx = new UNDX((int)properties.getDouble("undx.parents", 3),
					(int)properties.getDouble("undx.offspring", 2), properties
							.getDouble("undx.zeta", 0.5), properties.getDouble(
							"undx.eta", 0.35));

			UM um = new UM(properties.getDouble("um.rate", 1.0 / problem
					.getNumberOfVariables()));
			
			AdaptiveMultimethodVariation multioperator = new AdaptiveMultimethodVariation(
					archive);
			multioperator.addOperator(new GAVariation(sbx, pm));
			multioperator.addOperator(new GAVariation(de, pm));
			multioperator.addOperator(new GAVariation(pcx, pm));
			multioperator.addOperator(new GAVariation(spx, pm));
			multioperator.addOperator(new GAVariation(undx, pm));
			multioperator.addOperator(um);
			
			variation = multioperator;
		} else {
			variation = new GAVariation(sbx, pm);
		}

		EpsilonMOEA emoea = new EpsilonMOEA(problem, population, archive, 
				selection, variation, initialization);

		if (withEpsilonProgress && withAdaptivePopulationSizing) {
			final EpsilonProgressContinuation algorithm = new EpsilonProgressContinuation(
					emoea, 100, 10000, 1.0 / properties
							.getDouble("injectionRate", 0.25), 100, 10000,
					new UniformSelection(), new UM(1.0 / problem
							.getNumberOfVariables()));

			algorithm.addRestartListener(new RestartListener() {
	
				@Override
				public void restarted(RestartEvent event) {
					selection.setSize(getSelectionSize(properties.getDouble(
							"selectionRatio", 0.02), algorithm.getPopulation()
							.size()));
				}
	
			});
			
			return algorithm;
		} else if (withEpsilonProgress) {
			final EpsilonProgressContinuation algorithm = new EpsilonProgressContinuation(
					emoea, 100, 10000, 1.0 / properties
							.getDouble("injectionRate", 0.25), 100, 10000,
					new UniformSelection(), new UM(1.0 / problem
							.getNumberOfVariables())) {
				
				/**
				 * Disable SOFT restarts, which are the kind caused by adaptive
				 * population sizing
				 */
				@Override
				protected RestartType check() {
					RestartType restartType = super.check();
					
					if (restartType.equals(RestartType.SOFT)) {
						restartType = RestartType.NONE;
					}
					
					return restartType;
				}
				
			};

			return algorithm;
		} else if (withAdaptivePopulationSizing) {
			final AdaptiveTimeContinuation algorithm = new AdaptiveTimeContinuation(
					emoea, 100, 10000, 1.0 / properties
							.getDouble("injectionRate", 0.25), 100, 10000,
					new UniformSelection(), new UM(1.0 / problem
							.getNumberOfVariables()));
			
			algorithm.addRestartListener(new RestartListener() {
	
				@Override
				public void restarted(RestartEvent event) {
					selection.setSize(getSelectionSize(properties.getDouble(
							"selectionRatio", 0.02), algorithm.getPopulation()
							.size()));
				}
	
			});
			
			return algorithm;
		} else {
			return emoea;
		}
	}

}
