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

import java.text.NumberFormat;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Styles the label rendered alongside each data point.
 */
public class LabelAttribute implements StyleAttribute {
	
	private final String formatString;
		
	/**
	 * Constructs a new style attribute that renders labels.
	 * 
	 * @param formatString the format string, or {@code null} to use the default
	 */
	public LabelAttribute(String formatString) {
		super();
		this.formatString = formatString;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);
			StandardXYItemLabelGenerator labelGenerator = new StandardXYItemLabelGenerator(
					formatString == null ? StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT : formatString);
			
			if (renderer == null) {
				// do nothing
			} else if (series >= 0) {
				renderer.setSeriesItemLabelGenerator(series, labelGenerator);
				renderer.setSeriesItemLabelsVisible(series, true);
			} else {
				renderer.setDefaultItemLabelGenerator(labelGenerator);
				renderer.setDefaultItemLabelsVisible(true);
			}
		} else if (plot instanceof CategoryPlot categoryPlot) {
			CategoryItemRenderer renderer = categoryPlot.getRenderer(dataset);
			StandardCategoryItemLabelGenerator labelGenerator = new StandardCategoryItemLabelGenerator(
					formatString == null ? StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING : formatString,
					NumberFormat.getInstance());
			
			if (renderer == null) {
				// do nothing
			} else if (series >= 0) {
				renderer.setSeriesItemLabelGenerator(series, labelGenerator);
				renderer.setSeriesItemLabelsVisible(series, true);
			} else {
				renderer.setDefaultItemLabelGenerator(labelGenerator);
				renderer.setDefaultItemLabelsVisible(true);
			}
		}
	}
	
	/**
	 * Returns a style attribute that renders labels alongside points.
	 * 
	 * @return the resulting style attribute
	 */
	public static LabelAttribute of() {
		return new LabelAttribute(null);
	}
	
	/**
	 * Returns a style attribute that renders labels alongside points.
	 * 
	 * @param formatString the format string
	 * @return the resulting style attribute
	 */
	public static LabelAttribute of(String formatString) {
		return new LabelAttribute(formatString);
	}

}
