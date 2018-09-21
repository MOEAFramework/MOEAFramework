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

import org.moeaframework.algorithm.AdaptiveTimeContinuation;
import org.moeaframework.algorithm.RestartEvent;
import org.moeaframework.algorithm.RestartListener;

/**
 * Collects the number of restart events resulting from
 * {@link AdaptiveTimeContinuation}.
 */
public class AdaptiveTimeContinuationCollector implements Collector,
RestartListener {

	/**
	 * The number of restart events.
	 */
	private int numberOfRestarts;
	
	/**
	 * Constructs an unattached collector for recording the number of restart
	 * events resulting from {@code AdaptiveTimeContinuation}.
	 */
	public AdaptiveTimeContinuationCollector() {
		super();
	}
	
	/**
	 * Constructs a collector for recording the number of restart events 
	 * resulting from the specified {@code AdaptiveTimeContinuation} instance.
	 * 
	 * @param algorithm the algorithm this collector records data from
	 */
	public AdaptiveTimeContinuationCollector(
			AdaptiveTimeContinuation algorithm) {
		super();

		algorithm.addRestartListener(this);
	}

	@Override
	public void collect(Accumulator accumulator) {
		accumulator.add("Number of Restarts", numberOfRestarts);
	}

	@Override
	public void restarted(RestartEvent event) {
		numberOfRestarts++;
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(AdaptiveTimeContinuation.class);
	}

	@Override
	public Collector attach(Object object) {
		return new AdaptiveTimeContinuationCollector(
				(AdaptiveTimeContinuation)object);
	}

}
