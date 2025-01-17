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

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;

public class ParameterBuilderTest {
	
	@Test
	public void testInteger() {
		Constant<Integer> constant = Parameter.named("foo").asInt().constant(5);
		Assert.assertEquals("foo", constant.getName());
		Assert.assertEquals(5, constant.getValue());
		
		Enumeration<Integer> rangeInclusive = Parameter.named("foo").asInt().range(5, 10);
		Assert.assertEquals("foo", rangeInclusive.getName());
		Assert.assertEquals(List.of(5, 6, 7, 8, 9, 10), rangeInclusive.values());
		
		Enumeration<Integer> rangeInclusiveWithStep = Parameter.named("foo").asInt().range(5, 10, 2);
		Assert.assertEquals("foo", rangeInclusiveWithStep.getName());
		Assert.assertEquals(List.of(5, 7, 9), rangeInclusiveWithStep.values());
		
		Enumeration<Integer> rangeExclusive = Parameter.named("foo").asInt().rangeExclusive(5, 10);
		Assert.assertEquals("foo", rangeExclusive.getName());
		Assert.assertEquals(List.of(5, 6, 7, 8, 9), rangeExclusive.values());
		
		Enumeration<Integer> rangeExclusiveWithStep = Parameter.named("foo").asInt().rangeExclusive(5, 10, 2);
		Assert.assertEquals("foo", rangeExclusiveWithStep.getName());
		Assert.assertEquals(List.of(5, 7, 9), rangeExclusiveWithStep.values());
		
		Enumeration<Integer> values = Parameter.named("foo").asInt().withValues(5, 10);
		Assert.assertEquals("foo", values.getName());
		Assert.assertEquals(List.of(5, 10), values.values());
		
		Enumeration<Integer> random = Parameter.named("foo").asInt().random(5, 10, 2);
		Assert.assertEquals("foo", random.getName());
		Assert.assertEquals(2, random.size());
		
		IntegerRange sampled = Parameter.named("foo").asInt().sampledBetween(5, 10);
		Assert.assertEquals("foo", sampled.getName());
		Assert.assertEquals(5, sampled.getLowerBound());
		Assert.assertEquals(10, sampled.getUpperBound());
	}
	
	@Test
	public void testLong() {
		Constant<Long> constant = Parameter.named("foo").asLong().constant(5L);
		Assert.assertEquals("foo", constant.getName());
		Assert.assertEquals(5, constant.getValue());
		
		Enumeration<Long> rangeInclusive = Parameter.named("foo").asLong().range(5L, 10L);
		Assert.assertEquals("foo", rangeInclusive.getName());
		Assert.assertEquals(List.of(5L, 6L, 7L, 8L, 9L, 10L), rangeInclusive.values());
		
		Enumeration<Long> rangeInclusiveWithStep = Parameter.named("foo").asLong().range(5L, 10L, 2L);
		Assert.assertEquals("foo", rangeInclusiveWithStep.getName());
		Assert.assertEquals(List.of(5L, 7L, 9L), rangeInclusiveWithStep.values());
		
		Enumeration<Long> rangeExclusive = Parameter.named("foo").asLong().rangeExclusive(5L, 10L);
		Assert.assertEquals("foo", rangeExclusive.getName());
		Assert.assertEquals(List.of(5L, 6L, 7L, 8L, 9L), rangeExclusive.values());
		
		Enumeration<Long> rangeExclusiveWithStep = Parameter.named("foo").asLong().rangeExclusive(5L, 10L, 2L);
		Assert.assertEquals("foo", rangeExclusiveWithStep.getName());
		Assert.assertEquals(List.of(5L, 7L, 9L), rangeExclusiveWithStep.values());
		
		Enumeration<Long> values = Parameter.named("foo").asLong().withValues(5L, 10L);
		Assert.assertEquals("foo", values.getName());
		Assert.assertEquals(List.of(5L, 10L), values.values());
		
		Enumeration<Long> random = Parameter.named("foo").asLong().random(5L, 10L, 2);
		Assert.assertEquals("foo", random.getName());
		Assert.assertEquals(2, random.size());
		
		LongRange sampled = Parameter.named("foo").asLong().sampledBetween(5L, 10L);
		Assert.assertEquals("foo", sampled.getName());
		Assert.assertEquals(5L, sampled.getLowerBound());
		Assert.assertEquals(10L, sampled.getUpperBound());
	}
	
	@Test
	public void testDecimal() {
		Constant<Double> constant = Parameter.named("foo").asDecimal().constant(5.0);
		Assert.assertEquals("foo", constant.getName());
		Assert.assertEquals(5.0, constant.getValue());
		
		Enumeration<Double> rangeInclusive = Parameter.named("foo").asDecimal().range(5.0, 10.0, 1.0);
		Assert.assertEquals("foo", rangeInclusive.getName());
		Assert.assertEquals(List.of(5.0, 6.0, 7.0, 8.0, 9.0, 10.0), rangeInclusive.values());
		
		Enumeration<Double> rangeExclusive = Parameter.named("foo").asDecimal().rangeExclusive(5.0, 10.0, 1.0);
		Assert.assertEquals("foo", rangeExclusive.getName());
		Assert.assertEquals(List.of(5.0, 6.0, 7.0, 8.0, 9.0), rangeExclusive.values());

		Enumeration<Double> values = Parameter.named("foo").asDecimal().withValues(5.0, 10.0);
		Assert.assertEquals("foo", values.getName());
		Assert.assertEquals(List.of(5.0, 10.0), values.values());
		
		Enumeration<Double> random = Parameter.named("foo").asDecimal().random(5.0, 10.0, 2);
		Assert.assertEquals("foo", random.getName());
		Assert.assertEquals(2.0, random.size());
		
		DecimalRange sampled = Parameter.named("foo").asDecimal().sampledBetween(5.0, 10.0);
		Assert.assertEquals("foo", sampled.getName());
		Assert.assertEquals(5.0, sampled.getLowerBound());
		Assert.assertEquals(10.0, sampled.getUpperBound());
	}
	
	@Test
	public void testString() {
		Constant<String> constant = Parameter.named("foo").asString().constant("val");
		Assert.assertEquals("foo", constant.getName());
		Assert.assertEquals("val", constant.getValue());
		
		Enumeration<String> values = Parameter.named("foo").asString().withValues("val1", "val2");
		Assert.assertEquals("foo", values.getName());
		Assert.assertEquals(List.of("val1", "val2"), values.values());
	}

}
