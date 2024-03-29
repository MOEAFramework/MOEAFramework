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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;

/**
 * Utility for collecting timing information.  Use {@link #startTimer(String)} and {@link #stopTimer(String)} to
 * control when each named timer starts and stops.  Timers with different names can be interleaved or nested,
 * but two timers with the same name can not exist simultaneously.
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
		data = new LinkedHashMap<String, SummaryStatistics>();
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
			throw new IllegalArgumentException("timer '" + name + "' already started");
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
			throw new IllegalArgumentException("timer '" + name + "' not started");
		}

		SummaryStatistics statistics = data.get(name);
		if (statistics == null) {
			statistics = new SummaryStatistics();
			data.put(name, statistics);
		}

		statistics.addValue(stopTime - startTime);
	}

	/**
	 * Returns the accumulated timing statistics for the timer with the specified name; or {@code null} if no such
	 * timer exists.
	 * 
	 * @param name the name of the timer
	 * @return the accumulated timing statistics for the timer with the specified name; or {@code null} if no such
	 *         timer exists
	 */
	public static StatisticalSummary getStatistics(String name) {
		SummaryStatistics statistics = data.get(name);
		
		if (statistics == null) {
			return null;
		} else {
			return statistics.getSummary();
		}
	}
	
	/**
	 * Clears all timing data.
	 */
	public static void clear() {
		data.clear();
	}

	/**
	 * Returns the timing data in tabular format.
	 * 
	 * @return the tabular data
	 */
	public static TabularData<Pair<String, StatisticalSummary>> asTabularData() {
		List<Pair<String, StatisticalSummary>> summary = new ArrayList<>();
		
		for (Map.Entry<String, SummaryStatistics> entry : data.entrySet()) {
			summary.add(Pair.of(entry.getKey(), entry.getValue().getSummary()));
		}
		
		TabularData<Pair<String, StatisticalSummary>> result = new TabularData<>(summary);
		
		result.addColumn(new Column<Pair<String, StatisticalSummary>, String>("Timer", x -> x.getKey()));
		result.addColumn(new Column<Pair<String, StatisticalSummary>, Double>("Min", x -> toSeconds(x.getValue().getMin())));
		result.addColumn(new Column<Pair<String, StatisticalSummary>, Double>("Mean", x -> toSeconds(x.getValue().getMean())));
		result.addColumn(new Column<Pair<String, StatisticalSummary>, Double>("Max", x -> toSeconds(x.getValue().getMax())));
		result.addColumn(new Column<Pair<String, StatisticalSummary>, Long>("Count", x -> x.getValue().getN()));
		
		return result;
	}
	
	/**
	 * Displays the collected timing data to standard output.
	 */
	public static void display() {
		display(System.out);
	}
	
	/**
	 * Displays the collecting timing data.
	 * 
	 * @param out the stream for writing the timing data
	 */
	public static void display(PrintStream out) {
		asTabularData().display(out);
	}
	
	/**
	 * Converts nanoseconds, which is the unit used to collect timing data, to seconds for display.
	 * 
	 * @param value the time, in nanoseconds
	 * @return the time, in seconds
	 */
	private static final double toSeconds(double value) {
		return value / 1000000000.0;
	}

}
