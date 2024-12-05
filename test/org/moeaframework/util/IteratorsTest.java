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
package org.moeaframework.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.util.Iterators.IndexedValue;

public class IteratorsTest {
	
	@Test
	public void testEmptyOf() {
		Iterator<String> it = Iterators.of();
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testSingleItemOf() {
		Iterator<String> it = Iterators.of("foo");
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("foo", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testEmptyJoin() {
		Iterator<String> it = Iterators.join(Iterators.of());
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testSingleIteratorJoin() {
		Iterator<String> it = Iterators.join(Iterators.of("foo"));
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("foo", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testMultipleIteratorJoin() {
		Iterator<String> it = Iterators.join(Iterators.of("foo"), Iterators.of("bar"));
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("foo", it.next());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("bar", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testMultipleIteratorsWithEmptyJoin() {
		Iterator<String> it = Iterators.join(Iterators.of("foo"), Iterators.of(), Iterators.of("bar"));
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("foo", it.next());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("bar", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testIterableJoin() {
		Iterator<String> it = Iterators.join(List.of("foo"), List.of("bar")).iterator();
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("foo", it.next());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("bar", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testRemoveJoin() {
		List<String> list1 = new ArrayList<>(List.of("foo"));
		List<String> list2 = new ArrayList<>(List.of("bar"));
		
		Iterator<String> it = Iterators.join(list1, list2).iterator();
		
		it.next();
		it.remove();
		Assert.assertSize(0, list1);
		Assert.assertSize(1, list2);
		
		it.next();
		it.remove();
		Assert.assertSize(0, list1);
		Assert.assertSize(0, list2);
	}
	
	@Test
	public void testEmptyEnumerate() {
		Iterator<IndexedValue<String>> it = Iterators.enumerate(Iterators.of());
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testEnumerate() {
		Iterator<IndexedValue<String>> it = Iterators.enumerate(Iterators.of("first", "second"));
		
		Assert.assertTrue(it.hasNext());
		IndexedValue<String> first = it.next();
		Assert.assertEquals(0, first.getIndex());
		Assert.assertEquals("first", first.getValue());
		
		Assert.assertTrue(it.hasNext());
		IndexedValue<String> second = it.next();
		Assert.assertEquals(1, second.getIndex());
		Assert.assertEquals("second", second.getValue());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testIterableEnumerate() {
		Iterator<IndexedValue<String>> it = Iterators.enumerate(List.of("first", "second")).iterator();
		
		Assert.assertTrue(it.hasNext());
		IndexedValue<String> first = it.next();
		Assert.assertEquals(0, first.getIndex());
		Assert.assertEquals("first", first.getValue());
		
		Assert.assertTrue(it.hasNext());
		IndexedValue<String> second = it.next();
		Assert.assertEquals(1, second.getIndex());
		Assert.assertEquals("second", second.getValue());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testRemoveEnumerate() {
		List<String> list = new ArrayList<>(List.of("foo", "bar"));
		Iterator<IndexedValue<String>> it = Iterators.enumerate(list).iterator();
		
		it.next();
		it.remove();
		Assert.assertSize(1, list);
		
		it.next();
		it.remove();
		Assert.assertSize(0, list);
	}
	
	@Test
	public void testEmptyZip() {
		Iterator<Pair<String, String>> it = Iterators.zip(Iterators.of(), Iterators.of());
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testZip() {
		Iterator<Pair<String, String>> it = Iterators.zip(Iterators.of("foo", "hello"), Iterators.of("bar", "world"));
		
		Assert.assertTrue(it.hasNext());
		Pair<String, String> first = it.next();
		Assert.assertEquals("foo", first.getKey());
		Assert.assertEquals("bar", first.getValue());
		
		Assert.assertTrue(it.hasNext());
		Pair<String, String> second = it.next();
		Assert.assertEquals("hello", second.getKey());
		Assert.assertEquals("world", second.getValue());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testIterableZip() {
		Iterator<Pair<String, String>> it = Iterators.zip(List.of("foo", "hello"), List.of("bar", "world")).iterator();
		
		Assert.assertTrue(it.hasNext());
		Pair<String, String> first = it.next();
		Assert.assertEquals("foo", first.getKey());
		Assert.assertEquals("bar", first.getValue());
		
		Assert.assertTrue(it.hasNext());
		Pair<String, String> second = it.next();
		Assert.assertEquals("hello", second.getKey());
		Assert.assertEquals("world", second.getValue());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testRemoveZip() {
		List<String> list1 = new ArrayList<>(List.of("foo", "hello"));
		List<String> list2 = new ArrayList<>(List.of("bar", "world"));
		
		Iterator<Pair<String, String>> it = Iterators.zip(list1, list2).iterator();
		
		it.next();
		it.remove();
		Assert.assertSize(1, list1);
		Assert.assertSize(1, list2);
		
		it.next();
		it.remove();
		Assert.assertSize(0, list1);
		Assert.assertSize(0, list2);
	}
	
	@Test
	public void testEmptyMap() {
		Iterator<String> it = Iterators.map(Iterators.of(), (Function<Object, String>) Object::toString);
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testMap() {
		Iterator<String> it = Iterators.map(Iterators.of(1, 2), x -> x.toString());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("1", it.next());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("2", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testIterableMap() {
		Iterator<String> it = Iterators.map(List.of(1, 2), x -> x.toString()).iterator();
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("1", it.next());
		
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals("2", it.next());
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testRemoveMap() {
		List<Integer> list = new ArrayList<>(List.of(1, 2));
		Iterator<String> it = Iterators.map(list, x -> x.toString()).iterator();
		
		it.next();
		it.remove();
		Assert.assertSize(1, list);
		
		it.next();
		it.remove();
		Assert.assertSize(0, list);
	}
	
	@Test
	public void testEmptyMaterialize() {
		Iterator<String> it = Iterators.of();
		Assert.assertEquals(List.of(), Iterators.materialize(it));
	}
	
	@Test
	public void testMaterialize() {
		Iterator<String> it = Iterators.of("foo", "bar");
		Assert.assertEquals(List.of("foo", "bar"), Iterators.materialize(it));
	}
	
	@Test
	public void testIterableMaterialize() {
		Iterable<String> it = List.of("foo", "bar");
		Assert.assertEquals(List.of("foo", "bar"), Iterators.materialize(it));
	}
	
	@Test
	public void testEmptyLast() {
		Iterator<String> it = Iterators.of();
		Assert.assertNull(Iterators.last(it));
	}
	
	@Test
	public void testLast() {
		Iterator<String> it = Iterators.of("foo", "bar");
		Assert.assertEquals("bar", Iterators.last(it));
	}
	
	@Test
	public void testIterableLast() {
		Iterable<String> it = List.of("foo", "bar");
		Assert.assertEquals("bar", Iterators.last(it));
	}
	
	@Test
	public void testEmptyCount() {
		Iterator<String> it = Iterators.of();
		Assert.assertEquals(0, Iterators.count(it));
	}
	
	@Test
	public void testCount() {
		Iterator<String> it = Iterators.of("foo", "bar");
		Assert.assertEquals(2, Iterators.count(it));
	}
	
	@Test
	public void testIterableCount() {
		Iterable<String> it = List.of("foo", "bar");
		Assert.assertEquals(2, Iterators.count(it));
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testEmptyMinimum() {
		Iterator<Integer> it = Iterators.of();
		Iterators.minimum(it);
	}
	
	@Test
	public void testMinimum() {
		Iterator<Integer> it = Iterators.of(2, 1, 4, 3);
		Assert.assertEquals(1, Iterators.minimum(it));
	}
	
	@Test
	public void testIterableMinimum() {
		Iterable<Integer> it = List.of(2, 1, 4, 3);
		Assert.assertEquals(1, Iterators.minimum(it));
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testEmptyMaximum() {
		Iterator<Integer> it = Iterators.of();
		Iterators.maximum(it);
	}
	
	@Test
	public void testMaximum() {
		Iterator<Integer> it = Iterators.of(2, 1, 4, 3);
		Assert.assertEquals(4, Iterators.maximum(it));
	}
	
	@Test
	public void testIterableMaximum() {
		Iterable<Integer> it = List.of(2, 1, 4, 3);
		Assert.assertEquals(4, Iterators.maximum(it));
	}
	
	@Test
	public void testMinimumCustomKey() {
		Iterator<String> it = Iterators.of("hello", "world", "foo", "bar");
		Assert.assertEquals("foo", Iterators.minimum(it, s -> s.length(), Integer::compare));
	}
	
}
