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
package org.moeaframework.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.format.TabularData;

public class PopulationTest {

	private Population population;
	private Population constrainedPopulation;

	@Before
	public void setUp() {
		population = new Population(List.of(
				MockSolution.of().withObjectives(3.0, 2.0, 3.0),
				MockSolution.of().withObjectives(1.0, 2.0, 2.0),
				MockSolution.of().withObjectives(2.0, 2.0, 3.0),
				MockSolution.of().withObjectives(4.0, 3.0, 2.0)));
		
		constrainedPopulation = new Population(List.of(
				MockSolution.of().withObjectives(3.0, 2.0, 3.0).withConstraints(0.0),
				MockSolution.of().withObjectives(1.0, 2.0, 2.0).withConstraints(1.0),
				MockSolution.of().withObjectives(2.0, 2.0, 3.0).withConstraints(-1.0),
				MockSolution.of().withObjectives(4.0, 3.0, 2.0).withConstraints(0.0)));
	}

	@After
	public void tearDown() {
		population = null;
		constrainedPopulation = null;
	}

	@Test
	public void testCopyConstructor() {
		Population copy = new Population(population);

		Assert.assertEquals(population.size(), copy.size());
		Assert.assertTrue(copy.containsAll(population));
		Assert.assertTrue(population.containsAll(copy));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOutOfBounds1() {
		population.get(4);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOutOfBounds2() {
		population.get(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveOutOfBounds1() {
		population.remove(4);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveOutOfBounds2() {
		population.remove(-1);
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testConcurrentModificationWithObject() {
		for (Solution solution : population) {
			population.remove(solution);
		}
	}

	@Test(expected = ConcurrentModificationException.class)
	@SuppressWarnings("unused")
	public void testConcurrentModificationWithIndex() {
		for (Solution solution : population) {
			population.remove(0);
		}
	}

	@Test(expected = ConcurrentModificationException.class)
	@SuppressWarnings("unused")
	public void testConcurrentModificationOnClear() {
		for (Solution solution : population) {
			population.clear();
		}
	}

	@Test
	public void test() {
		Population subset = new Population();
		Assert.assertTrue(subset.add(population.get(1)));
		Assert.assertTrue(subset.add(population.get(3)));

		Assert.assertFalse(subset.contains(population.get(0)));
		Assert.assertTrue(subset.contains(population.get(1)));
		Assert.assertTrue(population.containsAll(subset));
		Assert.assertFalse(subset.containsAll(population));

		Assert.assertTrue(population.removeAll(subset));
		Assert.assertFalse(population.removeAll(subset));

		Assert.assertEquals(2, population.size());

		Solution s0 = population.get(0);
		Solution s1 = population.get(1);

		Assert.assertEquals(0, population.indexOf(s0));
		Assert.assertEquals(1, population.indexOf(s1));

		population.remove(0);

		Assert.assertEquals(1, population.size());
		Assert.assertEquals(-1, population.indexOf(s0));
		Assert.assertEquals(0, population.indexOf(s1));

		Assert.assertFalse(population.remove(s0));
		Assert.assertEquals(1, population.size());
		Assert.assertFalse(population.isEmpty());
		Assert.assertTrue(population.remove(s1));
		Assert.assertEquals(0, population.size());
		Assert.assertTrue(population.isEmpty());

		Assert.assertTrue(population.addAll(subset));
		Assert.assertTrue(population.add(subset.get(0)));
		Assert.assertEquals(3, population.size());

		population.clear();

		Assert.assertTrue(population.isEmpty());
	}

	@Test
	public void testSortAndTruncate() {
		population.truncate(1, new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {
				return Double.compare(o1.getObjective(0), o2.getObjective(0));
			}

		});

		Assert.assertSize(1, population);
		Assert.assertEquals(1.0, population.get(0).getObjective(0), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testAsTabularData() throws IOException {
		File tempFile = TempFiles.createFile();
		TabularData<Solution> data = population.asTabularData();
		
		data.saveCSV(tempFile);
		Assert.assertLineCount(population.size() + 1, tempFile);
	}
	
	@Test
	public void testDisplay() throws IOException {
		Solution solution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0), new BinaryIntegerVariable(5, 10), new BinaryVariable(5),
						new Permutation(5), new Subset(5, 10))
				.withObjectives(1.0, 0.0)
				.withConstraints(0.0)
				.build();
		
		Population population = new Population(List.of(solution));
		
		try (ByteArrayOutputStream output1 = new ByteArrayOutputStream();
				ByteArrayOutputStream output2 = new ByteArrayOutputStream();
				PrintStream ps1 = new PrintStream(output1);
				PrintStream ps2 = new PrintStream(output2)) {
			solution.display(ps1);
			population.display(ps2);
			
			Assert.assertEquals(output1.toString(), output2.toString());
		}
	}

	@Test
	public void testSaveLoadObjectives() throws IOException {
		File file = TempFiles.createFile();

		population.saveObjectives(file);
		Population population2 = Population.loadObjectives(file);

		Assert.assertEquals(population.size(), population2.size());

		for (int i = 0; i < population.size(); i++) {
			Assert.assertArrayEquals(population.get(i).getObjectives(), population2.get(i).getObjectives(),
					TestThresholds.LOW_PRECISION);
		}
	}

	@Test
	public void testSaveLoadBinary() throws IOException {		
		Solution s1 = MockSolution.of()
				.withVariables(new BinaryVariable(10), new Grammar(5), new Permutation(5), new RealVariable(0.0, 1.0))
				.withObjectives(1.0, 0.0)
				.withConstraints(1.0)
				.build();

		Solution s2 = MockSolution.of().withObjectives(1.0, -1.0).build();

		population.clear();
		population.addAll(List.of(s1, s2));

		File file = TempFiles.createFile();
		population.saveBinary(file);
		
		Population population2 = Population.loadBinary(file);
		Assert.assertEquals(population, population2);
	}
	
	@Test
	public void testReadWhitespace() throws IOException {
		File file = TempFiles.createFileWithContent("0   1 \t 2\n\t   3 4 5 \t\n");
		Population population = Population.loadObjectives(file);
		
		Assert.assertArrayEquals(new double[] {0.0, 1.0, 2.0}, population.get(0).getObjectives(), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] {3.0, 4.0, 5.0}, population.get(1).getObjectives(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testFilter() {
		population.filter(s -> true);
		Assert.assertSize(4, population);
		
		population.filter(s -> s.getObjective(0) >= 3.0);
		Assert.assertSize(2, population);
		
		for (Solution solution : population) {
			Assert.assertGreaterThanOrEqual(solution.getObjective(0), 3.0);
		}
		
		population.filter(s -> false);
		Assert.assertSize(0, population);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetLowerBoundsOnEmptyPopulation() {
		new Population().getLowerBounds();
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testGetUpperBoundsOnEmptyPopulation() {
		new Population().getUpperBounds();
	}
	
	@Test
	public void testGetLowerBounds() {
		double[] lowerBounds = constrainedPopulation.getLowerBounds();
		Assert.assertArrayEquals(new double[] { 1.0, 2.0, 2.0 }, lowerBounds, TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testGetUpperBounds() {
		double[] upperBounds = constrainedPopulation.getUpperBounds();
		Assert.assertArrayEquals(new double[] { 4.0, 3.0, 3.0 }, upperBounds, TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testCopy() {
		Population copy = population.copy();
		Assert.assertEquals(population, copy);
	}

}
