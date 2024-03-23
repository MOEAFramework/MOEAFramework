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
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.util.TypedProperties;

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
		register(fromProblem(AMOSA::new), "AMOSA");
		register(fromProblem(CMAES::new), "CMA-ES", "CMAES", "MO-CMA-ES");
		register(fromProblem(DBEA::new), "DBEA", "I-DBEA");
		register(fromProblem(EpsilonMOEA::new), "eMOEA", "e-MOEA", "EpsilonMOEA");
		register(fromProblem(EpsilonNSGAII::new), "eNSGAII", "e-NSGA-II", "eNSGA2", "EpsilonNSGAII");
		register(fromProblem(GDE3::new), "GDE3");
		register(fromProblem(IBEA::new), "IBEA");
		register(fromProblem(MOEAD::new), "MOEAD", "MOEA/D");
		register(fromProblem(MSOPS::new), "MSOPS");
		register(fromProblem(NSGAII::new), "NSGAII", "NSGA-II", "NSGA2");
		register(fromProblem(NSGAIII::new), "NSGAIII", "NSGA-III", "NSGA3");
		register(fromProblemWithIterations(OMOPSO::new), "OMOPSO");
		register(fromProblem(PAES::new), "PAES");
		register(fromProblem(PESA2::new), "PESA2");
		register(fromProblem(RandomSearch::new), "Random");
		register(fromProblemWithIterations(RVEA::new), "RVEA");
		register(fromProblem(SPEA2::new), "SPEA2");
		register(fromProblem(SMPSO::new), "SMPSO");
		register(fromProblem(SMSEMOA::new), "SMSEMOA", "SMS-EMOA");
		register(fromProblem(VEGA::new), "VEGA");
		
		// single-objective
		register(fromProblem(DifferentialEvolution::new), "DifferentialEvolution", "DE", "DE/rand/1/bin");
		register(fromProblem(GeneticAlgorithm::new), "GeneticAlgorithm", "GA");
		register(fromProblem(EvolutionStrategy::new), "EvolutionStrategy", "EvolutionaryStrategy", "ES");
		register(fromProblem(SimulatedAnnealing::new), "SimulatedAnnealing", "SA");
		
		// special cases
		register(this::newRSO, "RSO");
		
		// register all to appear in the diagnostic tool
		registerDiagnosticToolAlgorithm("AMOSA");
		registerDiagnosticToolAlgorithm("CMA-ES");
		registerDiagnosticToolAlgorithm("DBEA");
		registerDiagnosticToolAlgorithm("e-MOEA");
		registerDiagnosticToolAlgorithm("e-NSGA-II");
		registerDiagnosticToolAlgorithm("GDE3");
		registerDiagnosticToolAlgorithm("IBEA");
		registerDiagnosticToolAlgorithm("MOEA/D");
		registerDiagnosticToolAlgorithm("NSGA-II");
		registerDiagnosticToolAlgorithm("NSGA-III");
		registerDiagnosticToolAlgorithm("OMOPSO");
		registerDiagnosticToolAlgorithm("PAES");
		registerDiagnosticToolAlgorithm("PESA2");
		registerDiagnosticToolAlgorithm("Random");
		registerDiagnosticToolAlgorithm("RVEA");
		registerDiagnosticToolAlgorithm("SPEA2");
		registerDiagnosticToolAlgorithm("SMPSO");
		registerDiagnosticToolAlgorithm("SMS-EMOA");
		registerDiagnosticToolAlgorithm("VEGA");
		registerDiagnosticToolAlgorithm("DifferentialEvolution");
		registerDiagnosticToolAlgorithm("GeneticAlgorithm");
		registerDiagnosticToolAlgorithm("EvolutionStrategy");
		registerDiagnosticToolAlgorithm("SimulatedAnnealing");
		registerDiagnosticToolAlgorithm("RSO");
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
			algorithm.applyConfiguration(properties);
			return algorithm;
		};
	}
	
	/**
	 * Takes a reference to an algorithm constructor and returns a function that creates configured instances
	 * of the algorithm.  This variant works with constructors that take a second int argument specifying the
	 * number of iterations or generations.
	 * 
	 * @param <T> the type of algorithm, must implement {@link Algorithm} and {@link Configurable}
	 * @param supplier reference to the constructor
	 * @return a function that creates and configures instances of the algorithm
	 */
	private <T extends Algorithm & Configurable> BiFunction<TypedProperties, Problem, Algorithm> fromProblemWithIterations(
			BiFunction<Problem, Integer, T> supplier) {
		return (TypedProperties properties, Problem problem) -> {
			T algorithm = supplier.apply(problem, getMaxIterations(properties));
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
			int maxEvaluations = properties.getTruncatedInt("maxEvaluations", 25000);
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
