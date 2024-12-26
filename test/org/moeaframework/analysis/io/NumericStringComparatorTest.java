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
package org.moeaframework.analysis.io;

import org.junit.Test;
import org.moeaframework.Assert;

public class NumericStringComparatorTest {
	
	@Test
	public void test() {
		NumericStringComparator comparator = new NumericStringComparator();
		
		Assert.assertEquals(0, comparator.compare("", ""));
		
		Assert.assertEquals(0, comparator.compare("a", "a"));
		Assert.assertEquals(-1, comparator.compare("a", "b"));
		Assert.assertEquals(1, comparator.compare("b", "a"));
		
		Assert.assertEquals(0, comparator.compare("1", "1"));
		Assert.assertEquals(-1, comparator.compare("1", "2"));
		Assert.assertEquals(1, comparator.compare("2", "1"));
		
		Assert.assertEquals(0, comparator.compare("a1", "a1"));
		Assert.assertEquals(-1, comparator.compare("a1", "a2"));
		Assert.assertEquals(1, comparator.compare("a2", "a1"));
		
		Assert.assertEquals(-1, comparator.compare("a1", "b1"));
		Assert.assertEquals(1, comparator.compare("b1", "a1"));
		
		Assert.assertEquals(0, comparator.compare("1a", "1a"));
		Assert.assertEquals(-1, comparator.compare("1a", "1b"));
		Assert.assertEquals(1, comparator.compare("1b", "1a"));
		
		Assert.assertEquals(0, comparator.compare("a1a", "a1a"));
		Assert.assertEquals(-1, comparator.compare("a1a", "a1b"));
		Assert.assertEquals(1, comparator.compare("a1b", "a1a"));
		
		Assert.assertEquals(-1, comparator.compare("a", "a1"));
		Assert.assertEquals(1, comparator.compare("a1", "a"));
		Assert.assertEquals(-1, comparator.compare("1", "1a"));
		Assert.assertEquals(1, comparator.compare("1a", "1"));
		Assert.assertEquals(-1, comparator.compare("a1", "a1a"));
		Assert.assertEquals(1, comparator.compare("a1a", "a1"));
		
		Assert.assertEquals(0, comparator.compare("a1a1", "a1a1"));
		Assert.assertEquals(-1, comparator.compare("a1a1", "a1a2"));
		Assert.assertEquals(1, comparator.compare("a1a2", "a1a1"));
	}
	
}
