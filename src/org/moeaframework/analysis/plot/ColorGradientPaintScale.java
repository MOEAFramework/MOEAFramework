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

import java.awt.Color;

/**
 * Paint scale producing a gradient of some base color by adjusting its brightness.
 */
public class ColorGradientPaintScale extends IndexedPaintScale {
	
	/**
	 * Constructs a color gradient paint scale.
	 * 
	 * @param lowerBound the lower bound
	 * @param upperBound the upper bound
	 * @param baseColor the base color
	 */
	public ColorGradientPaintScale(double lowerBound, double upperBound, Color baseColor) {
		super(lowerBound, upperBound, generateColors(baseColor, 256));
    }
	
	private static final Color[] generateColors(Color baseColor, int numberOfColors) {
		Color[] colors = new Color[numberOfColors];
		float[] hsb = new float[3];
		
		Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), hsb);
		
		for (int i = 0; i < numberOfColors; i++) {
			colors[i] = Color.getHSBColor(hsb[0], hsb[1], (float)i / (numberOfColors - 1));
		}
		
		return colors;
	}
	
}