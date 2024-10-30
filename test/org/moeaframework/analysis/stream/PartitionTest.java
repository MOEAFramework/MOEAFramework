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
package org.moeaframework.analysis.stream;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.mock.function.MockBiConsumer;

public class PartitionTest {
	
	@Test
	public void testEmpty() {
		Partition<String, Integer> partition = Partition.of();
		
		Assert.assertEquals(0, partition.size());
		Assert.assertEquals(List.of(), partition.keys());
		Assert.assertArrayEquals(new String[0], partition.keys(String[]::new));
		Assert.assertEquals(List.of(), partition.values());
		Assert.assertArrayEquals(new Integer[0], partition.values(Integer[]::new));
		
		Assert.assertEquals(0, partition.map(x -> x).size());
		Assert.assertEquals(0, partition.sorted().size());
		Assert.assertEquals(0, partition.filter(x -> true).size());
		Assert.assertEquals(0, partition.distinct().size());
		Assert.assertEquals(0, partition.measure(Measures.count()));
		Assert.assertEquals(0, partition.groupBy(x -> x).size());
		
		Assert.assertThrows(NoSuchElementException.class, () -> partition.reduce((x, y) -> x + y));
		Assert.assertEquals(0, partition.reduce(0, (x, y) -> x + y));
		
		Assert.assertThrows(NoSuchElementException.class, () -> partition.first());
		Assert.assertThrows(NoSuchElementException.class, () -> partition.any());
		Assert.assertThrows(NoSuchElementException.class, () -> partition.single());
		Assert.assertEquals(Pair.of("foo", 0), partition.singleOrDefault("foo", 0));
		
		MockBiConsumer<String, Integer> forEachConsumer = MockBiConsumer.of();
		partition.forEach(forEachConsumer);
		forEachConsumer.assertCallCount(0);
		
		MockBiConsumer<Integer, Pair<String, Integer>> enumerateConsumer = MockBiConsumer.of();
		partition.enumerate(enumerateConsumer);
		enumerateConsumer.assertCallCount(0);
	}
	
	@Test
	public void test() {
		Partition<String, Integer> partition = Partition.zip(List.of("foo", "bar"), List.of(1, 2));
		
		Assert.assertEquals(2, partition.size());
		Assert.assertEquals(List.of("foo", "bar"), partition.keys());
		Assert.assertArrayEquals(new String[] { "foo", "bar" }, partition.keys(String[]::new));
		Assert.assertEquals(List.of(1, 2), partition.values());
		Assert.assertArrayEquals(new Integer[] { 1, 2 }, partition.values(Integer[]::new));
		
		Assert.assertEquals(List.of(11, 12), partition.map(x -> x + 10).values());
		Assert.assertEquals(List.of("bar", "foo"), partition.sorted().keys());
		Assert.assertEquals(List.of("bar"), partition.filter(x -> x.startsWith("b")).keys());
		Assert.assertEquals(List.of("foo", "bar"), partition.distinct().keys());
		Assert.assertEquals(2, partition.measure(Measures.count()));
		Assert.assertEquals(List.of("bar", "foo"), partition.groupBy(x -> x).keys());
		
		Assert.assertEquals(3, partition.reduce((x, y) -> x + y));
		Assert.assertEquals(13, partition.reduce(10, (x, y) -> x + y));
		
		Assert.assertEquals(Pair.of("foo", 1), partition.first());
		Assert.assertEquals(Pair.of("foo", 1), partition.any());
		Assert.assertThrows(NoSuchElementException.class, () -> partition.single());
		Assert.assertThrows(NoSuchElementException.class, () -> partition.singleOrDefault("default", 10));
		
		MockBiConsumer<String, Integer> forEachConsumer = MockBiConsumer.of();
		partition.forEach(forEachConsumer);
		forEachConsumer.assertCalls(Pair.of("foo", 1), Pair.of("bar", 2));
		
		MockBiConsumer<Integer, Pair<String, Integer>> enumerateConsumer = MockBiConsumer.of();
		partition.enumerate(enumerateConsumer);
		enumerateConsumer.assertCalls(Pair.of(0, Pair.of("foo", 1)), Pair.of(1, Pair.of("bar", 2)));
	}
	
	@Test
	public void testConstructors() {
		Assert.assertEquals(List.of(1, 2), Partition.of(List.of(1, 2)).keys());
		Assert.assertEquals(List.of(1, 2), Partition.of(List.of(1, 2)).values());
		
		Assert.assertEquals(List.of("1", "2"), Partition.of(x -> x.toString(), List.of(1, 2)).keys());
		Assert.assertEquals(List.of(1, 2), Partition.of(List.of(1, 2)).values());
		
		Assert.assertEquals(List.of("foo", "bar"), Partition.zip(List.of("foo", "bar"), List.of(1, 2)).keys());
		Assert.assertEquals(List.of(1, 2), Partition.zip(List.of("foo", "bar"), List.of(1, 2)).values());
	}

}
