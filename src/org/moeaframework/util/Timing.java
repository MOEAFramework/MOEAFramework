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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;

/**
 * Global timer for quickly measuring and displaying timing data.
 */
public class Timing {
	
	/**
	 * Collection of timers.
	 */
	private static Map<String, Timer> timers;

	static {
		timers = new LinkedHashMap<String, Timer>();
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
		getOrCreate(name).start();
	}

	/**
	 * Stops the timer with the specified name.
	 * 
	 * @param name the name of the timer to stop
	 */
	public static void stopTimer(String name) {
		getOrCreate(name).stop();
	}

	/**
	 * Returns the accumulated timing statistics for the timer with the specified name.  If no timing data has been
	 * collected, returns an empty statistics object.
	 * 
	 * @param name the name of the timer
	 * @return the accumulated timing statistics
	 */
	public static StatisticalSummary getStatistics(String name) {
		return getOrCreate(name).getStatistics();
	}
	
	/**
	 * Clears all timing data.
	 */
	public static void clear() {
		timers.clear();
	}
	
	/**
	 * Gets or creates the timer with the given name.
	 * 
	 * @param name the name of the timer
	 * @return the timer
	 */
	protected static Timer getOrCreate(String name) {
		Timer timer = timers.get(name);
		
		if (timer == null) {
			timer = new Timer(name);
			timers.put(name, timer);
		}
		
		return timer;
	}

	/**
	 * Returns the timing data in tabular format.
	 * 
	 * @return the tabular data
	 */
	public static TabularData<Timer> asTabularData() {
		TabularData<Timer> result = new TabularData<>(timers.values());
		
		result.addColumn(new Column<Timer, String>("Timer", x -> x.getName()));
		result.addColumn(new Column<Timer, Double>("Min", x -> x.getStatistics().getMin()));
		result.addColumn(new Column<Timer, Double>("Mean", x -> x.getStatistics().getMean()));
		result.addColumn(new Column<Timer, Double>("Max", x -> x.getStatistics().getMax()));
		result.addColumn(new Column<Timer, Long>("Count", x -> x.getStatistics().getN()));
		
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

}
