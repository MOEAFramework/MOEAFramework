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

import java.awt.BasicStroke;
import java.awt.geom.RectangularShape;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the line thickness or shape size of a plotted series.
 */
public class SeriesSize implements StyleAttribute {
	
	private final float size;
	
	/**
	 * Constructs a new size style attribute.
	 * 
	 * @param size the size
	 */
	public SeriesSize(float size) {
		super();
		this.size = size;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);			
			
			if (series >= 0) {
				if (renderer.getSeriesShape(series) instanceof RectangularShape shape) {
					shape.setFrame(-size / 2.0f, -size / 2.0f, size, size);
					renderer.setSeriesShape(series, shape);
				}
				
				if (renderer.getSeriesStroke(series) instanceof BasicStroke stroke) {
					renderer.setSeriesStroke(series, new BasicStroke(
							size,
							stroke.getEndCap(),
							stroke.getLineJoin(),
							stroke.getMiterLimit(),
							stroke.getDashArray(),
							stroke.getDashPhase()));
				} else {
					renderer.setSeriesStroke(series, new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				}
			} else {
				if (renderer.getDefaultShape() instanceof RectangularShape shape) {
					shape.setFrame(-size / 2.0f, -size / 2.0f, size, size);
					renderer.setDefaultShape(shape);
				}
				
				if (renderer.getDefaultStroke() instanceof BasicStroke stroke) {
					renderer.setDefaultStroke(new BasicStroke(
							size,
							stroke.getEndCap(),
							stroke.getLineJoin(),
							stroke.getMiterLimit(),
							stroke.getDashArray(),
							stroke.getDashPhase()));
				} else {
					renderer.setDefaultStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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
	public static SeriesSize of(float size) {
		return new SeriesSize(size);
	}
	
	/**
	 * Returns a small size style.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesSize small() {
		return of(3f);
	}
	
	/**
	 * Returns a medium size style.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesSize medium() {
		return of(6f);
	}
	
	/**
	 * Returns a large size style.
	 * 
	 * @return the resulting style attribute
	 */
	public static SeriesSize large() {
		return of(9f);
	}

}
