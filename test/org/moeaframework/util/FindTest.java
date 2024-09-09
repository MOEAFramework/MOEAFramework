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

import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.util.Find.IndexedItem;
import org.moeaframework.util.Find.KeyedItem;

public class FindTest {
	
	@Test
	public void testMatchEmpty() {
		IndexedItem<?> result = Find.match(List.of(), x -> true);
		Assert.assertNull(result);
	}
	
	@Test
	public void testMatchSingleElement() {
		IndexedItem<Integer> result = Find.match(List.of(3), x -> true);
		Assert.assertEquals(0, result.getIndex());
		Assert.assertEquals(3, result.getValue().intValue());
	}
	
	@Test
	public void testMatch() {
		IndexedItem<Integer> result = Find.match(List.of(3, 2, 4), x -> x == 2);
		Assert.assertEquals(1, result.getIndex());
		Assert.assertEquals(2, result.getValue().intValue());
	}
	
	@Test
	public void testNoMatch() {
		IndexedItem<Integer> result = Find.match(List.of(3, 2, 4), x -> false);
		Assert.assertNull(result);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFindEmpty() {
		Find.find(List.of(), Comparator.naturalOrder());
	}
	
	@Test
	public void testFindSingleElement() {
		IndexedItem<Integer> result = Find.find(List.of(3), Comparator.naturalOrder());
		Assert.assertEquals(0, result.getIndex());
		Assert.assertEquals(3, result.getValue().intValue());
	}
	
	@Test
	public void testFind() {
		IndexedItem<Integer> result = Find.find(List.of(3, 2, 4), Comparator.naturalOrder());
		Assert.assertEquals(1, result.getIndex());
		Assert.assertEquals(2, result.getValue().intValue());
	}
	
	@Test
	public void testFindMinimum() {
		IndexedItem<Integer> result = Find.minimum(List.of(3, 2, 4));
		Assert.assertEquals(1, result.getIndex());
		Assert.assertEquals(2, result.getValue().intValue());
	}
	
	@Test
	public void testFindMaximum() {
		IndexedItem<Integer> result = Find.maximum(List.of(3, 2, 4));
		Assert.assertEquals(2, result.getIndex());
		Assert.assertEquals(4, result.getValue().intValue());
	}
	
	@Test
	public void testFindKeyMinimum() {
		KeyedItem<Integer, Integer> result = Find.minimum(List.of(3, 2, 4), k -> 10 - k);
		Assert.assertEquals(2, result.getIndex());
		Assert.assertEquals(6, result.getKey().intValue());
		Assert.assertEquals(4, result.getValue().intValue());
	}
	
	@Test
	public void testFindKeyMaximum() {
		KeyedItem<Integer, Integer> result = Find.maximum(List.of(3, 2, 4), k -> 10 - k);
		Assert.assertEquals(1, result.getIndex());
		Assert.assertEquals(8, result.getKey().intValue());
		Assert.assertEquals(2, result.getValue().intValue());
	}
	
}
