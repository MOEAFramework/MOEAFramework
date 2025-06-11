package org.moeaframework.analysis.plot;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

public class HeatMapBuilder extends PlotBuilder<HeatMapBuilder> {

	private final NumberAxis xAxis;

	private final NumberAxis yAxis;

	private final NumberAxis zAxis;
	
	private double[] x;
	
	private double[] y;
	
	private double[][] z;
	
	private PaintScale paintScale;

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
	 * Sets the x-axis label.
	 * 
	 * @param label the label for the x-axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder xLabel(String label) {
		xAxis.setLabel(label);
		return getInstance();
	}

	/**
	 * Sets the y-axis label.
	 * 
	 * @param label the label for the y-axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder yLabel(String label) {
		yAxis.setLabel(label);
		return getInstance();
	}

	/**
	 * Sets the z-axis label.
	 * 
	 * @param label the label for the z-axis
	 * @return a reference to this builder
	 */
	public HeatMapBuilder zLabel(String label) {
		zAxis.setLabel(label);
		return getInstance();
	}
	
	public HeatMapBuilder xCoords(double[] x) {
		this.x = x.clone();
		return getInstance();
	}
	
	public HeatMapBuilder xCoords(List<? extends Number> x) {
		return xCoords(toArray(x));
	}
	
	public HeatMapBuilder yCoords(double[] y) {
		this.y = y.clone();
		return getInstance();
	}
	
	public HeatMapBuilder yCoords(List<? extends Number> y) {
		return yCoords(toArray(y));
	}
	
	public HeatMapBuilder zData(double[][] z) {
		this.z = new double[z.length][];
		
		for (int i = 0; i < z.length; i++) {
			this.z[i] = z[i].clone();
		}
		
		return getInstance();
	}
	
	public HeatMapBuilder zData(List<? extends List<? extends Number>> z) {
		return zData(to2DArray(z));
	}
	
	public HeatMapBuilder paintScale(PaintScale paintScale) {
		this.paintScale = paintScale;
		return getInstance();
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
		
		xCoords(xs);
		yCoords(ys);
		zData(zs);
		
		return getInstance();
	}
	
	private abstract static class AutoScaledPaintScale implements PaintScale {
		
		private final double lowerBound;
		private final double upperBound;

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
		 * @param lowerBound the new lower bounds
		 * @param upperBound the new upper bounds
		 * @return the new paint scale
		 */
		public abstract AutoScaledPaintScale scale(double lowerBound, double upperBound);

		@Override
		public Paint getPaint(double value) {
			double boundedValue = Math.min(Math.max(value, lowerBound), upperBound);
			return getScaledPaint((boundedValue - lowerBound) / (upperBound - lowerBound));
		}
		
	}
	
	private static class IndexedPaintScale extends AutoScaledPaintScale {
		
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
		
		@Override
		public IndexedPaintScale scale(double lowerBound, double upperBound) {
			return new IndexedPaintScale(lowerBound, upperBound, colors);
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

	public static class RainbowPaintScale extends AutoScaledPaintScale {

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
	
	public static void main(String[] args) {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> (double)i).toArray();
		double[] y = IntStream.range(0, 20).mapToDouble(i -> (double)i).toArray();
		double[][] z = new double[x.length][y.length];
		
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				z[i][j] = i*j;
			}
		}
		
		new HeatMapBuilder()
				.xCoords(x)
				.yCoords(y)
				.zData(z)
				.xLabel("X")
				.yLabel("Y")
				.show();
	}

}
