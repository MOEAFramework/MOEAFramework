/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.population;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.TempFiles;
import org.moeaframework.TestThresholds;
import org.moeaframework.analysis.io.EmptyResultFileException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.format.TableFormat;
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
	public void testAsList() {
		List<Solution> list = population.asList();
		
		// list content is identical to population
		Assert.assertSize(population.size(), list);
		Assert.assertSame(population.get(0), list.get(0));
		Assert.assertSame(population.get(1), list.get(1));
		Assert.assertSame(population.get(2), list.get(2));
		Assert.assertSame(population.get(3), list.get(3));
		
		// changes to list do not affect population
		list.clear();
		Assert.assertEquals(4, population.size());
	}

	@Test
	public void testSortAndTruncate() {
		population.truncate(1, (o1, o2) -> Double.compare(o1.getObjectiveValue(0), o2.getObjectiveValue(0)));

		Assert.assertSize(1, population);
		Assert.assertEquals(1.0, population.get(0).getObjectiveValue(0), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testAsTabularData() throws IOException {
		File tempFile = TempFiles.createFile();
		TabularData<Solution> data = population.asTabularData();
		
		data.save(TableFormat.CSV, tempFile);
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
		
		CaptureResult result1 = Capture.stream(ps -> solution.display(ps));
		CaptureResult result2 = Capture.stream(ps -> population.display(ps));
		
		Assert.assertEquals(result1.toString(), result2.toString());
	}

	@Test
	public void testSaveLoadObjectives() throws IOException {
		File file = TempFiles.createFile();

		population.save(file);
		System.out.println(Files.readString(file.toPath()));
		Population population2 = Population.load(file);

		Assert.assertEquals(population.size(), population2.size());

		for (int i = 0; i < population.size(); i++) {
			Assert.assertArrayEquals(population.get(i).getObjectiveValues(), population2.get(i).getObjectiveValues(),
					TestThresholds.LOW_PRECISION);
		}
	}

	@Test
	public void testSaveLoadAllTypes() throws IOException {
		Solution solution = MockSolution.of()
				.withVariables(new BinaryVariable(10), new Grammar(5), new Permutation(5), new RealVariable(0.0, 1.0))
				.withObjectives(1.0, 0.0)
				.withConstraints(1.0)
				.build();

		population.clear();
		population.add(solution);

		File file = TempFiles.createFile();
		population.save(file);
		
		Population result = Population.load(file);
		Assert.assertEquals(population, result);
	}
	
	@Test(expected = EmptyResultFileException.class)
	public void testLoadOfInvalidFile() throws IOException {
		File file = TempFiles.createFile().withContent("foo");
		Population.load(file);
	}
	
	@Test
	public void testReadWhitespace() throws IOException {
		File file = TempFiles.createFile().withContent("0   1 \t 2\n\t   3 4 5 \t\n");
		Population population = Population.load(file);
		
		Assert.assertSize(2, population);
		Assert.assertArrayEquals(new double[] {0.0, 1.0, 2.0}, population.get(0).getObjectiveValues(), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] {3.0, 4.0, 5.0}, population.get(1).getObjectiveValues(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testRemoveAllWithPredicate() {
		Assert.assertFalse(population.removeAll(s -> false));
		Assert.assertSize(4, population);
		
		Assert.assertTrue(population.removeAll(s -> s.getObjectiveValue(0) < 3.0));
		Assert.assertSize(2, population);
		
		for (Solution solution : population) {
			Assert.assertGreaterThanOrEqual(solution.getObjectiveValue(0), 3.0);
		}
		
		Assert.assertTrue(population.removeAll(s -> true));
		Assert.assertSize(0, population);
	}
	
	@Test
	public void testFilter() {
		Population filtered = population.filter(s -> true);
		Assert.assertSize(4, filtered);
		Assert.assertEquals(filtered, population);
		
		filtered = population.filter(s -> s.getObjectiveValue(0) >= 3.0);
		Assert.assertSize(2, filtered);
		Assert.assertSize(4, population);
		
		for (Solution solution : filtered) {
			Assert.assertGreaterThanOrEqual(solution.getObjectiveValue(0), 3.0);
		}
		
		filtered = population.filter(s -> false);
		Assert.assertSize(0, filtered);
		Assert.assertSize(4, population);
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
		Assert.assertNotSame(population, copy);
		Assert.assertEquals(population, copy);
	}

}
