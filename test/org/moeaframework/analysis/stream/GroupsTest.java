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

import org.junit.Test;
import org.moeaframework.Assert;

public class GroupsTest {
	
	@Test
	public void testEmpty() {
		Partition<String, Integer> partition = Partition.of();
		Groups<String, String, Integer> groups = partition.groupBy(x -> x);
		
		Assert.assertEquals(0, groups.size());
		Assert.assertEquals(List.of(), groups.keys());
		Assert.assertArrayEquals(new String[0], groups.keys(String[]::new));
		Assert.assertEquals(List.of(), groups.values());
		Assert.assertArrayEquals(new Partition[0], groups.values(Partition[]::new));
		
		Assert.assertEquals(0, groups.mapEach(x -> x).size());
		Assert.assertEquals(0, groups.measureEach(Measures.count()).size());
		Assert.assertEquals(0, groups.groupEachBy(x -> x).size());
		
		Assert.assertEquals(0, groups.reduceEach((x, y) -> x + y).size());
		Assert.assertEquals(0, groups.reduceEach(0, (x, y) -> x + y).size());
	}
	
	@Test
	public void test() {
		Partition<String, Integer> partition = Partition.zip(List.of("foo", "bar"), List.of(1, 2));
		Groups<String, String, Integer> groups = partition.groupBy(x -> x.toUpperCase());
		
		Assert.assertEquals(2, groups.size());
		Assert.assertEquals(List.of("BAR", "FOO"), groups.sorted().keys());
		Assert.assertArrayEquals(new String[] { "BAR", "FOO" }, groups.sorted().keys(String[]::new));
		
		Assert.assertEquals(2, groups.mapEach(x -> x).size());
		Assert.assertEquals(List.of(1, 1), groups.measureEach(Measures.count()).values());
		Assert.assertEquals(2, groups.groupEachBy(x -> x).size());
		
		Assert.assertEquals(List.of(2, 1), groups.reduceEach((x, y) -> x + y).sorted().values());
		Assert.assertEquals(List.of(2, 1), groups.reduceEach(0, (x, y) -> x + y).sorted().values());
	}

}
