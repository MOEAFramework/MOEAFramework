package org.moeaframework.analysis.plot;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sensitivity.FirstOrderSensitivity;
import org.moeaframework.analysis.sensitivity.SecondOrderSensitivity;
import org.moeaframework.analysis.sensitivity.Sensitivity;
import org.moeaframework.analysis.sensitivity.SensitivityResult;
import org.moeaframework.analysis.sensitivity.TotalOrderSensitivity;

public class SensitivityPlotBuilder extends PlotBuilder {
	
	private final SensitivityResult result;
	
	private Paint shapeFill;
	
	private Paint lineFill;
	
	private double sensitivityScaling;
	
	private double sizeScaling;
	
	private double labelOffset;

	public SensitivityPlotBuilder(SensitivityResult result) {
		super();
		this.result = result;
		this.shapeFill = Color.BLACK;
		this.lineFill = Color.GRAY;
		this.sensitivityScaling = 0.5;
		this.sizeScaling = 1.0;
		this.labelOffset = 1.3;
	}
	
	/**
	 * Sets the shape fill color.
	 * 
	 * @param shapeFill the shape fill
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder withShapeFill(Paint shapeFill) {
		this.shapeFill = shapeFill;
		return this;
	}
	
	/**
	 * Sets the line fill color.
	 * 
	 * @param lineFill the line fill
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder withLineFill(Paint lineFill) {
		this.lineFill = lineFill;
		return this;
	}
	
	/**
	 * Sets the scaling factor applied to the sensitivity values.
	 * 
	 * @param sensitivityScaling the scaling factor
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder withSensitivityScalingFactor(double sensitivityScaling) {
		this.sensitivityScaling = sensitivityScaling;
		return this;
	}
	
	/**
	 * Sets the scaling factor applied to the shape size / thickness.
	 * 
	 * @param sizeScaling the scaling factor
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder withSizeScalingFactor(double sizeScaling) {
		this.sizeScaling = sizeScaling;
		return this;
	}
	
	/**
	 * Sets the offset of labels from their corresponding shapes.
	 * 
	 * @param labelOffset the offset
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder withLabelOffset(double labelOffset) {
		this.labelOffset = labelOffset;
		return this;
	}
	
	@Override
	public JFreeChart build() {
		NumberAxis xAxis = new NumberAxis("");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("");
		yAxis.setAutoRangeIncludesZero(false);

		XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
		renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setRenderer(renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		
		ParameterSet parameterSet = result.getParameterSet();
		int n = parameterSet.size();
		double[] angles = IntStream.range(0, n).mapToDouble(x -> 2.0 * Math.PI * x / n).toArray();
		double[] xs = DoubleStream.of(angles).map(Math::cos).toArray();
		double[] ys = DoubleStream.of(angles).map(Math::sin).toArray();

		if (result instanceof SecondOrderSensitivity secondOrder) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					Sensitivity<?> value = secondOrder.getSecondOrder(parameterSet.get(i), parameterSet.get(j));
					double size = sizeScaling * Math.pow(value.getSensitivity(), sensitivityScaling) / 2.0;

					double angle = Math.atan((ys[j] - ys[i]) / (xs[j] - xs[i]));

					if (ys[j] - ys[i] < 0) {
						angle += Math.PI;
					}

					Path2D path = new Path2D.Double();
					path.moveTo(xs[i] - size * Math.sin(angle), ys[i] + size * Math.cos(angle));
					path.lineTo(xs[i] + size * Math.sin(angle), ys[i] - size * Math.cos(angle));
					path.lineTo(xs[j] + size * Math.sin(angle), ys[j] - size * Math.cos(angle));
					path.lineTo(xs[j] - size * Math.sin(angle), ys[j] + size * Math.cos(angle));
					path.closePath();

					XYShapeAnnotation annotation = new XYShapeAnnotation(path,
							plot.getRenderer().getDefaultStroke(),
							lineFill,
							lineFill);

					plot.addAnnotation(annotation);
				}
			}
		}

		if (result instanceof FirstOrderSensitivity firstOrder) {
			for (int i = 0; i < n; i++) {
				Sensitivity<?> value = firstOrder.getFirstOrder(parameterSet.get(i));
				double size = sizeScaling * Math.pow(value.getSensitivity(), sensitivityScaling) / 2.0;

				XYShapeAnnotation annotation = new XYShapeAnnotation(
						new Ellipse2D.Double(xs[i] - size / 2.0, ys[i] - size / 2.0, size, size),
						plot.getRenderer().getDefaultStroke(),
						shapeFill,
						shapeFill);

				plot.addAnnotation(annotation);
			}
		}

		if (result instanceof TotalOrderSensitivity totalOrder) {
			for (int i = 0; i < n; i++) {
				Sensitivity<?> value = totalOrder.getTotalOrder(parameterSet.get(i));
				double size = sizeScaling * Math.pow(value.getSensitivity(), sensitivityScaling) / 2.0;

				XYShapeAnnotation annotation = new XYShapeAnnotation(
						new Ellipse2D.Double(xs[i] - size / 2.0, ys[i] - size / 2.0, size, size),
						plot.getRenderer().getDefaultStroke(),
						shapeFill);

				plot.addAnnotation(annotation);
			}
		}

		for (int i = 0; i < n; i++) {
			XYTextAnnotation annotation = new XYTextAnnotation(parameterSet.get(i).getName(),
					labelOffset * xs[i], labelOffset * ys[i]);
			annotation.setTextAnchor(TextAnchor.CENTER);
			annotation.setFont(plot.getRenderer().getDefaultItemLabelFont());
			plot.addAnnotation(annotation);
		}

		plot.setBackgroundPaint(Color.WHITE);

		plot.setDomainGridlinesVisible(false);
		plot.setDomainMinorGridlinesVisible(false);

		plot.setRangeGridlinesVisible(false);
		plot.setRangeMinorGridlinesVisible(false);

		plot.getDomainAxis().setTickLabelsVisible(false);
		plot.getDomainAxis().setAutoRange(false);
		plot.getDomainAxis().setLowerBound(DoubleStream.of(xs).min().getAsDouble() - 0.5);
		plot.getDomainAxis().setUpperBound(DoubleStream.of(xs).max().getAsDouble() + 0.5);

		plot.getRangeAxis().setTickLabelsVisible(false);
		plot.getRangeAxis().setAutoRange(false);
		plot.getRangeAxis().setLowerBound(DoubleStream.of(ys).min().getAsDouble() - 0.5);
		plot.getRangeAxis().setUpperBound(DoubleStream.of(ys).max().getAsDouble() + 0.5);
		
		return build(plot);
	}
	
}
