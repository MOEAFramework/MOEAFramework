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

import org.junit.Test;
import org.moeaframework.Assert;

public class GroupingsTest {
	
	@Test
	public void testIntegerBucket() {
		Function<Integer, Integer> bucket = Groupings.bucket(10);
		Assert.assertEquals(5, bucket.apply(0));
		Assert.assertEquals(5, bucket.apply(5));
		Assert.assertEquals(5, bucket.apply(9));
		Assert.assertEquals(15, bucket.apply(10));
		Assert.assertEquals(15, bucket.apply(15));
		Assert.assertEquals(15, bucket.apply(19));
	}
	
	@Test
	public void testLongBucket() {
		Function<Long, Long> bucket = Groupings.bucket(10L);
		Assert.assertEquals(5, bucket.apply(0L));
		Assert.assertEquals(5, bucket.apply(5L));
		Assert.assertEquals(5, bucket.apply(9L));
		Assert.assertEquals(15, bucket.apply(10L));
		Assert.assertEquals(15, bucket.apply(15L));
		Assert.assertEquals(15, bucket.apply(19L));
	}
	
	@Test
	public void testDoubleBucket() {
		Function<Double, Double> bucket = Groupings.bucket(10.0);
		Assert.assertEquals(5.0, bucket.apply(0.0));
		Assert.assertEquals(5.0, bucket.apply(5.0));
		Assert.assertEquals(5.0, bucket.apply(9.9));
		Assert.assertEquals(15.0, bucket.apply(10.0));
		Assert.assertEquals(15.0, bucket.apply(15.0));
		Assert.assertEquals(15.0, bucket.apply(19.9));
	}
	
	@Test
	public void testRound() {
		Function<Double, Integer> round = Groupings.round();
		Assert.assertEquals(0, round.apply(0.0));
		Assert.assertEquals(1, round.apply(0.5));
		Assert.assertEquals(1, round.apply(1.0));
		Assert.assertEquals(2, round.apply(1.5));
	}

}
