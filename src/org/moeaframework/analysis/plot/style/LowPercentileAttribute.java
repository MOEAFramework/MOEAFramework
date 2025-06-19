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
 * Defines the low percentile when producing deviation plots.
 */
public class LowPercentileAttribute extends ValueAttribute<Double> {
	
	/**
	 * The default value.
	 */
	public static final LowPercentileAttribute DEFAULT_VALUE = LowPercentileAttribute.of(25.0);
	
	/**
	 * Constructs a new attribute defining the low percentile.
	 * 
	 * @param lowPercentile the percentile, a value between {@code 0.0} and {@code 50.0}
	 */
	public LowPercentileAttribute(double lowPercentile) {
		super(lowPercentile);
		Validate.that("lowPercentile", lowPercentile).isBetween(0.0, 50.0);
	}

	/**
	 * Returns an attribute defining the low percentile when producing deviation plots.
	 * 
	 * @param lowPercentile the percentile, a value between {@code 0.0} and {@code 50.0}
	 * @return the resulting attribute
	 */
	public static LowPercentileAttribute of(double lowPercentile) {
		return new LowPercentileAttribute(lowPercentile);
	}

}
