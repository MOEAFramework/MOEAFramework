package org.moeaframework.analysis.plot;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class SeriesPaint implements StyleAttribute {
	
	private final Paint paint;
	
	public SeriesPaint(Paint paint) {
		super();
		this.paint = paint;
	}
	
	@Override
	public void apply(Plot plot, int dataset, int series) {
		if (plot instanceof XYPlot xyPlot) {
			XYItemRenderer renderer = xyPlot.getRenderer(dataset);
			
			if (series >= 0) {
				renderer.setSeriesPaint(series, paint);
				renderer.setSeriesFillPaint(series, paint);
			} else {
				renderer.setDefaultPaint(paint);
				renderer.setDefaultFillPaint(paint);
			}
		} else if (plot instanceof CategoryPlot categoryPlot) {
			CategoryItemRenderer renderer = categoryPlot.getRenderer(dataset);
			
			if (series >= 0) {
				renderer.setSeriesPaint(series, paint);
				renderer.setSeriesFillPaint(series, paint);
			} else {
				renderer.setDefaultPaint(paint);
				renderer.setDefaultFillPaint(paint);
			}
		}
	}
	
	public static SeriesPaint of(Paint paint) {
		return new SeriesPaint(paint);
	}
	
	public static SeriesPaint black() {
		return of(Color.BLACK);
	}
	
	public static SeriesPaint red() {
		return of(Color.RED);
	}
	
	public static SeriesPaint green() {
		return of(Color.GREEN);
	}
	
	public static SeriesPaint blue() {
		return of(Color.BLUE);
	}
	
	public static SeriesPaint rgb(int r, int g, int b) {
		return of(new Color(r, g, b));
	}
	
	public static SeriesPaint hsb(float h, float s, float b) {
		return of(Color.getHSBColor(h, s, b));
	}

}
