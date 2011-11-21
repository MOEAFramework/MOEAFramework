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
package org.moeaframework.problem.reed;

import java.io.IOException;
import java.util.Properties;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.EpsilonMOEA;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
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
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.TypedProperties;

public class AviationExample {
	
	public static void main(String[] args) throws IOException {
		String[] algorithms = { "BorgVariant", "eMOEA", "eNSGAII",
				/*"GDE3", "IBEA", "MOEAD", "NSGAII", "OMOPSO", "SPEA2"*/ };
		
		Executor executor = new Executor()
				.usingAlgorithmFactory(new CustomFactory())
				.withProblemClass(Aviation.class)
				.withEpsilon(Aviation.EPSILON_LOWRES)
				.withMaxEvaluations(10000);
		
		Analyzer analyzer = new Analyzer()
				.withProblemClass(Aviation.class)
				.withEpsilon(Aviation.EPSILON_LOWRES)
				.includeGenerationalDistance()
				.includeInvertedGenerationalDistance()
				.includeAdditiveEpsilonIndicator()
				.includeContribution()
				.showAggregate()
				.showStatisticalSignificance();

		//run each algorithm for 50 seeds
		for (String algorithm : algorithms) {
			for (int i=0; i<50; i++) {
				analyzer.add(algorithm, 
						executor.withAlgorithm(algorithm).run());
			}
		}

		//print the results
		analyzer.printAnalysis();
	}
	
	public static class CustomFactory extends AlgorithmFactory {

		private Algorithm newBorgVariant(TypedProperties properties, Problem problem) {
			Initialization initialization = new RandomInitialization(problem, 100);

			Population population = new Population();

			DominanceComparator comparator = new ChainedComparator(
					new AggregateConstraintComparator(),
					new ParetoDominanceComparator());

			EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
					new EpsilonBoxConstraintComparator(properties.getDoubleArray("epsilon", null)));

			final TournamentSelection selection = new TournamentSelection(2, comparator);
			
			AdaptiveMultimethodVariation variation = new AdaptiveMultimethodVariation(archive);
			variation.addOperator(new GAVariation(
					new SBX(1.0, 15.0),
					new PM(1.0 / problem.getNumberOfVariables(), 20.0)));
			variation.addOperator(new GAVariation(
					new DifferentialEvolution(1.0, 0.5),
					new PM(1.0 / problem.getNumberOfVariables(), 20.0)));
			variation.addOperator(new PCX(10, 2));
			variation.addOperator(new SPX(10, 2));
			variation.addOperator(new UNDX(10, 2));
			variation.addOperator(new UM(1.0 / problem.getNumberOfVariables()));

			EpsilonMOEA emoea = new EpsilonMOEA(problem, population, archive, selection,
					variation, initialization);
			
			EpsilonProgress2 algorithm = new EpsilonProgress2(
					emoea, 
					new UM(1.0 / problem.getNumberOfVariables()), 
					100, 
					25000,
					0.25,
					100,
					10000,
					false,
					selection,
					0.02);
			
			return algorithm;
		}

