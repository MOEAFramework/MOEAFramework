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

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.moeaframework.core.PRNG;

/**
 * Builder interface for constructing parameters.
 */
public class ParameterBuilder {
	
	private final String name;
	
	/**
	 * Constructs an untyped builder for a parameter with the given name.
	 * 
	 * @param name the parameter name
	 */
	ParameterBuilder(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Returns a parameter builder for integer values.
	 * 
	 * @return the typed parameter builder
	 */
	public IntegerBuilder asInt() {
		return new IntegerBuilder(name);
	}
	
	/**
	 * Returns a parameter builder for long values.
	 * 
	 * @return the typed parameter builder
	 */
	public LongBuilder asLong() {
		return new LongBuilder(name);
	}
	
	/**
	 * Returns a parameter builder for decimal or floating-point values.
	 * 
	 * @return the typed parameter builder
	 */
	public DecimalBuilder asDecimal() {
		return new DecimalBuilder(name);
	}
	
	/**
	 * Returns a parameter builder for strings.
	 * 
	 * @return the typed parameter builder
	 */
	public StringBuilder asString() {
		return new StringBuilder(name);
	}
	
	/**
	 * Shorthand for calling {@link IntegerBuilder#constant(int)}.
	 * 
	 * @param value the constant value
	 * @return the constant parameter
	 */
	public Constant<Integer> asConstant(int value) {
		return asInt().constant(value);
	}
	
	/**
	 * Shorthand for calling {@link LongBuilder#constant(long)}.
	 * 
	 * @param value the constant value
	 * @return the constant parameter
	 */
	public Constant<Long> asConstant(long value) {
		return asLong().constant(value);
	}
	
	/**
	 * Shorthand for calling {@link DecimalBuilder#constant(double)}.
	 * 
	 * @param value the constant value
	 * @return the constant parameter
	 */
	public Constant<Double> asConstant(double value) {
		return asDecimal().constant(value);
	}
	
	/**
	 * Shorthand for calling {@link StringBuilder#constant(String)}.
	 * 
	 * @param value the constant value
	 * @return the constant parameter
	 */
	public Constant<String> asConstant(String value) {
		return asString().constant(value);
	}
	
	/**
	 * Shorthand for calling {@link IntegerBuilder#withValues(int...)}.
	 * 
	 * @param values the enumerated values
	 * @return the enumerated parameter
	 */
	public Enumeration<Integer> withValues(int... values) {
		return asInt().withValues(values);
	}
	
	/**
	 * Shorthand for calling {@link LongBuilder#withValues(long...)}.
	 * 
	 * @param values the enumerated values
	 * @return the enumerated parameter
	 */
	public Enumeration<Long> withValues(long... values) {
		return asLong().withValues(values);
	}
	
	/**
	 * Shorthand for calling {@link DecimalBuilder#withValues(double...)}.
	 * 
	 * @param values the enumerated values
	 * @return the enumerated parameter
	 */
	public Enumeration<Double> withValues(double... values) {
		return asDecimal().withValues(values);
	}
	
	/**
	 * Shorthand for calling {@link StringBuilder#withValues(String...)}.
	 * 
	 * @param values the enumerated values
	 * @return the enumerated parameter
	 */
	public Enumeration<String> withValues(String... values) {
		return asString().withValues(values);
	}
	
	/**
	 * Abstract class for implementing typed parameter builders.
	 */
	abstract static class TypedParameterBuilder {
		
		final String name;
		
		TypedParameterBuilder(String name) {
			super();
			this.name = name;
		}
		
	}
	
	/**
	 * A typed parameter builder for integer values.
	 */
	public static class IntegerBuilder extends TypedParameterBuilder {
		
		IntegerBuilder(String name) {
			super(name);
		}
		
		/**
		 * Builds a constant parameter with the given value.
		 * 
		 * @param value the constant value
		 * @return the parameter
		 */
		public Constant<Integer> constant(int value) {
			return new Constant<Integer>(name, value);
		}
		
		/**
		 * Builds an enumeration with the given values.
		 * 
		 * @param values the enumerated values
		 * @return the parameter
		 */
		public Enumeration<Integer> withValues(int... values) {
			return of(IntStream.of(values));
		}
		
		/**
		 * Builds an enumeration of all values between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endExclusive the upper bound (exclusive)
		 * @return the parameter
		 */
		public Enumeration<Integer> rangeExclusive(int startInclusive, int endExclusive) {
			return of(IntStream.range(startInclusive, endExclusive));
		}
		
		/**
		 * Builds an enumeration of values between the start and end bounds, incrementing by the given step size.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endExclusive the upper bound (exclusive)
		 * @param stepSize the step size
		 * @return the parameter
		 */
		public Enumeration<Integer> rangeExclusive(int startInclusive, int endExclusive, int stepSize) {
			return of(IntStream.iterate(startInclusive, i -> i < endExclusive, i -> i + stepSize));
		}
		
		/**
		 * Builds an enumeration of all values between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @return the parameter
		 */
		public Enumeration<Integer> range(int startInclusive, int endInclusive) {
			return of(IntStream.rangeClosed(startInclusive, endInclusive));
		}
		
		/**
		 * Builds an enumeration of values between the start and end bounds, incrementing by the given step size.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @param stepSize the step size
		 * @return the parameter
		 */
		public Enumeration<Integer> range(int startInclusive, int endInclusive, int stepSize) {
			return of(IntStream.iterate(startInclusive, i -> i <= endInclusive, i -> i + stepSize));
		}
		
		/**
		 * Builds an enumeration of values sampled uniformly at random between the start and end bounds.  Unlike
		 * {@link #sampledBetween(int, int)}, this produces a fixed enumeration of values.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @param count the number of values to enumerate
		 * @return the parameter
		 */
		public Enumeration<Integer> random(int startInclusive, int endInclusive, int count) {
			return of(IntStream.range(0, count).map(i -> PRNG.nextInt(startInclusive, endInclusive)));
		}
		
		Enumeration<Integer> of(IntStream stream) {
			return of(stream.boxed());
		}
		
		Enumeration<Integer> of(Stream<Integer> stream) {
			return new Enumeration<Integer>(name, stream.toList());
		}
		
		/**
		 * Builds a parameter that samples values uniformly at random between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @return the parameter
		 */
		public IntegerRange sampledBetween(int startInclusive, int endInclusive) {
			return new IntegerRange(name, startInclusive, endInclusive);
		}
		
	}
	
	/**
	 * A typed parameter builder for long values.
	 */
	public static class LongBuilder extends TypedParameterBuilder {
		
		LongBuilder(String name) {
			super(name);
		}
		
		/**
		 * Builds a constant parameter with the given value.
		 * 
		 * @param value the constant value
		 * @return the parameter
		 */
		public Constant<Long> constant(long value) {
			return new Constant<Long>(name, value);
		}
		
		/**
		 * Builds an enumeration with the given values.
		 * 
		 * @param values the enumerated values
		 * @return the parameter
		 */
		public Enumeration<Long> withValues(long... values) {
			return of(LongStream.of(values));
		}
		
		/**
		 * Builds an enumeration of all values between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endExclusive the upper bound (exclusive)
		 * @return the parameter
		 */
		public Enumeration<Long> rangeExclusive(long startInclusive, long endExclusive) {
			return of(LongStream.range(startInclusive, endExclusive));
		}
		
		/**
		 * Builds an enumeration of values between the start and end bounds, incrementing by the given step size.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endExclusive the upper bound (exclusive)
		 * @param stepSize the step size
		 * @return the parameter
		 */
		public Enumeration<Long> raneExclusive(long startInclusive, long endExclusive, long stepSize) {
			return of(LongStream.iterate(startInclusive, i -> i < endExclusive, i -> i + stepSize));
		}
		
		/**
		 * Builds an enumeration of all values between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @return the parameter
		 */
		public Enumeration<Long> range(long startInclusive, long endInclusive) {
			return of(LongStream.rangeClosed(startInclusive, endInclusive));
		}
		
		/**
		 * Builds an enumeration of values between the start and end bounds, incrementing by the given step size.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @param stepSize the step size
		 * @return the parameter
		 */
		public Enumeration<Long> range(long startInclusive, long endInclusive, long stepSize) {
			return of(LongStream.iterate(startInclusive, i -> i <= endInclusive, i -> i + stepSize));
		}
		
		/**
		 * Builds an enumeration of values sampled uniformly at random between the start and end bounds.  Unlike
		 * {@link #sampledBetween(long, long)}, this produces a fixed enumeration of values.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @param count the number of values to enumerate
		 * @return the parameter
		 */
		public Enumeration<Long> random(long startInclusive, long endInclusive, int count) {
			return of(LongStream.range(0, count).map(i -> PRNG.getRandom().nextLong(startInclusive, endInclusive)));
		}
		
		Enumeration<Long> of(LongStream stream) {
			return of(stream.boxed());
		}
		
		Enumeration<Long> of(Stream<Long> stream) {
			return new Enumeration<Long>(name, stream.toList());
		}
		
		/**
		 * Builds a parameter that samples values uniformly at random between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @return the parameter
		 */
		public LongRange sampledBetween(long startInclusive, long endInclusive) {
			return new LongRange(name, startInclusive, endInclusive);
		}
		
	}
	
	/**
	 * A typed parameter builder for decimal for floating-point values.
	 */
	public static class DecimalBuilder extends TypedParameterBuilder {
		
		DecimalBuilder(String name) {
			super(name);
		}
		
		/**
		 * Builds a constant parameter with the given value.
		 * 
		 * @param value the constant value
		 * @return the parameter
		 */
		public Constant<Double> constant(double value) {
			return new Constant<Double>(name, value);
		}
		
		/**
		 * Builds an enumeration with the given values.
		 * 
		 * @param values the enumerated values
		 * @return the parameter
		 */
		public Enumeration<Double> withValues(double... values) {
			return of(DoubleStream.of(values));
		}
		
		/**
		 * Builds an enumeration of values between the start and end bounds, incrementing by the given step size.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endExclusive the upper bound (exclusive)
		 * @param stepSize the step size
		 * @return the parameter
		 */
		public Enumeration<Double> rangeExclusive(double startInclusive, double endExclusive, double stepSize) {
			return of(DoubleStream.iterate(startInclusive, i -> i < endExclusive, i -> i + stepSize));
		}
		
		/**
		 * Builds an enumeration of values between the start and end bounds, incrementing by the given step size.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @param stepSize the step size
		 * @return the parameter
		 */
		public Enumeration<Double> range(double startInclusive, double endInclusive, double stepSize) {
			return of(DoubleStream.iterate(startInclusive, i -> i <= endInclusive, i -> i + stepSize));
		}
		
		/**
		 * Builds an enumeration of values sampled uniformly at random between the start and end bounds.  Unlike
		 * {@link #sampledBetween(double, double)}, this produces a fixed enumeration of values.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @param count the number of values to enumerate
		 * @return the parameter
		 */
		public Enumeration<Double> random(double startInclusive, double endInclusive, int count) {
			return of(IntStream.range(0, count).mapToDouble(i -> PRNG.nextDouble(startInclusive, endInclusive)));
		}
		
		Enumeration<Double> of(DoubleStream stream) {
			return of(stream.boxed());
		}
		
		Enumeration<Double> of(Stream<Double> stream) {
			return new Enumeration<Double>(name, stream.toList());
		}
		
		/**
		 * Builds a parameter that samples values uniformly at random between the start and end bounds.
		 * 
		 * @param startInclusive the lower bound (inclusive)
		 * @param endInclusive the upper bound (inclusive)
		 * @return the parameter
		 */
		public DecimalRange sampledBetween(double startInclusive, double endInclusive) {
			return new DecimalRange(name, startInclusive, endInclusive);
		}
		
	}
	
	/**
	 * A typed parameter builder for strings.
	 */
	public static class StringBuilder extends TypedParameterBuilder {
		
		StringBuilder(String name) {
			super(name);
		}
		
		/**
		 * Builds a constant parameter with the given value.
		 * 
		 * @param value the constant value
		 * @return the parameter
		 */
		public Constant<String> constant(String value) {
			return new Constant<String>(name, value);
		}
		
		/**
		 * Builds an enumeration with the given values.
		 * 
		 * @param values the enumerated values
		 * @return the parameter
		 */
		public Enumeration<String> withValues(String... values) {
			return of(Stream.of(values));
		}
		
		Enumeration<String> of(Stream<String> stream) {
			return new Enumeration<String>(name, stream.toList());
		}
		
	}

}
