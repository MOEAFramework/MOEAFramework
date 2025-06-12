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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Collection of style constants.
 */
public class Style {
	
	/**
	 * The default size.
	 */
	public static final double DEFAULT_SIZE = 6.0;
	
	private Style() {
		super();
	}
	
	/**
	 * Returns a style attribute to render the series shape as a circle.
	 * 
	 * @return the resulting style attribute
	 */
	public static ShapeAttribute circle() {
		return ShapeAttribute.of(new Ellipse2D.Double(-DEFAULT_SIZE / 2.0, -DEFAULT_SIZE / 2.0, DEFAULT_SIZE, DEFAULT_SIZE));
	}
	
	/**
	 * Returns a style attribute to render the series shape as a square.
	 * 
	 * @return the resulting style attribute
	 */
	public static ShapeAttribute square() {
		return ShapeAttribute.of(new Rectangle2D.Double(-DEFAULT_SIZE / 2.0, -DEFAULT_SIZE / 2.0, DEFAULT_SIZE, DEFAULT_SIZE));
	}

	/**
	 * Returns a style attribute to render the series line and/or shape in a small size.
	 * 
	 * @return the resulting style attribute
	 */
	public static SizeAttribute small() {
		return SizeAttribute.of(0.5 * DEFAULT_SIZE);
	}

	/**
	 * Returns a style attribute to render the series line and/or shape in a medium size.
	 * 
	 * @return the resulting style attribute
	 */
	public static SizeAttribute medium() {
		return SizeAttribute.of(DEFAULT_SIZE);
	}

	/**
	 * Returns a style attribute to render the series line and/or shape in a large size.
	 * 
	 * @return the resulting style attribute
	 */
	public static SizeAttribute large() {
		return SizeAttribute.of(1.5 * DEFAULT_SIZE);
	}
	
	/**
	 * Returns a style attribute to render the series in a black color.
	 * 
	 * @return the resulting style attribute
	 */
	public static PaintAttribute black() {
		return PaintAttribute.of(Color.BLACK);
	}
	
	/**
	 * Returns a style attribute to render the series in a red color.
	 * 
	 * @return the resulting style attribute
	 */
	public static PaintAttribute red() {
		return PaintAttribute.of(Color.RED);
	}
	
	/**
	 * Returns a style attribute to render the series in a green color.
	 * 
	 * @return the resulting style attribute
	 */
	public static PaintAttribute green() {
		return PaintAttribute.of(Color.GREEN);
	}
	
	/**
	 * Returns a style attribute to render the series in a blue color.
	 * 
	 * @return the resulting style attribute
	 */
	public static PaintAttribute blue() {
		return PaintAttribute.of(Color.BLUE);
	}
	
	/**
	 * Returns a style attribute to render the series in the specified RGB color.
	 * 
	 * @param r the red component ({@code 0 - 255})
	 * @param g the green component ({@code 0 - 255})
	 * @param b the blue component ({@code 0 - 255})
	 * @return the resulting style attribute
	 * @see Color#Color(int, int, int)
	 */
	public static PaintAttribute rgb(int r, int g, int b) {
		return PaintAttribute.of(new Color(r, g, b));
	}
	
	/**
	 * Returns a style attribute to render the series in the specified HSB color.
	 * 
	 * @param h the hue
	 * @param s the saturation
	 * @param b the brightness
	 * @return the resulting style attribute
	 * @see Color#getHSBColor(float, float, float)
	 */
	public static PaintAttribute hsb(float h, float s, float b) {
		return PaintAttribute.of(Color.getHSBColor(h, s, b));
	}
	
	/**
	 * Returns a style attribute to render the series in the specified color gradient paint scale.
	 * 
	 * @param baseColor the base color
	 * @return the resulting style attribute
	 */
	public static PaintScaleAttribute gradient(Color baseColor) {
		return PaintScaleAttribute.of(new ColorGradientPaintScale(0.0, 1.0, baseColor));
	}
	
	/**
	 * Returns a style attribute to render the series using a rainbow paint scale.
	 * 
	 * @return the resulting style attribute
	 */
	public static PaintScaleAttribute rainbow() {
		return PaintScaleAttribute.of(new RainbowPaintScale(0.0, 1.0));
	}
	
	/**
	 * Returns a style attribute that renders labels alongside data points.
	 * 
	 * @return the resulting style attribute
	 */
	public static LabelAttribute showLabels() {
		return LabelAttribute.of();
	}
	
	/**
	 * Returns a style attribute that displays tooltips when hovering the mouse over data points.
	 * 
	 * @return the resulting style attribute
	 */
	public static TooltipAttribute showToolTips() {
		return TooltipAttribute.of();
	}

}
