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

import org.jfree.chart.renderer.PaintScale;
import org.junit.Test;
import org.moeaframework.Assert;

public class ColorGradientPaintScaleTest {
	
	@Test
	public void testColorPaintScale() {
		PaintScale scale = new ColorGradientPaintScale(0.0, 10.0, Color.RED);
		Assert.assertEquals(Color.BLACK, scale.getPaint(0.0));
		Assert.assertEquals(Color.RED, scale.getPaint(10.0));
	}

}
