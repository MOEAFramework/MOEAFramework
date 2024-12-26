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
package org.moeaframework.analysis.sample;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.PropertyNotFoundException;

public class SampleTest {

	@Test
	public void testCopy() {
		Sample original = new Sample();
		original.setInt("foo", 5);
		
		Sample copy = original.copy();
		Assert.assertEquals(5, copy.getInt("foo"));
		
		copy.setInt("foo", 10);
		Assert.assertEquals(5, original.getInt("foo"));
		Assert.assertEquals(10, copy.getInt("foo"));
	}
	
	@Test
	public void testGet() {
		Sample sample = new Sample();
		sample.setInt("foo", 5);
		
		Parameter<Integer> fooParameter = Parameter.named("foo").asInt().range(0, 10);
		Assert.assertEquals(5, sample.get(fooParameter));
	}
	
	@Test(expected = PropertyNotFoundException.class)
	public void testGetMissing() {
		Sample sample = new Sample();
		
		Parameter<Integer> fooParameter = Parameter.named("foo").asInt().range(0, 10);
		Assert.assertEquals(5, sample.get(fooParameter));
	}
	
	@Test
	public void testReference() {
		Sample sample = new Sample();
		sample.setInt("foo", 5);
		sample.setString("bar", "baz");
		
		Reference reference = sample.getReference();
		Assert.assertArrayEquals(new String[] { "bar", "foo" }, reference.fields().toArray(String[]::new));
		Assert.assertEquals("5", reference.get("foo"));
		Assert.assertEquals("baz", reference.get("bar"));
	}

}
