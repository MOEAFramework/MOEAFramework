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
package org.moeaframework.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.moeaframework.Assert;

public class FixedOrderComparatorTest {
	
	@Test
	public void test() {
		Map<String, Integer> order = new HashMap<>();
		order.put("foo", 0);
		order.put("bar", 1);
		
		FixedOrderComparator<String> comparator = new FixedOrderComparator<>(order);
		
		Assert.assertEquals(0, comparator.compare("foo", "foo"));
		Assert.assertEquals(-1, comparator.compare("foo", "bar"));
		Assert.assertEquals(1, comparator.compare("bar", "foo"));
		Assert.assertEquals(-1, comparator.compare("foo", "baz"));
		Assert.assertEquals(1, comparator.compare("baz", "foo"));
		Assert.assertEquals(0, comparator.compare("baz", "baz"));
		Assert.assertEquals(0, comparator.compare("aaa", "bbb"));
		Assert.assertEquals(0, comparator.compare("bbb", "aaa"));
	}
	
}
