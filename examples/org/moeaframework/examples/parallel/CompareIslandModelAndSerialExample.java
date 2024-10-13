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
package org.moeaframework.examples.parallel;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.Problem;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.selection.Selection;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;
import org.moeaframework.parallel.island.executor.ThreadedIslandExecutor;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.migration.SingleNeighborMigration;
import org.moeaframework.parallel.island.topology.FullyConnectedTopology;
import org.moeaframework.parallel.island.topology.Topology;
import org.moeaframework.problem.CEC2009.UF1;

/**
 * Compares the result from running an algorithm serially versus an island
 * model with 8 islands.  While the serial and island models are given
 * the same number of function evaluations, the island model tends to produce
 * better Pareto sets.
 */
public class CompareIslandModelAndSerialExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new UF1();
				
		Selection migrationSelection = new TournamentSelection(2, 
				new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()));
		
		Migration migration = new SingleNeighborMigration(1, migrationSelection);
		Topology topology = new FullyConnectedTopology();
		IslandModel model = new IslandModel(1000, migration, topology);
		
		for (int i = 0; i < 8; i++) {
			NSGAII algorithm = new NSGAII(problem);
			model.addIsland(new Island(algorithm, algorithm.getPopulation()));
		}
		
		Plot plot = new Plot();
		
		// run island-model version
		try (ThreadedIslandExecutor executor = new ThreadedIslandExecutor(model)) {
			plot.add("Island Model", executor.run(100000));
		}
		
		// run serial version
		NSGAII serialAlgorithm = new NSGAII(problem);
		serialAlgorithm.run(100000);
		plot.add("Serial", serialAlgorithm.getResult());
		
		plot.show();
	}

}
