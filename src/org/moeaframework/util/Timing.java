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
package org.moeaframework.util;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Tool to simplify manually collecting timing information. Timers with
 * different names can be interleaved or nested, but two timers with the same
 * name can not exist simultaneously - the first timer with the shared name must
 * be stopped before the second is started.
 * 
 * <pre>
 * {@code
 * for (int i=0; i<N; i++) {
 *   Timing.startTimer("foo");
 *   ...code for which we are collecting timing information...
 *   Timing.stopTimer("foo");
 * }
 * }
 * </pre>
 */
public class Timing {

	/**
	 * Collection of open timers.
	 */
	private static Map<String, Long> openTimers;

	/**
	 * Collection of timing statistics.
	 */
	private static Map<String, SummaryStatistics> data;

	static {
		openTimers = new HashMap<String, Long>();
		data = new HashMap<String, SummaryStatistics>();
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Timing() {
		super();
	}

	/**
	 * Starts a timer with the specified name.
	 * 
	 * @param name the name of the timer to start
	 */
	public static void startTimer(String name) {
		if (openTimers.containsKey(name)) {
			throw new IllegalArgumentException("timer already exists");
		}

		openTimers.put(name, System.nanoTime());
	}

	/**
	 * Stops the timer with the specified name.
	 * 
	 * @param name the name of the timer to stop
	 */
	public static void stopTimer(String name) {
		long stopTime = System.nanoTime();

		Long startTime = openTimers.remove(name);
		if (startTime == null) {
			throw new IllegalArgumentException("timer does not exist");
		}

		SummaryStatistics statistics = data.get(name);
		if (statistics == null) {
			statistics = new SummaryStatistics();
			data.put(name, statistics);
		}

		statistics.addValue(stopTime - startTime);
	}

	/**
	 * Returns the accumulated timing statistics for the timer with the
	 * specified name; or {@code null} if no such timer exists.
	 * 
	 * @param name the name of the timer
	 * @return the accumulated timing statistics for the timer with the
	 *         specified name; or {@code null} if no such timer exists
	 */
	public static SummaryStatistics getStatistics(String name) {
		return data.get(name);
	}
	
	/**
	 * Prints the collected timer data to the standard output stream.
	 */
	public static void printStatistics() {
		printStatistics(System.out);
	}

	/**
	 * Prints the collected timer data to the specified {@link PrintStream}.
	 * 
	 * @param out the stream to which data is printed
	 */
	public static void printStatistics(PrintStream out) {
		for (Map.Entry<String, SummaryStatistics> entry : data.entrySet()) {
			out.print(entry.getKey());
			out.print(": ");
			out.print(entry.getValue().getMin() / 1000000000.0);
			out.print(' ');
			out.print(entry.getValue().getMean() / 1000000000.0);
			out.print(' ');
			out.print(entry.getValue().getMax() / 1000000000.0);
			out.print(' ');
			out.print(entry.getValue().getN());
			out.println();
		}
	}
	
	/**
	 * Prints the relative magnitudes of the collected timer data to the
	 * standard output stream.
	 */
	public static void printMagnitudes() {
		printMagnitudes(System.out);
	}
	
	/**
	 * Prints the relative magnitudes of the collected timer data to the
	 * specified {@link PrintStream}.
	 * 
	 * @param out the stream to which data is printed
	 */
	public static void printMagnitudes(PrintStream out) {
		double min = Double.POSITIVE_INFINITY;
		
		for (Map.Entry<String, SummaryStatistics> entry : data.entrySet()) {
			min = Math.min(min, entry.getValue().getMean());
		}
		
		for (Map.Entry<String, SummaryStatistics> entry : data.entrySet()) {
			out.print(entry.getKey());
			out.print(": ");
			out.print(entry.getValue().getMean() / min);
			out.println();
		}
	}
	
	/**
	 * Clears all timing data.
	 */
	public static void clear() {
		data.clear();
	}

}
