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

import java.awt.Paint;

/**
 * Paint scale that maps Z values to a list of colors.
 */
public class IndexedPaintScale extends AutoScaledPaintScale {
	
	private final Paint[] paints;
	
	/**
	 * Constructs an indexed paint scale.
	 * 
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @param paints the array of paints
	 */
	public IndexedPaintScale(double lowerBound, double upperBound, Paint... paints) {
		super(lowerBound, upperBound);
		this.paints = paints;
	}
	
	@Override
	public Paint getScaledPaint(double value) {
		int index = (int)(value * paints.length);
		
		if (index < 0) {
			index = 0;
		} else if (index >= paints.length) {
			index = paints.length - 1;
		}
		
		return paints[index];
	}
	
	@Override
	public IndexedPaintScale scale(double lowerBound, double upperBound) {
		return new IndexedPaintScale(lowerBound, upperBound, paints);
	}
	
}