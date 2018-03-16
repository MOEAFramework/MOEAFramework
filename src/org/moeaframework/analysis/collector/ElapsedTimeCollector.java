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
package org.moeaframework.analysis.collector;

import org.moeaframework.core.Algorithm;

/**
 * Collects the elapsed execution time of an algorithm.
 */
public class ElapsedTimeCollector implements Collector {

	/**
	 * The time, in nanoseconds, this collector was created.  This roughly
	 * corresponds to the time the algorithm starts, assuming that the algorithm
	 * is run immediately following its setup.
	 */
	private long startTime;

	/**
	 * Constructs a collector for recording the elapsed execution time of an
	 * algorithm.
	 */
	public ElapsedTimeCollector() {
		super();
		startTime = System.nanoTime();
	}

	@Override
	public void collect(Accumulator accumulator) {
		double elapsedTime = (System.nanoTime() - startTime) * 1e-9;
		accumulator.add("Elapsed Time", elapsedTime);
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

}
