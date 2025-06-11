package org.moeaframework.analysis.plot;

import java.awt.BasicStroke;
import java.awt.geom.RectangularShape;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class SeriesSize implements StyleAttribute {
	
	private final float size;
	
	public SeriesSize(float size) {
		super();
		this.size = size;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);			
			
			if (series >= 0) {
				if (renderer.getSeriesShape(series) instanceof RectangularShape shape) {
					shape.setFrame(-size / 2.0f, -size / 2.0f, size, size);
					renderer.setSeriesShape(series, shape);
				}
				
				if (renderer.getSeriesStroke(series) instanceof BasicStroke stroke) {
					renderer.setSeriesStroke(series, new BasicStroke(
							size,
							stroke.getEndCap(),
							stroke.getLineJoin(),
							stroke.getMiterLimit(),
							stroke.getDashArray(),
							stroke.getDashPhase()));
				} else {
					renderer.setSeriesStroke(series, new BasicStroke(size, 1, 1));
				}
			} else {
				if (renderer.getDefaultShape() instanceof RectangularShape shape) {
					shape.setFrame(-size / 2.0f, -size / 2.0f, size, size);
					renderer.setDefaultShape(shape);
				}
				
				if (renderer.getDefaultStroke() instanceof BasicStroke stroke) {
					renderer.setDefaultStroke(new BasicStroke(
							size,
							stroke.getEndCap(),
							stroke.getLineJoin(),
							stroke.getMiterLimit(),
							stroke.getDashArray(),
							stroke.getDashPhase()));
				} else {
					renderer.setDefaultStroke(new BasicStroke(size, 1, 1));
				}
			}
		}
	}
	
	public static SeriesSize of(float size) {
		return new SeriesSize(size);
	}
	
	public static SeriesSize small() {
		return of(3f);
	}
	
	public static SeriesSize medium() {
		return of(6f);
	}
	
	public static SeriesSize large() {
		return of(9f);
	}

}
