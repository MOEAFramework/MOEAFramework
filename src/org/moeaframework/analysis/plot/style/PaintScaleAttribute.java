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

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.DefaultXYZDataset;

/**
 * Styles the paint scale of a plot using block rendering, such as a heat map.
 */
public class PaintScaleAttribute implements StyleAttribute {
	
	private final PaintScale paintScale;
	
	/**
	 * Constructs a new paint scale style attribute.
	 * 
	 * @param paintScale the paint scale
	 */
	public PaintScaleAttribute(PaintScale paintScale) {
		super();
		this.paintScale = paintScale;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot &&
				xyPlot.getDataset(dataset) instanceof DefaultXYZDataset xyzDataset &&
				xyPlot.getRenderer(dataset) instanceof XYBlockRenderer blockRenderer) {
			series = series >= 0 ? series : 0;
			
			if (paintScale instanceof AutoScaledPaintScale autoScaledPaintScale) {
				double zMin = Double.POSITIVE_INFINITY;
				double zMax = Double.NEGATIVE_INFINITY;
				
				for (int i = 0; i < xyzDataset.getItemCount(series); i++) {
					zMin = Math.min(zMin, xyzDataset.getZValue(series, i));
					zMax = Math.max(zMax, xyzDataset.getZValue(series, i));
				}
				
				blockRenderer.setPaintScale(autoScaledPaintScale.scale(zMin, zMax));
			} else {
				blockRenderer.setPaintScale(paintScale);
			}
		}
	}
	
	/**
	 * Returns a custom paint scale style attribute.
	 * 
	 * @param paintScale the paint scale
	 * @return the resulting style attribute
	 */
	public static PaintScaleAttribute of(PaintScale paintScale) {
		return new PaintScaleAttribute(paintScale);
	}

}
