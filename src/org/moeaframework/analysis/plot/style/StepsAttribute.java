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
 * Defines the number of steps used when rendering histogram or deviation plots.  That is, when aggregating the data,
 * the width of each step is {@code (xMax - xMin) / steps}.
 */
public class StepsAttribute extends ValueAttribute<Integer> {
	
	/**
	 * The default value.
	 */
	public static final StepsAttribute DEFAULT_VALUE = StepsAttribute.of(100);

	/**
	 * Constructs a new step attribute.
	 * 
	 * @param steps the number of steps
	 */
	public StepsAttribute(int steps) {
		super(steps);
		Validate.that("steps", steps).isGreaterThan(0);
	}

	/**
	 * Returns an attribute defining the number of steps used when rendering histogram or deviation plots.
	 * 
	 * @param steps the number of steps
	 * @return the resulting attribute
	 */
	public static StepsAttribute of(int steps) {
		return new StepsAttribute(steps);
	}

}
