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

import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.moeaframework.Assert;
import org.moeaframework.analysis.plot.Style;

public class ShapeAttributeTest extends AbstractStyleAttributeTest<ShapeAttribute> {
	
	public ShapeAttribute createInstance() {
		return ShapeAttribute.of(new Ellipse2D.Double());
	}
	
	@Override
	public void assertStyle(Plot plot) {
		if (plot instanceof XYPlot xyPlot &&
				!(xyPlot.getRenderer() instanceof XYBlockRenderer) &&
				xyPlot.getDatasetCount() > 0) {
			RectangularShape defaultShape = Assert.assertInstanceOf(RectangularShape.class, xyPlot.getRenderer().getDefaultShape());
			Assert.assertEquals(Style.DEFAULT_SIZE, defaultShape.getWidth());
			Assert.assertEquals(Style.DEFAULT_SIZE, defaultShape.getHeight());
				
			if (xyPlot.getRenderer().getSeriesShape(0) != null) {
				RectangularShape seriesShape = Assert.assertInstanceOf(RectangularShape.class, xyPlot.getRenderer().getSeriesShape(0));
				Assert.assertEquals(Style.DEFAULT_SIZE, seriesShape.getWidth());
				Assert.assertEquals(Style.DEFAULT_SIZE, seriesShape.getHeight());
			}
		}
	}

}
