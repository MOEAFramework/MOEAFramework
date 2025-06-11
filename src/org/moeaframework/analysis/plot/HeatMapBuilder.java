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
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.DefaultXYZDataset;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.util.validate.Validate;

/**
 * Constructs a 2D heat map.  The map is a raster produced from a 2D grid, defined by the X and Y coordinates, and a
 * paint scale that maps values to colors.
 */
public class HeatMapBuilder extends PlotBuilder<HeatMapBuilder> {

	private final NumberAxis xAxis;

	private final NumberAxis yAxis;

	private final NumberAxis zAxis;
	
	private double[] x;
	
	private double[] y;
	
	private double[][] z;
	
	private PaintScale paintScale;

	/**
	 * Constructs a new, empty heat map builder.
	 */
	public HeatMapBuilder() {
		super();

		xAxis = new NumberAxis("");
		xAxis.setAutoRangeIncludesZero(false);
		xAxis.setLowerMargin(0.0);
		xAxis.setUpperMargin(0.0);

		yAxis = new NumberAxis("");
		yAxis.setAutoRangeIncludesZero(false);
		yAxis.setLowerMargin(0.0);
		yAxis.setUpperMargin(0.0);

		zAxis = new NumberAxis("");
		zAxis.setAutoRangeIncludesZero(false);
		zAxis.setLowerMargin(0.0);
		zAxis.setUpperMargin(0.0);
		zAxis.setVisible(false);
		
		paintScale = new ColorPaintScale(0.0, 1.0, Color.BLACK);
	}
	
	@Override
	protected HeatMapBuilder getInstance() {
		return this;
	}

