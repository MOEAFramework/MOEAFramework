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
package org.moeaframework.parallel.island.migration;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.parallel.island.AbstractIslandModelTest;
import org.moeaframework.parallel.island.Island;

public class SingleNeighborMigrationTest extends AbstractIslandModelTest {
	
	@Test
	public void test() {
		Island current = createIsland();
		Island neighbor = createIsland();
		
		Migration migration = new SingleNeighborMigration(1, new TournamentSelection());
		
		Solution solution1 = TestUtils.newSolution(0.0, 0.5);
		Solution solution2 = TestUtils.newSolution(1.0, 1.0);
		Solution solution3 = TestUtils.newSolution(0.5, 0.0);
		
		current.getPopulation().add(solution1);
		neighbor.getPopulation().add(solution2);
		
		// first migrate a dominating solution, which replaces the solution in the neighboring population
		migration.migrate(current, List.of(neighbor));
		Assert.assertEquals(1, neighbor.getPopulation().size());
		Assert.assertFalse(neighbor.getImmigrationQueue().isEmpty());
		
		migration.migrate(neighbor, List.of(current));
		Assert.assertEquals(1, neighbor.getPopulation().size());
		Assert.assertTrue(TestUtils.equals(solution1, neighbor.getPopulation().get(0)));
		Assert.assertTrue(neighbor.getImmigrationQueue().isEmpty());
		
		// then migrate a non-dominated solution, which randomly enters the neighboring population
		current.getImmigrationQueue().popAll();
		current.getPopulation().clear();
		current.getPopulation().add(solution3);
		
		migration.migrate(current, List.of(neighbor));
		Assert.assertEquals(1, neighbor.getPopulation().size());
		Assert.assertFalse(neighbor.getImmigrationQueue().isEmpty());
		
		migration.migrate(neighbor, List.of(current));
		Assert.assertEquals(1, neighbor.getPopulation().size());
		Assert.assertTrue(TestUtils.equals(solution1, neighbor.getPopulation().get(0)) ||
				TestUtils.equals(solution3, neighbor.getPopulation().get(0)));
		Assert.assertTrue(neighbor.getImmigrationQueue().isEmpty());
	}

}
