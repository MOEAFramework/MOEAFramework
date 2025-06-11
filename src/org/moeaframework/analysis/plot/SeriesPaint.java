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
import java.awt.Paint;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the paint / color of a plotted series.
 */
public class SeriesPaint implements StyleAttribute {
	
	private final Paint paint;
	
	/**
	 * Constructs a new paint style attribute.
	 * 
	 * @param paint the paint
	 */
	public SeriesPaint(Paint paint) {
		super();
		this.paint = paint;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);
			
			if (series >= 0) {
				renderer.setSeriesPaint(series, paint);
				renderer.setSeriesFillPaint(series, paint);
			} else {
				renderer.setDefaultPaint(paint);
				renderer.setDefaultFillPaint(paint);
			}
		} else if (plot instanceof CategoryPlot categoryPlot) {
			CategoryItemRenderer renderer = categoryPlot.getRenderer(dataset);
			
			if (series >= 0) {
				renderer.setSeriesPaint(series, paint);
				renderer.setSeriesFillPaint(series, paint);
			} else {
				renderer.setDefaultPaint(paint);
				renderer.setDefaultFillPaint(paint);
			}
		}
	}
	
	/**
	 * Returns a custom paint style attribute.
	 * 
	 * @param paint the paint
	 * @return the resulting style attribute
	 */
	public static SeriesPaint of(Paint paint) {
		return new SeriesPaint(paint);
	}
	
	/**
	 * Returns a black paint style attribute.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesPaint black() {
		return of(Color.BLACK);
	}
	
	/**
	 * Returns a red paint style attribute.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesPaint red() {
		return of(Color.RED);
	}
	
	/**
	 * Returns a green paint style attribute.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesPaint green() {
		return of(Color.GREEN);
	}
	
	/**
	 * Returns a blue paint style attribute.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesPaint blue() {
		return of(Color.BLUE);
	}
	
	/**
	 * Returns a paint style attribute using an RGB color model.
	 * 
	 * @param r the red component ({@code 0 - 255})
	 * @param g the green component ({@code 0 - 255})
	 * @param b the blue component ({@code 0 - 255})
	 * @return the resulting style attribute
	 * @see Color#Color(int, int, int)
	 */
	public static SeriesPaint rgb(int r, int g, int b) {
		return of(new Color(r, g, b));
	}
	
	/**
	 * Returns a paint style attribute using an HSB color model.
	 * 
	 * @param h the hue
	 * @param s the saturation
	 * @param b the brightness
	 * @return the resulting style attribute
	 * @see Color#getHSBColor(float, float, float)
	 */
	public static SeriesPaint hsb(float h, float s, float b) {
		return of(Color.getHSBColor(h, s, b));
	}

}
