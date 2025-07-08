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

import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYZDataset;

/**
 * Styles the tooltip displayed when hovering the mouse over a data point.
 */
public class TooltipAttribute implements StyleAttribute {
		
	/**
	 * Constructs a new style attribute that displays tooltips.
	 */
	public TooltipAttribute() {
		super();
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);	
			
			if (renderer == null) {
				// do nothing
			} else if (xyPlot.getDataset(dataset) instanceof XYZDataset) {
				if (series >= 0) {
					renderer.setSeriesToolTipGenerator(series, new StandardXYZToolTipGenerator());
				} else {
					renderer.setDefaultToolTipGenerator(new StandardXYZToolTipGenerator());
				}
			} else {
				if (series >= 0) {
					renderer.setSeriesToolTipGenerator(series, new StandardXYToolTipGenerator());
				} else {
					renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
				}
			}
		} else if (plot instanceof CategoryPlot categoryPlot) {
			CategoryItemRenderer renderer = categoryPlot.getRenderer(dataset);
			
			if (renderer == null) {
				// do nothing
			} else if (categoryPlot.getDataset(dataset) instanceof BoxAndWhiskerCategoryDataset) {
				if (series >= 0) {
					renderer.setSeriesToolTipGenerator(series, new BoxAndWhiskerToolTipGenerator());
				} else {
					renderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
				}
			} else {
				if (series >= 0) {
					renderer.setSeriesToolTipGenerator(series, new StandardCategoryToolTipGenerator());
				} else {
					renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
				}
			}
		}
	}
	
	/**
	 * Returns a style attribute that displays tooltips when hovering the mouse over data points.
	 * 
	 * @return the resulting style attribute
	 */
	public static TooltipAttribute of() {
		return new TooltipAttribute();
	}

}
