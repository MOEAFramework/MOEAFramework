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

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

public class DecimalRange extends AbstractParameter<Double> implements SampledParameter<Double>,
NumericParameter<Double> {
	
	private final double lowerBound;
	
	private final double upperBound;

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
	public void apply(Sample sample, double scale) {
		Validate.that("scale", scale).isBetween(0.0, 1.0);
		sample.setDouble(getName(), lowerBound + scale * (upperBound - lowerBound));
	}
	
	@Override
	public String toString() {
		return getName() + "(" + getLowerBound() + ", " + getUpperBound() + ")";
	}
	
	@Override
	public String encode(Tokenizer tokenizer) {
		return tokenizer.encode(List.of(getName(), "decimal", Double.toString(getLowerBound()),
				Double.toString(getUpperBound())));
	}
	
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

}
