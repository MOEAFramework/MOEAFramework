///* Copyright 2009-2011 David Hadka
// * 
// * This file is part of the MOEA Framework.
// * 
// * The MOEA Framework is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by 
// * the Free Software Foundation, either version 3 of the License, or (at your 
// * option) any later version.
// * 
// * The MOEA Framework is distributed in the hope that it will be useful, but 
// * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
// * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public 
// * License for more details.
// * 
// * You should have received a copy of the GNU Lesser General Public License 
// * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.moeaframework.studies.borg;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//import org.apache.commons.cli.CommandLine;
//import org.moeaframework.algorithm.EpsilonProgressContinuation;
//import org.moeaframework.algorithm.RestartEvent;
//import org.moeaframework.algorithm.RestartListener;
//import org.moeaframework.algorithm.EpsilonMOEA;
//import org.moeaframework.analysis.Accumulator;
//import org.moeaframework.analysis.collector.AdaptiveMultimethodVariationCollector;
//import org.moeaframework.analysis.collector.AdaptiveTimeContinuationCollector;
//import org.moeaframework.analysis.collector.AlgorithmCollector;
//import org.moeaframework.analysis.collector.Collector;
//import org.moeaframework.analysis.sensitivity.EpsilonHelper;
//import org.moeaframework.core.Algorithm;
//import org.moeaframework.core.EpsilonBoxDominanceArchive;
//import org.moeaframework.core.Initialization;
//import org.moeaframework.core.Population;
//import org.moeaframework.core.Problem;
//import org.moeaframework.core.comparator.AggregateConstraintComparator;
//import org.moeaframework.core.comparator.ChainedComparator;
//import org.moeaframework.core.comparator.DominanceComparator;
//import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;
//import org.moeaframework.core.comparator.ParetoDominanceComparator;
//import org.moeaframework.core.operator.AdaptiveMultimethodVariation;
//import org.moeaframework.core.operator.GAVariation;
//import org.moeaframework.core.operator.RandomInitialization;
//import org.moeaframework.core.operator.TournamentSelection;
//import org.moeaframework.core.operator.UniformSelection;
//import org.moeaframework.core.operator.real.DifferentialEvolution;
//import org.moeaframework.core.operator.real.PCX;
//import org.moeaframework.core.operator.real.PolynomialStepMutation;
//import org.moeaframework.core.operator.real.SBX;
//import org.moeaframework.core.operator.real.SPX;
//import org.moeaframework.core.operator.real.UM;
//import org.moeaframework.core.operator.real.UNDX;
//import org.moeaframework.core.spi.ProblemFactory;
//import org.moeaframework.util.ArrayMath;
//import org.moeaframework.util.CommandLineUtility;
//import org.moeaframework.util.TypedProperties;
//import org.moeaframework.util.Vector;
//
//public class BorgOperatorProbabilities extends CommandLineUtility {
//	
//	private static String[] keys = new String[] { "SBX", 
//			"DifferentialEvolution", "PCX", "SPX", "UNDX", "UM" };
//	
//	public static final String[] problems = new String[] { "UF1", "UF2", "UF3",
//		"UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12",
//		"UF13", "DTLZ1_2", "DTLZ1_4", "DTLZ1_6", "DTLZ1_8", "DTLZ2_2",
//		"DTLZ2_4", "DTLZ2_6", "DTLZ2_8", "DTLZ3_2", "DTLZ3_4", "DTLZ3_6",
//		"DTLZ3_8", "DTLZ4_2", "DTLZ4_4", "DTLZ4_6", "DTLZ4_8", "DTLZ7_2",
//		"DTLZ7_4", "DTLZ7_6", "DTLZ7_8" };
//
//	public static int seeds = 50;
//	
//	public static int totalEvaluations = 500000;
//	
//	public static int resolution = totalEvaluations / 50;
//
//	private BorgOperatorProbabilities() {
//		super();
//	}
//
//	private int getSelectionSize(double selectionRatio, int populationSize) {
//		return Math.max((int)(populationSize * selectionRatio), 2);
//	}
//
//	private Accumulator runInstance(Problem problem) {
//		final TypedProperties properties = new TypedProperties(new Properties());
//
//		int initialPopulationSize = (int)properties.getDouble(
//				"initialPopulationSize", 100);
//
//		Initialization initialization = new RandomInitialization(problem,
//				initialPopulationSize);
//
//		Population population = new Population();
//
//		DominanceComparator comparator = new ChainedComparator(
//				new AggregateConstraintComparator(),
//				new ParetoDominanceComparator());
//
//		EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
//				new EpsilonBoxDominanceComparator(properties.getDoubleArray(
//						"epsilon", new double[] { EpsilonHelper
//								.getEpsilon(problem) })));
//
//		final TournamentSelection selection = new TournamentSelection(
//				getSelectionSize(properties.getDouble("selectionRatio", 0.02),
//						initialPopulationSize), comparator);
//
//		SBX sbx = new SBX(properties.getDouble("sbx.rate", 1.0), properties
//				.getDouble("sbx.distributionIndex", 15.0));
//
//		DifferentialEvolution de = new DifferentialEvolution(properties
//				.getDouble("de.crossoverRate", 1.0), properties.getDouble(
//				"de.stepSize", 0.5));
//
//		PCX pcx = new PCX((int)properties.getDouble("pcx.parents", 3),
//				(int)properties.getDouble("pcx.offspring", 2), properties
//						.getDouble("pcx.eta", 0.1), properties.getDouble(
//						"pcx.zeta", 0.1));
//
//		SPX spx = new SPX((int)properties.getDouble("spx.parents", 3),
//				(int)properties.getDouble("spx.offspring", 2), properties
//						.getDouble("spx.epsilon", 2));
//
//		UNDX undx = new UNDX((int)properties.getDouble("undx.parents", 3),
//				(int)properties.getDouble("undx.offspring", 2), properties
//						.getDouble("undx.zeta", 0.5), properties.getDouble(
//						"undx.eta", 0.35));
//
//		UM um = new UM(properties.getDouble("um.rate", 1.0 / problem
//				.getNumberOfVariables()));
//
//		PolynomialStepMutation pm = new PolynomialStepMutation(properties
//				.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()),
//				properties.getDouble("pm.distributionIndex", 20.0));
//
//		AdaptiveMultimethodVariation variation = new AdaptiveMultimethodVariation(
//				archive);
//		variation.addOperator(new GAVariation(sbx, pm));
//		variation.addOperator(new GAVariation(de, pm));
//		variation.addOperator(new GAVariation(pcx, pm));
//		variation.addOperator(new GAVariation(spx, pm));
//		variation.addOperator(new GAVariation(undx, pm));
//		variation.addOperator(um);
//
//		EpsilonMOEA emoea = new EpsilonMOEA(problem, population, archive, 
//				selection, variation, initialization);
//
//		final EpsilonProgressContinuation algorithm = new EpsilonProgressContinuation(
//				emoea, 100, 10000, 1.0 / properties
//						.getDouble("injectionRate", 0.25), 100, 10000,
//				new UniformSelection(), new UM(1.0 / problem
//						.getNumberOfVariables()));
//
//		algorithm.addRestartListener(new RestartListener() {
//
//			@Override
//			public void restarted(RestartEvent event) {
//				selection.setSize(getSelectionSize(properties.getDouble(
//						"selectionRatio", 0.02), algorithm.getPopulation()
//						.size()));
//			}
//
//		});
//
//		List<Collector> collectors = new ArrayList<Collector>();
//		collectors.add(new AlgorithmCollector(algorithm));
//		collectors.add(new AdaptiveMultimethodVariationCollector(variation));
//		collectors.add(new AdaptiveTimeContinuationCollector(algorithm));
//
//		return collect(algorithm, collectors);
//	}
//
//	private Accumulator collect(Algorithm algorithm, List<Collector> collectors) {
//		Accumulator accumulator = new Accumulator();
//
//		for (Collector collector : collectors) {
//			collector.collect(accumulator);
//		}
//
//		int evaluationsAtLastSave = algorithm.getNumberOfEvaluations();
//
//		while (!algorithm.isTerminated() && (algorithm.getNumberOfEvaluations() < totalEvaluations)) {
//			algorithm.step();
//
//			if (evaluationsAtLastSave + resolution <= algorithm
//					.getNumberOfEvaluations()) {
//				for (Collector collector : collectors) {
//					collector.collect(accumulator);
//				}
//
//				evaluationsAtLastSave = algorithm.getNumberOfEvaluations();
//			}
//		}
//
//		if (evaluationsAtLastSave < totalEvaluations) {
//			for (Collector collector : collectors) {
//				collector.collect(accumulator);
//			}
//		}
//		
//		return accumulator;
//	}
//
//	public static void main(String[] args) throws IOException {
//		new BorgOperatorProbabilities().start(args);
//	}
//	
//	private double[] calculateProbabilities(Accumulator accumulator) {
//		double[] sums = new double[keys.length];
//		
//		for (int i=0; i<keys.length; i++) {
//			String key = keys[i];
//			
//			for (int j=0; j<accumulator.size(key); j++) {
//				sums[i] += accumulator.get(key, j).doubleValue();
//			}
//		}
//		
//		double sum = ArrayMath.sum(sums);
//		
//		for (int i=0; i<keys.length; i++) {
//			sums[i] /= sum;
//		}
//		
//		return sums;
//	}
//
//	@Override
//	public void run(CommandLine commandLine) throws Exception {
//		System.out.print("Problem");
//		for (int i = 0; i < keys.length; i++) {
//			System.out.print(" " + keys[i]);
//		}
//		System.out.print(" Hard_Restarts Soft_Restarts Total_Restarts");
//		System.out.println();
//		
//		for (String problemName : problems) {
//			Problem problem = ProblemFactory.getInstance().getProblem(
//					problemName);
//			double[] probabilities = new double[keys.length];
//			double hardRestartRate = 0.0;
//			double softRestartRate = 0.0;
//			double totalRestartRate = 0.0;
//
//			for (int i = 0; i < seeds; i++) {
//				Accumulator accumulator = runInstance(problem);
//				probabilities = Vector.add(probabilities, calculateProbabilities(accumulator));
//
//				for (int j = accumulator.size("Number of Hard Restarts")-1; j > 0; j--) {
//					int hardRate = accumulator.get("Number of Hard Restarts", j).intValue() - accumulator.get("Number of Hard Restarts", j-1).intValue();
//					int softRate = accumulator.get("Number of Soft Restarts", j).intValue() - accumulator.get("Number of Soft Restarts", j-1).intValue();
//					
//					if (hardRate + softRate < 20) {
//						hardRestartRate += hardRate;
//						softRestartRate += softRate;
//					}
//					
//					totalRestartRate += hardRate + softRate;
//				}
//			}
//			
//			probabilities = Vector.divide(probabilities, seeds);
//			hardRestartRate /= seeds;
//			softRestartRate /= seeds;
//			totalRestartRate /= seeds;
//			
//			System.out.print(problemName);
//			for (int i = 0; i < keys.length; i++) {
//				System.out.print(" " + probabilities[i]);
//			}
//			System.out.print(" " + hardRestartRate);
//			System.out.print(" " + softRestartRate);
//			System.out.print(" " + totalRestartRate);
//			System.out.println();
//		}
//	}
//
//}
