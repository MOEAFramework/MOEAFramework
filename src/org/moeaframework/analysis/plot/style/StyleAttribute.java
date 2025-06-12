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
package org.moeaframework.analysis.plot.style;

import org.jfree.chart.plot.Plot;

/**
 * Interface for customizing the style of series plotted in a graph.  Style attributes are best-effort, meaning if a
 * style is applied to a plot not supporting that style, it is silently ignored.
 */
public interface StyleAttribute {
	
	/**
	 * Applies the style to a specific dataset and series.
	 * 
	 * @param plot the plot
	 * @param dataset the index of the dataset
	 * @param series the index of the series
	 */
	public void apply(Plot plot, int dataset, int series);
	
	/**
	 * Applies the style to all series within the specified dataset.
	 * 
	 * @param plot the plot
	 * @param dataset the index of the dataset
	 */
	public default void apply(Plot plot, int dataset) {
		apply(plot, dataset, -1);
	}

}
