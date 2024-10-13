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
package org.moeaframework.parallel.island.executor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.selection.Selection;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.parallel.island.AbstractIslandModelTest;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;
import org.moeaframework.parallel.island.migration.Migration;
import org.moeaframework.parallel.island.migration.SingleNeighborMigration;
import org.moeaframework.parallel.island.topology.FullyConnectedTopology;
import org.moeaframework.parallel.island.topology.Topology;

public class BasicIslandExecutorTest extends AbstractIslandModelTest {
	
	@Test
	public void test() throws IOException {
		Selection migrationSelection = new TournamentSelection(2, 
				new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()));
		
		CountingMigration migration = new CountingMigration(new SingleNeighborMigration(1, migrationSelection));
		Topology topology = new FullyConnectedTopology();
		IslandModel model = new IslandModel(1000, migration, topology);
				
		for (int i = 0; i < 2; i++) {
			NSGAII algorithm = new NSGAII(new MockRealProblem(2));
			model.addIsland(new Island(algorithm, algorithm.getPopulation()));
		}
		
		for (Island island : model.getIslands()) {
			Assert.assertEquals(0 , island.getAlgorithm().getNumberOfEvaluations());
		}
		
		try (BasicIslandExecutor executor = new BasicIslandExecutor(model, Executors.newSingleThreadExecutor())) {
			NondominatedPopulation result = executor.run(100000);			
			Assert.assertNotNull(result);
			
			for (Island island : model.getIslands()) {
				Assert.assertEquals(50000, island.getAlgorithm().getNumberOfEvaluations());
			}
			
			Assert.assertEquals(100, migration.getCallCount());
			
			// check that the overall result is the combined set of individual results
			NondominatedPopulation expectedResult = new NondominatedPopulation();

			for (Island island : model.getIslands()) {
				expectedResult.addAll(island.getAlgorithm().getResult());
			}
			
			Assert.assertEquals(expectedResult, result);
		}
	}
	
	private class CountingMigration implements Migration {
		
		private final Migration inner;
		
		private final AtomicInteger count;
		
		public CountingMigration(Migration inner) {
			super();
			this.inner = inner;
			
			count = new AtomicInteger();
		}

		@Override
		public void migrate(Island current, List<Island> neighbors) {
			count.incrementAndGet();
			inner.migrate(current, neighbors);
		}
		
		public int getCallCount() {
			return count.get();
		}
		
	}

}
