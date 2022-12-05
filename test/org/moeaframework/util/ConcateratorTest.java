/* Copyright 2009-2022 David Hadka
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link Concaterator} class.
 */
public class ConcateratorTest {
	
	@Test
	public void testEmptyIterator() {
		Concaterator<String> c = new Concaterator<String>(iteratorOf());
		Assert.assertFalse(c.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> c.next());
	}
	
	@Test
	public void testSingleIterator() {
		Concaterator<String> c = new Concaterator<String>(iteratorOf("foo"));
		
		Assert.assertTrue(c.hasNext());
		Assert.assertEquals("foo", c.next());
		
		Assert.assertFalse(c.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> c.next());
	}
	
	@Test
	public void testMultipleIterators() {
		Concaterator<String> c = new Concaterator<String>(iteratorOf("foo"), iteratorOf("bar"));
		
		Assert.assertTrue(c.hasNext());
		Assert.assertEquals("foo", c.next());
		
		Assert.assertTrue(c.hasNext());
		Assert.assertEquals("bar", c.next());
		
		Assert.assertFalse(c.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> c.next());
	}
	
	@Test
	public void testMultipleIteratorsWithEmpty() {
		Concaterator<String> c = new Concaterator<String>(iteratorOf("foo"), iteratorOf(), iteratorOf("bar"));
		
		Assert.assertTrue(c.hasNext());
		Assert.assertEquals("foo", c.next());
		
		Assert.assertTrue(c.hasNext());
		Assert.assertEquals("bar", c.next());
		
		Assert.assertFalse(c.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> c.next());
	}
	
	@SafeVarargs
	private final <T> Iterator<T> iteratorOf(T... elements) {
		return Arrays.asList(elements).iterator();
	}
	
}
