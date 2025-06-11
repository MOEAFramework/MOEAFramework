package org.moeaframework.analysis.plot;

import org.jfree.chart.plot.Plot;

/**
 * 
 */
public interface StyleAttribute {
		
	public void apply(Plot plot, int dataset, int series);
	
	public default void apply(Plot plot, int dataset) {
		apply(plot, dataset, -1);
	}

}
