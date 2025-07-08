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
import org.moeaframework.Assert;

public class SizeAttributeTest extends AbstractStyleAttributeTest<SizeAttribute> {
	
	public SizeAttribute createInstance() {
		return SizeAttribute.of(12);
	}
	
	@Override
	public void assertStyle(Plot plot) {
		if (plot instanceof XYPlot xyPlot && xyPlot.getDatasetCount() > 0) {
			RectangularShape defaultShape = Assert.assertInstanceOf(RectangularShape.class, xyPlot.getRenderer().getDefaultShape());
			Assert.assertEquals(12.0, defaultShape.getWidth());
				
			BasicStroke defaultStroke = Assert.assertInstanceOf(BasicStroke.class, xyPlot.getRenderer().getDefaultStroke());
			Assert.assertEquals(6.0, defaultStroke.getLineWidth());
			
			if (xyPlot.getRenderer().getSeriesShape(0) != null) {
				RectangularShape seriesShape = Assert.assertInstanceOf(RectangularShape.class, xyPlot.getRenderer().getSeriesShape(0));
				Assert.assertEquals(12.0, seriesShape.getWidth());
			}
			
			if (xyPlot.getRenderer().getSeriesStroke(0) != null) {
				BasicStroke seriesStroke = Assert.assertInstanceOf(BasicStroke.class, xyPlot.getRenderer().getSeriesStroke(0));
				Assert.assertEquals(6.0, seriesStroke.getLineWidth());
			}
		}
	}

}
