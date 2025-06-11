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

public class HeatMapBuilder extends PlotBuilder {

	private final NumberAxis xAxis;

	private final NumberAxis yAxis;

	private final NumberAxis zAxis;
	
	private double[] xValues;
	
	private double[] yValues;
	
	private double[][] zValues;
	
	private Color baseColor;

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
		
		baseColor = Color.BLACK;
	}

	@Override
	public JFreeChart build() {
		Validate.that("xValues", xValues).isNotNull();
		Validate.that("yValues", yValues).isNotNull();
		Validate.that("zValues", zValues).isNotNull();
		Validate.that("xValues.length", xValues.length).isEqualTo("yValues.length", yValues.length);
				
		double[] xExpanded = new double[xValues.length * yValues.length];
		double[] yExpanded = new double[xValues.length * yValues.length];
		double[] zExpanded = new double[xValues.length * yValues.length];

		for (int i = 0; i < xValues.length; i++) {
			for (int j = 0; j < yValues.length; j++) {
				xExpanded[i * yValues.length + j] = xValues[i];
				yExpanded[i * yValues.length + j] = yValues[j];
				zExpanded[i * yValues.length + j] = zValues[i][j];
			}
		}

		DefaultXYZDataset dataset = new DefaultXYZDataset();
		dataset.addSeries("HeatMap", new double[][] { xExpanded, yExpanded, zExpanded });
		
		XYBlockRenderer renderer = new XYBlockRenderer();
		renderer.setBlockWidth((StatUtils.max(xValues) - StatUtils.min(xValues)) / (xValues.length - 1));
		renderer.setBlockHeight((StatUtils.max(yValues) - StatUtils.min(yValues)) / (yValues.length - 1));
		renderer.setDefaultToolTipGenerator(new StandardXYZToolTipGenerator());

		PaintScale paintScale = new ColorPaintScale(StatUtils.min(zExpanded), StatUtils.max(zExpanded), baseColor);
		renderer.setPaintScale(paintScale);
		
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
	 * Sets the x-axis label.
	 * 
	 * @param label the label for the x-axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder setXLabel(String label) {
		xAxis.setLabel(label);
		return this;
	}

	/**
	 * Sets the y-axis label.
	 * 
	 * @param label the label for the y-axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder setYLabel(String label) {
		yAxis.setLabel(label);
		return this;
	}

	/**
	 * Sets the z-axis label.
	 * 
	 * @param label the label for the z-axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder setZLabel(String label) {
		zAxis.setLabel(label);
		return this;
	}
	
	public HeatMapBuilder setX(double[] x) {
		this.xValues = x;
		return this;
	}
	
	public HeatMapBuilder setX(List<? extends Number> x) {
		double[] xValues = new double[x.size()];
		
		for (int i = 0; i < x.size(); i++) {
			xValues[i] = x.get(i).doubleValue();
		}
		
		return setX(xValues);
	}
	
	public HeatMapBuilder setY(double[] y) {
		this.yValues = y;
		return this;
	}
	
	public HeatMapBuilder setY(List<? extends Number> y) {
		double[] yValues = new double[y.size()];
		
		for (int i = 0; i < y.size(); i++) {
			yValues[i] = y.get(i).doubleValue();
		}
		
		return setY(yValues);
	}
	
	public HeatMapBuilder setZ(double[][] z) {
		this.zValues = z;
		return this;
	}
	
	public HeatMapBuilder setZ(List<? extends List<? extends Number>> z) {
		double[][] zValues = new double[z.size()][];
		
		for (int i = 0; i < z.size(); i++) {
			zValues[i] = new double[z.get(i).size()];
			
			for (int j = 0; j < z.get(i).size(); j++) {
				zValues[i][j] = z.get(i).get(j).doubleValue();
			}
		}
		
		return setZ(zValues);
	}
	
	public HeatMapBuilder withBaseColor(Color baseColor) {
		this.baseColor = baseColor;
		return this;
	}

	/**
	 * Creates a new heat map series using the keys and values from a {@link Partition}.
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
		
		setX(xs);
		setY(ys);
		setZ(zs);
		
		return this;
	}
	
	private abstract static class AbstractPaintScale implements PaintScale {
		
		private final double lowerBound;
		private final double upperBound;

		public AbstractPaintScale(double lowerBound, double upperBound) {
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

		@Override
		public Paint getPaint(double value) {
			double boundedValue = Math.min(Math.max(value, lowerBound), upperBound);
			return getScaledPaint((boundedValue - lowerBound) / (upperBound - lowerBound));
		}
		
	}
	
	private static class IndexedPaintScale extends AbstractPaintScale {
		
		private final Color[] colors;
		
		public IndexedPaintScale(double lowerBound, double upperBound, Color... colors) {
			super(lowerBound, upperBound);
			this.colors = colors;
		}
		
		@Override
		public Paint getScaledPaint(double value) {
			int index = (int)(value * colors.length);
			
			if (index < 0) {
				index = 0;
			} else if (index >= colors.length) {
				index = colors.length - 1;
			}
			
			return colors[index];
		}
		
	}

	public static class ColorPaintScale extends IndexedPaintScale {
		
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

	public static class RainbowPaintScale extends AbstractPaintScale {

		public RainbowPaintScale(double lowerBound, double upperBound) {
			super(lowerBound, upperBound);
		}

		@Override
		public Paint getScaledPaint(double value) {
			return Color.getHSBColor((float)value, 1f, 1f);
		}
	}

}
