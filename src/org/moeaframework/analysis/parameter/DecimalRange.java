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

import java.text.DecimalFormat;
import java.util.List;

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.core.Settings;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

/**
 * Parameter representing a {@link Double} value.
 */
public class DecimalRange extends AbstractParameter<Double> implements SampledParameter<Double>,
NumericParameter<Double> {
	
	private static final DecimalFormat PRECISION_FORMAT;
	
	static {
		int digits = (int)-Math.log10(Settings.EPS);
		PRECISION_FORMAT = new DecimalFormat("0.0" + (digits > 1 ? "#".repeat(digits-1) : ""));
	}
	
	private final double lowerBound;
	
	private final double upperBound;

	/**
	 * Constructs a new decimal parameter with the given sampling bounds.
	 * 
	 * @param name the parameter name
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 */
	public DecimalRange(String name, double lowerBound, double upperBound) {
		super(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public Double getLowerBound() {
		return lowerBound;
	}

	@Override
	public Double getUpperBound() {
		return upperBound;
	}
	
	@Override
	public Double parse(String str) {
		try {
			double value = Double.parseDouble(str);
			
			if (value < lowerBound || value > upperBound) {
				throw new InvalidParameterException(getName(), "decimal value expected in range [" + lowerBound +
						", " + upperBound + "], but given " + value);
			}
			
			return value;
		} catch (NumberFormatException e) {
			throw new InvalidParameterException(getName(), "value is not an decimal, given '" + str + "'");
		}
	}
	
	@Override
	public void sample(Sample sample, double scale) {
		Validate.that("scale", scale).isBetween(0.0, 1.0);
		sample.setString(getName(), applyPrecision(lowerBound + scale * (upperBound - lowerBound)));
	}
	
	@Override
	public String encode(Tokenizer tokenizer) {
		return tokenizer.encode(List.of(getName(), "decimal", Double.toString(getLowerBound()),
				Double.toString(getUpperBound())));
	}
	
	/**
	 * Decodes the string representation of this parameter.
	 * 
	 * @param tokenizer the tokenizer
	 * @param line the string representation
	 * @return the decoded parameter
	 * @throws InvalidParameterException if the string representation is not a valid parameter
	 */
	public static DecimalRange decode(Tokenizer tokenizer, String line) {
		String[] tokens = tokenizer.decodeToArray(line);
		
		if (tokens.length < 2) {
			throw new InvalidParameterException(tokens[0], "missing type");
		}
		
		if (!tokens[1].equalsIgnoreCase("decimal")) {
			throw new InvalidParameterException(tokens[0], "type does not match 'decimal'");
		}
		
		if (tokens.length != 4) {
			throw new InvalidParameterException(tokens[0], "ranges require a lower and upper bound");
		}
		
		return new DecimalRange(tokens[0], Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
	}
	
	/**
	 * Rounds the decimal value using the same precision as {@link Settings#EPS}.  This prevents small errors in the
	 * floating-point representation / arithmetic from manifesting as different parameter values.
	 * 
	 * @param value the original decimal value
	 * @return the rounded decimal value stored as a string
	 */
	public static String applyPrecision(double value) {
		return PRECISION_FORMAT.format(value);
	}

}
