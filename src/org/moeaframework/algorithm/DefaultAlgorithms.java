/* Copyright 2009-2025 David Hadka
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

import java.util.function.BiFunction;
import java.util.function.Function;

import org.moeaframework.algorithm.pso.OMOPSO;
import org.moeaframework.algorithm.pso.SMPSO;
import org.moeaframework.algorithm.sa.AMOSA;
import org.moeaframework.algorithm.single.DifferentialEvolution;
import org.moeaframework.algorithm.single.EvolutionStrategy;
import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.algorithm.single.RepeatedSingleObjective;
import org.moeaframework.algorithm.single.SimulatedAnnealing;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.problem.Problem;

/**
 * A provider of default algorithms.  Refer to {@code docs/algorithms.md} or the Javadoc for the specifics of
 * parameterizing the algorithms.
 */
public class DefaultAlgorithms extends RegisteredAlgorithmProvider {

	/**
	 * Constructs the default algorithm provider.
	 */
	public DefaultAlgorithms() {
		super();
		
		// multi-objective
		register(fromProblem(AGEMOEAII::new), "AGE-MOEA-II", "AGEMOEAII", "AGEMOEA2");
		register(fromProblem(AMOSA::new), "AMOSA");
		register(fromProblem(CMAES::new), "CMA-ES", "CMAES", "MO-CMA-ES");
		register(fromProblem(DBEA::new), "DBEA", "I-DBEA");
		register(fromProblem(EpsilonMOEA::new), "e-MOEA", "eMOEA", "EpsilonMOEA");
		register(fromProblem(EpsilonNSGAII::new), "e-NSGA-II", "eNSGAII", "eNSGA2", "EpsilonNSGAII");
		register(fromProblem(GDE3::new), "GDE3");
		register(fromProblem(IBEA::new), "IBEA");
		register(fromProblem(MOEAD::new), "MOEA/D", "MOEAD", "MOEA/D-DRA");
		register(fromProblem(MSOPS::new), "MSOPS");
		register(fromProblem(NSGAII::new), "NSGA-II", "NSGAII", "NSGA2");
		register(fromProblem(NSGAIII::new), "NSGA-III", "NSGAIII", "NSGA3");
		register(fromProblem(OMOPSO::new), "OMOPSO");
		register(fromProblem(PAES::new), "PAES");
		register(fromProblem(PESA2::new), "PESA2");
		register(fromProblem(RandomSearch::new), "Random");
		register(fromProblem(RVEA::new), "RVEA");
		register(fromProblem(SMPSO::new), "SMPSO");
		register(fromProblem(SMSEMOA::new), "SMS-EMOA", "SMSEMOA");
		register(fromProblem(SPEA2::new), "SPEA2");
		register(fromProblem(UNSGAIII::new), "U-NSGA-III", "UNSGAIII", "UNSGA3");
		register(fromProblem(VEGA::new), "VEGA");
		
		// single-objective
		register(fromProblem(DifferentialEvolution::new), "DifferentialEvolution", "DE", "DE/rand/1/bin");
		register(fromProblem(GeneticAlgorithm::new), "GeneticAlgorithm", "GA");
		register(fromProblem(EvolutionStrategy::new), "EvolutionStrategy", "EvolutionaryStrategy", "ES");
		register(fromProblem(SimulatedAnnealing::new), "SimulatedAnnealing", "SA");
		
		// special cases
		register(this::newRSO, "RSO");
		
		// register all to appear in the diagnostic tool
		registerDiagnosticToolAlgorithms(getRegisteredAlgorithms());
	}
	
	/**
	 * Takes a reference to an algorithm constructor and returns a function that creates configured instances
	 * of the algorithm.
	 * 
	 * @param <T> the type of algorithm, must implement {@link Algorithm} and {@link Configurable}
	 * @param supplier reference to the constructor
	 * @return a function that creates and configures instances of the algorithm
	 */
	private <T extends Algorithm & Configurable> BiFunction<TypedProperties, Problem, Algorithm> fromProblem(
			Function<Problem, T> supplier) {
		return (TypedProperties properties, Problem problem) -> {
			T algorithm = supplier.apply(problem);
			
			if (algorithm.getConfiguration().contains("maxIterations") && !properties.contains("maxIterations")) {
				properties.setInt("maxIterations", getMaxIterations(properties));
			}
			
			algorithm.applyConfiguration(properties);
			return algorithm;
		};
	}
	
	/**
	 * Reads or derives the max iterations property.  The search order is:
	 * <ol>
	 *   <li>The property {@code maxIterations}
	 *   <li>Derived from {@code maxEvaluations / populationSize}
	 *   <li>Derived from {@code maxEvaluations / swarmSize} (for PSO algorithms)
	 *   <li>Default to 250
	 * </ol>
	 * 
	 * @param properties the user-defined properties
	 * @return the max iterations
	 */
	public static int getMaxIterations(TypedProperties properties) {
		if (properties.contains("maxIterations")) {
			return properties.getTruncatedInt("maxIterations");
		} else {
			int maxEvaluations = properties.getTruncatedInt("maxEvaluations",
					Settings.DEFAULT_MAX_FUNCTION_EVALUATIONS);
			
			int populationSize = properties.getTruncatedInt("populationSize",
					properties.getTruncatedInt("swarmSize", Settings.DEFAULT_POPULATION_SIZE));
						
			return maxEvaluations / populationSize;
		}
	}

	private Algorithm newRSO(TypedProperties properties, Problem problem) {
		String algorithmName = properties.getString("algorithm", "GA");
		int instances = properties.getTruncatedInt("instances", 100);
		
		if (!properties.contains("method")) {
			properties.setString("method", "min-max");
		}

		return new RepeatedSingleObjective(problem, instances, algorithmName, properties);
	}
	
}
