package org.moeaframework.examples.parallel;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.parallel.SynchronizedMersenneTwister;
import org.moeaframework.parallel.SynchronizedNondominatedSortingPopulation;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.ThreadedIslandModel;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.migration.OneWayMigration;
import org.moeaframework.parallel.island.topology.FullyConnectedTopology;
import org.moeaframework.parallel.island.topology.Topology;

public class ThreadedIslandModelExample {
	
	public static void main(String[] args) {
		Problem problem = ProblemFactory.getInstance().getProblem("UF1");
		
		PRNG.setRandom(SynchronizedMersenneTwister.getInstance());
		
		Selection migrationSelection = new TournamentSelection(2, 
				new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()));
		
		Migration migration = new OneWayMigration(1, migrationSelection);
		
		Topology topology = new FullyConnectedTopology();
		
		ThreadedIslandModel model = new ThreadedIslandModel(1000, migration, topology);
		
		for (int i = 0; i < 8; i++) {
			SynchronizedNondominatedSortingPopulation population = new SynchronizedNondominatedSortingPopulation();
			
			NSGAII algorithm = new NSGAII(
					problem,
					population,
					null,
					new TournamentSelection(2, 
							new ChainedComparator(
									new ParetoDominanceComparator(),
									new CrowdingComparator())),
					new GAVariation(new SBX(1.0, 25.0), new PM(0.1, 30.0)),
					new RandomInitialization(problem, 100));
			
			model.addIsland(new Island(algorithm, population));
		}
		
		NondominatedPopulation result = model.run(100000);
		
		new Plot().add("Island Model", result).show();
	}

}
