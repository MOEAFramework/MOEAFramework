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

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

/**
 * Parameter representing a {@link Long} value, ranging from {@value Long#MIN_VALUE} to {@value Long#MAX_VALUE}.
 */
public class SampledLong extends AbstractParameter<Long> implements SampledParameter<Long>, NumericParameter<Long> {
	
	private final long lowerBound;
	
	private final long upperBound;

	/**
	 * Constructs a new long parameter with the given sampling bounds.
	 * 
	 * @param name the parameter name
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 */
	public SampledLong(String name, long lowerBound, long upperBound) {
		super(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public Long getLowerBound() {
		return lowerBound;
	}

	@Override
	public Long getUpperBound() {
		return upperBound;
	}
	
	@Override
	public Long parse(String str) {
		try {
			long value = Long.parseLong(str);
			
			if (value < lowerBound || value > upperBound) {
				throw new InvalidParameterException(getName(), "long value expected in range [" + lowerBound +
						", " + upperBound + "], but given " + value);
			}
			
			return value;
		} catch (NumberFormatException e) {
			throw new InvalidParameterException(getName(), "value is not a long, given '" + str + "'");
		}
	}
	
	@Override
	public void sample(Sample sample, double scale) {
		Validate.that("scale", scale).isBetween(0.0, 1.0);
		sample.setLong(getName(), (long)(lowerBound + scale *
				Math.nextAfter(upperBound - lowerBound + 1, Double.NEGATIVE_INFINITY)));
	}
	
	@Override
	public String encode(Tokenizer tokenizer) {
		return tokenizer.encode(List.of(getName(), "long", Long.toString(getLowerBound()),
				Long.toString(getUpperBound())));
	}
	
	/**
	 * Decodes the string representation of this parameter.
	 * 
	 * @param tokenizer the tokenizer
	 * @param line the string representation
	 * @return the decoded parameter
	 * @throws InvalidParameterException if the string representation is not a valid parameter
	 */
	public static SampledLong decode(Tokenizer tokenizer, String line) {
		String[] tokens = tokenizer.decodeToArray(line);
		
		if (tokens.length < 2) {
			throw new InvalidParameterException(tokens[0], "missing type");
		}
		
		if (!tokens[1].equalsIgnoreCase("long")) {
			throw new InvalidParameterException(tokens[0], "type does not match 'long'");
		}
		
		if (tokens.length != 4) {
			throw new InvalidParameterException(tokens[0], "ranges require a lower and upper bound");
		}
		
		return new SampledLong(tokens[0], Long.parseLong(tokens[2]), Long.parseLong(tokens[3]));
	}

}
