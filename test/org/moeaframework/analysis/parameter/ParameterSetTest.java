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
package org.moeaframework.analysis.parameter;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.util.sequence.Sequence;

public class ParameterSetTest {
	
	@Test
	public void testSample() {
		ParameterSet parameterSet = new ParameterSet();
		parameterSet.add(new SampledInteger("int", 0, 10));
		parameterSet.add(new Enumeration<>("string", "foo", "bar"));
		parameterSet.add(new Constant<>("double", 0.5));
		
		Samples samples = parameterSet.sample(2, new TestSequenceGenerator());
		
		Assert.assertEquals(2, samples.size());
		Assert.assertEquals(0, samples.get(0).getInt("int"));
		Assert.assertEquals("foo", samples.get(0).getString("string"));
		Assert.assertEquals(0.5, samples.get(0).getDouble("double"));
		Assert.assertEquals(10, samples.get(1).getInt("int"));
		Assert.assertEquals("bar", samples.get(1).getString("string"));
		Assert.assertEquals(0.5, samples.get(1).getDouble("double"));
	}
	
	@Test
	public void testEnumerate() {
		ParameterSet parameterSet = new ParameterSet();
		parameterSet.add(new Enumeration<>("int", 0, 1));
		parameterSet.add(new Enumeration<>("string", "foo", "bar"));
		parameterSet.add(new Constant<>("double", 0.5));
		
		Samples samples = parameterSet.enumerate();
		
		Assert.assertEquals(4, samples.size());
		Assert.assertEquals(0, samples.get(0).getInt("int"));
		Assert.assertEquals("foo", samples.get(0).getString("string"));
		Assert.assertEquals(0.5, samples.get(0).getDouble("double"));
		Assert.assertEquals(0, samples.get(1).getInt("int"));
		Assert.assertEquals("bar", samples.get(1).getString("string"));
		Assert.assertEquals(0.5, samples.get(1).getDouble("double"));
		Assert.assertEquals(1, samples.get(2).getInt("int"));
		Assert.assertEquals("foo", samples.get(2).getString("string"));
		Assert.assertEquals(0.5, samples.get(2).getDouble("double"));
		Assert.assertEquals(1, samples.get(3).getInt("int"));
		Assert.assertEquals("bar", samples.get(3).getString("string"));
		Assert.assertEquals(0.5, samples.get(3).getDouble("double"));
	}
	
	@Test
	public void testIsEnumerable() {
		ParameterSet parameterSet = new ParameterSet();
		parameterSet.add(new Enumeration<>("string", "foo", "bar"));
		parameterSet.add(new Constant<>("double", 0.5));
		
		Assert.assertTrue(parameterSet.isEnumerable());
		
		parameterSet.add(new SampledInteger("int", 0, 10));
		
		Assert.assertFalse(parameterSet.isEnumerable());
	}
	
	@Test
	public void testGet() {
		ParameterSet parameterSet = new ParameterSet();
		parameterSet.add(new Enumeration<>("int", 0, 1));
		parameterSet.add(new Enumeration<>("string", "foo", "bar"));
		parameterSet.add(new Constant<>("double", 0.5));
		
		Assert.assertEquals("string", parameterSet.get(1).getName());
		Assert.assertEquals("string", parameterSet.get("string").getName());
		Assert.assertThrows(IndexOutOfBoundsException.class, () -> parameterSet.get(3));
		Assert.assertThrows(NoSuchParameterException.class, () -> parameterSet.get("missing"));
	}
	
	@Test
	public void testIndexOf() {
		ParameterSet parameterSet = new ParameterSet();
		parameterSet.add(new Enumeration<>("int", 0, 1));
		parameterSet.add(new Enumeration<>("string", "foo", "bar"));
		parameterSet.add(new Constant<>("double", 0.5));
		
		Assert.assertEquals(0, parameterSet.indexOf(parameterSet.get(0)));
		Assert.assertEquals(1, parameterSet.indexOf(parameterSet.get(1)));
		Assert.assertEquals(2, parameterSet.indexOf(parameterSet.get(2)));
		
		Assert.assertEquals(0, parameterSet.indexOf("int"));
		Assert.assertEquals(1, parameterSet.indexOf("string"));
		Assert.assertEquals(2, parameterSet.indexOf("double"));
	}
	
	private static class TestSequenceGenerator implements Sequence {

		@Override
		public double[][] generate(int N, int D) {
			Assert.assertEquals(2, N);
			Assert.assertEquals(2, D); // Constants are not sampled!
			
			return new double[][] {
				{ 0.0, 0.0 },
				{ 1.0, 1.0 }
			};
		}
		
	}

}
