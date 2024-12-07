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

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.sample.Sample;

public class GroupingsTest {
	
	@Test
	public void testIntegerBucket() {
		Parameter<Integer> parameter = Parameter.named("test").asInt().sampledBetween(0, 100);
		Sample sample = new Sample();
		
		Function<Sample, Integer> bucket = Groupings.bucket(parameter, 10);
		
		sample.setInt("test", 0);
		Assert.assertEquals(5, bucket.apply(sample));
		
		sample.setInt("test", 5);
		Assert.assertEquals(5, bucket.apply(sample));
		
		sample.setInt("test", 9);
		Assert.assertEquals(5, bucket.apply(sample));
		
		sample.setInt("test", 10);
		Assert.assertEquals(15, bucket.apply(sample));
		
		sample.setInt("test", 15);
		Assert.assertEquals(15, bucket.apply(sample));
		
		sample.setInt("test", 19);
		Assert.assertEquals(15, bucket.apply(sample));
		
		sample.throwIfUnaccessedProperties();
	}
	
	@Test
	public void testLongBucket() {
		Parameter<Long> parameter = Parameter.named("test").asLong().sampledBetween(0L, 100L);
		Sample sample = new Sample();
		
		Function<Sample, Long> bucket = Groupings.bucket(parameter, 10L);
		
		sample.setLong("test", 0L);
		Assert.assertEquals(5L, bucket.apply(sample));
		
		sample.setLong("test", 5L);
		Assert.assertEquals(5L, bucket.apply(sample));
		
		sample.setLong("test", 9L);
		Assert.assertEquals(5L, bucket.apply(sample));
		
		sample.setLong("test", 10L);
		Assert.assertEquals(15L, bucket.apply(sample));
		
		sample.setLong("test", 15L);
		Assert.assertEquals(15L, bucket.apply(sample));
		
		sample.setLong("test", 19L);
		Assert.assertEquals(15L, bucket.apply(sample));
		
		sample.throwIfUnaccessedProperties();
	}
	
	@Test
	public void testDoubleBucket() {
		Parameter<Double> parameter = Parameter.named("test").asDecimal().sampledBetween(0.0, 100.0);
		Sample sample = new Sample();
		
		Function<Sample, Double> bucket = Groupings.bucket(parameter, 10.0);
		
		sample.setDouble("test", 0.0);
		Assert.assertEquals(5.0, bucket.apply(sample));
		
		sample.setDouble("test", 5.0);
		Assert.assertEquals(5.0, bucket.apply(sample));
		
		sample.setDouble("test", 9.9);
		Assert.assertEquals(5.0, bucket.apply(sample));
		
		sample.setDouble("test", 10.0);
		Assert.assertEquals(15.0, bucket.apply(sample));
		
		sample.setDouble("test", 15.0);
		Assert.assertEquals(15.0, bucket.apply(sample));
		
		sample.setDouble("test", 19.9);
		Assert.assertEquals(15.0, bucket.apply(sample));
		
		sample.throwIfUnaccessedProperties();
	}
	
	@Test
	public void testRound() {
		Parameter<Double> parameter = Parameter.named("test").asDecimal().sampledBetween(0.0, 100.0);
		Sample sample = new Sample();
		
		Function<Sample, Integer> bucket = Groupings.round(parameter);
		
		sample.setDouble("test", 0.0);
		Assert.assertEquals(0, bucket.apply(sample));
		
		sample.setDouble("test", 0.5);
		Assert.assertEquals(1, bucket.apply(sample));
		
		sample.setDouble("test", 1.0);
		Assert.assertEquals(1, bucket.apply(sample));
		
		sample.setDouble("test", 1.5);
		Assert.assertEquals(2, bucket.apply(sample));
		
		sample.throwIfUnaccessedProperties();
	}
	
	@Test
	public void testExactValue() {
		Parameter<Double> parameter = Parameter.named("test").asDecimal().sampledBetween(0.0, 100.0);
		Sample sample = new Sample();
		
		Function<Sample, Double> bucket = Groupings.exactValue(parameter);
		
		sample.setDouble("test", 0.0);
		Assert.assertEquals(0.0, bucket.apply(sample));
		
		sample.setDouble("test", 0.5);
		Assert.assertEquals(0.5, bucket.apply(sample));
		
		sample.setDouble("test", 1.0);
		Assert.assertEquals(1.0, bucket.apply(sample));
		
		sample.throwIfUnaccessedProperties();
	}
	
	@Test
	public void testPair() {
		Function<Double, Pair<Integer, Double>> bucket = Groupings.pair(Groupings.round(), Groupings.exactValue());
		
		Assert.assertEquals(Pair.of(0, 0.0), bucket.apply(0.0));
		Assert.assertEquals(Pair.of(1, 0.5), bucket.apply(0.5));
		Assert.assertEquals(Pair.of(1, 1.0), bucket.apply(1.0));
	}

}
