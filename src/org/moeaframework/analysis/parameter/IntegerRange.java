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

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.validate.Validate;

public class IntegerRange extends AbstractParameter<Integer> implements SampledParameter<Integer>,
NumericParameter<Integer> {
	
	private final int lowerBound;
	
	private final int upperBound;

	public IntegerRange(String name, int lowerBound, int upperBound) {
		super(name);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public Integer getLowerBound() {
		return lowerBound;
	}

	@Override
	public Integer getUpperBound() {
		return upperBound;
	}
	
	@Override
	public double getNormalizedValue(Sample sample) {
		int value = getValue(sample);
		return (value - lowerBound) / (double)(upperBound - lowerBound);
	}
	
	@Override
	public Integer parse(String str) {
		try {
			int value = Integer.parseInt(str);
			
			if (value < lowerBound || value > upperBound) {
				throw new InvalidParameterException(getName(), "integer value expected in range [" + lowerBound +
						", " + upperBound + "], but given " + value);
			}
			
			return value;
		} catch (NumberFormatException e) {
			throw new InvalidParameterException(getName(), "value is not an integer, given '" + str + "'");
		}
	}
	
	@Override
	public void apply(Sample sample, double scale) {
		Validate.that("scale", scale).isBetween(0.0, 1.0);
		sample.setInt(getName(), (int)(lowerBound + scale *
				(upperBound - lowerBound + Math.nextAfter(1.0, Double.NEGATIVE_INFINITY))));
	}
	
	@Override
	public String toString() {
		return getName() + ": (" + getLowerBound() + ", " + getUpperBound() + ")";
	}

}
