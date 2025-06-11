package org.moeaframework.analysis.plot;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class SeriesShape implements StyleAttribute {
	
	private final Shape shape;
	
	public SeriesShape(Shape shape) {
		super();
		this.shape = shape;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);			
			
			if (series >= 0) {
				renderer.setSeriesShape(series, shape);
			} else {
				renderer.setDefaultShape(shape);
			}
		}
	}
	
	public static SeriesShape of(Shape shape) {
		return new SeriesShape(shape);
	}
	
	public static SeriesShape circle() {
		return of(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
	}
	
	public static SeriesShape square() {
		return of(new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0));
	}

}
