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

import java.awt.Color;
import java.awt.Paint;

/**
 * Paint scale producing a rainbow of colors by adjusting the hue component.
 */
public class RainbowPaintScale extends AutoScaledPaintScale {

	/**
	 * Constructs a rainbow paint scale.
	 * 
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 */
	public RainbowPaintScale(double lowerBound, double upperBound) {
		super(lowerBound, upperBound);
	}

	@Override
	public Paint getScaledPaint(double value) {
		return Color.getHSBColor((float)value, 1f, 1f);
	}
	
	@Override
	public RainbowPaintScale scale(double lowerBound, double upperBound) {
		return new RainbowPaintScale(lowerBound, upperBound);
	}
	
}