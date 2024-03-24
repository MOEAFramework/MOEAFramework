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
package org.moeaframework.core.selection;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.algorithm.single.LinearDominanceComparator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.mock.MockSolution;

public class TournamentSelectionTest {

	private Solution solution1;
	private Solution solution2;
	private Solution solution3;

	private Population population;

	@Before
	public void setUp() {
		solution1 = MockSolution.of().withObjectives(0);
		solution2 = MockSolution.of().withObjectives(1);
		solution3 = MockSolution.of().withObjectives(2);

		population = new Population(List.of(solution1, solution2, solution3));
	}

	@After
	public void tearDown() {
		solution1 = null;
		solution2 = null;
		solution3 = null;
		population = null;
	}

	private double getPressure(int populationSize, int tournamentSize, int i) {
		return Math.pow(populationSize, -tournamentSize)
				* (Math.pow(populationSize - i + 1, tournamentSize) - Math.pow(populationSize - i, tournamentSize));
	}
	
	@Test
	public void testSetters() {
		TournamentSelection selection = new TournamentSelection(2);
		Assert.assertEquals(2, selection.getSize());
		Assert.assertNotNull(selection.getComparator());
		Assert.assertTrue(selection.getComparator() instanceof ParetoDominanceComparator);
		
		selection.setSize(3);
		Assert.assertEquals(3, selection.getSize());
		Assert.assertNotNull(selection.getComparator());
		Assert.assertTrue(selection.getComparator() instanceof ParetoDominanceComparator);
		
		selection.setComparator(new LinearDominanceComparator());
		Assert.assertEquals(3, selection.getSize());
		Assert.assertNotNull(selection.getComparator());
		Assert.assertTrue(selection.getComparator() instanceof LinearDominanceComparator);
	}

	@Test
	public void testTernaryTournamentSelectionPressure() {
		int[] counts = new int[3];

		TournamentSelection selection = new TournamentSelection(3);

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solution = selection.select(1, population)[0];
			counts[(int)solution.getObjective(0)]++;
		}

		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(getPressure(3, 3, i + 1), counts[i] / (double)TestThresholds.SAMPLES,
					TestThresholds.SELECTION_EPS);
		}
	}

	@Test
	public void testBinaryTournamentSelectionPressure() {
		int[] counts = new int[3];

		TournamentSelection selection = new TournamentSelection();

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solution = selection.select(1, population)[0];
			counts[(int)solution.getObjective(0)]++;
		}

		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(getPressure(3, 2, i + 1), counts[i] / (double)TestThresholds.SAMPLES,
					TestThresholds.SELECTION_EPS);
		}
	}

	@Test
	public void testUnaryTournamentSelectionPressure() {
		int[] counts = new int[3];

		TournamentSelection selection = new TournamentSelection(1);

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solution = selection.select(1, population)[0];
			counts[(int)solution.getObjective(0)]++;
		}

		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(getPressure(3, 1, i + 1), counts[i] / (double)TestThresholds.SAMPLES,
					TestThresholds.SELECTION_EPS);
		}
	}

	@Test
	public void testLargePopulationAndTournamentSize() {
		testSelectionPressure(100, 2);
		testSelectionPressure(100, 8);
		testSelectionPressure(100, 16);
	}

	private void testSelectionPressure(int populationSize, int tournamentSize) {
		Population population = new Population();

		for (int i = 0; i < populationSize; i++) {
			population.add(MockSolution.of().withObjectives(i));
		}

		int[] counts = new int[populationSize];

		TournamentSelection selection = new TournamentSelection(tournamentSize);

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solution = selection.select(1, population)[0];
			counts[(int)solution.getObjective(0)]++;
		}

		for (int i = 0; i < populationSize; i++) {
			Assert.assertEquals(getPressure(populationSize, tournamentSize, i + 1),
					counts[i] / (double)TestThresholds.SAMPLES, TestThresholds.SELECTION_EPS);
		}
	}

}
