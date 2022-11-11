package org.moeaframework.examples.parallel;

import java.io.IOException;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;
import org.moeaframework.parallel.island.executor.ThreadedIslandExecutor;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.migration.OneWayMigration;
import org.moeaframework.parallel.island.topology.FullyConnectedTopology;
import org.moeaframework.parallel.island.topology.Topology;
import org.moeaframework.parallel.util.ThreadLocalMersenneTwister;
import org.moeaframework.util.TypedProperties;

/**
 * Compares the result from running an algorithm serially versus an island
 * model with 8 islands.  While the serial and island models are given
 * the same number of function evaluations, the island model tends to produce
 * better Pareto sets.
 */
public class IslandModelExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("UF1");
		
		PRNG.setRandom(ThreadLocalMersenneTwister.getInstance());
		
		Selection migrationSelection = new TournamentSelection(2, 
				new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()));
		
		Migration migration = new OneWayMigration(1, migrationSelection);
		Topology topology = new FullyConnectedTopology();
		IslandModel model = new IslandModel(1000, migration, topology);
		
		for (int i = 0; i < 8; i++) {
			NSGAII algorithm = (NSGAII)AlgorithmFactory.getInstance().getAlgorithm(
					"NSGAII", new TypedProperties(), problem);
			
			model.addIsland(new Island(algorithm, algorithm.getPopulation()));
		}
		
		Plot plot = new Plot();
		
		try (ThreadedIslandExecutor executor = new ThreadedIslandExecutor(model)) {
			plot.add("Island Model", executor.run(100000));
		}
		
		plot.add("Serial", new Executor()
				.withProblem("UF1")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(100000)
				.run());
		
		plot.show();
	}

}
