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
