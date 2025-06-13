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
package org.moeaframework.analysis.plot.style;

import org.moeaframework.util.validate.Validate;

/**
 * Defines the high percentile when producing deviation plots.
 */
public class HighPercentileAttribute extends ValueAttribute<Double> {
	
	/**
	 * The default value.
	 */
	public static final HighPercentileAttribute DEFAULT_VALUE = HighPercentileAttribute.of(75.0);
	
	/**
	 * Constructs a new attribute defining the high percentile.
	 * 
	 * @param highPercentile the percentile, a value between {@code 50.0} and {@code 100.0}
	 */
	public HighPercentileAttribute(double highPercentile) {
		super(highPercentile);
		Validate.that("highPercentile", highPercentile).isBetween(50.0, 100.0);
	}

	/**
	 * Returns an attribute defining the high percentile when producing deviation plots.
	 * 
	 * @param highPercentile the percentile, a value between {@code 50.0} and {@code 100.0}
	 * @return the resulting attribute
	 */
	public static HighPercentileAttribute of(double highPercentile) {
		return new HighPercentileAttribute(highPercentile);
	}

}
