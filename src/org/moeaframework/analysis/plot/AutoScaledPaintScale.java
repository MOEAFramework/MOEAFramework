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
package org.moeaframework.analysis.plot;

import java.awt.Paint;

import org.jfree.chart.renderer.PaintScale;

/**
 * Abstract class for defining paint scales that are automatically scaled based on the range of Z values.
 */
abstract class AutoScaledPaintScale implements PaintScale {
	
	private final double lowerBound;
	private final double upperBound;

	/**
	 * Constructs an auto-scaled paint scale with initial bounds.
	 * 
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 */
	public AutoScaledPaintScale(double lowerBound, double upperBound) {
		super();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public double getLowerBound() {
		return lowerBound;
	}

	@Override
	public double getUpperBound() {
		return upperBound;
	}
	
	/**
	 * Returns the paint for a value scaled by the lower and upper bounds.
	 * 
	 * @param value the scaled value, between {@code 0.0} and {@code 1.0}
	 * @return the paint
	 */
	public abstract Paint getScaledPaint(double value);
	
	/**
	 * Returns a new paint scale with new lower and upper bounds.
	 * 
	 * @param lowerBound the new lower bound
	 * @param upperBound the new upper bound
	 * @return the new paint scale
	 */
	public abstract AutoScaledPaintScale scale(double lowerBound, double upperBound);

	@Override
	public Paint getPaint(double value) {
		double boundedValue = Math.min(Math.max(value, lowerBound), upperBound);
		return getScaledPaint((boundedValue - lowerBound) / (upperBound - lowerBound));
	}
	
}