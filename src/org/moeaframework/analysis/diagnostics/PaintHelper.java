/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.analysis.diagnostics;

import java.awt.Paint;

import org.jfree.chart.ChartColor;
import org.jfree.chart.PaintMap;

/**
 * Helper class for maintaining a mapping from series in a plot to their
 * assigned paint.  If the series has not yet been assigned a paint, one will
 * be automatically assigned when invoking {@link #get(Comparable)}.
 */
public class PaintHelper {
	
	/**
	 * The mapping from seeds to their assigned {@link Paint}.
	 */
	private PaintMap paintMap;
	
	/**
	 * The index of the next {@link Paint} to be returned by 
	 * {@link #getPaint(Comparable)}.
	 */
	private int nextPaintIndex;
	
	/**
	 * The internal list of paints.
	 */
	private static final Paint[] PAINTS = ChartColor.createDefaultPaintArray();
	
	/**
	 * Constructs a new paint helper with no assigned paints.
	 */
	public PaintHelper() {
		super();
		
		paintMap = new PaintMap();
		nextPaintIndex = 0;
	}
	
	/**
	 * Returns the paint used by the specified series key.  If the series key 
	 * has not yet been assigned a paint, one is automatically assigned.
	 * 
	 * @param key the series key
	 * @return the paint used by the specified series key
	 */
	public Paint get(Comparable<?> key) {
		if (paintMap.containsKey(key)) {
			return paintMap.getPaint(key);
		} else {
			Paint paint = PAINTS[(nextPaintIndex++) % PAINTS.length];
			paintMap.put(key, paint);
			return paint;
		}
	}
	
	/**
	 * Assigns a paint for the specified series key.  All subsequent invocations
	 * of {@link #get(Comparable)} with this series key will return the
	 * specified paint.
	 * 
	 * @param key the series key
	 * @param paint the paint to be used for the specified series key
	 */
	public void set(Comparable<?> key, Paint paint) {
		paintMap.put(key, paint);
	}
	
	/**
	 * Clears all paint assignments from this paint helper.
	 */
	public void clear() {
		paintMap.clear();
		nextPaintIndex = 0;
	}

}
