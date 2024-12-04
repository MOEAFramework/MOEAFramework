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

import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.moeaframework.core.Named;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * Utility for measuring elapsed time.  Unlike a "stopwatch" which measures the total elapsed time if started and
 * stopped multiple times, this timer is intended to measure timing statistics from repeated invocations.  Every
 * start / stop cycle records the elapsed time from which statistics (min, mean, max, etc.) are computed.
 * <p>
 * While this timer internally uses {@link System#nanoTime()} for up to nanosecond accuracy, the values reported are
 * converted into fractional seconds.
 */
public class Timer implements Formattable<StatisticalSummary>, Named {
	
	private static final long NOT_STARTED = -1;
	
	private String name;
	
	private long startTime;
	
	private final SummaryStatistics statistics;
	
	/**
	 * Creates a new, unnamed timer and starts it.  This is the equivalent of creating a new timer and calling
	 * {@link #start()}.
	 * 
	 * @return the new timer
	 */
	public static Timer startNew() {
		Timer timer = new Timer();
		timer.start();
		return timer;
	}
	
	/**
	 * Creates a new timer and starts it.  This is the equivalent of creating a new timer and calling {@link #start()}.
	 * 
	 * @param name the timer name
	 * @return the new timer
	 */
	public static Timer startNew(String name) {
		Timer timer = new Timer(name);
		timer.start();
		return timer;
	}
	
	/**
	 * Constructs a new, unnamed timer.
	 */
	public Timer() {
		this(null);
	}

	/**
	 * Constructs a new timer with the given name.
	 * 
	 * @param name the timer name
	 */
	public Timer(String name) {
		super();
		this.name = name;
		
		startTime = NOT_STARTED;
		statistics = new SummaryStatistics();
	}
	
	/**
	 * Returns the name given to this timer, or {@code null} if the timer is unnamed.
	 * 
	 * @return the timer name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Starts this timer.
	 */
	public void start() {
		if (startTime != NOT_STARTED) {
			throw new IllegalStateException(name == null ? "timer already started" :
				"timer '" + name + "'already started");
		}
		
		startTime = System.nanoTime();
	}

	/**
	 * Stops the timer and records the elapsed time.
	 * 
	 * @return the elapsed time, in seconds, since this timer was last started
	 */
	public double stop() {
		long stopTime = System.nanoTime();
		
		if (startTime == NOT_STARTED) {
			throw new IllegalStateException(name == null ? "timer not started" :
				"timer '" + name + "' not started");
		}
		
		double result = toSeconds(stopTime - startTime);
		statistics.addValue(result);
		startTime = NOT_STARTED;
		
		return result;
	}
	
	/**
	 * Returns the total elapsed time across all start / stop cycles.
	 * 
	 * @return the total elapsed time, in seconds
	 */
	public double getElapsedTime() {
		return statistics.getSum();
	}

	/**
	 * Returns the accumulated timing statistics for the timer.
	 * 
	 * @return the accumulated timing statistics
	 */
	public StatisticalSummary getStatistics() {
		return statistics.getSummary();
	}
	
	/**
	 * Clears all timing data and resets the timer if it was previously started.
	 */
	public void clear() {
		statistics.clear();
		startTime = NOT_STARTED;
	}

	@Override
	public TabularData<StatisticalSummary> asTabularData() {
		TabularData<StatisticalSummary> result = new TabularData<>(List.of(getStatistics()));
		
		result.addColumn(new Column<>("Timer", x -> name == null ? "<unnamed>" : name));
		result.addColumn(new Column<>("Min", StatisticalSummary::getMin));
		result.addColumn(new Column<>("Mean", StatisticalSummary::getMean));
		result.addColumn(new Column<>("Max", StatisticalSummary::getMax));
		result.addColumn(new Column<>("Count", StatisticalSummary::getN));
		
		return result;
	}
	
	/**
	 * Converts nanoseconds, which is the unit used to collect timing data, to seconds for display.
	 * 
	 * @param value the time, in nanoseconds
	 * @return the time, in seconds
	 */
	private static final double toSeconds(long value) {
		return value / 1e9;
	}

}
