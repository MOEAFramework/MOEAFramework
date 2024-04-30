/* Copyright 2009-2024 David Hadka
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
