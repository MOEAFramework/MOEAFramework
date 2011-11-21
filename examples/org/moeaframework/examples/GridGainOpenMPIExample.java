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
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.Properties;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//
//import org.apache.commons.cli.CommandLine;
//import org.gridgain.grid.Grid;
//import org.gridgain.grid.GridException;
//import org.gridgain.grid.GridFactory;
//import org.gridgain.grid.GridMessageListener;
//import org.moeaframework.algorithm.AlgorithmFactory;
//import org.moeaframework.core.Algorithm;
//import org.moeaframework.core.Problem;
//import org.moeaframework.core.Solution;
//import org.moeaframework.problem.CPUDemo;
//import org.moeaframework.util.CommandLineUtility;
//import org.moeaframework.util.distributed.DistributedProblem;
//
///**
// * Example using GridGain and OpenMPI for running a distributed job. The MPI
// * node with rank 0 is designated as the master node; all others serve as worker
// * nodes. Also demonstrates GridGain's interprocess communication to send a
// * shutdown signal to worker nodes.
// */
//public class GridGainOpenMPIExample extends CommandLineUtility {
//
//	/**
//	 * Private constructor to prevent instantiation.
//	 */
//	private GridGainOpenMPIExample() {
//		super();
//	}
//
//	/**
//	 * Runs a genetic algorithm on the {@link CPUDemo} problem using the
//	 * specified executor service.
//	 * 
//	 * @param executor the executor service
//	 */
//	public void run(ExecutorService executor) {
//		Problem problem = new DistributedProblem(new CPUDemo(), executor);
//
//		Properties properties = new Properties();
//		properties.setProperty("populationSize", "500");
//
//		Algorithm algorithm = AlgorithmFactory.getAlgorithm("NSGAII",
//				properties, problem);
//
//		while (!algorithm.isTerminated() && (algorithm.getNumberOfEvaluations() < 10000)) {
//			algorithm.step();
//		}
//
//		for (Solution solution : algorithm.getResult()) {
//			System.out.println(Arrays.toString(solution.getObjectives()));
//		}
//
//		executor.shutdown();
//	}
//
//	@Override
//	public void run(CommandLine commandLine) {
//		Map<String, String> env = System.getenv();
//		String rank = env.get("OMPI_COMM_WORLD_RANK");
//
//		if ((rank == null) || (Integer.parseInt(rank) == 0)) {
//			// running on master node
//			try {
//				GridFactory.start();
//				Grid grid = GridFactory.getGrid();
//				ExecutorService executor = grid.newGridExecutorService();
//
//				// take a short nap to let workers come online
//				Thread.sleep(10000);
//
//				// run the job
//				long start = System.nanoTime();
//				run(executor);
//				System.out.println("Elapsed time: "
//						+ (System.nanoTime() - start) / 1e9);
//
//				// send shutdown signal to nodes; the {@link
//				// GridFactory#stopAll} method should probably be used, but
//				// seems unreliable
//				grid.sendMessage(grid.getRemoteNodes(), "shutdown");
//
//				// take a short nap to let shutdown signal propagate
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				GridFactory.stop(true);
//				System.exit(0);
//			}
//		} else {
//			// running on worker node, start node but rely on master to shutdown
//			try {
//				GridFactory.start();
//				Grid grid = GridFactory.getGrid();
//
//				// add listener to respond to shutdown signal
//				grid.addMessageListener(new GridMessageListener() {
//
//					@Override
//					public void onMessage(UUID id, Serializable message) {
//						if (message.equals("shutdown")) {
//							System.exit(0);
//						}
//					}
//
//				});
//			} catch (GridException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * Command line utility for the example using GridGain and OpenMPI for
//	 * running a distributed job.
//	 * 
//	 * @param args the command line arguments
//	 */
//	public static void main(String[] args) {
//		new GridGainOpenMPIExample().start(args);
//	}
//
//}
