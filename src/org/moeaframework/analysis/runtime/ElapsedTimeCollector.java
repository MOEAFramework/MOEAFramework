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
package org.moeaframework.analysis.runtime;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.util.Timer;

/**
 * Collects the elapsed execution time of an algorithm.
 */
public class ElapsedTimeCollector implements Collector {

	/**
	 * The timer for measuring the elapsed time.  This roughly corresponds to the time the algorithm starts, assuming
	 * that the algorithm is run immediately following its setup.
	 */
	private Timer timer;

	/**
	 * Constructs a collector for recording the elapsed execution time of an algorithm.
	 */
	public ElapsedTimeCollector() {
		super();
		timer = Timer.startNew();
	}

	@Override
	public void collect(ResultEntry result) {
		result.getProperties().setDouble("Elapsed Time", timer.getElapsedTime());
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(Algorithm.class).and(
				AttachPoint.not(AttachPoint.isNestedIn(Algorithm.class)));
	}

	@Override
	public Collector attach(Object object) {
		return new ElapsedTimeCollector();
	}
	
	/**
	 * Reads the elapsed time value from the result.
	 * 
	 * @param result the result
	 * @return the elapsed time value.
	 */
	public static double getElapsedTime(ResultEntry result) {
		return result.getProperties().getDouble("Elapsed Time");
	}

}
