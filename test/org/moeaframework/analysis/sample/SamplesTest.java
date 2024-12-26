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

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;

public class SamplesTest {

	@Test
	public void testEmpty() {
		Samples samples = new Samples(null);
		
		Assert.assertTrue(samples.isEmpty());
		Assert.assertEquals(0, samples.size());
		
		SampledResults<Integer> results = samples.evaluateAll(s -> 5);
		Assert.assertEquals(0, results.size());
	}
	
	@Test
	public void test() {
		Samples samples = new Samples(null);
		
		Sample sample = new Sample();
		samples.add(sample);
		
		Assert.assertFalse(samples.isEmpty());
		Assert.assertEquals(1, samples.size());
		Assert.assertSame(sample, samples.get(0));
		
		SampledResults<Integer> results = samples.evaluateAll(s -> 5);
		Assert.assertEquals(1, results.size());
		Assert.assertSame(sample, results.first().getKey());
		Assert.assertEquals(5, results.first().getValue());
	}
	
	@Test
	public void testEquals() {
		Samples samples1 = new Samples(null);
		Samples samples2 = new Samples(null);
		Samples samples3 = new Samples(null);
		
		Sample sample = new Sample();
		sample.setInt("foo", 5);
		samples3.add(sample);
		
		Assert.assertFalse(samples1.equals(null));
		
		Assert.assertTrue(samples1.equals(samples2));
		Assert.assertTrue(samples2.equals(samples1));
		Assert.assertEquals(samples1.hashCode(), samples2.hashCode());
		
		Assert.assertFalse(samples1.equals(samples3));
		Assert.assertFalse(samples3.equals(samples1));
		Assert.assertNotEquals(samples1.hashCode(), samples3.hashCode());
		
		samples2.add(sample.copy());
		Assert.assertTrue(samples2.equals(samples3));
		Assert.assertTrue(samples3.equals(samples2));
		Assert.assertEquals(samples2.hashCode(), samples3.hashCode());
	}
	
	@Test
	public void testSaveLoad() throws IOException {
		ParameterSet parameterSet = new ParameterSet(
				Parameter.named("foo").asInt().range(0, 10));
		
		Samples samples = new Samples(parameterSet);
		
		Sample sample1 = new Sample();
		sample1.setInt("foo", 10);
		samples.add(sample1);
		
		Sample sample2 = new Sample();
		sample2.setInt("foo", 5);
		samples.add(sample2);
		
		File tempFile = TempFiles.createFile();
		samples.save(tempFile);
		
		Samples copy = Samples.load(tempFile, parameterSet);
		Assert.assertEquals(2, copy.size());
		Assert.assertEquals(10, copy.get(0).getInt("foo"));
		Assert.assertEquals(5, copy.get(1).getInt("foo"));
	}

}
