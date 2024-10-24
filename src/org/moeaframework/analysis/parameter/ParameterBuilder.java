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
package org.moeaframework.analysis.parameter;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.moeaframework.core.PRNG;

public class ParameterBuilder {
	
	private final String name;
	
	public ParameterBuilder(String name) {
		super();
		this.name = name;
	}
	
	public IntegerBuilder asInt() {
		return new IntegerBuilder(name);
	}
	
	public LongBuilder asLong() {
		return new LongBuilder(name);
	}
	
	public DecimalBuilder asDecimal() {
		return new DecimalBuilder(name);
	}
	
	public StringBuilder asString() {
		return new StringBuilder(name);
	}
	
	public Constant<Integer> asConstant(int value) {
		return new Constant<>(name, value);
	}
	
	public Constant<Long> asConstant(long value) {
		return new Constant<>(name, value);
	}
	
	public Constant<Double> asConstant(double value) {
		return new Constant<>(name, value);
	}
	
	public Constant<String> asConstant(String value) {
		return new Constant<>(name, value);
	}
	
	public Enumeration<Integer> withValues(int... values) {
		return new Enumeration<>(name, IntStream.of(values).boxed().toList());
	}
	
	public Enumeration<Long> withValues(long... values) {
		return new Enumeration<>(name, LongStream.of(values).boxed().toList());
	}
	
	public Enumeration<Double> withValues(double... values) {
		return new Enumeration<>(name, DoubleStream.of(values).boxed().toList());
	}
	
	public Enumeration<String> withValues(String... values) {
		return new Enumeration<>(name, values);
	}
	
	static abstract class TypedParameterBuilder {
		
		protected final String name;
		
		public TypedParameterBuilder(String name) {
			super();
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
	
	public static class IntegerBuilder extends TypedParameterBuilder {
		
		public IntegerBuilder(String name) {
			super(name);
		}
		
		public Constant<Integer> constant(int value) {
			return new Constant<Integer>(name, value);
		}
		
		public Enumeration<Integer> withValues(int... values) {
			return of(IntStream.of(values));
		}
		
		public Enumeration<Integer> range(int startInclusive, int endExclusive) {
			return of(IntStream.range(startInclusive, endExclusive));
		}
		
		public Enumeration<Integer> range(int startInclusive, int endExclusive, int stepSize) {
			return of(IntStream.iterate(startInclusive, i -> i < endExclusive, i -> i + stepSize));
		}
		
		public Enumeration<Integer> rangeClosed(int startInclusive, int endInclusive) {
			return of(IntStream.rangeClosed(startInclusive, endInclusive));
		}
		
		public Enumeration<Integer> rangeClosed(int startInclusive, int endInclusive, int stepSize) {
			return of(IntStream.iterate(startInclusive, i -> i <= endInclusive, i -> i + stepSize));
		}
		
		public Enumeration<Integer> random(int count) {
			return of(IntStream.range(0, count).map(i -> PRNG.nextInt()));
		}
		
		public Enumeration<Integer> random(int startInclusive, int endInclusive, int count) {
			return of(IntStream.range(0, count).map(i -> PRNG.nextInt(startInclusive, endInclusive)));
		}
		
		public Enumeration<Integer> of(IntStream stream) {
			return of(stream.boxed());
		}
		
		Enumeration<Integer> of(Stream<Integer> stream) {
			return new Enumeration<Integer>(name, stream.toList());
		}
		
		public IntegerRange sampledBetween(int lowerBound, int upperBound) {
			return new IntegerRange(name, lowerBound, upperBound);
		}
		
	}
	
	public static class LongBuilder extends TypedParameterBuilder {
		
		public LongBuilder(String name) {
			super(name);
		}
		
		public Constant<Long> constant(long value) {
			return new Constant<Long>(name, value);
		}
		
		public Enumeration<Long> withValues(long... values) {
			return of(LongStream.of(values));
		}
		
		public Enumeration<Long> range(long startInclusive, long endExclusive) {
			return of(LongStream.range(startInclusive, endExclusive));
		}
		
		public Enumeration<Long> range(long startInclusive, long endExclusive, long stepSize) {
			return of(LongStream.iterate(startInclusive, i -> i < endExclusive, i -> i + stepSize));
		}
		
		public Enumeration<Long> rangeClosed(long startInclusive, long endInclusive) {
			return of(LongStream.rangeClosed(startInclusive, endInclusive));
		}
		
		public Enumeration<Long> rangeClosed(long startInclusive, long endInclusive, long stepSize) {
			return of(LongStream.iterate(startInclusive, i -> i <= endInclusive, i -> i + stepSize));
		}
		
		public Enumeration<Long> random(int count) {
			return of(LongStream.range(0, count).map(i -> PRNG.getRandom().nextLong()));
		}
		
		public Enumeration<Long> random(long startInclusive, long endInclusive, int count) {
			return of(LongStream.range(0, count).map(i -> PRNG.getRandom().nextLong(startInclusive, endInclusive)));
		}
		
		public Enumeration<Long> of(LongStream stream) {
			return of(stream.boxed());
		}
		
		Enumeration<Long> of(Stream<Long> stream) {
			return new Enumeration<Long>(name, stream.toList());
		}
		
		public LongRange sampledBetween(long lowerBound, long upperBound) {
			return new LongRange(name, lowerBound, upperBound);
		}
		
	}
	
	public static class DecimalBuilder extends TypedParameterBuilder {
		
		public DecimalBuilder(String name) {
			super(name);
		}
		
		public Constant<Double> constant(double value) {
			return new Constant<Double>(name, value);
		}
		
		public Enumeration<Double> withValues(double... values) {
			return of(DoubleStream.of(values));
		}
		
		public Enumeration<Double> range(double startInclusive, double endExclusive, double stepSize) {
			return of(DoubleStream.iterate(startInclusive, i -> i < endExclusive, i -> i + stepSize));
		}
		
		public Enumeration<Double> rangeClosed(double startInclusive, double endInclusive, double stepSize) {
			return of(DoubleStream.iterate(startInclusive, i -> i <= endInclusive, i -> i + stepSize));
		}
		
		public Enumeration<Double> random(int count) {
			return of(IntStream.range(0, count).mapToDouble(i -> PRNG.nextDouble()));
		}
		
		public Enumeration<Double> random(double startInclusive, double endInclusive, int count) {
			return of(IntStream.range(0, count).mapToDouble(i -> PRNG.nextDouble(startInclusive, endInclusive)));
		}
		
		public Enumeration<Double> of(DoubleStream stream) {
			return of(stream.map(x -> DecimalRange.applyPrecision(x)).boxed());
		}
		
		Enumeration<Double> of(Stream<Double> stream) {
			return new Enumeration<Double>(name, stream.toList());
		}
		
		public DecimalRange sampledBetween(double lowerBound, double upperBound) {
			return new DecimalRange(name, lowerBound, upperBound);
		}
		
	}
	
	public static class StringBuilder extends TypedParameterBuilder {
		
		public StringBuilder(String name) {
			super(name);
		}
		
		public Constant<String> constant(String value) {
			return new Constant<String>(name, value);
		}
		
		public Enumeration<String> withValues(String... values) {
			return of(Stream.of(values));
		}
		
		public Enumeration<String> withValues(List<String> values) {
			return of(values.stream());
		}
		
		public Enumeration<String> of(Stream<String> stream) {
			return new Enumeration<String>(name, stream.toList());
		}
		
	}

}