	@Override
	public JFreeChart build() {
		Validate.that("x", x).isNotNull();
		Validate.that("y", y).isNotNull();
		Validate.that("z", z).isNotNull();
				
		double[] xExpanded = new double[x.length * y.length];
		double[] yExpanded = new double[x.length * y.length];
		double[] zExpanded = new double[x.length * y.length];

		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				xExpanded[i * y.length + j] = x[i];
				yExpanded[i * y.length + j] = y[j];
				zExpanded[i * y.length + j] = z[i][j];
			}
		}

		DefaultXYZDataset dataset = new DefaultXYZDataset();
		dataset.addSeries("HeatMap", new double[][] { xExpanded, yExpanded, zExpanded });
		
		XYBlockRenderer renderer = new XYBlockRenderer();
		renderer.setBlockWidth((StatUtils.max(x) - StatUtils.min(x)) / (x.length - 1));
		renderer.setBlockHeight((StatUtils.max(y) - StatUtils.min(y)) / (y.length - 1));
		renderer.setDefaultToolTipGenerator(new StandardXYZToolTipGenerator());
		
		if (paintScale instanceof AutoScaledPaintScale autoScaledPaintScale) {
			renderer.setPaintScale(autoScaledPaintScale.scale(StatUtils.min(zExpanded), StatUtils.max(zExpanded)));
		} else {
			renderer.setPaintScale(paintScale);
		}
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setRangeAxis(1, zAxis);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setDataset(dataset);
		plot.setRenderer(renderer);
		
		JFreeChart chart = build(plot);

		if (chart.getLegend() != null) {
			PaintScaleLegend paintScaleLegend = new PaintScaleLegend(renderer.getPaintScale(), new NumberAxis(zAxis.getLabel()));
			paintScaleLegend.setPosition(RectangleEdge.RIGHT);
			paintScaleLegend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
			paintScaleLegend.setMargin(10.0, 10.0, 10.0, 10.0);

			chart.addSubtitle(paintScaleLegend);
			chart.removeLegend();
		}

		return chart;
	}

	/**
	 * Sets the X axis label.
	 * 
	 * @param label the label for the X axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder xLabel(String label) {
		xAxis.setLabel(label);
		return getInstance();
	}

	/**
	 * Sets the Y axis label.
	 * 
	 * @param label the label for the Y axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder yLabel(String label) {
		yAxis.setLabel(label);
		return getInstance();
	}

	/**
	 * Sets the Z axis label.
	 * 
	 * @param label the label for the Z axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder zLabel(String label) {
		zAxis.setLabel(label);
		return getInstance();
	}
	
	/**
	 * Sets the X coordinates defining the grid.
	 * 
	 * @param x the X coordinates
	 * @return a reference to this builder
	 */
	public HeatMapBuilder xCoords(double[] x) {
		this.x = x.clone();
		return getInstance();
	}
	
	/**
	 * Sets the X coordinates defining the grid.
	 * 
	 * @param x the X coordinates
	 * @return a reference to this builder
	 */
	public HeatMapBuilder xCoords(List<? extends Number> x) {
		return xCoords(toArray(x));
	}
	
	/**
	 * Sets the Y coordinates defining the grid.
	 * 
	 * @param y the Y coordinates
	 * @return a reference to this builder
	 */
	public HeatMapBuilder yCoords(double[] y) {
		this.y = y.clone();
		return getInstance();
	}
	
	/**
	 * Sets the Y coordinates defining the grid.
	 * 
	 * @param y the Y coordinates
	 * @return a reference to this builder
	 */
	public HeatMapBuilder yCoords(List<? extends Number> y) {
		return yCoords(toArray(y));
	}
	
	/**
	 * Sets the Z data that define the color of each grid coordinate.
	 * 
	 * @param z the Z values
	 * @return a reference to this builder
	 */
	public HeatMapBuilder zData(double[][] z) {
		this.z = new double[z.length][];
		
		for (int i = 0; i < z.length; i++) {
			this.z[i] = z[i].clone();
		}
		
		return getInstance();
	}
	
	/**
	 * Sets the Z data that define the color of each grid coordinate.
	 * 
	 * @param z the Z values
	 * @return a reference to this builder
	 */
	public HeatMapBuilder zData(List<? extends List<? extends Number>> z) {
		return zData(to2DArray(z));
	}
	
	/**
	 * Sets the paint scale that maps Z values into colors.
	 * 
	 * @param paintScale the paint scale
	 * @return a reference to this builder
	 */
	public HeatMapBuilder paintScale(PaintScale paintScale) {
		Validate.that("paintScale", paintScale).isNotNull();
		this.paintScale = paintScale;
		return getInstance();
	}

	/**
	 * Sets the X, Y, and Z values based on a {@link Partition}.
	 * 
	 * @param partition the data stream partition
	 * @return a reference to this instance
	 */
	public HeatMapBuilder set(Partition<? extends Pair<? extends Number, ? extends Number>, ? extends Number> partition) {
		List<? extends Number> xs = partition.keys().stream().map(Pair::getLeft).distinct().sorted().toList();
		List<? extends Number> ys = partition.keys().stream().map(Pair::getRight).distinct().sorted().toList();
		List<List<Number>> zs = new ArrayList<>();

		for (Number x : xs) {
			List<Number> row = new ArrayList<>();

			for (Number y : ys) {
				row.add(partition.filter(v -> v.getLeft().equals(x) && v.getRight().equals(y)).single().getValue());
			}

			zs.add(row);
		}
		
		xCoords(xs);
		yCoords(ys);
		zData(zs);
		
		return getInstance();
	}
	
	/**
	 * Abstract class for defining paint scales that are automatically scaled based on the range of Z values.
	 */
	private abstract static class AutoScaledPaintScale implements PaintScale {
		
		private final double lowerBound;
		private final double upperBound;

		/**
		 * Constructs an auto-scaled paint scale with initial bounds.
		 * 
		 * @param lowerBound the lower bound
		 * @param upperBound the upper bound
		 */
		public AutoScaledPaintScale(double lowerBound, double upperBound) {
			super();
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		@Override
		public double getLowerBound() {
			return lowerBound;
		}

		@Override
		public double getUpperBound() {
			return upperBound;
		}
		
		/**
		 * Returns the paint for a value scaled by the lower and upper bounds.
		 * 
		 * @param value the scaled value, between {@code 0.0} and {@code 1.0}
		 * @return the paint
		 */
		public abstract Paint getScaledPaint(double value);
		
		/**
		 * Returns a new paint scale with new lower and upper bounds.
		 * 
		 * @param lowerBound the new lower bound
		 * @param upperBound the new upper bound
		 * @return the new paint scale
		 */
		public abstract AutoScaledPaintScale scale(double lowerBound, double upperBound);

		@Override
		public Paint getPaint(double value) {
			double boundedValue = Math.min(Math.max(value, lowerBound), upperBound);
			return getScaledPaint((boundedValue - lowerBound) / (upperBound - lowerBound));
		}
		
	}
	
	/**
	 * Paint scale that maps Z values to a list of colors.
	 */
	private static class IndexedPaintScale extends AutoScaledPaintScale {
		
		private final Paint[] paints;
		
		/**
		 * Constructs an indexed paint scale.
		 * 
		 * @param lowerBound the lower bound
		 * @param upperBound the upper bound
		 * @param paints the array of paints
		 */
		public IndexedPaintScale(double lowerBound, double upperBound, Paint... paints) {
			super(lowerBound, upperBound);
			this.paints = paints;
		}
		
		@Override
		public Paint getScaledPaint(double value) {
			int index = (int)(value * paints.length);
			
			if (index < 0) {
				index = 0;
			} else if (index >= paints.length) {
				index = paints.length - 1;
			}
			
			return paints[index];
		}
		
		@Override
		public IndexedPaintScale scale(double lowerBound, double upperBound) {
			return new IndexedPaintScale(lowerBound, upperBound, paints);
		}
		
	}

	/**
	 * Paint scale producing a gradient of some base color by adjusting its brightness.
	 */
	public static class ColorPaintScale extends IndexedPaintScale {
		
		/**
		 * Constructs a color gradient paint scale.
		 * 
		 * @param lowerBound the lower bound
		 * @param upperBound the upper bound
		 * @param baseColor the base color
		 */
		public ColorPaintScale(double lowerBound, double upperBound, Color baseColor) {
			super(lowerBound, upperBound, generateColors(baseColor, 256));
        }
		
		private static final Color[] generateColors(Color baseColor, int numberOfColors) {
			Color[] colors = new Color[numberOfColors];
			float[] hsb = new float[3];
			
			Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), hsb);
			
			for (int i = 0; i < numberOfColors; i++) {
				colors[i] = Color.getHSBColor(hsb[0], hsb[1], (float)i / (numberOfColors - 1));
			}
			
			return colors;
		}
		
	}

	/**
	 * Paint scale producing a rainbow of colors by adjusting the hue component.
	 */
	public static class RainbowPaintScale extends AutoScaledPaintScale {

		/**
		 * Constructs a rainbow paint scale.
		 * 
		 * @param lowerBound the lower bound
		 * @param upperBound the upper bound
		 */
		public RainbowPaintScale(double lowerBound, double upperBound) {
			super(lowerBound, upperBound);
		}

		@Override
		public Paint getScaledPaint(double value) {
			return Color.getHSBColor((float)value, 1f, 1f);
		}
		
		@Override
		public RainbowPaintScale scale(double lowerBound, double upperBound) {
			return new RainbowPaintScale(lowerBound, upperBound);
		}
		
	}

}
