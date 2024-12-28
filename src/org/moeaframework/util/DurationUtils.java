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
package org.moeaframework.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;

/**
 * Utility for converting a {@link Duration} into primitives.
 */
public class DurationUtils {
	
	private static final NumberFormat FORMAT;
	
	private static final NumberFormat FORMAT_HIGH_RESOLUTION;
	
	static {
		FORMAT = new DecimalFormat("#00");
		FORMAT.setRoundingMode(RoundingMode.FLOOR);
		
		FORMAT_HIGH_RESOLUTION = new DecimalFormat("0.0###");
	}
	
	private DurationUtils() {
		super();
	}
	
	/**
	 * Returns {@code true} if the left-hand side is greater than or equal to the right-hand side duration.
	 * 
	 * @param lhs the left-hand side
	 * @param rhs the right-hand side
	 * @return {@code true} if greater than or equal; {@code false} otherwise
	 */
	public static final boolean isGreaterThanOrEqual(Duration lhs, Duration rhs) {
		return lhs.compareTo(rhs) >= 0;
	}
	
	/**
	 * Returns the percentage as a value between {@code 0.0} and {@code 100.0} showing the percent complete.
	 * 
	 * @param elapsed the elasped time
	 * @param total the total time
	 * @return the percentage
	 */
	public static final double toPercentage(Duration elapsed, Duration total) {
		return 100.0 * DurationUtils.toMilliseconds(elapsed) / (double)DurationUtils.toMilliseconds(total);
	}
	
	/**
	 * Converts the given duration into fractional seconds.
	 * 
	 * @param duration the duration
	 * @return the duration in seconds
	 */
	public static final double toSeconds(Duration duration) {
		return duration.getSeconds() + duration.getNano() / 1e9;
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
	
	/**
	 * Produces a ISO 8601 duration format in the form {@code hh:mm:ss}.
	 * 
	 * @param duration the duration
	 * @return the formatted string
	 */
	public static final String format(Duration duration) {
		StringBuilder sb = new StringBuilder();

		sb.append(FORMAT.format(duration.toHoursPart() + 24 * duration.toDaysPart()));
		sb.append(":");
		sb.append(FORMAT.format(duration.toMinutesPart()));
		sb.append(":");
		sb.append(FORMAT.format(duration.toSecondsPart()));
		
		return sb.toString();
	}
	
	/**
	 * Produces a higher-resolution format string in the form {@code ss.mmmm "s"}.
	 * 
	 * @param duration the duration
	 * @return the formatted string
	 */
	public static final String formatHighResolution(Duration duration) {
		return FORMAT_HIGH_RESOLUTION.format(toSeconds(duration)) + " s";
	}

}
