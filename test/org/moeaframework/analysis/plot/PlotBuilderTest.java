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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.plot.Plot;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.CallCounter;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.plot.style.PaintAttribute;
import org.moeaframework.analysis.plot.style.PlotAttribute;
import org.moeaframework.analysis.plot.style.StepsAttribute;
import org.moeaframework.analysis.plot.style.StyleAttribute;
import org.moeaframework.analysis.plot.style.ValueAttribute;
import org.moeaframework.core.Settings;

public class PlotBuilderTest {
	
	@Test
	public void testDefaultDisplayDriver() {
		Assert.assertNotNull(PlotBuilder.getDisplayDriver());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullDisplayDriver() {
		PlotBuilder.setDisplayDriver(null);
	}

	@Test
	public void testSavePNG() throws IOException {
		File tempFile = TempFiles.createFileWithExtension(".png");
		
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
		
		Assert.assertFileWithContent(tempFile);
	}
	
	@Test
	public void testSaveSVG() throws IOException {
		Assume.assumeTrue("Skipping test as JFreeSVG library is not found", ImageUtils.supportsSVG());
		
		File tempFile = TempFiles.createFileWithExtension(".svg");
			
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
			
		Assert.assertFileWithContent(tempFile);
	}
	
	@Test
	public void testToArray() {
		XYPlotBuilder builder = new XYPlotBuilder();
		
		Assert.assertArrayEquals(new double[] { }, builder.toArray(List.of()), Settings.EPS);
		Assert.assertArrayEquals(new double[] { 5.0, 10.0, 0.5, 1.5 }, builder.toArray(List.of(5, 10L, 0.5f, 1.5)), Settings.EPS);
	}
	
	@Test
	public void testToArrayWithGetter() {
		XYPlotBuilder builder = new XYPlotBuilder();
		
		Assert.assertArrayEquals(new double[] { }, builder.toArray(List.<Color>of(), c -> c.getRed()), Settings.EPS);
		Assert.assertArrayEquals(new double[] { 0.0, 255.0 }, builder.toArray(List.of(Color.BLUE, Color.RED), c -> c.getRed()), Settings.EPS);
	}
	
	@Test
	public void testTo2DArray() {
		XYPlotBuilder builder = new XYPlotBuilder();
		
		Assert.assertEquals(0, builder.to2DArray(List.of()).length);
		
		Assert.assertEquals(1, builder.to2DArray(List.of(List.of())).length);
		Assert.assertEquals(0, builder.to2DArray(List.of(List.of()))[0].length);
		
		Assert.assertEquals(new double[][] { { 5.0, 10.0 }, { 0.5, 1.5 } }, builder.to2DArray(List.of(List.of(5, 10L), List.of(0.5f, 1.5))));
	}
	
	@Test
	public void testApplyStyle() {
		CallCounter<PlotAttribute> plotAttributeCounter = CallCounter.of(new ValueAttribute<>(5));
		CallCounter<StyleAttribute> styleAttributeCounter = CallCounter.of(new StyleAttribute() {

			@Override
			public void apply(Plot plot, int dataset, int series) {
				System.out.println(plot + " " + dataset + " " + series);
			}
			
		});
		
		XYPlotBuilder builder = new XYPlotBuilder();
		Plot plot = builder.build().getPlot();
		
		builder.applyStyle(plot, 0, plotAttributeCounter.getProxy(), styleAttributeCounter.getProxy());
		builder.applyStyle(plot, 0, 0, plotAttributeCounter.getProxy(), styleAttributeCounter.getProxy());
		builder.applyStyle(plot, 0, 1, plotAttributeCounter.getProxy(), styleAttributeCounter.getProxy());
		
		Assert.assertEquals(0, plotAttributeCounter.getTotalCallCount());
		Assert.assertEquals(3, styleAttributeCounter.getTotalCallCount());
	}
	
	@Test
	public void testGetAttribute() {
		PlotAttribute size = Style.large();
		PlotAttribute color = Style.blue();
		
		XYPlotBuilder builder = new XYPlotBuilder();
		
		PaintAttribute attribute1 = builder.getAttribute(PaintAttribute.class);
		Assert.assertNull(attribute1);
		
		PaintAttribute attribute2 = builder.getAttribute(PaintAttribute.class, size);
		Assert.assertNull(attribute2);
		
		PaintAttribute attribute3 = builder.getAttribute(PaintAttribute.class, size, color);
		Assert.assertNotNull(attribute3);
		Assert.assertSame(color, attribute3);
	}
	
	@Test
	public void testHasAttribute() {
		XYPlotBuilder builder = new XYPlotBuilder();
		
		Assert.assertFalse(builder.hasAttribute(PaintAttribute.class));
		Assert.assertFalse(builder.hasAttribute(PaintAttribute.class, Style.large()));
		Assert.assertTrue(builder.hasAttribute(PaintAttribute.class, Style.large(), Style.blue()));
	}
	
	@Test
	public void testGetValueOrDefault() {
		XYPlotBuilder builder = new XYPlotBuilder();
		
		int value1 = builder.getValueOrDefault(StepsAttribute.class, 10);
		Assert.assertEquals(value1, 10);
		
		int value2 = builder.getValueOrDefault(StepsAttribute.class, 10, Style.blue());
		Assert.assertEquals(value2, 10);
		
		int value3 = builder.getValueOrDefault(StepsAttribute.class, 10, Style.blue(), StepsAttribute.of(5));
		Assert.assertEquals(value3, 5);
		
		int value4 = builder.getValueOrDefault(StepsAttribute.class, StepsAttribute.DEFAULT_VALUE);
		Assert.assertEquals(value4, 100);
		
		int value5 = builder.getValueOrDefault(StepsAttribute.class, StepsAttribute.DEFAULT_VALUE, Style.blue());
		Assert.assertEquals(value5, 100);
		
		int value6 = builder.getValueOrDefault(StepsAttribute.class, StepsAttribute.DEFAULT_VALUE, Style.blue(), StepsAttribute.of(5));
		Assert.assertEquals(value6, 5);
	}

}
