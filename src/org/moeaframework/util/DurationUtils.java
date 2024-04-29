package org.moeaframework.util;

import java.time.Duration;

/**
 * Utility for converting a {@link Duration} into primitives.
 */
public class DurationUtils {
	
	private DurationUtils() {
		super();
	}
	
	/**
	 * Converts the given duration into milliseconds.
	 * 
	 * @param duration the duration
	 * @return the duration in milliseconds
	 */
	public static final long toMilliseconds(Duration duration) {
		return duration.getSeconds() * 1_000 + duration.getNano() / 1_000_000;
	}
	
	/**
	 * Converts the given duration into nanoseconds.
	 * 
	 * @param duration the duration
	 * @return the duration in nanoseconds
	 */
	public static final long toNanoseconds(Duration duration) {
		return duration.getSeconds() * 1_000_000_000 + duration.getNano();
	}

}
