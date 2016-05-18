/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

/**
 * Terminates a run when the maximum elapsed time is exceeded.
 */
public class MaxElapsedTime implements TerminationCondition {
	
	/**
	 * The maximum elapsed time in milliseconds.
	 */
	private final long maxTime;
	
	/**
	 * The starting time in milliseconds.
	 */
	private long startTime;
	
	/**
	 * Constructs a new termination condition based on the maximum elapsed time.
	 * 
	 * @param maxTime the maximum elapsed time in milliseconds
	 */
	public MaxElapsedTime(long maxTime) {
		super();
		this.maxTime = maxTime;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		startTime = System.currentTimeMillis();
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		return (System.currentTimeMillis() - startTime) >= maxTime;
	}

}
