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
//package org.moeaframework.examples;
//
//import java.util.Arrays;
//import java.util.Properties;
//
//import org.apache.commons.cli.CommandLine;
//import org.moeaframework.algorithm.AlgorithmFactory;
//import org.moeaframework.core.Algorithm;
//import org.moeaframework.core.Problem;
//import org.moeaframework.core.Solution;
//import org.moeaframework.problem.CPUDemo;
//import org.moeaframework.util.CommandLineUtility;
//
///**
// * Computes the baseline evaluation time of {@link CPUDemo} by running a
// * single-core optimization. This can be compared to the runtime of
// * {@link GridGainOpenMPIExample} to determine the speedup.
// */
//public class DistributedBaselineExample extends CommandLineUtility {
//
//	/**
//	 * Private constructor to prevent instantiation.
//	 */
//	private DistributedBaselineExample() {
//		super();
//	}
//
//	@Override
//	public void run(CommandLine commandLine) {
//		long start = System.nanoTime();
//
//		Problem problem = new CPUDemo();
//
//		Algorithm algorithm = AlgorithmFactory.getAlgorithm("NSGAII",
//				new Properties(), problem);
//
//		while (!algorithm.isTerminated() && (algorithm.getNumberOfEvaluations() < 1000)) {
//			algorithm.step();
//		}
//
//		for (Solution solution : algorithm.getResult()) {
//			System.out.println(Arrays.toString(solution.getObjectives()));
//		}
//
//		System.out
//				.println("Elapsed time: " + (System.nanoTime() - start) / 1e9);
//	}
//
//	/**
//	 * Command line utility for computing the baseline evaluation time of
//	 * {@link CPUDemo} by running a single-core optimization.
//	 * 
//	 * @param args the command line arguments
//	 */
//	public static void main(String[] args) {
//		new DistributedBaselineExample().start(args);
//	}
//
//}
