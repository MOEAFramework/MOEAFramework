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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
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
import org.moeaframework.util.validate.Validate;

/**
 * Displays sensitivity analysis results in a "spider web" plot, where:
 * <ol>
 *   <li>First-order effects are rendered as a solid circle / ellipse,
 *   <li>Total-order effects are rendered as a ring around the first-order effects, and
 *   <li>Second-order effects are rendered as lines joining the circles.
 * </ol>
 * The scale of the circles / lines reflect the relative magnitude of the sensitivities.
 */
public class SensitivityPlotBuilder extends PlotBuilder<SensitivityPlotBuilder> {
	
	private SensitivityResult data;
	
	private Paint shapeFill;
	
	private Paint lineFill;
	
	private double sensitivityScaling;
	
	private double shapeScaling;
	
	private double lineScaling;
	
	private double labelOffset;

	/**
	 * Constructs a new sensitivity plot builder with the given sensitivity analysis result.
	 */
	public SensitivityPlotBuilder() {
		super();
		this.shapeFill = Color.BLACK;
		this.lineFill = Color.GRAY;
		this.sensitivityScaling = 1.0;
		this.shapeScaling = 0.5;
		this.lineScaling = 0.25;
		this.labelOffset = 1.3;
	}
	
	@Override
	protected SensitivityPlotBuilder getInstance() {
		return this;
	}
	
	/**
	 * Sets the sensitivity analysis result.
	 * 
	 * @param data the sensitivity analysis result
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder data(SensitivityResult data) {
		this.data = data;
		return getInstance();
	}
	
	/**
	 * Sets the shape fill color.
	 * 
	 * @param shapeFill the shape fill
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder shapeFill(Paint shapeFill) {
		this.shapeFill = shapeFill;
		return getInstance();
	}
	
	/**
	 * Sets the line fill color.
	 * 
	 * @param lineFill the line fill
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder lineFill(Paint lineFill) {
		this.lineFill = lineFill;
		return getInstance();
	}
	
	/**
	 * Sets the scaling factor applied to the sensitivity values.
	 * 
	 * @param sensitivityScaling the scaling factor
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder sensitivityScalingFactor(double sensitivityScaling) {
		this.sensitivityScaling = sensitivityScaling;
		return getInstance();
	}
	
	/**
	 * Sets the scaling factor applied to the line thickness.
	 * 
	 * @param lineScaling the scaling factor
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder lineScalingFactor(double lineScaling) {
		this.lineScaling = lineScaling;
		return getInstance();
	}
	
	/**
	 * Sets the scaling factor applied to the shape size.
	 * 
	 * @param shapeScaling the scaling factor
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder shapeScalingFactor(double shapeScaling) {
		this.shapeScaling = shapeScaling;
		return getInstance();
	}
	
	/**
	 * Sets the offset of labels from their corresponding shapes.
	 * 
	 * @param labelOffset the offset
	 * @return a reference to this builder
	 */
	public SensitivityPlotBuilder labelOffset(double labelOffset) {
		this.labelOffset = labelOffset;
		return getInstance();
	}
	
	@Override
	public JFreeChart build() {
		Validate.that("data", data).isNotNull();
		
		NumberAxis xAxis = new NumberAxis("");
		xAxis.setAutoRangeIncludesZero(false);
		
		NumberAxis yAxis = new NumberAxis("");
		yAxis.setAutoRangeIncludesZero(false);

		XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
		
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		plot.setRenderer(renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		
		ParameterSet parameterSet = data.getParameterSet();
		int n = parameterSet.size();
		double[] angles = IntStream.range(0, n).mapToDouble(x -> 2.0 * Math.PI * x / n).toArray();
		double[] xs = DoubleStream.of(angles).map(Math::cos).toArray();
		double[] ys = DoubleStream.of(angles).map(Math::sin).toArray();

		if (data instanceof SecondOrderSensitivity secondOrder) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					Sensitivity<?> value = secondOrder.getSecondOrder(parameterSet.get(i), parameterSet.get(j));
					double size = lineScaling * Math.pow(value.getSensitivity(), sensitivityScaling);

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

		if (data instanceof FirstOrderSensitivity firstOrder) {
			for (int i = 0; i < n; i++) {
				Sensitivity<?> value = firstOrder.getFirstOrder(parameterSet.get(i));
				double size = shapeScaling * Math.pow(value.getSensitivity(), sensitivityScaling);

				XYShapeAnnotation annotation = new XYShapeAnnotation(
						new Ellipse2D.Double(xs[i] - size / 2.0, ys[i] - size / 2.0, size, size),
						plot.getRenderer().getDefaultStroke(),
						shapeFill,
						shapeFill);

				plot.addAnnotation(annotation);
			}
		}

		if (data instanceof TotalOrderSensitivity totalOrder) {
			for (int i = 0; i < n; i++) {
				Sensitivity<?> value = totalOrder.getTotalOrder(parameterSet.get(i));
				double size = shapeScaling * Math.pow(value.getSensitivity(), sensitivityScaling);

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
