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
package org.moeaframework.analysis.stream;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.junit.Test;
import org.moeaframework.Assert;

public class MeasuresTest {
	
	@Test
	public void testEmpty() {
		Function<Stream<Integer>, Integer> count = Measures.count();
		Assert.assertEquals(0, count.apply(IntStream.of().boxed()));

		Function<Stream<Integer>, Double> sum = Measures.sum();
		Assert.assertEquals(0, sum.apply(IntStream.of().boxed()));
		
		Function<Stream<Integer>, Integer> min = Measures.min();
		Assert.assertThrows(NoSuchElementException.class, () -> min.apply(IntStream.of().boxed()));
		
		Function<Stream<Integer>, Integer> max = Measures.max();
		Assert.assertThrows(NoSuchElementException.class, () -> max.apply(IntStream.of().boxed()));
		
		Function<Stream<Integer>, Double> average = Measures.average();
		Assert.assertEquals(Double.NaN, average.apply(IntStream.of().boxed()));
		
		Function<Stream<Integer>, Double> median = Measures.median();
		Assert.assertEquals(Double.NaN, median.apply(IntStream.of().boxed()));
		
		Function<Stream<Integer>, Double> percentile = Measures.percentile(50);
		Assert.assertEquals(Double.NaN, percentile.apply(IntStream.of().boxed()));
		
		Function<Stream<Integer>, StatisticalSummary> stats = Measures.stats();
		Assert.assertEquals(0, stats.apply(IntStream.of().boxed()).getN());
	}
	
	@Test
	public void test() {
		Function<Stream<Integer>, Integer> count = Measures.count();
		Assert.assertEquals(2, count.apply(IntStream.of(1, 2).boxed()));

		Function<Stream<Integer>, Double> sum = Measures.sum();
		Assert.assertEquals(3.0, sum.apply(IntStream.of(1, 2).boxed()));
		
		Function<Stream<Integer>, Integer> min = Measures.min();
		Assert.assertEquals(1, min.apply(IntStream.of(1, 2).boxed()));
		
		Function<Stream<Integer>, Integer> max = Measures.max();
		Assert.assertEquals(2, max.apply(IntStream.of(1, 2).boxed()));
		
		Function<Stream<Integer>, Double> average = Measures.average();
		Assert.assertEquals(1.5, average.apply(IntStream.of(1, 2).boxed()));
		
		Function<Stream<Integer>, Double> median = Measures.median();
		Assert.assertEquals(2.0, median.apply(IntStream.of(1, 2, 3).boxed()));
		
		Function<Stream<Integer>, Double> percentile = Measures.percentile(50);
		Assert.assertEquals(2.0, percentile.apply(IntStream.of(1, 2, 3).boxed()));
		
		Function<Stream<Integer>, StatisticalSummary> stats = Measures.stats();
		Assert.assertEquals(2, stats.apply(IntStream.of(1, 2).boxed()).getN());
	}

}