		@Override
		public synchronized Algorithm getAlgorithm(String name,
				Properties properties, Problem problem) {
			if (name.equals("BorgVariant")) {
				return newBorgVariant(new TypedProperties(properties), problem);
			} else {
				return super.getAlgorithm(name, properties, problem);
			}
		}
		
	}
	
	/**
	 * Decorates an epsilon-box evolutionary algorithm with the epsilon-progress
	 * measure of search progress, a restart mechanism with injection, and adaptive 
	 * population sizing.
	 */
	public static class EpsilonProgress2 implements EpsilonBoxEvolutionaryAlgorithm {
		
		/**
		 * The selection operator for selecting archive solutions during injection.
		 */
		private final Selection selection;

		/**
		 * The variation operator used to create new solutions during injection.
		 */
		private final Variation variation;
		
		/**
		 * The algorithm being decorated.
		 */
		private final EpsilonBoxEvolutionaryAlgorithm algorithm;
		
		/**
		 * The number of iterations that elapse between checks of epsilon-progress.
		 */
		private final int windowSize;
		
		/**
		 * The maximum number of iterations that elapse before epsilon-progress is
		 * automatically triggered.
		 */
		private final int maxWindowSize;
		
		/**
		 * The percentage of the population after injection consisting of archive
		 * solutions.  This rate controls the population size after restarts, but
		 * the resulting population size is bound by 
		 * <code>minimumPopulationSize</code> and 
		 * <code>maximumPopulationSize</code>.
		 */
		private final double injectionRate;
		
		/**
		 * The lower bound on the population size for adaptive population sizing.
		 */
		private final int minimumPopulationSize;
		
		/**
		 * The upper bound on the population size for adaptive population sizing.
		 */
		private final int maximumPopulationSize;
		
		/**
		 * <code>true</code> indicates only dominating improvements are counted;
		 * <code>false</code> indicates both dominating and non-dominating
		 * improvements are counted.
		 */
		private final boolean strictImprovement;

		/**
		 * The number of iterations since the last epsilon-progress check.
		 */
		private int iterationsAtLastCheck = 0;
		
		/**
		 * The number of epsilon-progress improvements detected during the last
		 * check.
		 */
		private int improvementsAtLastCheck = 0;
		
		/**
		 * The number of iterations since the last random restart was triggered.
		 */
		private int iterationsAtLastRestart = 0;
		
		private int iteration = 0;
		
		/**
		 * Counts the number of times random restart has occurred.
		 */
		private int numberOfRestarts = 0;
		
		private final TournamentSelection ts;
		
		private final double ps;

		/**
		 * Class constructor for extending the specified algorithm with epsilon-
		 * progress.
		 * 
		 * @param algorithm the algorithm being extended
		 * @param variation the variation operator for generating injected solutions
		 * @param windowSize the number of iterations between checking 
		 *        epsilon-progress
		 * @param maxWindowSize the maximum number of iterations that may elapse
		 *        before a random restart is automatically triggered
		 * @param injectionRate the injection rate controlling adaptive population
		 *        sizing
		 * @param minimumPopulationSize the minimum population size bound during 
		 *        adaptive population sizing
		 * @param maximumPopulationSize the maximum population size bound during
		 *        adaptive population sizing
		 */
		public EpsilonProgress2(EpsilonBoxEvolutionaryAlgorithm algorithm,
				Variation variation, int windowSize, int maxWindowSize, 
				double injectionRate, int minimumPopulationSize, 
				int maximumPopulationSize, boolean strictImprovement,
				TournamentSelection ts, double ps) {
			this.algorithm = algorithm;
			this.variation = variation;
			this.windowSize = windowSize;
			this.maxWindowSize = maxWindowSize;
			this.injectionRate = injectionRate;
			this.minimumPopulationSize = minimumPopulationSize;
			this.maximumPopulationSize = maximumPopulationSize;
			this.strictImprovement = strictImprovement;
			this.ts = ts;
			this.ps = ps;
			
			selection = new UniformSelection();
		}
		
		private int getNumberOfImprovements() {
			return strictImprovement ? 
					getArchive().getNumberOfDominatingImprovements() : 
					getArchive().getNumberOfImprovements();
		}
		
		@Override
		public void step() {
			algorithm.step();
			iteration++;
			
			boolean doCheck = iteration - iterationsAtLastCheck >= windowSize;
			boolean doRestart = iteration - iterationsAtLastRestart >= maxWindowSize;
			
			EpsilonBoxDominanceArchive archive = getArchive();

			if (doCheck && !doRestart) {
				//perform an epsilon-progress check	
				if (getNumberOfImprovements() <= improvementsAtLastCheck) {
					doRestart = true;
				}
				
				//check archive/population relative size
				if (algorithm.getPopulation().size() < 0.9 * (archive.size() / injectionRate)) {
					doRestart = true;
				}
				
				iterationsAtLastCheck = iteration;
				improvementsAtLastCheck = getNumberOfImprovements();
			}
			
			if (doRestart) {			
				//perform a random restart with injection
				Population population = algorithm.getPopulation();
				population.clear();
				population.addAll(archive);
				
				int newPopulationSize = (int)(archive.size() / injectionRate);
				
				if (newPopulationSize < minimumPopulationSize) {
					newPopulationSize = minimumPopulationSize;
				} else if (newPopulationSize > maximumPopulationSize) {
					newPopulationSize = maximumPopulationSize;
				}

				while (population.size() < newPopulationSize) {
					Solution[] parents = selection.select(variation.getArity(), archive);
					Solution[] children = variation.evolve(parents);
					
					for (Solution child : children) {
						algorithm.evaluate(child);
						population.add(child);
						archive.add(child);
					}
				}
				
				ts.setSize(Math.max(2, (int)(ps * newPopulationSize)));
				
				iterationsAtLastCheck = iteration;
				improvementsAtLastCheck = getNumberOfImprovements();
				iterationsAtLastRestart = iteration;
				
				numberOfRestarts++;
			}
		}

		@Override
		public Problem getProblem() {
			return algorithm.getProblem();
		}

		@Override
		public NondominatedPopulation getResult() {
			return algorithm.getResult();
		}
		
		@Override
		public Population getPopulation() {
			return algorithm.getPopulation();
		}

		@Override
		public EpsilonBoxDominanceArchive getArchive() {
			return algorithm.getArchive();
		}
		
		public int getNumberOfRestarts() {
			return numberOfRestarts;
		}

		@Override
		public void evaluate(Solution solution) {
			algorithm.evaluate(solution);
		}

		@Override
		public int getNumberOfEvaluations() {
			return algorithm.getNumberOfEvaluations();
		}

		@Override
		public boolean isTerminated() {
			return algorithm.isTerminated();
		}

		@Override
		public void terminate() {
			algorithm.terminate();
		}

	}


}
