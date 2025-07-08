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

import java.awt.geom.RectangularShape;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the shape of a plotted series.
 */
public class ShapeAttribute implements StyleAttribute {
	
	private final RectangularShape shape;
	
	/**
	 * Creates a new shape style attribute.
	 * 
	 * @param shape the shape
	 */
	public ShapeAttribute(RectangularShape shape) {
		super();
		this.shape = shape;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);	
			
			if (renderer == null) {
				// do nothing
			} else if (series >= 0) {
				if (renderer.getSeriesShape(series) instanceof RectangularShape oldShape) {
					shape.setFrame(oldShape.getFrame());
				}
								
				renderer.setSeriesShape(series, shape);
			} else {
				if (renderer.getDefaultShape() instanceof RectangularShape oldShape) {
					shape.setFrame(oldShape.getFrame());
				}
				
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
	public static ShapeAttribute of(RectangularShape shape) {
		return new ShapeAttribute(shape);
	}

}
