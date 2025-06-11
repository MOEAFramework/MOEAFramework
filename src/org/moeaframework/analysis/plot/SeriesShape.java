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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the shape of a plotted series
 */
public class SeriesShape implements StyleAttribute {
	
	private final Shape shape;
	
	/**
	 * Creates a new shape style attribute.
	 * 
	 * @param shape the shape
	 */
	public SeriesShape(Shape shape) {
		super();
		this.shape = shape;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);			
			
			if (series >= 0) {
				renderer.setSeriesShape(series, shape);
			} else {
				renderer.setDefaultShape(shape);
			}
		}
	}
	
	/**
	 * Returns a custom shape style attribute.
	 * 
	 * @param shape the shape
	 * @return the resulting shape style attribute
	 */
	public static SeriesShape of(Shape shape) {
		return new SeriesShape(shape);
	}
	
	/**
	 * Returns a circle shape style attribute.
	 * 
	 * @return the resulting shape style attribute
	 */
	public static SeriesShape circle() {
		return of(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
	}
	
	/**
	 * Returns a square shape style attribute.
	 * 
	 * @return the resulting shape style attribute
	 */
	public static SeriesShape square() {
		return of(new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0));
	}

}
