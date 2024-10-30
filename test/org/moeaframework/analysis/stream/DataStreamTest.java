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
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.mock.function.MockBiConsumer;
import org.moeaframework.mock.function.MockConsumer;

public class DataStreamTest {
	
	@Test
	public void testEmpty() {
		DataStream<String> stream = DataStream.of();
		
		Assert.assertEquals(0, stream.size());
		Assert.assertEquals(List.of(), stream.values());
		Assert.assertArrayEquals(new String[0], stream.values(String[]::new));
		
		Assert.assertEquals(0, stream.map(x -> x).size());
		Assert.assertEquals(0, stream.sorted().size());
		Assert.assertEquals(0, stream.filter(x -> true).size());
		Assert.assertEquals(0, stream.keyedOn(x -> x).size());
		Assert.assertEquals(0, stream.distinct().size());
		Assert.assertEquals(0, stream.measure(Measures.count()));
		Assert.assertEquals(0, stream.groupBy(x -> x).size());
		
		Assert.assertThrows(NoSuchElementException.class, () -> stream.reduce((x, y) -> x + y));
		Assert.assertEquals("id", stream.reduce("id", (x, y) -> x + y));
		
		Assert.assertThrows(NoSuchElementException.class, () -> stream.first());
		Assert.assertThrows(NoSuchElementException.class, () -> stream.any());
		Assert.assertThrows(NoSuchElementException.class, () -> stream.single());
		Assert.assertEquals("default", stream.singleOrDefault("default"));
		
		MockConsumer<String> forEachConsumer = MockConsumer.of();
		stream.forEach(forEachConsumer);
		forEachConsumer.assertCallCount(0);
		
		MockBiConsumer<Integer, String> enumerateConsumer = MockBiConsumer.of();
		stream.enumerate(enumerateConsumer);
		enumerateConsumer.assertCallCount(0);
	}
	
	@Test
	public void test() {
		DataStream<String> stream = DataStream.of(List.of("foo", "bar"));
		
		Assert.assertEquals(2, stream.size());
		Assert.assertEquals(List.of("foo", "bar"), stream.values());
		Assert.assertArrayEquals(new String[] { "foo", "bar" }, stream.values(String[]::new));
		
		Assert.assertEquals(List.of("FOO", "BAR"), stream.map(x -> x.toUpperCase()).values());
		Assert.assertEquals(List.of("bar", "foo"), stream.sorted().values());
		Assert.assertEquals(List.of("bar"), stream.filter(x -> x.startsWith("b")).values());
		Assert.assertEquals(List.of("foo", "bar"), stream.keyedOn(x -> x).values());
		Assert.assertEquals(List.of("foo", "bar"), stream.distinct().values());
		Assert.assertEquals(2, stream.measure(Measures.count()));
		Assert.assertEquals(List.of("bar", "foo"), stream.groupBy(x -> x).keys());
		
		Assert.assertEquals("foobar", stream.reduce((x, y) -> x + y));
		Assert.assertEquals("idfoobar", stream.reduce("id", (x, y) -> x + y));
		
		Assert.assertEquals("foo", stream.first());
		Assert.assertEquals("foo", stream.any());
		Assert.assertThrows(NoSuchElementException.class, () -> stream.single());
		Assert.assertThrows(NoSuchElementException.class, () -> stream.singleOrDefault("default"));
		
		MockConsumer<String> forEachConsumer = MockConsumer.of();
		stream.forEach(forEachConsumer);
		forEachConsumer.assertCalls("foo", "bar");
		
		MockBiConsumer<Integer, String> enumerateConsumer = MockBiConsumer.of();
		stream.enumerate(enumerateConsumer);
		enumerateConsumer.assertCalls(Pair.of(0, "foo"), Pair.of(1, "bar"));
	}
	
	@Test
	public void testConstructors() {
		Assert.assertEquals(List.of(1, 2), DataStream.of(new Integer[] { 1, 2 }).values());
		Assert.assertEquals(List.of(1, 2), DataStream.of(List.of(1, 2)).values());
		Assert.assertEquals(List.of(1, 2), DataStream.of(IntStream.of(1, 2)).values());
		Assert.assertEquals(List.of(1.0, 2.0), DataStream.of(DoubleStream.of(1.0, 2.0)).values());
		
		Assert.assertEquals(List.of(0, 1), DataStream.range(2).values());
		Assert.assertEquals(List.of(1, 2), DataStream.range(1, 3).values());
		Assert.assertEquals(List.of(1, 1), DataStream.repeat(2, () -> 1).values());
		Assert.assertEquals(List.of(1, 2), DataStream.enumerate(2, (x) -> x + 1).values());
	}

}
