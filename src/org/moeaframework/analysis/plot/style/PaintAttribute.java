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

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the paint / color of a plotted series.
 */
public class PaintAttribute implements StyleAttribute {
	
	private final Paint paint;
	
	private final Paint outlinePaint;
	
	private final Paint fillPaint;
	
	/**
	 * Constructs a new paint style attribute.
	 * 
	 * @param paint the paint
	 */
	public PaintAttribute(Paint paint) {
		this(paint, paint, paint);
	}
	
	/**
	 * Constructs a new paint style attribute.
	 * 
	 * @param paint the paint
	 * @param outlinePaint the outline paint
	 * @param fillPaint the fill paint
	 */
	public PaintAttribute(Paint paint, Paint outlinePaint, Paint fillPaint) {
		super();
		this.paint = paint;
		this.outlinePaint = outlinePaint;
		this.fillPaint = fillPaint;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);
			
			if (renderer == null) {
				// do nothing
			} else if (renderer instanceof XYBlockRenderer && paint instanceof Color color) {
				new PaintScaleAttribute(new ColorGradientPaintScale(0.0, 1.0, color)).apply(plot, dataset, series);
			} else if (series >= 0) {
				renderer.setSeriesPaint(series, paint);
				renderer.setSeriesOutlinePaint(series, outlinePaint);
				renderer.setSeriesFillPaint(series, fillPaint);
			} else {
				renderer.setDefaultPaint(paint);
				renderer.setDefaultOutlinePaint(outlinePaint);
				renderer.setDefaultFillPaint(fillPaint);
			}
		} else if (plot instanceof CategoryPlot categoryPlot) {
			CategoryItemRenderer renderer = categoryPlot.getRenderer(dataset);
			
			if (renderer == null) {
				// do nothing
			} else if (series >= 0) {
				renderer.setSeriesPaint(series, paint);
				renderer.setSeriesOutlinePaint(series, outlinePaint);
				renderer.setSeriesFillPaint(series, fillPaint);
			} else {
				renderer.setDefaultPaint(paint);
				renderer.setDefaultOutlinePaint(outlinePaint);
				renderer.setDefaultFillPaint(fillPaint);
			}
		}
	}
	
	/**
	 * Returns a custom paint style attribute.
	 * 
	 * @param paint the paint
	 * @return the resulting style attribute
	 */
	public static PaintAttribute of(Paint paint) {
		return new PaintAttribute(paint);
	}

}
