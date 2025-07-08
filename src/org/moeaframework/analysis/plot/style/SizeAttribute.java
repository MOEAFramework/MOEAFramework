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

import java.awt.BasicStroke;
import java.awt.geom.RectangularShape;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the line thickness or shape size of a plotted series.
 */
public class SizeAttribute implements StyleAttribute {
	
	private final double size;
	
	/**
	 * Constructs a new size style attribute.
	 * 
	 * @param size the size
	 */
	public SizeAttribute(double size) {
		super();
		this.size = size;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);
			
			if (renderer == null) {
				// do nothing
			} else if (series >= 0) {
				if (renderer.getSeriesShape(series) instanceof RectangularShape shape) {
					shape.setFrame(-size / 2.0, -size / 2.0, size, size);
					renderer.setSeriesShape(series, shape);
				}
				
				if (renderer.getSeriesStroke(series) instanceof BasicStroke stroke) {
					renderer.setSeriesStroke(series, new BasicStroke(
							(float)(size / 2.0),
							stroke.getEndCap(),
							stroke.getLineJoin(),
							stroke.getMiterLimit(),
							stroke.getDashArray(),
							stroke.getDashPhase()));
				} else {
					renderer.setSeriesStroke(series, new BasicStroke(
							(float)(size / 2.0),
							BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND));
				}
			} else {
				if (renderer.getDefaultShape() instanceof RectangularShape shape) {
					shape.setFrame(-size / 2.0, -size / 2.0, size, size);
					renderer.setDefaultShape(shape);
				}
				
				if (renderer.getDefaultStroke() instanceof BasicStroke stroke) {
					renderer.setDefaultStroke(new BasicStroke(
							(float)(size / 2.0),
							stroke.getEndCap(),
							stroke.getLineJoin(),
							stroke.getMiterLimit(),
							stroke.getDashArray(),
							stroke.getDashPhase()));
				} else {
					renderer.setDefaultStroke(new BasicStroke((float)(size / 2.0),
							BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND));
				}
			}
		}
	}
	
	
	
	/**
	 * Returns a custom size style attribute.
	 * 
	 * @param size the size
	 * @return the resulting style attribute
	 */
	public static SizeAttribute of(double size) {
		return new SizeAttribute(size);
	}

}
